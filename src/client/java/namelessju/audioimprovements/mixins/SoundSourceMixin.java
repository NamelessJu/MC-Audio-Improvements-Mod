package namelessju.audioimprovements.mixins;

import namelessju.audioimprovements.AudioImprovementsClient;
import namelessju.audioimprovements.SoundSourceMixinAccessor;
import namelessju.audioimprovements.SoundType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.Source;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.openal.AL10;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Source.class)
public abstract class SoundSourceMixin implements SoundSourceMixinAccessor
{
    @Shadow @Final
    private int pointer;
    
    @Unique
    private boolean audioImprovements$monoBefore = false;
    @Unique @Nullable
    private Vec3d audioImprovements$pos = null;
    @Unique @Nullable
    private Boolean audioImprovements$isRelativeOriginal = null;
    @Unique
    public SoundType audioImprovements$type = SoundType.OTHER;
    
    @Inject(method = "stop", at = @At("HEAD"))
    private void audioImprovements$stop(CallbackInfo ci)
    {
        audioImprovements$removeMusicDiscSource();
    }
    
    @Inject(method = "close", at = @At("HEAD"))
    private void audioImprovements$close(CallbackInfo ci)
    {
        audioImprovements$removeMusicDiscSource();
    }
    
    @Inject(method = "setPosition", at = @At("HEAD"), cancellable = true)
    private void audioImprovements$setPosition(Vec3d pos, CallbackInfo ci)
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
    
    @Inject(method = "tick", at = @At("HEAD"))
    private void audioImprovements$tick(CallbackInfo ci)
    {
        if (audioImprovements$pos == null) return;
        if (audioImprovements$isMono())
        {
            AL10.alSourcei(this.pointer, AL10.AL_SOURCE_RELATIVE, 1);
            if (!Boolean.TRUE.equals(audioImprovements$isRelativeOriginal))
            {
                Vec3d listenerPos = MinecraftClient.getInstance().getSoundManager().getListenerTransform().position();
                float distanceToListener = (float) listenerPos.distanceTo(audioImprovements$pos);
                AL10.alSourcefv(this.pointer, AL10.AL_POSITION, new float[] {0f, 0f, distanceToListener});
            }
            else AL10.alSourcefv(this.pointer, AL10.AL_POSITION, new float[] {(float) audioImprovements$pos.x, (float) audioImprovements$pos.y, (float) audioImprovements$pos.z});
            audioImprovements$monoBefore = true;
        }
        else if (audioImprovements$monoBefore)
        {
            AL10.alSourcei(this.pointer, AL10.AL_SOURCE_RELATIVE, Boolean.TRUE.equals(audioImprovements$isRelativeOriginal) ? 1 : 0);
            AL10.alSourcefv(this.pointer, AL10.AL_POSITION, new float[] {(float) audioImprovements$pos.x, (float) audioImprovements$pos.y, (float) audioImprovements$pos.z});
            audioImprovements$monoBefore = false;
            AudioImprovementsClient.LOGGER.debug("Reset sound {} position to true 3D", pointer);
        }
    }
    
    @Override
    public void audioImprovements$setSoundType(SoundType type)
    {
        this.audioImprovements$type = type;
    }
    
    @Override
    public @Nullable Vec3d audioImprovements$getPos()
    {
        return audioImprovements$pos;
    }
    
    @Unique
    private boolean audioImprovements$isMono()
    {
        return AudioImprovementsClient.getInstance().isSoundTypeMono(audioImprovements$type);
    }
    
    @Unique
    private void audioImprovements$removeMusicDiscSource()
    {
        if (this.audioImprovements$type == SoundType.MUSIC_DISC)
        {
            AudioImprovementsClient.getInstance().musicDiscSources.remove((Source)(Object)this);
            AudioImprovementsClient.LOGGER.debug("Removed reference to music disc source {}", pointer);
        }
    }
}