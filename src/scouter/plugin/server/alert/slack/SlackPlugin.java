
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

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

import scouter.lang.AlertLevel;
import scouter.lang.pack.AlertPack;
import scouter.lang.pack.ObjectPack;
import scouter.lang.plugin.PluginConstants;
import scouter.lang.plugin.annotation.ServerPlugin;
import scouter.server.Configure;
import scouter.server.core.AgentManager;
import scouter.server.Logger;

/**
 * Scouter server plugin to send alert via Slack
 * 
 * @author Se-Wang Lee(ssamzie101@gmail.com) on 2016. 5. 2.
 */
public class SlackPlugin {
	final Configure conf = Configure.getInstance();
	
	@ServerPlugin(PluginConstants.PLUGIN_SERVER_ALERT)
	public void alert(final AlertPack pack){
		if (conf.getBoolean("ext_plugin_slack_send_alert", false)) {
			
			int level = conf.getInt("ext_plugin_slack_level", 0);
			// Get log level (0 : INFO, 1 : WARN, 2 : ERROR, 3 : FATAL)
			if(level <= pack.level){
				new Thread(){
					public void run(){ 
						try{
							String webhookURL = conf.getValue("ext_plugin_slack_webhook_url");
							String channel = conf.getValue("ext_plugin_slack_channel");
							String botName = conf.getValue("ext_plugin_slack_botName");
							String iconURL = conf.getValue("ext_plugin_slack_icon_url");
							String iconEmoji = conf.getValue("ext_plugin_slack_icon_emoji");
							
							assert webhookURL != null;
							
							// Get the agent Name
	                    	String name = AgentManager.getAgentName(pack.objHash) == null ? "N/A" : AgentManager.getAgentName(pack.objHash);
	                    	
	                    	if (name.equals("N/A") && pack.message.endsWith("connected.")) {
	                			int idx = pack.message.indexOf("connected");
	                    		if (pack.message.indexOf("reconnected") > -1) {
	                    			name = pack.message.substring(0, idx - 6);
	                    		} else {
	                    			name = pack.message.substring(0, idx - 4);
	                    		}
	                    	}
	                    	
	                    	String title = pack.title;
	                        String msg = pack.message;
	                        if (title.equals("INACTIVE_OBJECT")) {
	                        	title = "An object has been inactivated.";
	                        	msg = pack.message.substring(0, pack.message.indexOf("OBJECT") - 1);
	                        }
	                        
	                    	// Make message contents
	                        String contents = "[TYPE] : " + pack.objType.toUpperCase() + "\n" + 
	                                       	  "[NAME] : " + name + "\n" + 
	                                          "[LEVEL] : " + AlertLevel.getName(pack.level) + "\n" +
	                                          "[TITLE] : " + title + "\n" + 
	                                          "[MESSAGE] : " + msg;
	                        
	                        Message message = new Message(contents, channel, botName, iconURL, iconEmoji);
	                        String payload = new Gson().toJson(message);
	                        
	                        if(conf.getBoolean("ext_plugin_slack_debug", false)){
	                        	println("WebHookURL : "+webhookURL);
	                        	println("param : "+payload);
	                        }
	              
	                        HttpPost post = new HttpPost(webhookURL);
	                        post.addHeader("Content-Type","application/json");
	                        post.setEntity(new StringEntity(payload));
	                      
	                        CloseableHttpClient client = HttpClientBuilder.create().build();
	                      
	                        // send the post request
	                        HttpResponse response = client.execute(post);
	                        
	                        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	                            println("Slack message sent to [" + channel + "] successfully.");
	                        } else {
	                            println("Slack message sent failed. Verify below information.");
	                            println("[WebHookURL] : " + webhookURL);
	                            println("[Message] : " + payload);
	                            println("[Reason] : " + EntityUtils.toString(response.getEntity(), "UTF-8"));
	                        }
							
						}catch(Exception e){
							println("[Error] : " + e.getMessage());
	                    	
	                    	if(conf._trace) {
	                            e.printStackTrace();
	                    	}
						}
					}
				}.start();
			}
		}
	}
	
	@ServerPlugin(PluginConstants.PLUGIN_SERVER_OBJECT)
	public void object(ObjectPack pack){
		if (pack.version != null && pack.version.length() > 0) {
			AlertPack ap = null;
			ObjectPack op = AgentManager.getAgent(pack.objHash);
	    	
			if (op == null && pack.wakeup == 0L) {
				// in case of new agent connected
				ap = new AlertPack();
		        ap.level = AlertLevel.INFO;
		        ap.objHash = pack.objHash;
		        ap.title = "An object has been activated.";
		        ap.message = pack.objName + " is connected.";
		        ap.time = System.currentTimeMillis();
		        ap.objType = "scouter";
				
		        alert(ap);
	    	} else if (op.alive == false) {
				// in case of agent reconnected
				ap = new AlertPack();
		        ap.level = AlertLevel.INFO;
		        ap.objHash = pack.objHash;
		        ap.title = "An object has been activated.";
		        ap.message = pack.objName + " is reconnected.";
		        ap.time = System.currentTimeMillis();
		        ap.objType = "scouter";
				
		        alert(ap);
	    	}
			// inactive state can be handled in alert() method.
    	}
	}
	
	private void println(Object o){
		if(conf.getBoolean("ext_plugin_slack_debug", false)){
			System.out.println(o);
			Logger.println(o);
			
		}
	}
}
