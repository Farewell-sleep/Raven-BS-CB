package myau.module;

import java.util.HashMap;
import java.util.Map;

/**
 * 最小化 ModuleManager —— 注册 Scaffold 移植所需的模块存根。
 */
public class ModuleManager {
    public final Map<Class<?>, Module> modules = new HashMap<>();

    public ModuleManager() {
        modules.put(myau.module.modules.BedNuker.class, new myau.module.modules.BedNuker());
        modules.put(myau.module.modules.LongJump.class, new myau.module.modules.LongJump());
        modules.put(myau.module.modules.HUD.class, new myau.module.modules.HUD());
        modules.put(myau.module.modules.KeepSprint.class, new myau.module.modules.KeepSprint());
        modules.put(myau.module.modules.TargetStrafe.class, new myau.module.modules.TargetStrafe());
        modules.put(myau.module.modules.AutoHeal.class, new myau.module.modules.AutoHeal());
        modules.put(myau.module.modules.AutoBlockIn.class, new myau.module.modules.AutoBlockIn());
        modules.put(myau.module.modules.Scaffold.class, new myau.module.modules.Scaffold());
        modules.put(myau.module.modules.NoSlow.class, new myau.module.modules.NoSlow());
        modules.put(myau.module.modules.KillAura.class, new myau.module.modules.KillAura());
        modules.put(myau.module.modules.AimAssist.class, new myau.module.modules.AimAssist());
        modules.put(myau.module.modules.AutoClicker.class, new myau.module.modules.AutoClicker());
        modules.put(myau.module.modules.Reach.class, new myau.module.modules.Reach());
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T get(Class<T> clazz) {
        return (T) modules.get(clazz);
    }

    public void playSound() {
    }
}
