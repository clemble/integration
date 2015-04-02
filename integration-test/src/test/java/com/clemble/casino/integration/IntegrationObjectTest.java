package com.clemble.casino.integration;

import static com.clemble.test.random.ObjectGenerator.register;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.*;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import com.clemble.casino.bet.Bet;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import com.clemble.casino.goal.post.GoalStartedPost;
import com.clemble.casino.lifecycle.configuration.rule.bet.FixedBidRule;
import com.clemble.casino.notification.PlayerNotification;
import com.clemble.casino.payment.*;
import com.clemble.casino.player.event.PlayerInvitationAcceptedAction;
import com.clemble.casino.player.event.PlayerInvitationAction;
import com.clemble.casino.goal.lifecycle.management.GoalContext;
import com.clemble.casino.goal.lifecycle.management.GoalPlayerContext;
import com.clemble.casino.event.action.PlayerExpectedAction;
import com.clemble.casino.event.Event;
import com.clemble.casino.lifecycle.management.event.action.bet.BetAction;
import com.clemble.casino.lifecycle.management.event.action.surrender.GiveUpAction;

import com.clemble.casino.lifecycle.configuration.rule.ConfigurationRule;
import com.clemble.casino.player.notification.PlayerConnectedNotification;
import com.clemble.casino.post.PlayerPost;
import com.clemble.casino.security.ClembleConsumerDetails;
import com.clemble.casino.security.ClientDetails;
import com.clemble.casino.server.event.bet.SystemBetCanceledEvent;
import com.clemble.casino.server.event.schedule.SystemAddJobScheduleEvent;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Ignore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth.common.signature.RSAKeySecret;

import com.clemble.casino.VersionAware;
import com.clemble.casino.lifecycle.configuration.rule.bet.FixedBetRule;
import com.clemble.casino.lifecycle.configuration.rule.bet.LimitedBetRule;
import com.clemble.casino.lifecycle.configuration.rule.bet.UnlimitedBetRule;
import com.clemble.casino.money.Currency;
import com.clemble.casino.money.Money;
import com.clemble.casino.money.Operation;
import com.clemble.casino.player.PlayerGender;
import com.clemble.casino.player.PlayerProfile;
import com.clemble.casino.registration.PlayerCredential;
import com.clemble.casino.utils.ClembleConsumerDetailUtils;
import com.clemble.test.random.AbstractValueGenerator;
import com.clemble.test.random.ObjectGenerator;
import com.clemble.test.random.ValueGenerator;

@Ignore
public class IntegrationObjectTest {

    static {
        ObjectGenerator.register(DateTimeZone.class, new AbstractValueGenerator<DateTimeZone>() {
            @Override
            public DateTimeZone generate() {
                return DateTimeZone.UTC;
            }
        });
        ObjectGenerator.register(DateTime.class, new AbstractValueGenerator<DateTime>() {
            @Override
            public DateTime generate() {
                return new DateTime(ObjectGenerator.generate(long.class));
            }
        });
        ObjectGenerator.register(SystemAddJobScheduleEvent.class, new AbstractValueGenerator<SystemAddJobScheduleEvent>() {
            @Override
            public SystemAddJobScheduleEvent generate() {
                return new SystemAddJobScheduleEvent(RandomStringUtils.random(5), RandomStringUtils.random(5), new SystemBetCanceledEvent("a", "a"), DateTime.now(DateTimeZone.UTC));
            }
        });
        ObjectGenerator.register(PlayerPost.class, new AbstractValueGenerator<PlayerPost>() {
            @Override
            public PlayerPost generate() {
                return new GoalStartedPost(
                        "A",
                        "AB",
                        ObjectGenerator.generate(GoalState.class),
                        DateTime.now(DateTimeZone.UTC)
                );
            }
        });
        ObjectGenerator.register(PlayerNotification.class, new AbstractValueGenerator<PlayerNotification>() {
            @Override
            public PlayerNotification generate() {
                return new PlayerConnectedNotification("A:B", "A", "B", DateTime.now(DateTimeZone.UTC));
            }
        });
        ObjectGenerator.register(FixedBidRule.class, new AbstractValueGenerator<FixedBidRule>() {
            @Override
            public FixedBidRule generate() {
                return FixedBidRule.create(ObjectGenerator.generate(Bet.class));
            }
        });
        ObjectGenerator.register(SortedSet.class, new AbstractValueGenerator<SortedSet>() {
            public SortedSet generate() {
                return new TreeSet();
            }
        });
        ObjectGenerator.register(GoalContext.class, new AbstractValueGenerator<GoalContext>() {
            @Override
            public GoalContext generate() {
                List<GoalPlayerContext> playerContexts = Collections.emptyList();
                return new GoalContext(null, playerContexts);
            }
        });
        register(PlayerInvitationAction.class, new AbstractValueGenerator<PlayerInvitationAction>() {
            @Override
            public PlayerInvitationAction generate() {
            return new PlayerInvitationAcceptedAction();
            }
        });
        register(PlayerExpectedAction.class, new AbstractValueGenerator<PlayerExpectedAction>() {
            @Override
            public PlayerExpectedAction generate() {
            return PlayerExpectedAction.fromClass(PlayerInvitationAcceptedAction.class);
            }
        });
        register(FixedBetRule.class, new AbstractValueGenerator<FixedBetRule>() {
            @Override
            public FixedBetRule generate() {
            return FixedBetRule.create(10);
            }
        });
        register(Event.class, new AbstractValueGenerator<Event>() {
            @Override
            public Event generate() {
            return new GiveUpAction();
            }
        });
        register(BetAction.class, new AbstractValueGenerator<BetAction>() {
            @Override
            public BetAction generate() {
            return new BetAction(100);
            }
        });
        register(PlayerAccount.class, new AbstractValueGenerator<PlayerAccount>() {
            @Override
            public PlayerAccount generate() {
            return new PlayerAccount(
                RandomStringUtils.random(5),
                ImmutableMap.of(Currency.point, Money.create(Currency.point, 500)),
                null);
            }
        });
        register(PaymentTransaction.class, new AbstractValueGenerator<PaymentTransaction>() {
            @Override
            public PaymentTransaction generate() {
                return new PaymentTransaction()
                        .setTransactionKey(RandomStringUtils.random(5))
                        .setTransactionDate(DateTime.now(DateTimeZone.UTC))
                        .setProcessingDate(DateTime.now(DateTimeZone.UTC))
                        .addOperation(
                                new PaymentOperation(RandomStringUtils.random(5), Money.create(Currency.point, 50), Operation.Credit))
                        .addOperation(
                                new PaymentOperation(RandomStringUtils.random(5), Money.create(Currency.point, 50), Operation.Debit));
            }
        });
        register(PlayerCredential.class, new AbstractValueGenerator<PlayerCredential>() {
            @Override
            public PlayerCredential generate() {
                return new PlayerCredential(RandomStringUtils.randomAlphabetic(10) + "@gmail.com", RandomStringUtils.random(10));
            }
        });
        register(PlayerProfile.class, new AbstractValueGenerator<PlayerProfile>() {
            @Override
            public PlayerProfile generate() {
                return new PlayerProfile().setBirthDate(new DateTime(0)).setFirstName(RandomStringUtils.randomAlphabetic(10))
                        .setGender(PlayerGender.M).setLastName(RandomStringUtils.randomAlphabetic(10)).setNickName(RandomStringUtils.randomAlphabetic(10))
                        .setPlayer(RandomStringUtils.random(5));
            }
        });
        register(ConfigurationRule.class, new AbstractValueGenerator<ConfigurationRule>() {
            @Override
            public ConfigurationRule generate() {
                return UnlimitedBetRule.INSTANCE;
            }
        });
        register(LimitedBetRule.class, new AbstractValueGenerator<LimitedBetRule>() {
            @Override
            public LimitedBetRule generate() {
                return LimitedBetRule.create(10, 200);
            }
        });
        register(VersionAware.class, "version", new ValueGenerator<Integer>() {
            @Override
            public Integer generate() {
                return 0;
            }

            @Override
            public int scope() {
                return 1;
            }

            public ValueGenerator<Integer> clone() {
                return this;
            }
        });

        final RSAKeySecret rsaKey = ClembleConsumerDetailUtils.randomKey();
        register(PrivateKey.class, new AbstractValueGenerator<PrivateKey>() {
            @Override
            public PrivateKey generate() {
                return rsaKey.getPrivateKey();
            }
        });
        register(PublicKey.class, new AbstractValueGenerator<PublicKey>() {
            @Override
            public PublicKey generate() {
                return rsaKey.getPublicKey();
            }
        });
        try {
            final KeyGenerator AES = KeyGenerator.getInstance("AES");
            AES.init(256, new SecureRandom());
            register(SecretKey.class, new AbstractValueGenerator<SecretKey>() {
                @Override
                public SecretKey generate() {
                    return AES.generateKey();
                }
            });
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        ObjectGenerator.register(ClembleConsumerDetails.class, new AbstractValueGenerator<ClembleConsumerDetails>() {
            @Override
            public ClembleConsumerDetails generate() {
                return new ClembleConsumerDetails(
                    "consumerKey",
                    "consumer",
                    rsaKey,
                    Collections.<GrantedAuthority>emptyList(),
                    new ClientDetails("consumer")
                );
            }
        });
    }

}
