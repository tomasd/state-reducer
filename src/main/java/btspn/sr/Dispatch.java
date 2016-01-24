package btspn.sr;

import btspn.sr.dispatch.ClassCommandDispatcher;
import btspn.sr.dispatch.ClassEsStateReducingDispatcher;
import btspn.sr.dispatch.ClassEventDispatcher;
import btspn.sr.dispatch.ClassStateReducingDispatcher;

public class Dispatch {
    public static <S, Ctx> ClassEventDispatcher<S, Ctx> event() {
        return new ClassEventDispatcher<>();
    }


    public static <S, Ctx> ClassCommandDispatcher<S, Ctx> cmd() {
        return new ClassCommandDispatcher<>();
    }


    public static <S, Ctx> ClassStateReducingDispatcher<S, Ctx> state() {
        return new ClassStateReducingDispatcher<>();
    }


    public static <S, Ctx> ClassEsStateReducingDispatcher<S, Ctx> esState() {
        return new ClassEsStateReducingDispatcher<>();
    }
}
