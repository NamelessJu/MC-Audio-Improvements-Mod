package namelessju.audioimprovements.common.config;

import com.google.gson.JsonElement;
import namelessju.audioimprovements.common.AudioImprovements;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

public abstract class ConfigEntry
{
    protected final Config config;
    
    public final String key;
    
    boolean hasTooltip = false;
    
    protected ConfigEntry(Config config, String key)
    {
        this.config = config;
        this.key = key;
    }
    
    public MutableComponent getTranslatableComponent(@Nullable String subKey)
    {
        String translationKey = AudioImprovements.MOD_ID + ".config.option." + key;
        if (subKey != null) translationKey += "." + subKey;
        return Component.translatable(translationKey);
    }
    
    public MutableComponent getNameComponent()
    {
        return getTranslatableComponent(null);
    }
    
    public MutableComponent getTooltipComponent()
    {
        if (!hasTooltip) return null;
        return getTranslatableComponent("tooltip");
    }
    
    public <T> OptionInstance.TooltipSupplier<T> getTooltipSupplier()
    {
        return unused -> {
            Component tooltipComponent = getTooltipComponent();
            return tooltipComponent != null ? Tooltip.create(tooltipComponent) : null;
        };
    }
    
    public abstract void reset();
    
    abstract boolean loadFromJsonElement(JsonElement element);
    
    abstract JsonElement saveToJsonElement();
    
    public abstract AbstractWidget createWidget(int x, int y, int width);
}