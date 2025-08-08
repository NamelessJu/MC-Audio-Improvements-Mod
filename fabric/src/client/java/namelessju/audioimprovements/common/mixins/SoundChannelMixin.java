package namelessju.audioimprovements.common.mixins;

import com.mojang.blaze3d.audio.Channel;
import namelessju.audioimprovements.common.AudioImprovements;
import namelessju.audioimprovements.common.data.SoundChannelType;
import namelessju.audioimprovements.common.mixinaccessors.SoundChannelMixinAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.openal.AL10;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Channel.class)
public abstract class SoundChannelMixin implements SoundChannelMixinAccessor
{
    @Shadow @Final
    private int source;
    
    @Unique @Nullable
    private SoundChannelType audioImprovements$type = null;
    @Unique @Nullable
    private Vec3 audioImprovements$posOriginal = null;
    @Unique @Nullable
    private Boolean audioImprovements$isRelativeOriginal = null;
    @Unique
    private float audioImprovements$attenuationOriginal = 1f;
    @Unique
    private float audioImprovements$attenuationMultiplier = 1f;
    @Unique
    private boolean audioImprovements$monoBefore = false;
    
    
    @Inject(method = "play", at = @At("HEAD"))
    private void audioImprovements$play(CallbackInfo ci)
    {
        audioImprovements$updatePosition();
    }
    
    @Inject(method = "stop", at = @At("HEAD"))
    private void audioImprovements$stop(CallbackInfo ci)
    {
        audioImprovements$removeMusicBlockSource();
    }
    
    @Inject(method = "destroy", at = @At("HEAD"))
    private void audioImprovements$destroy(CallbackInfo ci)
    {
        audioImprovements$removeMusicBlockSource();
    }
    
    @Inject(method = "setSelfPosition", at = @At("HEAD"), cancellable = true)
    private void audioImprovements$setSelfPosition(Vec3 pos, CallbackInfo ci)
    {
        this.audioImprovements$posOriginal = pos;
        if (audioImprovements$isMono()) ci.cancel();
    }
    
    @Inject(method = "setRelative", at = @At("HEAD"), cancellable = true)
    private void audioImprovements$setRelative(boolean relative, CallbackInfo ci)
    {
        audioImprovements$isRelativeOriginal = relative;
        if (audioImprovements$isMono()) ci.cancel();
    }
    
    @Inject(method = "updateStream", at = @At("HEAD"))
    private void audioImprovements$updateStream(CallbackInfo ci)
    {
        // Update mono audio
        audioImprovements$updatePosition();
        
        // Update attenuation
        float attenuationMultiplier = AudioImprovements.getInstance().getAttenuationMultiplier(audioImprovements$type);
        if (audioImprovements$attenuationMultiplier != attenuationMultiplier)
        {
            audioImprovements$attenuationMultiplier = attenuationMultiplier;
            audioImprovements$applyAttenuation();
        }
    }
    
    @Inject(method = "linearAttenuation", at = @At("HEAD"), cancellable = true)
    private void audioImprovements$linearAttenuation(float attenuation, CallbackInfo ci)
    {
        audioImprovements$attenuationOriginal = attenuation;
        AL10.alSourcei(source, AL10.AL_DISTANCE_MODEL, 53251);
        AL10.alSourcef(source, AL10.AL_ROLLOFF_FACTOR, 1f);
        AL10.alSourcef(source, AL10.AL_REFERENCE_DISTANCE, 0f);
        audioImprovements$applyAttenuation();
        ci.cancel();
    }
    
    @Override
    public void audioImprovements$setSoundType(SoundChannelType type)
    {
        this.audioImprovements$type = type;
    }
    
    @Override
    public SoundChannelType audioImprovements$getSoundType()
    {
        return audioImprovements$type;
    }
    
    @Override
    public Vec3 audioImprovements$getPos()
    {
        return audioImprovements$posOriginal;
    }
    
    @Override
    public float audioImprovements$getMaxDistance()
    {
        if (audioImprovements$attenuationMultiplier < 0f) return Float.POSITIVE_INFINITY;
        return audioImprovements$attenuationOriginal * audioImprovements$attenuationMultiplier;
    }
    
    @Unique
    private boolean audioImprovements$isMono()
    {
        return AudioImprovements.getInstance().isSoundTypeMono(audioImprovements$type);
    }
    
    @Unique
    private void audioImprovements$updatePosition()
    {
        if (audioImprovements$posOriginal == null) return;
        
        if (audioImprovements$isMono())
        {
            AL10.alSourcei(this.source, AL10.AL_SOURCE_RELATIVE, 1);
            if (!Boolean.TRUE.equals(audioImprovements$isRelativeOriginal))
            {
                Vec3 listenerPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
                float distanceToListener = (float) listenerPos.distanceTo(audioImprovements$posOriginal);
                AL10.alSourcefv(this.source, AL10.AL_POSITION, new float[] {0f, 0f, distanceToListener});
            }
            else AL10.alSourcefv(this.source, AL10.AL_POSITION, new float[] {(float) audioImprovements$posOriginal.x, (float) audioImprovements$posOriginal.y, (float) audioImprovements$posOriginal.z});
            audioImprovements$monoBefore = true;
        }
        else if (audioImprovements$monoBefore)
        {
            AL10.alSourcei(this.source, AL10.AL_SOURCE_RELATIVE, Boolean.TRUE.equals(audioImprovements$isRelativeOriginal) ? 1 : 0);
            AL10.alSourcefv(this.source, AL10.AL_POSITION, new float[] {(float) audioImprovements$posOriginal.x, (float) audioImprovements$posOriginal.y, (float) audioImprovements$posOriginal.z});
            audioImprovements$monoBefore = false;
            AudioImprovements.LOGGER.debug("Reset sound {} position from mono to true 3D", source);
        }
    }
    
    @Unique
    private void audioImprovements$applyAttenuation()
    {
        if (AL10.alGetSourcei(source, AL10.AL_DISTANCE_MODEL) != 53251) return;
        AL10.alSourcef(source, AL10.AL_MAX_DISTANCE, audioImprovements$getMaxDistance());
    }
    
    @Unique
    private void audioImprovements$removeMusicBlockSource()
    {
        if (this.audioImprovements$type == SoundChannelType.MUSIC_DISC
            || this.audioImprovements$type == SoundChannelType.NOTE_BLOCK)
        {
            boolean wasRemoved = AudioImprovements.getInstance().musicBlockChannels.remove((Channel)(Object)this);
            if (wasRemoved && AudioImprovements.LOGGER.isDebugEnabled())
            {
                AudioImprovements.LOGGER.debug("Removed reference to music block sound channel ({} references left)",
                    AudioImprovements.getInstance().musicBlockChannels.size());
            }
        }
    }
}