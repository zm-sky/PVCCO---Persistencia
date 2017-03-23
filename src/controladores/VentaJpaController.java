
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
import objetosNegocio.MovimientoEnVenta;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import objetosNegocio.Venta;
import objetosNegocio.VentaTalla;

/**
 *
 * @author Raul Karim Sabag Ballesteros
 */
public class VentaJpaController implements Serializable {

    public VentaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Venta venta) throws PreexistingEntityException, Exception {
        if (venta.getMovimientoEnVentaCollection() == null) {
            venta.setMovimientoEnVentaCollection(new ArrayList<MovimientoEnVenta>());
        }
        if (venta.getVentaTallaCollection() == null) {
            venta.setVentaTallaCollection(new ArrayList<VentaTalla>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario idUsuario = venta.getIdUsuario();
            if (idUsuario != null) {
                idUsuario = em.getReference(idUsuario.getClass(), idUsuario.getIdUsuario());
                venta.setIdUsuario(idUsuario);
            }
            Collection<MovimientoEnVenta> attachedMovimientoEnVentaCollection = new ArrayList<MovimientoEnVenta>();
            for (MovimientoEnVenta movimientoEnVentaCollectionMovimientoEnVentaToAttach : venta.getMovimientoEnVentaCollection()) {
                movimientoEnVentaCollectionMovimientoEnVentaToAttach = em.getReference(movimientoEnVentaCollectionMovimientoEnVentaToAttach.getClass(), movimientoEnVentaCollectionMovimientoEnVentaToAttach.getIdMovimientoVenta());
                attachedMovimientoEnVentaCollection.add(movimientoEnVentaCollectionMovimientoEnVentaToAttach);
            }
            venta.setMovimientoEnVentaCollection(attachedMovimientoEnVentaCollection);
            Collection<VentaTalla> attachedVentaTallaCollection = new ArrayList<VentaTalla>();
            for (VentaTalla ventaTallaCollectionVentaTallaToAttach : venta.getVentaTallaCollection()) {
                ventaTallaCollectionVentaTallaToAttach = em.getReference(ventaTallaCollectionVentaTallaToAttach.getClass(), ventaTallaCollectionVentaTallaToAttach.getIdVentaTalla());
                attachedVentaTallaCollection.add(ventaTallaCollectionVentaTallaToAttach);
            }
            venta.setVentaTallaCollection(attachedVentaTallaCollection);
            em.persist(venta);
            if (idUsuario != null) {
                idUsuario.getVentaCollection().add(venta);
                idUsuario = em.merge(idUsuario);
            }
            for (MovimientoEnVenta movimientoEnVentaCollectionMovimientoEnVenta : venta.getMovimientoEnVentaCollection()) {
                Venta oldIdVentaOfMovimientoEnVentaCollectionMovimientoEnVenta = movimientoEnVentaCollectionMovimientoEnVenta.getIdVenta();
                movimientoEnVentaCollectionMovimientoEnVenta.setIdVenta(venta);
                movimientoEnVentaCollectionMovimientoEnVenta = em.merge(movimientoEnVentaCollectionMovimientoEnVenta);
                if (oldIdVentaOfMovimientoEnVentaCollectionMovimientoEnVenta != null) {
                    oldIdVentaOfMovimientoEnVentaCollectionMovimientoEnVenta.getMovimientoEnVentaCollection().remove(movimientoEnVentaCollectionMovimientoEnVenta);
                    oldIdVentaOfMovimientoEnVentaCollectionMovimientoEnVenta = em.merge(oldIdVentaOfMovimientoEnVentaCollectionMovimientoEnVenta);
                }
            }
            for (VentaTalla ventaTallaCollectionVentaTalla : venta.getVentaTallaCollection()) {
                Venta oldIdVentaOfVentaTallaCollectionVentaTalla = ventaTallaCollectionVentaTalla.getIdVenta();
                ventaTallaCollectionVentaTalla.setIdVenta(venta);
                ventaTallaCollectionVentaTalla = em.merge(ventaTallaCollectionVentaTalla);
                if (oldIdVentaOfVentaTallaCollectionVentaTalla != null) {
                    oldIdVentaOfVentaTallaCollectionVentaTalla.getVentaTallaCollection().remove(ventaTallaCollectionVentaTalla);
                    oldIdVentaOfVentaTallaCollectionVentaTalla = em.merge(oldIdVentaOfVentaTallaCollectionVentaTalla);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findVenta(venta.getIdVenta()) != null) {
                throw new PreexistingEntityException("Venta " + venta + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Venta venta) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Venta persistentVenta = em.find(Venta.class, venta.getIdVenta());
            Usuario idUsuarioOld = persistentVenta.getIdUsuario();
            Usuario idUsuarioNew = venta.getIdUsuario();
            Collection<MovimientoEnVenta> movimientoEnVentaCollectionOld = persistentVenta.getMovimientoEnVentaCollection();
            Collection<MovimientoEnVenta> movimientoEnVentaCollectionNew = venta.getMovimientoEnVentaCollection();
            Collection<VentaTalla> ventaTallaCollectionOld = persistentVenta.getVentaTallaCollection();
            Collection<VentaTalla> ventaTallaCollectionNew = venta.getVentaTallaCollection();
            List<String> illegalOrphanMessages = null;
            for (MovimientoEnVenta movimientoEnVentaCollectionOldMovimientoEnVenta : movimientoEnVentaCollectionOld) {
                if (!movimientoEnVentaCollectionNew.contains(movimientoEnVentaCollectionOldMovimientoEnVenta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain MovimientoEnVenta " + movimientoEnVentaCollectionOldMovimientoEnVenta + " since its idVenta field is not nullable.");
                }
            }
            for (VentaTalla ventaTallaCollectionOldVentaTalla : ventaTallaCollectionOld) {
                if (!ventaTallaCollectionNew.contains(ventaTallaCollectionOldVentaTalla)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain VentaTalla " + ventaTallaCollectionOldVentaTalla + " since its idVenta field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getIdUsuario());
                venta.setIdUsuario(idUsuarioNew);
            }
            Collection<MovimientoEnVenta> attachedMovimientoEnVentaCollectionNew = new ArrayList<MovimientoEnVenta>();
            for (MovimientoEnVenta movimientoEnVentaCollectionNewMovimientoEnVentaToAttach : movimientoEnVentaCollectionNew) {
                movimientoEnVentaCollectionNewMovimientoEnVentaToAttach = em.getReference(movimientoEnVentaCollectionNewMovimientoEnVentaToAttach.getClass(), movimientoEnVentaCollectionNewMovimientoEnVentaToAttach.getIdMovimientoVenta());
                attachedMovimientoEnVentaCollectionNew.add(movimientoEnVentaCollectionNewMovimientoEnVentaToAttach);
            }
            movimientoEnVentaCollectionNew = attachedMovimientoEnVentaCollectionNew;
            venta.setMovimientoEnVentaCollection(movimientoEnVentaCollectionNew);
            Collection<VentaTalla> attachedVentaTallaCollectionNew = new ArrayList<VentaTalla>();
            for (VentaTalla ventaTallaCollectionNewVentaTallaToAttach : ventaTallaCollectionNew) {
                ventaTallaCollectionNewVentaTallaToAttach = em.getReference(ventaTallaCollectionNewVentaTallaToAttach.getClass(), ventaTallaCollectionNewVentaTallaToAttach.getIdVentaTalla());
                attachedVentaTallaCollectionNew.add(ventaTallaCollectionNewVentaTallaToAttach);
            }
            ventaTallaCollectionNew = attachedVentaTallaCollectionNew;
            venta.setVentaTallaCollection(ventaTallaCollectionNew);
            venta = em.merge(venta);
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getVentaCollection().remove(venta);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getVentaCollection().add(venta);
                idUsuarioNew = em.merge(idUsuarioNew);
            }
            for (MovimientoEnVenta movimientoEnVentaCollectionNewMovimientoEnVenta : movimientoEnVentaCollectionNew) {
                if (!movimientoEnVentaCollectionOld.contains(movimientoEnVentaCollectionNewMovimientoEnVenta)) {
                    Venta oldIdVentaOfMovimientoEnVentaCollectionNewMovimientoEnVenta = movimientoEnVentaCollectionNewMovimientoEnVenta.getIdVenta();
                    movimientoEnVentaCollectionNewMovimientoEnVenta.setIdVenta(venta);
                    movimientoEnVentaCollectionNewMovimientoEnVenta = em.merge(movimientoEnVentaCollectionNewMovimientoEnVenta);
                    if (oldIdVentaOfMovimientoEnVentaCollectionNewMovimientoEnVenta != null && !oldIdVentaOfMovimientoEnVentaCollectionNewMovimientoEnVenta.equals(venta)) {
                        oldIdVentaOfMovimientoEnVentaCollectionNewMovimientoEnVenta.getMovimientoEnVentaCollection().remove(movimientoEnVentaCollectionNewMovimientoEnVenta);
                        oldIdVentaOfMovimientoEnVentaCollectionNewMovimientoEnVenta = em.merge(oldIdVentaOfMovimientoEnVentaCollectionNewMovimientoEnVenta);
                    }
                }
            }
            for (VentaTalla ventaTallaCollectionNewVentaTalla : ventaTallaCollectionNew) {
                if (!ventaTallaCollectionOld.contains(ventaTallaCollectionNewVentaTalla)) {
                    Venta oldIdVentaOfVentaTallaCollectionNewVentaTalla = ventaTallaCollectionNewVentaTalla.getIdVenta();
                    ventaTallaCollectionNewVentaTalla.setIdVenta(venta);
                    ventaTallaCollectionNewVentaTalla = em.merge(ventaTallaCollectionNewVentaTalla);
                    if (oldIdVentaOfVentaTallaCollectionNewVentaTalla != null && !oldIdVentaOfVentaTallaCollectionNewVentaTalla.equals(venta)) {
                        oldIdVentaOfVentaTallaCollectionNewVentaTalla.getVentaTallaCollection().remove(ventaTallaCollectionNewVentaTalla);
                        oldIdVentaOfVentaTallaCollectionNewVentaTalla = em.merge(oldIdVentaOfVentaTallaCollectionNewVentaTalla);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = venta.getIdVenta();
                if (findVenta(id) == null) {
                    throw new NonexistentEntityException("The venta with id " + id + " no longer exists.");
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
            Venta venta;
            try {
                venta = em.getReference(Venta.class, id);
                venta.getIdVenta();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The venta with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<MovimientoEnVenta> movimientoEnVentaCollectionOrphanCheck = venta.getMovimientoEnVentaCollection();
            for (MovimientoEnVenta movimientoEnVentaCollectionOrphanCheckMovimientoEnVenta : movimientoEnVentaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Venta (" + venta + ") cannot be destroyed since the MovimientoEnVenta " + movimientoEnVentaCollectionOrphanCheckMovimientoEnVenta + " in its movimientoEnVentaCollection field has a non-nullable idVenta field.");
            }
            Collection<VentaTalla> ventaTallaCollectionOrphanCheck = venta.getVentaTallaCollection();
            for (VentaTalla ventaTallaCollectionOrphanCheckVentaTalla : ventaTallaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Venta (" + venta + ") cannot be destroyed since the VentaTalla " + ventaTallaCollectionOrphanCheckVentaTalla + " in its ventaTallaCollection field has a non-nullable idVenta field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Usuario idUsuario = venta.getIdUsuario();
            if (idUsuario != null) {
                idUsuario.getVentaCollection().remove(venta);
                idUsuario = em.merge(idUsuario);
            }
            em.remove(venta);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Venta> findVentaEntities() {
        return findVentaEntities(true, -1, -1);
    }

    public List<Venta> findVentaEntities(int maxResults, int firstResult) {
        return findVentaEntities(false, maxResults, firstResult);
    }

    private List<Venta> findVentaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Venta.class));
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

    public Venta findVenta(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Venta.class, id);
        } finally {
            em.close();
        }
    }

    public int getVentaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Venta> rt = cq.from(Venta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
