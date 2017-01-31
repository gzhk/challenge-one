package com.gft.application.config;

import com.gft.path.PathTreeNodeObservableFactory;
import com.gft.path.collection.PathTreeNodeCollectionFactory;
import com.gft.path.treenode.PathTreeNodeFactory;
import com.gft.path.watcher.PathWatcherFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import java.nio.file.FileSystems;

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
    public PathTreeNodeFactory pathTreeNodeFactory() {
        return new PathTreeNodeFactory();
    }

    @Bean
    public PathTreeNodeCollectionFactory pathTreeNodeCollectionFactory() {
        return new PathTreeNodeCollectionFactory();
    }

    @Bean
    public PathTreeNodeObservableFactory pathTreeNodeObservableFactory(
        PathTreeNodeFactory pathTreeNodeFactory,
        PathTreeNodeCollectionFactory pathTreeNodeCollectionFactory
    ) {
        return new PathTreeNodeObservableFactory(pathTreeNodeFactory, pathTreeNodeCollectionFactory);
    }

    @Bean
    public PathWatcherFactory pathWatcherFactory() {
        return new PathWatcherFactory(FileSystems.getDefault());
    }
}
