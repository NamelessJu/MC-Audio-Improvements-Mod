package namelessju.audioimprovements.gui.screens;

import namelessju.audioimprovements.ConfigImpl;
import namelessju.audioimprovements.gui.components.GuiList;
import net.minecraft.client.gui.screens.Screen;

public class MonoAudioConfigScreen extends AbstractConfigScreen
{
    public MonoAudioConfigScreen(Screen parentScreen, ConfigImpl config)
    {
        super("configMonoAudio", parentScreen, config);
    }
    
    @Override
    protected void initList(GuiList list)
    {
        list.addTwoColumns(
            config.monoMusicDiscs.createButton(0, 0, 150, null),
            config.monoNoteBlocks.createButton(0, 0, 150, null)
        );
        list.addTwoColumns(
            config.monoWeather.createButton(0, 0, 150, null),
            config.monoBlocks.createButton(0, 0, 150, null)
        );
        list.addTwoColumns(
            config.monoHostile.createButton(0, 0, 150, null),
            config.monoNeutral.createButton(0, 0, 150, null)
        );
        list.addTwoColumns(
            config.monoPlayers.createButton(0, 0, 150, null),
            config.monoAmbient.createButton(0, 0, 150, null)
        );
    }
}
