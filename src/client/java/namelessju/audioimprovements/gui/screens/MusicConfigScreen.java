package namelessju.audioimprovements.gui.screens;

import namelessju.audioimprovements.ConfigImpl;
import namelessju.audioimprovements.data.MusicFrequencyValue;
import namelessju.audioimprovements.gui.components.GuiList;
import namelessju.audioimprovements.gui.components.slider.ValueListSlider;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;

public class MusicConfigScreen extends AbstractConfigScreen
{
    private ValueListSlider<MusicFrequencyValue> musicFrequencyMinSlider;
    private ValueListSlider<MusicFrequencyValue> musicFrequencyMaxSlider;
    private CycleButton<Boolean> musicFrequencyAffectMenuButton;
    
    public MusicConfigScreen(Screen parentScreen, ConfigImpl config)
    {
        super("configMusic", parentScreen, config);
    }
    
    @Override
    protected void initList(GuiList list)
    {
        list.addFullWidth(config.preventMusicRepeat.createButton(0, 0, 0, null));
        
        
        list.addSection(getTranslatableComponent("section.musicFrequency"));
        
        list.addFullWidth(config.customMusicFrequency.createButton(0, 0, 0,
            enabled -> updateMusicFrequencyWidgets()
        ));
        musicFrequencyMinSlider = MusicFrequencyValue.createConfigSlider(config.musicFrequencyMinTicks,
            (index, value) -> {
                if (value.ticks > config.musicFrequencyMaxTicks.getValue())
                {
                    config.musicFrequencyMaxTicks.setValue(value.ticks);
                    musicFrequencyMaxSlider.setIndex(index);
                }
            }
        );
        musicFrequencyMaxSlider = MusicFrequencyValue.createConfigSlider(config.musicFrequencyMaxTicks,
            (index, value) -> {
                if (value.ticks < config.musicFrequencyMinTicks.getValue())
                {
                    config.musicFrequencyMinTicks.setValue(value.ticks);
                    musicFrequencyMinSlider.setIndex(index);
                }
            }
        );
        list.addFullWidth(musicFrequencyMinSlider);
        list.addFullWidth(musicFrequencyMaxSlider);
        
        list.addFullWidth(musicFrequencyAffectMenuButton = config.musicFrequencyAffectMenu.createButton(0, 0, 0, null));
        
        updateMusicFrequencyWidgets();
    }
    
    private void updateMusicFrequencyWidgets()
    {
        boolean enabled = config.customMusicFrequency.value;
        musicFrequencyMinSlider.active = enabled;
        musicFrequencyMaxSlider.active = enabled;
        musicFrequencyAffectMenuButton.active = enabled;
    }
    
}
