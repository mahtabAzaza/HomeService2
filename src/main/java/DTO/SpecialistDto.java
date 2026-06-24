package DTO;

import java.time.LocalDateTime;

public class SpecialistDto {

    private Long id;
    private String name ;
    private String email;
    private byte[] profileImage;
    private String status;
    private LocalDateTime customerRegisterDate;



    public Long getId() {
        return id;
    }

    public String getName() {
        return name ;
    }

    public void setName(String name) {
        this.name  = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getRegisterDate() {
        return customerRegisterDate;
    }

    public void setRegisterDate(LocalDateTime registerDate) {
        this.customerRegisterDate = registerDate;
    }

    public byte[] getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }
}