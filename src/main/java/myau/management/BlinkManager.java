package myau.management;

import myau.enums.BlinkModules;
import myau.util.PacketUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * BlinkManager 精简版 —— 保留 setBlinkState/offerPacket 等核心逻辑，
 * 去除对 myau PacketEvent/TickEvent 的依赖（Scaffold 仅使用 setBlinkState）。
 */
public class BlinkManager {
    public static Minecraft mc = Minecraft.getMinecraft();
    public BlinkModules blinkModule = BlinkModules.NONE;
    public boolean blinking = false;
    public Deque<Packet<?>> blinkedPackets = new ConcurrentLinkedDeque<>();

    public boolean offerPacket(Packet<?> packet) {
        if (this.blinkModule == BlinkModules.NONE || packet instanceof C00PacketKeepAlive || packet instanceof C01PacketChatMessage) {
            return false;
        } else if (this.blinkedPackets.isEmpty() && packet instanceof C0FPacketConfirmTransaction) {
            return false;
        } else {
            this.blinkedPackets.offer(packet);
            return true;
        }
    }

    public boolean setBlinkState(boolean state, BlinkModules module) {
        if (module == BlinkModules.NONE) {
            return false;
        }
        if (state) {
            this.blinkModule = module;
            this.blinking = true;
        } else {
            if (blinkModule != module) {
                return false;
            }
            this.blinking = false;
            if (Minecraft.getMinecraft().getNetHandler() != null && this.blinkedPackets.isEmpty()) {
                return true;
            }
            for (Packet<?> blinkedPacket : blinkedPackets) {
                PacketUtil.sendPacketNoEvent(blinkedPacket);
            }
            this.blinkedPackets.clear();
            this.blinkModule = BlinkModules.NONE;
        }
        return true;
    }

    public BlinkModules getBlinkingModule() {
        return this.blinkModule;
    }

    public long countMovement() {
        return this.blinkedPackets.stream().filter(packet -> packet instanceof C03PacketPlayer).count();
    }

    public boolean isBlinking() {
        return blinking;
    }
}
