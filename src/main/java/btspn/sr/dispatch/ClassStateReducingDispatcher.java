package btspn.sr.dispatch;

import btspn.sr.EsStateReducer;
import btspn.sr.event.Holder;
import btspn.sr.StateReducer;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.HashMap;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.control.Option;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class ClassStateReducingDispatcher<S, Ctx> implements StateReducer<S, Ctx> {
    private final List<Tuple2<Predicate, StateReducer>> predicates;
    private final Map<Class, StateReducer> map;
    private final StateReducer defaultHandler;

    public ClassStateReducingDispatcher(List<Tuple2<Predicate, StateReducer>> predicates, Map<Class, StateReducer> map, StateReducer<S, Ctx> defaultHandler) {
        this.predicates = predicates;
        this.map = map;
        this.defaultHandler = defaultHandler;
    }

    public ClassStateReducingDispatcher() {
        this(List.empty(), HashMap.empty(), null);
    }

    public ClassStateReducingDispatcher(StateReducer<S, Ctx> defaultHandler) {
        this(List.empty(), HashMap.empty(), defaultHandler);
    }

    @Override
    public S apply(Holder<S> holder, Supplier<Ctx> ctx, Object command) {
        Option<StateReducer> byPredicate = predicates.findFirst(t -> t._1.test(command)).map(Tuple2::_2);
        StateReducer<S, Ctx> handler = byPredicate.orElse(this.map.get(command.getClass()).orElse(defaultHandler));
        if (handler == null) {
            throw new IllegalArgumentException("Handler not defined for " + command.getClass());
        }
        return handler.apply(holder, ctx, command);
    }

    public ClassStateReducingDispatcher<S, Ctx> on(Class<?> clazz, StateReducer<S, Ctx> fn) {
        return new ClassStateReducingDispatcher<>(predicates, map.put(clazz, fn), defaultHandler);
    }

    public ClassStateReducingDispatcher<S, Ctx> on(Predicate<?> predicate, StateReducer<S, Ctx> fn) {
        return new ClassStateReducingDispatcher<>(predicates.append(Tuple.of(predicate, fn)), map, defaultHandler);
    }

    public ClassStateReducingDispatcher<S, Ctx> orElse(StateReducer<S, Ctx> fn) {
        return new ClassStateReducingDispatcher<>(predicates, map, fn);
    }
}
