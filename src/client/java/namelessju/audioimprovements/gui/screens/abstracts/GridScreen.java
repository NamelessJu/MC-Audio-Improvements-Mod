package namelessju.audioimprovements.gui.screens.abstracts;

import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class GridScreen extends LayoutScreen
{
    public GridScreen(Component titleComponent, String translationMenuId, Screen parentScreen)
    {
        super(titleComponent, translationMenuId, parentScreen);
    }
    
    public GridScreen(String titleTranslationMenuId, Screen parentScreen)
    {
        super(titleTranslationMenuId, parentScreen);
    }
    
    @Override
    protected final void initContent()
    {
        GridLayout grid = new GridLayout();
        grid.defaultCellSetting().paddingHorizontal(4).paddingBottom(4).alignHorizontallyCenter();
        initGrid(grid);
        layout.addToContents(grid);
    }
    
    protected abstract void initGrid(GridLayout grid);
}
