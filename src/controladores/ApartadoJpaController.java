
package controladores;

import controladores.exceptions.IllegalOrphanException;
import controladores.exceptions.NonexistentEntityException;
import controladores.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import objetosNegocio.Usuario;
import objetosNegocio.MovimientoEnApartado;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import objetosNegocio.Apartado;
import objetosNegocio.TallaApartado;

/**
 *
 * @author Raul Karim Sabag Ballesteros
 */
public class ApartadoJpaController implements Serializable {

    public ApartadoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Apartado apartado) throws PreexistingEntityException, Exception {
        if (apartado.getMovimientoEnApartadoCollection() == null) {
            apartado.setMovimientoEnApartadoCollection(new ArrayList<MovimientoEnApartado>());
        }
        if (apartado.getTallaApartadoCollection() == null) {
            apartado.setTallaApartadoCollection(new ArrayList<TallaApartado>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario idUsuario = apartado.getIdUsuario();
            if (idUsuario != null) {
                idUsuario = em.getReference(idUsuario.getClass(), idUsuario.getIdUsuario());
                apartado.setIdUsuario(idUsuario);
            }
            Collection<MovimientoEnApartado> attachedMovimientoEnApartadoCollection = new ArrayList<MovimientoEnApartado>();
            for (MovimientoEnApartado movimientoEnApartadoCollectionMovimientoEnApartadoToAttach : apartado.getMovimientoEnApartadoCollection()) {
                movimientoEnApartadoCollectionMovimientoEnApartadoToAttach = em.getReference(movimientoEnApartadoCollectionMovimientoEnApartadoToAttach.getClass(), movimientoEnApartadoCollectionMovimientoEnApartadoToAttach.getIdMovimientoApartado());
                attachedMovimientoEnApartadoCollection.add(movimientoEnApartadoCollectionMovimientoEnApartadoToAttach);
            }
            apartado.setMovimientoEnApartadoCollection(attachedMovimientoEnApartadoCollection);
            Collection<TallaApartado> attachedTallaApartadoCollection = new ArrayList<TallaApartado>();
            for (TallaApartado tallaApartadoCollectionTallaApartadoToAttach : apartado.getTallaApartadoCollection()) {
                tallaApartadoCollectionTallaApartadoToAttach = em.getReference(tallaApartadoCollectionTallaApartadoToAttach.getClass(), tallaApartadoCollectionTallaApartadoToAttach.getIdTallaApartado());
                attachedTallaApartadoCollection.add(tallaApartadoCollectionTallaApartadoToAttach);
            }
            apartado.setTallaApartadoCollection(attachedTallaApartadoCollection);
            em.persist(apartado);
            if (idUsuario != null) {
                idUsuario.getApartadoCollection().add(apartado);
                idUsuario = em.merge(idUsuario);
            }
            for (MovimientoEnApartado movimientoEnApartadoCollectionMovimientoEnApartado : apartado.getMovimientoEnApartadoCollection()) {
                Apartado oldIdApartadoOfMovimientoEnApartadoCollectionMovimientoEnApartado = movimientoEnApartadoCollectionMovimientoEnApartado.getIdApartado();
                movimientoEnApartadoCollectionMovimientoEnApartado.setIdApartado(apartado);
                movimientoEnApartadoCollectionMovimientoEnApartado = em.merge(movimientoEnApartadoCollectionMovimientoEnApartado);
                if (oldIdApartadoOfMovimientoEnApartadoCollectionMovimientoEnApartado != null) {
                    oldIdApartadoOfMovimientoEnApartadoCollectionMovimientoEnApartado.getMovimientoEnApartadoCollection().remove(movimientoEnApartadoCollectionMovimientoEnApartado);
                    oldIdApartadoOfMovimientoEnApartadoCollectionMovimientoEnApartado = em.merge(oldIdApartadoOfMovimientoEnApartadoCollectionMovimientoEnApartado);
                }
            }
            for (TallaApartado tallaApartadoCollectionTallaApartado : apartado.getTallaApartadoCollection()) {
                Apartado oldIdApartadoOfTallaApartadoCollectionTallaApartado = tallaApartadoCollectionTallaApartado.getIdApartado();
                tallaApartadoCollectionTallaApartado.setIdApartado(apartado);
                tallaApartadoCollectionTallaApartado = em.merge(tallaApartadoCollectionTallaApartado);
                if (oldIdApartadoOfTallaApartadoCollectionTallaApartado != null) {
                    oldIdApartadoOfTallaApartadoCollectionTallaApartado.getTallaApartadoCollection().remove(tallaApartadoCollectionTallaApartado);
                    oldIdApartadoOfTallaApartadoCollectionTallaApartado = em.merge(oldIdApartadoOfTallaApartadoCollectionTallaApartado);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findApartado(apartado.getIdApartado()) != null) {
                throw new PreexistingEntityException("Apartado " + apartado + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Apartado apartado) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Apartado persistentApartado = em.find(Apartado.class, apartado.getIdApartado());
            Usuario idUsuarioOld = persistentApartado.getIdUsuario();
            Usuario idUsuarioNew = apartado.getIdUsuario();
            Collection<MovimientoEnApartado> movimientoEnApartadoCollectionOld = persistentApartado.getMovimientoEnApartadoCollection();
            Collection<MovimientoEnApartado> movimientoEnApartadoCollectionNew = apartado.getMovimientoEnApartadoCollection();
            Collection<TallaApartado> tallaApartadoCollectionOld = persistentApartado.getTallaApartadoCollection();
            Collection<TallaApartado> tallaApartadoCollectionNew = apartado.getTallaApartadoCollection();
            List<String> illegalOrphanMessages = null;
            for (MovimientoEnApartado movimientoEnApartadoCollectionOldMovimientoEnApartado : movimientoEnApartadoCollectionOld) {
                if (!movimientoEnApartadoCollectionNew.contains(movimientoEnApartadoCollectionOldMovimientoEnApartado)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain MovimientoEnApartado " + movimientoEnApartadoCollectionOldMovimientoEnApartado + " since its idApartado field is not nullable.");
                }
            }
            for (TallaApartado tallaApartadoCollectionOldTallaApartado : tallaApartadoCollectionOld) {
                if (!tallaApartadoCollectionNew.contains(tallaApartadoCollectionOldTallaApartado)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain TallaApartado " + tallaApartadoCollectionOldTallaApartado + " since its idApartado field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getIdUsuario());
                apartado.setIdUsuario(idUsuarioNew);
            }
            Collection<MovimientoEnApartado> attachedMovimientoEnApartadoCollectionNew = new ArrayList<MovimientoEnApartado>();
            for (MovimientoEnApartado movimientoEnApartadoCollectionNewMovimientoEnApartadoToAttach : movimientoEnApartadoCollectionNew) {
                movimientoEnApartadoCollectionNewMovimientoEnApartadoToAttach = em.getReference(movimientoEnApartadoCollectionNewMovimientoEnApartadoToAttach.getClass(), movimientoEnApartadoCollectionNewMovimientoEnApartadoToAttach.getIdMovimientoApartado());
                attachedMovimientoEnApartadoCollectionNew.add(movimientoEnApartadoCollectionNewMovimientoEnApartadoToAttach);
            }
            movimientoEnApartadoCollectionNew = attachedMovimientoEnApartadoCollectionNew;
            apartado.setMovimientoEnApartadoCollection(movimientoEnApartadoCollectionNew);
            Collection<TallaApartado> attachedTallaApartadoCollectionNew = new ArrayList<TallaApartado>();
            for (TallaApartado tallaApartadoCollectionNewTallaApartadoToAttach : tallaApartadoCollectionNew) {
                tallaApartadoCollectionNewTallaApartadoToAttach = em.getReference(tallaApartadoCollectionNewTallaApartadoToAttach.getClass(), tallaApartadoCollectionNewTallaApartadoToAttach.getIdTallaApartado());
                attachedTallaApartadoCollectionNew.add(tallaApartadoCollectionNewTallaApartadoToAttach);
            }
            tallaApartadoCollectionNew = attachedTallaApartadoCollectionNew;
            apartado.setTallaApartadoCollection(tallaApartadoCollectionNew);
            apartado = em.merge(apartado);
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getApartadoCollection().remove(apartado);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getApartadoCollection().add(apartado);
                idUsuarioNew = em.merge(idUsuarioNew);
            }
            for (MovimientoEnApartado movimientoEnApartadoCollectionNewMovimientoEnApartado : movimientoEnApartadoCollectionNew) {
                if (!movimientoEnApartadoCollectionOld.contains(movimientoEnApartadoCollectionNewMovimientoEnApartado)) {
                    Apartado oldIdApartadoOfMovimientoEnApartadoCollectionNewMovimientoEnApartado = movimientoEnApartadoCollectionNewMovimientoEnApartado.getIdApartado();
                    movimientoEnApartadoCollectionNewMovimientoEnApartado.setIdApartado(apartado);
                    movimientoEnApartadoCollectionNewMovimientoEnApartado = em.merge(movimientoEnApartadoCollectionNewMovimientoEnApartado);
                    if (oldIdApartadoOfMovimientoEnApartadoCollectionNewMovimientoEnApartado != null && !oldIdApartadoOfMovimientoEnApartadoCollectionNewMovimientoEnApartado.equals(apartado)) {
                        oldIdApartadoOfMovimientoEnApartadoCollectionNewMovimientoEnApartado.getMovimientoEnApartadoCollection().remove(movimientoEnApartadoCollectionNewMovimientoEnApartado);
                        oldIdApartadoOfMovimientoEnApartadoCollectionNewMovimientoEnApartado = em.merge(oldIdApartadoOfMovimientoEnApartadoCollectionNewMovimientoEnApartado);
                    }
                }
            }
            for (TallaApartado tallaApartadoCollectionNewTallaApartado : tallaApartadoCollectionNew) {
                if (!tallaApartadoCollectionOld.contains(tallaApartadoCollectionNewTallaApartado)) {
                    Apartado oldIdApartadoOfTallaApartadoCollectionNewTallaApartado = tallaApartadoCollectionNewTallaApartado.getIdApartado();
                    tallaApartadoCollectionNewTallaApartado.setIdApartado(apartado);
                    tallaApartadoCollectionNewTallaApartado = em.merge(tallaApartadoCollectionNewTallaApartado);
                    if (oldIdApartadoOfTallaApartadoCollectionNewTallaApartado != null && !oldIdApartadoOfTallaApartadoCollectionNewTallaApartado.equals(apartado)) {
                        oldIdApartadoOfTallaApartadoCollectionNewTallaApartado.getTallaApartadoCollection().remove(tallaApartadoCollectionNewTallaApartado);
                        oldIdApartadoOfTallaApartadoCollectionNewTallaApartado = em.merge(oldIdApartadoOfTallaApartadoCollectionNewTallaApartado);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = apartado.getIdApartado();
                if (findApartado(id) == null) {
                    throw new NonexistentEntityException("The apartado with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Apartado apartado;
            try {
                apartado = em.getReference(Apartado.class, id);
                apartado.getIdApartado();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The apartado with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<MovimientoEnApartado> movimientoEnApartadoCollectionOrphanCheck = apartado.getMovimientoEnApartadoCollection();
            for (MovimientoEnApartado movimientoEnApartadoCollectionOrphanCheckMovimientoEnApartado : movimientoEnApartadoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Apartado (" + apartado + ") cannot be destroyed since the MovimientoEnApartado " + movimientoEnApartadoCollectionOrphanCheckMovimientoEnApartado + " in its movimientoEnApartadoCollection field has a non-nullable idApartado field.");
            }
            Collection<TallaApartado> tallaApartadoCollectionOrphanCheck = apartado.getTallaApartadoCollection();
            for (TallaApartado tallaApartadoCollectionOrphanCheckTallaApartado : tallaApartadoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Apartado (" + apartado + ") cannot be destroyed since the TallaApartado " + tallaApartadoCollectionOrphanCheckTallaApartado + " in its tallaApartadoCollection field has a non-nullable idApartado field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Usuario idUsuario = apartado.getIdUsuario();
            if (idUsuario != null) {
                idUsuario.getApartadoCollection().remove(apartado);
                idUsuario = em.merge(idUsuario);
            }
            em.remove(apartado);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Apartado> findApartadoEntities() {
        return findApartadoEntities(true, -1, -1);
    }

    public List<Apartado> findApartadoEntities(int maxResults, int firstResult) {
        return findApartadoEntities(false, maxResults, firstResult);
    }

    private List<Apartado> findApartadoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Apartado.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Apartado findApartado(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Apartado.class, id);
        } finally {
            em.close();
        }
    }

    public int getApartadoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Apartado> rt = cq.from(Apartado.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
