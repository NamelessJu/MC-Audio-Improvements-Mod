package namelessju.audio_improvements.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import namelessju.audio_improvements.AudioImprovements;
import namelessju.audio_improvements.Config;
import namelessju.audio_improvements.mixinaccessors.MusicManagerMixinAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public abstract class MusicManagerMixin implements MusicManagerMixinAccessor
{
    @Shadow @Final
    private Minecraft minecraft;
    @Shadow @Final
    private RandomSource random;
    @Shadow
    private SoundInstance currentMusic;
    @Shadow
    private int nextSongDelay;

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci)
    {
        AudioImprovements mod = AudioImprovements.instance();
        float newVolumeMultiplier = 1f;
        if (this.minecraft.player != null)
        {
            float targetMultiplier = mod.shouldFadeMusic() ? 0f : 1f;
            if (!Mth.equal(targetMultiplier, mod.musicVolumeMultiplier))
            {
                Config config = AudioImprovements.config();
                float volumeChange = targetMultiplier - mod.musicVolumeMultiplier;
                volumeChange
                    = volumeChange > 0f ? Math.min(volumeChange, 1f/Math.max(config.musicFadeInTicks, 1))
                    : Math.max(volumeChange, -1f/Math.max(config.musicFadeOutTicks, 1));
                newVolumeMultiplier = mod.musicVolumeMultiplier + volumeChange;
            }
            else newVolumeMultiplier = targetMultiplier;
        }

        if (!Mth.equal(newVolumeMultiplier, mod.musicVolumeMultiplier))
        {
            mod.musicVolumeMultiplier = newVolumeMultiplier;
            AudioImprovements.LOGGER.debug("Updated music volume multiplier to {}", newVolumeMultiplier);
            audioImprovements$updateSoundVolume();
        }
    }

    @Redirect(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/sounds/MusicManager;nextSongDelay:I",
            //? if >= 1.21.1 {
                //? if neoforge {
                /*ordinal = 3, // stupid fucking event hooks altering the actual code
                *///? } else {
                ordinal = 2,
                //? }
            //? } else {
                /*ordinal = 1,
            *///? }
            opcode = Opcodes.PUTFIELD
        )
    )
    private void tickSetNextSongDelayOnCurrentMusicCleared(MusicManager musicManager, int nextSongDelay)
    {
        // skip unnecessary duplicate statement for custom frequency
        if (audioImprovements$shouldApplyCustomFrequency()) return;
        this.nextSongDelay = nextSongDelay;
    }

    @Redirect(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/sounds/MusicManager;nextSongDelay:I",
            //? if >= 1.21.1 {
                //? if neoforge {
                /*ordinal = 4, // stupid fucking event hooks altering the actual code
                *///? } else {
                ordinal = 3,
                //? }
            //? } else {
                /*ordinal = 2,
            *///? }
            opcode = Opcodes.PUTFIELD
        )
    )
    private void tickSetNextSongDelayTick(MusicManager musicManager, int nextSongDelay)
    {
        if (audioImprovements$shouldApplyCustomFrequency())
        {
            Config config = AudioImprovements.config();
            if ((this.nextSongDelay >= 0 && this.nextSongDelay <= config.musicFrequencyMaxTicks)
                && (currentMusic == null || this.nextSongDelay >= config.musicFrequencyMinTicks))
            {
                // prevent Mojank code from heavily skewing
                // the next music towards playing early
                return;
            }

            this.nextSongDelay = audioImprovements$randomCustomSongDelay();
            AudioImprovements.LOGGER.debug("Replaced next music delay with custom random value {}", this.nextSongDelay);
        }
        else this.nextSongDelay = nextSongDelay;
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
    private void stopPlayingSetNextSongDelay(MusicManager instance, int value)
    {
        if (audioImprovements$shouldApplyCustomFrequency())
        {
            nextSongDelay = -1;
            return;
        }

        nextSongDelay = value;
    }

    @Override @Unique
    public void audioImprovements$afterJoinLevel()
    {
        if (!AudioImprovements.config().customMusicFrequencyEnabled) return;

        nextSongDelay = Integer.MAX_VALUE;
        AudioImprovements.LOGGER.debug("Rerolled next music delay on joining world");
    }

    @Override @Unique
    public void audioImprovements$beforeDisconnect()
    {
        if (AudioImprovements.config().musicFrequencyAffectMenu)
        {
            nextSongDelay = 40;
            AudioImprovements.LOGGER.debug("Reset music delay for main menu");
        }
    }

    @Unique
    private void audioImprovements$updateSoundVolume()
    {
        //? if > 1.21.7 {
        minecraft.getSoundManager().updateCategoryVolume(SoundSource.MUSIC, 1f);
        //? } else {
        /*minecraft.getSoundManager().updateSourceVolume(SoundSource.MUSIC, 1f);
        *///? }
    }

    @Unique
    private boolean audioImprovements$shouldApplyCustomFrequency()
    {
        return AudioImprovements.config().customMusicFrequencyEnabled
            && (
                Minecraft.getInstance().level != null
                || AudioImprovements.config().musicFrequencyAffectMenu
            );
    }

    @Unique
    private int audioImprovements$randomCustomSongDelay()
    {
        Config config = AudioImprovements.config();
        return Mth.nextInt(random,
            config.musicFrequencyMinTicks,
            config.musicFrequencyMaxTicks
        );
    }
}
