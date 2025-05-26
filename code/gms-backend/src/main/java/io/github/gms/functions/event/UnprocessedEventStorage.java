package io.github.gms.functions.event;

import io.github.gms.common.model.UserEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Component
public class UnprocessedEventStorage {
    private static final List<UserEvent> items = new CopyOnWriteArrayList<>();

    public void addToQueue(UserEvent item) {
        items.add(item);
    }

    public List<UserEvent> getAll(boolean clear) {
        List<UserEvent> resultList = new ArrayList<>(items);

        if (clear) {
            items.clear();
        }

        return resultList;
    }
}