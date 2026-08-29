package com.alan.clients.util.ime;

/**
 * 精简实现：记录拼音使用频率，此处为空实现。
 */
public class PinyinUsageStore {
    private static final PinyinUsageStore INSTANCE = new PinyinUsageStore();

    public static PinyinUsageStore uC() {
        return INSTANCE;
    }

    public synchronized void D(String pinyin, String word) {
    }
}
