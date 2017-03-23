
package controladores;

import controladores.exceptions.NonexistentEntityException;
import controladores.exceptions.PreexistingEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import objetosNegocio.Apartado;
import objetosNegocio.MovimientoEnApartado;

/**
 *
 * @author Raul Karim Sabag Ballesteros
 */
public class MovimientoEnApartadoJpaController implements Serializable {

    public MovimientoEnApartadoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(MovimientoEnApartado movimientoEnApartado) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Apartado idApartado = movimientoEnApartado.getIdApartado();
            if (idApartado != null) {
                idApartado = em.getReference(idApartado.getClass(), idApartado.getIdApartado());
                movimientoEnApartado.setIdApartado(idApartado);
            }
            em.persist(movimientoEnApartado);
            if (idApartado != null) {
                idApartado.getMovimientoEnApartadoCollection().add(movimientoEnApartado);
                idApartado = em.merge(idApartado);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMovimientoEnApartado(movimientoEnApartado.getIdMovimientoApartado()) != null) {
                throw new PreexistingEntityException("MovimientoEnApartado " + movimientoEnApartado + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(MovimientoEnApartado movimientoEnApartado) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MovimientoEnApartado persistentMovimientoEnApartado = em.find(MovimientoEnApartado.class, movimientoEnApartado.getIdMovimientoApartado());
            Apartado idApartadoOld = persistentMovimientoEnApartado.getIdApartado();
            Apartado idApartadoNew = movimientoEnApartado.getIdApartado();
            if (idApartadoNew != null) {
                idApartadoNew = em.getReference(idApartadoNew.getClass(), idApartadoNew.getIdApartado());
                movimientoEnApartado.setIdApartado(idApartadoNew);
            }
            movimientoEnApartado = em.merge(movimientoEnApartado);
            if (idApartadoOld != null && !idApartadoOld.equals(idApartadoNew)) {
                idApartadoOld.getMovimientoEnApartadoCollection().remove(movimientoEnApartado);
                idApartadoOld = em.merge(idApartadoOld);
            }
            if (idApartadoNew != null && !idApartadoNew.equals(idApartadoOld)) {
                idApartadoNew.getMovimientoEnApartadoCollection().add(movimientoEnApartado);
                idApartadoNew = em.merge(idApartadoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = movimientoEnApartado.getIdMovimientoApartado();
                if (findMovimientoEnApartado(id) == null) {
                    throw new NonexistentEntityException("The movimientoEnApartado with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MovimientoEnApartado movimientoEnApartado;
            try {
                movimientoEnApartado = em.getReference(MovimientoEnApartado.class, id);
                movimientoEnApartado.getIdMovimientoApartado();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The movimientoEnApartado with id " + id + " no longer exists.", enfe);
            }
            Apartado idApartado = movimientoEnApartado.getIdApartado();
            if (idApartado != null) {
                idApartado.getMovimientoEnApartadoCollection().remove(movimientoEnApartado);
                idApartado = em.merge(idApartado);
            }
            em.remove(movimientoEnApartado);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<MovimientoEnApartado> findMovimientoEnApartadoEntities() {
        return findMovimientoEnApartadoEntities(true, -1, -1);
    }

    public List<MovimientoEnApartado> findMovimientoEnApartadoEntities(int maxResults, int firstResult) {
        return findMovimientoEnApartadoEntities(false, maxResults, firstResult);
    }

    private List<MovimientoEnApartado> findMovimientoEnApartadoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(MovimientoEnApartado.class));
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

    public MovimientoEnApartado findMovimientoEnApartado(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(MovimientoEnApartado.class, id);
        } finally {
            em.close();
        }
    }

    public int getMovimientoEnApartadoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<MovimientoEnApartado> rt = cq.from(MovimientoEnApartado.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
