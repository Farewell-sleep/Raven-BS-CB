package keystrokesmod.module.impl.render;

import keystrokesmod.event.AttackEvent;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class KillEffect extends Module {

    private final ButtonSetting lightning = new ButtonSetting("Lightning", true);
    private final ButtonSetting bloodExplosion = new ButtonSetting("Blood Explosion", true);
    private final ButtonSetting explosion = new ButtonSetting("Explosion", true);

    private EntityLivingBase target;

    public KillEffect() {
        super("KillEffect", category.render);
        this.registerSetting(lightning);
        this.registerSetting(bloodExplosion);
        this.registerSetting(explosion);
    }

    @Override
    public void onEnable() {
        target = null;
    }

    @Override
    public void onDisable() {
        target = null;
    }

    @SubscribeEvent
    public void onAttack(AttackEvent e) {
        if (!Utils.nullCheck()) return;
        if (e.attacker != mc.thePlayer) return;
        if (e.target instanceof EntityLivingBase) {
            this.target = (EntityLivingBase) e.target;
        }
    }

    @SubscribeEvent
    public void onPreMotion(PreMotionEvent e) {
        if (!Utils.nullCheck()) return;
        if (this.target == null) return;
        if (mc.theWorld.loadedEntityList.contains(this.target)) return;

        // Target died / removed from world — trigger effects
        if (lightning.isToggled()) {
            EntityLightningBolt bolt = new EntityLightningBolt(
                mc.theWorld, this.target.posX, this.target.posY, this.target.posZ
            );
            mc.theWorld.addEntityToWorld((int) (-Math.random() * 100000.0), bolt);
            mc.thePlayer.playSound("ambient.weather.thunder", 10000.0F, 0.95F);
            mc.thePlayer.playSound("random.explode", 2.0F, 0.57F);
        }

        if (explosion.isToggled()) {
            for (int i = 0; i <= 8; i++) {
                mc.effectRenderer.emitParticleAtEntity(this.target, EnumParticleTypes.FLAME);
            }
            mc.thePlayer.playSound("item.fireCharge.use", 1.0F, 1.0F);
        }

        if (bloodExplosion.isToggled()) {
            double d0 = this.target.posY;
            double d1 = this.target.posY + this.target.height + 0.4;
            double d2 = 0.4;

            for (int j = 0; j < 100; j++) {
                for (double d3 = d0; d3 <= d1; d3 += d2) {
                    mc.theWorld.spawnParticle(
                        EnumParticleTypes.BLOCK_CRACK,
                        this.target.posX, d3, this.target.posZ,
                        0.0, 0.0, 0.0,
                        Block.getStateId(Blocks.redstone_block.getDefaultState())
                    );
                }
            }

            for (double d4 = d0; d4 <= d1; d4 += d2) {
                mc.thePlayer.playSound("dig.stone", 1.0F, 1.0F);
            }
        }

        this.target = null;
    }
}
