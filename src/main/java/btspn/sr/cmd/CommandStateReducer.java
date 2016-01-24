package btspn.sr.cmd;

import btspn.sr.CommandFunction;
import btspn.sr.EsStateReducer;
import btspn.sr.EventFunction;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.List;

import java.util.function.Supplier;

public class CommandStateReducer<S, Ctx> implements EsStateReducer<S, Ctx> {
    private final CommandFunction<S, Ctx> cmdHandler;
    private final EventFunction<S, Ctx> eventHandler;
    private final Integer unsnapshottedEvents;

    public CommandStateReducer(CommandFunction<S, Ctx> cmdHandler, EventFunction<S, Ctx> eventHandler, Integer unsnapshottedEvents) {
        this.cmdHandler = cmdHandler;
        this.eventHandler = eventHandler;
        this.unsnapshottedEvents = unsnapshottedEvents;
    }

    @Override
    public S apply(EventStore eventStore, Supplier<Ctx> ctx, Object id, Object cmd) {
        Ctx ctx1 = ctx.get();
        Tuple2<Integer, S> snapshot = eventStore.lastSnapshot(id);
        List headEvents = eventStore.events(id, snapshot._1);
        final S s0 = eventHandler.reduce(snapshot._2, snapshot._2, ctx1, headEvents);
        Tuple2<List, S> tailEvents = cmdHandler.reduce(s0, ctx1, cmd, eventHandler);
        int events = headEvents.length() + tailEvents._1.length();
        S sN = tailEvents._2;
        if (unsnapshottedEvents != null && unsnapshottedEvents < events) {
            eventStore.record(id, tailEvents._1, Tuple.of(snapshot._1 + events, sN));
        } else {
            eventStore.record(id, tailEvents._1);
        }

        return sN;
    }

}
