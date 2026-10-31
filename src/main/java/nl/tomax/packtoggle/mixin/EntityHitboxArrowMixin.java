package nl.tomax.packtoggle.mixin;

import net.minecraft.client.renderer.debug.EntityHitboxDebugRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Verbergt de blauwe kijkrichting-pijl die vanilla bij elke hitbox tekent
 * (alpha 0 = onzichtbaar). Staat in een aparte mixin zodat de kleur-mixin
 * altijd blijft werken, ook als dit onderdeel niet past.
 */
@Mixin(EntityHitboxDebugRenderer.class)
public class EntityHitboxArrowMixin {

    private static final int VANILLA_VIEW_BLUE = 0xFF0000FF;

    @ModifyArg(
            method = "showHitboxes",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/gizmos/Gizmos;arrow"),
            index = 2,
            require = 0
    )
    private static int tomaxpacktoggle$hideArrow(int color) {
        return color == VANILLA_VIEW_BLUE ? 0x00000000 : color;
    }
}
