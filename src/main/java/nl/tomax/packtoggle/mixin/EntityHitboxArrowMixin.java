package nl.tomax.packtoggle.mixin;

import net.minecraft.client.renderer.debug.EntityHitboxDebugRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Verbergt de blauwe kijkrichting-pijl die vanilla bij elke hitbox tekent
 * (alpha 0 = onzichtbaar). Aparte mixin zodat de kleur-mixin los blijft werken.
 */
@Mixin(EntityHitboxDebugRenderer.class)
public class EntityHitboxArrowMixin {

    private static final String SHOW = "showHitboxes(Lnet/minecraft/world/entity/Entity;FZ)V";
    private static final int VANILLA_VIEW_BLUE = 0xFF0000FF;
    private static final Logger LOGGER = LoggerFactory.getLogger("tomaxpacktoggle");
    private static boolean logged = false;

    @ModifyArg(
            method = SHOW,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/gizmos/Gizmos;arrow(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;I)Lnet/minecraft/gizmos/GizmoProperties;"),
            index = 2,
            require = 0
    )
    private static int tomaxpacktoggle$hideArrow(int color) {
        return hide(color);
    }

    @ModifyArg(
            method = SHOW,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/gizmos/Gizmos;arrow(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;IF)Lnet/minecraft/gizmos/GizmoProperties;"),
            index = 2,
            require = 0
    )
    private static int tomaxpacktoggle$hideArrowWidth(int color) {
        return hide(color);
    }

    private static int hide(int color) {
        if (nl.tomax.packtoggle.TomaxConfig.vanillaHitbox) return color;
        if (color == VANILLA_VIEW_BLUE) {
            if (!logged) {
                logged = true;
                LOGGER.info("Kijkrichting-pijl wordt verborgen (mixin actief)");
            }
            return 0x00000000;
        }
        return color;
    }
}
