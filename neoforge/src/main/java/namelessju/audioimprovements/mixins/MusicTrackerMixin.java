package namelessju.audioimprovements.mixins;

import namelessju.audioimprovements.AudioImprovements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicInfo;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicManager.class)
public class MusicTrackerMixin
{
    @Unique
    private static final float audioImprovements$FADE_IN_SPEED = 1f/80f;
    @Unique
    private static final float audioImprovements$FADE_OUT_SPEED = 1f/40f;
    
    
    @Shadow @Final
    private Minecraft minecraft;
    @Shadow @Nullable
    private SoundInstance currentMusic;
    @Shadow
    private float currentGain;
    
    @Unique
    private float audioImprovements$volumeMultiplier = 1f;
    
    @Inject(method = "tick", at = @At("TAIL"))
    private void audioImprovements$tick(CallbackInfo ci)
    {
        if (this.currentMusic == null) return;
        
        AudioImprovements mod = AudioImprovements.getInstance();
        float newVolumeMultiplier = 1f;
        if (this.minecraft.level != null
            && mod.getConfig().fadeMusicWhenMusicDiscPlaying)
        {
            float targetMultiplier = mod.isMusicDiscPlayingNearby() ? 0f : 1f;
            newVolumeMultiplier = audioImprovements$volumeMultiplier
                + Math.clamp(
                    targetMultiplier - audioImprovements$volumeMultiplier,
                    -audioImprovements$FADE_OUT_SPEED,
                    audioImprovements$FADE_IN_SPEED
                );
        }
        
        if (newVolumeMultiplier != audioImprovements$volumeMultiplier)
        {
            audioImprovements$volumeMultiplier = newVolumeMultiplier;
            audioImprovements$setSoundVolume();
        }
    }
    
    @Inject(method = "startPlaying", at = @At("TAIL"))
    private void audioImprovements$play(MusicInfo music, CallbackInfo ci)
    {
        audioImprovements$setSoundVolume();
    }
    
    @Redirect(
        method = "fadePlaying",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/sounds/SoundManager;setVolume(Lnet/minecraft/client/resources/sounds/SoundInstance;F)V",
            ordinal = 0
        )
    )
    private void audioImprovements$onSoundManagerSetVolume(SoundManager soundManager, SoundInstance current, float volume)
    {
        audioImprovements$setSoundVolume();
    }
    
    @Unique
    @SuppressWarnings("null")
    private void audioImprovements$setSoundVolume()
    {
        if (this.currentMusic == null) return;
        this.minecraft.getSoundManager().setVolume(this.currentMusic, this.currentGain * (audioImprovements$volumeMultiplier * audioImprovements$volumeMultiplier));
        
        if (AudioImprovements.LOGGER.isDebugEnabled())
        {
            AudioImprovements.LOGGER.debug("Set music currentGain to {} (base: {}, multiplier: {})", this.currentGain * audioImprovements$volumeMultiplier, this.currentGain, this.audioImprovements$volumeMultiplier);
        }
    }
}
