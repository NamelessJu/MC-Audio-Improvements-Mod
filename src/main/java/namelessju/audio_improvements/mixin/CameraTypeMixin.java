package namelessju.audio_improvements.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import namelessju.audio_improvements.AudioImprovements;
import net.minecraft.client.CameraType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CameraType.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public abstract class CameraTypeMixin
{
    @Inject(method = "cycle", at = @At("HEAD"))
    private void onCycle(CallbackInfoReturnable<CameraType> cir)
    {
        AudioImprovements.instance().skipNextListenerDopplerVelocityUpdate = true;
        AudioImprovements.LOGGER.debug("Camera type cycled");
    }
}
