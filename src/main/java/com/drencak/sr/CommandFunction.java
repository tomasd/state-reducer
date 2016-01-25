package com.drencak.sr;

import javaslang.*;
import javaslang.collection.List;

/**
 * Command function processes command on current state and returns list of events .
 *
 * @param <S>   state
 * @param <Ctx> context
 */
public interface CommandFunction<S, Ctx> extends Function5<Object, S, S, Ctx, EventFunction<S, Ctx>, Tuple2<List, S>> {
    /**
     * Invariant wrapper.
     * <p>
     * Creates a command function from function taking state and returning list. Useful
     * for validation and computing derived values.
     *
     * @param fn    function
     * @param <S>   state
     * @param <Ctx> context
     * @return command function
     */
    static <S, Ctx> CommandFunction<S, Ctx> ic(Function1<S, List> fn) {
        return (c, s, s2, ctx, player) -> play(player, s, s2, ctx, fn.apply(s2));
    }

    static <S, Ctx> Tuple2<List, S> play(EventFunction<S, Ctx> player, S s0, S s2, Ctx ctx, List events) {
        S reduce = player.reduce(s0, s2, ctx, events);
        return Tuple.of(events, reduce);
    }


    /**
     * Invariant wrapper.
     * <p>
     * Creates a command function from function taking state, context and returning list. Useful
     * for validation and computing derived values.
     *
     * @param fn    function
     * @param <S>   state
     * @param <Ctx> context
     * @return command function
     */
    static <S, Ctx> CommandFunction<S, Ctx> ic(Function2<S, Ctx, List> fn) {
        return (c, s, s2, ctx, player) -> play(player, s, s2, ctx, fn.apply(s2, ctx));
    }

    /**
     * Process wrapper.
     * <p>
     * Creates a command function from function taking command, current state and returning list of events.
     * Useful form command processing.
     *
     * @param fn    function
     * @param <C>   command
     * @param <S>   state
     * @param <Ctx> context
     * @return command function
     */
    static <C, S, Ctx> CommandFunction<S, Ctx> pc(Function2<C, S, List> fn) {
        return (c, s, s2, ctx, player) -> play(player, s, s2, ctx, fn.apply((C) c, s2));
    }

    /**
     * Process wrapper.
     * <p>
     * Creates a command function from function taking command, current state and returning list of events.
     * Useful form command processing.
     *
     * @param fn    function
     * @param <C>   command
     * @param <S>   state
     * @param <Ctx> context
     * @return command function
     */
    static <C, S, Ctx> CommandFunction<S, Ctx> pc(Function2<C, S, List> fn, Class<Ctx> ctxClazz) {
        return (c, s, s2, ctx, player) -> play(player, s, s2, ctx, fn.apply((C) c, s2));
    }


    /**
     * Process wrapper.
     * <p>
     * Creates a command function from function taking command, current state, context and returning list of events.
     * Useful form command processing.
     *
     * @param fn    function
     * @param <C>   command
     * @param <S>   state
     * @param <Ctx> context
     * @return command function
     */

    static <C, S, Ctx> CommandFunction<S, Ctx> pc(Function3<C, S, Ctx, List> fn) {
        return (c, s, s2, ctx, player) -> play(player, s, s2, ctx, fn.apply((C) c, s2, ctx));
    }

    /**
     * Threading wrapper.
     * <p>
     * Processes multiple command functions as 1 function. Each next function gets state generated by previous function.
     *
     * @param fns   functions
     * @param <S>   state
     * @param <Ctx> context
     * @return command function
     */
    @SafeVarargs
    static <S, Ctx> CommandFunction<S, Ctx> tc(CommandFunction<S, Ctx>... fns) {
        return (c, s, s2, ctx, player) -> {
            List<Object> events = List.empty();
            for (CommandFunction<S, Ctx> fn : fns) {
                Tuple2<List, S> t = fn.apply(c, s, s2, ctx, player);
                events = events.appendAll(t._1);
                s2 = t._2;
            }
            return Tuple.of(events, s2);
        };
    }

    /**
     * Empty command function.
     *
     * @param <S>   state
     * @param <Ctx> context
     * @return empty list
     */
    static <S, Ctx> CommandFunction<S, Ctx> nil() {
        return (c, s, s2, ctx, player) -> Tuple.of(List.empty(), s2);
    }

    /**
     * Start of the computations s0 == sN.
     *
     * @param state   state
     * @param ctx     context
     * @param command command
     * @return list of generated events
     */
    default Tuple2<List, S> reduce(S state, Ctx ctx, Object command, EventFunction<S, Ctx> player) {
        return apply(command, state, state, ctx, player);
    }
}