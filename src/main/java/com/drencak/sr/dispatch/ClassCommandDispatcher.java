package com.drencak.sr.dispatch;

import com.drencak.sr.CommandFunction;
import com.drencak.sr.EventFunction;
import javaslang.Tuple2;
import javaslang.collection.HashMap;
import javaslang.collection.List;
import javaslang.collection.Map;

public class ClassCommandDispatcher<S, Ctx> implements CommandFunction<S, Ctx> {
    private final Map<Class, CommandFunction> map;
    private final CommandFunction defaultHandler;

    public ClassCommandDispatcher(Map<Class, CommandFunction> map, CommandFunction defaultHandler) {
        this.map = map;
        this.defaultHandler = defaultHandler;
    }

    public ClassCommandDispatcher() {
        this(HashMap.empty(), null);
    }

    @Override
    public Tuple2<List, S> apply(Object cmd, S s0, S sN, Ctx ctx, EventFunction<S, Ctx> player) {
        CommandFunction<S, Ctx> handler = this.map.get(cmd.getClass()).getOrElse(defaultHandler);
        if (handler == null) {
            throw new IllegalArgumentException("Handler not defined for " + cmd.getClass());
        }
        return handler.apply(cmd, s0, sN, ctx, player);
    }

    public <C> ClassCommandDispatcher<S, Ctx> on(Class<C> clazz, CommandFunction<S, Ctx> fn) {
        return new ClassCommandDispatcher<>(map.put(clazz, fn), defaultHandler);
    }

    public <C> ClassCommandDispatcher<S, Ctx> orElse(CommandFunction<S, Ctx> fn) {
        return new ClassCommandDispatcher<>(map, fn);
    }

    public CommandFunction<S,Ctx> cast() {
        return this;
    }
}
