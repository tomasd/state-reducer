package btspn.sr.cmd;

import javaslang.Tuple2;
import javaslang.collection.List;

public interface EventStore {
    List events(Object id, int sinceVersion);

    default List events(Object id) {
        return events(id, 0);
    }

    default void record(Object id, List events) {
        record(id, events, null);
    }

    void record(Object id, List events, Tuple2<Integer, Object> snapshot);

    <S> Tuple2<Integer, S> lastSnapshot(Object id);

}
