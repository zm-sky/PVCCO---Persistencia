
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
import objetosNegocio.BajaDeInventario;
import objetosNegocio.Talla;

/**
 *
 * @author Raul Karim Sabag Ballesteros
 */
public class BajaDeInventarioJpaController implements Serializable {

    public BajaDeInventarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(BajaDeInventario bajaDeInventario) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Talla idTalla = bajaDeInventario.getIdTalla();
            if (idTalla != null) {
                idTalla = em.getReference(idTalla.getClass(), idTalla.getIdTalla());
                bajaDeInventario.setIdTalla(idTalla);
            }
            em.persist(bajaDeInventario);
            if (idTalla != null) {
                idTalla.getBajaDeInventarioCollection().add(bajaDeInventario);
                idTalla = em.merge(idTalla);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findBajaDeInventario(bajaDeInventario.getIdBajaInventario()) != null) {
                throw new PreexistingEntityException("BajaDeInventario " + bajaDeInventario + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(BajaDeInventario bajaDeInventario) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            BajaDeInventario persistentBajaDeInventario = em.find(BajaDeInventario.class, bajaDeInventario.getIdBajaInventario());
            Talla idTallaOld = persistentBajaDeInventario.getIdTalla();
            Talla idTallaNew = bajaDeInventario.getIdTalla();
            if (idTallaNew != null) {
                idTallaNew = em.getReference(idTallaNew.getClass(), idTallaNew.getIdTalla());
                bajaDeInventario.setIdTalla(idTallaNew);
            }
            bajaDeInventario = em.merge(bajaDeInventario);
            if (idTallaOld != null && !idTallaOld.equals(idTallaNew)) {
                idTallaOld.getBajaDeInventarioCollection().remove(bajaDeInventario);
                idTallaOld = em.merge(idTallaOld);
            }
            if (idTallaNew != null && !idTallaNew.equals(idTallaOld)) {
                idTallaNew.getBajaDeInventarioCollection().add(bajaDeInventario);
                idTallaNew = em.merge(idTallaNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = bajaDeInventario.getIdBajaInventario();
                if (findBajaDeInventario(id) == null) {
                    throw new NonexistentEntityException("The bajaDeInventario with id " + id + " no longer exists.");
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
            BajaDeInventario bajaDeInventario;
            try {
                bajaDeInventario = em.getReference(BajaDeInventario.class, id);
                bajaDeInventario.getIdBajaInventario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The bajaDeInventario with id " + id + " no longer exists.", enfe);
            }
            Talla idTalla = bajaDeInventario.getIdTalla();
            if (idTalla != null) {
                idTalla.getBajaDeInventarioCollection().remove(bajaDeInventario);
                idTalla = em.merge(idTalla);
            }
            em.remove(bajaDeInventario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<BajaDeInventario> findBajaDeInventarioEntities() {
        return findBajaDeInventarioEntities(true, -1, -1);
    }

    public List<BajaDeInventario> findBajaDeInventarioEntities(int maxResults, int firstResult) {
        return findBajaDeInventarioEntities(false, maxResults, firstResult);
    }

    private List<BajaDeInventario> findBajaDeInventarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(BajaDeInventario.class));
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

    public BajaDeInventario findBajaDeInventario(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(BajaDeInventario.class, id);
        } finally {
            em.close();
        }
    }

    public int getBajaDeInventarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<BajaDeInventario> rt = cq.from(BajaDeInventario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
