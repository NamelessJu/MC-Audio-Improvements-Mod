package namelessju.audioimprovements.common.mixins;

import namelessju.audioimprovements.common.AudioImprovements;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(AbstractSoundInstance.class)
public abstract class AbstractSoundInstanceMixin
{
    @Unique
    private static final int audioImprovements$MAX_REPEAT_PREVENTION_TRIES = 100;
    
    @Shadow @Final
    protected SoundSource source;
    
    @Redirect(
        method = "resolve",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/sounds/WeighedSoundEvents;getSound(Lnet/minecraft/util/RandomSource;)Lnet/minecraft/client/resources/sounds/Sound;",
            ordinal = 0
        )
    )
    private Sound audioImprovements$getSound(WeighedSoundEvents weighedSoundEvents, RandomSource randomSource)
    {
        if (source == SoundSource.MUSIC)
        {
            AudioImprovements mod = AudioImprovements.getInstance();
            if (mod.config.preventMusicRepeat.isEnabled && mod.lastPlayedMusicLocation != null)
            {
                int tries = 0;
                Sound sound;
                do sound = weighedSoundEvents.getSound(randomSource);
                while (sound != SoundManager.EMPTY_SOUND
                    && ++tries <= audioImprovements$MAX_REPEAT_PREVENTION_TRIES
                    && Objects.equals(sound.getLocation(), mod.lastPlayedMusicLocation));
                
                if (tries > 1)
                {
                    if (tries <= audioImprovements$MAX_REPEAT_PREVENTION_TRIES)
                    {
                        AudioImprovements.LOGGER.debug("Prevented playing repeat music \"{}\" x{}", mod.lastPlayedMusicLocation, tries - 1);
                    }
                    else AudioImprovements.LOGGER.warn("Ran out of tries while trying to prevent repeat music \"{}\" (exceeded {} tries)", mod.lastPlayedMusicLocation, audioImprovements$MAX_REPEAT_PREVENTION_TRIES);
                }
                
                return sound;
            }
        }
        
        return weighedSoundEvents.getSound(randomSource);
    }
}
