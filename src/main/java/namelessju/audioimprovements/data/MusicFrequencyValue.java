package namelessju.audioimprovements.data;

import namelessju.audioimprovements.config.IntegerEntry;
import namelessju.audioimprovements.gui.components.slider.ValueListSlider;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class MusicFrequencyValue
{
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
    
    public static final Function<MusicFrequencyValue, Component> VALUE_COMPONENT_PROVIDER
        = value -> value.valueComponent;
    
    public static ValueListSlider<MusicFrequencyValue> createConfigSlider(IntegerEntry configEntry, @Nullable BiConsumer<Integer, MusicFrequencyValue> onValueChanged)
    {
        ValueListSlider<MusicFrequencyValue> slider = new ValueListSlider<>(0, 0, 0, 20,
            configEntry.getNameComponent(), VALUE_COMPONENT_PROVIDER,
            VALUES, ValueListSlider.getClosestValidValueFromNumber(VALUES, configEntry.getValue(), value -> value.ticks),
            (index, value) -> {
                configEntry.setValue(value.ticks);
                if (onValueChanged != null) onValueChanged.accept(index, value);
            }
        );
        Component tooltipComponent = configEntry.getTooltipComponent();
        if (tooltipComponent != null) slider.setTooltip(Tooltip.create(tooltipComponent));
        return slider;
    }
    
    
    public final int ticks;
    private final Component valueComponent;
    
    private MusicFrequencyValue(int value, boolean isMinutes)
    {
        ticks = value * 20 * (isMinutes ? 60 : 1);
        String translationKey = "audioimprovements.unit." + (
            isMinutes ? (value == 1 ? "minute" : "minutes")
            : (value == 1 ? "second" : "seconds")
        );
        this.valueComponent = Component.translatable(translationKey, value);
    }
}
