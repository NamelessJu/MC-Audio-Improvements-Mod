package namelessju.audioimprovements.mixins;

import namelessju.audioimprovements.AudioImprovementsClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicTracker.class)
public class MusicTrackerMixin
{
    @Unique
    private static final float audioImprovements$FADE_IN_SPEED = 1f/80f;
    @Unique
    private static final float audioImprovements$FADE_OUT_SPEED = 1f/40f;
    
    
    @Shadow @Final
    private MinecraftClient client;
    @Shadow @Nullable
    private SoundInstance current;
    @Shadow
    private float volume;
    
    @Unique
    private float audioImprovements$volumeMultiplier = 1f;
    
    @Inject(method = "tick", at = @At("TAIL"))
    private void audioImprovements$tick(CallbackInfo ci)
    {
        if (this.current == null) return;
        
        AudioImprovementsClient mod = AudioImprovementsClient.getInstance();
        float newVolumeMultiplier = 1f;
        if (this.client.world != null
            && mod.getConfig().fadeMusicWhenMusicDiscPlaying)
        {
            float targetMultiplier = mod.isMusicDiscPlayingNearby() ? 0f : 1f;
            newVolumeMultiplier = audioImprovements$volumeMultiplier
                + MathHelper.clamp(
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
    
    @Inject(method = "play", at = @At("TAIL"))
    private void audioImprovements$play(CallbackInfo ci)
    {
        audioImprovements$setSoundVolume();
    }
    
    @Redirect(
        method = "canFadeTowardsVolume",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/sound/SoundManager;setVolume(Lnet/minecraft/client/sound/SoundInstance;F)V",
            ordinal = 0
        )
    )
    private void audioImprovements$onSoundManagerSetVolume(SoundManager soundManager, SoundInstance current, float volume)
    {
        audioImprovements$setSoundVolume();
    }
    
    @Unique
    private void audioImprovements$setSoundVolume()
    {
        this.client.getSoundManager().setVolume(this.current, this.volume * (audioImprovements$volumeMultiplier * audioImprovements$volumeMultiplier));
        
        if (AudioImprovementsClient.LOGGER.isDebugEnabled())
        {
            AudioImprovementsClient.LOGGER.debug("Set music volume to {} (base: {}, multiplier: {})", this.volume * audioImprovements$volumeMultiplier, this.volume, this.audioImprovements$volumeMultiplier);
        }
    }
}
