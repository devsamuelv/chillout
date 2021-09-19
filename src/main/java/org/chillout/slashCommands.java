package org.chillout;

import java.util.Arrays;
import java.util.Collection;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.apache.commons.collections4.bag.CollectionBag;
import org.chillout.listeners.slash.play;

public class slashCommands extends ListenerAdapter {
  public slashCommands() {
    Collection<CommandData> commands = Arrays.asList(new CommandData("play", "plays audio from the selected url"));
    Constants.bot.updateCommands().addCommands(commands);

    Constants.bot.addEventListener(new play());
  }
}
