package org.chillout;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import org.chillout.handler.GuildMusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.Map;

public class Main extends ListenerAdapter {
    private static JDA bot;

    private static AudioPlayerManager playerManager;
    private static Map<Long, GuildMusicManager> musicManagers;

    public Main() {
        musicManagers = new HashMap<>();

        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public static void buildbot(String token) throws IllegalArgumentException, LoginException, RateLimitedException {
        bot = JDABuilder.createDefault(token)
                .build();

        bot.addEventListener(new Main());
    }

    public static void main(String[] args) {
        try {
            buildbot(System.getProperty("token"));
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] command = event.getMessage().getContentRaw().split(" ", 2);

        event.getAuthor().getId();

        if ("~play".equals(command[0]) && command.length == 2) {
            loadAndPlay(event.getChannel(), command[1], event.getAuthor().getId());
        } else if ("~skip".equals(command[0])) {
            skipTrack(event.getChannel());
        } else if ("~stop".equals(command[0])) {
            stopTrack(event.getChannel());
        } else if ("~leave".equals(command[0])) {
            leaveChannel(event.getChannel());
        }

        super.onGuildMessageReceived(event);

    }

    private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    private void loadAndPlay(final TextChannel channel, final String trackUrl, String userId) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                channel.sendMessage("Adding to queue `" + track.getInfo().title + "`").queue();

                play(channel.getGuild(), musicManager, track, userId);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();

                play(channel.getGuild(), musicManager, firstTrack, userId);
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

    private void leaveChannel(final TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

        for (VoiceChannel voiceChannel : channel.getGuild().getVoiceChannels()) {
            for (Member member : voiceChannel.getMembers()) {
                if (member.getId().equals("806351851091132426") || member.getId().equals("683866713858768926")) {
                    voiceChannel.getGuild().getAudioManager().closeAudioConnection();
                }
            }
        }
    }

    private void stopTrack(final TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        AudioTrackInfo trackInfo = musicManager.player.getPlayingTrack().getInfo();

        channel.sendMessage("Stoping Track `" + trackInfo.title + " By " + trackInfo.author + "`").queue();
        musicManager.player.stopTrack();
    }

    private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track, String userId) {
        connectToChannel(guild.getAudioManager(), userId);

        musicManager.scheduler.queue(track);
    }

    private void skipTrack(TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.nextTrack();

        channel.sendMessage("Skipped to next track.").queue();
    }

    private static void connectToFirstVoiceChannel(AudioManager audioManager) {
        if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
            for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
                audioManager.openAudioConnection(voiceChannel);
                break;
            }
        }
    }

    private static void connectToChannel(AudioManager audioManager, String userId) {
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
