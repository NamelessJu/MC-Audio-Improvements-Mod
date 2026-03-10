package namelessju.audioimprovements.gui.screens.abstracts;

import namelessju.audioimprovements.gui.components.WidgetFactory;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class LayoutScreen extends BaseScreen
{
    protected final HeaderAndFooterLayout layout;
    
    public LayoutScreen(Component titleComponent, String translationMenuId, Screen parentScreen)
    {
        super(titleComponent, translationMenuId, parentScreen);
        this.layout = new HeaderAndFooterLayout(this);
    }
    
    public LayoutScreen(String titleTranslationMenuId, Screen parentScreen)
    {
        super(titleTranslationMenuId, parentScreen);
        this.layout = new HeaderAndFooterLayout(this);
    }
    
    @Override
    protected final void init()
    {
        layout.addToHeader(createTitleWidget(), layout.newHeaderLayoutSettings().alignHorizontallyCenter());
        initContent();
        initFooter();
        
        layout.visitWidgets(this::addRenderableWidget);
        repositionElements();
    }
    
    protected abstract void initContent();
    
    protected void initFooter()
    {
        layout.addToFooter(WidgetFactory.buildDoneButton(this));
    }
    
    @Override
    protected void repositionElements()
    {
        this.layout.arrangeElements();
    }
    
    protected StringWidget createTitleWidget()
    {
        return new StringWidget(title, font);
    }
}
