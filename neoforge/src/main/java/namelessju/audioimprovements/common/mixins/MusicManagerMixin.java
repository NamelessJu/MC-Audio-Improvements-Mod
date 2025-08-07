package namelessju.audioimprovements.common.mixins;

import namelessju.audioimprovements.common.AudioImprovements;
import namelessju.audioimprovements.common.ConfigImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MusicManager.class)
public abstract class MusicManagerMixin
{
    @Shadow @Final
    private Minecraft minecraft;
    @Shadow @Nullable
    private SoundInstance currentMusic;
    @Shadow
    private float currentGain;
    
    @Inject(method = "tick", at = @At("TAIL"))
    private void audioImprovements$tick(CallbackInfo ci)
    {
        if (this.currentMusic == null) return;
        
        AudioImprovements mod = AudioImprovements.getInstance();
        float newVolumeMultiplier = 1f;
        if (this.minecraft.player != null)
        {
            float targetMultiplier = mod.shouldFadeMusic() ? 0f : 1f;
            if (targetMultiplier != mod.musicVolumeMultiplier)
            {
                ConfigImpl config = AudioImprovements.getInstance().config;
                float volumeChange = targetMultiplier - mod.musicVolumeMultiplier;
                volumeChange
                    = volumeChange > 0f ? Math.min(volumeChange, 1f/Math.max(config.musicFadeInSeconds.getValue() * 20, 1))
                    : Math.max(volumeChange, -1f/Math.max(config.musicFadeOutSeconds.getValue() * 20, 1));
                newVolumeMultiplier = mod.musicVolumeMultiplier + volumeChange;
            }
            else newVolumeMultiplier = targetMultiplier;
        }
        
        if (newVolumeMultiplier != mod.musicVolumeMultiplier)
        {
            mod.musicVolumeMultiplier = newVolumeMultiplier;
            audioImprovements$updateSoundVolume();
        }
    }
    
    @Unique
    private void audioImprovements$updateSoundVolume()
    {
        minecraft.getSoundManager().setVolume(currentMusic, currentGain);
    }
    
    
    @Mixin(MusicManager.MusicFrequency.class)
    public static abstract class MusicFrequencyMixin
    {
        @Inject(method = "getNextSongDelay", at = @At("HEAD"), cancellable = true)
        private void audioImprovements$getNextSongDelay(Music music, RandomSource randomSource, CallbackInfoReturnable<Integer> cir)
        {
            ConfigImpl config = AudioImprovements.getInstance().config;
            if (config.customMusicFrequency.isEnabled)
            {
                if (music != null)
                {
                    if (music.event().value() == SoundEvents.MUSIC_MENU.value()
                        && !config.musicFrequencyAffectMenu.isEnabled)
                    {
                        return;
                    }
                }
                
                cir.setReturnValue(Mth.nextInt(randomSource,
                    config.musicFrequencyMinTicks.getValue(),
                    config.musicFrequencyMaxTicks.getValue()
                ));
                cir.cancel();
            }
        }
    }
}
