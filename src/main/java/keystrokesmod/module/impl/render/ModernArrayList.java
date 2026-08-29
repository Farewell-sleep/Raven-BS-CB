package keystrokesmod.module.impl.render;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.RenderUtils;
import keystrokesmod.utility.Theme;
import keystrokesmod.utility.Utils;
import keystrokesmod.utility.font.FontManager;
import keystrokesmod.utility.font.RavenFontRenderer;
import keystrokesmod.utility.shader.BlurUtils;
import keystrokesmod.utility.shader.RoundedUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * ModernArrayList - TargetHUD Modern style HUD module list.
 *
 * Style: Bloom glow + Blur frosted glass background, rounded corners,
 * theme gradient accent, smooth enable/disable animations.
 */
public class ModernArrayList extends Module {

    private static final String[] FONT_OPTIONS = FontManager.getHudFontOptions();
    private static final String[] SORT_MODES = new String[]{"Length", "Alphabetical"};

    private final SliderSetting font;
    private final SliderSetting fontSize;
    private final SliderSetting theme;
    private final SliderSetting sortMode;
    private final SliderSetting xOffset;
    private final SliderSetting yOffset;
    private final SliderSetting cornerRadius;
    private final ButtonSetting rightAlign;
    private final ButtonSetting textShadow;
    private final ButtonSetting showSuffix;

    private final List<ModuleEntry> entries = new ArrayList<>();
    private long lastFrame;

    private static class ModuleEntry {
        Module module;
        float animProgress = 0f;
        float targetWidth = 0f;
        float currentWidth = 0f;
        float animY = 0f;

        ModuleEntry(Module module) {
            this.module = module;
        }
    }

    public ModernArrayList() {
        super("ModernArrayList", category.render);
        this.registerSetting(font = new SliderSetting("Font", 0, FONT_OPTIONS));
        this.registerSetting(fontSize = new SliderSetting("Scale", 1.0, 0.5, 2.0, 0.1));
        this.registerSetting(theme = new SliderSetting("Theme", 0, Theme.themes));
        this.registerSetting(sortMode = new SliderSetting("Sort mode", 0, SORT_MODES));
        this.registerSetting(xOffset = new SliderSetting("X offset", 4, 0, 50, 1));
        this.registerSetting(yOffset = new SliderSetting("Y offset", 4, 0, 50, 1));
        this.registerSetting(cornerRadius = new SliderSetting("Corner radius", 6, 0, 12, 1));
        this.registerSetting(rightAlign = new ButtonSetting("Right align", true));
        this.registerSetting(textShadow = new ButtonSetting("Text shadow", true));
        this.registerSetting(showSuffix = new ButtonSetting("Show suffix", true));
    }

    @Override
    public void onEnable() {
        lastFrame = System.currentTimeMillis();
        entries.clear();
    }

    @Override
    public void onDisable() {
        entries.clear();
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.END || mc.thePlayer == null || mc.theWorld == null) return;

        long now = System.currentTimeMillis();
        float dt = Math.min(50, now - lastFrame) / 1000f;
        lastFrame = now;

        updateEntries(dt);
        drawArrayList();
    }

    private void updateEntries(float dt) {
        // Add new enabled modules
        for (Module m : Raven.moduleManager.getModules()) {
            if (m.isHidden() || m.moduleCategory() == category.profiles || m.moduleCategory() == category.scripts) continue;
            boolean found = false;
            for (ModuleEntry e : entries) {
                if (e.module == m) { found = true; break; }
            }
            if (!found && m.isEnabled()) {
                ModuleEntry entry = new ModuleEntry(m);
                entry.animProgress = 0f;
                entries.add(entry);
            }
        }

        RavenFontRenderer fr = getFontRenderer();
        float scale = (float) fontSize.getInput();
        List<ModuleEntry> toRemove = new ArrayList<>();

        for (ModuleEntry e : entries) {
            String name = e.module.getName();
            String suffix = e.module.getInfo();
            String display = (showSuffix.isToggled() && suffix != null && !suffix.isEmpty())
                    ? name + "  " + suffix : name;
            e.targetWidth = fr.getStringWidth(display) * scale + 20f;

            if (e.module.isEnabled()) {
                e.animProgress += (1f - e.animProgress) * Math.min(1, dt * 8f);
            } else {
                e.animProgress += (0f - e.animProgress) * Math.min(1, dt * 8f);
                if (e.animProgress < 0.02f) {
                    toRemove.add(e);
                }
            }
            e.currentWidth += (e.targetWidth - e.currentWidth) * Math.min(1, dt * 10f);
        }

        entries.removeAll(toRemove);

        // Sort
        if ((int) sortMode.getInput() == 0) {
            entries.sort(Comparator.comparingDouble((ModuleEntry e) -> -e.currentWidth));
        } else {
            entries.sort(Comparator.comparing((ModuleEntry e) -> e.module.getName()));
        }
    }

    private void drawArrayList() {
        ScaledResolution sr = new ScaledResolution(mc);
        RavenFontRenderer fr = getFontRenderer();
        float scale = (float) fontSize.getInput();

        int[] gradient = Theme.getGradients((int) theme.getInput());
        int gradLeft = gradient[0];
        int gradRight = gradient[1];

        float x = (float) xOffset.getInput();
        float y = (float) yOffset.getInput();
        float lineHeight = fr.getFontHeight() * scale + 8f;
        float radius = (float) cornerRadius.getInput();

        boolean right = rightAlign.isToggled();
        float baseX = right ? sr.getScaledWidth() - x : x;

        // Bloom pass
        BlurUtils.prepareBloom();
        for (int i = 0; i < entries.size(); i++) {
            ModuleEntry e = entries.get(i);
            if (e.animProgress < 0.02f) continue;

            float entryY = y + i * lineHeight;
            float w = e.currentWidth * e.animProgress;
            float h = lineHeight - 2f;
            float drawX = right ? baseX - w : baseX;

            // Bloom shape (slightly larger for glow)
            RoundedUtils.drawRound(drawX - 1, entryY - 1, w + 2, h + 2, radius, true,
                    new Color(0, 0, 0, (int)(180 * e.animProgress)));
        }
        BlurUtils.bloomEnd(3, 2.5f);

        // Blur pass
        BlurUtils.prepareBlur();
        for (int i = 0; i < entries.size(); i++) {
            ModuleEntry e = entries.get(i);
            if (e.animProgress < 0.02f) continue;

            float entryY = y + i * lineHeight;
            float w = e.currentWidth * e.animProgress;
            float h = lineHeight - 2f;
            float drawX = right ? baseX - w : baseX;

            RoundedUtils.drawRound(drawX, entryY, w, h, radius, true,
                    new Color(0, 0, 0, (int)(140 * e.animProgress)));
        }
        BlurUtils.blurEnd(2, 3f);

        // Content pass
        for (int i = 0; i < entries.size(); i++) {
            ModuleEntry e = entries.get(i);
            if (e.animProgress < 0.02f) continue;

            float entryY = y + i * lineHeight;
            float w = e.currentWidth * e.animProgress;
            float h = lineHeight - 2f;
            float drawX = right ? baseX - w : baseX;

            int alpha = (int) (255 * e.animProgress);

            // Background outline (gradient)
            RenderUtils.drawRoundedGradientOutlinedRectangle(
                    drawX, entryY, drawX + w, entryY + h, radius,
                    Utils.mergeAlpha(Color.black.getRGB(), (int)(100 * e.animProgress)),
                    Utils.mergeAlpha(gradLeft, alpha),
                    Utils.mergeAlpha(gradRight, alpha));

            // Accent bar on the edge
            if (right) {
                RenderUtils.drawRoundedRectangle(drawX + w - 2f, entryY + 3f, drawX + w, entryY + h - 3f, 1f,
                        Utils.mergeAlpha(gradRight, alpha));
            } else {
                RenderUtils.drawRoundedRectangle(drawX, entryY + 3f, drawX + 2f, entryY + h - 3f, 1f,
                        Utils.mergeAlpha(gradLeft, alpha));
            }

            // Text
            String name = e.module.getName();
            String suffix = e.module.getInfo();
            String display = (showSuffix.isToggled() && suffix != null && !suffix.isEmpty())
                    ? name + "  " + suffix : name;

            float textX = right ? drawX + w - fr.getStringWidth(display) * scale - 8f : drawX + 8f;
            float textY = entryY + (h - fr.getFontHeight() * scale) / 2f;

            int textColor = Utils.mergeAlpha(new Color(225, 225, 230, 255).getRGB(), alpha);

            net.minecraft.client.renderer.GlStateManager.pushMatrix();
            net.minecraft.client.renderer.GlStateManager.translate(textX, textY, 0);
            net.minecraft.client.renderer.GlStateManager.scale(scale, scale, 1);
            if (textShadow.isToggled()) {
                fr.drawStringWithShadow(display, 0, 0, textColor);
            } else {
                fr.drawString(display, 0, 0, textColor);
            }
            net.minecraft.client.renderer.GlStateManager.popMatrix();
        }
    }

    private RavenFontRenderer getFontRenderer() {
        int idx = (int) Math.max(0, Math.min(font.getOptions().length - 1, font.getInput()));
        return FontManager.getHudRenderer(font.getOptions()[idx], 1f);
    }
}
