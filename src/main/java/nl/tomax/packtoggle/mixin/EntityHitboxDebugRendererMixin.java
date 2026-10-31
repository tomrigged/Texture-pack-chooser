package nl.tomax.packtoggle.mixin;

import net.minecraft.client.renderer.debug.EntityHitboxDebugRenderer;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.world.entity.Entity;
import nl.tomax.packtoggle.PackHitboxColor;
import nl.tomax.packtoggle.TomaxConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Past in EntityHitboxDebugRenderer#showHitboxes de kleur en dikte van de hitbox aan:
 *  - kleur van de actieve pack (binnen reach van een tegenstander: wit)
 *  - dikkere lijnen
 */
@Mixin(EntityHitboxDebugRenderer.class)
public class EntityHitboxDebugRendererMixin {

    private static final String SHOW = "showHitboxes(Lnet/minecraft/world/entity/Entity;FZ)V";

    @Inject(method = SHOW, at = @At("HEAD"), cancellable = true, require = 0)
    private static void tomaxpacktoggle$begin(Entity entity, float partialTick, boolean server, CallbackInfo ci) {
        if (!TomaxConfig.hitboxEnabled) {
            ci.cancel(); // hitbox uit via het Right Shift menu
            return;
        }
        PackHitboxColor.currentEntity = entity;
    }

    @Inject(method = SHOW, at = @At("RETURN"), require = 0)
    private static void tomaxpacktoggle$end(Entity entity, float partialTick, boolean server, CallbackInfo ci) {
        PackHitboxColor.currentEntity = null;
    }

    @Redirect(
            method = SHOW,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/gizmos/GizmoStyle;stroke(I)Lnet/minecraft/gizmos/GizmoStyle;"),
            require = 0
    )
    private static GizmoStyle tomaxpacktoggle$stroke(int color) {
        if (TomaxConfig.vanillaHitbox) return GizmoStyle.stroke(color);
        return GizmoStyle.stroke(PackHitboxColor.styleColor(color), TomaxConfig.width);
    }

    @Redirect(
            method = SHOW,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/gizmos/GizmoStyle;stroke(IF)Lnet/minecraft/gizmos/GizmoStyle;"),
            require = 0
    )
    private static GizmoStyle tomaxpacktoggle$strokeWidth(int color, float width) {
        if (TomaxConfig.vanillaHitbox) return GizmoStyle.stroke(color, width);
        return GizmoStyle.stroke(PackHitboxColor.styleColor(color), TomaxConfig.width);
    }
}
