import java.util.*;

/**
 * Represents a simple bank that holds multiple {@link Account} objects and manages basic operations.
 * <p>
 * The Bank supports creating accounts (individually or in bulk), deleting accounts, and retrieving accounts.
 * It maintains a sequential account ID generator and a cache for random account selection.
 * </p>
 *
 * <p><b>Concurrency Note:</b> This implementation is <b>not thread-safe</b>.
 * Accessing the Bank from multiple threads concurrently may result in inconsistent state,
 * especially when adding, deleting, or retrieving accounts.</p>
 *
 * @author Georgios Papalamprakopoulos
 * @since October 2025
 */
public class Bank {

    private final Map<Integer, Account> accounts;
    private Integer[] cachedAccountIds;
    private final Random random;
    private int nextAvailableAccountId;

    /**
     * Constructs a new empty Bank.
     * Initializes internal data structures and sets the next available account ID to 1.
     */
    public Bank() {
        accounts = new HashMap<>();
        random = new Random();
        nextAvailableAccountId = 1;
    }

    /**
     * Creates multiple accounts in bulk with random initial balances.
     * Each account will have an owner name "Owner&lt;id&gt;".
     *
     * @param number the number of accounts to create; must be positive
     * @throws IllegalArgumentException if {@code number < 1}
     */
    public void createBulkAccounts(int number) {
        if (number < 1) {
            throw new IllegalArgumentException("Number of bulk accounts must be positive.");
        }
        for (int i = 0; i < number; i++) {
            createAccount("Owner" + nextAvailableAccountId, random.nextInt(1_00, 10000_00));
        }
    }

    /**
     * Creates a single account with the specified owner and initial balance.
     *
     * @param owner the name of the account owner; must not be null or blank
     * @param initialBalanceCents the initial balance in cents; must be >= 0
     * @throws IllegalArgumentException if {@code owner} is null/blank or {@code balance < 0}
     */
    public void createAccount(String owner, int initialBalanceCents) {
        if (owner == null || owner.isBlank()) {
            throw new IllegalArgumentException("Owner must not be null or empty.");
        }
        if (initialBalanceCents < 0) {
            throw new IllegalArgumentException("Initial balance must not be negative.");
        }
        Account account = new Account(nextAvailableAccountId, owner, initialBalanceCents);
        accounts.put(nextAvailableAccountId, account);
        nextAvailableAccountId++;
        cachedAccountIds = null;
    }

    /**
     * Deletes the account with the specified ID.
     *
     * @param id the ID of the account to delete; must be positive and exist
     * @throws IllegalArgumentException if {@code id < 1}
     * @throws NoSuchElementException if the account with specified {@code id} does not exist
     */
    public void deleteAccount(int id) {
        if (id < 1) {
            throw new IllegalArgumentException("Account ID must be positive number.");
        }
        if (!accounts.containsKey(id)) {
            throw new NoSuchElementException("Account with ID " + id + " does not exist.");
        }
        accounts.remove(id);
        cachedAccountIds = null;
    }

    /**
     * Returns an unmodifiable view of the accounts in the bank.
     * <p>
     * This map is read-only; modifications to it will throw {@link UnsupportedOperationException}.
     * </p>
     *
     * @return an unmodifiable {@link Map} of accounts keyed by account ID
     * @throws UnsupportedOperationException if try to modify returning Map
     */
    public Map<Integer, Account> getAccounts() {
        return Collections.unmodifiableMap(accounts);
    }

    /**
     * Returns the total number of accounts in the bank.
     *
     * @return the number of accounts
     */
    public int getAccountCount() {
        return accounts.size();
    }

    /**
     * Computes and returns the total balance in cents of all accounts in the bank.
     *
     * @return the total balanceCents of all accounts
     */
    public int getTotalBalanceCents() {
        int total = 0;
        for (Account account: accounts.values()) {
            total += account.getBalanceCents();
        }
        return total;
    }

    /**
     * Returns the next available account ID for a new account.
     *
     * @return the next account ID
     */
    public int getNextAvailableAccountId() {
        return nextAvailableAccountId;
    }

    /**
     * Returns a randomly selected account from the bank.
     * <p>
     * Internally uses a cached array of account IDs for performance. Cache is invalidated
     * when accounts are added or removed.
     * </p>
     *
     * @return a randomly selected {@link Account}, or {@code null} if the bank has no accounts
     */
    public Account getRandomAccount() {
        if (accounts.isEmpty()) {
            return null;
        }
        if (cachedAccountIds == null) {
            cachedAccountIds = accounts.keySet().toArray(new Integer[0]);
        }
        int randomAccountKey = cachedAccountIds[random.nextInt(cachedAccountIds.length)];
        return accounts.get(randomAccountKey);
    }

    /**
     * Retrieves the account with the specified ID.
     *
     * @param id the account ID
     * @return the {@link Account} with the given ID, or {@code null} if not found
     */
    public Account getAccount(int id) {
        return accounts.get(id);
    }
}