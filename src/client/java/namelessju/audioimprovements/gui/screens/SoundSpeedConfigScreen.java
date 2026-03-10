package namelessju.audioimprovements.gui.screens;

import namelessju.audioimprovements.AudioImprovements;
import namelessju.audioimprovements.ConfigImpl;
import namelessju.audioimprovements.config.IntegerEntry;
import namelessju.audioimprovements.gui.components.GuiList;
import namelessju.audioimprovements.gui.components.slider.ValueListSlider;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class SoundSpeedConfigScreen extends AbstractConfigScreen
{
    private final Integer[] soundSpeedValues = new Integer[] {
        0, 25, 50, 75, 100, 125, 150, 175, 200, 225, 250, 275, 300, 343
    };
    
    public SoundSpeedConfigScreen(Screen parentScreen, ConfigImpl config)
    {
        super("configSoundSpeed", parentScreen, config);
    }
    
    @Override
    protected void initList(GuiList list)
    {
        list.addFullWidth(buildSoundSpeedSlider(config.soundSpeedThunder));
        list.addFullWidth(buildSoundSpeedSlider(config.soundSpeedExplosions));
        list.addFullWidth(buildSoundSpeedSlider(config.soundSpeedOther));
    }
    
    private ValueListSlider<Integer> buildSoundSpeedSlider(IntegerEntry configEntry)
    {
        ValueListSlider<Integer> slider = new ValueListSlider<>(0, 0, 0, 20,
            configEntry.getNameComponent(), soundSpeedValueSupplier,
            soundSpeedValues, ValueListSlider.getClosestValidValueFromNumber(soundSpeedValues, configEntry.getValue(), value -> value),
            (index, value) -> configEntry.setValue(value)
        );
        Component tooltipComponent = configEntry.getTooltipComponent();
        if (tooltipComponent != null) slider.setTooltip(Tooltip.create(tooltipComponent));
        return slider;
    }
    
    private final Function<Integer, Component> soundSpeedValueSupplier = value -> {
        if (value <= 0) return CommonComponents.OPTION_OFF;
        
        Component valueComponent = Component.translatable(
            AudioImprovements.MOD_ID + ".unit." + (value == 1 ? "blockPerSecond" : "blocksPerSecond"),
            value
        );
        if (value == 343) valueComponent = Component.translatable(
            AudioImprovements.MOD_ID + ".config.option.soundSpeed.realistic",
            valueComponent
        );
        return valueComponent;
    };
}
