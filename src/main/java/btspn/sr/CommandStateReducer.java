package btspn.sr;

import javaslang.collection.List;

import java.util.function.Supplier;

class CommandStateReducer<S, Ctx> implements EsStateReducer<S, Ctx>{
    private final CommandFunction cmdHandler;
    private final EventFunction eventHandler;

    CommandStateReducer(CommandFunction cmdHandler, EventFunction eventHandler) {
        this.cmdHandler = cmdHandler;
        this.eventHandler = eventHandler;
    }

    @Override
    public S apply(EventStore eventStore, Supplier<Ctx> ctx, Object id, Object cmd) {
        Ctx ctx1 = ctx.get();
        final S s0 = (S) eventHandler.reduce(null, ctx1, eventStore.events(id));
        List events = cmdHandler.reduce(s0, ctx1, cmd);
        eventStore.record(id, events);
        S sN = (S) eventHandler.reduce(s0, ctx1, events);
        return sN;
    }

}
