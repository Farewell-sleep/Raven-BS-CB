package myaupp.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

/**
 * RotationUtil —— Telly 的 raycastBlock 依赖 rayTrace。
 * 使用 1:1 复刻原版 Entity.getVectorForRotation 的数学实现（不依赖 accessor）。
 */
public class RotationUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static MovingObjectPosition rayTrace(float yaw, float pitch, double distance, float partialTicks) {
        Vec3 eyePos = RotationUtil.mc.thePlayer.getPositionEyes(partialTicks);
        Vec3 lookVec = getLookVector(pitch, yaw);
        Vec3 targetPos = eyePos.addVector(lookVec.xCoord * distance, lookVec.yCoord * distance, lookVec.zCoord * distance);
        return RotationUtil.mc.theWorld.rayTraceBlocks(eyePos, targetPos);
    }

    private static Vec3 getLookVector(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * (float) Math.PI / 180.0F - (float) Math.PI);
        float f1 = MathHelper.sin(-yaw * (float) Math.PI / 180.0F - (float) Math.PI);
        float f2 = -MathHelper.cos(-pitch * (float) Math.PI / 180.0F);
        float f3 = MathHelper.sin(-pitch * (float) Math.PI / 180.0F);
        return new Vec3((double) (f1 * f2), (double) f3, (double) (f * f2));
    }
}
