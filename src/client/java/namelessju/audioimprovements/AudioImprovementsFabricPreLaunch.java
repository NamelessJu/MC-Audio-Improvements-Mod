package namelessju.audioimprovements;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.spongepowered.asm.mixin.Mixins;

public class AudioImprovementsFabricPreLaunch implements PreLaunchEntrypoint
{
    @Override
    public void onPreLaunch()
    {
        if (FabricLoader.getInstance().isModLoaded("vinurl"))
        {
            Mixins.addConfiguration("audioimprovements.mixins.vinurl.json");
        }
    }
}
