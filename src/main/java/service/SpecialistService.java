package service;
import entities.Proposal;
import entities.Specialist;

public interface SpecialistService{

//Sign up
    void signUp(Specialist specialist);
//login
    void logIn(String email, String password);
//add proposal for orders
    void addProposal( Proposal proposal);
    // add Long loggedInSpecialistId to method input

//use wallet
    void useWallet (Long specialistID, double balance);
//change info
    void changeInfo (Long specialistID, Specialist updatedSpecialist);

}
