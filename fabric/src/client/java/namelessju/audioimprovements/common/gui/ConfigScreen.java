package namelessju.audioimprovements.common.gui;

import namelessju.audioimprovements.common.AudioImprovements;
import namelessju.audioimprovements.common.ConfigImpl;
import namelessju.audioimprovements.common.data.MusicFrequencyValue;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen
{
    private final Screen parent;
    private final ConfigImpl config;
    
    public final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private ConfigList list;
    
    private ValueListSlider<MusicFrequencyValue> musicFrequencyMinSlider;
    private ValueListSlider<MusicFrequencyValue> musicFrequencyMaxSlider;
    
    public ConfigScreen(Screen parent, ConfigImpl config)
    {
        super(Component.translatable(AudioImprovements.MOD_ID + ".config.title"));
        this.parent = parent;
        this.config = config;
    }
    
    @Override
    protected void init()
    {
        layout.addTitleHeader(this.title, this.font);
        list = layout.addToContents(new ConfigList(minecraft, this));
        
        list.addHeader(Component.translatable(AudioImprovements.MOD_ID + ".config.header.mono"));
        list.addFullWidth(config.monoMusicDiscs.createWidget(0, 0, 0, null));
        list.addFullWidth(config.monoOther.createWidget(0, 0, 0, null));
        
        list.addHeader(Component.translatable(AudioImprovements.MOD_ID + ".config.header.music"));
        list.addTwoColumns(
            config.preventMusicClash.createWidget(0, 0, 0, null),
            config.preventMusicRepeat.createWidget(0, 0, 0, null)
        );
        
        list.addFullWidth(config.overrideMusicFrequency.createWidget(0, 0, 0,
            enabled -> updateMusicFrequencySliders()
        ));
        
        musicFrequencyMinSlider = MusicFrequencyValue.createConfigSlider(config.musicFrequencyMinTicks,
            (index, value) -> {
                if (value.ticks > config.musicFrequencyMaxTicks.value)
                {
                    config.musicFrequencyMaxTicks.value = value.ticks;
                    musicFrequencyMaxSlider.setIndex(index);
                }
            }
        );
        musicFrequencyMaxSlider = MusicFrequencyValue.createConfigSlider(config.musicFrequencyMaxTicks,
            (index, value) -> {
                if (value.ticks < config.musicFrequencyMinTicks.value)
                {
                    config.musicFrequencyMinTicks.value = value.ticks;
                    musicFrequencyMinSlider.setIndex(index);
                }
            }
        );
        updateMusicFrequencySliders();
        list.addFullWidth(musicFrequencyMinSlider);
        list.addFullWidth(musicFrequencyMaxSlider);
        
        layout.addToFooter(WidgetFactory.buildDoneButton(this));
        
        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }
    
    private void updateMusicFrequencySliders()
    {
        musicFrequencyMinSlider.active = config.overrideMusicFrequency.isEnabled;
        musicFrequencyMaxSlider.active = musicFrequencyMinSlider.active;
    }
    
    @Override
    protected void repositionElements()
    {
        this.layout.arrangeElements();
        
        if (list != null) list.updateSize(width, layout);
    }
    
    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onClose()
    {
        if (list != null) list.applyUnsavedChanges();
        
        minecraft.setScreen(parent);
    }
    
    @Override
    public void removed()
    {
        config.save();
    }
}
