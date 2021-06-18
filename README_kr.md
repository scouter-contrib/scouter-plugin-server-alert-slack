# scouter-plugin-server-alert-slack
### Scouter server plugin to send a alert via slack

- noces96님의 telegram plugin project를 토대로 만들었습니다. 매우 흡사합니다 ^^

- 본 프로젝트는 스카우터 서버 플러그인으로써 서버에서 발생한 Alert 메시지를 Slack 으로 전송하는 역할을 한다.
- 현재 지원되는 Alert의 종류는 다음과 같다.
	- Agent의 CPU (warning / fatal)
	- Agent의 Memory (warning / fatal)
	- Agent의 Disk (warning / fatal)
	- 신규 Agent 연결
	- Agent의 연결 해제
	- Agent의 재접속

### Properties (스카우터 서버 설치 경로 하위의 conf/scouter.conf)
* **_ext\_plugin\_slack\_send\_alert_** : Slack 메시지 발송 여부 (true / false) - 기본 값은 false
* **_ext\_plugin\_slack\_debug_** : 로깅 여부 - 기본 값은 false
* **_ext\_plugin\_slack\_level_** : 수신 레벨(0 : INFO, 1 : WARN, 2 : ERROR, 3 : FATAL) - 기본 값은 0
* **_ext\_plugin\_slack\_webhook_url_** : Slack WebHook URL
* **_ext\_plugin\_slack\_channel_** : 채널(#Channel) 혹은 아이디(@user_id)
* **_ext\_plugin\_slack\_botName_** : bot name
* **_ext\_plugin\_slack\_icon\_emoji_** : Slack 이모티콘 이름 (icon url 보다 우선순위 )
* **_ext\_plugin\_slack\_icon\_url_** : icon 이미지 URL
* **_ext\_plugin\_elapsed\_time\_threshold_** : 응답시간의 임계치 (ms) - 기본 값은 0으로, 0일때 응답시간의 임계치 초과 여부를 확인하지 않는다.
* **_ext\_plugin\_gc\_time\_threshold_** : GC Time의 임계치 (ms) - 기본 값은 0으로, 0일때 GC Time의 임계치 초과 여부를 확인하지 않는다.
* **_ext\_plugin\_thread\_count\_threshold_** : Thread Count의 임계치 - 기본 값은 0으로, 0일때 Thread Count의 임계치 초과 여부를 확인하지 않는다.
* **_ext\_plugin\_active\_service\_threshold_** : Active Service의 임계치 - 기본 값은 0으로, 0일때 Active Service의 임계치 초과 여부를 확인하지 않는다.
* **_ext\_plugin\_slack\_xlog\_enabled_** : xlog maasege send (true / false) - default : false
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

ext_plugin_elapsed_time_threshold=5000
ext_plugin_gc_time_threshold=5000
ext_plugin_thread_count_threshold=300
ext_plugin_active_service_threshold=30
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
    - .bashrc 나 .zshrc에 SCOUTER_SERVER_HOME 변수를 설정해야합니다

* Build
    - ant compile

* Deploy
    - ant dist
