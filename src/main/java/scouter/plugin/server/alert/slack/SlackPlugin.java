
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

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import scouter.lang.AlertLevel;
import scouter.lang.TextTypes;
import scouter.lang.TimeTypeEnum;
import scouter.lang.counters.CounterConstants;
import scouter.lang.pack.AlertPack;
import scouter.lang.pack.MapPack;
import scouter.lang.pack.ObjectPack;
import scouter.lang.pack.PerfCounterPack;
import scouter.lang.pack.XLogPack;
import scouter.lang.plugin.PluginConstants;
import scouter.lang.plugin.annotation.ServerPlugin;
import scouter.net.RequestCmd;
import scouter.server.Configure;
import scouter.server.CounterManager;
import scouter.server.Logger;
import scouter.server.core.AgentManager;
import scouter.server.db.TextRD;
import scouter.server.netio.AgentCall;
import scouter.util.DateUtil;
import scouter.util.HashUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Scouter server plugin to send alert via Slack
 *
 * @author Se-Wang Lee(ssamzie101@gmail.com) on 2016. 5. 2.
 */
public class SlackPlugin {
	
	final Configure conf = Configure.getInstance();
	
	private final MonitoringGroupConfigure groupConf;
	
    private static AtomicInteger ai = new AtomicInteger(0);
    private static List<Integer> javaeeObjHashList = new ArrayList<Integer>();

    public SlackPlugin() {
    	groupConf = new MonitoringGroupConfigure(conf);
    	
    	if (ai.incrementAndGet() == 1) {
	    	ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

	    	// thread count check
	    	executor.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					if (conf.getInt("ext_plugin_thread_count_threshold", 0) == 0) {
						return;
					}
					for (int objHash : javaeeObjHashList) {
						try {
							if (AgentManager.isActive(objHash)) {
								ObjectPack objectPack = AgentManager.getAgent(objHash);
								MapPack mapPack = new MapPack();
				            	mapPack.put("objHash", objHash);

								mapPack = AgentCall.call(objectPack, RequestCmd.OBJECT_THREAD_LIST, mapPack);

				        		int threadCountThreshold = groupConf.getInt("ext_plugin_thread_count_threshold", objectPack.objType, 0);
				        		int threadCount = mapPack.getList("name").size();

				        		if (threadCountThreshold != 0 && threadCount > threadCountThreshold) {
				        			AlertPack ap = new AlertPack();

				    		        ap.level = AlertLevel.WARN;
				    		        ap.objHash = objHash;
				    		        ap.title = "Thread count exceed a threshold.";
				    		        ap.message = objectPack.objName + "'s Thread count(" + threadCount + ") exceed a threshold.";
				    		        ap.time = System.currentTimeMillis();
				    		        ap.objType = objectPack.objType;

				    		        alert(ap);
				        		}
							}
						} catch (Exception e) {
							// ignore
						}
					}
				}
	    	},
	    	0, 5, TimeUnit.SECONDS);
    	}
	}

	@ServerPlugin(PluginConstants.PLUGIN_SERVER_ALERT)
	public void alert(final AlertPack pack){
		if (groupConf.getBoolean("ext_plugin_slack_send_alert", pack.objType, false)) {
			int level = groupConf.getInt("ext_plugin_slack_level", pack.objType, 0);
			// Get log level (0 : INFO, 1 : WARN, 2 : ERROR, 3 : FATAL)
			if(level <= pack.level){
				new Thread(){
					public void run(){
						try{
							String webhookURL = groupConf.getValue("ext_plugin_slack_webhook_url", pack.objType);
							String channel = groupConf.getValue("ext_plugin_slack_channel", pack.objType);
							String botName = groupConf.getValue("ext_plugin_slack_botName", pack.objType);
							String iconURL = groupConf.getValue("ext_plugin_slack_icon_url", pack.objType);
							String iconEmoji = groupConf.getValue("ext_plugin_slack_icon_emoji", pack.objType);

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

                if(groupConf.getBoolean("ext_plugin_slack_debug", pack.objType, false)){
                	println("WebHookURL : "+webhookURL);
                	println("param : "+payload);
                }

                HttpPost post = new HttpPost(webhookURL);
                post.addHeader("Content-Type","application/json");
								// charset set utf-8
								post.setEntity(new StringEntity(payload, "utf-8"));

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
		if (!conf.getBoolean("ext_plugin_slack_object_alert_enabled", false)) {
			return;
		}

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

		        if (AgentManager.getAgent(pack.objHash) != null) {
		        	ap.objType = AgentManager.getAgent(pack.objHash).objType;
		        } else {
		        	ap.objType = "scouter";
		        }

		        alert(ap);
	    	} else if (op.alive == false) {
				// in case of agent reconnected
				ap = new AlertPack();
		        ap.level = AlertLevel.INFO;
		        ap.objHash = pack.objHash;
		        ap.title = "An object has been activated.";
		        ap.message = pack.objName + " is reconnected.";
		        ap.time = System.currentTimeMillis();
		        ap.objType = AgentManager.getAgent(pack.objHash).objType;

		        alert(ap);
	    	}
			// inactive state can be handled in alert() method.
    	}
	}

	@ServerPlugin(PluginConstants.PLUGIN_SERVER_XLOG)
	public void xlog(XLogPack pack) {
		if (!conf.getBoolean("ext_plugin_slack_xlog_enabled", false)) {
			return;
		}

		String objType = AgentManager.getAgent(pack.objHash).objType;
		if (groupConf.getBoolean("ext_plugin_slack_xlog_enabled", objType, true)) {
			if (pack.error != 0) {
				String date = DateUtil.yyyymmdd(pack.endTime);
				String service = TextRD.getString(date, TextTypes.SERVICE, pack.service);
				AlertPack ap = new AlertPack();
				ap.level = AlertLevel.ERROR;
				ap.objHash = pack.objHash;
				ap.title = "xlog Error";
				ap.message = service + " - " + TextRD.getString(date, TextTypes.ERROR, pack.error);
				ap.time = System.currentTimeMillis();
				ap.objType = objType;
				alert(ap);
			}

			try {
					int elapsedThreshold = groupConf.getInt("ext_plugin_elapsed_time_threshold", objType, 0);
					
					if (elapsedThreshold != 0 && pack.elapsed > elapsedThreshold) {
						String serviceName = TextRD.getString(DateUtil.yyyymmdd(pack.endTime), TextTypes.SERVICE, pack.service);

						AlertPack ap = new AlertPack();

							ap.level = AlertLevel.WARN;
							ap.objHash = pack.objHash;
							ap.title = "Elapsed time exceed a threshold.";
							ap.message = "[" + AgentManager.getAgentName(pack.objHash) + "] "
											+ pack.service + "(" + serviceName + ") "
											+ "elapsed time(" + pack.elapsed + " ms) exceed a threshold.";
							ap.time = System.currentTimeMillis();
							ap.objType = objType;

							alert(ap);
					}

			} catch (Exception e) {
				Logger.printStackTrace(e);
			}
		}
	}


@ServerPlugin(PluginConstants.PLUGIN_SERVER_COUNTER)
  public void counter(PerfCounterPack pack) {
      String objName = pack.objName;
      int objHash = HashUtil.hash(objName);
      String objType = null;
      String objFamily = null;

      if (AgentManager.getAgent(objHash) != null) {
      	objType = AgentManager.getAgent(objHash).objType;
      }

      if (objType != null) {
      	objFamily = CounterManager.getInstance().getCounterEngine().getObjectType(objType).getFamily().getName();
      }

      try {
        // in case of objFamily is javaee
        if (CounterConstants.FAMILY_JAVAEE.equals(objFamily)) {
        	// save javaee type's objHash
        	if (!javaeeObjHashList.contains(objHash)) {
        		javaeeObjHashList.add(objHash);
        	}

        	if (pack.timetype == TimeTypeEnum.REALTIME) {
        		long gcTimeThreshold = groupConf.getLong("ext_plugin_gc_time_threshold", objType, 0);
        		long gcTime = pack.data.getLong(CounterConstants.JAVA_GC_TIME);

        		if (gcTimeThreshold != 0 && gcTime > gcTimeThreshold) {
        			AlertPack ap = new AlertPack();

    		        ap.level = AlertLevel.WARN;
    		        ap.objHash = objHash;
    		        ap.title = "GC time exceed a threshold.";
    		        ap.message = objName + "'s GC time(" + gcTime + " ms) exceed a threshold.";
    		        ap.time = System.currentTimeMillis();
    		        ap.objType = objType;

    		        alert(ap);
        		}
        	}
    	}
      } catch (Exception e) {
		Logger.printStackTrace(e);
      }
  }

	private void println(Object o){
		if(conf.getBoolean("ext_plugin_slack_debug", false)){
			System.out.println(o);
			Logger.println(o);
		}
	}
}
