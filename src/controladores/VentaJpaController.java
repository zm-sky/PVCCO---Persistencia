/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import objetosNegocio.Venta;
import objetosNegocio.VentaTalla;

/**
 *
 * @author zippy
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
        if (venta.getMovimientoenventaList() == null) {
            venta.setMovimientoenventaList(new ArrayList<MovimientoEnVenta>());
        }
        if (venta.getVentatallaList() == null) {
            venta.setVentatallaList(new ArrayList<VentaTalla>());
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
            List<MovimientoEnVenta> attachedMovimientoenventaList = new ArrayList<MovimientoEnVenta>();
            for (MovimientoEnVenta movimientoenventaListMovimientoenventaToAttach : venta.getMovimientoenventaList()) {
                movimientoenventaListMovimientoenventaToAttach = em.getReference(movimientoenventaListMovimientoenventaToAttach.getClass(), movimientoenventaListMovimientoenventaToAttach.getIdMovimientoVenta());
                attachedMovimientoenventaList.add(movimientoenventaListMovimientoenventaToAttach);
            }
            venta.setMovimientoenventaList(attachedMovimientoenventaList);
            List<VentaTalla> attachedVentatallaList = new ArrayList<VentaTalla>();
            for (VentaTalla ventatallaListVentatallaToAttach : venta.getVentatallaList()) {
                ventatallaListVentatallaToAttach = em.getReference(ventatallaListVentatallaToAttach.getClass(), ventatallaListVentatallaToAttach.getIdVentaTalla());
                attachedVentatallaList.add(ventatallaListVentatallaToAttach);
            }
            venta.setVentatallaList(attachedVentatallaList);
            em.persist(venta);
            if (idUsuario != null) {
                idUsuario.getVentaList().add(venta);
                idUsuario = em.merge(idUsuario);
            }
            for (MovimientoEnVenta movimientoenventaListMovimientoenventa : venta.getMovimientoenventaList()) {
                Venta oldIdVentaOfMovimientoenventaListMovimientoenventa = movimientoenventaListMovimientoenventa.getIdVenta();
                movimientoenventaListMovimientoenventa.setIdVenta(venta);
                movimientoenventaListMovimientoenventa = em.merge(movimientoenventaListMovimientoenventa);
                if (oldIdVentaOfMovimientoenventaListMovimientoenventa != null) {
                    oldIdVentaOfMovimientoenventaListMovimientoenventa.getMovimientoenventaList().remove(movimientoenventaListMovimientoenventa);
                    oldIdVentaOfMovimientoenventaListMovimientoenventa = em.merge(oldIdVentaOfMovimientoenventaListMovimientoenventa);
                }
            }
            for (VentaTalla ventatallaListVentatalla : venta.getVentatallaList()) {
                Venta oldIdVentaOfVentatallaListVentatalla = ventatallaListVentatalla.getIdVenta();
                ventatallaListVentatalla.setIdVenta(venta);
                ventatallaListVentatalla = em.merge(ventatallaListVentatalla);
                if (oldIdVentaOfVentatallaListVentatalla != null) {
                    oldIdVentaOfVentatallaListVentatalla.getVentatallaList().remove(ventatallaListVentatalla);
                    oldIdVentaOfVentatallaListVentatalla = em.merge(oldIdVentaOfVentatallaListVentatalla);
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
            List<MovimientoEnVenta> movimientoenventaListOld = persistentVenta.getMovimientoenventaList();
            List<MovimientoEnVenta> movimientoenventaListNew = venta.getMovimientoenventaList();
            List<VentaTalla> ventatallaListOld = persistentVenta.getVentatallaList();
            List<VentaTalla> ventatallaListNew = venta.getVentatallaList();
            List<String> illegalOrphanMessages = null;
            for (MovimientoEnVenta movimientoenventaListOldMovimientoenventa : movimientoenventaListOld) {
                if (!movimientoenventaListNew.contains(movimientoenventaListOldMovimientoenventa)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Movimientoenventa " + movimientoenventaListOldMovimientoenventa + " since its idVenta field is not nullable.");
                }
            }
            for (VentaTalla ventatallaListOldVentatalla : ventatallaListOld) {
                if (!ventatallaListNew.contains(ventatallaListOldVentatalla)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ventatalla " + ventatallaListOldVentatalla + " since its idVenta field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getIdUsuario());
                venta.setIdUsuario(idUsuarioNew);
            }
            List<MovimientoEnVenta> attachedMovimientoenventaListNew = new ArrayList<MovimientoEnVenta>();
            for (MovimientoEnVenta movimientoenventaListNewMovimientoenventaToAttach : movimientoenventaListNew) {
                movimientoenventaListNewMovimientoenventaToAttach = em.getReference(movimientoenventaListNewMovimientoenventaToAttach.getClass(), movimientoenventaListNewMovimientoenventaToAttach.getIdMovimientoVenta());
                attachedMovimientoenventaListNew.add(movimientoenventaListNewMovimientoenventaToAttach);
            }
            movimientoenventaListNew = attachedMovimientoenventaListNew;
            venta.setMovimientoenventaList(movimientoenventaListNew);
            List<VentaTalla> attachedVentatallaListNew = new ArrayList<VentaTalla>();
            for (VentaTalla ventatallaListNewVentatallaToAttach : ventatallaListNew) {
                ventatallaListNewVentatallaToAttach = em.getReference(ventatallaListNewVentatallaToAttach.getClass(), ventatallaListNewVentatallaToAttach.getIdVentaTalla());
                attachedVentatallaListNew.add(ventatallaListNewVentatallaToAttach);
            }
            ventatallaListNew = attachedVentatallaListNew;
            venta.setVentatallaList(ventatallaListNew);
            venta = em.merge(venta);
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getVentaList().remove(venta);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getVentaList().add(venta);
                idUsuarioNew = em.merge(idUsuarioNew);
            }
            for (MovimientoEnVenta movimientoenventaListNewMovimientoenventa : movimientoenventaListNew) {
                if (!movimientoenventaListOld.contains(movimientoenventaListNewMovimientoenventa)) {
                    Venta oldIdVentaOfMovimientoenventaListNewMovimientoenventa = movimientoenventaListNewMovimientoenventa.getIdVenta();
                    movimientoenventaListNewMovimientoenventa.setIdVenta(venta);
                    movimientoenventaListNewMovimientoenventa = em.merge(movimientoenventaListNewMovimientoenventa);
                    if (oldIdVentaOfMovimientoenventaListNewMovimientoenventa != null && !oldIdVentaOfMovimientoenventaListNewMovimientoenventa.equals(venta)) {
                        oldIdVentaOfMovimientoenventaListNewMovimientoenventa.getMovimientoenventaList().remove(movimientoenventaListNewMovimientoenventa);
                        oldIdVentaOfMovimientoenventaListNewMovimientoenventa = em.merge(oldIdVentaOfMovimientoenventaListNewMovimientoenventa);
                    }
                }
            }
            for (VentaTalla ventatallaListNewVentatalla : ventatallaListNew) {
                if (!ventatallaListOld.contains(ventatallaListNewVentatalla)) {
                    Venta oldIdVentaOfVentatallaListNewVentatalla = ventatallaListNewVentatalla.getIdVenta();
                    ventatallaListNewVentatalla.setIdVenta(venta);
                    ventatallaListNewVentatalla = em.merge(ventatallaListNewVentatalla);
                    if (oldIdVentaOfVentatallaListNewVentatalla != null && !oldIdVentaOfVentatallaListNewVentatalla.equals(venta)) {
                        oldIdVentaOfVentatallaListNewVentatalla.getVentatallaList().remove(ventatallaListNewVentatalla);
                        oldIdVentaOfVentatallaListNewVentatalla = em.merge(oldIdVentaOfVentatallaListNewVentatalla);
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
            List<MovimientoEnVenta> movimientoenventaListOrphanCheck = venta.getMovimientoenventaList();
            for (MovimientoEnVenta movimientoenventaListOrphanCheckMovimientoenventa : movimientoenventaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Venta (" + venta + ") cannot be destroyed since the Movimientoenventa " + movimientoenventaListOrphanCheckMovimientoenventa + " in its movimientoenventaList field has a non-nullable idVenta field.");
            }
            List<VentaTalla> ventatallaListOrphanCheck = venta.getVentatallaList();
            for (VentaTalla ventatallaListOrphanCheckVentatalla : ventatallaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Venta (" + venta + ") cannot be destroyed since the Ventatalla " + ventatallaListOrphanCheckVentatalla + " in its ventatallaList field has a non-nullable idVenta field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Usuario idUsuario = venta.getIdUsuario();
            if (idUsuario != null) {
                idUsuario.getVentaList().remove(venta);
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
