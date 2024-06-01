package com.libre.mqtt;

import com.libre.mqtt.annotation.MqttListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MqttMessageInboundHandler implements MessageHandler, InitializingBean, ApplicationContextAware {

	private final Map<String, MqttMessageListener> mqttMessageListenerContext = new ConcurrentHashMap<>();

	private final List<MqttMessageListener> mqttMessageListenerList;

	private final MqttOptions mqttOptions;

	@Nullable
	private ApplicationContext applicationContext;

	public MqttMessageInboundHandler(List<MqttMessageListener> mqttMessageListenerList, MqttOptions mqttOptions) {
		this.mqttMessageListenerList = mqttMessageListenerList;
		this.mqttOptions = mqttOptions;
	}

	@Override
	@ServiceActivator(inputChannel = MqttProperties.MQTT_INPUT_CHANNEL_NAME)
	public void handleMessage(Message<?> message) throws MessagingException {
		log.debug("message arrived from server, message: {}", message);
		MqttMessage mqttMessage = MqttMessage.of(message);
		for (String topicFilter : mqttMessageListenerContext.keySet()) {
			try {
				if (TopicUtils.isTopicFilter(topicFilter) && TopicUtils.match(topicFilter, mqttMessage.getTopic())) {
					MqttMessageListener mqttMessageListener = mqttMessageListenerContext.get(topicFilter);
					mqttMessageListener.onMessage(mqttMessage);
				}
				else if (TopicFilterType.SHARE.equals(TopicFilterType.getType(topicFilter))
						&& TopicFilterType.SHARE.match(topicFilter, mqttMessage.getTopic())) {
					MqttMessageListener mqttMessageListener = mqttMessageListenerContext.get(topicFilter);
					mqttMessageListener.onMessage(mqttMessage);
				}
				else if (topicFilter.equals(mqttMessage.getTopic())) {
					MqttMessageListener mqttMessageListener = mqttMessageListenerContext.get(topicFilter);
					mqttMessageListener.onMessage(mqttMessage);
				}
				else {
					log.error("Topic filter match failed, topic: {}, message: {}", topicFilter, mqttMessage);
				}
			}
			catch (Exception e) {
				log.debug("error occurred when handling mqtt message", e);
			}
		}
	}

	@Override
	public void afterPropertiesSet() {
		Assert.notNull(applicationContext, "applicationContext must not be null");
		if (CollectionUtils.isEmpty(mqttMessageListenerList)) {
			return;
		}

		for (MqttMessageListener mqttMessageListener : mqttMessageListenerList) {
			Class<?> clazz = ClassUtils.getUserClass(mqttMessageListener);
			MqttListener mqttListener = AnnotationUtils.findAnnotation(clazz, MqttListener.class);
			if (Objects.isNull(mqttListener)) {
				continue;
			}
			String topic = mqttListener.topic();
			mqttOptions.addTopic(topic, mqttListener.qos());
			mqttMessageListenerContext.put(topic, mqttMessageListener);
			log.debug("register topic listener {} success，topic: {}", mqttMessageListener.getClass().getName(), topic);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
