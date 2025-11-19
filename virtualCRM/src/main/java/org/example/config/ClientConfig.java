package org.example.config;

import org.example.client.InternalCRMClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {

    @Bean
    public InternalCRMClient internalCRMClient() throws Exception {
        return new InternalCRMClient("localhost", 9090);
    }

}
