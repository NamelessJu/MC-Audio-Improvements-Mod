package namelessju.audioimprovements.common.mixins;

import com.mojang.blaze3d.audio.Channel;
import namelessju.audioimprovements.common.AudioImprovements;
import namelessju.audioimprovements.common.SoundType;
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
    
    @Unique
    private boolean audioImprovements$monoBefore = false;
    @Unique @Nullable
    private Vec3 audioImprovements$pos = null;
    @Unique @Nullable
    private Boolean audioImprovements$isRelativeOriginal = null;
    @Unique
    public SoundType audioImprovements$type = SoundType.OTHER;
    
    @Inject(method = "stop", at = @At("HEAD"))
    private void audioImprovements$stop(CallbackInfo ci)
    {
        audioImprovements$removeMusicDiscSource();
    }
    
    @Inject(method = "destroy", at = @At("HEAD"))
    private void audioImprovements$destroy(CallbackInfo ci)
    {
        audioImprovements$removeMusicDiscSource();
    }
    
    @Inject(method = "setSelfPosition", at = @At("HEAD"), cancellable = true)
    private void audioImprovements$setSelfPosition(Vec3 pos, CallbackInfo ci)
    {
        this.audioImprovements$pos = pos;
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
        if (audioImprovements$pos == null) return;
        if (audioImprovements$isMono())
        {
            AL10.alSourcei(this.source, AL10.AL_SOURCE_RELATIVE, 1);
            if (!Boolean.TRUE.equals(audioImprovements$isRelativeOriginal))
            {
                Vec3 listenerPos = Minecraft.getInstance().getSoundManager().getListenerTransform().position();
                float distanceToListener = (float) listenerPos.distanceTo(audioImprovements$pos);
                AL10.alSourcefv(this.source, AL10.AL_POSITION, new float[] {0f, 0f, distanceToListener});
            }
            else AL10.alSourcefv(this.source, AL10.AL_POSITION, new float[] {(float) audioImprovements$pos.x, (float) audioImprovements$pos.y, (float) audioImprovements$pos.z});
            audioImprovements$monoBefore = true;
        }
        else if (audioImprovements$monoBefore)
        {
            AL10.alSourcei(this.source, AL10.AL_SOURCE_RELATIVE, Boolean.TRUE.equals(audioImprovements$isRelativeOriginal) ? 1 : 0);
            AL10.alSourcefv(this.source, AL10.AL_POSITION, new float[] {(float) audioImprovements$pos.x, (float) audioImprovements$pos.y, (float) audioImprovements$pos.z});
            audioImprovements$monoBefore = false;
            AudioImprovements.LOGGER.debug("Reset sound {} position to true 3D", source);
        }
    }
    
    @Override
    public void audioImprovements$setSoundType(SoundType type)
    {
        this.audioImprovements$type = type;
    }
    
    @Override
    public Vec3 audioImprovements$getPos()
    {
        return audioImprovements$pos;
    }
    
    @Unique
    private boolean audioImprovements$isMono()
    {
        return AudioImprovements.getInstance().isSoundTypeMono(audioImprovements$type);
    }
    
    @Unique
    private void audioImprovements$removeMusicDiscSource()
    {
        if (this.audioImprovements$type == SoundType.MUSIC_DISC)
        {
            AudioImprovements.getInstance().musicDiscChannels.remove((Channel)(Object)this);
            AudioImprovements.LOGGER.debug("Removed reference to music disc source {}", source);
        }
    }
}