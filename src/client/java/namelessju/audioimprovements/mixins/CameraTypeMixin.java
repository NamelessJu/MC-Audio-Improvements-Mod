package namelessju.audioimprovements.mixins;

import namelessju.audioimprovements.AudioImprovements;
import net.minecraft.client.CameraType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CameraType.class)
public abstract class CameraTypeMixin
{
    @Inject(method = "cycle", at = @At("HEAD"))
    private void audioImprovements$onCycle(CallbackInfoReturnable<CameraType> cir)
    {
        AudioImprovements.getInstance().skipNextListenerDopplerVelocityUpdate = true;
        AudioImprovements.LOGGER.debug("Camera type cycled");
    }
}
