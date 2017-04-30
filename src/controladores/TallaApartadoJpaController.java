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
import objetosNegocio.Apartado;
import objetosNegocio.TallaApartado;

/**
 *
 * @author zippy
 */
public class TallaApartadoJpaController implements Serializable {

    public TallaApartadoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TallaApartado tallaapartado) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Talla idTalla = tallaapartado.getIdTalla();
            if (idTalla != null) {
                idTalla = em.getReference(idTalla.getClass(), idTalla.getIdTalla());
                tallaapartado.setIdTalla(idTalla);
            }
            Apartado idApartado = tallaapartado.getIdApartado();
            if (idApartado != null) {
                idApartado = em.getReference(idApartado.getClass(), idApartado.getIdApartado());
                tallaapartado.setIdApartado(idApartado);
            }
            em.persist(tallaapartado);
            if (idTalla != null) {
                idTalla.getTallaapartadoList().add(tallaapartado);
                idTalla = em.merge(idTalla);
            }
            if (idApartado != null) {
                idApartado.getTallaapartadoList().add(tallaapartado);
                idApartado = em.merge(idApartado);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTallaapartado(tallaapartado.getIdTallaApartado()) != null) {
                throw new PreexistingEntityException("Tallaapartado " + tallaapartado + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TallaApartado tallaapartado) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TallaApartado persistentTallaapartado = em.find(TallaApartado.class, tallaapartado.getIdTallaApartado());
            Talla idTallaOld = persistentTallaapartado.getIdTalla();
            Talla idTallaNew = tallaapartado.getIdTalla();
            Apartado idApartadoOld = persistentTallaapartado.getIdApartado();
            Apartado idApartadoNew = tallaapartado.getIdApartado();
            if (idTallaNew != null) {
                idTallaNew = em.getReference(idTallaNew.getClass(), idTallaNew.getIdTalla());
                tallaapartado.setIdTalla(idTallaNew);
            }
            if (idApartadoNew != null) {
                idApartadoNew = em.getReference(idApartadoNew.getClass(), idApartadoNew.getIdApartado());
                tallaapartado.setIdApartado(idApartadoNew);
            }
            tallaapartado = em.merge(tallaapartado);
            if (idTallaOld != null && !idTallaOld.equals(idTallaNew)) {
                idTallaOld.getTallaapartadoList().remove(tallaapartado);
                idTallaOld = em.merge(idTallaOld);
            }
            if (idTallaNew != null && !idTallaNew.equals(idTallaOld)) {
                idTallaNew.getTallaapartadoList().add(tallaapartado);
                idTallaNew = em.merge(idTallaNew);
            }
            if (idApartadoOld != null && !idApartadoOld.equals(idApartadoNew)) {
                idApartadoOld.getTallaapartadoList().remove(tallaapartado);
                idApartadoOld = em.merge(idApartadoOld);
            }
            if (idApartadoNew != null && !idApartadoNew.equals(idApartadoOld)) {
                idApartadoNew.getTallaapartadoList().add(tallaapartado);
                idApartadoNew = em.merge(idApartadoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = tallaapartado.getIdTallaApartado();
                if (findTallaapartado(id) == null) {
                    throw new NonexistentEntityException("The tallaapartado with id " + id + " no longer exists.");
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
            TallaApartado tallaapartado;
            try {
                tallaapartado = em.getReference(TallaApartado.class, id);
                tallaapartado.getIdTallaApartado();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tallaapartado with id " + id + " no longer exists.", enfe);
            }
            Talla idTalla = tallaapartado.getIdTalla();
            if (idTalla != null) {
                idTalla.getTallaapartadoList().remove(tallaapartado);
                idTalla = em.merge(idTalla);
            }
            Apartado idApartado = tallaapartado.getIdApartado();
            if (idApartado != null) {
                idApartado.getTallaapartadoList().remove(tallaapartado);
                idApartado = em.merge(idApartado);
            }
            em.remove(tallaapartado);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TallaApartado> findTallaapartadoEntities() {
        return findTallaapartadoEntities(true, -1, -1);
    }

    public List<TallaApartado> findTallaapartadoEntities(int maxResults, int firstResult) {
        return findTallaapartadoEntities(false, maxResults, firstResult);
    }

    private List<TallaApartado> findTallaapartadoEntities(boolean all, int maxResults, int firstResult) {
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

    public TallaApartado findTallaapartado(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TallaApartado.class, id);
        } finally {
            em.close();
        }
    }

    public int getTallaapartadoCount() {
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
