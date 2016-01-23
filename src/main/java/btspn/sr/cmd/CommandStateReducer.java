package btspn.sr.cmd;

import btspn.sr.CommandFunction;
import btspn.sr.EsStateReducer;
import btspn.sr.EventFunction;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.List;

import java.util.function.Supplier;

public class CommandStateReducer<S, Ctx> implements EsStateReducer<S, Ctx> {
    private final CommandFunction cmdHandler;
    private final EventFunction eventHandler;
    private final Integer unsnapshottedEvents;

    public CommandStateReducer(CommandFunction cmdHandler, EventFunction eventHandler, Integer unsnapshottedEvents) {
        this.cmdHandler = cmdHandler;
        this.eventHandler = eventHandler;
        this.unsnapshottedEvents = unsnapshottedEvents;
    }

    @Override
    public S apply(EventStore eventStore, Supplier<Ctx> ctx, Object id, Object cmd) {
        Ctx ctx1 = ctx.get();
        Tuple2<Integer, Object> snapshot = eventStore.lastSnapshot(id);
        List headEvents = eventStore.events(id, snapshot._1);
        final S s0 = (S) eventHandler.reduce(snapshot._2, ctx1, headEvents);
        List tailEvents = cmdHandler.reduce(s0, ctx1, cmd);
        int events = headEvents.length() + tailEvents.length();
        S sN = (S) eventHandler.reduce(s0, ctx1, tailEvents);
        if (unsnapshottedEvents != null && unsnapshottedEvents < events) {
            eventStore.record(id, tailEvents, Tuple.of(snapshot._1 + events, sN));
        } else {
            eventStore.record(id, tailEvents);
        }

        return sN;
    }

}
