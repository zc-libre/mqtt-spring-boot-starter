package com.libre.mqtt.config;

import com.libre.mqtt.*;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;

import static com.libre.mqtt.MqttProperties.MQTT_INPUT_CHANNEL_NAME;
import static com.libre.mqtt.MqttProperties.MQTT_OUT_BOUND_CHANNEL_NAME;


@AutoConfiguration
@EnableConfigurationProperties(MqttProperties.class)
@ConditionalOnProperty(prefix = "libre.mqtt", name = "enabled", matchIfMissing = true)
public class MqttAutoConfiguration {

    @Bean
    public MqttConnectOptions mqttConnectOptions(MqttProperties mqttProperties) {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setServerURIs(mqttProperties.getUrls());
        mqttConnectOptions.setPassword(mqttProperties.getPassword().toCharArray());
        mqttConnectOptions.setConnectionTimeout(mqttProperties.getConnectionTimeout());
        mqttConnectOptions.setAutomaticReconnect(mqttProperties.getAutomaticReconnect());
        mqttConnectOptions.setCleanSession(mqttProperties.getCleanSession());
        mqttConnectOptions.setCustomWebSocketHeaders(mqttProperties.getCustomWebSocketHeaders());
        mqttConnectOptions.setHttpsHostnameVerificationEnabled(mqttProperties.getHttpsHostnameVerificationEnabled());
        mqttConnectOptions.setKeepAliveInterval(mqttProperties.getKeepAliveInterval());
        mqttConnectOptions.setMaxInflight(mqttProperties.getMaxInflight());
        mqttConnectOptions.setExecutorServiceTimeout(mqttProperties.getExecutorServiceTimeout());
        mqttConnectOptions.setMaxReconnectDelay(mqttProperties.getMaxReconnectDelay());
        mqttConnectOptions.setSSLProperties(mqttProperties.getSslClientProps());
        mqttConnectOptions.setUserName(mqttProperties.getUsername());
        return mqttConnectOptions;
    }

    @Bean
    public MqttPahoClientFactory mqttPahoClientFactory(MqttConnectOptions mqttConnectOptions) {
        DefaultMqttPahoClientFactory clientFactory = new DefaultMqttPahoClientFactory();
        clientFactory.setConnectionOptions(mqttConnectOptions);
        return clientFactory;
    }

    @Bean(name = MQTT_INPUT_CHANNEL_NAME)
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean(name = MQTT_OUT_BOUND_CHANNEL_NAME)
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapter(
            MqttPahoClientFactory mqttPahoClientFactory, MqttProperties properties,
            @Qualifier(MQTT_INPUT_CHANNEL_NAME) MessageChannel mqttInputChannel, Environment environment) {
        MqttProperties.Consumer consumer = properties.getConsumer();
        String clientId = consumer.getClientId();
        if (!StringUtils.hasText(clientId)) {
            clientId = getClientId(environment);
        }
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(clientId,
                mqttPahoClientFactory);
        adapter.setAutoStartup(consumer.getAutoStartUp());
        adapter.setOutputChannel(mqttInputChannel);
        adapter.setQos(consumer.getQos());
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setCompletionTimeout(consumer.getCompletionTimeout());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = MQTT_OUT_BOUND_CHANNEL_NAME)
    public MqttPahoMessageHandler mqttOutbound(MqttPahoClientFactory mqttPahoClientFactory,
                                               MqttProperties mqttProperties, Environment environment) {
        MqttProperties.Producer producer = mqttProperties.getProducer();
        String clientId = producer.getClientId();
        if (!StringUtils.hasText(clientId)) {
            clientId = getClientId(environment);
        }
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(clientId, mqttPahoClientFactory);
        messageHandler.setAsync(producer.getAsync());
        messageHandler.setAsyncEvents(producer.getAsyncEvents());
        messageHandler.setDefaultTopic(producer.getDefaultTopic());
        messageHandler.setDefaultQos(producer.getDefaultQos());
        messageHandler.setConverter(new DefaultPahoMessageConverter());
        messageHandler.setDefaultRetained(producer.getDefaultRetained());
        return messageHandler;
    }

    @Bean
    public MqttOptions mqttOptions(MqttPahoMessageDrivenChannelAdapter adapter, MqttMessageGateWay mqttMessageGateWay) {
        return new MqttTemplate(adapter, mqttMessageGateWay);
    }

    @Bean
    public MqttMessageInboundHandler mqttMessageInboundHandler(List<MqttMessageListener> messageListeners,
                                                               MqttOptions mqttOptions, MqttProperties properties) {
        return new MqttMessageInboundHandler(messageListeners, mqttOptions, properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "libre.mqtt.consumer", name = "async", havingValue = "true")
    public ThreadPoolTaskExecutor mqttConsumerExecutor(MqttProperties properties) {
        MqttProperties.MqttExecutor executorProperties = properties.getConsumer().getExecutor();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(executorProperties.getCorePoolSize());
        executor.setMaxPoolSize(executorProperties.getMaxPoolSize());
        executor.setQueueCapacity(executorProperties.getQueueCapacity());
        executor.setKeepAliveSeconds(executorProperties.getKeepAliveSeconds());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix("mqtt-consumer-");
        executor.initialize();
        return executor;
    }

    private String getClientId(Environment environment) {
        String applicationName = environment.getProperty("spring.application.name", "client");
        return applicationName + "-" + UUID.randomUUID().toString().replace("-", "");
    }

}
