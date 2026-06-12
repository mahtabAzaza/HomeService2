package service;

import entities.Manager;
import jakarta.persistence.ManyToOne;

public class ManagerService {

    @ManyToOne
    public Manager manager;





    public void managerLogIn(String name, String password) {
        this.manager = new Manager();
        return;
    }
}
