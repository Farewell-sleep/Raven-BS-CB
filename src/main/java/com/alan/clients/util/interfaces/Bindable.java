package com.alan.clients.util.interfaces;

public interface Bindable {
    int getKey();

    void setKey(int var1);

    void onKey();

    String[] getAliases();

    String getName();
}
