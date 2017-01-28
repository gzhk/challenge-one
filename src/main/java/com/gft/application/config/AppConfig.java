package com.gft.application.config;

import com.gft.path.collection.PathTreeNodeCollectionFactory;
import com.gft.path.treenode.PathTreeNodeFactory;
import com.gft.path.watcher.PathService;
import com.gft.path.watcher.RecursivePathWatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.WatchService;
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

    @Bean(destroyMethod = "close")
    public RecursivePathWatcher recursivePathWatcher(@Value("${dir}") String dir) throws IOException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        RecursivePathWatcher recursivePathWatcher = new RecursivePathWatcher(
            Paths.get(dir),
            watchService,
            Executors.newSingleThreadExecutor(),
            new PathService()
        );
        recursivePathWatcher.start();

        return recursivePathWatcher;
    }
}
