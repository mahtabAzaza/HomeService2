package service;

import entities.Service;
import entities.Specialist;

public interface ServiceService {

//add service
    void addService(Service service);

//remove service
    void removeService(Long serviceId);
//edit service
    void updateService(Service service);
//show services
    void Display(Service service);


}
