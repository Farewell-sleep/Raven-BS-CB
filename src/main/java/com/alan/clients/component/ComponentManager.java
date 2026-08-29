package com.alan.clients.component;

import java.util.ArrayList;
import java.util.List;

public class ComponentManager {
    private final List<Component> components = new ArrayList<>();

    public void a(Component component) {
        if (component != null && !this.components.contains(component)) this.components.add(component);
    }

    public <T extends Component> T b(Class<T> type) {
        for (Component c : this.components) {
            if (type.isInstance(c)) return (T) c;
        }
        return null;
    }

    public void init() {
    }
}
