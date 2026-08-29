# -*- coding: utf-8 -*-
import os
import shutil

ime_dir = r"C:\Games\raven-bs\src\main\java\com\alan\clients\util\ime"
os.makedirs(ime_dir, exist_ok=True)

# 1) 复制原版 PinyinInputHandler 和 PinyinImeState
rise_ime = r"C:\Game\Rise-6.9.5-6.9.5\Rise-6.9.5-6.9.5\src\main\java\com\alan\clients\util\ime"
for name in ["PinyinInputHandler.java", "PinyinImeState.java"]:
    src = os.path.join(rise_ime, name)
    dst = os.path.join(ime_dir, name)
    if os.path.exists(src):
        with open(src, "r", encoding="utf-8", errors="replace") as f:
            content = f.read()
        with open(dst, "w", encoding="utf-8", newline="\n") as f:
            f.write(content)
        print("copied", name)
    else:
        print("MISSING", name)

# 2) stub PinyinDictionary
pd = '''package com.alan.clients.util.ime;

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
'''
with open(os.path.join(ime_dir, "PinyinDictionary.java"), "w", encoding="utf-8", newline="\n") as f:
    f.write(pd)
print("wrote PinyinDictionary stub")

# 3) stub PinyinUsageStore
pu = '''package com.alan.clients.util.ime;

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
'''
with open(os.path.join(ime_dir, "PinyinUsageStore.java"), "w", encoding="utf-8", newline="\n") as f:
    f.write(pu)
print("wrote PinyinUsageStore stub")
print("DONE")
