package com.alan.clients.util.shader;

import com.alan.clients.util.interfaces.InstanceAccess;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class ShaderUtil implements InstanceAccess {
    private static final IResourceManager RESOURCE_MANAGER = aEg.getResourceManager();

    public ShaderUtil() {
    }

    public static int createShader(String var0, String var1) {
        String s = getShaderResource(var0);
        String s1 = getShaderResource(var1);
        if (var0 != null && var1 != null) {
            int i = GL20.glCreateShader(35632);
            int j = GL20.glCreateShader(35633);
            GL20.glShaderSource(i, s);
            GL20.glShaderSource(j, s1);
            GL20.glCompileShader(i);
            GL20.glCompileShader(j);
            if (!compileShader(i)) {
                return -1;
            }

            if (!compileShader(j)) {
                return -1;
            }

            int k = GL20.glCreateProgram();
            GL20.glAttachShader(k, i);
            GL20.glAttachShader(k, j);
            GL20.glValidateProgram(k);
            GL20.glLinkProgram(k);
            GL20.glDeleteShader(i);
            GL20.glDeleteShader(j);
            return k;
        }
        System.out.println("An error occurred whilst creating shader");
        System.out.println("Fragment: " + s == null);
        System.out.println("Vertex: " + s1 == null);
        return -1;
    }

    private static boolean compileShader(int var0) {
        boolean flag = GL20.glGetShaderi(var0, 35713) == 1;
        if (flag) {
            return true;
        }

        String s = GL20.glGetShaderInfoLog(var0, 8192);
        System.out.println("\nError while compiling shader: ");
        System.out.println("-------------------------------");
        System.out.println(s);
        return false;
    }

    public static String getShaderResource(String var0) {
        try {
            InputStream inputstream = RESOURCE_MANAGER.getResource(new ResourceLocation("rise/shader/" + var0)).getInputStream();
            InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
            BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
            String s = "";

            String s1;
            try {
                while ((s1 = bufferedreader.readLine()) != null) {
                    s = s + s1 + System.lineSeparator();
                }
            } catch (IOException ioexception) {
            }

            return s;
        } catch (Exception ioexception1) {
            System.out.println("An error occurred while getting a shader resource");
            ioexception1.printStackTrace();
            return null;
        }
    }

    public static void c(ScaledResolution resolution) {
        if (!Minecraft.getMinecraft().gameSettings.fboEnable) {
            float f = (float)resolution.getScaledWidth_double();
            float f1 = (float)resolution.getScaledHeight_double();
            GL11.glBegin(7);
            GL11.glTexCoord2f(0.0F, 1.0F);
            GL11.glVertex2f(0.0F, 0.0F);
            GL11.glTexCoord2f(0.0F, 0.0F);
            GL11.glVertex2f(0.0F, f1);
            GL11.glTexCoord2f(1.0F, 0.0F);
            GL11.glVertex2f(f, f1);
            GL11.glTexCoord2f(1.0F, 1.0F);
            GL11.glVertex2f(f, 0.0F);
            GL11.glEnd();
        }
    }
}
