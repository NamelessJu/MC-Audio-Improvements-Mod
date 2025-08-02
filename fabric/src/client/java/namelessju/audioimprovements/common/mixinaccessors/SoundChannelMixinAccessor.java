package namelessju.audioimprovements.common.mixinaccessors;

import namelessju.audioimprovements.common.SoundType;
import net.minecraft.world.phys.Vec3;

public interface SoundChannelMixinAccessor
{
    void audioImprovements$setSoundType(SoundType type);
    Vec3 audioImprovements$getPos();
}
