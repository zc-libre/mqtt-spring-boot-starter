# mqtt-spring-boot-starter
MQTT封装 使用方法：
```java
// 消息订阅
@Slf4j
@MqttListener(topic = "test")
public class MqttTestListener implements MqttMessageListener {

	@Override
	public void onMessage(MqttMessage message) {
		log.info("接收到消息, topic: {}, message: {}", message.getTopic(), message);
	}

}
```

```java
    // 消息发送
    @Autowired
	private MqttMessageGateWay mqttMessageGateWay;

	@Test
	void publish() {
		for (int i = 0; i < 10; i++) {
			mqttMessageGateWay.sendToMqtt("test", "123");
		}

	}
```
可配置项见MqttProperties
