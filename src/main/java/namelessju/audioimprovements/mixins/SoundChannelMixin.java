package namelessju.audioimprovements.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.SoundBuffer;
import namelessju.audioimprovements.AudioImprovements;
import namelessju.audioimprovements.data.SoundChannelType;
import namelessju.audioimprovements.mixinaccessors.SoundBufferMixinAccessor;
import namelessju.audioimprovements.mixinaccessors.SoundChannelMixinAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
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

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;

@Mixin(Channel.class)
public abstract class SoundChannelMixin implements SoundChannelMixinAccessor
{
    @Shadow @Final
    private int source;
    
    @Unique @Nullable
    private SoundChannelType audioImprovements$type = null;
    @Unique
    private float audioImprovements$attenuationMultiplier = 1f;
    @Unique
    private boolean audioImprovements$isMono = false;
    @Unique
    private final float[] audioImprovements$posArray = new float[3];
    
    /** The original, unaltered position */
    @Unique @Nullable
    private Vec3 audioImprovements$posOriginal = null;
    
    /** The original, unaltered position from the previous tick */
    @Unique @Nullable
    private Vec3 audioImprovements$posPreviousTick = null;
    
    /** The original, unaltered relative position flag */
    @Unique @Nullable
    private Boolean audioImprovements$isRelativeOriginal = null;
    
    /** The original, unaltered attenuation */
    @Unique
    private float audioImprovements$attenuationOriginal = 0f;
    
    /** The attenuation before it gets overridden (may be altered by other mods) */
    @Unique
    private float audioImprovements$attenuationBefore = 0f;
    
    /** The attenuation reference distance before it gets overridden (may be altered by other mods) */
    @Unique
    private float audioImprovements$attenuationRefDistanceBefore = 0f;
    
    
    @Inject(method = "play", at = @At("HEAD"))
    private void audioImprovements$play(CallbackInfo ci)
    {
        audioImprovements$posPreviousTick = null;
        AudioImprovements.getInstance().directSourcePositionChanges.remove(source);
        audioImprovements$updateMonoPosition(audioImprovements$posOriginal);
    }
    
    @Inject(method = "stop", at = @At("HEAD"))
    private void audioImprovements$stop(CallbackInfo ci)
    {
        audioImprovements$cleanUp();
    }
    
    @Inject(method = "destroy", at = @At("HEAD"))
    private void audioImprovements$destroy(CallbackInfo ci)
    {
        audioImprovements$cleanUp();
    }
    
    @Inject(
        method = "setSelfPosition",
        at = @At(value = "INVOKE", target = "Lorg/lwjgl/openal/AL10;alSourcefv(II[F)V", ordinal = 0, remap = false),
        cancellable = true
    )
    private void audioImprovements$onSetLibraryPosition(Vec3 pos, CallbackInfo ci)
    {
        this.audioImprovements$posOriginal = pos;
        if (audioImprovements$isMono)
        {
            ci.cancel();
            return;
        }
        AudioImprovements.getInstance().isSettingChannelPosition = true;
    }
    
    @Inject(
        method = "setSelfPosition",
        at = @At("RETURN")
    )
    private void audioImprovements$afterSelfPosition(Vec3 pos, CallbackInfo ci)
    {
        AudioImprovements.getInstance().isSettingChannelPosition = false;
    }
    
    @Inject(
        method = "setRelative",
        at = @At(value = "INVOKE", target = "Lorg/lwjgl/openal/AL10;alSourcei(III)V", ordinal = 0, remap = false),
        cancellable = true
    )
    private void audioImprovements$setRelative(boolean relative, CallbackInfo ci)
    {
        audioImprovements$isRelativeOriginal = relative;
        if (audioImprovements$isMono) ci.cancel();
    }
    
    @Inject(method = "updateStream", at = @At("HEAD"))
    private void audioImprovements$beforeUpdateStream(CallbackInfo ci)
    {
        Vec3 directlyUpdatedPos = AudioImprovements.getInstance().directSourcePositionChanges.get(source);
        if (directlyUpdatedPos != null)
        {
            audioImprovements$posOriginal = directlyUpdatedPos;
            AudioImprovements.getInstance().directSourcePositionChanges.remove(source);
        }
        
        // Update mono audio
        audioImprovements$updateMonoPosition(audioImprovements$posOriginal);
        
        // Update velocity for doppler effect
        if (audioImprovements$posOriginal != null)
        {
            if (!audioImprovements$isMono &&
                !Boolean.TRUE.equals(audioImprovements$isRelativeOriginal) && audioImprovements$posPreviousTick != null)
            {
                Vec3 vel = audioImprovements$posOriginal.subtract(audioImprovements$posPreviousTick).scale(20f);
                AL10.alSource3f(source, AL10.AL_VELOCITY, (float) vel.x, (float) vel.y, (float) vel.z);
            }
            else AL10.alSource3f(source, AL10.AL_VELOCITY, 0f, 0f, 0f);
        }
        
        // Update attenuation
        audioImprovements$updateAttenuation(true);
        
        
        audioImprovements$posPreviousTick = audioImprovements$posOriginal;
    }
    
    @Inject(
        method = "attachStaticBuffer",
        at = @At("HEAD")
    )
    public void audioImprovements$attachStaticBufferStereoFix(SoundBuffer soundBuffer, CallbackInfo ci)
    {
        audioImprovements$fixStereoSpatialization(soundBuffer);
    }
    
    @WrapOperation(
        method = "pumpBuffers",
        at = @At(
            value = "NEW",
            target = "Lcom/mojang/blaze3d/audio/SoundBuffer;",
            ordinal = 0
        )
    )
    public SoundBuffer audioImprovements$pumpBuffersStereoFix(ByteBuffer byteBuffer, AudioFormat audioFormat, Operation<SoundBuffer> original)
    {
        SoundBuffer soundBuffer = original.call(byteBuffer, audioFormat);
        audioImprovements$fixStereoSpatialization(soundBuffer);
        return soundBuffer;
    }
    
    @Unique
    private void audioImprovements$fixStereoSpatialization(SoundBuffer soundBuffer)
    {
        if (AudioImprovements.getInstance().config.stereoSpatializationFix.value
            && !Boolean.TRUE.equals(audioImprovements$isRelativeOriginal))
        {
            ((SoundBufferMixinAccessor) soundBuffer).audioimprovements$convertStereoToMono();
        }
    }
    
    @Override
    public void audioImprovements$setOriginalAttenuation(float value)
    {
        audioImprovements$attenuationOriginal = value;
    }
    
    @Override
    public void audioImprovements$afterSetLinearAttenuation()
    {
        audioImprovements$attenuationRefDistanceBefore = AL10.alGetSourcef(source, AL10.AL_REFERENCE_DISTANCE);
        audioImprovements$attenuationBefore = AL10.alGetSourcef(source, AL10.AL_MAX_DISTANCE);
        audioImprovements$updateAttenuation(false);
    }
    
    
    @Unique
    private void audioImprovements$updateMonoPosition(Vec3 originalPos)
    {
        if (originalPos == null) return;
        
        boolean shouldBeMono = AudioImprovements.getInstance().isSoundTypeMono(audioImprovements$type);
        
        // Handle mono audio
        if (shouldBeMono)
        {
            audioImprovements$isMono = true;
            
            AL10.alSourcei(this.source, AL10.AL_SOURCE_RELATIVE, 1);
            Vec3 listenerPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            
            float distanceToListener = Boolean.TRUE.equals(audioImprovements$isRelativeOriginal)
                ? (float) originalPos.length()
                : (float) listenerPos.distanceTo(originalPos);
            
            AudioImprovements.getInstance().isSettingChannelPosition = true;
            audioImprovements$posArray[0] = 0f;
            audioImprovements$posArray[1] = 0f;
            audioImprovements$posArray[2] = -distanceToListener;
            AL10.alSourcefv(this.source, AL10.AL_POSITION, audioImprovements$posArray);
            AudioImprovements.getInstance().isSettingChannelPosition = false;
        }
        
        if (!shouldBeMono && audioImprovements$isMono)
        {
            AL10.alSourcei(this.source, AL10.AL_SOURCE_RELATIVE, Boolean.TRUE.equals(audioImprovements$isRelativeOriginal) ? 1 : 0);
            AudioImprovements.getInstance().isSettingChannelPosition = true;
            audioImprovements$posArray[0] = (float) originalPos.x;
            audioImprovements$posArray[1] = (float) originalPos.y;
            audioImprovements$posArray[2] = (float) originalPos.z;
            AL10.alSourcefv(this.source, AL10.AL_POSITION, audioImprovements$posArray);
            AudioImprovements.getInstance().isSettingChannelPosition = false;
            audioImprovements$isMono = false;
            AudioImprovements.LOGGER.debug("Reset sound {} position from mono to true 3D", source);
        }
    }
    
    @Unique
    private void audioImprovements$updateAttenuation(boolean allowResetToOriginal)
    {
        float attenuationMultiplier = AudioImprovements.getInstance().getAttenuationMultiplier(audioImprovements$type);
        if (audioImprovements$attenuationMultiplier != attenuationMultiplier)
        {
            audioImprovements$attenuationMultiplier = attenuationMultiplier;
            
            if (Mth.equal(attenuationMultiplier, 1f) || AL10.alGetSourcei(source, AL10.AL_DISTANCE_MODEL) != 53251)
            {
                if (!allowResetToOriginal) return;
                AL10.alSourcef(source, AL10.AL_REFERENCE_DISTANCE, audioImprovements$attenuationRefDistanceBefore);
                AL10.alSourcef(source, AL10.AL_MAX_DISTANCE, audioImprovements$attenuationBefore);
                AudioImprovements.LOGGER.debug("Reset {} ({}) attenuation to {} - {}",
                    source, audioImprovements$type,
                    audioImprovements$attenuationRefDistanceBefore,
                    audioImprovements$attenuationBefore
                );
            }
            else
            {
                AL10.alSourcef(source, AL10.AL_REFERENCE_DISTANCE, 0f);
                AL10.alSourcef(source, AL10.AL_MAX_DISTANCE, audioImprovements$getMaxDistance());
                AudioImprovements.LOGGER.debug("Overrode {} ({}) attenuation as {} - {}",
                    source, audioImprovements$type,
                    0f,
                    audioImprovements$getMaxDistance()
                );
            }
        }
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
    
    @Unique
    private void audioImprovements$clearReferences()
    {
        audioImprovements$type = null;
        audioImprovements$posOriginal = null;
        audioImprovements$posPreviousTick = null;
        audioImprovements$isRelativeOriginal = null;
    }
    
    @Unique
    private void audioImprovements$cleanUp()
    {
        audioImprovements$clearReferences();
        audioImprovements$removeMusicBlockSource();
        AudioImprovements.getInstance().directSourcePositionChanges.remove(source);
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
        return Mth.equal(audioImprovements$attenuationMultiplier, 1f)
            ? audioImprovements$attenuationBefore
            : audioImprovements$attenuationOriginal * audioImprovements$attenuationMultiplier;
    }
}