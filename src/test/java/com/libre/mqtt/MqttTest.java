package com.libre.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class MqttTest {

	@Autowired
	private MqttMessageGateWay mqttMessageGateWay;

	@Test
	void publish() {
		for (int i = 0; i < 10; i++) {
			mqttMessageGateWay.sendToMqtt("test", "123");
			log.info("发送消息，" + i);
		}

	}

}
