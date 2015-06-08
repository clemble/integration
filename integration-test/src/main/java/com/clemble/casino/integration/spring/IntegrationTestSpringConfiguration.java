package com.clemble.casino.integration.spring;

import java.util.ArrayList;
import java.util.List;

import com.clemble.casino.goal.spring.GoalConfigurationSpringConfiguration;
import com.clemble.casino.goal.spring.GoalConstructionSpringConfiguration;
import com.clemble.casino.goal.spring.GoalManagementSpringConfiguration;
import com.clemble.casino.goal.suggestion.spring.GoalSuggestionSpringConfiguration;
import com.clemble.casino.integration.event.SystemEventAccumulator;
import com.clemble.casino.integration.game.construction.EmailScenarios;
import com.clemble.casino.integration.player.IntegrationClembleCasinoRegistrationOperationsWrapper;
import com.clemble.casino.schedule.spring.ScheduleSpringConfiguration;
import com.clemble.casino.server.email.spring.PlayerEmailSpringConfiguration;
import com.clemble.casino.server.event.email.SystemEmailSendDirectRequestEvent;
import com.clemble.casino.server.event.email.SystemEmailSendRequestEvent;
import com.clemble.casino.server.event.phone.SystemPhoneSMSSendRequestEvent;
import com.clemble.casino.server.event.share.SystemSharePostEvent;
import com.clemble.casino.server.notification.spring.PlayerNotificationSpringConfiguration;
import com.clemble.casino.server.post.spring.PlayerFeedSpringConfiguration;
import com.clemble.casino.server.spring.WebJsonSpringConfiguration;
import com.clemble.casino.server.spring.common.PropertiesSpringConfiguration;
import com.clemble.casino.server.bonus.spring.PaymentBonusSpringConfiguration;
import com.clemble.casino.server.connection.spring.PlayerConnectionSpringConfiguration;
import com.clemble.casino.server.profile.spring.PlayerProfileSpringConfiguration;
import com.clemble.casino.server.social.spring.PlayerSocialSpringConfiguration;
import com.clemble.casino.server.registration.spring.RegistrationSpringConfiguration;
import com.clemble.server.tag.spring.TagSpringConfiguration;
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
import com.clemble.casino.client.ClembleCasinoRegistrationOperations;
import com.clemble.casino.client.error.ClembleResponseErrorHandler;
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
        public EmailScenarios emailScenarios(SystemEventAccumulator<SystemEmailSendDirectRequestEvent> systemEmailSendDirectRequestEventAccumulator) {
            return new EmailScenarios(systemEmailSendDirectRequestEventAccumulator);
        }

        @Bean
        public SystemEventAccumulator<SystemEmailSendDirectRequestEvent> systemEmailSendDirectRequestEventAccumulator(){
            return new SystemEventAccumulator<>(SystemEmailSendDirectRequestEvent.CHANNEL);
        }

        @Bean
        public SystemEventAccumulator<SystemEmailSendRequestEvent> systemEmailSendRequestEventAccumulator(){
            return new SystemEventAccumulator<>(SystemEmailSendRequestEvent.CHANNEL);
        }

        @Bean
        public SystemEventAccumulator<SystemPhoneSMSSendRequestEvent> systemPhoneSMSSendRequestEventAccumulator() {
            return new SystemEventAccumulator(SystemPhoneSMSSendRequestEvent.CHANNEL);
        }

        @Bean
        public SystemEventAccumulator<SystemSharePostEvent> systemSharePostEventAccumulator() {
            return new SystemEventAccumulator<>(SystemSharePostEvent.CHANNEL);
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
            restTemplate.setErrorHandler(new ClembleResponseErrorHandler(objectMapper));
            return restTemplate;
        }

        @Bean
        public ClembleCasinoRegistrationOperations registrationOperations() {
            return new IntegrationClembleCasinoRegistrationOperationsWrapper(new AndroidCasinoRegistrationTemplate(baseUrl));
        }

        @Bean
        public ClembleCasinoRegistrationOperations playerRegistrationService() {
            return new AndroidCasinoRegistrationTemplate(baseUrl);
        }

    }
}
