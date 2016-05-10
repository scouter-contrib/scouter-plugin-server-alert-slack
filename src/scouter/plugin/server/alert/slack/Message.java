/*
 *  Copyright 2016 Scouter Project.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 *  
 *  @author Se-Wang Lee
 */
package scouter.plugin.server.alert.slack;

import com.google.gson.annotations.SerializedName;

/**
 * Alert message class to send alert via Slack
 * 
 * @author Se-Wang Lee(ssamzie101@gmail.com) on 2016. 5. 2.
 */
public class Message {
	
	@SerializedName("text")
	private String text;
	@SerializedName("channel")
	private String channel;
	@SerializedName("username")
	private String botName;
	@SerializedName("icon_emoji")
	private String iconEmoji;
	@SerializedName("icon_url")
	private String iconURL;
	
	public Message(String text, String channel, String botName, String iconURL, String iconEmoji){
		this.text = text;
		this.channel = channel;
		this.botName = botName;
		this.iconURL = iconURL;
		this.iconEmoji = iconEmoji;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getBotName() {
		return botName;
	}
	public void setBotName(String botName) {
		this.botName = botName;
	}
	public String getIconURL() {
		return iconURL;
	}
	public void setIconURL(String iconURL) {
		this.iconURL = iconURL;
	}

	public String getIconEmoji() {
		return iconEmoji;
	}

	public void setIconEmoji(String iconEmoji) {
		this.iconEmoji = iconEmoji;
	}

}
