package nl.tomax.packtoggle.mixin;

import nl.tomax.packtoggle.PackHitboxColor;
import net.minecraft.client.renderer.entity.state.HitboxRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Elke renderer leest de hitbox-kleur via red()/green()/blue().
 * Als de hitbox wit is (de standaard entity-hitbox) geven we de kleur van de actieve pack terug.
 * De rode ooghoogte-box is niet wit en blijft dus rood.
 */
@Mixin(HitboxRenderState.class)
public abstract class HitboxRenderStateMixin {

    @Shadow @Final private float red;
    @Shadow @Final private float green;
    @Shadow @Final private float blue;

    @Unique
    private boolean tomaxpacktoggle$isWhite() {
        return red >= 0.99F && green >= 0.99F && blue >= 0.99F;
    }

    @Inject(method = "red()F", at = @At("RETURN"), cancellable = true, require = 0)
    private void tomaxpacktoggle$red(CallbackInfoReturnable<Float> cir) {
        if (tomaxpacktoggle$isWhite()) {
            float[] c = PackHitboxColor.current();
            if (c != null) cir.setReturnValue(c[0]);
        }
    }

    @Inject(method = "green()F", at = @At("RETURN"), cancellable = true, require = 0)
    private void tomaxpacktoggle$green(CallbackInfoReturnable<Float> cir) {
        if (tomaxpacktoggle$isWhite()) {
            float[] c = PackHitboxColor.current();
            if (c != null) cir.setReturnValue(c[1]);
        }
    }

    @Inject(method = "blue()F", at = @At("RETURN"), cancellable = true, require = 0)
    private void tomaxpacktoggle$blue(CallbackInfoReturnable<Float> cir) {
        if (tomaxpacktoggle$isWhite()) {
            float[] c = PackHitboxColor.current();
            if (c != null) cir.setReturnValue(c[2]);
        }
    }
}
