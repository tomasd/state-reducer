package btspn.sr.event;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Holder<T> extends Supplier<T>, Consumer<T> {
    static <T> Holder<T> hold(T value) {
        return new AtomicHolder<>(value);
    }
}
