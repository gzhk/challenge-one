package com.gft.application.config;

import com.gft.application.file.model.PathViewFactory;
import com.gft.application.file.watcher.PathWatcherTask;
import com.gft.application.file.watcher.SendPathViewObserver;
import com.gft.node.NodePayloadIterableFactory;
import com.gft.node.NodePayloadObservableFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import java.io.IOException;
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

    @Bean
    public NodePayloadIterableFactory nodePayloadIterableFactory() {
        return new NodePayloadIterableFactory();
    }

    @Bean
    public NodePayloadObservableFactory nodePayloadIteratorObservableFactory(
        NodePayloadIterableFactory nodePayloadIterableFactory
    ) {
        return new NodePayloadObservableFactory(nodePayloadIterableFactory);
    }

    @Bean
    public SendPathViewObserver sendPathViewObserver(PathViewFactory pathViewFactory, SimpMessagingTemplate simpMessagingTemplate) {
        return new SendPathViewObserver(pathViewFactory, simpMessagingTemplate, LoggerFactory.getLogger(SendPathViewObserver.class));
    }

    @Bean(destroyMethod = "close")
    protected PathWatcherTask pathWatcherTask(@Value("${dir}") String rootPath, SendPathViewObserver observer) throws IOException {
        PathWatcherTask task = new PathWatcherTask(FileSystems.getDefault());
        task.watch(Paths.get(rootPath), observer);

        return task;
    }
}
