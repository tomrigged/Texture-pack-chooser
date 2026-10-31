package nl.tomax.packtoggle.mixin;

import net.minecraft.client.renderer.debug.EntityHitboxDebugRenderer;
import nl.tomax.packtoggle.PackHitboxColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * In 1.21.11 tekent vanilla elke hitbox in EntityHitboxDebugRenderer#showHitboxes met
 * Gizmos.cuboid(box, GizmoStyle.stroke(kleur)).
 * Hier vervangen we wit (hitbox) en rood (ooghoogte/hoofd) door de kleur van de actieve pack.
 */
@Mixin(EntityHitboxDebugRenderer.class)
public class EntityHitboxDebugRendererMixin {

    private static final String SHOW = "showHitboxes(Lnet/minecraft/world/entity/Entity;FZ)V";

    @ModifyArg(
            method = SHOW,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/gizmos/GizmoStyle;stroke(I)Lnet/minecraft/gizmos/GizmoStyle;"),
            index = 0,
            require = 0
    )
    private static int tomaxpacktoggle$recolor(int color) {
        return recolor(color);
    }

    @ModifyArg(
            method = SHOW,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/gizmos/GizmoStyle;stroke(IF)Lnet/minecraft/gizmos/GizmoStyle;"),
            index = 0,
            require = 0
    )
    private static int tomaxpacktoggle$recolorWidth(int color) {
        return recolor(color);
    }

    private static int recolor(int color) {
        if (color == 0xFFFFFFFF || color == 0xFFFF0000) {
            int argb = PackHitboxColor.currentArgb();
            if (argb != 0) return argb;
        }
        return color;
    }
}
