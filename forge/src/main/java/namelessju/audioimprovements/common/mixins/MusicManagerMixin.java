package namelessju.audioimprovements.common.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import namelessju.audioimprovements.common.AudioImprovements;
import namelessju.audioimprovements.common.ConfigImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
public abstract class MusicManagerMixin
{
    @Shadow @Final
    private RandomSource random;
    @Shadow @Final
    private Minecraft minecraft;
    @Shadow @Nullable
    private SoundInstance currentMusic;
    
    @Unique
    private Music audioImprovements$music = null;
    
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
                    = volumeChange > 0f ? Math.min(volumeChange, 1f / Math.max(config.musicFadeInSeconds.getValue() * 20, 1))
                    : Math.max(volumeChange, -1f / Math.max(config.musicFadeOutSeconds.getValue() * 20, 1));
                newVolumeMultiplier = mod.musicVolumeMultiplier + volumeChange;
            }
            else newVolumeMultiplier = targetMultiplier;
        }
        
        if (newVolumeMultiplier != mod.musicVolumeMultiplier)
        {
            mod.musicVolumeMultiplier = newVolumeMultiplier;
            audioImprovements$updateSoundVolume();
        }
        
        audioImprovements$music = null;
    }
    
    @Inject(
        method = "tick",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/client/Minecraft;getSituationalMusic()Lnet/minecraft/sounds/Music;",
            ordinal = 0
        )
    )
    private void audioImprovements$captureMusic(CallbackInfo ci, @Local Music music)
    {
        audioImprovements$music = music;
    }
    
    @Redirect(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/Mth;nextInt(Lnet/minecraft/util/RandomSource;II)I",
            ordinal = 1
        )
    )
    private int audioImprovements$redirectMusicDelay1(RandomSource randomSource, int min, int max)
    {
        int customDelay = audioImprovements$getNextSongDelay(randomSource);
        return customDelay >= 0 ? customDelay : Mth.nextInt(randomSource, min, max);
    }
    
    @Redirect(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/sounds/Music;getMaxDelay()I",
            ordinal = 1
        )
    )
    private int audioImprovements$redirectMusicDelay2(Music music)
    {
        int customDelay = audioImprovements$getNextSongDelay(random);
        return customDelay >= 0 ? customDelay : music.getMaxDelay();
    }
    
    
    @Unique
    private void audioImprovements$updateSoundVolume()
    {
        minecraft.getSoundManager().updateSourceVolume(SoundSource.MUSIC, 1f);
    }
    
    @Unique
    private int audioImprovements$getNextSongDelay(RandomSource randomSource)
    {
        ConfigImpl config = AudioImprovements.getInstance().config;
        if (config.customMusicFrequency.isEnabled)
        {
            if (audioImprovements$music != null
                && audioImprovements$music.getEvent().value() == SoundEvents.MUSIC_MENU.value()
                && !config.musicFrequencyAffectMenu.isEnabled)
            {
                return -1;
            }
            
            return Mth.nextInt(randomSource,
                config.musicFrequencyMinTicks.getValue(),
                config.musicFrequencyMaxTicks.getValue()
            );
        }
        
        return -1;
    }
}
