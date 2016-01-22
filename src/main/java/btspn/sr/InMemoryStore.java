package btspn.sr;

import javaslang.collection.List;

import java.util.HashMap;
import java.util.Map;

public class InMemoryStore implements EventStore {
    private final Map<Object, List> map;

    private InMemoryStore(Map<Object, List> map) {
        this.map = map;
    }

    public static EventStore empty() {
        return new InMemoryStore(new HashMap<>());
    }

    @Override
    public List events(Object id) {
        return map.getOrDefault(id, List.empty());
    }

    @Override
    public void record(Object id, List events) {
        map.compute(id, (o, list) -> {
            return (list != null ? list : List.empty()).appendAll(events);
        });
    }
}
