package btspn.sr;

import btspn.sr.dispatch.ClassCommandDispatcher;
import btspn.sr.dispatch.ClassEsStateReducingDispatcher;
import btspn.sr.dispatch.ClassEventDispatcher;
import btspn.sr.dispatch.ClassStateReducingDispatcher;
import javaslang.control.Match;

public class Dispatch {
    public static <S, Ctx> ClassEventDispatcher<S, Ctx> event() {
        return new ClassEventDispatcher<>();
    }

    public static <S, Ctx> EventFunction<S, Ctx> event(Match<EventFunction<S, Ctx>> match) {
        return (o, s, s2, ctx) -> match.apply(o).apply(o, s, s2, ctx);
    }

    public static <S, Ctx> ClassCommandDispatcher<S, Ctx> cmd() {
        return new ClassCommandDispatcher<>();
    }

    public static <S, Ctx> CommandFunction<S, Ctx> cmd(Match<CommandFunction<S, Ctx>> match) {
        return (o, s, s2, ctx, player) -> match.apply(o).apply(o, s, s2, ctx, player);
    }

    public static <S, Ctx> ClassStateReducingDispatcher<S, Ctx> state() {
        return new ClassStateReducingDispatcher<>();
    }

    public static <S, Ctx> StateReducer<S, Ctx> state(Match<StateReducer<S, Ctx>> match) {
        return (sHolder, ctxSupplier, o) -> match.apply(o).apply(sHolder, ctxSupplier, o);
    }

    public static <S, Ctx> ClassEsStateReducingDispatcher<S, Ctx> esState() {
        return new ClassEsStateReducingDispatcher<>();
    }

    public static <S, Ctx> EsStateReducer<S, Ctx> esState(Match<EsStateReducer<S, Ctx>> match) {
        return (eventStore, ctxSupplier, id, cmd) -> match.apply(cmd).apply(eventStore, ctxSupplier, id, cmd);
    }
}
