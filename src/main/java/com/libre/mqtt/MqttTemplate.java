package com.libre.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.libre.mqtt.exception.MqttException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;

import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class MqttTemplate implements MqttOptions {

	private final MqttPahoMessageDrivenChannelAdapter adapter;

	private final MqttMessageGateWay mqttMessageGateWay;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void addTopic(String topic) {
		this.addTopic(topic, MqttQoS.AT_MOST_ONCE.value());
	}

	@Override
	public void addTopic(String topic, int qos) {
		Set<String> topics = Sets.newHashSet(adapter.getTopic());
		if (topics.contains(topic)) {
			return;
		}
		adapter.addTopic(topic, qos);
		log.debug("topic{}添加成功， qos: {}", topic, qos);
	}

	@Override
	public void removeTopic(String topic) {
		Set<String> topics = Sets.newHashSet(adapter.getTopic());
		if (!topics.contains(topic)) {
			return;
		}
		adapter.removeTopic(topic);
		log.debug("topic{}删除成功", topic);
	}

	@Override
	public List<String> listTopics() {
		return Lists.newArrayList(adapter.getTopic());
	}

	@Override
	public void convertAndSend(MqttMessage mqttMessage) {
		try {
			String payload = objectMapper.writeValueAsString(mqttMessage.getPayload());
			mqttMessageGateWay.sendToMqtt(mqttMessage.getTopic(), mqttMessage.getQos(), mqttMessage.getRetained(),
					payload);
			log.debug("消息{}发布成功, topic: {}", mqttMessage, mqttMessage.getTopic());
		}
		catch (Exception e) {
			throw new MqttException("Failed to send message, message: " + mqttMessage, e);
		}
	}

}
