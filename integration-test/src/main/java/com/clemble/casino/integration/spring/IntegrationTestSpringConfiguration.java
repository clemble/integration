package com.clemble.casino.integration.spring;

import java.util.ArrayList;
import java.util.List;

import com.clemble.casino.goal.configuration.spring.GoalConfigurationSpringConfiguration;
import com.clemble.casino.goal.construction.spring.GoalConstructionSpringConfiguration;
import com.clemble.casino.goal.spring.GoalManagementSpringConfiguration;
import com.clemble.casino.goal.suggestion.spring.GoalSuggestionSpringConfiguration;
import com.clemble.casino.integration.event.EventAccumulator;
import com.clemble.casino.integration.game.construction.EmailScenarios;
import com.clemble.casino.integration.player.IntegrationClembleCasinoRegistrationOperationsWrapper;
import com.clemble.casino.schedule.spring.ScheduleSpringConfiguration;
import com.clemble.casino.server.email.spring.PlayerEmailSpringConfiguration;
import com.clemble.casino.server.event.SystemEvent;
import com.clemble.casino.server.notification.spring.PlayerNotificationSpringConfiguration;
import com.clemble.casino.server.player.notification.SystemEventListener;
import com.clemble.casino.server.player.notification.SystemNotificationServiceListener;
import com.clemble.casino.server.post.spring.PlayerFeedSpringConfiguration;
import com.clemble.casino.server.spring.WebJsonSpringConfiguration;
import com.clemble.casino.server.spring.common.PropertiesSpringConfiguration;
import com.clemble.casino.server.bonus.spring.PaymentBonusSpringConfiguration;
import com.clemble.casino.server.connection.spring.PlayerConnectionSpringConfiguration;
import com.clemble.casino.server.profile.spring.PlayerProfileSpringConfiguration;
import com.clemble.casino.server.social.spring.PlayerSocialSpringConfiguration;
import com.clemble.casino.server.registration.spring.RegistrationSpringConfiguration;
import com.clemble.server.tag.spring.TagSpringConfiguration;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.clemble.casino.android.AndroidCasinoRegistrationTemplate;
import com.clemble.casino.android.player.AndroidFacadeRegistrationService;
import com.clemble.casino.client.ClembleCasinoRegistrationOperations;
import com.clemble.casino.client.error.ClembleCasinoResponseErrorHandler;
import com.clemble.casino.registration.service.FacadeRegistrationService;
import com.clemble.casino.server.spring.common.JsonSpringConfiguration;
import com.clemble.casino.server.spring.web.ClientRestCommonSpringConfiguration;
import com.clemble.casino.server.payment.spring.PaymentSpringConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@Import(value = {
    BaseTestSpringConfiguration.class,
    JsonSpringConfiguration.class,
    IntegrationTestSpringConfiguration.LocalTestConfiguration.class,
    IntegrationTestSpringConfiguration.IntegrationTestConfiguration.class
})
public class IntegrationTestSpringConfiguration implements TestSpringConfiguration {


    @Configuration
    @Profile(DEFAULT)
    @Import({
        RegistrationSpringConfiguration.class,
        PlayerConnectionSpringConfiguration.class,
        PlayerProfileSpringConfiguration.class,
        PlayerSocialSpringConfiguration.class,
        PaymentBonusSpringConfiguration.class,
        PaymentSpringConfiguration.class,
        PlayerNotificationSpringConfiguration.class,
        GoalConstructionSpringConfiguration.class,
        GoalConfigurationSpringConfiguration.class,
        GoalManagementSpringConfiguration.class,
        WebJsonSpringConfiguration.class,
        GoalSuggestionSpringConfiguration.class,
        PlayerEmailSpringConfiguration.class,
        PlayerFeedSpringConfiguration.class,
        TagSpringConfiguration.class,
        ScheduleSpringConfiguration.class// For testing MappingJackson2HttpMessageConverter
    })
    public static class LocalTestConfiguration {

        @Bean
        public EmailScenarios emailScenarios(EventAccumulator<SystemEvent> systemEventAccumulator) {
            return new EmailScenarios(systemEventAccumulator);
        }

        @Autowired
        public SystemNotificationServiceListener serviceListener;

        @Bean
        public EventAccumulator<SystemEvent> systemEventAccumulator() {
            JsonSubTypes annotation = SystemEvent.class.getDeclaredAnnotation(JsonSubTypes.class);
            EventAccumulator<SystemEvent> eventAccumulator = new EventAccumulator<>();
            for(JsonSubTypes.Type type: annotation.value()){
                final String channel = type.name();
                serviceListener.subscribe(new SystemEventListener<SystemEvent>() {

                    @Override
                    public void onEvent(SystemEvent event) {
                        eventAccumulator.onEvent(event);
                    }

                    @Override
                    public String getChannel() {
                        return channel;
                    }

                    @Override
                    public String getQueueName() {
                        return "integration:" + channel;
                    }

                });
            }
            return eventAccumulator;
        }

    }

    @Configuration
    @Profile({ INTEGRATION_TEST, INTEGRATION_CLOUD, INTEGRATION_DEFAULT })
    @Import({PropertiesSpringConfiguration.class, ClientRestCommonSpringConfiguration.class })
    public static class IntegrationTestConfiguration {

        @Autowired
        @Qualifier("objectMapper")
        public ObjectMapper objectMapper;

        @Value("${clemble.host}")
        public String baseUrl;

        @Bean
        public RestTemplate restTemplate() {
            RestTemplate restTemplate = new RestTemplate();

            List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
            MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
            jackson2HttpMessageConverter.setObjectMapper(objectMapper);
            messageConverters.add(jackson2HttpMessageConverter);

            restTemplate.setMessageConverters(messageConverters);
            restTemplate.setErrorHandler(new ClembleCasinoResponseErrorHandler(objectMapper));
            return restTemplate;
        }

        @Bean
        public ClembleCasinoRegistrationOperations registrationOperations() {
            return new IntegrationClembleCasinoRegistrationOperationsWrapper(new AndroidCasinoRegistrationTemplate(baseUrl));
        }

        @Bean
        public FacadeRegistrationService playerRegistrationService() {
            return new AndroidFacadeRegistrationService(baseUrl);
        }

    }
}