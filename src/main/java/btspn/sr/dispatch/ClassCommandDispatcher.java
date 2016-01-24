package btspn.sr.dispatch;

import btspn.sr.CommandFunction;
import btspn.sr.EventFunction;
import btspn.sr.StateReducer;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.HashMap;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.control.Option;

import java.util.function.Predicate;

public class ClassCommandDispatcher<S, Ctx> implements CommandFunction<Object,S,Ctx> {
    private final List<Tuple2<Predicate, CommandFunction>> predicates;
    private final Map<Class, CommandFunction> map;
    private final CommandFunction defaultHandler;

    private ClassCommandDispatcher(List<Tuple2<Predicate, CommandFunction>> predicates, Map<Class, CommandFunction> map, CommandFunction defaultHandler) {
        this.predicates = predicates;
        this.map = map;
        this.defaultHandler = defaultHandler;
    }

    public ClassCommandDispatcher() {
        this(List.empty(), HashMap.empty(), null);
    }

    public ClassCommandDispatcher(CommandFunction<Object, S, Ctx> defaultHandler) {
        this(List.empty(), HashMap.empty(), defaultHandler);
    }


    @Override
    public Tuple2<List, S> apply(Object cmd, S s0, S sN, Ctx ctx, EventFunction<Object, S, Ctx> player) {
        Option<CommandFunction> byPredicate = predicates.findFirst(t -> t._1.test(cmd)).map(Tuple2::_2);
        CommandFunction<Object, S, Ctx> handler = byPredicate.orElse(this.map.get(cmd.getClass()).orElse(defaultHandler));
        if (handler == null) {
            throw new IllegalArgumentException("Handler not defined for " + cmd.getClass());
        }
        return handler.apply(cmd, s0, sN, ctx, player);
    }

    public <C> ClassCommandDispatcher<S, Ctx> on(Class<C> clazz, CommandFunction<C, S, Ctx> fn) {
        return new ClassCommandDispatcher<>(predicates, map.put(clazz, fn), defaultHandler);
    }

    public <E> ClassCommandDispatcher<S, Ctx> on(Predicate<E> predicate, CommandFunction<E, S, Ctx> fn) {
        return new ClassCommandDispatcher<>(predicates.append(Tuple.of(predicate, fn)), map, defaultHandler);
    }

    public <C> ClassCommandDispatcher<S, Ctx> orElse(CommandFunction<Object, S, Ctx> fn) {
        return new ClassCommandDispatcher<>(predicates, map, fn);
    }
}
