package myaupp;

import myaupp.module.ModuleManager;
import myaupp.property.PropertyManager;

/**
 * OpenMyau 最小存根 —— 仅承载 Telly 移植所需的静态管理器。
 */
public class OpenMyau {
    public static ModuleManager moduleManager = new ModuleManager();
    public static PropertyManager propertyManager = new PropertyManager();
}
