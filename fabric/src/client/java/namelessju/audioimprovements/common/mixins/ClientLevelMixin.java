package namelessju.audioimprovements.common.mixins;

import namelessju.audioimprovements.common.AudioImprovements;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin
{
    @Inject(method="disconnect", at=@At("TAIL"))
    private void audioImprovements$disconnect(CallbackInfo ci)
    {
        AudioImprovements.getInstance().musicBlockChannels.clear();
        AudioImprovements.LOGGER.debug("Disconnected from level");
    }
}
