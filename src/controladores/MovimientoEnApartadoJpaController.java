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
import objetosNegocio.Apartado;
import objetosNegocio.MovimientoEnApartado;

/**
 *
 * @author zippy
 */
public class MovimientoEnApartadoJpaController implements Serializable {

    public MovimientoEnApartadoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(MovimientoEnApartado movimientoenapartado) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Apartado idApartado = movimientoenapartado.getIdApartado();
            if (idApartado != null) {
                idApartado = em.getReference(idApartado.getClass(), idApartado.getIdApartado());
                movimientoenapartado.setIdApartado(idApartado);
            }
            em.persist(movimientoenapartado);
            if (idApartado != null) {
                idApartado.getMovimientoenapartadoList().add(movimientoenapartado);
                idApartado = em.merge(idApartado);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMovimientoenapartado(movimientoenapartado.getIdMovimientoApartado()) != null) {
                throw new PreexistingEntityException("Movimientoenapartado " + movimientoenapartado + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(MovimientoEnApartado movimientoenapartado) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MovimientoEnApartado persistentMovimientoenapartado = em.find(MovimientoEnApartado.class, movimientoenapartado.getIdMovimientoApartado());
            Apartado idApartadoOld = persistentMovimientoenapartado.getIdApartado();
            Apartado idApartadoNew = movimientoenapartado.getIdApartado();
            if (idApartadoNew != null) {
                idApartadoNew = em.getReference(idApartadoNew.getClass(), idApartadoNew.getIdApartado());
                movimientoenapartado.setIdApartado(idApartadoNew);
            }
            movimientoenapartado = em.merge(movimientoenapartado);
            if (idApartadoOld != null && !idApartadoOld.equals(idApartadoNew)) {
                idApartadoOld.getMovimientoenapartadoList().remove(movimientoenapartado);
                idApartadoOld = em.merge(idApartadoOld);
            }
            if (idApartadoNew != null && !idApartadoNew.equals(idApartadoOld)) {
                idApartadoNew.getMovimientoenapartadoList().add(movimientoenapartado);
                idApartadoNew = em.merge(idApartadoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = movimientoenapartado.getIdMovimientoApartado();
                if (findMovimientoenapartado(id) == null) {
                    throw new NonexistentEntityException("The movimientoenapartado with id " + id + " no longer exists.");
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
            MovimientoEnApartado movimientoenapartado;
            try {
                movimientoenapartado = em.getReference(MovimientoEnApartado.class, id);
                movimientoenapartado.getIdMovimientoApartado();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The movimientoenapartado with id " + id + " no longer exists.", enfe);
            }
            Apartado idApartado = movimientoenapartado.getIdApartado();
            if (idApartado != null) {
                idApartado.getMovimientoenapartadoList().remove(movimientoenapartado);
                idApartado = em.merge(idApartado);
            }
            em.remove(movimientoenapartado);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<MovimientoEnApartado> findMovimientoenapartadoEntities() {
        return findMovimientoenapartadoEntities(true, -1, -1);
    }

    public List<MovimientoEnApartado> findMovimientoenapartadoEntities(int maxResults, int firstResult) {
        return findMovimientoenapartadoEntities(false, maxResults, firstResult);
    }

    private List<MovimientoEnApartado> findMovimientoenapartadoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(MovimientoEnApartado.class));
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

    public MovimientoEnApartado findMovimientoenapartado(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(MovimientoEnApartado.class, id);
        } finally {
            em.close();
        }
    }

    public int getMovimientoenapartadoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<MovimientoEnApartado> rt = cq.from(MovimientoEnApartado.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
