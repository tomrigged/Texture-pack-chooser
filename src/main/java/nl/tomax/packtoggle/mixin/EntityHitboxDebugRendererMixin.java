package nl.tomax.packtoggle.mixin;

import net.minecraft.client.renderer.debug.EntityHitboxDebugRenderer;
import nl.tomax.packtoggle.PackHitboxColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * In 1.21.11 tekent vanilla elke hitbox in EntityHitboxDebugRenderer#showHitboxes met
 * Gizmos.cuboid(box, GizmoStyle.stroke(kleur)).
 * Hier vervangen we de standaard witte kleur door de kleur van de actieve pack.
 * De rode ooghoogte-lijn en blauwe kijkrichting-lijn zijn niet wit en blijven vanilla.
 */
@Mixin(EntityHitboxDebugRenderer.class)
public class EntityHitboxDebugRendererMixin {

    @ModifyArg(
            method = "showHitboxes",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/gizmos/GizmoStyle;stroke(I)Lnet/minecraft/gizmos/GizmoStyle;"
            ),
            index = 0,
            require = 0
    )
    private static int tomaxpacktoggle$recolor(int color) {
        if (color == 0xFFFFFFFF) {
            int argb = PackHitboxColor.currentArgb();
            if (argb != 0) return argb;
        }
        return color;
    }
}
