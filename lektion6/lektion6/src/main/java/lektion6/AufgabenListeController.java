package lektion6;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Named("aufgabenListeController")
@SessionScoped
public class AufgabenListeController implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject private AufgabeService aufgabeService;
    @Inject private LoginController loginController;

    private List<Aufgabe> netze = new ArrayList<>();

    // Formularfelder
    private String eingabeName;
    private String eingabeTelefon;
    private String eingabeKoordinaten;
    private Integer eingabeGroesse;

    // Signal für Seiten (z.B. Karte), dass es neue Daten gibt
    private volatile long dataVersion = 0L;

    @PostConstruct
    public void init() {
        reload();
        if (netze.isEmpty()) {
            beispieldatenAnlegen();
        }
    }

    public void reload() {
        try {
            List<Aufgabe> alle = aufgabeService.findAll();
            netze = (alle != null) ? alle : new ArrayList<>();
        } catch (Exception ignore) {
            if (netze == null) netze = new ArrayList<>();
        }
    }

    private void bumpVersion() { dataVersion++; }
    public long getDataVersion() { return dataVersion; }

    private void beispieldatenAnlegen() {
        safeAdd(new Aufgabe(null, "Max Fischer", "54.50,6.50", 120d, NetzStatus.GEMELDET, "0151-1234567", null));
        safeAdd(new Aufgabe(null, "Anna Taucher", "44.50,-7.50", 50d,  NetzStatus.GEBORGEN, "0170-9876543", "Peter Meeresretter"));
        safeAdd(new Aufgabe(null, "Lars Küste", "36.80,-123.50",200d, NetzStatus.BEVORSTEHEND, "0160-5555555", "Katharina"));
        safeAdd(new Aufgabe(null, "Ines Hafen", "-35.00,152.00",300d, NetzStatus.VERSCHOLLEN, "0152-1112223", null));
    }

    // ================= Aktionen =================

    @Transactional
    public void melden() {
        try {
            Aufgabe a = new Aufgabe();
            a.setMelderName(trimToNull(eingabeName));
            a.setTelefonnummer(trimToNull(eingabeTelefon));
            a.setKoordinaten(trimToNull(eingabeKoordinaten));
            a.setGroesse(eingabeGroesse != null ? eingabeGroesse.doubleValue() : null);
            a.setStatus(NetzStatus.GEMELDET);
            a.setBergendePerson(null);

            safeAdd(a);
            clearForm();

            addMessage(FacesMessage.SEVERITY_INFO, "Netz gemeldet", "Vielen Dank für deine Unterstützung!");
        } catch (Exception ex) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Fehler beim Melden", "Das Netz konnte nicht gespeichert werden.");
        }
    }

    @Transactional
    public void uebernehmen(Aufgabe netz) {
        if (!isAktionErlaubt()) {
            addMessage(FacesMessage.SEVERITY_WARN, "Nicht erlaubt", "Bitte melde dich als bergende Person oder Admin an.");
            return;
        }
        String name = loginController.getAktuellerBenutzerName();
        if (name == null || name.isBlank()) name = "Bergungsteam";

        netz.setBergendePerson(name);
        netz.setStatus(NetzStatus.BEVORSTEHEND);
        safeUpdate(netz);

        addMessage(FacesMessage.SEVERITY_INFO, "Bergung übernommen", "Bergende Person: " + name);
    }

    @Transactional
    public void verschollen(Aufgabe netz) {
        netz.setStatus(NetzStatus.VERSCHOLLEN);
        safeUpdate(netz);
        addMessage(FacesMessage.SEVERITY_INFO, "Status geändert", "Netz ist als verschollen markiert.");
    }

    @Transactional
    public void geborgen(Aufgabe netz) {
        netz.setStatus(NetzStatus.GEBORGEN);
        safeUpdate(netz);
        addMessage(FacesMessage.SEVERITY_INFO, "Status geändert", "Netz ist geborgen.");
    }

    @Transactional
    public void loeschen(Aufgabe netz) {
        try {
            // Einfache Variante: direkt das Entity löschen (Service muss delete(Aufgabe) haben)
            aufgabeService.delete(netz);
        } catch (Exception ignore) { /* UI soll konsistent bleiben */ }

        netze.remove(netz);
        bumpVersion();
        addMessage(FacesMessage.SEVERITY_INFO, "Gelöscht", "Das Netz wurde entfernt.");
    }

    // ================= Persistenz-Helfer =================

    private void safeAdd(Aufgabe a) {
        try { aufgabeService.save(a); } catch (Exception ignore) { }
        netze.add(a);
        bumpVersion();
    }

    private void safeUpdate(Aufgabe a) {
        try { aufgabeService.save(a); } catch (Exception ignore) { }
        bumpVersion();
    }

    // ================= Helfer =================

    private boolean isAktionErlaubt() {
        return loginController != null
                && loginController.getEingeloggt()
                && (loginController.getBergend() || loginController.getAdmin());
    }

    private void clearForm() {
        eingabeName = "";
        eingabeTelefon = "";
        eingabeKoordinaten = "";
        eingabeGroesse = null;
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private void addMessage(FacesMessage.Severity sev, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(sev, summary, detail));
    }

    // ================= Getter/Setter =================

    public List<Aufgabe> getNetze() { return netze; }

    public List<Aufgabe> getNetzeMitBergenden() {
        if (netze == null) return List.of();
        return netze.stream()
                .filter(a -> a.getBergendePerson() != null && !a.getBergendePerson().isBlank())
                .collect(Collectors.toList());
    }

    public String getEingabeName() { return eingabeName; }
    public void setEingabeName(String eingabeName) { this.eingabeName = eingabeName; }

    public String getEingabeTelefon() { return eingabeTelefon; }
    public void setEingabeTelefon(String eingabeTelefon) { this.eingabeTelefon = eingabeTelefon; }

    public String getEingabeKoordinaten() { return eingabeKoordinaten; }
    public void setEingabeKoordinaten(String eingabeKoordinaten) { this.eingabeKoordinaten = eingabeKoordinaten; }

    public Integer getEingabeGroesse() { return eingabeGroesse; }
    public void setEingabeGroesse(Integer eingabeGroesse) { this.eingabeGroesse = eingabeGroesse; }
}
