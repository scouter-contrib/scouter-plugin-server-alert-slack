# scouter-plugin-server-alert-slack
### Scouter server plugin to send a alert via slack

- this project inspired by telegram plugin project of noces96. it is very similar.

- this project is  scouter server plugin project. this project goal is that send message to slack.
-  this project only support a sort of Alert.
	- CPU of Agent  (warning / fatal)
	- Memory of Agent (warning / fatal)
	- Disk of Agent (warning / fatal)
	- connected new Agent 
	- disconnected Agent
	- reconnect Agent

### Properties (you can modify in conf/scouter.conf of scouter server home )
* **_ext\_plugin\_slack\_send\_alert_** : can send slack message or can'not  (true / false) - default : false
* **_ext\_plugin\_slack\_debug_** : can log message or can't  - default false
* **_ext\_plugin\_slack\_level_** : log level (0 : INFO, 1 : WARN, 2 : ERROR, 3 : FATAL) - default 0
* **_ext\_plugin\_slack\_webhook_url_** : Slack WebHook URL 
* **_ext\_plugin\_slack\_channel_** : #Channel or @user_id
* **_ext\_plugin\_slack\_botName_** : bot name
* **_ext\_plugin\_slack\_icon\_emoji_** : Slack emoticon  (if  it is existed slack emotion, it will use slack emotion )
* **_ext\_plugin\_slack\_icon\_url_** : icon image URL



* Example
```
# External Interface (Slack)
ext_plugin_slack_send_alert=true
ext_plugin_slack_debug=true
ext_plugin_slack_level=1
ext_plugin_slack_webhook_url=https://hooks.slack.com/services/T02XXXXX/B159XXXXX/W5CDXXXXXXXXXXXXXXXXXXXX
ext_plugin_slack_channel=#scouter
ext_plugin_slack_botName=scouter
ext_plugin_slack_icon_emoji=:computer:
ext_plugin_slack_icon_url=http://XXX.XXX.XXX/XXX.gif
```

### Dependencies
* Project
    - scouter.common
    - scouter.server
* Library
    - commons-codec-1.9.jar
    - commons-logging-1.2.jar
    - gson-2.6.2.jar
    - httpclient-4.5.2.jar
    - httpcore-4.4.4.jar
    
### Build & Deploy
* Pre-condition
    - should set scouter server home  in .bashrc or .zshrc  
* Build
    - ant compile
    
* Deploy
    - ant dist
