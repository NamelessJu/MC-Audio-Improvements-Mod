//? fabric {
package namelessju.audio_improvements.platform.fabric;

import namelessju.audio_improvements.AudioImprovements;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.spongepowered.asm.mixin.Mixins;

public class FabricClientPreLaunch implements PreLaunchEntrypoint
{
    @Override
    public void onPreLaunch()
    {
        AudioImprovements.LOGGER.info("Running Fabric pre launch");

        if (FabricLoader.getInstance().isModLoaded("vinurl"))
        {
            AudioImprovements.LOGGER.info("VinURL is installed, adding mixin config");
            Mixins.addConfiguration("audio_improvements.mixins.vinurl.json");
        }
    }
}
//? }
