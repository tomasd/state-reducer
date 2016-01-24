package btspn.sr;

import btspn.sr.dispatch.ClassCommandDispatcher;
import btspn.sr.dispatch.ClassEsStateReducingDispatcher;
import btspn.sr.dispatch.ClassEventDispatcher;
import btspn.sr.dispatch.ClassStateReducingDispatcher;
import javaslang.collection.HashMap;

public class Dispatch {
    public static <S, Ctx> ClassEventDispatcher<S, Ctx> event() {
        return new ClassEventDispatcher<>();
    }

    public static <S, Ctx> ClassEventDispatcher<S, Ctx> event(EventFunction<Object, S, Ctx> defaultHandler) {
        return new ClassEventDispatcher<>(defaultHandler);
    }

    public static <S, Ctx> ClassCommandDispatcher<S, Ctx> cmd() {
        return new ClassCommandDispatcher<>();
    }

    public static <S, Ctx> ClassCommandDispatcher<S, Ctx> cmd(CommandFunction<Object, S, Ctx> defaultHandler) {
        return new ClassCommandDispatcher<>(defaultHandler);
    }


    public static <S, Ctx> ClassStateReducingDispatcher<S, Ctx> state() {
        return new ClassStateReducingDispatcher<>();
    }

    public static <S, Ctx> ClassStateReducingDispatcher<S, Ctx> state(StateReducer<S, Ctx> defaultHandler) {
        return new ClassStateReducingDispatcher<>(defaultHandler);
    }

    public static <S, Ctx> ClassStateReducingDispatcher<S, Ctx> state(EventFunction<?, S, Ctx> eventFn) {
        return new ClassStateReducingDispatcher<>(StateReducer.of(eventFn));
    }

    public static <S, Ctx> ClassEsStateReducingDispatcher<S, Ctx> esState() {
        return new ClassEsStateReducingDispatcher<>();
    }

    public static <S, Ctx> ClassEsStateReducingDispatcher<S, Ctx> esState(
            CommandFunction<Object, S, Ctx> cmdFn, EventFunction<Object, S, Ctx> eventFn
    ) {
        return new ClassEsStateReducingDispatcher<>(EsStateReducer.of(cmdFn, eventFn));
    }

    public static <S, Ctx> ClassEsStateReducingDispatcher<S, Ctx> esState(
            CommandFunction<Object, S, Ctx> cmdFn, EventFunction<Object, S, Ctx> eventFn, Integer snapshotEach
    ) {
        return new ClassEsStateReducingDispatcher<>(EsStateReducer.of(cmdFn, eventFn, snapshotEach));
    }

}
