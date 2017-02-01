package com.gft.application.config;

import com.gft.application.file.model.PathViewFactory;
import com.gft.application.file.watcher.PathWatcherService;
import com.gft.path.PathTreeNodeObservableFactory;
import com.gft.path.collection.PathTreeNodeCollectionFactory;
import com.gft.path.treenode.PathTreeNodeFactory;
import com.gft.path.watcher.PathWatcherFactory;
import com.gft.path.watcher.async.AsyncPathWatcherFactory;
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
import java.util.concurrent.Executors;

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
        return new AsyncPathWatcherFactory(FileSystems.getDefault());
    }

    @Bean
    protected PathWatcherService pathWatcherService(
        @Value("${dir}") String path,
        SimpMessagingTemplate simpMessagingTemplate,
        PathTreeNodeObservableFactory pathTreeNodeObservableFactory,
        PathViewFactory pathViewFactory,
        PathWatcherFactory pathWatcherFactory
    ) {
        PathWatcherService pathWatcherService = new PathWatcherService(
            Executors.newSingleThreadExecutor(),
            simpMessagingTemplate,
            pathTreeNodeObservableFactory,
            pathViewFactory,
            pathWatcherFactory
        );
        pathWatcherService.watchPath(Paths.get(path));

        return pathWatcherService;
    }
}
