# -*- coding: utf-8 -*-
import os
base = r"C:\Games\raven-bs\src\main\java\com\alan\clients"

files = {}

files["newevent/Event.java"] = '''package com.alan.clients.newevent;

public interface Event {
}
'''

files["newevent/bus/impl/EventBus.java"] = '''package com.alan.clients.newevent.bus.impl;

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
'''

files["newevent/impl/other/ModuleToggleEvent.java"] = '''package com.alan.clients.newevent.impl.other;

import com.alan.clients.module.Module;
import com.alan.clients.newevent.Event;

public class ModuleToggleEvent implements Event {
    private final Module module;

    public ModuleToggleEvent(Module module) {
        this.module = module;
    }

    public Module getModule() {
        return this.module;
    }
}
'''

files["newevent/impl/other/AttackEvent.java"] = '''package com.alan.clients.newevent.impl.other;

import com.alan.clients.newevent.Event;
import net.minecraft.entity.Entity;

public class AttackEvent implements Event {
    private final Entity target;

    public AttackEvent(Entity target) {
        this.target = target;
    }

    public Entity getTarget() {
        return this.target;
    }
}
'''

files["newevent/impl/other/GameEvent.java"] = '''package com.alan.clients.newevent.impl.other;

import com.alan.clients.newevent.Event;

public class GameEvent implements Event {
}
'''

files["newevent/impl/game/GameEvent.java"] = '''package com.alan.clients.newevent.impl.game;

import com.alan.clients.newevent.Event;

public class GameEvent implements Event {
}
'''

files["newevent/impl/render/Render2DEvent.java"] = '''package com.alan.clients.newevent.impl.render;

import com.alan.clients.newevent.Event;

public class Render2DEvent implements Event {
    private final float partialTicks;

    public Render2DEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }
}
'''

files["newevent/impl/render/Render3DEvent.java"] = '''package com.alan.clients.newevent.impl.render;

import com.alan.clients.newevent.Event;

public class Render3DEvent implements Event {
    private final float partialTicks;

    public Render3DEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }
}
'''

files["newevent/impl/render/RenderGuiEvent.java"] = '''package com.alan.clients.newevent.impl.render;

import com.alan.clients.newevent.Event;

public class RenderGuiEvent implements Event {
}
'''

files["newevent/impl/input/GuiKeyEvent.java"] = '''package com.alan.clients.newevent.impl.input;

import com.alan.clients.newevent.Event;

public class GuiKeyEvent implements Event {
    private final int keyCode;

    public GuiKeyEvent(int keyCode) {
        this.keyCode = keyCode;
    }

    public int cO() {
        return this.keyCode;
    }
}
'''

files["security/SecurityFeatureManager.java"] = '''package com.alan.clients.security;

import java.util.ArrayList;
import java.util.List;

public class SecurityFeatureManager {
    private final List<Object> features = new ArrayList<>();

    public void a(Object feature) {
        this.features.add(feature);
    }

    public boolean nN() {
        return false;
    }

    public void init() {
    }
}
'''

files["component/ComponentManager.java"] = '''package com.alan.clients.component;

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
'''

files["util/file/config/ConfigFile.java"] = '''package com.alan.clients.util.file.config;

public class ConfigFile {
    public String getName() {
        return "";
    }

    public void te() {
    }

    public void write() {
    }
}
'''

files["util/file/config/ConfigManager.java"] = '''package com.alan.clients.util.file.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConfigManager {
    private final List<ConfigFile> configs = new ArrayList<>();

    public void update() {
    }

    public ConfigFile getConfigfile() {
        return new ConfigFile();
    }

    public void forEach(Consumer<ConfigFile> consumer) {
        for (ConfigFile c : this.configs) consumer.accept(c);
    }
}
'''

files["module/impl/render/Interface.java"] = '''package com.alan.clients.module.impl.render;

import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.value.impl.NumberValue;

@ModuleInfo(aliases = "module.render.interface.name", description = "module.render.interface.description", category = Category.RENDER)
public class Interface extends Module {
    public BooleanValue suffix = new BooleanValue("Suffix", this, true);
    public BooleanValue aoc = new BooleanValue("Shaders", this, false);
    private final NumberValue roundingRadius = new NumberValue("Rounding Radius", this, 5, 0, 20, 0.5);

    public Interface() {
    }

    public void rebuildEntries() {
    }

    public void createArrayList() {
    }

    public void updateEntryNames() {
    }

    public int getBlurRadius() {
        return 8;
    }

    public float getBlurCompression() {
        return 0.5F;
    }

    public int getBloomRadius() {
        return 8;
    }

    public float getBloomCompression() {
        return 0.5F;
    }

    public int getBackgroundAlpha() {
        return 150;
    }

    public double getRoundingRadius() {
        return this.roundingRadius.wo().doubleValue();
    }
}
'''

files["module/api/manager/ModuleManager.java"] = '''package com.alan.clients.module.api.manager;

import com.alan.clients.bridge.RiseModuleBridge;
import com.alan.clients.module.Module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModuleManager {
    public final Map<Class<? extends Module>, Module> modules = new HashMap<>();
    private final ArrayList<Module> list = new ArrayList<>();

    public ArrayList<Module> getAll() {
        return new ArrayList<>(this.list);
    }

    public <T extends Module> T c(Class<T> type) {
        return (T) this.modules.get(type);
    }

    public <T extends Module> T get(String name) {
        for (Module m : this.list) {
            String[] aliases = m.getAliases();
            if (aliases != null) {
                for (String a : aliases) {
                    if (a != null && a.replace(" ", "").equalsIgnoreCase(name.replace(" ", ""))) return (T) m;
                }
            }
        }
        return null;
    }

    public void register(Class<? extends Module> type, Module module) {
        this.modules.put(type, module);
        if (!this.list.contains(module)) this.list.add(module);
    }

    public void add(Module module) {
        if (!this.list.contains(module)) this.list.add(module);
    }

    public void remove(Module module) {
        this.list.remove(module);
        this.modules.values().remove(module);
    }

    public void updateArraylistCache() {
    }

    public void init() {
        RiseModuleBridge.build(this);
    }
}
'''

for rel, content in files.items():
    p = os.path.join(base, rel)
    os.makedirs(os.path.dirname(p), exist_ok=True)
    with open(p, "w", encoding="utf-8", newline="\n") as f:
        f.write(content)
    print("wrote", rel)
print("DONE")
