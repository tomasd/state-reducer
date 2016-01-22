package btspn.sr;

class EventStateReducer<S, Ctx> implements StateReducer<S, Ctx>{
    private final EventFunction handler;

    EventStateReducer(EventFunction<?, S, Ctx> handler) {
        this.handler = handler;
    }


    @Override
    public S apply( Holder<S> holder, Ctx ctx, Object event) {
        S s0 = holder.get();
        S sN = (S) handler.apply(event, s0, s0, ctx);
        holder.accept(sN);
        return sN;
    }
}
