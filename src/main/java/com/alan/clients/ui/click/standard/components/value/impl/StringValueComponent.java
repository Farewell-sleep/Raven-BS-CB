package com.alan.clients.ui.click.standard.components.value.impl;

import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.value.Value;
import com.alan.clients.value.impl.StringValue;
import com.alan.clients.ui.click.standard.components.value.ValueComponent;
import com.alan.clients.ui.click.standard.UIColors;
import com.alan.clients.util.gui.textbox.TextAlign;
import com.alan.clients.util.gui.textbox.TextBox;
import com.alan.clients.util.localization.Localization;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.font.FontWeight;
import java.awt.Color;

public class StringValueComponent extends ValueComponent {
    public final TextBox azo = new TextBox(new Vector2d(200.0, 200.0), FontManager.MAIN.a(16, FontWeight.REGULAR), Color.WHITE, TextAlign.LEFT, "", 20.0F);

    public StringValueComponent(Value<?> var1) {
        super(var1);
        StringValue stringvalue = (StringValue)var1;
        this.azo.bW(stringvalue.wo());
        this.azo.ar(stringvalue.wo().length());
    }

    @Override
    public void draw(Vector2d position, int var2, int var3, float var4) {
        this.position = position;
        StringValue stringvalue = (StringValue)this.value;
        this.height = 28.0;
        String s = Localization.ce(this.value.getName());
        FontManager.MAIN.a(16, FontWeight.REGULAR).a(s, this.position.x, this.position.y, UIColors.SECONDARY_TEXT.Z(this.ayD));
        this.azo.setColor(ColorUtil.withAlpha(this.azo.getColor(), this.ayD));
        this.position = new Vector2d(this.position.x, this.position.y + 14.0);
        this.azo.setPosition(this.position);
        this.azo.setWidth(230.5F);
        this.azo.draw();
        stringvalue.n(this.azo.getText());
    }

    @Override
    public boolean e(int var1, int var2, int var3) {
        if (this.position == null) {
            return false;
        }

        this.azo.click(var1, var2, var3);
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
        if (this.position != null) {
            this.azo.key(var1, var2);
        }
    }
}
