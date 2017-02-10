package com.gft.application.config;

import com.gft.application.file.model.PathViewFactory;
import com.gft.application.file.watcher.PathWatcherService;
import com.gft.application.file.watcher.PathWatcherTask;
import com.gft.node.NodePayloadIterableFactory;
import com.gft.node.NodePayloadIteratorObservableFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import java.nio.file.FileSystems;
import java.nio.file.Paths;

@Configuration
@EnableWebSocketMessageBroker
public class AppConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
            .addEndpoint("/websocket")
            .setAllowedOrigins("*")
            .withSockJS();
    }

//    @Bean
//    public AsyncPathWatcherFactory asyncPathWatcherFactory() {
//        return new AsyncPathWatcherFactory(FileSystems.getDefault());
//    }

    @Bean
    public NodePayloadIterableFactory nodePayloadIterableFactory() {
        return new NodePayloadIterableFactory();
    }

    @Bean
    public NodePayloadIteratorObservableFactory nodePayloadIteratorObservableFactory(
        NodePayloadIterableFactory nodePayloadIterableFactory
    ) {
        return new NodePayloadIteratorObservableFactory(nodePayloadIterableFactory);
    }

    @Bean
    protected PathWatcherTask pathWatcherTask(
        @Value("${dir}") String path,
        PathWatcherService pathWatcherService,
        SimpMessagingTemplate simpMessagingTemplate,
        PathViewFactory pathViewFactory
    ) {
        PathWatcherTask task = new PathWatcherTask(pathWatcherService, simpMessagingTemplate, pathViewFactory);
        task.watchAndSend(Paths.get(path));

        return task;
    }
}
