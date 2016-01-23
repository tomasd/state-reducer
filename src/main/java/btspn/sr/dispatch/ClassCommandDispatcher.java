package btspn.sr.dispatch;

import btspn.sr.CommandFunction;
import btspn.sr.EventFunction;
import javaslang.Tuple2;
import javaslang.collection.HashMap;
import javaslang.collection.List;
import javaslang.collection.Map;

public class ClassCommandDispatcher<S, Ctx> implements CommandFunction<Object,S,Ctx> {
    private final Map<Class, CommandFunction> map;
    private final CommandFunction defaultHandler;

    private ClassCommandDispatcher(Map<Class, CommandFunction> map, CommandFunction defaultHandler) {
        this.map = map;
        this.defaultHandler = defaultHandler;
    }

    public ClassCommandDispatcher() {
        this(HashMap.empty(), null);
    }

    public ClassCommandDispatcher(CommandFunction<Object, S, Ctx> defaultHandler) {
        this(HashMap.empty(), defaultHandler);
    }


    @Override
    public Tuple2<List, S> apply(Object cmd, S s0, S sN, Ctx ctx, EventFunction<Object, S, Ctx> player) {
        CommandFunction<Object, S, Ctx> handler = map.get(cmd.getClass()).orElse(defaultHandler);
        if (handler == null) {
            throw new IllegalArgumentException("Handler not defined for " + cmd.getClass());
        }
        return handler.apply(cmd, s0, sN, ctx, player);
    }

    public <C> ClassCommandDispatcher<S, Ctx> on(Class<C> clazz, CommandFunction<C, S, Ctx> fn) {
        return new ClassCommandDispatcher<>(map.put(clazz, fn), defaultHandler);
    }

    public <C> ClassCommandDispatcher<S, Ctx> orElse(CommandFunction<Object, S, Ctx> fn) {
        return new ClassCommandDispatcher<>(map, fn);
    }
}
