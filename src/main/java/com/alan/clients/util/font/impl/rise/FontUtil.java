package com.alan.clients.util.font.impl.rise;

import com.alan.clients.util.interfaces.InstanceAccess;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class FontUtil {
    private static final IResourceManager RESOURCE_MANAGER = InstanceAccess.aEg.getResourceManager();

    public FontUtil() {
    }

    public static Font p(String var0, int var1) {
        try {
            return Font.createFont(0, RESOURCE_MANAGER.getResource(new ResourceLocation(var0)).getInputStream()).deriveFont((float)var1);
        } catch (FontFormatException | IOException fontformatexception) {
            return null;
        }
    }

    public static Font q(String var0, int var1) {
        try {
            return Font.createFont(0, new File(var0)).deriveFont((float)var1);
        } catch (FontFormatException | IOException fontformatexception) {
            return null;
        }
    }
}
