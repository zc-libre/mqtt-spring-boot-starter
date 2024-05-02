package com.libre.mqtt;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/test")
@RestController
@RequiredArgsConstructor
public class MqttTestController {

	private final MqttOptions mqttOptions;

	@GetMapping("publish")
	public void publish() {
		for (int i = 0; i < 1000000; i++) {
			int finalI = i;
			MqttMessage message = MqttMessage.of("第" + finalI + "条消息", "test", 0);
			mqttOptions.convertAndSend(message);
		}

	}

}
