package namelessju.audioimprovements.gui.components;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GuiList extends ContainerObjectSelectionList<GuiList.Entry>
{
    private final Screen screen;
    private final HeaderAndFooterLayout layout;

    public GuiList(Minecraft minecraft, Screen screen, HeaderAndFooterLayout layout)
    {
        super(minecraft, 0, 0, 0, 25);
        this.screen = screen;
        this.layout = layout;
        updateSize();
        this.centerListVertically = false;
    }

    public void updateSize()
    {
        this.updateSize(screen.width, layout);
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
        addFullWidth(new StringWidget(0, 25/2 - 9/2 - 1, 0, 9, component, minecraft.font));
    }

    @Override
    public int getRowWidth()
    {
        return 310;
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
        public void renderContent(GuiGraphics guiGraphics, int index, int y, boolean selected, float partialTick)
        {
            int x = this.screen.width / 2 - 155;
            int entryY = getY();
            for (AbstractWidget abstractWidget : this.children)
            {
                abstractWidget.setPosition(x, entryY + yOffset);
                abstractWidget.render(guiGraphics, index, y, partialTick);
                x += abstractWidget.getWidth() + 10;
            }
        }
    }
}
