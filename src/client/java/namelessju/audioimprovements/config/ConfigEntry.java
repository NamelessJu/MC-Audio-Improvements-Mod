package namelessju.audioimprovements.config;

import com.google.gson.JsonObject;
import namelessju.audioimprovements.AudioImprovements;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public abstract class ConfigEntry<T extends ConfigEntry<T>> implements IJsonSavable
{
    public final String key;
    private boolean hasTooltip = false;
    @Nullable
    private Predicate<JsonObject> formatUpdater = null;
    
    protected ConfigEntry(Config config, String key)
    {
        this.key = key;
        config.addEntry(this);
    }
    
    protected abstract T getThis();
    
    public T withTooltip()
    {
        hasTooltip = true;
        return getThis();
    }
    
    public T withUpdater(Predicate<JsonObject> formatUpdater)
    {
        this.formatUpdater = formatUpdater;
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
    
    public boolean updateFormat(JsonObject jsonObject)
    {
        if (formatUpdater != null) return formatUpdater.test(jsonObject);
        return false;
    }
    
    public abstract void reset();
}