package com.alan.clients.ui.click.standard.components.value.impl;

import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.value.Value;
import com.alan.clients.ui.click.standard.components.value.ValueComponent;
import net.minecraft.util.ResourceLocation;

public class PositionValueComponent extends ValueComponent {
    private final ResourceLocation image = new ResourceLocation("rise/icons/click.png");

    public PositionValueComponent(Value<?> var1) {
        super(var1);
    }

    @Override
    public void draw(Vector2d vector2d, int var2, int var3, float var4) {
        this.height = 0.0;
    }

    @Override
    public boolean e(int var1, int var2, int var3) {
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
