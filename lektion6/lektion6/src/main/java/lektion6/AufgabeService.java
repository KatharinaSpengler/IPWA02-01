package lektion6;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class AufgabeService {

    @PersistenceContext
    private EntityManager em;

    public List<Aufgabe> findAll() {
        return em.createQuery("SELECT a FROM Aufgabe a ORDER BY a.id", Aufgabe.class)
                 .getResultList();
    }

    public Aufgabe findById(Long id) {
        return em.find(Aufgabe.class, id);
    }

    @Transactional
    public Aufgabe save(Aufgabe a) {
        if (a.getId() == null) {
            em.persist(a);
            return a;
        } else {
            return em.merge(a);
        }
    }

    @Transactional
    public void delete(Aufgabe a) {
        Aufgabe managed = (a.getId() != null) ? em.find(Aufgabe.class, a.getId()) : null;
        if (managed != null) {
            em.remove(managed);
        }
    }
}
