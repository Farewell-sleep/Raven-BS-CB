# -*- coding: utf-8 -*-
import io
import os

BASE = r"C:\Games\raven-bs\src\main\java\com\alan\clients"


def path(p):
    return os.path.join(BASE, p.replace("/", os.sep))


def read(p):
    return io.open(path(p), encoding="utf-8", errors="replace").read()


def write(p, content):
    io.open(path(p), "w", encoding="utf-8", newline="\n").write(content)


def rep(p, old, new, count=None, must=True):
    c = read(p)
    n = c.count(old)
    if n == 0:
        if must:
            print("!! NOT FOUND in %s: %r" % (p, old[:80]))
        return
    if count is not None and n > count:
        print("!! MULTIPLE(%d) in %s: %r" % (n, p, old[:80]))
    write(p, c.replace(old, new))
    print("OK %s: %dx %r" % (p, n, old[:70]))


def add_import(p, imp):
    c = read(p)
    if imp in c:
        return
    idx = c.index("\nimport ")  # 在第一个 import 前插入
    write(p, c[:idx] + "\n" + imp + c[idx:])
    print("IMPORT %s: %s" % (p, imp.strip()))


# ---------- 1. RiseClickGUI ----------
P = "ui/click/standard/RiseClickGUI.java"
rep(P, "import com.alan.clients.util.interfaces.ExecutorAccess;",
       "import com.alan.clients.util.interfaces.ExecutorAccess;\nimport com.alan.clients.util.interfaces.InstanceAccess;\nimport com.alan.clients.util.MCPCompat;")
rep(P, "public class RiseClickGUI extends GuiScreen implements ExecutorAccess {",
       "public class RiseClickGUI extends GuiScreen implements InstanceAccess, ExecutorAccess {")
rep(P, "ScaledResolution scaledresolution = aEg.jY;",
       "ScaledResolution scaledresolution = MCPCompat.scaledResolution();")
rep(P, "float f = minecraft.getTimer().bWm;",
       "float f = MCPCompat.renderPartialTicks();")

# ---------- 2. Themes ----------
rep("ui/theme/Themes.java", "EnumChatFormatting.NONE",
    "EnumChatFormatting.RESET", count=4)

# ---------- 3. TextBox ----------
P = "util/gui/textbox/TextBox.java"
rep(P, '"*".repeat(this.text.length())',
       "this.repeatChar('*', this.text.length())")
old_tb = "            (this.lq == Minecraft.getMinecraft().fontRendererObj ? FontManager.MAIN.a(18, FontWeight.REGULAR) : this.lq)\n                .a("
new_tb = "            this.lq\n                .a("
rep(P, old_tb, new_tb)

# ---------- 4. ClickGUI ----------
rep("module/impl/render/ClickGUI.java", "aEg.Av();", "aEg.setIngameFocus();", count=2)

# ---------- 5. ConfigCard ----------
P = "ui/click/standard/components/ConfigCard.java"
rep(P, "StringUtils.b(this.aAd, var2, 86.450005F - 20)",
       "truncate(this.aAd, var2, 86.450005F - 20)")
rep(P, "StringUtils.b(this.aAd, s, 86.450005F - 20)",
       "truncate(this.aAd, s, 86.450005F - 20)")
rep(P, "import net.minecraft.util.StringUtils;\n", "")

# ---------- 6. GuiIngameCache ----------
P = "ui/ingame/GuiIngameCache.java"
rep(P, "if (aEg.ingameGUI.showCrosshair()) {", "if (MCPCompat.showCrosshair()) {")
rep(P, "            GlStateManager.bKk = true;\n", "")
rep(P, "            GlStateManager.bKk = false;\n", "")
add_import(P, "import com.alan.clients.util.MCPCompat;")

# ---------- 7. Mouse ----------
P = "util/dragging/Mouse.java"
rep(P, "ScaledResolution scaledresolution = aEg.jY;",
       "ScaledResolution scaledresolution = MCPCompat.scaledResolution();")
add_import(P, "import com.alan.clients.util.MCPCompat;")

# ---------- 8. FontManager ----------
P = "util/font/FontManager.java"
rep(P, 'MINECRAFT("Minecraft", () -> Minecraft.getMinecraft().fontRendererObj),',
       'MINECRAFT("Minecraft", () -> new MinecraftFont(Minecraft.getMinecraft().fontRendererObj)),')
add_import(P, "import com.alan.clients.util.font.impl.minecraft.MinecraftFont;")

# ---------- 9. FontRenderer ----------
rep("util/font/impl/rise/FontRenderer.java",
    "return Minecraft.getMinecraft().fontRendererObj.b(var1, var2, var4, var6, var7);",
    "return Minecraft.getMinecraft().fontRendererObj.drawString(var1, (float)var2, (float)var4, var6, var7);")

# ---------- 10. MouseUtil ----------
P = "util/MouseUtil.java"
rep(P, "int i = aEg.jY.getScaledWidth();", "int i = MCPCompat.scaledResolution().getScaledWidth();")
rep(P, "int j = aEg.jY.getScaledHeight();", "int j = MCPCompat.scaledResolution().getScaledHeight();")

# ---------- 11. RenderUtil ----------
P = "util/render/RenderUtil.java"
rep(P, "return new Vector3d(-RenderManager.bUO, -RenderManager.bUP, -RenderManager.bUQ);",
       "return new Vector3d(-MCPCompat.renderPosX(), -MCPCompat.renderPosY(), -MCPCompat.renderPosZ());")
rep(P, "aEg.thePlayer.getArmSwingAnimationEnd()",
       "MCPCompat.armSwingAnimationEnd(aEg.thePlayer)")
rep(P, "aEg.getTextureManager().dz(var0);", "GlStateManager.bindTexture(var0);")
rep(P, "aEg.getRenderItem().b(stack, var4, var0, var2);",
       "GlStateManager.translate((float)var0, (float)var2, (float)var4);\n            aEg.getRenderItem().renderItemIntoGUI(stack, 0, 0);\n            GlStateManager.translate((float)-var0, (float)-var2, (float)-var4);")
rep(P, "ScaledResolution scaledresolution = aEg.jY;",
       "ScaledResolution scaledresolution = MCPCompat.scaledResolution();")
rep(P, "RenderManager.bUO", "MCPCompat.renderPosX()", count=None)
rep(P, "RenderManager.bUP", "MCPCompat.renderPosY()", count=None)
rep(P, "RenderManager.bUQ", "MCPCompat.renderPosZ()", count=None)
add_import(P, "import com.alan.clients.util.MCPCompat;")

# ---------- 12. RiseShaderProgram ----------
P = "util/shader/base/RiseShaderProgram.java"
rep(P, "ScaledResolution scaledresolution = aEg.jY;",
       "ScaledResolution scaledresolution = MCPCompat.scaledResolution();")
add_import(P, "import com.alan.clients.util.MCPCompat;")

# ---------- 13. BloomShader ----------
P = "util/shader/impl/BloomShader.java"
rep(P, "RendererLivingEntity.bWd", "RendererLivingEntity.NAME_TAG_RANGE", count=None)
rep(P, "RendererLivingEntity.bWe", "RendererLivingEntity.NAME_TAG_RANGE_SNEAK", count=None)
rep(P, "            aEg.entityRenderer.IU();\n", "")
rep(P, "if (this.inputFramebuffer.ah(i, j)) {",
       "if (this.inputFramebuffer.framebufferWidth != i || this.inputFramebuffer.framebufferHeight != j) {")
rep(P, "if (this.outputFramebuffer.ah(i, j)) {",
       "if (this.outputFramebuffer.framebufferWidth != i || this.outputFramebuffer.framebufferHeight != j) {")

# ---------- 14. GaussianBlurShader ----------
P = "util/shader/impl/GaussianBlurShader.java"
rep(P, "if (this.inputFramebuffer.ah(i, j)) {",
       "if (this.inputFramebuffer.framebufferWidth != i || this.inputFramebuffer.framebufferHeight != j) {")
rep(P, "if (this.outputFramebuffer.ah(i, j)) {",
       "if (this.outputFramebuffer.framebufferWidth != i || this.outputFramebuffer.framebufferHeight != j) {")

# ---------- 15. OutlineShader ----------
P = "util/shader/impl/OutlineShader.java"
rep(P, "RendererLivingEntity.bWd", "RendererLivingEntity.NAME_TAG_RANGE", count=None)
rep(P, "RendererLivingEntity.bWe", "RendererLivingEntity.NAME_TAG_RANGE_SNEAK", count=None)
rep(P, "            aEg.entityRenderer.IU();\n", "")

# ---------- 16. MainMenuBackgroundShader ----------
rep("util/shader/impl/MainMenuBackgroundShader.java", "aEg.Bx()", "aEg.getSystemTime()")

# ---------- 17/18. RGQTestShader / TriRGQShader ----------
rep("util/shader/impl/RGQTestShader.java", "Minecraft.getMinecraft().Bx()",
    "Minecraft.getMinecraft().getSystemTime()")
rep("util/shader/impl/TriRGQShader.java", "Minecraft.getMinecraft().Bx()",
    "Minecraft.getMinecraft().getSystemTime()")

# ---------- 19. ShaderUtil ----------
rep("util/shader/ShaderUtil.java", "Minecraft.getMinecraft().gameSettings.cij",
    "Minecraft.getMinecraft().gameSettings.fboEnable")

# ---------- 20. BoundsNumberValue ----------
rep("value/impl/BoundsNumberValue.java", "return new BoundsNumberValue$1(this, l);",
    "return l;")

print("DONE")
