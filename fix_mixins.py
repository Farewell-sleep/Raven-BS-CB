# -*- coding: utf-8 -*-
import io
import re
import os

BASE = r"C:\Games\raven-bs\src\main\java\keystrokesmod\mixin\impl"

FILES = [
    ("client", "MixinGameSettings.java"),
    ("client", "MixinMovementInputFromOptions.java"),
    ("entity", "MixinEntity.java"),
    ("entity", "MixinEntityLivingBase.java"),
    ("entity", "MixinEntityPlayer.java"),
    ("entity", "MixinEntityPlayerSP.java"),
    ("world", "MixinBlock.java"),
]

JAVADOC = "    /**\n     * @author Raven BS\n     */\n"

for sub, fname in FILES:
    p = os.path.join(BASE, sub, fname)
    c = io.open(p, encoding="utf-8", errors="replace").read()
    orig = c
    # 1) 给缺失 javadoc 的 @Overwrite 方法补 javadoc
    lines = c.split("\n")
    out = []
    n_added = 0
    i = 0
    while i < len(lines):
        line = lines[i]
        stripped = line.strip()
        if stripped.startswith("@Overwrite"):
            # 检查前一行是否已经是 javadoc 结束
            has_javadoc = i > 0 and lines[i - 1].strip().endswith("*/")
            if not has_javadoc:
                indent = line[: len(line) - len(line.lstrip())]
                out.append(indent + "/**")
                out.append(indent + " * @author Raven BS")
                out.append(indent + " */")
                n_added += 1
        out.append(line)
        i += 1
    c = "\n".join(out)

    # 2) MixinEntityLivingBase 的 @Inject 方法名修复
    c = c.replace(
        '@Inject(method = { "updateDistance", "func_110146_f" }, at = @At("HEAD"), cancellable = true)',
        '@Inject(method = "updateDistance", at = @At("HEAD"), cancellable = true)',
    )

    if c != orig:
        io.open(p, "w", encoding="utf-8", newline="\n").write(c)
        print("PATCHED %s javadoc=%d" % (fname, n_added))
    else:
        print("no change %s" % fname)
print("DONE")
