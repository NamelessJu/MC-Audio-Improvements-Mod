package namelessju.audioimprovements.mixins;

import namelessju.audioimprovements.AudioImprovements;
import namelessju.audioimprovements.SoundSourceMixinAccessor;
import namelessju.audioimprovements.SoundType;
import net.minecraft.client.sound.*;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(SoundSystem.class)
public abstract class SoundSystemMixin
{
    @Redirect(
        method = "play(Lnet/minecraft/client/sound/SoundInstance;)Lnet/minecraft/client/sound/SoundSystem$PlayResult;",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
            ordinal = 1
        )
    )
    private Object audioImprovements$onSourcesPut(Map<SoundInstance, Channel.SourceManager> sources, Object soundObj, Object sourceManagerObj)
    {
        SoundInstance sound = (SoundInstance) soundObj;
        Channel.SourceManager sourceManager = (Channel.SourceManager) sourceManagerObj;
        
        sourceManager.run(source -> {
            SoundSourceMixinAccessor mixinAccessor = (SoundSourceMixinAccessor) source;
            
            if (sound.getCategory() == SoundCategory.RECORDS)
            {
                mixinAccessor.audioImprovements$setSoundType(SoundType.MUSIC_DISC);
                AudioImprovements.getInstance().musicDiscSources.add(source);
                AudioImprovements.LOGGER.debug("Music disc played");
            }
        });
        
        return sources.put(sound, sourceManager);
    }
}
