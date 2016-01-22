package btspn.sr;

import java.util.concurrent.atomic.AtomicReference;

class AtomicHolder<T> implements Holder<T>{
    private final AtomicReference<T> ref;

    AtomicHolder(T value) {
        this.ref = new AtomicReference<>(value);
    }

    @Override
    public void accept(T t) {
        ref.set(t);
    }

    @Override
    public T get() {
        return ref.get();
    }
}
