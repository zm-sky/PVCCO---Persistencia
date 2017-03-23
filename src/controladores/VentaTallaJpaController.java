
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
import objetosNegocio.Talla;
import objetosNegocio.Venta;
import objetosNegocio.VentaTalla;

/**
 *
 * @author Raul Karim Sabag Ballesteros
 */
public class VentaTallaJpaController implements Serializable {

    public VentaTallaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(VentaTalla ventaTalla) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Talla idTalla = ventaTalla.getIdTalla();
            if (idTalla != null) {
                idTalla = em.getReference(idTalla.getClass(), idTalla.getIdTalla());
                ventaTalla.setIdTalla(idTalla);
            }
            Venta idVenta = ventaTalla.getIdVenta();
            if (idVenta != null) {
                idVenta = em.getReference(idVenta.getClass(), idVenta.getIdVenta());
                ventaTalla.setIdVenta(idVenta);
            }
            em.persist(ventaTalla);
            if (idTalla != null) {
                idTalla.getVentaTallaCollection().add(ventaTalla);
                idTalla = em.merge(idTalla);
            }
            if (idVenta != null) {
                idVenta.getVentaTallaCollection().add(ventaTalla);
                idVenta = em.merge(idVenta);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findVentaTalla(ventaTalla.getIdVentaTalla()) != null) {
                throw new PreexistingEntityException("VentaTalla " + ventaTalla + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(VentaTalla ventaTalla) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            VentaTalla persistentVentaTalla = em.find(VentaTalla.class, ventaTalla.getIdVentaTalla());
            Talla idTallaOld = persistentVentaTalla.getIdTalla();
            Talla idTallaNew = ventaTalla.getIdTalla();
            Venta idVentaOld = persistentVentaTalla.getIdVenta();
            Venta idVentaNew = ventaTalla.getIdVenta();
            if (idTallaNew != null) {
                idTallaNew = em.getReference(idTallaNew.getClass(), idTallaNew.getIdTalla());
                ventaTalla.setIdTalla(idTallaNew);
            }
            if (idVentaNew != null) {
                idVentaNew = em.getReference(idVentaNew.getClass(), idVentaNew.getIdVenta());
                ventaTalla.setIdVenta(idVentaNew);
            }
            ventaTalla = em.merge(ventaTalla);
            if (idTallaOld != null && !idTallaOld.equals(idTallaNew)) {
                idTallaOld.getVentaTallaCollection().remove(ventaTalla);
                idTallaOld = em.merge(idTallaOld);
            }
            if (idTallaNew != null && !idTallaNew.equals(idTallaOld)) {
                idTallaNew.getVentaTallaCollection().add(ventaTalla);
                idTallaNew = em.merge(idTallaNew);
            }
            if (idVentaOld != null && !idVentaOld.equals(idVentaNew)) {
                idVentaOld.getVentaTallaCollection().remove(ventaTalla);
                idVentaOld = em.merge(idVentaOld);
            }
            if (idVentaNew != null && !idVentaNew.equals(idVentaOld)) {
                idVentaNew.getVentaTallaCollection().add(ventaTalla);
                idVentaNew = em.merge(idVentaNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = ventaTalla.getIdVentaTalla();
                if (findVentaTalla(id) == null) {
                    throw new NonexistentEntityException("The ventaTalla with id " + id + " no longer exists.");
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
            VentaTalla ventaTalla;
            try {
                ventaTalla = em.getReference(VentaTalla.class, id);
                ventaTalla.getIdVentaTalla();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ventaTalla with id " + id + " no longer exists.", enfe);
            }
            Talla idTalla = ventaTalla.getIdTalla();
            if (idTalla != null) {
                idTalla.getVentaTallaCollection().remove(ventaTalla);
                idTalla = em.merge(idTalla);
            }
            Venta idVenta = ventaTalla.getIdVenta();
            if (idVenta != null) {
                idVenta.getVentaTallaCollection().remove(ventaTalla);
                idVenta = em.merge(idVenta);
            }
            em.remove(ventaTalla);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<VentaTalla> findVentaTallaEntities() {
        return findVentaTallaEntities(true, -1, -1);
    }

    public List<VentaTalla> findVentaTallaEntities(int maxResults, int firstResult) {
        return findVentaTallaEntities(false, maxResults, firstResult);
    }

    private List<VentaTalla> findVentaTallaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(VentaTalla.class));
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

    public VentaTalla findVentaTalla(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(VentaTalla.class, id);
        } finally {
            em.close();
        }
    }

    public int getVentaTallaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<VentaTalla> rt = cq.from(VentaTalla.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
