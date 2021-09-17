package org.chillout.handler;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

public class AudioSender implements AudioSendHandler {
    AudioPlayer player;
    AudioFrame lastFrame;

    public AudioSender(AudioPlayer player) {
        this.player = player;
    }

    @Override
    public boolean canProvide() {
        lastFrame = player.provide();

        return lastFrame != null;
    }

    @Override
    public boolean isOpus() {
        return true;
    }

    @Nullable
    @Override
    public ByteBuffer provide20MsAudio() {
        return ByteBuffer.wrap(lastFrame.getData());
    }
}
