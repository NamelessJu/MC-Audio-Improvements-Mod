package namelessju.audioimprovements.common.mixinaccessors;

import namelessju.audioimprovements.common.data.SoundChannelType;
import net.minecraft.world.phys.Vec3;

public interface SoundChannelMixinAccessor
{
    void audioImprovements$setSoundType(SoundChannelType type);
    SoundChannelType audioImprovements$getSoundType();
    Vec3 audioImprovements$getPos();
    float audioImprovements$getAttenuation();
}
