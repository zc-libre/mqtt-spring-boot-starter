package com.libre.mqtt;

import java.util.List;

public interface MqttOptions {

	void addTopic(String topic);

	void addTopic(String topic, int qos);

	void removeTopic(String topic);

	List<String> listTopics();

	void convertAndSend(MqttMessage mqttMessage);

}
