package training.web.config;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MqttPublisher {

    private static final Logger logger = LoggerFactory.getLogger(MqttPublisher.class);
    private final MqttProperties mqttProperties;
    private MqttClient client;

    public MqttPublisher(MqttProperties mqttProperties) {
        this.mqttProperties = mqttProperties;
        init();
    }

    private void init() {
        if (!mqttProperties.isEnabled()) {
            logger.info("MQTT is disabled, skipping connection");
            return;
        }

        try {
            logger.info("Initializing MQTT connection to: {}", mqttProperties.getUrl());
            
            client = new MqttClient(
                    mqttProperties.getUrl(),
                    mqttProperties.getClientId());
            logger.debug("MQTT client created");

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            options.setUserName(mqttProperties.getUsername());
            options.setPassword(mqttProperties.getPassword().toCharArray());
            logger.debug("MQTT connection options set");

            client.connect(options);
            logger.info("MQTT connected successfully: {}", mqttProperties.getUrl());

        } catch (Exception e) {
            logger.error("MQTT connection failed at initialization", e);
        }
    }

    public void publish(String topic, String message) {
        try {

            if (!mqttProperties.isEnabled()) {
                logger.info("MQTT DISABLED: {}", message);
                return;
            }

            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttMessage.setQos(0);

            client.publish(topic, mqttMessage);

        } catch (Exception e) {
            logger.error("MQTT publish failed for topic: {}, message: {}", topic, message, e);
        }
    }
}