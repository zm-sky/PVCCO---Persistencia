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
import objetosNegocio.TipoUsuario;
import objetosNegocio.Venta;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import objetosNegocio.Apartado;
import objetosNegocio.Usuario;

/**
 *
 * @author zippy
 */
public class UsuarioJpaController implements Serializable {

    public UsuarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) throws PreexistingEntityException, Exception {
        if (usuario.getVentaList() == null) {
            usuario.setVentaList(new ArrayList<Venta>());
        }
        if (usuario.getApartadoList() == null) {
            usuario.setApartadoList(new ArrayList<Apartado>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TipoUsuario idTipoUsuario = usuario.getIdTipoUsuario();
            if (idTipoUsuario != null) {
                idTipoUsuario = em.getReference(idTipoUsuario.getClass(), idTipoUsuario.getIdTipoUsuario());
                usuario.setIdTipoUsuario(idTipoUsuario);
            }
            List<Venta> attachedVentaList = new ArrayList<Venta>();
            for (Venta ventaListVentaToAttach : usuario.getVentaList()) {
                ventaListVentaToAttach = em.getReference(ventaListVentaToAttach.getClass(), ventaListVentaToAttach.getIdVenta());
                attachedVentaList.add(ventaListVentaToAttach);
            }
            usuario.setVentaList(attachedVentaList);
            List<Apartado> attachedApartadoList = new ArrayList<Apartado>();
            for (Apartado apartadoListApartadoToAttach : usuario.getApartadoList()) {
                apartadoListApartadoToAttach = em.getReference(apartadoListApartadoToAttach.getClass(), apartadoListApartadoToAttach.getIdApartado());
                attachedApartadoList.add(apartadoListApartadoToAttach);
            }
            usuario.setApartadoList(attachedApartadoList);
            em.persist(usuario);
            if (idTipoUsuario != null) {
                idTipoUsuario.getUsuarioList().add(usuario);
                idTipoUsuario = em.merge(idTipoUsuario);
            }
            for (Venta ventaListVenta : usuario.getVentaList()) {
                Usuario oldIdUsuarioOfVentaListVenta = ventaListVenta.getIdUsuario();
                ventaListVenta.setIdUsuario(usuario);
                ventaListVenta = em.merge(ventaListVenta);
                if (oldIdUsuarioOfVentaListVenta != null) {
                    oldIdUsuarioOfVentaListVenta.getVentaList().remove(ventaListVenta);
                    oldIdUsuarioOfVentaListVenta = em.merge(oldIdUsuarioOfVentaListVenta);
                }
            }
            for (Apartado apartadoListApartado : usuario.getApartadoList()) {
                Usuario oldIdUsuarioOfApartadoListApartado = apartadoListApartado.getIdUsuario();
                apartadoListApartado.setIdUsuario(usuario);
                apartadoListApartado = em.merge(apartadoListApartado);
                if (oldIdUsuarioOfApartadoListApartado != null) {
                    oldIdUsuarioOfApartadoListApartado.getApartadoList().remove(apartadoListApartado);
                    oldIdUsuarioOfApartadoListApartado = em.merge(oldIdUsuarioOfApartadoListApartado);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUsuario(usuario.getIdUsuario()) != null) {
                throw new PreexistingEntityException("Usuario " + usuario + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuario) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getIdUsuario());
            TipoUsuario idTipoUsuarioOld = persistentUsuario.getIdTipoUsuario();
            TipoUsuario idTipoUsuarioNew = usuario.getIdTipoUsuario();
            List<Venta> ventaListOld = persistentUsuario.getVentaList();
            List<Venta> ventaListNew = usuario.getVentaList();
            List<Apartado> apartadoListOld = persistentUsuario.getApartadoList();
            List<Apartado> apartadoListNew = usuario.getApartadoList();
            List<String> illegalOrphanMessages = null;
            for (Venta ventaListOldVenta : ventaListOld) {
                if (!ventaListNew.contains(ventaListOldVenta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Venta " + ventaListOldVenta + " since its idUsuario field is not nullable.");
                }
            }
            for (Apartado apartadoListOldApartado : apartadoListOld) {
                if (!apartadoListNew.contains(apartadoListOldApartado)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Apartado " + apartadoListOldApartado + " since its idUsuario field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idTipoUsuarioNew != null) {
                idTipoUsuarioNew = em.getReference(idTipoUsuarioNew.getClass(), idTipoUsuarioNew.getIdTipoUsuario());
                usuario.setIdTipoUsuario(idTipoUsuarioNew);
            }
            List<Venta> attachedVentaListNew = new ArrayList<Venta>();
            for (Venta ventaListNewVentaToAttach : ventaListNew) {
                ventaListNewVentaToAttach = em.getReference(ventaListNewVentaToAttach.getClass(), ventaListNewVentaToAttach.getIdVenta());
                attachedVentaListNew.add(ventaListNewVentaToAttach);
            }
            ventaListNew = attachedVentaListNew;
            usuario.setVentaList(ventaListNew);
            List<Apartado> attachedApartadoListNew = new ArrayList<Apartado>();
            for (Apartado apartadoListNewApartadoToAttach : apartadoListNew) {
                apartadoListNewApartadoToAttach = em.getReference(apartadoListNewApartadoToAttach.getClass(), apartadoListNewApartadoToAttach.getIdApartado());
                attachedApartadoListNew.add(apartadoListNewApartadoToAttach);
            }
            apartadoListNew = attachedApartadoListNew;
            usuario.setApartadoList(apartadoListNew);
            usuario = em.merge(usuario);
            if (idTipoUsuarioOld != null && !idTipoUsuarioOld.equals(idTipoUsuarioNew)) {
                idTipoUsuarioOld.getUsuarioList().remove(usuario);
                idTipoUsuarioOld = em.merge(idTipoUsuarioOld);
            }
            if (idTipoUsuarioNew != null && !idTipoUsuarioNew.equals(idTipoUsuarioOld)) {
                idTipoUsuarioNew.getUsuarioList().add(usuario);
                idTipoUsuarioNew = em.merge(idTipoUsuarioNew);
            }
            for (Venta ventaListNewVenta : ventaListNew) {
                if (!ventaListOld.contains(ventaListNewVenta)) {
                    Usuario oldIdUsuarioOfVentaListNewVenta = ventaListNewVenta.getIdUsuario();
                    ventaListNewVenta.setIdUsuario(usuario);
                    ventaListNewVenta = em.merge(ventaListNewVenta);
                    if (oldIdUsuarioOfVentaListNewVenta != null && !oldIdUsuarioOfVentaListNewVenta.equals(usuario)) {
                        oldIdUsuarioOfVentaListNewVenta.getVentaList().remove(ventaListNewVenta);
                        oldIdUsuarioOfVentaListNewVenta = em.merge(oldIdUsuarioOfVentaListNewVenta);
                    }
                }
            }
            for (Apartado apartadoListNewApartado : apartadoListNew) {
                if (!apartadoListOld.contains(apartadoListNewApartado)) {
                    Usuario oldIdUsuarioOfApartadoListNewApartado = apartadoListNewApartado.getIdUsuario();
                    apartadoListNewApartado.setIdUsuario(usuario);
                    apartadoListNewApartado = em.merge(apartadoListNewApartado);
                    if (oldIdUsuarioOfApartadoListNewApartado != null && !oldIdUsuarioOfApartadoListNewApartado.equals(usuario)) {
                        oldIdUsuarioOfApartadoListNewApartado.getApartadoList().remove(apartadoListNewApartado);
                        oldIdUsuarioOfApartadoListNewApartado = em.merge(oldIdUsuarioOfApartadoListNewApartado);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = usuario.getIdUsuario();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
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
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getIdUsuario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Venta> ventaListOrphanCheck = usuario.getVentaList();
            for (Venta ventaListOrphanCheckVenta : ventaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Venta " + ventaListOrphanCheckVenta + " in its ventaList field has a non-nullable idUsuario field.");
            }
            List<Apartado> apartadoListOrphanCheck = usuario.getApartadoList();
            for (Apartado apartadoListOrphanCheckApartado : apartadoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Apartado " + apartadoListOrphanCheckApartado + " in its apartadoList field has a non-nullable idUsuario field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            TipoUsuario idTipoUsuario = usuario.getIdTipoUsuario();
            if (idTipoUsuario != null) {
                idTipoUsuario.getUsuarioList().remove(usuario);
                idTipoUsuario = em.merge(idTipoUsuario);
            }
            em.remove(usuario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
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

    public Usuario findUsuario(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
