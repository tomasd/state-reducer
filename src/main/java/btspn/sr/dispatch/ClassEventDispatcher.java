package btspn.sr.dispatch;

import btspn.sr.EventFunction;
import javaslang.Tuple2;
import javaslang.collection.HashMap;
import javaslang.collection.Map;
import javaslang.control.Option;

import java.util.function.Predicate;

public class ClassEventDispatcher<S, Ctx> implements EventFunction<S, Ctx> {
    private final Map<Class, EventFunction> map;
    private final EventFunction defaultHandler;

    public ClassEventDispatcher(Map<Class, EventFunction> map, EventFunction defaultHandler) {
        this.map = map;
        this.defaultHandler = defaultHandler;
    }

    public ClassEventDispatcher() {
        this(HashMap.empty(), null);
    }

    @Override
    public S apply(Object event, S s0, S sN, Ctx ctx) {
        EventFunction<S, Ctx> handler = this.map.get(event.getClass()).orElse(defaultHandler);
        if (handler == null) {
            throw new IllegalArgumentException("Handler not defined for " + event.getClass());
        }
        return handler.apply(event, s0, sN, ctx);
    }

    public <E> ClassEventDispatcher<S, Ctx> on(Class<E> clazz, EventFunction<S, Ctx> fn) {
        return new ClassEventDispatcher<>(map.put(clazz, fn), defaultHandler);
    }

    public <E> ClassEventDispatcher<S, Ctx> orElse(EventFunction<S, Ctx> fn) {
        return new ClassEventDispatcher<>(map, fn);
    }
}
