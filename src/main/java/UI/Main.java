package UI;

import entity.Customer;
import repository.CustomerRepository;
import repository.impl.CustomerRepositoryImpl;

public class Main {

    public static void main(String[] args) {
        Customer customer = new Customer();

        customer.setName("mina");
        customer.setEmail("min@test.com");
        customer.setPassword("1234");

        CustomerRepository customerRepository =
                new CustomerRepositoryImpl();

        customerRepository.save(customer);







//    Scanner sc = new Scanner(System.in);
//    while(true)
//            System.out.println("1. manager login")
//            System.out.println("2. specialist login")
//            System.out.println("3. customer login")
//            System.out.println("4. exit")
//    int choice = sc.nextInt();
//
//    {
//
//
//        switch (choice) {
//            case 1:
//                managerLogIn;
//                break;
//            case 2:
//                specialistLogIn
//                break;
//            case 3:
//                customerLogIn
//                        break;
//            case 4:
//                System.out.println ("exit");
//                        return;
//            default:
//                System.out.println("invalid choice");
//
//
//        }
//
//    }
    }
}