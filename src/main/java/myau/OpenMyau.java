package myau;

import myau.management.BlinkManager;
import myau.management.FriendManager;
import myau.management.PlayerStateManager;
import myau.management.RotationManager;
import myau.management.TargetManager;
import myau.module.ModuleManager;

/**
 * 最小化 OpenMyau 入口 —— 仅提供 KillAura 移植所需的静态管理器引用。
 * 不初始化完整客户端，不加载配置，不注册命令。
 */
public class OpenMyau {
    public static String clientName = "&7[&cO&6p&ee&an&bM&9y&da&cu&b++&7]&r ";
    public static String version = "dev";
    public static RotationManager rotationManager = new RotationManager();
    public static BlinkManager blinkManager = new BlinkManager();
    public static PlayerStateManager playerStateManager = new PlayerStateManager();
    public static FriendManager friendManager = new FriendManager();
    public static TargetManager targetManager = new TargetManager();
    public static ModuleManager moduleManager = new ModuleManager();
}
