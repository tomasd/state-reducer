package com.drencak.sr.cmd;

import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.List;

import java.util.HashMap;
import java.util.Map;

public class InMemoryStore implements EventStore {
    private final Map<Object, List> map;
    private final Map<Object, Tuple2<Integer, Object>> snapshots;

    private InMemoryStore(Map<Object, List> map, Map<Object, Tuple2<Integer, Object>> snapshots) {
        this.map = map;
        this.snapshots = snapshots;
    }

    public static EventStore empty() {
        return new InMemoryStore(new HashMap<>(), new HashMap<>());
    }

    @Override
    public List events(Object id, int sinceVersion) {
        return map.getOrDefault(id, List.empty()).subSequence(sinceVersion);
    }

    @Override
    public void record(Object id, List events, Tuple2<Integer, Object> snapshot) {
        map.compute(id, (o, list) -> {
            return (list != null ? list : List.empty()).appendAll(events);
        });

        if (snapshot != null) {
            snapshots.put(id, snapshot);
        }
    }

    @Override
    public <S> Tuple2<Integer, S> lastSnapshot(Object id) {
        return (Tuple2<Integer, S>) snapshots.getOrDefault(id, Tuple.of(0, null));
    }

}
