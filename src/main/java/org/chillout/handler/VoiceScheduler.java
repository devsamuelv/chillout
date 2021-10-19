package org.chillout.handler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VoiceScheduler {
  private Map<String, Date> joinedVoiceChannel = new HashMap<String, Date>();

  public void addChannel(String channelId) {
    this.joinedVoiceChannel.put(channelId, new Date());
  }

  public Date getChannel(String channelId) {
    return this.joinedVoiceChannel.get(channelId);
  }

  public Map<String, Date> getChannelList() {
    return this.joinedVoiceChannel;
  }
}
