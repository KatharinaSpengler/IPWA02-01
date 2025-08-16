package lektion6;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

@Named("loginController")
@SessionScoped
public class LoginController implements Serializable {

    private static final long serialVersionUID = 1L;

    // --- Loginformular-Felder (deutsch) ---
    private String benutzername;
    private String passwort;

    // --- Session-Status ---
    private boolean eingeloggt;
    private boolean admin;
    private boolean bergend;

    // Anzeigename des eingeloggten Users (wird z.B. als "bergende Person" verwendet)
    private String aktuellerBenutzerName;

    // =========================================================
    // Login / Logout
    // =========================================================
    public String login() {
        if ("admin".equalsIgnoreCase(benutzername) && "admin".equals(passwort)) {
            eingeloggt = true;
            admin = true;
            bergend = false;

            if (aktuellerBenutzerName == null || aktuellerBenutzerName.isBlank()) {
                aktuellerBenutzerName = "Admin";
            }
            return "fischernetze?faces-redirect=true";
        }

        if ("bergend".equalsIgnoreCase(benutzername) && "bergend".equals(passwort)) {
            eingeloggt = true;
            admin = false;
            bergend = true;

            // Nutzer darf eigenen Anzeigenamen setzen; leer lassen, wenn noch keiner gewählt
            if (aktuellerBenutzerName == null) {
                aktuellerBenutzerName = "";
            }
            return "fischernetze?faces-redirect=true";
        }

        // Fehlermeldung anzeigen
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Login fehlgeschlagen", "Bitte Benutzername/Passwort prüfen."));
        return null;
    }

    public String logout() {
        eingeloggt = false;
        admin = false;
        bergend = false;

        benutzername = null;
        passwort = null;
        aktuellerBenutzerName = null;

        return "index?faces-redirect=true";
    }

    // =========================================================
    // Helfer / Komfort
    // =========================================================
    /** true, wenn ein nicht-leerer Anzeigename gesetzt ist. */
    public boolean getHasDisplayName() {
        return aktuellerBenutzerName != null && !aktuellerBenutzerName.isBlank();
    }

    /** true, wenn eingeloggt, aber noch kein Anzeigename gesetzt wurde. */
    public boolean getMustSetDisplayName() {
        return eingeloggt && !getHasDisplayName();
    }

    /** No-Op: Wird z. B. per Button aufgerufen, wenn der Anzeigename gesetzt wurde. */
    public void saveDisplayName() {
        // kein Persistieren nötig – Session-bezogen
        if (aktuellerBenutzerName != null) {
            aktuellerBenutzerName = aktuellerBenutzerName.trim();
        }
    }

    /** Fallback-Name für Anzeige (DisplayName oder Benutzername). */
    public String getDisplayName() {
        if (aktuellerBenutzerName != null && !aktuellerBenutzerName.isBlank()) {
            return aktuellerBenutzerName;
        }
        return benutzername;
    }

    // =========================================================
    // Getter/Setter – deutsche Namen (bestehend)
    // =========================================================
    public String getBenutzername() { return benutzername; }
    public void setBenutzername(String benutzername) { this.benutzername = benutzername; }

    public String getPasswort() { return passwort; }
    public void setPasswort(String passwort) { this.passwort = passwort; }

    public boolean getEingeloggt() { return eingeloggt; }
    public boolean getAdmin() { return admin; }
    public boolean getBergend() { return bergend; }

    public String getAktuellerBenutzerName() { return aktuellerBenutzerName; }
    public void setAktuellerBenutzerName(String aktuellerBenutzerName) { this.aktuellerBenutzerName = aktuellerBenutzerName; }

    // =========================================================
    // Alias-Getter/Setter – engl./gemischte Namen für EL-Kompatibilität
    // (damit alle vorhandenen Seiten funktionieren)
    // =========================================================

    // loggedIn / isLoggedIn (für #{loginController.loggedIn})
    public boolean isLoggedIn() { return eingeloggt; }
    public boolean getLoggedIn() { return eingeloggt; }

    // istEingeloggt() (ältere EL-Ausdrücke)
    public boolean istEingeloggt() { return eingeloggt; }

    // isAdmin / istAdmin
    public boolean isAdmin() { return admin; }
    public boolean istAdmin() { return admin; }

    // isBergend / istBergend
    public boolean isBergend() { return bergend; }
    public boolean istBergend() { return bergend; }

    // username/password (für Seiten, die englische Namen erwarten)
    public String getUsername() { return benutzername; }
    public void setUsername(String username) { this.benutzername = username; }

    public String getPassword() { return passwort; }
    public void setPassword(String password) { this.passwort = password; }

    // displayName (Alias)
    public String getDisplayname() { return getDisplayName(); } // toleranter Alias
    public void setDisplayname(String displayName) { this.aktuellerBenutzerName = displayName; }
}
