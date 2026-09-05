package namelessju.audio_improvements.data;

import net.minecraft.network.chat.Component;

public class MusicFrequencyValue
{
    public static final MusicFrequencyValue[] VALUES;

    static
    {
        int[] secondValues = new int[] {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55,
            60, 60*2, 60*3, 60*4, 60*5, 60*6, 60*7, 60*8, 60*9, 60*10, 60*15,
            60*20, 60*25, 60*30, 60*35, 60*40, 60*45, 60*50, 60*55, 60*60
        };
        VALUES = new MusicFrequencyValue[secondValues.length];
        for (int i = 0; i < VALUES.length; i ++)
        {
            VALUES[i] = new MusicFrequencyValue(secondValues[i]);
        }
    }


    public final int ticks;
    public final Component valueComponent;

    private MusicFrequencyValue(int seconds)
    {
        ticks = seconds * 20;
        boolean isMinutes = seconds >= 60;
        String translationKey = "audioImprovements.format." + (
            isMinutes ? (seconds == 60 ? "minute" : "minutes")
                : (seconds == 1 ? "second" : "seconds")
        );
        this.valueComponent = Component.translatable(translationKey, isMinutes ? Math.round(seconds / 60f) : seconds);
    }
}
