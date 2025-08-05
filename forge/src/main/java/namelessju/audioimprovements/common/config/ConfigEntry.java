package namelessju.audioimprovements.common.config;

import namelessju.audioimprovements.common.AudioImprovements;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

public abstract class ConfigEntry<T extends ConfigEntry<T>> implements IJsonSavable
{
    protected final Config config;
    
    public final String key;
    
    private boolean hasTooltip = false;
    
    protected ConfigEntry(Config config, String key)
    {
        this.config = config;
        this.key = key;
        config.addEntry(this);
    }
    
    protected abstract T getThis();
    
    public T withTooltip()
    {
        hasTooltip = true;
        return getThis();
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
    
    public <U> OptionInstance.TooltipSupplier<U> getTooltipSupplier()
    {
        return unused -> {
            Component tooltipComponent = getTooltipComponent();
            return tooltipComponent != null ? Tooltip.create(tooltipComponent) : null;
        };
    }
    
    public abstract void reset();
}