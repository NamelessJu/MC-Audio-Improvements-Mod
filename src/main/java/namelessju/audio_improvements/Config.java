package namelessju.audio_improvements;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionEventListener;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import namelessju.audio_improvements.data.MusicFrequencyValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

public class Config
{
    public static ConfigClassHandler<Config> HANDLER = ConfigClassHandler.createBuilder(Config.class)
            .id(AudioImprovements.id("config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                .setPath(AudioImprovements.instance().getConfigDir().resolve("audioImprovementsConfig.json"))
                .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                .build()
            )
            .build();

    private static final Config DEFAULTS = new Config();


    /*============*/
    /* Mono Audio */
    /*============*/

    @SerialEntry(value = "monoMusicDiscs")
    public boolean monoMusicDiscs = true;
    @SerialEntry(value = "monoNoteBlocks")
    public boolean monoNoteBlocks = true;
    @SerialEntry(value = "monoWeather")
    public boolean monoWeather = false;
    @SerialEntry(value = "monoBlocks")
    public boolean monoBlocks = false;
    @SerialEntry(value = "monoHostile")
    public boolean monoHostile = false;
    @SerialEntry(value = "monoNeutral")
    public boolean monoNeutral = false;
    @SerialEntry(value = "monoPlayers")
    public boolean monoPlayers = false;
    @SerialEntry(value = "monoAmbient")
    public boolean monoAmbient = false;

    /*=======*/
    /* Music */
    /*=======*/

    @SerialEntry(value = "preventMusicRepeat")
    public boolean preventMusicRepeat = true;
    @SerialEntry(value = "musicDiscDistanceMultiplier")
    public float musicDiscDistanceMultiplier = 1f;

    // Music Frequency
    @SerialEntry(value = "customMusicFrequency")
    public boolean customMusicFrequencyEnabled = false;
    @SerialEntry(value = "musicFrequencyMinTicks")
    public int musicFrequencyMinTicks = 20;
    @SerialEntry(value = "musicFrequencyMaxTicks")
    public int musicFrequencyMaxTicks = 5 * 60 * 20;
    @SerialEntry(value = "musicFrequencyAffectMenu")
    public boolean musicFrequencyAffectMenu = false;

    // Music Clash Prevention
    @SerialEntry(value = "fadeMusicWhenMusicDiscPlaying")
    public boolean fadeMusicWhenMusicDiscPlaying = true;
    @SerialEntry(value = "fadeMusicWhenNoteBlockPlaying")
    public boolean fadeMusicWhenNoteBlockPlaying = true;
    @SerialEntry(value = "musicFadeOutTicks")
    public int musicFadeOutTicks = 40;
    @SerialEntry(value = "musicFadeInTicks")
    public int musicFadeInTicks = 100;

    /*===============*/
    /* Miscellaneous */
    /*===============*/

    @SerialEntry(value = "logarithmicVolumeSliders")
    public boolean logarithmicVolumeSlidersEnabled = true;
    @SerialEntry(value = "dopplerEffectIntensity")
    public float dopplerEffectIntensity = 0f;
    @SerialEntry(value = "stereoSpatializationFix")
    public boolean stereoSpatializationFixEnabled = false;

    // Speed of Sound
    @SerialEntry(value = "soundSpeedThunder")
    public int speedOfSoundThunder = 100;
    @SerialEntry(value = "soundSpeedExplosions")
    public int speedOfSoundExplosions = 100;
    @SerialEntry(value = "soundSpeedOther")
    public int speedOfSoundOther = 0;


    private static final ValueFormatter<Integer> VALUE_FORMATTER_SECONDS =
        value -> Component.translatable(value == 1 ? "audioImprovements.format.second" : "audioImprovements.format.seconds", value);
    private static final ValueFormatter<Float> VALUE_FORMATTER_PERCENTAGE =
        value -> Component.translatable("audioImprovements.format.percentage", Math.round(value * 100f));

    public Screen createScreen(@Nullable Screen parent)
    {
        AtomicBoolean stereoSpatializationFixEnabledBefore = new AtomicBoolean(HANDLER.instance().stereoSpatializationFixEnabled);

        return YetAnotherConfigLib.createBuilder()
            .title(Component.translatable("audioImprovements.config.title", AudioImprovements.MOD_NAME))
            .category(this::buildScreenMonoCategory)
            .category(this::buildScreenMusicCategory)
            .category(this::buildScreenMiscellaneousCategory)
            .save(() -> {
                HANDLER.save();

                if (HANDLER.instance().stereoSpatializationFixEnabled != stereoSpatializationFixEnabledBefore.get())
                {
                    // Minecraft caches sound files -> we need to reload the sound engine to
                    // make it load the sounds again so the new setting value takes effect
                    Minecraft.getInstance().getSoundManager().reload();
                }
                stereoSpatializationFixEnabledBefore.set(HANDLER.instance().stereoSpatializationFixEnabled);
            })
            .build()
            .generateScreen(parent);
    }

    private ConfigCategory buildScreenMonoCategory()
    {
        return ConfigCategory.createBuilder()
            .name(Component.translatable("audioImprovements.config.category.monoAudio"))
            .option(boolOpt("monoMusicDiscs", false,
                config -> config.monoMusicDiscs, newVal -> monoMusicDiscs = newVal))
            .option(boolOpt("monoNoteBlocks", false,
                config -> config.monoNoteBlocks, newVal -> monoNoteBlocks = newVal))
            .option(boolOpt("monoWeather", false,
                config -> config.monoWeather, newVal -> monoWeather = newVal))
            .option(boolOpt("monoBlocks", false,
                config -> config.monoBlocks, newVal -> monoBlocks = newVal))
            .option(boolOpt("monoHostile", false,
                config -> config.monoHostile, newVal -> monoHostile = newVal))
            .option(boolOpt("monoNeutral", false,
                config -> config.monoNeutral, newVal -> monoNeutral = newVal))
            .option(boolOpt("monoPlayers", false,
                config -> config.monoPlayers, newVal -> monoPlayers = newVal))
            .option(boolOpt("monoAmbient", false,
                config -> config.monoAmbient, newVal -> monoAmbient = newVal))
            .build();
    }

    private ConfigCategory buildScreenMusicCategory()
    {
        Function<MusicFrequencyValue, Integer> musicFrequencyValueGetter = frequency -> frequency.ticks;

        Option<Integer> musicFrequencyMinTicksOption = intOptArraySlider(
            "customMusicFrequencyMinTicks", false,
            MusicFrequencyValue.VALUES, musicFrequencyValueGetter,
            config -> config.musicFrequencyMinTicks, element -> musicFrequencyMinTicks = element.ticks,
            element -> element.valueComponent);
        Option<Integer> musicFrequencyMaxTicksOption = intOptArraySlider(
            "customMusicFrequencyMaxTicks", false,
            MusicFrequencyValue.VALUES, musicFrequencyValueGetter,
            config -> config.musicFrequencyMaxTicks, element -> musicFrequencyMaxTicks = element.ticks,
            element -> element.valueComponent);
        // Note: these check the indices, but that's alright because the MusicFrequencyValue.VALUES array is ordered
        musicFrequencyMinTicksOption.addEventListener((option, event) -> {
            if (event != OptionEventListener.Event.STATE_CHANGE) return;
            if (option.pendingValue() > musicFrequencyMaxTicksOption.pendingValue())
                musicFrequencyMaxTicksOption.requestSet(option.pendingValue());
        });
        musicFrequencyMaxTicksOption.addEventListener((option, event) -> {
            if (event != OptionEventListener.Event.STATE_CHANGE) return;
            if (option.pendingValue() < musicFrequencyMinTicksOption.pendingValue())
                musicFrequencyMinTicksOption.requestSet(option.pendingValue());
        });

        return ConfigCategory.createBuilder()
            .name(Component.translatable("audioImprovements.config.category.music"))
            .option(boolOpt("preventMusicRepeat", true,
                config -> config.preventMusicRepeat, newVal -> preventMusicRepeat = newVal))
            .option(Option.<Float>createBuilder()
                .name(Component.translatable("audioImprovements.config.option.musicDiscDistanceMultiplier"))
                .description(OptionDescription.of(Component.translatable("audioImprovements.config.option.musicDiscDistanceMultiplier.tooltip")))
                .binding(DEFAULTS.musicDiscDistanceMultiplier, () -> musicDiscDistanceMultiplier, newVal -> musicDiscDistanceMultiplier = newVal)
                .controller(option -> FloatSliderControllerBuilder.create(option)
                    .range(0.5f, 3.1f).step(0.1f).formatValue(value -> value > 3f
                        ? Component.translatable("audioImprovements.config.option.musicDiscDistanceMultiplier.global")
                        : VALUE_FORMATTER_PERCENTAGE.format(value)))
                .build())
            .group(OptionGroup.createBuilder()
                .name(Component.translatable("audioImprovements.config.category.music.section.musicFrequency"))
                .option(boolOpt("customMusicFrequency", true,
                    config -> config.customMusicFrequencyEnabled, newVal -> customMusicFrequencyEnabled = newVal))
                .option(musicFrequencyMinTicksOption)
                .option(musicFrequencyMaxTicksOption)
                .option(boolOpt("customMusicFrequencyAffectMenu", false,
                    config -> config.musicFrequencyAffectMenu, newVal -> musicFrequencyAffectMenu = newVal))
                .build())
            .group(OptionGroup.createBuilder()
                .name(Component.translatable("audioImprovements.config.category.music.section.musicClashPrevention"))
                .option(boolOpt("fadeMusicWhenMusicDiscPlaying", true,
                    config -> config.fadeMusicWhenMusicDiscPlaying, newVal -> fadeMusicWhenMusicDiscPlaying = newVal))
                .option(boolOpt("fadeMusicWhenNoteBlockPlaying", true,
                    config -> config.fadeMusicWhenNoteBlockPlaying, newVal -> fadeMusicWhenNoteBlockPlaying = newVal))
                .option(Option.<Integer>createBuilder()
                    .name(Component.translatable("audioImprovements.config.option.musicFadeOutTicks"))
                    .binding(DEFAULTS.musicFadeOutTicks, () -> musicFadeOutTicks, newVal -> musicFadeOutTicks = newVal)
                    .controller(option -> IntegerSliderControllerBuilder.create(option)
                        .range(0, 200).step(20).formatValue(value -> VALUE_FORMATTER_SECONDS.format(Math.round(value / 20f))))
                    .build())
                .option(Option.<Integer>createBuilder()
                    .name(Component.translatable("audioImprovements.config.option.musicFadeInTicks"))
                    .binding(DEFAULTS.musicFadeInTicks, () -> musicFadeInTicks, newVal -> musicFadeInTicks = newVal)
                    .controller(option -> IntegerSliderControllerBuilder.create(option)
                        .range(0, 200).step(20).formatValue(value -> VALUE_FORMATTER_SECONDS.format(Math.round(value / 20f))))
                    .build())
                .build())
            .build();
    }

    private ConfigCategory buildScreenMiscellaneousCategory()
    {
        final Integer[] speedOfSoundValues = new Integer[] {
            0, 25, 50, 75, 100, 125, 150, 175, 200, 225, 250, 275, 300, 343
        };
        Function<Integer, Integer> speedOfSoundValueGetter = value -> value;
        ValueFormatter<Integer> speedOfSoundFormatter =
            value -> value > 0
                ? (value == 343
                    ? Component.translatable("audioImprovements.config.option.speedOfSound.realistic",
                        Component.translatable("audioImprovements.format.blocksPerSecond", value))
                    : Component.translatable("audioImprovements.format.blocksPerSecond", value))
                : CommonComponents.OPTION_OFF;

        boolean canEditLogarithmicVolumeOption = !AudioImprovements.instance().isLogarithmicVolumeControlInstalled();
        Option<Boolean> logarithmicVolumeSlidersOption = Option.<Boolean>createBuilder()
            .name(Component.translatable("audioImprovements.config.option.logarithmicVolumeSliders"))
            .binding(DEFAULTS.logarithmicVolumeSlidersEnabled, () -> logarithmicVolumeSlidersEnabled,
                newValue -> {
                    logarithmicVolumeSlidersEnabled = newValue;

                    for (SoundSource soundSource : SoundSource.values())
                    {
                        //? if >=1.21.11 {
                        Minecraft.getInstance().getSoundManager().refreshCategoryVolume(soundSource);
                        //? } else {
                        /*Minecraft.getInstance().getSoundManager().updateSourceVolume(
                            soundSource, Minecraft.getInstance().options.getSoundSourceVolume(soundSource)
                        );
                        *///? }
                    }
                }
            )
            .controller(TickBoxControllerBuilder::create)
            .description(OptionDescription.of(Component.translatable(
                canEditLogarithmicVolumeOption
                    ? "audioImprovements.config.option.logarithmicVolumeSliders.tooltip"
                    : "audioImprovements.config.option.logarithmicVolumeSliders.tooltip.logarithmicVolumeControlInstalled"
            )))
            .available(canEditLogarithmicVolumeOption)
            .build();

        return ConfigCategory.createBuilder()
            .name(Component.translatable("audioImprovements.config.category.miscellaneous"))
            .option(logarithmicVolumeSlidersOption)
            .option(Option.<Float>createBuilder()
                .name(Component.translatable("audioImprovements.config.option.dopplerEffectIntensity"))
                .description(OptionDescription.of(Component.translatable("audioImprovements.config.option.dopplerEffectIntensity.tooltip")))
                .binding(DEFAULTS.dopplerEffectIntensity, () -> dopplerEffectIntensity, newVal -> dopplerEffectIntensity = newVal)
                .controller(option -> FloatSliderControllerBuilder.create(option)
                    .range(0f, 5f).step(0.1f).formatValue(
                        value -> Mth.equal(value, 0f) ? CommonComponents.OPTION_OFF : VALUE_FORMATTER_PERCENTAGE.format(value)
                    ))
                .build())
            .option(boolOpt("stereoSpatializationFix", true,
                config -> config.stereoSpatializationFixEnabled, newVal -> stereoSpatializationFixEnabled = newVal))
            .group(OptionGroup.createBuilder()
                .name(Component.translatable("audioImprovements.config.category.miscellaneous.section.speedOfSound"))
                .option(intOptArraySlider("speedOfSoundThunder", true,
                    speedOfSoundValues, speedOfSoundValueGetter,
                    config -> config.speedOfSoundThunder, value -> speedOfSoundThunder = value,
                    speedOfSoundFormatter))
                .option(intOptArraySlider("speedOfSoundExplosions", true,
                    speedOfSoundValues, speedOfSoundValueGetter,
                    config -> config.speedOfSoundExplosions, value -> speedOfSoundExplosions = value,
                    speedOfSoundFormatter))
                .option(intOptArraySlider("speedOfSoundOther", true,
                    speedOfSoundValues, speedOfSoundValueGetter,
                    config -> config.speedOfSoundOther, value -> speedOfSoundOther = value,
                    speedOfSoundFormatter))
                .build())
            .build();
    }

    private Option<Boolean> boolOpt(String translationKeyName, boolean hasDescription,
                                    Function<Config, Boolean> valueSupplier, Consumer<Boolean> valueSetter)
    {
        Option.Builder<Boolean> builder = Option.<Boolean>createBuilder()
            .name(Component.translatable("audioImprovements.config.option." + translationKeyName))
            .binding(valueSupplier.apply(DEFAULTS), () -> valueSupplier.apply(this), valueSetter)
            .controller(TickBoxControllerBuilder::create);
        if (hasDescription) builder.description(OptionDescription.of(Component.translatable("audioImprovements.config.option." + translationKeyName + ".tooltip")));
        return builder.build();
    }

    private <T> Option<Integer> intOptArraySlider(String translationKeyName, boolean hasDescription,
                                                  T[] array, Function<T, Integer> elementValueGetter,
                                                  Function<Config, Integer> configValueSupplier, Consumer<T> configValueSetter,
                                                  ValueFormatter<T> valueFormatter)
    {
        Option.Builder<Integer> builder = Option.<Integer>createBuilder()
            .name(Component.translatable("audioImprovements.config.option." + translationKeyName))
            .binding(
                getClosestIndexFor(array, configValueSupplier.apply(DEFAULTS), elementValueGetter),
                () -> getClosestIndexFor(array, configValueSupplier.apply(this), elementValueGetter),
                newIndex -> configValueSetter.accept(array[newIndex])
            )
            .controller(option -> IntegerSliderControllerBuilder.create(option)
                .range(0, array.length - 1).step(1)
                .formatValue(index -> valueFormatter.format(array[index])));
        if (hasDescription) builder.description(OptionDescription.of(Component.translatable("audioImprovements.config.option." + translationKeyName + ".tooltip")));
        return builder.build();
    }

    private static <T> int getClosestIndexFor(T[] array, int value, Function<T, Integer> valueGetter)
    {
        int currentIndex = 0;
        int minDeviation = Integer.MAX_VALUE;
        for (int i = 0; i < array.length; i ++)
        {
            int deviation = Math.abs(value - valueGetter.apply(array[i]));
            if (deviation < minDeviation)
            {
                currentIndex = i;
                minDeviation = deviation;
            }
        }
        return currentIndex;
    }
}
