package com.alan.clients.ui.click.standard.components.value.impl;

import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.value.Mode;
import com.alan.clients.value.Value;
import com.alan.clients.value.impl.ListValue;
import com.alan.clients.value.impl.ModeValue;
import com.alan.clients.ui.click.standard.components.value.ValueComponent;
import com.alan.clients.ui.click.standard.UIColors;
import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.localization.Localization;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.font.FontWeight;

public class ListValueComponent extends ValueComponent {
    @Override
    public void draw(Vector2d position, int var2, int var3, float var4) {
        ListValue listvalue = (ListValue)this.value;
        this.position = position;
        String s = Localization.ce(this.value.getName()) + ":";
        FontManager.MAIN.a(16, FontWeight.REGULAR).a(s, this.position.x, this.position.y, UIColors.SECONDARY_TEXT.Z(this.ayD));
        FontManager.MAIN
            .a(16, FontWeight.REGULAR)
            .a(
                Localization.ce(listvalue instanceof ModeValue ? ((ModeValue)listvalue).wo().getName() : listvalue.wo().toString()),
                this.position.x + FontManager.MAIN.a(16, FontWeight.REGULAR).getStringWidth(s) + 2.0,
                this.position.y,
                UIColors.SECONDARY_TEXT.Z(this.ayD)
            );
    }

    public ListValueComponent(Value<?> var1) {
        super(var1);
    }

    @Override
    public boolean e(int var1, int var2, int var3) {
        if (this.position == null) {
            return false;
        }

        ListValue listvalue = (ListValue)this.value;
        boolean flag = var3 == 0;
        boolean flag1 = var3 == 1;
        if (GUIUtil.c(this.position.x, this.position.y - 3.5, this.getStandardClickGUI().width - 70, this.height, var1, var2)) {
            int i = listvalue.getModes().indexOf(listvalue.wo());
            Object object = null;
            if (flag) {
                if (listvalue.getModes().size() <= i + 1) {
                    object = listvalue.getModes().get(0);
                } else {
                    object = listvalue.getModes().get(i + 1);
                }
            } else if (flag1) {
                if (0 > i - 1) {
                    object = listvalue.getModes().get(listvalue.getModes().size() - 1);
                } else {
                    object = listvalue.getModes().get(i - 1);
                }
            }

            if (object != null) {
                if (this.getValue() instanceof ModeValue) {
                    ((ModeValue)listvalue).update((Mode<?>)object);
                } else {
                    listvalue.setValueAsObject(object);
                }
            }

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
