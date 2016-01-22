package btspn.sr;

import javaslang.Function1;
import javaslang.Function2;
import javaslang.Function3;
import javaslang.Function4;
import javaslang.collection.List;

public interface CommandFunction<C, S, Ctx> extends Function4<C, S, S, Ctx, List> {
    static <C, S, Ctx> CommandFunction<C, S, Ctx> ci(Function1<S, List> fn) {
        return (c, s, s2, ctx) -> fn.apply(s2);
    }

    static <C, S, Ctx> CommandFunction<C, S, Ctx> ci(Function2<S, Ctx, List> fn) {
        return (c, s, s2, ctx) -> fn.apply(s2, ctx);
    }

    static <C, S, Ctx> CommandFunction<C, S, Ctx> cp(Function2<C, S, List> fn) {
        return (c, s, s2, ctx) -> fn.apply(c, s2);
    }

    static <C, S, Ctx> CommandFunction<C, S, Ctx> cp(Function3<C, S, Ctx, List> fn) {
        return (c, s, s2, ctx) -> fn.apply(c, s2, ctx);
    }

    @SafeVarargs
    static <C, S, Ctx> CommandFunction<C, S, Ctx> ct(CommandFunction<C, S, Ctx>... fns) {
        return (c, s, s2, ctx) -> {
            List<Object> events = List.empty();
            for (CommandFunction<C, S, Ctx> fn : fns) {
                events = events.appendAll(fn.apply(c, s, s2, ctx));
            }
            return events;
        };
    }

    static <C, S, Ctx> CommandFunction<C, S, Ctx> nil() {
        return (c, s, s2, ctx) -> List.empty();
    }

    default List reduce(S state, Ctx ctx, C command) {
        return apply(command, state, state, ctx);
    }
}
