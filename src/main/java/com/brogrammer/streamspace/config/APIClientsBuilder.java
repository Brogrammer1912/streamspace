package com.brogrammer.streamspace.config;

import com.brogrammer.streamspace.www.APIBayClient;
import com.brogrammer.streamspace.www.MicrosoftStoreAPI;
import com.brogrammer.streamspace.www.YTSAPIClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class APIClientsBuilder {

    @Bean
    RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }

    @Bean
    HttpServiceProxyFactory httpServiceProxyFactory(RestClient restClient) {
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();
    }

    @Bean
    YTSAPIClient ytsapiClient(HttpServiceProxyFactory factory) {
        return factory.createClient(YTSAPIClient.class);
    }

    @Bean
    APIBayClient apiBayClient(HttpServiceProxyFactory factory) {
        return factory.createClient(APIBayClient.class);
    }

    @Bean
    MicrosoftStoreAPI microsoftStoreAPI(HttpServiceProxyFactory factory) {
        return factory.createClient(MicrosoftStoreAPI.class);
    }
}
