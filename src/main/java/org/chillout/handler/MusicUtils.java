package org.chillout.handler;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import org.chillout.Constants;

import java.util.Date;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class MusicUtils {
  public static VoiceScheduler voiceScheduler = new VoiceScheduler();

  public static synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
    long guildId = Long.parseLong(guild.getId());
    GuildMusicManager musicManager = Constants.musicManagers.get(guildId);

    if (musicManager == null) {
      musicManager = new GuildMusicManager(Constants.playerManager);
      Constants.musicManagers.put(guildId, musicManager);
    }

    guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

    return musicManager;
  }

  public static void loadAndPlay(final TextChannel channel, final String trackUrl, String userId) {
    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

    Constants.playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
      @Override
      public void trackLoaded(AudioTrack track) {
        channel.sendMessage("Adding to queue `" + track.getInfo().title + "`").queue();

        play(channel.getGuild(), channel.getId(), musicManager, track, userId);
      }

      @Override
      public void playlistLoaded(AudioPlaylist playlist) {
        AudioTrack firstTrack = playlist.getSelectedTrack();

        if (firstTrack == null) {
          firstTrack = playlist.getTracks().get(0);
        }

        channel.sendMessage(
            "Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")")
            .queue();

        play(channel.getGuild(), channel.getId(), musicManager, firstTrack, userId);
      }

      @Override
      public void noMatches() {
        channel.sendMessage("Nothing found by " + trackUrl).queue();
      }

      @Override
      public void loadFailed(FriendlyException exception) {
        channel.sendMessage("Could not play: " + exception.getMessage()).queue();
      }
    });
  }

  public static void leaveChannel(final TextChannel channel) {
    for (VoiceChannel voiceChannel : channel.getGuild().getVoiceChannels()) {
      for (Member member : voiceChannel.getMembers()) {
        if (member.getId().equals("806351851091132426") || member.getId().equals("683866713858768926")) {
          voiceChannel.getGuild().getAudioManager().closeAudioConnection();
        }
      }
    }
  }

  public static void volume(final TextChannel channel, int vol) {
    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

    musicManager.player.setVolume(vol);
  }

  public static void loop(final TextChannel channel) {
  }

  public static void stopTrack(final TextChannel channel) {
    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
    AudioTrackInfo trackInfo = musicManager.player.getPlayingTrack().getInfo();

    channel.sendMessage("Stoping Track `" + trackInfo.title + " By " + trackInfo.author + "`").queue();
    musicManager.player.stopTrack();
  }

  public static void play(Guild guild, String channelId, GuildMusicManager musicManager, AudioTrack track,
      String userId) {
    connectToChannel(guild.getAudioManager(), userId);

    Date joinedDate = voiceScheduler.getChannel(channelId);

    if (joinedDate == null) {
      voiceScheduler.addChannel(channelId);
    }

    musicManager.scheduler.queue(track);
  }

  public static void skipTrack(TextChannel channel) {
    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
    musicManager.scheduler.nextTrack();

    channel.sendMessage("Skipped to next track.").queue();
  }

  public static void connectToFirstVoiceChannel(AudioManager audioManager) {
    if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
      for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
        audioManager.openAudioConnection(voiceChannel);
        break;
      }
    }
  }

  public static void connectToChannel(AudioManager audioManager, String userId) {
    if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
      for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
        for (Member member : voiceChannel.getMembers()) {
          if (member.getId().equals(userId)) {
            System.out.println(voiceChannel.getName());
            audioManager.openAudioConnection(voiceChannel);
            break;
          }
        }
      }
    }
  }
}
