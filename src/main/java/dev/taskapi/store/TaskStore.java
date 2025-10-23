package dev.taskapi.store;

import dev.taskapi.model.Task;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskStore {
    private final Map<Integer, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    public TaskStore() {}

    public List<Task> list() {
        ArrayList<Task> out = new ArrayList<>(tasks.values());
        out.sort(Comparator.comparing(Task::getId));
        return out;
    }

    public Optional<Task> get(int id) {
        return Optional.ofNullable(tasks.get(id));
    }

    public Task create(Task t) {
        int id = nextId.getAndIncrement();
        if (t.getStatus() == null) t.setStatus("pending");
        t.setId(id);
        tasks.put(id, t);
        return t;
    }

    public Optional<Task> update(int id, Task patch) {
        return Optional.ofNullable(tasks.computeIfPresent(id, (k, existing) -> {
            if (patch.getTitle() != null) existing.setTitle(patch.getTitle());
            if (patch.getDescription() != null) existing.setDescription(patch.getDescription());
            if (patch.getStatus() != null) existing.setStatus(patch.getStatus());
            return existing;
        }));
    }
}
