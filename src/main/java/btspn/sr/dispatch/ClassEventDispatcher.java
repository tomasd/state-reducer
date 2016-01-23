package btspn.sr.dispatch;

import btspn.sr.EventFunction;
import javaslang.collection.HashMap;
import javaslang.collection.Map;

public class ClassEventDispatcher<S, Ctx> implements EventFunction<Object, S, Ctx> {
    private final Map<Class, EventFunction> map;
    private final EventFunction defaultHandler;

    public ClassEventDispatcher(Map<Class, EventFunction> map, EventFunction defaultHandler) {
        this.map = map;
        this.defaultHandler = defaultHandler;
    }

    public ClassEventDispatcher() {
        this(HashMap.empty(), null);
    }

    public ClassEventDispatcher(EventFunction<Object, S, Ctx> defaultHandler) {
        this(HashMap.empty(), defaultHandler);
    }

    @Override
    public S apply(Object event, S s0, S sN, Ctx ctx) {
        EventFunction<Object, S, Ctx> handler = map.get(event.getClass()).orElse(defaultHandler);
        if (handler == null) {
            throw new IllegalArgumentException("Handler not defined for " + event.getClass());
        }
        return handler.apply(event, s0, sN, ctx);
    }

    public <E> ClassEventDispatcher<S, Ctx> on(Class<E> clazz, EventFunction<E, S, Ctx> fn) {
        return new ClassEventDispatcher<>(map.put(clazz, fn), defaultHandler);
    }

    public <E> ClassEventDispatcher<S, Ctx> orElse(EventFunction<E, S, Ctx> fn) {
        return new ClassEventDispatcher<>(map, fn);
    }
}
