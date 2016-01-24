package btspn.sr.dispatch;

import btspn.sr.CommandFunction;
import btspn.sr.EventFunction;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.HashMap;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.control.Option;

import java.util.function.Predicate;

public class ClassEventDispatcher<S, Ctx> implements EventFunction<Object, S, Ctx> {
    private final List<Tuple2<Predicate, EventFunction>> predicates;
    private final Map<Class, EventFunction> map;
    private final EventFunction defaultHandler;

    public ClassEventDispatcher(List<Tuple2<Predicate, EventFunction>> predicates, Map<Class, EventFunction> map, EventFunction defaultHandler) {
        this.predicates = predicates;
        this.map = map;
        this.defaultHandler = defaultHandler;
    }

    public ClassEventDispatcher() {
        this(List.empty(), HashMap.empty(), null);
    }

    public ClassEventDispatcher(EventFunction<Object, S, Ctx> defaultHandler) {
        this(List.empty(), HashMap.empty(), defaultHandler);
    }

    @Override
    public S apply(Object event, S s0, S sN, Ctx ctx) {
        Option<EventFunction> byPredicate = predicates.findFirst(t -> t._1.test(event)).map(Tuple2::_2);
        EventFunction<Object, S, Ctx> handler = byPredicate.orElse(this.map.get(event.getClass()).orElse(defaultHandler));
        if (handler == null) {
            throw new IllegalArgumentException("Handler not defined for " + event.getClass());
        }
        return handler.apply(event, s0, sN, ctx);
    }

    public <E> ClassEventDispatcher<S, Ctx> on(Class<E> clazz, EventFunction<E, S, Ctx> fn) {
        return new ClassEventDispatcher<>(predicates, map.put(clazz, fn), defaultHandler);
    }

    public <E> ClassEventDispatcher<S, Ctx> on(Predicate<E> predicate, EventFunction<E, S, Ctx> fn) {
        return new ClassEventDispatcher<>(predicates.append(Tuple.of(predicate, fn)), map, defaultHandler);
    }

    public <E> ClassEventDispatcher<S, Ctx> orElse(EventFunction<E, S, Ctx> fn) {
        return new ClassEventDispatcher<>(predicates, map, fn);
    }
}
