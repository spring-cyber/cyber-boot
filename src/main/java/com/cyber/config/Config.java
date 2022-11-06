
package com.cyber.config;

import com.cyber.interceptor.JWTTokenInterceptor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hubspot.jackson.datatype.protobuf.ProtobufModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.net.ssl.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Configuration
public class Config  {

    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

    @Autowired
    okhttp3.OkHttpClient okHttpClient;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(new OkHttp3ClientHttpRequestFactory(okHttpClient));
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM, MediaType.parseMediaType("application/x-tar")));
        restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder -> {
            jacksonObjectMapperBuilder.featuresToDisable(
                    JsonGenerator.Feature.IGNORE_UNKNOWN,
                    MapperFeature.DEFAULT_VIEW_INCLUSION,
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                    SerializationFeature.FAIL_ON_EMPTY_BEANS,
                    SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                    SerializationFeature.FAIL_ON_SELF_REFERENCES,
                    SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS
            );
            jacksonObjectMapperBuilder.propertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
            jacksonObjectMapperBuilder.modulesToInstall(ProtobufModule.class);
        };
    }
}
