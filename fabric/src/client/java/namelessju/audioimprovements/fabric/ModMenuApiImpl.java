package namelessju.audioimprovements.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import namelessju.audioimprovements.common.AudioImprovements;

public class ModMenuApiImpl implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return parent -> AudioImprovements.getInstance().createConfigScreen(parent);
    }
}
