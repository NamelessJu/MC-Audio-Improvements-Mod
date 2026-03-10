package namelessju.audioimprovements.mixins;

import namelessju.audioimprovements.AudioImprovements;
import namelessju.audioimprovements.ConfigImpl;
import namelessju.audioimprovements.mixinaccessors.MusicManagerMixinAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicManager.class)
public abstract class MusicManagerMixin implements MusicManagerMixinAccessor
{
    @Shadow @Final
    private Minecraft minecraft;
    @Shadow @Final
    private RandomSource random;
    @Shadow @Nullable
    private SoundInstance currentMusic;
    @Shadow
    private int nextSongDelay;
    
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
                    = volumeChange > 0f ? Math.min(volumeChange, 1f/Math.max(config.musicFadeInTicks.getValue(), 1))
                    : Math.max(volumeChange, -1f/Math.max(config.musicFadeOutTicks.getValue(), 1));
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
    
    @Redirect(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/sounds/MusicManager;nextSongDelay:I",
            ordinal = 1,
            opcode = Opcodes.PUTFIELD
        )
    )
    private void audioImprovements$tickSetNextSongDelayOrd1(MusicManager musicManager, int nextSongDelay)
    {
        audioImprovements$handleTickNextSongDelayOrd1(nextSongDelay);
    }
    
    @Redirect(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/sounds/MusicManager;nextSongDelay:I",
            ordinal = 2,
            opcode = Opcodes.PUTFIELD
        )
    )
    private void audioImprovements$tickSetNextSongDelayOrd2(MusicManager musicManager, int nextSongDelay)
    {
        audioImprovements$handleTickNextSongDelayOrd2(nextSongDelay);
    }
    
    @Override @Unique
    public void audioImprovements$handleTickNextSongDelayOrd1(int newDelay)
    {
        // skip unnecessary duplicate statement for custom frequency
        if (audioImprovements$shouldApplyCustomFrequency()) return;
        nextSongDelay = newDelay;
    }
    
    @Override @Unique
    public void audioImprovements$handleTickNextSongDelayOrd2(int newDelay)
    {
        if (audioImprovements$shouldApplyCustomFrequency())
        {
            ConfigImpl config = AudioImprovements.getInstance().config;
            if ((nextSongDelay >= 0 && nextSongDelay <= config.musicFrequencyMaxTicks.getValue())
                && (currentMusic == null || nextSongDelay >= config.musicFrequencyMinTicks.getValue()))
            {
                // prevent Mojank code from heavily skewing
                // the next music towards playing early
                return;
            }
            
            nextSongDelay = audioImprovements$randomCustomSongDelay();
            AudioImprovements.LOGGER.debug("Replaced next music delay with custom random value {}", nextSongDelay);
        }
        else nextSongDelay = newDelay;
    }
    
    @Redirect(
        method = "stopPlaying()V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/sounds/MusicManager;nextSongDelay:I",
            ordinal = 0,
            opcode = Opcodes.PUTFIELD
        )
    )
    private void audioImprovements$stopPlayingSetNextSongDelay(MusicManager instance, int value)
    {
        if (audioImprovements$shouldApplyCustomFrequency()) return; // do not add 100
        nextSongDelay = value;
    }
    
    @Override @Unique
    public void audioImprovements$afterJoinLevel()
    {
        if (!AudioImprovements.getInstance().config.customMusicFrequency.value)
            return;
        
        nextSongDelay = Integer.MAX_VALUE;
        AudioImprovements.LOGGER.debug("Rerolled next music delay on joining world");
    }
    
    @Override @Unique
    public void audioImprovements$beforeDisconnect()
    {
        if (AudioImprovements.getInstance().config.musicFrequencyAffectMenu.value)
        {
            nextSongDelay = 40;
            AudioImprovements.LOGGER.debug("Reset music delay for main menu");
        }
    }
    
    @Unique
    private void audioImprovements$updateSoundVolume()
    {
        minecraft.getSoundManager().updateSourceVolume(SoundSource.MUSIC, 1f);
    }
    
    @Unique
    private boolean audioImprovements$shouldApplyCustomFrequency()
    {
        return AudioImprovements.getInstance().config.customMusicFrequency.value
            && (
                Minecraft.getInstance().level != null
                || AudioImprovements.getInstance().config.musicFrequencyAffectMenu.value
            );
    }
    
    @Unique
    private int audioImprovements$randomCustomSongDelay()
    {
        ConfigImpl config = AudioImprovements.getInstance().config;
        return Mth.nextInt(random,
            config.musicFrequencyMinTicks.getValue(),
            config.musicFrequencyMaxTicks.getValue()
        );
    }
}
