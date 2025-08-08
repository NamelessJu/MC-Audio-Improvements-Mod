package namelessju.audioimprovements.common.gui;

import namelessju.audioimprovements.common.AudioImprovements;
import namelessju.audioimprovements.common.ConfigImpl;
import namelessju.audioimprovements.common.data.MusicFrequencyValue;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
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
    private CycleButton<Boolean> musicFrequencyAffectMenuButton;
    
    public ConfigScreen(Screen parent, ConfigImpl config)
    {
        super(Component.translatable(AudioImprovements.MOD_ID + ".config.title"));
        this.parent = parent;
        this.config = config;
    }
    
    @Override
    protected void init()
    {
        layout.addToHeader(new StringWidget(this.title, this.font).alignCenter());
        list = new ConfigList(minecraft, this);
        
        list.addSection(Component.translatable(AudioImprovements.MOD_ID + ".config.section.mono"));
        list.addTwoColumns(
            config.monoMusicDiscs.createButton(0, 0, 0, null),
            config.monoNoteBlocks.createButton(0, 0, 0, null)
        );
        list.addTwoColumns(
            config.monoWeather.createButton(0, 0, 0, null),
            config.monoBlocks.createButton(0, 0, 0, null)
        );
        list.addTwoColumns(
            config.monoHostile.createButton(0, 0, 0, null),
            config.monoNeutral.createButton(0, 0, 0, null)
        );
        list.addTwoColumns(
            config.monoPlayers.createButton(0, 0, 0, null),
            config.monoAmbient.createButton(0, 0, 0, null)
        );
        
        list.addSection(Component.translatable(AudioImprovements.MOD_ID + ".config.section.musicDiscs"));
        IntegerSlider musicDiscDistanceSlider;
        list.addFullWidth(musicDiscDistanceSlider = config.maxDistancePercentMusicDiscs.createSlider(0, 0, 0, null));
        musicDiscDistanceSlider.setTooltip(Tooltip.create(config.maxDistancePercentMusicDiscs.getTooltipComponent()));
        musicDiscDistanceSlider.stepSize = 10;
        musicDiscDistanceSlider.setValueComponentSupplier(
            value -> {
                if (value > 300) return config.maxDistancePercentMusicDiscs.getTranslatableComponent("global");
                return Component.literal(Integer.toString(value)).append("%");
            }
        );
        
        list.addSection(Component.translatable(AudioImprovements.MOD_ID + ".config.section.musicClashPrevention"));
        list.addFullWidth(config.fadeMusicWhenMusicDiscPlaying.createButton(0, 0, 0, null));
        list.addFullWidth(config.fadeMusicWhenNoteBlockPlaying.createButton(0, 0, 0, null));
        IntegerSlider musicFadeOutSlider, musicFadeInSlider;
        list.addTwoColumns(
            musicFadeOutSlider = config.musicFadeOutSeconds.createSlider(0, 0, 0, null),
            musicFadeInSlider = config.musicFadeInSeconds.createSlider(0, 0, 0, null)
        );
        Slider.ValueComponentProvider<Integer> musicFadeComponentSupplier = value -> Component.empty()
            .append(Integer.toString(value))
            .append(" ")
            .append(Component.translatable("audioimprovements.time.seconds"));
        musicFadeOutSlider.setValueComponentSupplier(musicFadeComponentSupplier);
        musicFadeInSlider.setValueComponentSupplier(musicFadeComponentSupplier);
        
        list.addSection(Component.translatable(AudioImprovements.MOD_ID + ".config.section.music"));
        list.addFullWidth(config.preventMusicRepeat.createButton(0, 0, 0, null));
        
        list.addSection(Component.translatable(AudioImprovements.MOD_ID + ".config.section.musicFrequency"));
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
        
        this.addWidget(this.list);
        
        layout.addToFooter(WidgetFactory.buildDoneButton(this));
        
        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }
    
    private void updateMusicFrequencyWidgets()
    {
        boolean enabled = config.customMusicFrequency.isEnabled;
        musicFrequencyMinSlider.active = enabled;
        musicFrequencyMaxSlider.active = enabled;
        musicFrequencyAffectMenuButton.active = enabled;
    }
    
    @Override
    protected void repositionElements()
    {
        this.layout.arrangeElements();
        
        if (list != null) list.updateSize();
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f)
    {
        this.renderBackground(guiGraphics);
        list.render(guiGraphics, i, j, f);
        super.render(guiGraphics, i, j, f);
    }
    
    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onClose()
    {
        minecraft.setScreen(parent);
    }
    
    @Override
    public void removed()
    {
        config.save();
    }
}
