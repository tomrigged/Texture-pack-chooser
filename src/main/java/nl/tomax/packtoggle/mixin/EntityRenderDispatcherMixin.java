package nl.tomax.packtoggle.mixin;

import nl.tomax.packtoggle.PackHitboxColor;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.HitboxRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    /**
     * Maakt de standaard witte hitbox de kleur van de actieve pack.
     * De rode ooghoogte-lijn en andere gekleurde boxen blijven zoals ze zijn.
     */
    @ModifyVariable(
            method = "renderHitbox(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/renderer/entity/state/HitboxRenderState;)V",
            at = @At("HEAD"),
            argsOnly = true,
            require = 0
    )
    private static HitboxRenderState tomaxpacktoggle$recolor(HitboxRenderState s) {
        if (s.red() == 1.0F && s.green() == 1.0F && s.blue() == 1.0F) {
            float[] c = PackHitboxColor.current();
            if (c != null) {
                return new HitboxRenderState(
                        s.x0(), s.y0(), s.z0(), s.x1(), s.y1(), s.z1(),
                        s.offsetX(), s.offsetY(), s.offsetZ(),
                        c[0], c[1], c[2]);
            }
        }
        return s;
    }
}
