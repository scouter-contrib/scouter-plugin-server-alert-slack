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
* **_ext\_plugin\_elapsed\_time\_threshold_** : 응답시간의 임계치 (ms) - 기본 값은 0으로, 0일때 응답시간의 임계치 초과 여부를 확인하지 않는다.
* **_ext\_plugin\_gc\_time\_threshold_** : GC Time의 임계치 (ms) - 기본 값은 0으로, 0일때 GC Time의 임계치 초과 여부를 확인하지 않는다.
* **_ext\_plugin\_thread\_count\_threshold_** : Thread Count의 임계치 - 기본 값은 0으로, 0일때 Thread Count의 임계치 초과 여부를 확인하지 않는다.
* **_ext\_plugin\_slack\_xlog\_enabled_** : xlog message send (true / false) - default : false
* **_ext_plugin_slack_object_alert_enabled_** : object active/dead alert (true / false) - default : false
  
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
ext_plugin_slack_xlog_enabled=true
ext_plugin_slack_object_alert_enabled=true

ext_plugin_elapsed_time_threshold=5000
ext_plugin_gc_time_threshold=5000
ext_plugin_thread_count_threshold=300
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
