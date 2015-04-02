package com.clemble.casino.integration;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import com.clemble.casino.player.event.PlayerInvitationDeclinedAction;
import com.clemble.casino.lifecycle.management.event.action.bet.BetAction;
import com.clemble.casino.lifecycle.management.event.action.surrender.GiveUpAction;
import com.clemble.casino.server.event.player.SystemPlayerProfileRegisteredEvent;
import com.clemble.casino.server.event.player.SystemPlayerSocialGrantRegisteredEvent;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.clemble.casino.event.Event;
import com.clemble.test.random.ObjectGenerator;
import com.clemble.test.reflection.AnnotationReflectionUtils;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ClembleIntegrationTest
public class IntegrationObjectMapperTest extends IntegrationObjectTest {

    @Autowired
    public ObjectMapper objectMapper;

//    TODO this is disabled because WebAppContext is not created by default
    @Autowired
    public MappingJackson2HttpMessageConverter httpMessageConverter;


    @Test
    public void testSpecialSerialization() {
        Assert.assertNull(checkSerialization(SystemPlayerProfileRegisteredEvent.class));
        Assert.assertNull(checkSerialization(SystemPlayerSocialGrantRegisteredEvent.class));
        Assert.assertNull(checkSerialization(PlayerInvitationDeclinedAction.class));
        Assert.assertNull(checkSerialization(BetAction.class));
    }

    @Test
    public void testGiveUpReadWrite() throws JsonParseException, JsonMappingException, IOException {
        GiveUpAction event = new GiveUpAction();
        String stringEvent = objectMapper.writeValueAsString(event);
        GiveUpAction readEvent = (GiveUpAction) objectMapper.readValue(stringEvent, Event.class);
        Assert.assertEquals(event, readEvent);
    }

    @Test
    public void testCreatorSerialization() throws IOException {
        List<Class<?>> candidates = AnnotationReflectionUtils.findCandidates("com.clemble.casino", JsonCreator.class);

        Map<Class<?>, Throwable> errors = new HashMap<Class<?>, Throwable>();

        for (Class<?> candidate : candidates) {
            Throwable error = checkSerialization(candidate);
            if (error != null) {
                errors.put(candidate, error);
            }
        }

        if (errors.size() != 0) {
            for (Entry<Class<?>, Throwable> problem : errors.entrySet()) {
                System.out.println("Problem " + problem.getKey().getSimpleName() + " > " + problem.getValue().getClass().getSimpleName());
                System.out.println("Sample: " + objectMapper.writeValueAsString(ObjectGenerator.generate(problem.getKey())));
            }
        }
        Assert.assertTrue(errors.toString(), errors.isEmpty());

    }

    @Test
    public void testSimpleSerialization() throws IOException {
        List<Class<?>> candidates = AnnotationReflectionUtils.findCandidates("com.clemble.casino", JsonTypeName.class);

        Map<Class<?>, Throwable> errors = new HashMap<Class<?>, Throwable>();

        for (Class<?> candidate : candidates) {
            Throwable error = checkSerialization(candidate);
            if (error != null) {
                errors.put(candidate, error);
            }
        }

        if (errors.size() != 0) {
            for (Entry<Class<?>, Throwable> problem : errors.entrySet()) {
                System.out.println("Problem " + problem.getKey().getSimpleName() + " > " + problem.getValue().getClass().getSimpleName());
                System.out.println("Sample: " + objectMapper.writeValueAsString(ObjectGenerator.generate(problem.getKey())));
            }
        }
        Assert.assertTrue(errors.toString(), errors.isEmpty());

    }

    private Throwable checkSerialization(Class<?> candidate) {
        Throwable error = null;
        try {
            Object expected = ObjectGenerator.generate(candidate);
            String stringPresentation = objectMapper.writeValueAsString(expected);
            Object actual = objectMapper.readValue(stringPresentation, candidate);

            assertEquals(stringPresentation, expected, actual);

            Class<?> originalClass = getOriginal(candidate);
            if (originalClass != null) {
                actual = objectMapper.readValue(stringPresentation, originalClass);

                Assert.assertEquals(expected, actual);

                MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
                httpMessageConverter.write(expected, MediaType.APPLICATION_JSON, outputMessage);

                HttpInputMessage inputMessage = new MockHttpInputMessage(outputMessage.getBodyAsBytes());
                actual = httpMessageConverter.read(candidate, inputMessage);

                Assert.assertEquals(expected, actual);

                inputMessage = new MockHttpInputMessage(outputMessage.getBodyAsBytes());
                actual = httpMessageConverter.read(originalClass, inputMessage);
                Assert.assertEquals(expected, actual);
            }
        } catch (Throwable throwable) {
            error = throwable;
        }
        return error;
    }

    final public Class<?> getOriginal(final Class<?> source) {
        Class<?> superClass = source;
        boolean found = false;
        Set<Class<?>> interfaces = new HashSet<Class<?>>();

        do {
            found = superClass.getAnnotation(JsonTypeInfo.class) != null;
            if (!found) {
                interfaces.addAll(Arrays.asList(superClass.getInterfaces()));
                superClass = superClass.getSuperclass();
            }
        } while (superClass != Object.class && !found && superClass != null);

        if (!found) {
            Set<Class<?>> interfaceParents = new HashSet<Class<?>>();
            do {
                for (Class<?> interfaceClass : interfaces) {
                    if (interfaceClass.getAnnotation(JsonTypeInfo.class) != null) {
                        superClass = interfaceClass;
                        found = true;
                        break;
                    } else {
                        if (interfaceClass.getSuperclass() != null && interfaceClass.getSuperclass() != Object.class) {
                            interfaceParents.add(interfaceClass);
                        }
                        interfaceParents.addAll(Arrays.asList(interfaceClass.getInterfaces()));
                    }
                }
                interfaces = interfaceParents;
                interfaceParents = new HashSet<Class<?>>();
            } while (!found && !interfaces.isEmpty());
        }

        return superClass != Object.class ? superClass : null;
    }

}
