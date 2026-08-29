package com.alan.clients.ui.click.standard.components.category;

import com.alan.clients.Client;
import com.alan.clients.module.api.Category;
import com.alan.clients.ui.click.standard.RiseClickGUI;
import com.alan.clients.ui.click.standard.components.category.CategoryComponent;
import com.alan.clients.util.animation.Animation;
import com.alan.clients.util.animation.Easing;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.ui.click.standard.UIColors;
import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.font.FontWeight;
import java.awt.Color;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Generated;
import org.lwjgl.input.Mouse;

public final class SidebarCategory implements InstanceAccess {
    private final List<CategoryComponent> categories;
    public double aym = 100.0;
    private double axT;
    private double ayn;
    private boolean ayo;
    private long lastTime = 0L;
    private Animation animation = new Animation(Easing.EASE_OUT_EXPO, 300L);
    private Animation ayp = new Animation(Easing.LINEAR, 300L);

    public SidebarCategory() {
        this.categories = Arrays.stream(Category.values()).map(CategoryComponent::new).collect(Collectors.toList());
    }

    public void pF() {
        RiseClickGUI riseclickgui = Client.a.getStandardClickGUI();
        Color color = UIColors.SECONDARY.Y((int)this.axT);
        this.animation.setDuration(this.ayo ? 700L : 2000L);
        this.animation.Q(this.ayo ? 0.0 : -this.aym / 1.5);
        RenderUtil.a(
            riseclickgui.axI.x,
            riseclickgui.axI.y,
            this.aym + this.animation.getValue(),
            riseclickgui.position.y,
            this.getStandardClickGUI().getRound(),
            color,
            true,
            false,
            false,
            true
        );
        this.ayp.setDuration(1000L);
        this.ayp.Q(riseclickgui.getScreen().qa() ? 255.0 : 0.0);
        RenderUtil.horizontalCenteredGradient(
            riseclickgui.axI.x + this.aym + this.animation.getValue(),
            riseclickgui.axI.y,
            30.0,
            riseclickgui.position.y,
            ColorUtil.withAlpha(Color.BLACK, (int)Math.min(this.ayp.getValue(), this.axT / 7.0)),
            new Color(0, 0, 0, 0)
        );
    }

    public void renderSidebar(float var1, float var2) {
        RiseClickGUI riseclickgui = Client.a.getStandardClickGUI();
        long now = System.currentTimeMillis();
        if (this.lastTime == 0L) {
            this.lastTime = now;
        }

        boolean flag = riseclickgui.axK.qa();
        if (this.ayo = (!Mouse.isButtonDown(0) || this.ayo)
                && GUIUtil.c(riseclickgui.axI.x - 200.0F, riseclickgui.axI.y, this.ayo ? 310.0 : 210.0, riseclickgui.position.y, var1, var2)
            || !flag) {
            this.axT = Math.min(this.axT + (now - this.lastTime) * 2L, 255.0);
        } else {
            this.axT = Math.max(this.axT - (float)(now - this.lastTime) * 1.5F, 0.0);
        }

        if (GUIUtil.c(riseclickgui.axI.x, riseclickgui.axI.y, this.ayn > 0.0 ? 70.0 : 10.0, riseclickgui.position.y, var1, var2) && flag) {
            this.ayn = Math.min(this.ayn + (now - this.lastTime) * 2L, 255.0);
        } else {
            this.ayn = Math.max(this.ayn - (now - this.lastTime), 0.0);
        }

        this.lastTime = now;
        double d0 = 10.0;

        for (CategoryComponent categoryComponent : this.categories) {
            categoryComponent.render(d0 += 19.5, this.aym + this.animation.getValue(), (int)this.axT, riseclickgui.axK);
        }

        float f = (float)(riseclickgui.axI.getX() + 9.0F + this.animation.getValue());
        float f1 = riseclickgui.axI.getY() + (24.75F - FontManager.MAIN.a(42, FontWeight.REGULAR).height() / 2.0F);
        FontManager.MAIN.a(32, FontWeight.REGULAR).a(Client.b, f + 5.0F, f1 + 2.0F, ColorUtil.withAlpha(Color.WHITE, (int)this.axT).hashCode());
        FontManager.MAIN
            .a(16, FontWeight.REGULAR)
            .a("16.1", f + 5.0F + FontManager.MAIN.a(32, FontWeight.REGULAR).getStringWidth(Client.b), f1, ColorUtil.withAlpha(this.rz().rA(), (int)Math.min(this.axT, 200.0)).getRGB());
    }

    public void preRenderClickGUI() {
        Iterator iterator = this.categories.iterator();

        while (iterator.hasNext()) {
            ((CategoryComponent)iterator.next()).bloom(this.axT);
        }
    }

    public void clickSidebar(float var1, float var2, int var3) {
        if (this.axT > 0.0) {
            Iterator iterator = this.categories.iterator();

            while (iterator.hasNext()) {
                ((CategoryComponent)iterator.next()).click(var1, var2, var3);
            }
        }
    }

    public void pE() {
        if (this.axT > 0.0) {
            Iterator iterator = this.categories.iterator();

            while (iterator.hasNext()) {
                ((CategoryComponent)iterator.next()).release();
            }
        }
    }

    @Generated
    public boolean pG() {
        return this.ayo;
    }
}
