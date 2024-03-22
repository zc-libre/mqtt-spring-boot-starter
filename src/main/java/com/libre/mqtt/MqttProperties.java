package com.libre.mqtt;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.integration.mqtt.core.ClientManager;

import javax.net.ssl.HostnameVerifier;
import java.util.Properties;

@Data
@ConfigurationProperties("libre.mqtt")
public class MqttProperties {

	public final static String MQTT_INPUT_CHANNEL_NAME = "mqttInputChannel";

	public final static String MQTT_OUT_BOUND_CHANNEL_NAME = "mqttOutboundChannel";

	public final static String MQTT_CONSUMER_EXECUTOR = "mqttConsumerExecutor";

	private Boolean enabled = true;

	private String[] urls = { "tcp://127.0.0.1:1883" };

	private String username = "admin";

	private String password = "public";

	private Integer keepAliveInterval = 60;

	private Integer maxInflight = 10;

	private String willDestination;

	private MqttMessage willMessage = new MqttMessage();

	private Properties sslClientProps;

	private Boolean httpsHostnameVerificationEnabled = false;

	private HostnameVerifier sslHostnameVerifier;

	private Boolean cleanSession = true;

	private Integer connectionTimeout = 30;

	private Boolean automaticReconnect = true;

	private Integer maxReconnectDelay = 128000;

	private Properties customWebSocketHeaders;

	/**
	 * How long to wait in seconds when terminating the executor service
	 */
	private Integer executorServiceTimeout = 1;

	private Producer producer = new Producer();

	private Consumer consumer = new Consumer();

	@Getter
	@Setter
	public static class Producer {

		private Integer defaultQos = MqttQoS.AT_MOST_ONCE.value();

		private String clientId;

		private String defaultTopic = "producer";

		private Boolean async = false;

		private Boolean asyncEvents = false;

		private Boolean defaultRetained = false;

	}

	@Getter
	@Setter
	public static class Consumer {

		private Integer qos = MqttQoS.AT_MOST_ONCE.value();

		private Long completionTimeout = ClientManager.DEFAULT_COMPLETION_TIMEOUT;

		private Boolean autoStartUp = true;

		private String clientId;

		private Boolean async = false;

		private MqttExecutor executor = new MqttExecutor();

	}

	@Getter
	@Setter
	public static class MqttExecutor {

		private Integer corePoolSize = 5;

		private Integer maxPoolSize = 10;

		private Integer keepAliveSeconds = 60;

		private Integer queueCapacity = 512;

	}

}
