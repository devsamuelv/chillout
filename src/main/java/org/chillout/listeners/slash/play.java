package org.chillout.listeners.slash;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.chillout.handler.MusicUtils;

public class play extends ListenerAdapter {
  @Override
  public void onSlashCommand(SlashCommandEvent event) {
    TextChannel channel = event.getTextChannel();
    boolean isExecutable = event.getName().equals("play");

    if (!isExecutable)
      return;

    String url = event.getOption("url").getAsString();

    if (url == null) {
      channel.sendMessage("the url cannot be null").queue();
      return;
    }

    MusicUtils.loadAndPlay(channel, url, event.getUser().getId());
  }
}
