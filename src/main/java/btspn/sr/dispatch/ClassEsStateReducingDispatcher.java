package btspn.sr.dispatch;

import btspn.sr.EsStateReducer;
import btspn.sr.cmd.EventStore;
import javaslang.Tuple2;
import javaslang.collection.HashMap;
import javaslang.collection.Map;
import javaslang.control.Option;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class ClassEsStateReducingDispatcher<S, Ctx> implements EsStateReducer<S, Ctx> {
    private final Map<Class, EsStateReducer> map;
    private final EsStateReducer defaultHandler;

    public ClassEsStateReducingDispatcher(Map<Class, EsStateReducer> map, EsStateReducer defaultHandler) {
        this.map = map;
        this.defaultHandler = defaultHandler;
    }

    public ClassEsStateReducingDispatcher() {
        this(HashMap.empty(), null);
    }


    @Override
    public S apply(EventStore eventStore, Supplier<Ctx> ctx, Object id, Object command) {
        EsStateReducer<S, Ctx> handler = this.map.get(command.getClass()).orElse(defaultHandler);
        if (handler == null) {
            throw new IllegalArgumentException("Handler not defined for " + command.getClass());
        }
        return handler.apply(eventStore, ctx, id, command);
    }

    public ClassEsStateReducingDispatcher<S, Ctx> on(Class<?> clazz, EsStateReducer<S, Ctx> fn) {
        return new ClassEsStateReducingDispatcher<>(map.put(clazz, fn), defaultHandler);
    }

    public ClassEsStateReducingDispatcher<S, Ctx> orElse(EsStateReducer<S, Ctx> fn) {
        return new ClassEsStateReducingDispatcher<>(map, fn);
    }

}
