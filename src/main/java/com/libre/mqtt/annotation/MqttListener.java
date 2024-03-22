package com.libre.mqtt.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Component
public @interface MqttListener {

	String topic();

	int qos() default 0;

}
