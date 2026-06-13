package service;


public interface WalletService {

    // show balance
    double showBalance(long walletID);
    // charge wallet
    void chargeWallet(long walletID, double amount);
    // pay for order
    void payForOrder(long customerId, long orderID );
    // withdraw money
    void withdrawMoney(long walletID, double amount, String cardNumber);

}
