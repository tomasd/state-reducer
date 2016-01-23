package btspn.sr;

import btspn.sr.event.EventStateReducer;
import btspn.sr.event.Holder;
import javaslang.Function3;

import java.util.function.Supplier;

/**
 * Kind of event function with side effects separated into Holder.
 *
 * @param <S> state
 * @param <Ctx> context
 */
public interface StateReducer<S, Ctx> extends Function3<Holder<S>, Supplier<Ctx>, Object, S> {
    static <S, Ctx> StateReducer<S, Ctx> of(EventFunction<?, S, Ctx> fn) {
        return new EventStateReducer<>(fn);
    }
}
