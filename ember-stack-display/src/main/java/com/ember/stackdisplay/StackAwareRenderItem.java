package com.ember.stackdisplay;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

/**
 * Wraps vanilla {@link RenderItem} so inventory/hotbar overlays draw the true
 * {@link ItemStack#getCount()} (including 65–127) without clamping the label to 64.
 * Durability / cooldown overlays are preserved by temporarily setting count to 1
 * before calling super, then drawing our own count string.
 */
@SideOnly(Side.CLIENT)
public class StackAwareRenderItem extends RenderItem {

    public StackAwareRenderItem(RenderItem base) {
        super(stealTextureManager(base), stealModelManager(base), stealItemColors(base));
        Object mesher = ObfuscationReflectionHelper.getPrivateValue(
                RenderItem.class, base, "itemModelMesher", "field_175059_m");
        ObfuscationReflectionHelper.setPrivateValue(
                RenderItem.class, this, mesher, "itemModelMesher", "field_175059_m");
    }

    private static TextureManager stealTextureManager(RenderItem base) {
        return ObfuscationReflectionHelper.getPrivateValue(
                RenderItem.class, base, "textureManager", "field_175057_n");
    }

    private static ItemColors stealItemColors(RenderItem base) {
        return ObfuscationReflectionHelper.getPrivateValue(
                RenderItem.class, base, "itemColors", "field_184395_f");
    }

    private static ModelManager stealModelManager(RenderItem base) {
        Object mesher = ObfuscationReflectionHelper.getPrivateValue(
                RenderItem.class, base, "itemModelMesher", "field_175059_m");
        return readModelManager(mesher);
    }

    @SuppressWarnings("unchecked")
    private static ModelManager readModelManager(Object mesher) {
        Class<?> clazz = mesher.getClass();
        String[] names = new String[] { "modelManager", "field_178090_d" };
        for (String name : names) {
            try {
                Field f = findField(clazz, name);
                if (f != null) {
                    f.setAccessible(true);
                    return (ModelManager) f.get(mesher);
                }
            } catch (Throwable ignored) {
            }
        }
        throw new IllegalStateException("Could not read ItemModelMesher.modelManager");
    }

    private static Field findField(Class<?> clazz, String name) {
        Class<?> c = clazz;
        while (c != null) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                c = c.getSuperclass();
            }
        }
        return null;
    }

    @Override
    public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, @Nullable String text) {
        if (stack.isEmpty()) {
            super.renderItemOverlayIntoGUI(fr, stack, xPosition, yPosition, text);
            return;
        }

        final int realCount = stack.getCount();
        final String overlayText = text;

        // Suppress vanilla count drawing (skipped when count==1 && text==null),
        // but still let super draw durability bar / cooldown overlay.
        stack.setCount(1);
        try {
            super.renderItemOverlayIntoGUI(fr, stack, xPosition, yPosition, null);
        } finally {
            stack.setCount(realCount);
        }

        if (realCount != 1 || overlayText != null) {
            String s = overlayText == null ? String.valueOf(realCount) : overlayText;
            drawCountLabel(fr, s, xPosition, yPosition, realCount);
        }
    }

    private static void drawCountLabel(FontRenderer fr, String s, int xPosition, int yPosition, int realCount) {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();

        float scale = 1.0F;
        if (s.length() >= 3) {
            scale = 0.75F;
        } else if (realCount > 64) {
            scale = 0.9F;
        }

        float x = xPosition + 19 - 2;
        float y = yPosition + 6 + 3;

        if (scale != 1.0F) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, 0.0F);
            GlStateManager.scale(scale, scale, 1.0F);
            fr.drawStringWithShadow(s, -fr.getStringWidth(s), 0.0F, 0xFFFFFF);
            GlStateManager.popMatrix();
        } else {
            fr.drawStringWithShadow(s, x - fr.getStringWidth(s), y, 0xFFFFFF);
        }

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableBlend();
    }
}
