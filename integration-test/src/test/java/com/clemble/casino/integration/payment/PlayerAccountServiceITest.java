package com.clemble.casino.integration.payment;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.clemble.casino.integration.ClembleIntegrationTest;
import com.clemble.casino.integration.utils.AsyncUtils;
import com.clemble.casino.money.MoneySource;
import com.clemble.casino.payment.bonus.RegistrationBonusPaymentSource;
import com.clemble.casino.payment.event.PaymentEvent;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.client.event.EventListener;
import com.clemble.casino.client.event.EventTypeSelector;
import com.clemble.casino.error.ClembleErrorCode;
import com.clemble.casino.integration.game.construction.PlayerScenarios;
import com.clemble.casino.test.util.ClembleCasinoExceptionMatcherFactory;
import com.clemble.casino.payment.PaymentOperation;
import com.clemble.casino.payment.PaymentTransaction;
import com.clemble.casino.payment.PlayerAccount;
import com.clemble.casino.money.Currency;
import com.clemble.casino.money.Money;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ClembleIntegrationTest
public class PlayerAccountServiceITest {

    @Autowired
    public PlayerScenarios playerOperations;

//    @Autowired
//    public GameScenarios gameOperations;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testInitialAmount() throws InterruptedException {
        // Step 1. Creating random player A
        final ClembleCasinoOperations A = playerOperations.createPlayer();
        // Step 1.1 Registering bonus event listener, and waiting
        final BlockingQueue<PaymentEvent> bonusLatch = new ArrayBlockingQueue<>(2);
        A.listenerOperations().subscribe(new EventTypeSelector(PaymentEvent.class), new EventListener<PaymentEvent>() {
            @Override
            public void onEvent(PaymentEvent event) {
                bonusLatch.add(event);
            }
        });
        AsyncUtils.verify(() -> A.paymentService().myTransactions().size() == 1);
        // Step 2. Fetching account and precondition
        PlayerAccount accountA = A.accountService().myAccount();
        assertTrue(accountA.getMoney().size() > 0);
        assertTrue(accountA.getMoney(Currency.point).getAmount() > 0);
        // Step 3. Checking registration transaction
        A.paymentService().myTransactions();
        String transactionKey = RegistrationBonusPaymentSource.INSTANCE.toTransactionKey(A.getPlayer());
        PaymentTransaction transaction = A.paymentService().getTransaction(transactionKey);
        Set<PaymentOperation> paymentOperations = transaction.getOperations();
        Money transactionAmount = paymentOperations.iterator().next().getAmount();
        // Step 4.1 Checking daily bonus transaction (Which might be delayed, because of the system event delays)
        assertEquals(transactionAmount, A.accountService().myAccount().getMoney(Currency.point));
    }
//
//    @Test
//    public void runningOutOfMoney() throws Exception {
//        // TODO can fail, because cash transactions are asynchronous (Need to manage this)
//        final ClembleCasinoOperations A = playerOperations.createPlayer();
//        final ClembleCasinoOperations B = playerOperations.createPlayer();
//
//        AsyncCompletionUtils.equals(new Get<Money>() {
//            @Override
//            public Money get() {
//                return A.accountService().myAccount().getMoney(Currency.point);
//            }
//        }, new Get<Money>() {
//            @Override
//            public Money get() {
//                return B.accountService().myAccount().getMoney(Currency.point);
//            }
//        });
//
//        expectedException.expect(ClembleCasinoExceptionMatcherFactory.fromErrors(ClembleCasinoError.GameConstructionInsufficientMoney));
//
//        do {
//            final Money cashAbefore = A.accountService().myAccount().getMoney(Currency.point);
//            final Money cashBbefore = B.accountService().myAccount().getMoney(Currency.point);
//
//            assertTrue("Unexpected amount " + cashAbefore.getAmount() + " for " + A.getPlayer(), cashAbefore.getAmount() >= 0);
//            assertTrue("Unexpected amount " + cashBbefore.getAmount() + " for " + B.getPlayer(),cashBbefore.getAmount() >= 0);
//
//            final RoundGamePlayer AvsB = gameOperations.round(Game.num, A, B.getPlayer());
//            final RoundGamePlayer BvsA = gameOperations.accept(AvsB.getSessionKey(), B);
//
//            AvsB.waitForStart();
//            BvsA.waitForStart();
//
//            AvsB.perform(new SelectNumberAction(3));
//            BvsA.perform(new SelectNumberAction(4));
//
//            BvsA.syncWith(AvsB);
//
////            assertFalse(BvsA.isAlive());
////            assertFalse(AvsB.isAlive());
//
//            final Money price = AvsB.getConfiguration().getPrice();
//
//            AsyncUtils.verifyEquals(
//                () -> cashAbefore.add(price.negate()),
//                () -> A.accountService().myAccount().getMoney(Currency.point)
//            );
//            AsyncUtils.verify(() -> cashBbefore.add(price).equals(B.accountService().myAccount().getMoney(Currency.point)));
//        } while (true);
//    }

    @Ignore // TODO security was temporary disabled
    @Test
    public void testAmountAfterRegistration() {
        // Step 1. Creating player
        ClembleCasinoOperations player = playerOperations.createPlayer();
        // Step 2. Checking there is at least one
        PlayerAccount accountA = player.accountService().myAccount();
        // Step 3. Checking that there are some fake moneys in the newly created account
        assertNotNull(accountA);
        assertNotNull(accountA.getMoney(Currency.point));
        assertTrue(accountA.getMoney(Currency.point).getAmount() > 0);
        // Step 4. Checking that there are some fake moneys in the newly created account, accesed through WalletOperations
        PlayerAccount accountB = player.accountService().myAccount();
        assertNotNull(accountB);
        assertNotNull(accountB.getMoney(Currency.point));
        assertEquals(accountB, accountA);
        // Step 5. Checking that there are some fake moneys in the newly created account, accesed through another WalletOperations
        PlayerAccount anotherWallet = player.accountService().myAccount();
        assertNotNull(anotherWallet);
        assertNotNull(anotherWallet.getMoney(Currency.point));
        assertEquals(anotherWallet, accountA);
    }

    @Ignore // TODO security was temporary disabled
    @Test
    public void testTransactionsListAccess() {
        // Step 1. Checking player has no transactions to access
        ClembleCasinoOperations player = playerOperations.createPlayer();
        List<PaymentTransaction> transactions = player.paymentService().myTransactions();
        Assert.assertFalse(transactions.isEmpty());
        // Step 2. Checking no other player can't access the transactions
        ClembleCasinoOperations anotherPlayer = playerOperations.createPlayer();
        // Step 3. Checking no other player can access the transactions
        expectedException.expect(ClembleCasinoExceptionMatcherFactory.fromErrors(ClembleErrorCode.PaymentTransactionAccessDenied));
        player.paymentService().getTransaction(anotherPlayer.getPlayer() + MoneySource.registration);
    }

    @Test
    public void testAccessNonExistent() {
        // Step 1. Checking player has no transactions to access
        ClembleCasinoOperations player = playerOperations.createPlayer();
        assertNull(player.paymentService().getTransaction("TicTacToe-1"));
    }

}
