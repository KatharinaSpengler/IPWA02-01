package lektion6;

public class Benutzer {
    private String username;
    private String rolle; // bergend/admin

    public Benutzer() {}
    public Benutzer(String username, String rolle) {
        this.username = username;
        this.rolle = rolle;
    }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRolle() { return rolle; }
    public void setRolle(String rolle) { this.rolle = rolle; }
}
