package namelessju.audioimprovements.common.gui;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConfigList extends ContainerObjectSelectionList<ConfigList.Entry>
{
    private final ConfigScreen screen;
    
    public ConfigList(Minecraft minecraft, ConfigScreen screen)
    {
        super(minecraft, 0, 0, 0, 0, 25);
        this.screen = screen;
        updateSize();
        this.centerListVertically = false;
    }
    
    public void updateSize()
    {
        this.updateSize(screen.width, screen.height, 32, screen.height - 32);
        this.setScrollAmount(getScrollAmount());
    }
    
    public void addFullWidth(@NotNull AbstractWidget widget)
    {
        widget.setWidth(getRowWidth());
        Entry entry = new Entry(List.of(widget), screen);
        entry.yOffset = widget.getY();
        addEntry(entry);
    }
    
    public void addTwoColumns(@NotNull AbstractWidget widgetLeft, @Nullable AbstractWidget widgetRight)
    {
        List<AbstractWidget> widgetList;
        widgetLeft.setWidth(150);
        if (widgetRight != null)
        {
            widgetRight.setWidth(150);
            widgetList = List.of(widgetLeft, widgetRight);
        }
        else widgetList = List.of(widgetLeft);
        addEntry(new Entry(widgetList, screen));
    }
    
    public void addSection(Component component)
    {
        addFullWidth(new StringWidget(0, 25 / 2 - 9 / 2 - 1, 0, 9, component, minecraft.font).alignCenter());
    }
    
    @Override
    public int getRowWidth()
    {
        return 310;
    }
    
    @Override
    protected int getScrollbarPosition()
    {
        return super.getScrollbarPosition() + 32;
    }
    
    public static class Entry extends ContainerObjectSelectionList.Entry<Entry>
    {
        protected final List<AbstractWidget> children;
        private final Screen screen;
        public int yOffset = 0;
        
        public Entry(List<AbstractWidget> children, Screen screen)
        {
            this.children = ImmutableList.copyOf(children);
            this.screen = screen;
        }
        
        @Override
        public @NotNull List<? extends GuiEventListener> children()
        {
            return children;
        }
        
        @Override
        public @NotNull List<? extends NarratableEntry> narratables()
        {
            return children;
        }
        
        @Override
        public void render(GuiGraphics guiGraphics, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f)
        {
            int x = this.screen.width / 2 - 155;
            for (AbstractWidget abstractWidget : this.children)
            {
                abstractWidget.setPosition(x, j + yOffset);
                abstractWidget.render(guiGraphics, n, o, f);
                x += abstractWidget.getWidth() + 10;
            }
        }
    }
}
