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
        if (venta.getMovimientoEnVentaList() == null) {
            venta.setMovimientoEnVentaList(new ArrayList<MovimientoEnVenta>());
        }
        if (venta.getVentaTallaList() == null) {
            venta.setVentaTallaList(new ArrayList<VentaTalla>());
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
            List<MovimientoEnVenta> attachedMovimientoEnVentaList = new ArrayList<MovimientoEnVenta>();
            for (MovimientoEnVenta movimientoEnVentaListMovimientoEnVentaToAttach : venta.getMovimientoEnVentaList()) {
                movimientoEnVentaListMovimientoEnVentaToAttach = em.getReference(movimientoEnVentaListMovimientoEnVentaToAttach.getClass(), movimientoEnVentaListMovimientoEnVentaToAttach.getIdMovimientoVenta());
                attachedMovimientoEnVentaList.add(movimientoEnVentaListMovimientoEnVentaToAttach);
            }
            venta.setMovimientoEnVentaList(attachedMovimientoEnVentaList);
            List<VentaTalla> attachedVentaTallaList = new ArrayList<VentaTalla>();
            for (VentaTalla ventaTallaListVentaTallaToAttach : venta.getVentaTallaList()) {
                ventaTallaListVentaTallaToAttach = em.getReference(ventaTallaListVentaTallaToAttach.getClass(), ventaTallaListVentaTallaToAttach.getIdVentaTalla());
                attachedVentaTallaList.add(ventaTallaListVentaTallaToAttach);
            }
            venta.setVentaTallaList(attachedVentaTallaList);
            em.persist(venta);
            if (idUsuario != null) {
                idUsuario.getVentaList().add(venta);
                idUsuario = em.merge(idUsuario);
            }
            for (MovimientoEnVenta movimientoEnVentaListMovimientoEnVenta : venta.getMovimientoEnVentaList()) {
                Venta oldIdVentaOfMovimientoEnVentaListMovimientoEnVenta = movimientoEnVentaListMovimientoEnVenta.getIdVenta();
                movimientoEnVentaListMovimientoEnVenta.setIdVenta(venta);
                movimientoEnVentaListMovimientoEnVenta = em.merge(movimientoEnVentaListMovimientoEnVenta);
                if (oldIdVentaOfMovimientoEnVentaListMovimientoEnVenta != null) {
                    oldIdVentaOfMovimientoEnVentaListMovimientoEnVenta.getMovimientoEnVentaList().remove(movimientoEnVentaListMovimientoEnVenta);
                    oldIdVentaOfMovimientoEnVentaListMovimientoEnVenta = em.merge(oldIdVentaOfMovimientoEnVentaListMovimientoEnVenta);
                }
            }
            for (VentaTalla ventaTallaListVentaTalla : venta.getVentaTallaList()) {
                Venta oldIdVentaOfVentaTallaListVentaTalla = ventaTallaListVentaTalla.getIdVenta();
                ventaTallaListVentaTalla.setIdVenta(venta);
                ventaTallaListVentaTalla = em.merge(ventaTallaListVentaTalla);
                if (oldIdVentaOfVentaTallaListVentaTalla != null) {
                    oldIdVentaOfVentaTallaListVentaTalla.getVentaTallaList().remove(ventaTallaListVentaTalla);
                    oldIdVentaOfVentaTallaListVentaTalla = em.merge(oldIdVentaOfVentaTallaListVentaTalla);
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
            List<MovimientoEnVenta> movimientoEnVentaListOld = persistentVenta.getMovimientoEnVentaList();
            List<MovimientoEnVenta> movimientoEnVentaListNew = venta.getMovimientoEnVentaList();
            List<VentaTalla> ventaTallaListOld = persistentVenta.getVentaTallaList();
            List<VentaTalla> ventaTallaListNew = venta.getVentaTallaList();
            List<String> illegalOrphanMessages = null;
            for (MovimientoEnVenta movimientoEnVentaListOldMovimientoEnVenta : movimientoEnVentaListOld) {
                if (!movimientoEnVentaListNew.contains(movimientoEnVentaListOldMovimientoEnVenta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain MovimientoEnVenta " + movimientoEnVentaListOldMovimientoEnVenta + " since its idVenta field is not nullable.");
                }
            }
            for (VentaTalla ventaTallaListOldVentaTalla : ventaTallaListOld) {
                if (!ventaTallaListNew.contains(ventaTallaListOldVentaTalla)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain VentaTalla " + ventaTallaListOldVentaTalla + " since its idVenta field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getIdUsuario());
                venta.setIdUsuario(idUsuarioNew);
            }
            List<MovimientoEnVenta> attachedMovimientoEnVentaListNew = new ArrayList<MovimientoEnVenta>();
            for (MovimientoEnVenta movimientoEnVentaListNewMovimientoEnVentaToAttach : movimientoEnVentaListNew) {
                movimientoEnVentaListNewMovimientoEnVentaToAttach = em.getReference(movimientoEnVentaListNewMovimientoEnVentaToAttach.getClass(), movimientoEnVentaListNewMovimientoEnVentaToAttach.getIdMovimientoVenta());
                attachedMovimientoEnVentaListNew.add(movimientoEnVentaListNewMovimientoEnVentaToAttach);
            }
            movimientoEnVentaListNew = attachedMovimientoEnVentaListNew;
            venta.setMovimientoEnVentaList(movimientoEnVentaListNew);
            List<VentaTalla> attachedVentaTallaListNew = new ArrayList<VentaTalla>();
            for (VentaTalla ventaTallaListNewVentaTallaToAttach : ventaTallaListNew) {
                ventaTallaListNewVentaTallaToAttach = em.getReference(ventaTallaListNewVentaTallaToAttach.getClass(), ventaTallaListNewVentaTallaToAttach.getIdVentaTalla());
                attachedVentaTallaListNew.add(ventaTallaListNewVentaTallaToAttach);
            }
            ventaTallaListNew = attachedVentaTallaListNew;
            venta.setVentaTallaList(ventaTallaListNew);
            venta = em.merge(venta);
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getVentaList().remove(venta);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getVentaList().add(venta);
                idUsuarioNew = em.merge(idUsuarioNew);
            }
            for (MovimientoEnVenta movimientoEnVentaListNewMovimientoEnVenta : movimientoEnVentaListNew) {
                if (!movimientoEnVentaListOld.contains(movimientoEnVentaListNewMovimientoEnVenta)) {
                    Venta oldIdVentaOfMovimientoEnVentaListNewMovimientoEnVenta = movimientoEnVentaListNewMovimientoEnVenta.getIdVenta();
                    movimientoEnVentaListNewMovimientoEnVenta.setIdVenta(venta);
                    movimientoEnVentaListNewMovimientoEnVenta = em.merge(movimientoEnVentaListNewMovimientoEnVenta);
                    if (oldIdVentaOfMovimientoEnVentaListNewMovimientoEnVenta != null && !oldIdVentaOfMovimientoEnVentaListNewMovimientoEnVenta.equals(venta)) {
                        oldIdVentaOfMovimientoEnVentaListNewMovimientoEnVenta.getMovimientoEnVentaList().remove(movimientoEnVentaListNewMovimientoEnVenta);
                        oldIdVentaOfMovimientoEnVentaListNewMovimientoEnVenta = em.merge(oldIdVentaOfMovimientoEnVentaListNewMovimientoEnVenta);
                    }
                }
            }
            for (VentaTalla ventaTallaListNewVentaTalla : ventaTallaListNew) {
                if (!ventaTallaListOld.contains(ventaTallaListNewVentaTalla)) {
                    Venta oldIdVentaOfVentaTallaListNewVentaTalla = ventaTallaListNewVentaTalla.getIdVenta();
                    ventaTallaListNewVentaTalla.setIdVenta(venta);
                    ventaTallaListNewVentaTalla = em.merge(ventaTallaListNewVentaTalla);
                    if (oldIdVentaOfVentaTallaListNewVentaTalla != null && !oldIdVentaOfVentaTallaListNewVentaTalla.equals(venta)) {
                        oldIdVentaOfVentaTallaListNewVentaTalla.getVentaTallaList().remove(ventaTallaListNewVentaTalla);
                        oldIdVentaOfVentaTallaListNewVentaTalla = em.merge(oldIdVentaOfVentaTallaListNewVentaTalla);
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
            List<MovimientoEnVenta> movimientoEnVentaListOrphanCheck = venta.getMovimientoEnVentaList();
            for (MovimientoEnVenta movimientoEnVentaListOrphanCheckMovimientoEnVenta : movimientoEnVentaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Venta (" + venta + ") cannot be destroyed since the MovimientoEnVenta " + movimientoEnVentaListOrphanCheckMovimientoEnVenta + " in its movimientoEnVentaList field has a non-nullable idVenta field.");
            }
            List<VentaTalla> ventaTallaListOrphanCheck = venta.getVentaTallaList();
            for (VentaTalla ventaTallaListOrphanCheckVentaTalla : ventaTallaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Venta (" + venta + ") cannot be destroyed since the VentaTalla " + ventaTallaListOrphanCheckVentaTalla + " in its ventaTallaList field has a non-nullable idVenta field.");
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
