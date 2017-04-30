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
import objetosNegocio.Talla;
import objetosNegocio.Venta;
import objetosNegocio.VentaTalla;

/**
 *
 * @author zippy
 */
public class VentaTallaJpaController implements Serializable {

    public VentaTallaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(VentaTalla ventatalla) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Talla idTalla = ventatalla.getIdTalla();
            if (idTalla != null) {
                idTalla = em.getReference(idTalla.getClass(), idTalla.getIdTalla());
                ventatalla.setIdTalla(idTalla);
            }
            Venta idVenta = ventatalla.getIdVenta();
            if (idVenta != null) {
                idVenta = em.getReference(idVenta.getClass(), idVenta.getIdVenta());
                ventatalla.setIdVenta(idVenta);
            }
            em.persist(ventatalla);
            if (idTalla != null) {
                idTalla.getVentatallaList().add(ventatalla);
                idTalla = em.merge(idTalla);
            }
            if (idVenta != null) {
                idVenta.getVentatallaList().add(ventatalla);
                idVenta = em.merge(idVenta);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findVentatalla(ventatalla.getIdVentaTalla()) != null) {
                throw new PreexistingEntityException("Ventatalla " + ventatalla + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(VentaTalla ventatalla) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            VentaTalla persistentVentatalla = em.find(VentaTalla.class, ventatalla.getIdVentaTalla());
            Talla idTallaOld = persistentVentatalla.getIdTalla();
            Talla idTallaNew = ventatalla.getIdTalla();
            Venta idVentaOld = persistentVentatalla.getIdVenta();
            Venta idVentaNew = ventatalla.getIdVenta();
            if (idTallaNew != null) {
                idTallaNew = em.getReference(idTallaNew.getClass(), idTallaNew.getIdTalla());
                ventatalla.setIdTalla(idTallaNew);
            }
            if (idVentaNew != null) {
                idVentaNew = em.getReference(idVentaNew.getClass(), idVentaNew.getIdVenta());
                ventatalla.setIdVenta(idVentaNew);
            }
            ventatalla = em.merge(ventatalla);
            if (idTallaOld != null && !idTallaOld.equals(idTallaNew)) {
                idTallaOld.getVentatallaList().remove(ventatalla);
                idTallaOld = em.merge(idTallaOld);
            }
            if (idTallaNew != null && !idTallaNew.equals(idTallaOld)) {
                idTallaNew.getVentatallaList().add(ventatalla);
                idTallaNew = em.merge(idTallaNew);
            }
            if (idVentaOld != null && !idVentaOld.equals(idVentaNew)) {
                idVentaOld.getVentatallaList().remove(ventatalla);
                idVentaOld = em.merge(idVentaOld);
            }
            if (idVentaNew != null && !idVentaNew.equals(idVentaOld)) {
                idVentaNew.getVentatallaList().add(ventatalla);
                idVentaNew = em.merge(idVentaNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = ventatalla.getIdVentaTalla();
                if (findVentatalla(id) == null) {
                    throw new NonexistentEntityException("The ventatalla with id " + id + " no longer exists.");
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
            VentaTalla ventatalla;
            try {
                ventatalla = em.getReference(VentaTalla.class, id);
                ventatalla.getIdVentaTalla();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ventatalla with id " + id + " no longer exists.", enfe);
            }
            Talla idTalla = ventatalla.getIdTalla();
            if (idTalla != null) {
                idTalla.getVentatallaList().remove(ventatalla);
                idTalla = em.merge(idTalla);
            }
            Venta idVenta = ventatalla.getIdVenta();
            if (idVenta != null) {
                idVenta.getVentatallaList().remove(ventatalla);
                idVenta = em.merge(idVenta);
            }
            em.remove(ventatalla);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<VentaTalla> findVentatallaEntities() {
        return findVentatallaEntities(true, -1, -1);
    }

    public List<VentaTalla> findVentatallaEntities(int maxResults, int firstResult) {
        return findVentatallaEntities(false, maxResults, firstResult);
    }

    private List<VentaTalla> findVentatallaEntities(boolean all, int maxResults, int firstResult) {
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

    public VentaTalla findVentatalla(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(VentaTalla.class, id);
        } finally {
            em.close();
        }
    }

    public int getVentatallaCount() {
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
