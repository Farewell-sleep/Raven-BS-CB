package myau;

import myau.management.BlinkManager;
import myau.management.NotificationManager;
import myau.module.ModuleManager;

/**
 * 最小化 Myau 运行时 —— 仅承载 Scaffold 移植所需的全局管理器。
 */
public class Myau {
    public static ModuleManager moduleManager = new ModuleManager();
    public static BlinkManager blinkManager = new BlinkManager();
    public static NotificationManager notificationManager = new NotificationManager();

    public static void playSound() {
    }
}
