package DTO;

public class SpecialistSignupDTO {

    private String name;
    private String email;
    private String password;
     private byte[] profileImage;

    public String getName() {
        return name;
    }

    public void setName(String fullName) {
        this.name = name;
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

    public byte[] getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }
}