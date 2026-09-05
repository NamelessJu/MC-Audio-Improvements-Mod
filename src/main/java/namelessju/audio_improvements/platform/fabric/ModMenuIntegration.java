//? fabric {
package namelessju.audio_improvements.platform.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import namelessju.audio_improvements.Config;

public class ModMenuIntegration implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return parentScreen -> Config.HANDLER.instance().createScreen(parentScreen);
    }
}
//? }
