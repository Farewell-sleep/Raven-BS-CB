package com.alan.clients.util.ime;

import java.util.ArrayList;
import java.util.List;

/**
 * 精简实现：原版为内置拼音词典，此处返回空结果。
 */
public class PinyinDictionary {
    public static PinyinDictionary um() {
        return new PinyinDictionary();
    }

    public List<String> s(String input, int limit) {
        return new ArrayList<>();
    }
}
