package btspn.sr;

import javaslang.Function3;

public interface StateReducer<S, Ctx> extends Function3<Holder<S>, Ctx, Object, S> {
    static <S, Ctx> StateReducer<S, Ctx> event(EventFunction<?, S, Ctx> fn) {
        return new EventStateReducer<>(fn);
    }
}
