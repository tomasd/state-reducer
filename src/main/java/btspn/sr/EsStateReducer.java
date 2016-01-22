package btspn.sr;

import javaslang.Function4;

public interface EsStateReducer<S, Ctx> extends Function4<EventStore, Ctx, Object, Object, S> {
    static <S, Ctx> StateReducer<S, Ctx> event(EventFunction<?, S, Ctx> fn) {
        return StateReducer.event(fn);
    }

    static <S, Ctx> EsStateReducer<S, Ctx> cmd(CommandFunction<?, S, Ctx> cmdFn, EventFunction<?, S, Ctx> eventFn) {
        return new CommandStateReducer<>(cmdFn, eventFn);
    }
}
