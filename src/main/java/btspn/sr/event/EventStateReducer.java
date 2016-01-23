package btspn.sr.event;

import btspn.sr.EventFunction;
import btspn.sr.StateReducer;

import java.util.function.Supplier;

public class EventStateReducer<S, Ctx> implements StateReducer<S, Ctx> {
    private final EventFunction handler;

    public EventStateReducer(EventFunction<?, S, Ctx> handler) {
        this.handler = handler;
    }


    @Override
    public S apply(Holder<S> holder, Supplier<Ctx> ctx, Object event) {
        S s0 = holder.get();
        S sN = (S) handler.apply(event, s0, s0, ctx.get());
        holder.accept(sN);
        return sN;
    }
}
