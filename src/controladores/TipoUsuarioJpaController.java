
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import objetosNegocio.TipoUsuario;

/**
 *
 * @author Raul Karim Sabag Ballesteros
 */
public class TipoUsuarioJpaController implements Serializable {

    public TipoUsuarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TipoUsuario tipoUsuario) throws PreexistingEntityException, Exception {
        if (tipoUsuario.getUsuarioCollection() == null) {
            tipoUsuario.setUsuarioCollection(new ArrayList<Usuario>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Usuario> attachedUsuarioCollection = new ArrayList<Usuario>();
            for (Usuario usuarioCollectionUsuarioToAttach : tipoUsuario.getUsuarioCollection()) {
                usuarioCollectionUsuarioToAttach = em.getReference(usuarioCollectionUsuarioToAttach.getClass(), usuarioCollectionUsuarioToAttach.getIdUsuario());
                attachedUsuarioCollection.add(usuarioCollectionUsuarioToAttach);
            }
            tipoUsuario.setUsuarioCollection(attachedUsuarioCollection);
            em.persist(tipoUsuario);
            for (Usuario usuarioCollectionUsuario : tipoUsuario.getUsuarioCollection()) {
                TipoUsuario oldIdTipoUsuarioOfUsuarioCollectionUsuario = usuarioCollectionUsuario.getIdTipoUsuario();
                usuarioCollectionUsuario.setIdTipoUsuario(tipoUsuario);
                usuarioCollectionUsuario = em.merge(usuarioCollectionUsuario);
                if (oldIdTipoUsuarioOfUsuarioCollectionUsuario != null) {
                    oldIdTipoUsuarioOfUsuarioCollectionUsuario.getUsuarioCollection().remove(usuarioCollectionUsuario);
                    oldIdTipoUsuarioOfUsuarioCollectionUsuario = em.merge(oldIdTipoUsuarioOfUsuarioCollectionUsuario);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTipoUsuario(tipoUsuario.getIdTipoUsuario()) != null) {
                throw new PreexistingEntityException("TipoUsuario " + tipoUsuario + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TipoUsuario tipoUsuario) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TipoUsuario persistentTipoUsuario = em.find(TipoUsuario.class, tipoUsuario.getIdTipoUsuario());
            Collection<Usuario> usuarioCollectionOld = persistentTipoUsuario.getUsuarioCollection();
            Collection<Usuario> usuarioCollectionNew = tipoUsuario.getUsuarioCollection();
            List<String> illegalOrphanMessages = null;
            for (Usuario usuarioCollectionOldUsuario : usuarioCollectionOld) {
                if (!usuarioCollectionNew.contains(usuarioCollectionOldUsuario)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Usuario " + usuarioCollectionOldUsuario + " since its idTipoUsuario field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Usuario> attachedUsuarioCollectionNew = new ArrayList<Usuario>();
            for (Usuario usuarioCollectionNewUsuarioToAttach : usuarioCollectionNew) {
                usuarioCollectionNewUsuarioToAttach = em.getReference(usuarioCollectionNewUsuarioToAttach.getClass(), usuarioCollectionNewUsuarioToAttach.getIdUsuario());
                attachedUsuarioCollectionNew.add(usuarioCollectionNewUsuarioToAttach);
            }
            usuarioCollectionNew = attachedUsuarioCollectionNew;
            tipoUsuario.setUsuarioCollection(usuarioCollectionNew);
            tipoUsuario = em.merge(tipoUsuario);
            for (Usuario usuarioCollectionNewUsuario : usuarioCollectionNew) {
                if (!usuarioCollectionOld.contains(usuarioCollectionNewUsuario)) {
                    TipoUsuario oldIdTipoUsuarioOfUsuarioCollectionNewUsuario = usuarioCollectionNewUsuario.getIdTipoUsuario();
                    usuarioCollectionNewUsuario.setIdTipoUsuario(tipoUsuario);
                    usuarioCollectionNewUsuario = em.merge(usuarioCollectionNewUsuario);
                    if (oldIdTipoUsuarioOfUsuarioCollectionNewUsuario != null && !oldIdTipoUsuarioOfUsuarioCollectionNewUsuario.equals(tipoUsuario)) {
                        oldIdTipoUsuarioOfUsuarioCollectionNewUsuario.getUsuarioCollection().remove(usuarioCollectionNewUsuario);
                        oldIdTipoUsuarioOfUsuarioCollectionNewUsuario = em.merge(oldIdTipoUsuarioOfUsuarioCollectionNewUsuario);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = tipoUsuario.getIdTipoUsuario();
                if (findTipoUsuario(id) == null) {
                    throw new NonexistentEntityException("The tipoUsuario with id " + id + " no longer exists.");
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
            TipoUsuario tipoUsuario;
            try {
                tipoUsuario = em.getReference(TipoUsuario.class, id);
                tipoUsuario.getIdTipoUsuario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tipoUsuario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Usuario> usuarioCollectionOrphanCheck = tipoUsuario.getUsuarioCollection();
            for (Usuario usuarioCollectionOrphanCheckUsuario : usuarioCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This TipoUsuario (" + tipoUsuario + ") cannot be destroyed since the Usuario " + usuarioCollectionOrphanCheckUsuario + " in its usuarioCollection field has a non-nullable idTipoUsuario field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(tipoUsuario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TipoUsuario> findTipoUsuarioEntities() {
        return findTipoUsuarioEntities(true, -1, -1);
    }

    public List<TipoUsuario> findTipoUsuarioEntities(int maxResults, int firstResult) {
        return findTipoUsuarioEntities(false, maxResults, firstResult);
    }

    private List<TipoUsuario> findTipoUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TipoUsuario.class));
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

    public TipoUsuario findTipoUsuario(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TipoUsuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getTipoUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TipoUsuario> rt = cq.from(TipoUsuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
