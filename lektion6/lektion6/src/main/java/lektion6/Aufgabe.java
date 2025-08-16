package lektion6;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
public class Aufgabe implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String melderName;      // optional (anonym erlaubt)
    private String telefonnummer;   // optional
    private String koordinaten;     // "lat, lon"
    private Double groesse;         // m² (geschätzt)

    @Enumerated(EnumType.STRING)
    private NetzStatus status = NetzStatus.GEMELDET;

    private String bergendePerson;  // max. 1 bergende Person (oder null)

    public Aufgabe() {}

    public Aufgabe(Long id, String melderName, String koordinaten, Double groesse,
                   NetzStatus status, String telefonnummer, String bergendePerson) {
        this.id = id;
        this.melderName = melderName;
        this.koordinaten = koordinaten;
        this.groesse = groesse;
        this.status = (status == null ? NetzStatus.GEMELDET : status);
        this.telefonnummer = telefonnummer;
        this.bergendePerson = bergendePerson;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMelderName() { return melderName; }
    public void setMelderName(String melderName) { this.melderName = melderName; }

    public String getTelefonnummer() { return telefonnummer; }
    public void setTelefonnummer(String telefonnummer) { this.telefonnummer = telefonnummer; }

    public String getKoordinaten() { return koordinaten; }
    public void setKoordinaten(String koordinaten) { this.koordinaten = koordinaten; }

    public Double getGroesse() { return groesse; }
    public void setGroesse(Double groesse) { this.groesse = groesse; }

    public NetzStatus getStatus() { return status; }
    public void setStatus(NetzStatus status) { this.status = status; }

    public String getBergendePerson() { return bergendePerson; }
    public void setBergendePerson(String bergendePerson) { this.bergendePerson = bergendePerson; }
}
