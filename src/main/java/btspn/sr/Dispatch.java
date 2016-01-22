package btspn.sr;

import javaslang.collection.HashMap;
import javaslang.collection.List;
import javaslang.collection.Map;

import java.util.function.Supplier;

public class Dispatch {
    public static <S, Ctx> ClassEventDispatcher<S, Ctx> event() {
        return ClassEventDispatcher.<S, Ctx>create();
    }

    public static <S, Ctx> ClassEventDispatcher<S, Ctx> event(EventFunction<Object, S, Ctx> defaultHandler) {
        return ClassEventDispatcher.<S, Ctx>create(defaultHandler);
    }

    public static <S, Ctx> ClassCommandDispatcher<S, Ctx> cmd() {
        return ClassCommandDispatcher.<S, Ctx>create();
    }

    public static <S, Ctx> ClassCommandDispatcher<S, Ctx> cmd(CommandFunction<Object, S, Ctx> defaultHandler) {
        return ClassCommandDispatcher.<S, Ctx>create(defaultHandler);
    }


    public static <S, Ctx> ClassStateReducingDispatcher<S, Ctx> state() {
        return ClassStateReducingDispatcher.<S, Ctx>create();
    }

    public static <S, Ctx> ClassStateReducingDispatcher<S, Ctx> state(StateReducer<S, Ctx> defaultHandler) {
        return ClassStateReducingDispatcher.<S, Ctx>create(defaultHandler);
    }

    public static <S, Ctx> ClassStateReducingDispatcher<S, Ctx> state(EventFunction<?, S, Ctx> eventFn) {
        return ClassStateReducingDispatcher.<S, Ctx>create(StateReducer.event(eventFn));
    }

    public static <S, Ctx> ClassEsStateReducingDispatcher<S, Ctx> esState() {
        return ClassEsStateReducingDispatcher.<S, Ctx>create();
    }

    public static <S, Ctx> ClassEsStateReducingDispatcher<S, Ctx> esState(CommandFunction<?, S, Ctx> cmdFn, EventFunction<?, S, Ctx> eventFn) {
        return ClassEsStateReducingDispatcher.<S, Ctx>create(EsStateReducer.cmd(cmdFn, eventFn));
    }

    public static class ClassEventDispatcher<S, Ctx> implements EventFunction<Object, S, Ctx> {
        private final Map<Class, EventFunction> map;
        private final EventFunction defaultHandler;

        private ClassEventDispatcher(Map<Class, EventFunction> map, EventFunction defaultHandler) {
            this.map = map;
            this.defaultHandler = defaultHandler;
        }

        @Override
        public S apply(Object event, S s0, S sN, Ctx ctx) {
            EventFunction<Object, S, Ctx> handler = map.get(event.getClass()).orElse(defaultHandler);
            if (handler == null) {
                throw new IllegalArgumentException("Handler not defined for " + event.getClass());
            }
            return handler.apply(event, s0, sN, ctx);
        }

        public <E> ClassEventDispatcher<S, Ctx> put(Class<E> clazz, EventFunction<E, S, Ctx> fn) {
            return new ClassEventDispatcher<>(map.put(clazz, fn), defaultHandler);
        }

        public <E> ClassEventDispatcher<S, Ctx> orElse(EventFunction<E, S, Ctx> fn) {
            return new ClassEventDispatcher<>(map, fn);
        }

        public static <S, Ctx> ClassEventDispatcher<S, Ctx> create(EventFunction<Object, S, Ctx> defaultHandler) {
            return new ClassEventDispatcher<>(HashMap.empty(), defaultHandler);
        }

        public static <S, Ctx> ClassEventDispatcher<S, Ctx> create() {
            return new ClassEventDispatcher<>(HashMap.empty(), null);
        }


    }

    public static class ClassCommandDispatcher<S, Ctx> implements CommandFunction<Object, S, Ctx> {
        private final Map<Class, CommandFunction> map;
        private final CommandFunction defaultHandler;

        private ClassCommandDispatcher(Map<Class, CommandFunction> map, CommandFunction defaultHandler) {
            this.map = map;
            this.defaultHandler = defaultHandler;
        }

        @Override
        public List apply(Object cmd, S s0, S sN, Ctx ctx) {
            CommandFunction<Object, S, Ctx> handler = map.get(cmd.getClass()).orElse(defaultHandler);
            if (handler == null) {
                throw new IllegalArgumentException("Handler not defined for " + cmd.getClass());
            }
            return handler.apply(cmd, s0, sN, ctx);
        }

        public <C> ClassCommandDispatcher<S, Ctx> put(Class<C> clazz, CommandFunction<C, S, Ctx> fn) {
            return new ClassCommandDispatcher<>(map.put(clazz, fn), defaultHandler);
        }

        public <C> ClassCommandDispatcher<S, Ctx> orElse(CommandFunction<Object, S, Ctx> fn) {
            return new ClassCommandDispatcher<>(map, fn);
        }

        public static <S, Ctx> ClassCommandDispatcher<S, Ctx> create(CommandFunction<Object, S, Ctx> defaultHandler) {
            return new ClassCommandDispatcher<>(HashMap.empty(), defaultHandler);
        }

        public static <S, Ctx> ClassCommandDispatcher<S, Ctx> create() {
            return new ClassCommandDispatcher<>(HashMap.empty(), null);
        }
    }

    public static class ClassStateReducingDispatcher<S, Ctx> implements StateReducer<S, Ctx> {
        private final Map<Class, StateReducer> map;
        private final StateReducer defaultHandler;

        private ClassStateReducingDispatcher(Map<Class, StateReducer> map, StateReducer defaultHandler) {
            this.map = map;
            this.defaultHandler = defaultHandler;
        }

        @Override
        public S apply(Holder<S> holder, Supplier<Ctx> ctx, Object command) {
            StateReducer<S, Ctx> handler = map.get(command.getClass()).orElse(defaultHandler);
            if (handler == null) {
                throw new IllegalArgumentException("Handler not defined for " + command.getClass());
            }
            return handler.apply(holder, ctx, command);
        }

        public ClassStateReducingDispatcher<S, Ctx> put(Class<?> clazz, StateReducer<S, Ctx> fn) {
            return new ClassStateReducingDispatcher<>(map.put(clazz, fn), defaultHandler);
        }

        public ClassStateReducingDispatcher<S, Ctx> orElse(StateReducer<S, Ctx> fn) {
            return new ClassStateReducingDispatcher<>(map, fn);
        }

        public static <S, Ctx> ClassStateReducingDispatcher<S, Ctx> create(StateReducer<S, Ctx> defaultHandler) {
            return new ClassStateReducingDispatcher<>(HashMap.empty(), defaultHandler);
        }

        public static <S, Ctx> ClassStateReducingDispatcher<S, Ctx> create() {
            return new ClassStateReducingDispatcher<>(HashMap.empty(), null);
        }

    }

    public static class ClassEsStateReducingDispatcher<S, Ctx> implements EsStateReducer<S, Ctx> {
        private final Map<Class, EsStateReducer> map;
        private final EsStateReducer defaultHandler;

        private ClassEsStateReducingDispatcher(Map<Class, EsStateReducer> map, EsStateReducer defaultHandler) {
            this.map = map;
            this.defaultHandler = defaultHandler;
        }

        @Override
        public S apply(EventStore eventStore, Supplier<Ctx> ctx, Object id, Object command) {
            EsStateReducer<S, Ctx> handler = map.get(command.getClass()).orElse(defaultHandler);
            if (handler == null) {
                throw new IllegalArgumentException("Handler not defined for " + command.getClass());
            }
            return handler.apply(eventStore, ctx, id, command);
        }

        public ClassEsStateReducingDispatcher<S, Ctx> put(Class<?> clazz, EsStateReducer<S, Ctx> fn) {
            return new ClassEsStateReducingDispatcher<>(map.put(clazz, fn), defaultHandler);
        }

        public ClassEsStateReducingDispatcher<S, Ctx> orElse(EsStateReducer<S, Ctx> fn) {
            return new ClassEsStateReducingDispatcher<>(map, fn);
        }

        public static <S, Ctx> ClassEsStateReducingDispatcher<S, Ctx> create(EsStateReducer<S, Ctx> defaultHandler) {
            return new ClassEsStateReducingDispatcher<>(HashMap.empty(), defaultHandler);
        }

        public static <S, Ctx> ClassEsStateReducingDispatcher<S, Ctx> create() {
            return new ClassEsStateReducingDispatcher<>(HashMap.empty(), null);
        }

    }

}
