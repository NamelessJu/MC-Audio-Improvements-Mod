package namelessju.audioimprovements.gui.screens;

import namelessju.audioimprovements.ConfigImpl;
import namelessju.audioimprovements.gui.components.GuiList;
import namelessju.audioimprovements.gui.components.slider.FloatSlider;
import namelessju.audioimprovements.gui.components.slider.IntegerSlider;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class MusicBlocksConfigScreen extends AbstractConfigScreen
{
    public MusicBlocksConfigScreen(Screen parentScreen, ConfigImpl config)
    {
        super("configMusicBlocks", parentScreen, config);
    }
    
    @Override
    protected void initList(GuiList list)
    {
        list.addSection(getTranslatableComponent("section.musicClashPrevention"));
        
        list.addFullWidth(config.fadeMusicWhenMusicDiscPlaying.createButton(0, 0, 0, null));
        list.addFullWidth(config.fadeMusicWhenNoteBlockPlaying.createButton(0, 0, 0, null));
        IntegerSlider musicFadeOutSlider, musicFadeInSlider;
        list.addTwoColumns(
            musicFadeOutSlider = config.musicFadeOutTicks.createSlider(0, 0, 0, null),
            musicFadeInSlider = config.musicFadeInTicks.createSlider(0, 0, 0, null)
        );
        Function<Integer, Component> musicFadeComponentSupplier = value -> {
            int seconds = value / 20;
            return Component.translatable("audioimprovements.unit." + (seconds == 1 ? "second" : "seconds"), seconds);
        };
        musicFadeOutSlider.stepSize = 20;
        musicFadeOutSlider.setValueComponentSupplier(musicFadeComponentSupplier);
        musicFadeInSlider.stepSize = 20;
        musicFadeInSlider.setValueComponentSupplier(musicFadeComponentSupplier);
        
        
        list.addSection(getTranslatableComponent("section.musicDiscs"));
        
        FloatSlider musicDiscDistanceSlider;
        list.addFullWidth(musicDiscDistanceSlider = config.musicDiscDistanceMultiplier.createSlider(0, 0, 0, null));
        musicDiscDistanceSlider.stepSize = 0.1f;
        musicDiscDistanceSlider.setTooltip(Tooltip.create(config.musicDiscDistanceMultiplier.getTooltipComponent()));
        musicDiscDistanceSlider.setValueComponentSupplier(value -> {
            if (value > 3f) return config.musicDiscDistanceMultiplier.getTranslatableComponent("global");
            return Component.translatable("audioimprovements.gui.percentageFormat", Math.round(value * 100f));
        });
    }
}
