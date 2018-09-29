package dailyalex.com.openthedoor;

/**
 * Created by alex on 25.02.2018.
 */

public class User {

    private String msUsername;
    private String msPassword;
    private String msFirstName;
    private String msLastName;
    private boolean mbIsAdministrator;

    //=======================================================

    public User(String msFirstName, String msSecondName, String msUsername, String msPassword, boolean mbAdministrator) {
        this.msFirstName = msFirstName;
        this.msLastName = msSecondName;
        this.msUsername = msUsername;
        this.msPassword = msPassword;
        this.mbIsAdministrator = mbAdministrator;
    }
    //=======================================================
    public User() {}
    //=======================================================
    public String getUsername() {
        return msUsername;
    }
    //=======================================================
    public void setUsername(String msUsername) {
        this.msUsername = msUsername;
    }
    //=======================================================
    public String getPassword() {
        return msPassword;
    }
    //=======================================================
    public void setPassword(String msPassword) {
        this.msPassword = msPassword;
    }
    //=======================================================
    public String getFirstName() {
        return msFirstName;
    }
    //=======================================================
    public void setFirstName(String firstName) {
        this.msFirstName = firstName;
    }
    //=======================================================
    public String getLastName() {
        return msLastName;
    }
    //=======================================================
    public void setLastName(String lastName) {
        this.msLastName = lastName;
    }
    //=======================================================
    public boolean isAdministrator() {
        return mbIsAdministrator;
    }
    //=======================================================
    public void setIsAdministrator(boolean mbAdministrator) {
        this.mbIsAdministrator = mbAdministrator;
    }
    //=======================================================
}
