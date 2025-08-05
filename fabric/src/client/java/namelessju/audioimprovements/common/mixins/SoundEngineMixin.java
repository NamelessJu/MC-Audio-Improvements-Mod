package namelessju.audioimprovements.common.mixins;

import namelessju.audioimprovements.common.AudioImprovements;
import namelessju.audioimprovements.common.data.SoundType;
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
            
            switch (soundInstance.getSource())
            {
                case SoundSource.RECORDS:
                    mixinAccessor.audioImprovements$setSoundType(SoundType.MUSIC_DISC);
                    AudioImprovements.getInstance().musicDiscChannels.add(source);
                    AudioImprovements.LOGGER.debug("Music disc played");
                    break;
                    
                case SoundSource.MUSIC:
                    Sound sound = soundInstance.getSound();
                    if (sound != null)
                    {
                        AudioImprovements.getInstance().lastPlayedMusicLocation = sound.getLocation();
                        AudioImprovements.LOGGER.debug("Played music \"{}\"", AudioImprovements.getInstance().lastPlayedMusicLocation);
                    }
                    break;
            }
        });
        
        return instanceToChannel.put(soundInstance, channelHandle);
    }
}
