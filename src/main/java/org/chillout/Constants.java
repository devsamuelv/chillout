package org.chillout;

import java.util.Map;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.JDA;
import org.chillout.handler.GuildMusicManager;

public class Constants {
  public static JDA bot;

  public static AudioPlayerManager playerManager;
  public static Map<Long, GuildMusicManager> musicManagers;
}
