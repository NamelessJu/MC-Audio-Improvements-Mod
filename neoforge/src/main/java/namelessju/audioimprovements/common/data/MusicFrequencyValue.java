package namelessju.audioimprovements.common.data;

import namelessju.audioimprovements.common.config.IntegerEntry;
import namelessju.audioimprovements.common.gui.ValueListSlider;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class MusicFrequencyValue implements ValueListSlider.Value
{
    private static final Component SUFFIX_SECONDS = Component.translatable("audioimprovements.time.seconds");
    private static final Component SUFFIX_MINUTES = Component.translatable("audioimprovements.time.minutes");
    
    public static final MusicFrequencyValue[] VALUES = new MusicFrequencyValue[] {
        new MusicFrequencyValue(0, false),
        new MusicFrequencyValue(1, false),
        new MusicFrequencyValue(2, false),
        new MusicFrequencyValue(3, false),
        new MusicFrequencyValue(4, false),
        new MusicFrequencyValue(5, false),
        new MusicFrequencyValue(6, false),
        new MusicFrequencyValue(7, false),
        new MusicFrequencyValue(8, false),
        new MusicFrequencyValue(9, false),
        new MusicFrequencyValue(10, false),
        new MusicFrequencyValue(15, false),
        new MusicFrequencyValue(20, false),
        new MusicFrequencyValue(25, false),
        new MusicFrequencyValue(30, false),
        new MusicFrequencyValue(35, false),
        new MusicFrequencyValue(40, false),
        new MusicFrequencyValue(45, false),
        new MusicFrequencyValue(50, false),
        new MusicFrequencyValue(55, false),
        new MusicFrequencyValue(1, true),
        new MusicFrequencyValue(2, true),
        new MusicFrequencyValue(3, true),
        new MusicFrequencyValue(4, true),
        new MusicFrequencyValue(5, true),
        new MusicFrequencyValue(6, true),
        new MusicFrequencyValue(7, true),
        new MusicFrequencyValue(8, true),
        new MusicFrequencyValue(9, true),
        new MusicFrequencyValue(10, true),
        new MusicFrequencyValue(15, true),
        new MusicFrequencyValue(20, true),
        new MusicFrequencyValue(25, true),
        new MusicFrequencyValue(30, true),
        new MusicFrequencyValue(35, true),
        new MusicFrequencyValue(40, true),
        new MusicFrequencyValue(45, true),
        new MusicFrequencyValue(50, true),
        new MusicFrequencyValue(55, true),
        new MusicFrequencyValue(60, true)
    };
    
    public static ValueListSlider<MusicFrequencyValue> createConfigSlider(IntegerEntry configEntry, @Nullable BiConsumer<Integer, MusicFrequencyValue> onValueChanged)
    {
        return new ValueListSlider<>(0, 0, 0, 20,
            configEntry.getNameComponent(), MusicFrequencyValue.VALUES, getClosestFromTicks(configEntry.getValue()),
            (index, value) -> {
                configEntry.setValue(value.ticks);
                if (onValueChanged != null) onValueChanged.accept(index, value);
            }
        );
    }
    
    public static MusicFrequencyValue getClosestFromTicks(int ticks)
    {
        // assumes that the VALUES array is in order!
        MusicFrequencyValue closestValue = null;
        int closestDistance = -1;
        for (MusicFrequencyValue value : VALUES)
        {
            int distance = Math.abs(value.ticks - ticks);
            if (closestDistance < 0 || distance < closestDistance)
            {
                closestDistance = distance;
                closestValue = value;
            }
            else break;
        }
        return closestValue;
    }
    
    
    public final int ticks;
    public final Component valueComponent;
    private final Component suffixComponent;
    
    private MusicFrequencyValue(int value, boolean isMinutes)
    {
        ticks = value * 20 * (isMinutes ? 60 : 1);
        this.valueComponent = Component.literal(Integer.toString(value));
        this.suffixComponent = isMinutes ? SUFFIX_MINUTES : SUFFIX_SECONDS;
    }
    
    @Override
    public @NotNull Component getValueComponent()
    {
        return valueComponent;
    }
    
    @Override
    public @Nullable Component getSuffixComponent()
    {
        return suffixComponent;
    }
}
