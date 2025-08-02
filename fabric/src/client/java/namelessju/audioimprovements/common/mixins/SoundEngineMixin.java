package namelessju.audioimprovements.common.mixins;

import namelessju.audioimprovements.common.AudioImprovements;
import namelessju.audioimprovements.common.SoundType;
import namelessju.audioimprovements.common.mixinaccessors.SoundChannelMixinAccessor;
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
        SoundInstance sound = (SoundInstance) soundObj;
        ChannelAccess.ChannelHandle channelHandle = (ChannelAccess.ChannelHandle) channelHandleObj;
        
        channelHandle.execute(source -> {
            SoundChannelMixinAccessor mixinAccessor = (SoundChannelMixinAccessor) source;
            
            if (sound.getSource() == SoundSource.RECORDS)
            {
                mixinAccessor.audioImprovements$setSoundType(SoundType.MUSIC_DISC);
                AudioImprovements.getInstance().musicDiscChannels.add(source);
                AudioImprovements.LOGGER.debug("Music disc played");
            }
        });
        
        return instanceToChannel.put(sound, channelHandle);
    }
}
