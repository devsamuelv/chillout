package org.chillout.listeners;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;

public class Init extends ListenerAdapter {
  @Override
  public void onReady(ReadyEvent event) {
    getMessages(event.getJDA());

    super.onReady(event);
  }

  private void processHistoryContent(ArrayList<Message> messages, String name) {
    try {
      File folder = new File("Messages/");

      File file = new File("./Messages/Collection[ " + name + " ].watch");

      if (!folder.exists())
        folder.mkdir();

      if (!file.exists())
        file.createNewFile();

      FileWriter writer = new FileWriter(file);
      StringBuilder builder = new StringBuilder();

      for (Message msg : messages) {
        builder.append(msg.getContentRaw() + "\n");
      }

      writer.write(builder.toString());
      writer.close();
    } catch (IOException err) {
      err.printStackTrace();
    }

  }

  private void getMessages(JDA bot) {
    try {
      for (Guild guild : bot.getGuilds()) {
        for (TextChannel channel : guild.getTextChannels()) {
          MessageHistory his = channel.getHistory();
          ArrayList<Message> buff = new ArrayList<Message>();

          Consumer<List<Message>> messageConsumer = res -> {
            for (Message msg : res) {
              buff.add(msg);
            }

            processHistoryContent(buff, channel.getName());
          };

          for (int i = 5; i != 0; i--) {
            his.retrievePast(100).queue(messageConsumer);
          }

        }
      }

    } catch (Exception err) {
      System.err.println(err.toString());
    }
  }
}
