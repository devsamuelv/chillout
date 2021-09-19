package org.chillout.listeners;

import net.dv8tion.jda.api.entities.TextChannel;
import org.chillout.handler.MusicUtils;

public class volume {
  public static void setVolume(TextChannel channel, String message) {
    int vol = Integer.parseInt(message);

    MusicUtils.volume(channel, vol);

    channel.sendMessage("Setting volume to " + message).queue();
  }
}
