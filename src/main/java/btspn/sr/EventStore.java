package btspn.sr;

import javaslang.collection.List;

public interface EventStore {
    List events(Object id);

    void record(Object id, List events);
}
