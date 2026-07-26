package namelessju.audioimprovements.mixinaccessors;

import namelessju.audioimprovements.data.SoundChannelType;
import net.minecraft.world.phys.Vec3;

public interface SoundChannelMixinAccessor
{
    void audioImprovements$setSoundType(SoundChannelType type);
    SoundChannelType audioImprovements$getSoundType();
    Vec3 audioImprovements$getPos();
    float audioImprovements$getMaxDistance();
    
    void audioImprovements$setOriginalAttenuation(float value);
    void audioImprovements$afterSetLinearAttenuation();
}
