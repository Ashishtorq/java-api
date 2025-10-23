package dev.taskapi.store;

import dev.taskapi.model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskStoreTest {

    @Test
    public void testCreateGetUpdate() {
        TaskStore s = new TaskStore();
        Task t = new Task(null, "title", "desc", null);
        Task created = s.create(t);
        assertNotNull(created.getId());
        assertEquals("pending", created.getStatus());

        var fetched = s.get(created.getId());
        assertTrue(fetched.isPresent());
        assertEquals("title", fetched.get().getTitle());

        Task patch = new Task();
        patch.setTitle("new title");
        s.update(created.getId(), patch);
        var after = s.get(created.getId());
        assertEquals("new title", after.get().getTitle());
    }
}
