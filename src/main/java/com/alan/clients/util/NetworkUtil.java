package com.alan.clients.util;

import com.alan.clients.Client;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.google.gson.JsonArray;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class NetworkUtil implements InstanceAccess {
    static final boolean aEj = !NetworkUtil.class.desiredAssertionStatus();

    public NetworkUtil() {
    }

    public static String requestLine(String var0, String var1) {
        try {
            HttpURLConnection httpurlconnection = (HttpURLConnection)new URL(var0).openConnection();
            httpurlconnection.setRequestMethod(var1);
            return new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream())).readLine();
        } catch (Exception exception) {
            exception.printStackTrace();
            return "";
        }
    }

    public static String aY(String var0) {
        try {
            HttpsURLConnection httpsurlconnection = (HttpsURLConnection)new URL(var0).openConnection();
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(httpsurlconnection.getInputStream()));
            StringBuilder stringbuilder = new StringBuilder();

            String s1;
            while ((s1 = bufferedreader.readLine()) != null) {
                stringbuilder.append(s1);
            }

            return stringbuilder.toString();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    public static JsonArray aZ(String var0) {
        String s = aY(var0);
        if (!aEj && s == null) {
            throw new AssertionError();
        }
        return Client.a.A().fromJson(s, JsonArray.class);
    }
}
