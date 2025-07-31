package namelessju.audioimprovements.mixins;

import namelessju.audioimprovements.AudioImprovements;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin
{
    @Inject(method="disconnect", at=@At("TAIL"))
    private void audioImprovements$tick(Text reason, CallbackInfo ci)
    {
        AudioImprovements.getInstance().musicDiscSources.clear();
        AudioImprovements.LOGGER.debug("Disconnected from world");
    }
}
