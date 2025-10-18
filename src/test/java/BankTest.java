import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the class {@link Bank}.
 *
 * @author Georgios Papalamprakopoulos
 * @since October 2025
 */
class BankTest {

    private Bank bank;

    @BeforeEach
    void setUp() {
        bank = new Bank();
    }

    @AfterEach
    void tearDown() {
        bank = null;
    }

    // Constructor tests
    @Test
    void whenConstructed_thenInitializesEmptyBank() throws Exception {
        assertNotNull(bank.getAccounts());
        assertTrue(bank.getAccounts().isEmpty());
        assertEquals(0, bank.getAccountCount());
        assertNull(accessPrivateField(bank, "cachedAccountIds"));
        assertNotNull(accessPrivateField(bank, "random"));
        assertEquals(1, bank.getNextAvailableAccountId());
    }

    // createBulkAccounts() tests
    @Test
    void givenNonPositiveNumber_whenCreateBulkAccounts_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> bank.createBulkAccounts(0));
        assertThrows(IllegalArgumentException.class, () -> bank.createBulkAccounts(-1));
    }

    @Test
    void givenPositiveNumber_whenCreateBulkAccounts_thenAccountsAreInitializedCorrectly() {
        int number = 100;
        bank.createBulkAccounts(number);
        assertEquals(100, bank.getAccountCount());
        assertEquals(101, bank.getNextAvailableAccountId());
        for (int i = 1; i <= number; i++) {
            Account account = bank.getAccount(i);
            assertNotNull(account);
            assertEquals(i, account.getId());
            assertTrue(account.getOwner().startsWith("Owner"));
            assertTrue(account.getBalanceCents() >= 0 && account.getBalanceCents() < 10000_00);
        }
    }

    // createAccount() tests
    @Test
    void givenNullOrBlankOwner_whenCreateAccount_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> bank.createAccount(null, 15_00));
        assertThrows(IllegalArgumentException.class, () -> bank.createAccount("", 15_00));
        assertThrows(IllegalArgumentException.class, () -> bank.createAccount("   ", 15_00));
        assertThrows(IllegalArgumentException.class, () -> bank.createAccount("\t\t", 15_00));
    }

    @Test
    void givenNegativeBalanceCents_whenCreateAccount_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> bank.createAccount("Owner", -1));
        assertThrows(IllegalArgumentException.class, () -> bank.createAccount("Owner", -10_00));
    }

    @Test
    void givenValidOwnerAndBalance_whenCreateAccount_thenAccountIsInitializedCorrectly() throws Exception {
        bank.createAccount("Owner1", 1000_00);
        Account account1 = bank.getAccount(1);

        assertNull(accessPrivateField(bank, "cachedAccountIds"));
        assertEquals(2, bank.getNextAvailableAccountId());
        assertEquals(1, bank.getAccountCount());

        assertNotNull(account1);
        assertEquals(1, account1.getId());
        assertEquals("Owner1", account1.getOwner());
        assertEquals(1000_00, account1.getBalanceCents());

        bank.createAccount("Owner2", 0);
        Account account2 = bank.getAccount(2);

        assertNull(accessPrivateField(bank, "cachedAccountIds"));
        assertEquals(2, bank.getAccountCount());
        assertEquals(3, bank.getNextAvailableAccountId());

        assertNotNull(account2);
        assertEquals(2, account2.getId());
        assertEquals("Owner2", account2.getOwner());
        assertEquals(0, account2.getBalanceCents());
    }

    // deleteAccount() tests
    @Test
    void givenNonPositiveId_whenDeleteAccount_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> bank.deleteAccount(-1));
        assertThrows(IllegalArgumentException.class, () -> bank.deleteAccount(0));
    }

    @Test
    void givenNonExistingAccountId_whenDeleteAccount_thenThrowsNoSuchElementException() {
        bank.createBulkAccounts(10);
        assertThrows(NoSuchElementException.class, () -> bank.deleteAccount(11));
    }

    @Test
    void givenExistingAccountId_whenDeleteAccount_thenAccountRemovedAndCacheInvalidated() throws Exception {
        bank.createBulkAccounts(10);
        bank.getRandomAccount(); // Triggers cache creation
        assertNotNull(accessPrivateField(bank, "cachedAccountIds"));
        bank.deleteAccount(5); // Trigger cache invalidation
        assertNull(accessPrivateField(bank, "cachedAccountIds"));
        assertNull(bank.getAccount(5));
        assertFalse(bank.getAccounts().containsKey(5));
        assertEquals(9, bank.getAccountCount());
    }

    // getTotalBalanceCents() tests
    @Test
    void givenBankWithAccounts_whenGetTotalBalanceCents_thenReturnsValidTotalBalanceCents() {
        bank.createAccount("Owner1", 10000_00);
        bank.createAccount("Owner2", 30000_00);
        bank.createAccount("Owner3", 55000_00);
        bank.createAccount("Owner4", 9000_00);

        assertEquals(104000_00, bank.getTotalBalanceCents());
    }

    // getRandomAccount tests
    @Test
    void givenEmptyBank_whenGetRandomAccount_thenReturnsNull() {
        assertNull(bank.getRandomAccount());
    }

    @Test
    void givenBankWithAccounts_whenGetRandomAccount_thenReturnsAnAccount() {
        bank.createBulkAccounts(3);
        for (int i = 0; i < 10; i++) {
            Account randomAccount = bank.getRandomAccount();
            assertNotNull(randomAccount);
            assertTrue(bank.getAccounts().containsValue(randomAccount));
        }
    }

    // getAccounts tests
    @Test
    void givenBankWithAccount_whenGetAccounts_thenReturnedMapIsUnmodifiable() {
        bank.createAccount("Owner1", 100);
        Map<Integer, Account> accounts = bank.getAccounts();
        assertThrows(UnsupportedOperationException.class, accounts::clear);
        assertEquals(1, accounts.size());
        bank.createAccount("Owner2", 200);
        assertEquals(2, bank.getAccounts().size());
    }

    // cachedAccountIds field related tests
    // Verify lazy caching and invalidation logic for performance optimization
    @Test
    void givenBankWithAccounts_whenGetRandomAccount_thenCachedAccountIdsIsUpdated() throws Exception {
        bank.createBulkAccounts(3);
        bank.getRandomAccount(); // Triggers cache creation
        assertNotNull(accessPrivateField(bank, "cachedAccountIds"));
    }

    @Test
    void givenBankWithAccounts_whenGetRandomAccount_AndCreateAccount_thenCachedAccountIdsIsInvalidated() throws Exception {
        bank.createBulkAccounts(3);
        bank.getRandomAccount(); // Triggers cache creation
        assertNotNull(accessPrivateField(bank, "cachedAccountIds"));
        bank.createAccount("NewOwner", 500); // Triggers cache invalidation
        assertNull(accessPrivateField(bank, "cachedAccountIds"));
    }

    @Test
    void givenBankWithAccounts_whenGetRandomAccount_thenCachedAccountIdsReference_RemainsTheSame() throws Exception {
        bank.createBulkAccounts(3);
        bank.getRandomAccount();
        Object cachedAccountIds = accessPrivateField(bank, "cachedAccountIds");
        bank.getRandomAccount();
        assertSame(cachedAccountIds, accessPrivateField(bank, "cachedAccountIds"));
    }

    // Utility Method
    private static Object accessPrivateField(Bank bank, String fieldName) throws Exception {
        Field field = Bank.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(bank);
    }
}