package namelessju.audioimprovements.gui.screens.abstracts;

import namelessju.audioimprovements.AudioImprovements;
import namelessju.audioimprovements.gui.components.WidgetFactory;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Function;

public abstract class BaseScreen extends Screen
{
    public static MutableComponent getScreenTranslatableComponent(String translationSubKey)
    {
        return Component.translatable(
            AudioImprovements.MOD_ID + ".screen." + translationSubKey
        );
    }
    
    protected Screen parentScreen;
    private final String translationMenuId;
    
    public BaseScreen(String translationMenuId, Screen parentScreen)
    {
        this(
            Component.literal(AudioImprovements.MOD_NAME + " - ")
                .append(getScreenTranslatableComponent(translationMenuId + ".title")),
            translationMenuId,
            parentScreen
        );
    }
    
    public BaseScreen(Component titleComponent, String translationMenuId, Screen parentScreen)
    {
        super(titleComponent);
        this.parentScreen = parentScreen;
        this.translationMenuId = translationMenuId;
    }
    
    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onClose()
    {
        minecraft.setScreen(parentScreen);
    }
    
    protected final MutableComponent getTranslatableComponent(String subKey)
    {
        return getScreenTranslatableComponent(translationMenuId + "." + subKey);
    }
    
    protected Button buildSubScreenButton(String subMenuTranslationId, Function<Screen, Screen> subScreenFactory)
    {
        return buildSubScreenButton(0, 0, 150, 20, subMenuTranslationId, subScreenFactory);
    }
    
    protected Button buildSubScreenButton(int x, int y, int width, int height,
                                          String subMenuTranslationId, Function<Screen, Screen> subScreenFactory)
    {
        return WidgetFactory.buildSubScreenButton(
            x, y, width, height,
            getTranslatableComponent("subScreenButton." + subMenuTranslationId),
            this, subScreenFactory
        );
    }
}
