package cn.enncloud.iot.analogdevices.configuration;


import cn.enncloud.iot.analogdevices.constant.AdapterProperties;
import cn.enncloud.iot.analogdevices.process.MessageMqttProcessHandler;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Slf4j
@Configuration
public class MqttConfig {

    public static final String CHANNEL_NAME_IN = "mqttInBoundChannel";
    public static final String CHANNEL_NAME_OUT = "mqttOutBoundChannel";
    @Autowired
    private MessageMqttProcessHandler messageProcess;
    @Autowired
    private AdapterProperties adapterProperties;
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(adapterProperties.getHost().split(","));
        options.setUserName(adapterProperties.getUsername());
        options.setPassword(adapterProperties.getPassword().toCharArray());
        options.setCleanSession(adapterProperties.isCleanSession());
        factory.setConnectionOptions(options);
        return factory;
    }

    // publisher

    @Bean
    public IntegrationFlow mqttOutFlow() {
        //console input
//        return IntegrationFlows.from(CharacterStreamReadingMessageSource.stdin(),
//                e -> e.poller(Pollers.fixedDelay(1000)))
//                .transform(p -> p + " sent to MQTT")
//                .handle(mqttOutbound())
//                .get();
        return IntegrationFlows.from(outChannelMqtt())
                .handle(mqttOutbound())
                .get();
    }

    @Bean(name = CHANNEL_NAME_OUT)
    public MessageChannel outChannelMqtt() {
        return new DirectChannel();
    }

    @Bean
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(adapterProperties.getPublisher(), mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(adapterProperties.getTopic());
        return messageHandler;
    }

    // consumer

    @Bean
    public IntegrationFlow mqttInFlow() {
        return IntegrationFlows.from(mqttInbound())
//                .transform(p -> p + ", received from MQTT")
//                .handle(logger())
                .handle(accephandler())
                .get();
    }
    @Bean
//    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler accephandler() {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                Optional optional = Optional.ofNullable(message.getPayload());
                if(optional.isPresent()){
                    messageProcess.doProcess(message);
                }else{
                    log.info("my"+message.getHeaders());
                }

            }

        };
    }


    private LoggingHandler logger() {
        LoggingHandler loggingHandler = new LoggingHandler("INFO");
        loggingHandler.setLoggerName("siSample");
        return loggingHandler;
    }

    @Bean(name = CHANNEL_NAME_IN)
    public MessageProducerSupport mqttInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(adapterProperties.getConsumer(),
                mqttClientFactory(),adapterProperties.getTopic().split(","));
        adapter.setCompletionTimeout(adapterProperties.getTimeout());
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(adapterProperties.getQos());
        return adapter;
    }
//    @MessagingGateway(defaultRequestChannel = "outChannelMqtt")
//    public interface MessageWriter{
//        void write(String data);
//    }

}
