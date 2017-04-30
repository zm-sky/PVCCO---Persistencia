/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import objetosNegocio.MovimientoEnVenta;
import objetosNegocio.Venta;

/**
 *
 * @author zippy
 */
public class MovimientoEnVentaJpaController implements Serializable {

    public MovimientoEnVentaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(MovimientoEnVenta movimientoenventa) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Venta idVenta = movimientoenventa.getIdVenta();
            if (idVenta != null) {
                idVenta = em.getReference(idVenta.getClass(), idVenta.getIdVenta());
                movimientoenventa.setIdVenta(idVenta);
            }
            em.persist(movimientoenventa);
            if (idVenta != null) {
                idVenta.getMovimientoenventaList().add(movimientoenventa);
                idVenta = em.merge(idVenta);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMovimientoenventa(movimientoenventa.getIdMovimientoVenta()) != null) {
                throw new PreexistingEntityException("Movimientoenventa " + movimientoenventa + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(MovimientoEnVenta movimientoenventa) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MovimientoEnVenta persistentMovimientoenventa = em.find(MovimientoEnVenta.class, movimientoenventa.getIdMovimientoVenta());
            Venta idVentaOld = persistentMovimientoenventa.getIdVenta();
            Venta idVentaNew = movimientoenventa.getIdVenta();
            if (idVentaNew != null) {
                idVentaNew = em.getReference(idVentaNew.getClass(), idVentaNew.getIdVenta());
                movimientoenventa.setIdVenta(idVentaNew);
            }
            movimientoenventa = em.merge(movimientoenventa);
            if (idVentaOld != null && !idVentaOld.equals(idVentaNew)) {
                idVentaOld.getMovimientoenventaList().remove(movimientoenventa);
                idVentaOld = em.merge(idVentaOld);
            }
            if (idVentaNew != null && !idVentaNew.equals(idVentaOld)) {
                idVentaNew.getMovimientoenventaList().add(movimientoenventa);
                idVentaNew = em.merge(idVentaNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = movimientoenventa.getIdMovimientoVenta();
                if (findMovimientoenventa(id) == null) {
                    throw new NonexistentEntityException("The movimientoenventa with id " + id + " no longer exists.");
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
            MovimientoEnVenta movimientoenventa;
            try {
                movimientoenventa = em.getReference(MovimientoEnVenta.class, id);
                movimientoenventa.getIdMovimientoVenta();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The movimientoenventa with id " + id + " no longer exists.", enfe);
            }
            Venta idVenta = movimientoenventa.getIdVenta();
            if (idVenta != null) {
                idVenta.getMovimientoenventaList().remove(movimientoenventa);
                idVenta = em.merge(idVenta);
            }
            em.remove(movimientoenventa);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<MovimientoEnVenta> findMovimientoenventaEntities() {
        return findMovimientoenventaEntities(true, -1, -1);
    }

    public List<MovimientoEnVenta> findMovimientoenventaEntities(int maxResults, int firstResult) {
        return findMovimientoenventaEntities(false, maxResults, firstResult);
    }

    private List<MovimientoEnVenta> findMovimientoenventaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(MovimientoEnVenta.class));
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

    public MovimientoEnVenta findMovimientoenventa(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(MovimientoEnVenta.class, id);
        } finally {
            em.close();
        }
    }

    public int getMovimientoenventaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<MovimientoEnVenta> rt = cq.from(MovimientoEnVenta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
