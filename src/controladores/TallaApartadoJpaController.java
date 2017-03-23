
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
import objetosNegocio.Apartado;
import objetosNegocio.TallaApartado;

/**
 *
 * @author Raul Karim Sabag Ballesteros
 */
public class TallaApartadoJpaController implements Serializable {

    public TallaApartadoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TallaApartado tallaApartado) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Talla idTalla = tallaApartado.getIdTalla();
            if (idTalla != null) {
                idTalla = em.getReference(idTalla.getClass(), idTalla.getIdTalla());
                tallaApartado.setIdTalla(idTalla);
            }
            Apartado idApartado = tallaApartado.getIdApartado();
            if (idApartado != null) {
                idApartado = em.getReference(idApartado.getClass(), idApartado.getIdApartado());
                tallaApartado.setIdApartado(idApartado);
            }
            em.persist(tallaApartado);
            if (idTalla != null) {
                idTalla.getTallaApartadoCollection().add(tallaApartado);
                idTalla = em.merge(idTalla);
            }
            if (idApartado != null) {
                idApartado.getTallaApartadoCollection().add(tallaApartado);
                idApartado = em.merge(idApartado);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTallaApartado(tallaApartado.getIdTallaApartado()) != null) {
                throw new PreexistingEntityException("TallaApartado " + tallaApartado + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TallaApartado tallaApartado) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TallaApartado persistentTallaApartado = em.find(TallaApartado.class, tallaApartado.getIdTallaApartado());
            Talla idTallaOld = persistentTallaApartado.getIdTalla();
            Talla idTallaNew = tallaApartado.getIdTalla();
            Apartado idApartadoOld = persistentTallaApartado.getIdApartado();
            Apartado idApartadoNew = tallaApartado.getIdApartado();
            if (idTallaNew != null) {
                idTallaNew = em.getReference(idTallaNew.getClass(), idTallaNew.getIdTalla());
                tallaApartado.setIdTalla(idTallaNew);
            }
            if (idApartadoNew != null) {
                idApartadoNew = em.getReference(idApartadoNew.getClass(), idApartadoNew.getIdApartado());
                tallaApartado.setIdApartado(idApartadoNew);
            }
            tallaApartado = em.merge(tallaApartado);
            if (idTallaOld != null && !idTallaOld.equals(idTallaNew)) {
                idTallaOld.getTallaApartadoCollection().remove(tallaApartado);
                idTallaOld = em.merge(idTallaOld);
            }
            if (idTallaNew != null && !idTallaNew.equals(idTallaOld)) {
                idTallaNew.getTallaApartadoCollection().add(tallaApartado);
                idTallaNew = em.merge(idTallaNew);
            }
            if (idApartadoOld != null && !idApartadoOld.equals(idApartadoNew)) {
                idApartadoOld.getTallaApartadoCollection().remove(tallaApartado);
                idApartadoOld = em.merge(idApartadoOld);
            }
            if (idApartadoNew != null && !idApartadoNew.equals(idApartadoOld)) {
                idApartadoNew.getTallaApartadoCollection().add(tallaApartado);
                idApartadoNew = em.merge(idApartadoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = tallaApartado.getIdTallaApartado();
                if (findTallaApartado(id) == null) {
                    throw new NonexistentEntityException("The tallaApartado with id " + id + " no longer exists.");
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
            TallaApartado tallaApartado;
            try {
                tallaApartado = em.getReference(TallaApartado.class, id);
                tallaApartado.getIdTallaApartado();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tallaApartado with id " + id + " no longer exists.", enfe);
            }
            Talla idTalla = tallaApartado.getIdTalla();
            if (idTalla != null) {
                idTalla.getTallaApartadoCollection().remove(tallaApartado);
                idTalla = em.merge(idTalla);
            }
            Apartado idApartado = tallaApartado.getIdApartado();
            if (idApartado != null) {
                idApartado.getTallaApartadoCollection().remove(tallaApartado);
                idApartado = em.merge(idApartado);
            }
            em.remove(tallaApartado);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TallaApartado> findTallaApartadoEntities() {
        return findTallaApartadoEntities(true, -1, -1);
    }

    public List<TallaApartado> findTallaApartadoEntities(int maxResults, int firstResult) {
        return findTallaApartadoEntities(false, maxResults, firstResult);
    }

    private List<TallaApartado> findTallaApartadoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TallaApartado.class));
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

    public TallaApartado findTallaApartado(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TallaApartado.class, id);
        } finally {
            em.close();
        }
    }

    public int getTallaApartadoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TallaApartado> rt = cq.from(TallaApartado.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
