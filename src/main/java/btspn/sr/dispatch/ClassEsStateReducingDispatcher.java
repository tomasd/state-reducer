package btspn.sr.dispatch;

import btspn.sr.EsStateReducer;
import btspn.sr.EventFunction;
import btspn.sr.StateReducer;
import btspn.sr.cmd.EventStore;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.HashMap;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.control.Option;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class ClassEsStateReducingDispatcher<S, Ctx> implements EsStateReducer<S, Ctx> {
    private final List<Tuple2<Predicate, EsStateReducer>> predicates;
    private final Map<Class, EsStateReducer> map;
    private final EsStateReducer defaultHandler;

    public ClassEsStateReducingDispatcher(List<Tuple2<Predicate, EsStateReducer>> predicates, Map<Class, EsStateReducer> map, EsStateReducer defaultHandler) {
        this.predicates = predicates;
        this.map = map;
        this.defaultHandler = defaultHandler;
    }

    public ClassEsStateReducingDispatcher(Map<Class, EsStateReducer> map, EsStateReducer defaultHandler) {
        this(List.empty(), map, defaultHandler);
    }

    public ClassEsStateReducingDispatcher(Map<Class, EsStateReducer> map) {
        this(List.empty(), map, null);
    }

    public ClassEsStateReducingDispatcher(EsStateReducer defaultHandler) {
        this(List.empty(), HashMap.empty(), defaultHandler);
    }

    public ClassEsStateReducingDispatcher() {
        this(List.empty(), HashMap.empty(), null);
    }


    @Override
    public S apply(EventStore eventStore, Supplier<Ctx> ctx, Object id, Object command) {
        Option<EsStateReducer> byPredicate = predicates.findFirst(t -> t._1.test(command)).map(Tuple2::_2);
        EsStateReducer<S, Ctx> handler = byPredicate.orElse(this.map.get(command.getClass()).orElse(defaultHandler));
        if (handler == null) {
            throw new IllegalArgumentException("Handler not defined for " + command.getClass());
        }
        return handler.apply(eventStore, ctx, id, command);
    }

    public ClassEsStateReducingDispatcher<S, Ctx> on(Class<?> clazz, EsStateReducer<S, Ctx> fn) {
        return new ClassEsStateReducingDispatcher<>(predicates, map.put(clazz, fn), defaultHandler);
    }

    public ClassEsStateReducingDispatcher<S, Ctx> on(Predicate<?> predicate, EsStateReducer<S, Ctx> fn) {
        return new ClassEsStateReducingDispatcher<>(predicates.append(Tuple.of(predicate, fn)), map, defaultHandler);
    }

    public ClassEsStateReducingDispatcher<S, Ctx> orElse(EsStateReducer<S, Ctx> fn) {
        return new ClassEsStateReducingDispatcher<>(predicates, map, fn);
    }

}
