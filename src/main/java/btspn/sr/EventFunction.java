package btspn.sr;

import javaslang.Function1;
import javaslang.Function2;
import javaslang.Function3;
import javaslang.Function4;

import java.util.Arrays;

/**
 * Event function applies event on current state and returns new state.
 *
 * @param <E>   event
 * @param <S>   state
 * @param <Ctx> context
 */
public interface EventFunction<E, S, Ctx> extends Function4<E, S, S, Ctx, S> {
    /**
     * Invariant wrapper.
     * <p>
     * Invariant is useful for validation and recomputation.
     *
     * @param fn    function
     * @param <E>   event
     * @param <S>   state
     * @param <Ctx> context
     * @return event function
     */
    static <E, S, Ctx> EventFunction<E, S, Ctx> i(Function1<S, S> fn) {
        return (c, s, s2, ctx) -> fn.apply(s2);
    }

    /**
     * Invariant wrapper.
     * <p>
     * Invariant is useful for validation and recomputation.
     *
     * @param fn    function
     * @param <E>   event
     * @param <S>   state
     * @param <Ctx> context
     * @return event function
     */
    static <E, S, Ctx> EventFunction<E, S, Ctx> i(Function2<S, Ctx, S> fn) {
        return (c, s, s2, ctx) -> fn.apply(s2, ctx);
    }

    /**
     * Process wrapper.
     * <p>
     * Process is useful for command validation and process.
     *
     * @param fn    function
     * @param <E>   event
     * @param <S>   state
     * @param <Ctx> context
     * @return event function
     */
    static <E, S, Ctx> EventFunction<E, S, Ctx> p(Function2<E, S, S> fn) {
        return (c, s, s2, ctx) -> fn.apply(c, s2);
    }

    /**
     * Process wrapper.
     * <p>
     * Process is useful for command validation and process.
     *
     * @param fn    function
     * @param <E>   event
     * @param <S>   state
     * @param <Ctx> context
     * @return event function
     */
    static <E, S, Ctx> EventFunction<E, S, Ctx> p(Function3<E, S, Ctx, S> fn) {
        return (c, s, s2, ctx) -> fn.apply(c, s2, ctx);
    }

    /**
     * Thread wrapper.
     * <p>
     * Wires multiple event functions into one.
     *
     * @param fns   function
     * @param <E>   event
     * @param <S>   state
     * @param <Ctx> context
     * @return event function
     */
    @SafeVarargs
    static <E, S, Ctx> EventFunction<E, S, Ctx> t(EventFunction<E, S, Ctx>... fns) {
        return (c, s, s2, ctx) -> {

            for (EventFunction<E, S, Ctx> fn : fns) {
                s2 = fn.apply(c, s, s2, ctx);
            }
            return s2;
        };
    }


    /**
     * Empty event function.
     *
     * @param <E>   event
     * @param <S>   state
     * @param <Ctx> context
     * @return event function
     */
    static <E, S, Ctx> EventFunction<E, S, Ctx> nil() {
        return (c, s, s2, ctx) -> s2;
    }

    default S reduce(S s0, Ctx ctx, Object... events) {
        return reduce(s0, s0, ctx, events);
    }

    default S reduce(S s0, S sN, Ctx ctx, Object... events) {
        return reduce(s0, sN, ctx, Arrays.asList(events));
    }

    default S reduce(S s0, S sN, Ctx ctx, Iterable events) {
        for (Object event : events) {
            sN = apply((E) event, s0, sN, ctx);
        }
        return sN;
    }
}
