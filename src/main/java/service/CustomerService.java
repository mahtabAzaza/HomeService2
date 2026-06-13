package service;
import entities.Customer;
import entities.Order;
import entities.Review;
import entities.Wallet;


public interface CustomerService  {

    //sign up
    void SignUp(Customer customer);
    //login
    void LogIn(Customer customer);
    //change info
    void changeInformation(Customer customer);
    //see services
    void displayServices ();
    //register orders/ add orders
    void addOrder(Order order);
    //wallet
    void useWallet(Wallet wallet);
    //review
    void addReview(Review review);



}
