package myaupp.module;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * 外部模块包装器 —— 把宿主客户端的模块桥接进 myaupp 的 ModuleManager。
 * 通过函数式接口隔离，不直接引用宿主类。
 */
public class ExternalModule extends Module {
    private final BooleanSupplier enabledGetter;
    private final Consumer<Boolean> setter;

    public ExternalModule(String name, BooleanSupplier enabledGetter, Consumer<Boolean> setter) {
        super(name, false);
        this.enabledGetter = enabledGetter;
        this.setter = setter;
    }

    @Override
    public boolean isEnabled() {
        return enabledGetter.getAsBoolean();
    }

    @Override
    public void setEnabled(boolean enabled) {
        setter.accept(enabled);
    }

    @Override
    public boolean toggle() {
        boolean next = !isEnabled();
        setter.accept(next);
        return isEnabled();
    }
}
