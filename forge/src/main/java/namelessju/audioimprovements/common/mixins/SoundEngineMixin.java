package namelessju.audioimprovements.common.mixins;

import namelessju.audioimprovements.common.AudioImprovements;
import namelessju.audioimprovements.common.data.SoundChannelType;
import namelessju.audioimprovements.common.mixinaccessors.SoundChannelMixinAccessor;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin
{
    @Redirect(
        method = "play",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
            ordinal = 1
        )
    )
    private Object audioImprovements$onSourcesPut(Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel, Object soundObj, Object channelHandleObj)
    {
        SoundInstance soundInstance = (SoundInstance) soundObj;
        ChannelAccess.ChannelHandle channelHandle = (ChannelAccess.ChannelHandle) channelHandleObj;
        
        channelHandle.execute(source -> {
            SoundChannelMixinAccessor mixinAccessor = (SoundChannelMixinAccessor) source;
            
            String soundPath = soundInstance.getLocation().getPath();
            // ignore namespace to potentially also
            // detect music block sounds from other mods
            if (soundPath.startsWith("music_disc"))
            {
                mixinAccessor.audioImprovements$setSoundType(SoundChannelType.MUSIC_DISC);
                AudioImprovements.getInstance().musicBlockChannels.add(source);
                AudioImprovements.LOGGER.debug("Music disc played");
            }
            else if (soundPath.startsWith("block.note_block"))
            {
                mixinAccessor.audioImprovements$setSoundType(SoundChannelType.NOTE_BLOCK);
                AudioImprovements.getInstance().musicBlockChannels.add(source);
                AudioImprovements.LOGGER.debug("Note block played");
            }
            else
            {
                switch (soundInstance.getSource())
                {
                    case SoundSource.WEATHER:
                        mixinAccessor.audioImprovements$setSoundType(SoundChannelType.WEATHER);
                        break;
                    case SoundSource.BLOCKS:
                        mixinAccessor.audioImprovements$setSoundType(SoundChannelType.BLOCKS);
                        break;
                    case SoundSource.HOSTILE:
                        mixinAccessor.audioImprovements$setSoundType(SoundChannelType.HOSTILE);
                        break;
                    case SoundSource.NEUTRAL:
                        mixinAccessor.audioImprovements$setSoundType(SoundChannelType.NEUTRAL);
                        break;
                    case SoundSource.PLAYERS:
                        mixinAccessor.audioImprovements$setSoundType(SoundChannelType.PLAYERS);
                        break;
                    case SoundSource.AMBIENT:
                        mixinAccessor.audioImprovements$setSoundType(SoundChannelType.AMBIENT);
                        break;
                    
                    case SoundSource.MUSIC:
                        Sound sound = soundInstance.getSound();
                        if (sound != null)
                        {
                            AudioImprovements.getInstance().lastPlayedMusic = sound.getLocation();
                            AudioImprovements.LOGGER.debug("Played music \"{}\"", AudioImprovements.getInstance().lastPlayedMusic);
                        }
                        break;
                }
            }
        });
        
        return instanceToChannel.put(soundInstance, channelHandle);
    }
}
