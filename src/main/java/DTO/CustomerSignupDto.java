package DTO;

import java.time.LocalDateTime;

public class CustomerSignupDto {


    private String name ;
    private String email;
    private String password;
    private byte[] profilePicture;
    private LocalDateTime customerRegisterDate;



    // getters and setters
    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }


    public String getName() {
        return name ;
    }

    public void setName(String fullName) {
        this.name  = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCustomerRegisterDate() {
        return customerRegisterDate;
    }

    public void setCustomerRegisterDate(LocalDateTime customerRegisterDate) {
        this.customerRegisterDate = customerRegisterDate;
    }
}

