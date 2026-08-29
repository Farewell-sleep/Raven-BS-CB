# -*- coding: utf-8 -*-
import io
import os

BASE = r"C:\Games\raven-bs\src\main\java\com\alan\clients"


def add_method(p, method):
    path = os.path.join(BASE, p.replace("/", os.sep))
    c = io.open(path, encoding="utf-8", errors="replace").read()
    if method.strip().splitlines()[0] in c:
        print("already present", p)
        return
    idx = c.rfind("\n}")
    if idx == -1:
        print("!! no class end in", p)
        return
    c = c[:idx] + "\n" + method + "\n" + c[idx:]
    io.open(path, "w", encoding="utf-8", newline="\n").write(c)
    print("added method to", p)


repeat_char = (
    "    private static String repeatChar(char c, int count) {\n"
    "        StringBuilder stringbuilder = new StringBuilder(count);\n"
    "        for (int i = 0; i < count; i++) {\n"
    "            stringbuilder.append(c);\n"
    "        }\n"
    "        return stringbuilder.toString();\n"
    "    }"
)
add_method("util/gui/textbox/TextBox.java", repeat_char)

truncate = (
    "    private static String truncate(Font font, String text, float maxWidth) {\n"
    "        if (text == null) {\n"
    '            return "";\n'
    "        }\n"
    "        if (font.getStringWidth(text) <= maxWidth) {\n"
    "            return text;\n"
    "        }\n"
    "        String result = text;\n"
    "        while (!result.isEmpty() && font.getStringWidth(result) > maxWidth) {\n"
    "            result = result.substring(0, result.length() - 1);\n"
    "        }\n"
    "        return result;\n"
    "    }"
)
add_method("ui/click/standard/components/ConfigCard.java", truncate)
print("DONE")
