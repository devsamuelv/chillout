package org.chillout;

import org.chillout.graphql.Graphql;
import org.chillout.handler.GuildMusicManager;
import org.chillout.handler.MusicUtils;
import org.chillout.listeners.Init;
import org.chillout.listeners.loopChannel;
import org.chillout.listeners.volume;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import graphql.com.google.common.collect.ImmutableMap;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;

import javax.security.auth.login.LoginException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

public class Main extends ListenerAdapter {
    private File file = new File("./msgs.msg");
    private final loopChannel loopChannelCmd = new loopChannel();
    private Graphql ql = new Graphql();

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

    public void getServerContent() {
        for (Guild guild : Constants.bot.getGuilds()) {
            for (TextChannel channel : guild.getTextChannels()) {
                for (Message msg : channel.getIterableHistory().complete()) {
                    String raw = msg.getContentRaw();

                    ql.execute("AddMessage", ImmutableMap.of("content", raw, "author", msg.getAuthor().getName(), "id",
                            msg.getId(), "server", guild.getName()));
                }
            }
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        this.getServerContent();

        String[] command = event.getMessage().getContentRaw().split(" ", 2);

        event.getAuthor().getId();

        ql.execute("AddMessage",
                ImmutableMap.of("content", event.getMessage().getContentRaw(), "author", event.getAuthor().getName(),
                        "id", event.getMessage().getId(), "server", event.getChannel().getGuild().getName()));

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
        } else if ("~loop".equals(command[0])) {
            loopChannelCmd.run(event.getChannel(), event.getMessage());
        }

        super.onGuildMessageReceived(event);

    }
}
