package myaupp.util;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;

/**
 * BlockUtil —— 1:1 复刻 OpenMyauPP 中 Telly 依赖的 isInteractable/isSolid。
 */
public class BlockUtil {
    public static boolean isInteractable(Block block) {
        if (block instanceof BlockContainer) return true;
        if (block instanceof BlockWorkbench) return true;
        if (block instanceof BlockAnvil) return true;
        if (block instanceof BlockBed) return true;
        if (block instanceof BlockDoor) {
            if (block.getMaterial() != Material.iron) return true;
        }
        if (block instanceof BlockTrapDoor) return true;
        if (block instanceof BlockFenceGate) return true;
        if (block instanceof BlockFence) return true;
        if (block instanceof BlockButton) return true;
        if (block instanceof BlockLever) return true;
        return block instanceof BlockJukebox;
    }

    public static boolean isSolid(Block block) {
        if (block instanceof BlockStairs) return false;
        if (block instanceof BlockSlab) return false;
        if (block instanceof BlockEndPortalFrame) return false;
        if (block instanceof BlockEndPortal) return false;
        if (block instanceof BlockVine) return false;
        if (block instanceof BlockPumpkin) return false;
        if (block instanceof BlockCactus) return false;
        if (block instanceof BlockBush) return false;
        if (block instanceof BlockFalling) return false;
        if (block instanceof BlockWeb) return false;
        if (block instanceof BlockPane) return false;
        if (block instanceof BlockCarpet) return false;
        if (block instanceof BlockSnow) return false;
        if (block instanceof BlockFence) return false;
        if (block instanceof BlockFenceGate) return false;
        if (block instanceof BlockWall) return false;
        if (block instanceof BlockLadder) return false;
        if (block instanceof BlockTorch) return false;
        if (block instanceof BlockRedstoneWire) return false;
        if (block instanceof BlockRedstoneDiode) return false;
        if (block instanceof BlockBasePressurePlate) return false;
        if (block instanceof BlockTripWire) return false;
        if (block instanceof BlockTripWireHook) return false;
        if (block instanceof BlockRailBase) return false;
        if (block instanceof BlockSlime) return false;
        return !(block instanceof BlockTNT);
    }
}
