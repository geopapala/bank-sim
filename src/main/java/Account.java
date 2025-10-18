/**
 * Represents a simple bank account with an ID, owner, and balance in cents.
 * <p>
 * Supports deposit, withdrawal, and transfer operations with input validation.
 * The balance is stored as an integer in cents to avoid floating-point rounding issues.
 * </p>
 *
 * @author Georgios Papalamprakopoulos
 * @since October 2025
 */
public class Account {

    private final int id;
    private final String owner;
    private int balanceCents;

    /**
     * Constructs a new Account with a specified ID, owner, and initial balance.
     *
     * @param id           the unique account ID (must be > 0)
     * @param owner        the name of the account owner (must not be null or blank)
     * @param balanceCents the initial balance in cents (must be >= 0)
     * @throws IllegalArgumentException if {@code id < 1}, {@code owner} is null/blank, or {@code balanceCents < 0}
     */
    public Account(int id, String owner, int balanceCents) {
        if (id < 1) {
            throw new IllegalArgumentException("Account ID must not be less than 1.");
        }
        if (owner == null || owner.isBlank()) {
            throw new IllegalArgumentException("Account owner must not be null or blank.");
        }
        if (balanceCents < 0) {
            throw new IllegalArgumentException("Balance must not be negative.");
        }
        this.id = id;
        this.owner = owner;
        this.balanceCents = balanceCents;
    }

    /**
     * Constructs a new Account with a specified ID, owner, and initial balance set to zero.
     *
     * @param id           the unique account ID (must be > 0)
     * @param owner        the name of the account owner (must not be null or blank)
     * @throws IllegalArgumentException if {@code id < 1} or {@code owner} is null/blank
     */
    public Account(int id, String owner) {
        this(id, owner, 0);
    }

    /**
     * Deposits a positive amount into this account.
     *
     * @param amountCents the amount to deposit in cents (must be > 0)
     * @throws IllegalArgumentException if {@code amountCents < 1}
     */
    public void deposit(int amountCents) {
        validatePositiveAmount(amountCents);
        balanceCents += amountCents;
    }

    /**
     * Withdraws a positive amount from this account.
     *
     * @param amountCents the amount to withdraw in cents (must be > 0)
     * @throws IllegalArgumentException if {@code amountCents < 1}
     * @throws IllegalStateException the {@code balance} is insufficient
     */
    public void withdraw(int amountCents) {
        validatePositiveAmount(amountCents);
        if (amountCents > balanceCents) {
            throw new IllegalStateException("Insufficient balance.");
        }
        balanceCents -= amountCents;
    }

    /**
     * Transfers a positive amount from this account to another account.
     *
     * @param amountCents the amount to transfer in cents (must be > 0)
     * @param other the target account (must not be null or the same as this account)
     * @throws IllegalArgumentException if {@code amountCents < 1}, if {@code other} is null, or
     *                                  if {@code other == this}
     * @throws IllegalStateException the {@code balance} is insufficient
     */
    public void transfer(int amountCents, Account other) {
        if (other == null) {
            throw new IllegalArgumentException("Target account must not be null.");
        }
        if (this == other) {
            throw new IllegalArgumentException("Target account must not be the same account.");
        }
        validatePositiveAmount(amountCents);
        if (amountCents > balanceCents) {
            throw new IllegalStateException("Insufficient balance.");
        }

        this.balanceCents -= amountCents;
        other.balanceCents += amountCents;
    }

    /**
     * Returns a string representation of the account, including id, owner, and balance in euros.
     *
     * @return the string representation of the account
     */
    @Override
    public String toString() {
        return String.format("Account{id=%d, owner='%s', balance=%.2f€}", id, owner, (balanceCents / 100.00));
    }

    /**
     * Returns the ID of the account.
     *
     * @return the account ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the account owner name.
     *
     * @return the owner name
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Returns the current balance in cents.
     *
     * @return the balance in cents
     */
    public int getBalanceCents() {
        return balanceCents;
    }

    /**
     * Validates that the given amount is positive (> 0).
     *
     * @param amountCents the amount to validate
     * @throws IllegalArgumentException if {@code amountCents < 1}
     */
    private void validatePositiveAmount(int amountCents) {
        if (amountCents < 1) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
    }
}