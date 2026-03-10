package namelessju.audioimprovements.gui.screens;

import namelessju.audioimprovements.ConfigImpl;
import namelessju.audioimprovements.gui.screens.abstracts.ScrollableScreen;
import net.minecraft.client.gui.screens.Screen;

public abstract class AbstractConfigScreen extends ScrollableScreen
{
    protected final ConfigImpl config;
    
    public AbstractConfigScreen(String titleTranslationMenuId, Screen parentScreen, ConfigImpl config)
    {
        super(titleTranslationMenuId, parentScreen);
        this.config = config;
    }
    
    @Override
    public void removed()
    {
        config.save();
    }
}
