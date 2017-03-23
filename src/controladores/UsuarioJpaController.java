
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
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import objetosNegocio.Apartado;
import objetosNegocio.Usuario;

/**
 *
 * @author Raul Karim Sabag Ballesteros
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
        if (usuario.getVentaCollection() == null) {
            usuario.setVentaCollection(new ArrayList<Venta>());
        }
        if (usuario.getApartadoCollection() == null) {
            usuario.setApartadoCollection(new ArrayList<Apartado>());
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
            Collection<Venta> attachedVentaCollection = new ArrayList<Venta>();
            for (Venta ventaCollectionVentaToAttach : usuario.getVentaCollection()) {
                ventaCollectionVentaToAttach = em.getReference(ventaCollectionVentaToAttach.getClass(), ventaCollectionVentaToAttach.getIdVenta());
                attachedVentaCollection.add(ventaCollectionVentaToAttach);
            }
            usuario.setVentaCollection(attachedVentaCollection);
            Collection<Apartado> attachedApartadoCollection = new ArrayList<Apartado>();
            for (Apartado apartadoCollectionApartadoToAttach : usuario.getApartadoCollection()) {
                apartadoCollectionApartadoToAttach = em.getReference(apartadoCollectionApartadoToAttach.getClass(), apartadoCollectionApartadoToAttach.getIdApartado());
                attachedApartadoCollection.add(apartadoCollectionApartadoToAttach);
            }
            usuario.setApartadoCollection(attachedApartadoCollection);
            em.persist(usuario);
            if (idTipoUsuario != null) {
                idTipoUsuario.getUsuarioCollection().add(usuario);
                idTipoUsuario = em.merge(idTipoUsuario);
            }
            for (Venta ventaCollectionVenta : usuario.getVentaCollection()) {
                Usuario oldIdUsuarioOfVentaCollectionVenta = ventaCollectionVenta.getIdUsuario();
                ventaCollectionVenta.setIdUsuario(usuario);
                ventaCollectionVenta = em.merge(ventaCollectionVenta);
                if (oldIdUsuarioOfVentaCollectionVenta != null) {
                    oldIdUsuarioOfVentaCollectionVenta.getVentaCollection().remove(ventaCollectionVenta);
                    oldIdUsuarioOfVentaCollectionVenta = em.merge(oldIdUsuarioOfVentaCollectionVenta);
                }
            }
            for (Apartado apartadoCollectionApartado : usuario.getApartadoCollection()) {
                Usuario oldIdUsuarioOfApartadoCollectionApartado = apartadoCollectionApartado.getIdUsuario();
                apartadoCollectionApartado.setIdUsuario(usuario);
                apartadoCollectionApartado = em.merge(apartadoCollectionApartado);
                if (oldIdUsuarioOfApartadoCollectionApartado != null) {
                    oldIdUsuarioOfApartadoCollectionApartado.getApartadoCollection().remove(apartadoCollectionApartado);
                    oldIdUsuarioOfApartadoCollectionApartado = em.merge(oldIdUsuarioOfApartadoCollectionApartado);
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
            Collection<Venta> ventaCollectionOld = persistentUsuario.getVentaCollection();
            Collection<Venta> ventaCollectionNew = usuario.getVentaCollection();
            Collection<Apartado> apartadoCollectionOld = persistentUsuario.getApartadoCollection();
            Collection<Apartado> apartadoCollectionNew = usuario.getApartadoCollection();
            List<String> illegalOrphanMessages = null;
            for (Venta ventaCollectionOldVenta : ventaCollectionOld) {
                if (!ventaCollectionNew.contains(ventaCollectionOldVenta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Venta " + ventaCollectionOldVenta + " since its idUsuario field is not nullable.");
                }
            }
            for (Apartado apartadoCollectionOldApartado : apartadoCollectionOld) {
                if (!apartadoCollectionNew.contains(apartadoCollectionOldApartado)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Apartado " + apartadoCollectionOldApartado + " since its idUsuario field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idTipoUsuarioNew != null) {
                idTipoUsuarioNew = em.getReference(idTipoUsuarioNew.getClass(), idTipoUsuarioNew.getIdTipoUsuario());
                usuario.setIdTipoUsuario(idTipoUsuarioNew);
            }
            Collection<Venta> attachedVentaCollectionNew = new ArrayList<Venta>();
            for (Venta ventaCollectionNewVentaToAttach : ventaCollectionNew) {
                ventaCollectionNewVentaToAttach = em.getReference(ventaCollectionNewVentaToAttach.getClass(), ventaCollectionNewVentaToAttach.getIdVenta());
                attachedVentaCollectionNew.add(ventaCollectionNewVentaToAttach);
            }
            ventaCollectionNew = attachedVentaCollectionNew;
            usuario.setVentaCollection(ventaCollectionNew);
            Collection<Apartado> attachedApartadoCollectionNew = new ArrayList<Apartado>();
            for (Apartado apartadoCollectionNewApartadoToAttach : apartadoCollectionNew) {
                apartadoCollectionNewApartadoToAttach = em.getReference(apartadoCollectionNewApartadoToAttach.getClass(), apartadoCollectionNewApartadoToAttach.getIdApartado());
                attachedApartadoCollectionNew.add(apartadoCollectionNewApartadoToAttach);
            }
            apartadoCollectionNew = attachedApartadoCollectionNew;
            usuario.setApartadoCollection(apartadoCollectionNew);
            usuario = em.merge(usuario);
            if (idTipoUsuarioOld != null && !idTipoUsuarioOld.equals(idTipoUsuarioNew)) {
                idTipoUsuarioOld.getUsuarioCollection().remove(usuario);
                idTipoUsuarioOld = em.merge(idTipoUsuarioOld);
            }
            if (idTipoUsuarioNew != null && !idTipoUsuarioNew.equals(idTipoUsuarioOld)) {
                idTipoUsuarioNew.getUsuarioCollection().add(usuario);
                idTipoUsuarioNew = em.merge(idTipoUsuarioNew);
            }
            for (Venta ventaCollectionNewVenta : ventaCollectionNew) {
                if (!ventaCollectionOld.contains(ventaCollectionNewVenta)) {
                    Usuario oldIdUsuarioOfVentaCollectionNewVenta = ventaCollectionNewVenta.getIdUsuario();
                    ventaCollectionNewVenta.setIdUsuario(usuario);
                    ventaCollectionNewVenta = em.merge(ventaCollectionNewVenta);
                    if (oldIdUsuarioOfVentaCollectionNewVenta != null && !oldIdUsuarioOfVentaCollectionNewVenta.equals(usuario)) {
                        oldIdUsuarioOfVentaCollectionNewVenta.getVentaCollection().remove(ventaCollectionNewVenta);
                        oldIdUsuarioOfVentaCollectionNewVenta = em.merge(oldIdUsuarioOfVentaCollectionNewVenta);
                    }
                }
            }
            for (Apartado apartadoCollectionNewApartado : apartadoCollectionNew) {
                if (!apartadoCollectionOld.contains(apartadoCollectionNewApartado)) {
                    Usuario oldIdUsuarioOfApartadoCollectionNewApartado = apartadoCollectionNewApartado.getIdUsuario();
                    apartadoCollectionNewApartado.setIdUsuario(usuario);
                    apartadoCollectionNewApartado = em.merge(apartadoCollectionNewApartado);
                    if (oldIdUsuarioOfApartadoCollectionNewApartado != null && !oldIdUsuarioOfApartadoCollectionNewApartado.equals(usuario)) {
                        oldIdUsuarioOfApartadoCollectionNewApartado.getApartadoCollection().remove(apartadoCollectionNewApartado);
                        oldIdUsuarioOfApartadoCollectionNewApartado = em.merge(oldIdUsuarioOfApartadoCollectionNewApartado);
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
            Collection<Venta> ventaCollectionOrphanCheck = usuario.getVentaCollection();
            for (Venta ventaCollectionOrphanCheckVenta : ventaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Venta " + ventaCollectionOrphanCheckVenta + " in its ventaCollection field has a non-nullable idUsuario field.");
            }
            Collection<Apartado> apartadoCollectionOrphanCheck = usuario.getApartadoCollection();
            for (Apartado apartadoCollectionOrphanCheckApartado : apartadoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Apartado " + apartadoCollectionOrphanCheckApartado + " in its apartadoCollection field has a non-nullable idUsuario field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            TipoUsuario idTipoUsuario = usuario.getIdTipoUsuario();
            if (idTipoUsuario != null) {
                idTipoUsuario.getUsuarioCollection().remove(usuario);
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
