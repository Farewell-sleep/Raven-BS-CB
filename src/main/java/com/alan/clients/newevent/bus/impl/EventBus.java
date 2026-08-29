package com.alan.clients.newevent.bus.impl;

import com.alan.clients.newevent.Event;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus<E extends Event> {
    private final List<Object> listeners = new CopyOnWriteArrayList<>();

    public void b(Object listener) {
        if (listener != null && !listeners.contains(listener)) listeners.add(listener);
    }

    public void c(Object listener) {
        listeners.remove(listener);
    }

    public void d(E event) {
        for (Object o : listeners) {
            for (Field f : o.getClass().getDeclaredFields()) {
                if (f.isAnnotationPresent(EventLink.class) && Listener.class.isAssignableFrom(f.getType())) {
                    try {
                        f.setAccessible(true);
                        Object l = f.get(o);
                        if (l instanceof Listener) ((Listener) l).call(event);
                    } catch (Throwable ignored) {
                    }
                }
            }
        }
    }
}
