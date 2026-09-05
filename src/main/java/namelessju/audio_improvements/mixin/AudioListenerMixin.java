package namelessju.audio_improvements.mixin;

import com.mojang.blaze3d.audio.Listener;
//? if >1.21.1 {
import com.mojang.blaze3d.audio.ListenerTransform;
//? }
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import namelessju.audio_improvements.AudioImprovements;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.openal.AL10;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Listener.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public abstract class AudioListenerMixin
{
    //? if <=1.21.1 {
    /*@Shadow
    private Vec3 position;
    *///? } else {
    @Shadow
    private ListenerTransform transform;
    //? }

    @Unique
    private int audioImprovements$skipCounter = 0;

    //? if <=1.21.1 {
    /*@Inject(method = "setListenerPosition", at = @At("HEAD"))
    private void beforeSetTransform(Vec3 position, CallbackInfo ci)
    {
        audioImprovements$updateListenerDopplerVelocity(this.position, position);
    }
    *///? } else {
    @Inject(method = "setTransform", at = @At("HEAD"))
    private void beforeSetTransform(ListenerTransform transform, CallbackInfo ci)
    {
        audioImprovements$updateListenerDopplerVelocity(this.transform.position(), transform.position());
    }
    //? }

    @Unique
    private void audioImprovements$updateListenerDopplerVelocity(Vec3 oldListenerPosition, Vec3 newListenerPosition)
    {
        if (AudioImprovements.instance().skipNextListenerDopplerVelocityUpdate)
        {
            // skip 2 updates because the first can be too early
            audioImprovements$skipCounter = 2;
            AudioImprovements.instance().skipNextListenerDopplerVelocityUpdate = false;
        }

        if (audioImprovements$skipCounter <= 0)
        {
            Vec3 vel = newListenerPosition.subtract(oldListenerPosition).scale(20f);
            AL10.alListener3f(AL10.AL_VELOCITY, (float) vel.x, (float) vel.y, (float) vel.z);
        }
        else
        {
            audioImprovements$skipCounter--;
            AudioImprovements.LOGGER.debug("Skipped listener doppler velocity update");
        }
    }
}
