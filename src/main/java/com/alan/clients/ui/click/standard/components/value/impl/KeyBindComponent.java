package com.alan.clients.ui.click.standard.components.value.impl;

import com.alan.clients.ui.click.standard.RiseClickGUI;
import com.alan.clients.ui.click.standard.components.value.ValueComponent;
import com.alan.clients.ui.click.standard.UIColors;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.font.FontWeight;
import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.value.Value;
import keystrokesmod.module.Module;
import org.lwjgl.input.Keyboard;

import java.awt.Color;

/**
 * 绑定按键组件 —— 显示模块当前按键，点击后进入"按下任意键绑定"模式（RiseClickGUI 处理）。
 * 按键逻辑与 raven-bs 原生一致：keycode < 1000 为键盘键，>= 1000 为鼠标键（1000 + button）。
 */
public class KeyBindComponent extends ValueComponent {
    private final Module module;

    public KeyBindComponent(Module module) {
        super(null);
        this.module = module;
    }

    public static String getKeyName(int keycode) {
        if (keycode == 0) {
            return "None";
        }
        if (keycode >= 1000) {
            return "Mouse " + (keycode - 1000);
        }
        String name = Keyboard.getKeyName(keycode);
        return name == null || name.isEmpty() ? "Key " + keycode : name;
    }

    @Override
    public void draw(Vector2d position, int var2, int var3, float var4) {
        this.position = position;
        String label = "Bind Key";
        FontManager.MAIN.a(16, FontWeight.REGULAR).a(label, position.x, position.y, UIColors.SECONDARY_TEXT.Z(this.ayD));
        String keyName = getKeyName(this.module == null ? 0 : this.module.getKeycode());
        boolean hovered = this.position != null && GUIUtil.c(position.x, position.y - 3.5, this.getStandardClickGUI().width - 70, this.height, var3, var4);
        int col = hovered ? this.rz().rA().getRGB() : UIColors.SECONDARY_TEXT.Z(this.ayD);
        FontManager.MAIN.a(16, FontWeight.REGULAR).a(keyName, position.x + 90.0, position.y, col);
    }

    @Override
    public boolean e(int var1, int var2, int var3) {
        if (this.position == null || this.module == null) {
            return false;
        }
        if (GUIUtil.c(this.position.x, this.position.y - 3.5, this.getStandardClickGUI().width - 70, this.height, var1, var2)) {
            RiseClickGUI gui = this.getStandardClickGUI();
            gui.startKeyBind(this.module);
            return true;
        }
        return false;
    }

    @Override
    public void pz() {
    }

    @Override
    public void released() {
    }

    @Override
    public void key(char var1, int var2) {
    }
}
