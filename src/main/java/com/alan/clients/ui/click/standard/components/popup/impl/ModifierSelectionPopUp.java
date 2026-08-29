package com.alan.clients.ui.click.standard.components.popup.impl;

import com.alan.clients.ui.click.standard.components.popup.PopUp;
import com.alan.clients.util.vector.Vector2f;

public class ModifierSelectionPopUp extends PopUp {
    public ModifierSelectionPopUp() {
    }

    @Override
    public void draw() {
        this.scale = new Vector2f(200.0F, 120.0F);
        super.draw();
    }
}
