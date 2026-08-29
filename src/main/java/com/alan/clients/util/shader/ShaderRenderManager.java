package com.alan.clients.util.shader;

import com.alan.clients.Client;
import com.alan.clients.module.impl.render.Interface;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.other.GameEvent;
import com.alan.clients.newevent.impl.render.Render2DEvent;
import com.alan.clients.newevent.impl.render.Render3DEvent;
import com.alan.clients.ui.ingame.GuiIngameCache;
import com.alan.clients.util.shader.base.RiseShader;
import com.alan.clients.util.shader.base.ShaderRenderType;
import com.alan.clients.newevent.impl.render.RenderGuiEvent;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Generated;
import net.minecraft.client.renderer.GlStateManager;

public class ShaderRenderManager {
    private final LinkedHashMap<Integer, LinkedHashMap<ShaderQueueType, ShaderRenderQueue>> kM = new LinkedHashMap<>();
    private final int kN = 3;
    private boolean kO;
    @EventLink(value = -1)
    public final Listener<RenderGuiEvent> kP = var1 -> this.b(ShaderRenderType.OVERLAY);
    @EventLink(value = -1)
    public final Listener<Render2DEvent> onRender2D = var1 -> {
        GuiIngameCache.renderGameOverlay(0.0F);
        this.b(ShaderRenderType.OVERLAY);
    };
    @EventLink
    public final Listener<GameEvent> onGame = var1 -> this.kO = Client.a.g().c(Interface.class).aoc.wo();
    @EventLink(value = -1)
    public final Listener<Render3DEvent> onRender3D = var1 -> this.b(ShaderRenderType.CAMERA);

    public ShaderRenderManager() {
        try {
            for (int i = 0; i <= 3; i++) {
                this.kM.put(i, new LinkedHashMap<>());

                for (ShaderQueueType gg : ShaderQueueType.values()) {
                    this.kM.get(i).put(gg, new ShaderRenderQueue(gg.dW().getType() == null ? null : (RiseShader)gg.dW().getType().newInstance()));
                }
            }

            Client.a.e().b(this);
        } catch (RuntimeException | Error throwable) {
            throw throwable;
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public ShaderRenderQueue a(ShaderQueueType var1) {
        return this.a(var1, 0);
    }

    public ShaderRenderQueue a(ShaderQueueType var1, int var2) {
        return this.kM.get(var2).get(var1);
    }

    private void b(ShaderRenderType var1) {
        try {
            this.kM.forEach((var2, var3) -> var3.values().forEach(var2x -> {
                if (var2x.dU() == null || this.kO) {
                    var2x.a(var1);
                }
            }));
        } finally {
            this.kM.forEach((var0, var1x) -> var1x.forEach((var0x, var1xx) -> var1xx.clear()));
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
    }

    @Generated
    public LinkedHashMap<Integer, LinkedHashMap<ShaderQueueType, ShaderRenderQueue>> dV() {
        return this.kM;
    }

    private static void a(AtomicInteger atomicInteger, Integer var1, LinkedHashMap var2) {
        var2.values().forEach(var1x -> {
            if (((ShaderRenderQueue)var1x).dU() != null && !((ShaderRenderQueue)var1x).dT().isEmpty()) {
                atomicInteger.getAndIncrement();
            }
        });
    }
}
