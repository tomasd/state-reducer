package btspn.sr;

import btspn.sr.cmd.CommandStateReducer;
import btspn.sr.cmd.EventStore;
import javaslang.Function4;

import java.util.function.Supplier;

/**
 * Combination of command and event function used together with EventStore for CQRS.
 *
 * @param <S>
 * @param <Ctx>
 */
public interface EsStateReducer<S, Ctx> extends Function4<EventStore, Supplier<Ctx>, Object, Object, S> {
    static <S, Ctx> EsStateReducer<S, Ctx> of(CommandFunction<Object, S, Ctx> cmdFn, EventFunction<Object, S, Ctx> eventFn) {
        return of(cmdFn, eventFn, null);
    }

    static <S, Ctx> EsStateReducer<S, Ctx> of(CommandFunction<Object, S, Ctx> cmdFn, EventFunction<Object, S, Ctx> eventFn, Integer snapshotEach) {
        return new CommandStateReducer<>(cmdFn, eventFn, snapshotEach);
    }
}
