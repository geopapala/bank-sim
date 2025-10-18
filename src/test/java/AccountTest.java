import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the class {@link Account}.
 *
 * @author Georgios Papalamprakopoulos
 * @since October 2025
 */
class AccountTest {

    private Account account;

    @AfterEach
    void tearDown() {
        account = null;
    }

    // Constructor tests
    @Test
    void givenNonPositiveId_whenConstructed_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Account(-1, "Owner"));
        assertThrows(IllegalArgumentException.class, () -> new Account(0, "Owner"));
    }

    @Test
    void givenNullOrBlankOwner_whenConstructed_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Account(1, null));
        assertThrows(IllegalArgumentException.class, () -> new Account(2, ""));
        assertThrows(IllegalArgumentException.class, () -> new Account(3, "   "));
        assertThrows(IllegalArgumentException.class, () -> new Account(4, "\t\t"));
    }

    @Test
    void givenNegativeBalance_whenConstructed_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Account(1, "Owner", -100));
    }

    @Test
    void givenValidIdAndOwner_whenConstructed_thenFieldsAreSetCorrectlyWithZeroBalance() {
        account = new Account(1, "Owner");
        assertEquals(1, account.getId());
        assertEquals("Owner", account.getOwner());
        assertEquals(0, account.getBalanceCents());
    }

    @Test
    void givenValidIdOwnerAndBalance_whenConstructed_thenFieldsAreSetCorrectly() {
        account = new Account(1, "Owner", 1234);
        assertEquals(1, account.getId());
        assertEquals("Owner", account.getOwner());
        assertEquals(1234, account.getBalanceCents());
    }

    // deposit() tests
    @Test
    void givenNonPositiveAmount_whenDeposit_thenThrowsIllegalArgumentException() {
        account = new Account(1, "Owner", 0);
        assertThrows(IllegalArgumentException.class, () -> account.deposit(-100));
        assertThrows(IllegalArgumentException.class, () -> account.deposit(0));
    }

    @Test
    void givenPositiveAmount_whenDeposit_thenBalanceIncreasedCorrectly() {
        account = new Account(1, "Owner", 0);
        assertEquals(0, account.getBalanceCents());
        account.deposit(200);
        assertEquals(200, account.getBalanceCents());
        account.deposit(1000);
        assertEquals(1200, account.getBalanceCents());
    }

    // withdraw() tests
    @Test
    void givenNonPositiveAmount_whenWithdraw_thenThrowsIllegalArgumentException() {
        account = new Account(1, "Owner", 200);
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(-100));
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(0));
    }

    @Test
    void givenPositiveAmountThatExceedsBalance_whenWithdraw_thenThrowsIllegalStateException() {
        account = new Account(1, "Owner", 200);
        assertThrows(IllegalStateException.class, () -> account.withdraw(300));
    }

    @Test
    void givenPositiveAmount_whenWithdraw_thenBalanceDecreasedCorrectly() {
        account = new Account(1, "Owner", 200);
        assertEquals(200, account.getBalanceCents());
        account.withdraw(200);
        assertEquals(0, account.getBalanceCents());
    }

    // transfer() tests
    @Test
    void givenNullTargetAccount_whenTransfer_thenThrowsIllegalArgumentException() {
        account = new Account(1, "Owner1", 200);
        assertThrows(IllegalArgumentException.class, () -> account.transfer(200, null));
    }

    @Test
    void givenSameAccountAsTargetAccount_whenTransfer_thenThrowsIllegalArgumentException() {
        account = new Account(1, "Owner1", 200);
        assertThrows(IllegalArgumentException.class, () -> account.transfer(200, account));
    }

    @Test
    void givenNegativeAmount_whenTransfer_thenThrowsIllegalArgumentException() {
        account = new Account(1, "Owner1", 200);
        Account targetAccount = new Account(2, "Owner2", 500);
        assertThrows(IllegalArgumentException.class, () -> account.transfer(-100, targetAccount));
        assertThrows(IllegalArgumentException.class, () -> account.transfer(0, targetAccount));
    }

    @Test
    void givenPositiveAmountThatExceedsBalance_whenTransfer_thenThrowsIllegalStateException() {
        account = new Account(1, "Owner1", 500);
        Account targetAccount = new Account(2, "Owner2", 1000);
        assertThrows(IllegalStateException.class, () -> account.transfer(700, targetAccount));
    }

    @Test
    void givenPositiveAmount_whenTransfer_thenBothBalancesAlterCorrectly() {
        account = new Account(1, "Owner1", 500);
        Account targetAccount = new Account(2, "Owner2", 1000);
        account.transfer(200, targetAccount);
        assertEquals(1200, targetAccount.getBalanceCents());
        assertEquals(300, account.getBalanceCents());
    }

    //toString() tests
    @Test
    void whenCalledToString_thenReturnsCorrectAccountStringRepresentation() {
        account = new Account(1, "Owner1", 500);
        assertEquals("Account{id=1, owner='Owner1', balance=5,00€}", account.toString());
    }
}