package com.drencak.sr;

import javaslang.Function1;
import javaslang.Function2;
import javaslang.Function3;
import javaslang.Function4;

import java.util.Arrays;

/**
 * Event function applies event on current state and returns new state.
 *
 * @param <S>   state
 * @param <Ctx> context
 */
public interface EventFunction<S, Ctx> extends Function4<Object, S, S, Ctx, S> {
    /**
     * Invariant wrapper.
     * <p>
     * Invariant is useful for validation and recomputation.
     *
     * @param fn    function
     * @param <S>   state
     * @param <Ctx> context
     * @return event function
     */
    static <S, Ctx> EventFunction<S, Ctx> i(Function1<S, S> fn) {
        return (c, s, s2, ctx) -> fn.apply(s2);
    }

    /**
     * Invariant wrapper.
     * <p>
     * Invariant is useful for validation and recomputation.
     *
     * @param fn    function
     * @param <S>   state
     * @param <Ctx> context
     * @return event function
     */
    static <S, Ctx> EventFunction<S, Ctx> i(Function2<S, Ctx, S> fn) {
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
    static <E, S, Ctx> EventFunction<S, Ctx> p(Function2<E, S, S> fn) {
        return (c, s, s2, ctx) -> fn.apply((E) c, s2);
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
    static <E, S, Ctx> EventFunction<S, Ctx> p(Function2<E, S, S> fn, Class<Ctx> ctxClass) {
        return (c, s, s2, ctx) -> fn.apply((E) c, s2);
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
    static <E, S, Ctx> EventFunction<S, Ctx> p(Function3<E, S, Ctx, S> fn) {
        return (c, s, s2, ctx) -> fn.apply((E) c, s2, ctx);
    }

    /**
     * Thread wrapper.
     * <p>
     * Wires multiple event functions into one.
     *
     * @param fns   function
     * @param <S>   state
     * @param <Ctx> context
     * @return event function
     */
    @SafeVarargs
    static <S, Ctx> EventFunction<S, Ctx> t(EventFunction<S, Ctx>... fns) {
        return (c, s, s2, ctx) -> {

            for (EventFunction<S, Ctx> fn : fns) {
                s2 = fn.apply(c, s, s2, ctx);
            }
            return s2;
        };
    }


    /**
     * Empty event function.
     *
     * @param <S>   state
     * @param <Ctx> context
     * @return event function
     */
    static <S, Ctx> EventFunction<S, Ctx> nil() {
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
            sN = apply(event, s0, sN, ctx);
        }
        return sN;
    }
}
