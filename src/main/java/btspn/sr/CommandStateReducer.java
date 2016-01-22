package btspn.sr;

import javaslang.collection.List;

class CommandStateReducer<S, Ctx> implements EsStateReducer<S, Ctx>{
    private final CommandFunction cmdHandler;
    private final EventFunction eventHandler;

    CommandStateReducer(CommandFunction cmdHandler, EventFunction eventHandler) {
        this.cmdHandler = cmdHandler;
        this.eventHandler = eventHandler;
    }

    @Override
    public S apply(EventStore eventStore, Ctx ctx, Object id, Object cmd) {
        final S s0 = (S) eventHandler.reduce(null, ctx, eventStore.events(id));
        List events = cmdHandler.reduce(s0, ctx, cmd);
        eventStore.record(id, events);
        S sN = (S) eventHandler.reduce(s0, ctx, events);
        return sN;
    }

}
