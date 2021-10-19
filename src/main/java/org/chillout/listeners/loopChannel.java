package org.chillout.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.chillout.handler.GuildMusicManager;
import org.chillout.handler.MusicUtils;

public class loopChannel {
  public void run(TextChannel channel, Message message) {
    String[] command = message.getContentRaw().split(" ", 2);
    String url = command[1];
    GuildMusicManager manager = MusicUtils.getGuildAudioPlayer(channel.getGuild());

  }
}
