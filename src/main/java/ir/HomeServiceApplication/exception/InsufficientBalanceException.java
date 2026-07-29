package ir.HomeServiceApplication.exception;

import lombok.Getter;

@Getter
public class InsufficientBalanceException extends RuntimeException {

    private final Long currentBalance;
    private final Long requiredAmount;
    private final String chargeUrl;

    // used for general insufficient balance (withdraw, etc.)
    public InsufficientBalanceException(String message) {
        super(message);
        this.currentBalance = null;
        this.requiredAmount = null;
        this.chargeUrl = null;
    }

    // used when customer tries to pay but wallet is short — carries payment link
    public InsufficientBalanceException(Long currentBalance, Long requiredAmount, String chargeUrl) {
        super("Insufficient balance");
        this.currentBalance = currentBalance;
        this.requiredAmount = requiredAmount;
        this.chargeUrl = chargeUrl;
    }
}
