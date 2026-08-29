package com.alan.clients.util.localization;

import com.alan.clients.Client;
import com.alan.clients.util.localization.Locale;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class Localization {
    private static boolean populated = false;

    public Localization() {
    }

    public static String ce(String var0) {
        return a(var0, Client.a.getLocale());
    }

    public static String a(String var0, Locale locale) {
        if (!populated) {
            populate();
        }

        String s = locale.getStrings().get(var0);
        if (s == null) {
            s = Locale.EN_US.getStrings().get(var0);
        }

        return s == null ? var0 : s;
    }

    public static void populate() {
        for (Locale locale : Locale.values()) {
            ResourceLocation resourcelocation = new ResourceLocation("rise/text/" + locale.getFile() + ".properties");

            try (
                InputStream inputstream = Minecraft.getMinecraft().getResourceManager().getResource(resourcelocation).getInputStream();
                BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
            ) {
                locale.getStrings().clear();

                String s;
                while ((s = bufferedreader.readLine()) != null) {
                    String s1 = s.trim();
                    if (!s1.isEmpty() && !s1.startsWith("#")) {
                        int i = s1.indexOf(61);
                        if (i > 0) {
                            String s2 = s1.substring(0, i).trim();
                            String s3 = s1.substring(i + 1);
                            if (!s2.isEmpty()) {
                                if (s3.contains("\\u")) {
                                    s3 = s3.replace("\\u2022", "•");
                                }

                                locale.getStrings().put(s2, s3);
                            }
                        }
                    }
                }
            } catch (Exception exception) {
                System.out.println("Localization exception");
                exception.printStackTrace();
            }
        }

        populated = true;
    }

    static {
        populate();
        populated = false;
    }
}
