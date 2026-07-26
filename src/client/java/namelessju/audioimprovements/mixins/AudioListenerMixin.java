package namelessju.audioimprovements.mixins;

import com.mojang.blaze3d.audio.Listener;
import com.mojang.blaze3d.audio.ListenerTransform;
import namelessju.audioimprovements.AudioImprovements;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.openal.AL10;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Listener.class)
public abstract class AudioListenerMixin
{
    @Shadow
    private ListenerTransform transform;

    @Unique
    private int audioImprovements$skipCounter = 0;

    @Inject(method = "setTransform", at = @At("HEAD"))
    private void audioImprovements$beforeSetTransform(ListenerTransform newTransform, CallbackInfo ci)
    {
        if (AudioImprovements.getInstance().skipNextListenerDopplerVelocityUpdate)
        {
            audioImprovements$skipCounter = 2;
            AudioImprovements.getInstance().skipNextListenerDopplerVelocityUpdate = false;
        }

        if (audioImprovements$skipCounter <= 0)
        {
            Vec3 vel = newTransform.position().subtract(this.transform.position()).scale(20f);
            AL10.alListener3f(AL10.AL_VELOCITY, (float) vel.x, (float) vel.y, (float) vel.z);
        }
        else
        {
            audioImprovements$skipCounter--;
            AudioImprovements.LOGGER.debug("Skipped listener doppler velocity update");
        }
    }
}
