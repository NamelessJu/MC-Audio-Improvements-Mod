package namelessju.audioimprovements.gui.screens;

import namelessju.audioimprovements.AudioImprovements;
import namelessju.audioimprovements.ConfigImpl;
import namelessju.audioimprovements.gui.components.slider.FloatSlider;
import namelessju.audioimprovements.gui.screens.abstracts.GridScreen;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class MainConfigScreen extends GridScreen
{
    private final ConfigImpl config;
    
    public MainConfigScreen(Screen parentScreen, ConfigImpl config)
    {
        super("config", parentScreen);
        this.config = config;
    }
    
    @Override
    @SuppressWarnings("DataFlowIssue")
    protected void initGrid(GridLayout grid)
    {
        GridLayout.RowHelper rowHelper = grid.createRowHelper(2);
        rowHelper.addChild(buildSubScreenButton("monoAudio",
            parentScreen -> new MonoAudioConfigScreen(parentScreen, config)));
        rowHelper.addChild(buildSubScreenButton("musicBlocks",
            parentScreen -> new MusicBlocksConfigScreen(parentScreen, config)));
        rowHelper.addChild(buildSubScreenButton("music",
            parentScreen -> new MusicConfigScreen(parentScreen, config)));
        rowHelper.addChild(buildSubScreenButton("soundSpeed",
            parentScreen -> new SoundSpeedConfigScreen(parentScreen, config)));
        
        FloatSlider dopplerEffectIntensitySlider = config.dopplerEffectIntensity.createSlider(0, 0, 310,
            value -> AudioImprovements.getInstance().updateDopplerEffect()
        );
        rowHelper.addChild(dopplerEffectIntensitySlider, 2, rowHelper.newCellSettings().paddingTop(8));
        dopplerEffectIntensitySlider.stepSize = 0.1f;
        dopplerEffectIntensitySlider.setValueComponentSupplier(
            value -> value == 0f ? CommonComponents.OPTION_OFF
                : Component.translatable("audioimprovements.gui.percentageFormat", Math.round(value * 100))
        );
        
        rowHelper.addChild(
            config.stereoSpatializationFix.createButton(0, 0, 310,
                value -> {
                    // Minecraft caches sound files -> we need to reload the sound engine to
                    // make it load the sounds again so the new setting value takes effect
                    
                    minecraft.getSoundManager().reload();
                }
            ), 2, rowHelper.newCellSettings().paddingTop(8)
        );
    }
    
    @Override
    public void removed()
    {
        config.save();
    }
}
