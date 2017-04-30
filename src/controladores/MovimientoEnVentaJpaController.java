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

    public void create(MovimientoEnVenta movimientoEnVenta) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Venta idVenta = movimientoEnVenta.getIdVenta();
            if (idVenta != null) {
                idVenta = em.getReference(idVenta.getClass(), idVenta.getIdVenta());
                movimientoEnVenta.setIdVenta(idVenta);
            }
            em.persist(movimientoEnVenta);
            if (idVenta != null) {
                idVenta.getMovimientoEnVentaList().add(movimientoEnVenta);
                idVenta = em.merge(idVenta);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMovimientoEnVenta(movimientoEnVenta.getIdMovimientoVenta()) != null) {
                throw new PreexistingEntityException("MovimientoEnVenta " + movimientoEnVenta + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(MovimientoEnVenta movimientoEnVenta) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MovimientoEnVenta persistentMovimientoEnVenta = em.find(MovimientoEnVenta.class, movimientoEnVenta.getIdMovimientoVenta());
            Venta idVentaOld = persistentMovimientoEnVenta.getIdVenta();
            Venta idVentaNew = movimientoEnVenta.getIdVenta();
            if (idVentaNew != null) {
                idVentaNew = em.getReference(idVentaNew.getClass(), idVentaNew.getIdVenta());
                movimientoEnVenta.setIdVenta(idVentaNew);
            }
            movimientoEnVenta = em.merge(movimientoEnVenta);
            if (idVentaOld != null && !idVentaOld.equals(idVentaNew)) {
                idVentaOld.getMovimientoEnVentaList().remove(movimientoEnVenta);
                idVentaOld = em.merge(idVentaOld);
            }
            if (idVentaNew != null && !idVentaNew.equals(idVentaOld)) {
                idVentaNew.getMovimientoEnVentaList().add(movimientoEnVenta);
                idVentaNew = em.merge(idVentaNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = movimientoEnVenta.getIdMovimientoVenta();
                if (findMovimientoEnVenta(id) == null) {
                    throw new NonexistentEntityException("The movimientoEnVenta with id " + id + " no longer exists.");
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
            MovimientoEnVenta movimientoEnVenta;
            try {
                movimientoEnVenta = em.getReference(MovimientoEnVenta.class, id);
                movimientoEnVenta.getIdMovimientoVenta();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The movimientoEnVenta with id " + id + " no longer exists.", enfe);
            }
            Venta idVenta = movimientoEnVenta.getIdVenta();
            if (idVenta != null) {
                idVenta.getMovimientoEnVentaList().remove(movimientoEnVenta);
                idVenta = em.merge(idVenta);
            }
            em.remove(movimientoEnVenta);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<MovimientoEnVenta> findMovimientoEnVentaEntities() {
        return findMovimientoEnVentaEntities(true, -1, -1);
    }

    public List<MovimientoEnVenta> findMovimientoEnVentaEntities(int maxResults, int firstResult) {
        return findMovimientoEnVentaEntities(false, maxResults, firstResult);
    }

    private List<MovimientoEnVenta> findMovimientoEnVentaEntities(boolean all, int maxResults, int firstResult) {
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

    public MovimientoEnVenta findMovimientoEnVenta(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(MovimientoEnVenta.class, id);
        } finally {
            em.close();
        }
    }

    public int getMovimientoEnVentaCount() {
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
