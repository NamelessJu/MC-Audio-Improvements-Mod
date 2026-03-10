package namelessju.audioimprovements.gui.screens.abstracts;

import namelessju.audioimprovements.gui.components.GuiList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public abstract class ScrollableScreen extends LayoutScreen
{
    @Nullable
    private GuiList list = null;
    
    public ScrollableScreen(Component titleComponent, String translationMenuId, Screen parentScreen)
    {
        super(titleComponent, translationMenuId, parentScreen);
    }
    
    public ScrollableScreen(String titleTranslationMenuId, Screen parentScreen)
    {
        super(titleTranslationMenuId, parentScreen);
    }
    
    @Override
    protected final void initContent()
    {
        list = new GuiList(minecraft, this, layout);
        initList(list);
        addWidget(list);
    }
    
    protected abstract void initList(GuiList list);
    
    @Override
    protected void repositionElements()
    {
        super.repositionElements();
        if (list != null) list.updateSize();
    }
    
    @Override
    protected void renderBehindWidgets(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
    {
        if (list != null) list.render(guiGraphics, mouseX, mouseY, partialTicks);
    }
}
