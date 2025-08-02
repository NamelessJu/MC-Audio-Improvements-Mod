package namelessju.audioimprovements.common.gui;

import namelessju.audioimprovements.common.AudioImprovements;
import namelessju.audioimprovements.common.ConfigImpl;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen
{
    private final Screen parent;
    private final ConfigImpl config;
    
    public final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private ConfigList list;
    
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
        
        list.addFullWidth(config.monoMusicDiscs.createWidget(0, 0, 0));
        list.addFullWidth(config.monoOther.createWidget(0, 0, 0));
        list.addFullWidth(config.preventMusicClash.createWidget(0, 0, 0));
        list.addFullWidth(config.overrideMusicFrequency.createWidget(0, 0, 0));
        
        layout.addToFooter(WidgetBuilder.buildDoneButton(this));
        
        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
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
