package namelessju.audioimprovements.mixins;

import namelessju.audioimprovements.AudioImprovements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin
{
    @Inject(method = "handleMovePlayer", at = @At("HEAD"))
    private void audioImprovements$onPlayerTeleport(ClientboundPlayerPositionPacket packet, CallbackInfo ci)
    {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && !player.isPassenger())
        {
            AudioImprovements.getInstance().skipNextListenerDopplerVelocityUpdate = true;
            AudioImprovements.LOGGER.debug("Player teleportation detected");
        }
    }
}
