package btspn.sr;

import javaslang.Function1;
import javaslang.Function2;
import javaslang.Function3;
import javaslang.Function4;

public interface EventFunction<E, S, Ctx> extends Function4<E, S, S, Ctx, S> {
    static <E, S, Ctx> EventFunction<E, S, Ctx> ei(Function1<S, S> fn) {
        return (c, s, s2, ctx) -> fn.apply(s2);
    }

    static <E, S, Ctx> EventFunction<E, S, Ctx> ei(Function2<S, Ctx, S> fn) {
        return (c, s, s2, ctx) -> fn.apply(s2, ctx);
    }

    static <E, S, Ctx> EventFunction<E, S, Ctx> ep(Function2<E, S, S> fn) {
        return (c, s, s2, ctx) -> fn.apply(c, s2);
    }

    static <E, S, Ctx> EventFunction<E, S, Ctx> ep(Function3<E, S, Ctx, S> fn) {
        return (c, s, s2, ctx) -> fn.apply(c, s2, ctx);
    }

    @SafeVarargs
    static <E, S, Ctx> EventFunction<E, S, Ctx> et(EventFunction<E, S, Ctx>... fns) {
        return (c, s, s2, ctx) -> {

            for (EventFunction<E, S, Ctx> fn : fns) {
                s2 = fn.apply(c, s, s2, ctx);
            }
            return s2;
        };
    }


    static <E, S, Ctx> EventFunction<E, S, Ctx> nil() {
        return (c, s, s2, ctx) -> s2;
    }

    default S reduce(S s0, Ctx ctx, Object... events) {
        S sN = s0;
        for (Object event : events) {
            sN = apply((E) event, s0, sN, ctx);
        }
        return sN;
    }

    default S reduce(S s0, Ctx ctx, Iterable events) {
        S sN = s0;
        for (Object event : events) {
            sN = apply((E) event, s0, sN, ctx);
        }
        return sN;
    }
}
