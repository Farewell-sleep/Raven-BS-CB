package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.RenderUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Notification extends Module {

    private static final Minecraft mc = Minecraft.getMinecraft();

    // Animation timing (ms)
    private static final long SLIDE_IN_MS = 250L;
    private static final long STAY_MS = 1750L;
    private static final long SLIDE_OUT_MS = 500L;
    private static final long TOTAL_MS = SLIDE_IN_MS + STAY_MS + SLIDE_OUT_MS;

    // Layout
    private static final int NOTIF_HEIGHT = 24;
    private static final int NOTIF_PADDING = 6;
    private static final int STACK_GAP = 4;
    private static final int EDGE_MARGIN = 8;

    // Colors (ARGB)
    private static final int BG_ENABLED = 0x4D1AFF4A;   // 30% light green
    private static final int BG_DISABLED = 0x4DFF1A1A;  // 30% red
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int ICON_ENABLED = 0xFF33FF66;
    private static final int ICON_DISABLED = 0xFFFF5555;

    public final SliderSetting scale;

    private final Map<Module, Boolean> lastStates = new HashMap<>();
    private final List<NotificationItem> notifications = new ArrayList<>();

    public Notification() {
        super("Notification", category.render);
        this.registerSetting(scale = new SliderSetting("Scale", 1.0, 0.5, 2.0, 0.1));
    }

    @Override
    public void onEnable() {
        lastStates.clear();
        notifications.clear();
        // Snapshot current states so we don't spam notifications on enable
        for (Module m : ModuleManager.modules) {
            if (shouldTrack(m)) {
                lastStates.put(m, m.isEnabled());
            }
        }
    }

    @Override
    public void onDisable() {
        lastStates.clear();
        notifications.clear();
    }

    private boolean shouldTrack(Module m) {
        if (m == this) return false;
        if (m.isHidden()) return false;
        if (m.alwaysOn) return false;
        if (m.script != null) return false;
        return true;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        if (!Utils.nullCheck()) return;
        pollModuleStates();
    }

    private void pollModuleStates() {
        for (Module m : ModuleManager.modules) {
            if (!shouldTrack(m)) continue;
            Boolean prev = lastStates.get(m);
            boolean now = m.isEnabled();
            if (prev == null) {
                lastStates.put(m, now);
                continue;
            }
            if (prev != now) {
                lastStates.put(m, now);
                pushNotification(m.getName(), now);
            }
        }
    }

    private void pushNotification(String moduleName, boolean enabled) {
        notifications.add(new NotificationItem(moduleName, enabled, System.currentTimeMillis()));
        // Cap queue to prevent memory buildup
        while (notifications.size() > 12) {
            notifications.remove(0);
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        if (!Utils.nullCheck()) return;
        if (mc.currentScreen != null && mc.gameSettings.showDebugInfo) return;
        renderNotifications();
    }

    private void renderNotifications() {
        if (notifications.isEmpty()) return;

        ScaledResolution res = new ScaledResolution(mc);
        int screenW = res.getScaledWidth();
        int screenH = res.getScaledHeight();
        float s = (float) scale.getInput();

        long now = System.currentTimeMillis();

        // Remove expired
        Iterator<NotificationItem> it = notifications.iterator();
        while (it.hasNext()) {
            NotificationItem item = it.next();
            if (now - item.createdAt >= TOTAL_MS) {
                it.remove();
            }
        }
        if (notifications.isEmpty()) return;

        GL11.glPushMatrix();
        GL11.glScalef(s, s, s);

        // Render from bottom of screen upward; newest at bottom
        float baseY = screenH / s - EDGE_MARGIN - NOTIF_HEIGHT;

        for (int i = notifications.size() - 1; i >= 0; i--) {
            NotificationItem item = notifications.get(i);
            drawNotification(item, screenW / s, baseY);
            baseY -= (NOTIF_HEIGHT + STACK_GAP);
        }

        GL11.glPopMatrix();
    }

    private void drawNotification(NotificationItem item, float screenW, float y) {
        long elapsed = System.currentTimeMillis() - item.createdAt;

        // Compute horizontal offset: 0 = fully visible at right edge, positive = off-screen right
        float offsetX;
        if (elapsed < SLIDE_IN_MS) {
            // Slide in: from right (screenW) to 0
            float t = (float) elapsed / SLIDE_IN_MS;
            offsetX = (1f - easeOutCubic(t)) * (screenW + 10f);
        } else if (elapsed < SLIDE_IN_MS + STAY_MS) {
            offsetX = 0f;
        } else if (elapsed < TOTAL_MS) {
            // Slide out: from 0 to right (screenW)
            float t = (float) (elapsed - SLIDE_IN_MS - STAY_MS) / SLIDE_OUT_MS;
            offsetX = easeInCubic(t) * (screenW + 10f);
        } else {
            return;
        }

        float x = screenW - EDGE_MARGIN - getNotifWidth(item) + offsetX;
        float right = x + getNotifWidth(item);
        float bottom = y + NOTIF_HEIGHT;

        int bg = item.enabled ? BG_ENABLED : BG_DISABLED;
        int iconColor = item.enabled ? ICON_ENABLED : ICON_DISABLED;

        // Background
        RenderUtils.drawRect(x, y, right, bottom, bg);

        // Left accent bar
        RenderUtils.drawRect(x, y, x + 2f, bottom, iconColor);

        // Icon (check / cross) drawn with lines
        float iconX = x + NOTIF_PADDING + 6f;
        float iconY = y + NOTIF_HEIGHT / 2f;
        drawIcon(iconX, iconY, item.enabled, iconColor);

        // Text
        String text = item.moduleName + (item.enabled ? " enabled" : " disabled");
        float textX = x + NOTIF_PADDING + 18f;
        float textY = y + (NOTIF_HEIGHT - mc.fontRendererObj.FONT_HEIGHT) / 2f + 1f;
        mc.fontRendererObj.drawString(text, textX, textY, TEXT_COLOR, true);
    }

    private float getNotifWidth(NotificationItem item) {
        String text = item.moduleName + (item.enabled ? " enabled" : " disabled");
        return NOTIF_PADDING * 2 + 18f + mc.fontRendererObj.getStringWidth(text);
    }

    private void drawIcon(float cx, float cy, boolean enabled, int color) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(2.0f);

        float a = ((color >> 24) & 0xFF) / 255f;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        GL11.glColor4f(r, g, b, a);

        if (enabled) {
            // Check mark: \/ shape
            GL11.glBegin(GL11.GL_LINES);
            GL11.glVertex2f(cx - 4f, cy);
            GL11.glVertex2f(cx - 1f, cy + 3f);
            GL11.glVertex2f(cx - 1f, cy + 3f);
            GL11.glVertex2f(cx + 4f, cy - 4f);
            GL11.glEnd();
        } else {
            // Cross mark: X
            GL11.glBegin(GL11.GL_LINES);
            GL11.glVertex2f(cx - 3.5f, cy - 3.5f);
            GL11.glVertex2f(cx + 3.5f, cy + 3.5f);
            GL11.glVertex2f(cx + 3.5f, cy - 3.5f);
            GL11.glVertex2f(cx - 3.5f, cy + 3.5f);
            GL11.glEnd();
        }

        GL11.glLineWidth(1.0f);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    private static float easeOutCubic(float t) {
        return 1f - (float) Math.pow(1f - t, 3);
    }

    private static float easeInCubic(float t) {
        return t * t * t;
    }

    private static class NotificationItem {
        final String moduleName;
        final boolean enabled;
        final long createdAt;

        NotificationItem(String moduleName, boolean enabled, long createdAt) {
            this.moduleName = moduleName;
            this.enabled = enabled;
            this.createdAt = createdAt;
        }
    }
}
