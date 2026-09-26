package com.ember.stackdisplay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ClientHooks {

    private ClientHooks() {
    }

    public static void install() {
        try {
            Minecraft mc = Minecraft.getMinecraft();
            RenderItem original = mc.getRenderItem();
            if (original instanceof StackAwareRenderItem) {
                EmberStackDisplay.LOGGER.info("StackAwareRenderItem already installed");
                return;
            }
            StackAwareRenderItem wrapped = new StackAwareRenderItem(original);
            // MCP renderItem / SRG field_175621_X — ORH remaps SRG in deobf envs
            ObfuscationReflectionHelper.setPrivateValue(
                    Minecraft.class,
                    mc,
                    wrapped,
                    "renderItem", "field_175621_X"
            );
            EmberStackDisplay.LOGGER.info("Installed StackAwareRenderItem wrapper (real stack counts in GUI)");
        } catch (Throwable t) {
            EmberStackDisplay.LOGGER.error("Failed to install StackAwareRenderItem", t);
        }
    }
}
