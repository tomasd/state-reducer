package btspn.sr.dispatch;

import btspn.sr.event.Holder;
import btspn.sr.StateReducer;
import javaslang.collection.HashMap;
import javaslang.collection.Map;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class ClassStateReducingDispatcher<S, Ctx> implements StateReducer<S, Ctx> {
    private final Map<Class, StateReducer> map;
    private final StateReducer defaultHandler;

    public ClassStateReducingDispatcher(Map<Class, StateReducer> map, StateReducer<S, Ctx> defaultHandler) {
        this.map = map;
        this.defaultHandler = defaultHandler;
    }

    public ClassStateReducingDispatcher() {
        this(HashMap.empty(), null);
    }

    public ClassStateReducingDispatcher(StateReducer<S, Ctx> defaultHandler) {
        this(HashMap.empty(), defaultHandler);
    }

    @Override
    public S apply(Holder<S> holder, Supplier<Ctx> ctx, Object command) {
        StateReducer<S, Ctx> handler = this.map.get(command.getClass()).orElse(defaultHandler);
        if (handler == null) {
            throw new IllegalArgumentException("Handler not defined for " + command.getClass());
        }
        return handler.apply(holder, ctx, command);
    }

    public ClassStateReducingDispatcher<S, Ctx> on(Class<?> clazz, StateReducer<S, Ctx> fn) {
        return new ClassStateReducingDispatcher<>(map.put(clazz, fn), defaultHandler);
    }

    public ClassStateReducingDispatcher<S, Ctx> orElse(StateReducer<S, Ctx> fn) {
        return new ClassStateReducingDispatcher<>(map, fn);
    }
}
