package org.chillout;

import org.chillout.handler.GuildMusicManager;
import org.chillout.handler.MusicUtils;
import org.chillout.listeners.volume;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.HashMap;

public class Main extends ListenerAdapter {
    public Main() {
        Constants.musicManagers = new HashMap<>();

        Constants.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(Constants.playerManager);
        AudioSourceManagers.registerLocalSource(Constants.playerManager);
    }

    public static void buildbot(String token) throws IllegalArgumentException, LoginException, RateLimitedException {
        Constants.bot = JDABuilder.createDefault(token).build();

        Constants.bot.getPresence().setPresence(OnlineStatus.ONLINE,
                Activity.of(ActivityType.STREAMING, "Don't worry youtube can't take me down"), false);

        Constants.bot.addEventListener(new Main());

        // initialize all the slash commands
        new slashCommands();
    }

    public static void main(String[] args) {
        try {
            buildbot(System.getProperty("token"));
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        if (event.getMember().getId() == "683866713858768926") {
            GuildMusicManager manager = MusicUtils.getGuildAudioPlayer(event.getGuild());

            manager.scheduler.clear();
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] command = event.getMessage().getContentRaw().split(" ", 2);

        event.getAuthor().getId();

        if ("~play".equals(command[0]) && command.length == 2) {
            MusicUtils.loadAndPlay(event.getChannel(), command[1], event.getAuthor().getId());
        } else if ("~skip".equals(command[0])) {
            MusicUtils.skipTrack(event.getChannel());
        } else if ("~stop".equals(command[0])) {
            MusicUtils.stopTrack(event.getChannel());
        } else if ("~leave".equals(command[0])) {
            MusicUtils.leaveChannel(event.getChannel());
        } else if ("~volume".equals(command[0])) {
            volume.setVolume(event.getChannel(), command[1]);
        }

        super.onGuildMessageReceived(event);

    }
}
