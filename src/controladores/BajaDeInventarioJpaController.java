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
import objetosNegocio.BajaDeInventario;
import objetosNegocio.Talla;

/**
 *
 * @author zippy
 */
public class BajaDeInventarioJpaController implements Serializable {

    public BajaDeInventarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(BajaDeInventario bajadeinventario) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Talla idTalla = bajadeinventario.getIdTalla();
            if (idTalla != null) {
                idTalla = em.getReference(idTalla.getClass(), idTalla.getIdTalla());
                bajadeinventario.setIdTalla(idTalla);
            }
            em.persist(bajadeinventario);
            if (idTalla != null) {
                idTalla.getBajadeinventarioList().add(bajadeinventario);
                idTalla = em.merge(idTalla);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findBajadeinventario(bajadeinventario.getIdBajaInventario()) != null) {
                throw new PreexistingEntityException("Bajadeinventario " + bajadeinventario + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(BajaDeInventario bajadeinventario) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            BajaDeInventario persistentBajadeinventario = em.find(BajaDeInventario.class, bajadeinventario.getIdBajaInventario());
            Talla idTallaOld = persistentBajadeinventario.getIdTalla();
            Talla idTallaNew = bajadeinventario.getIdTalla();
            if (idTallaNew != null) {
                idTallaNew = em.getReference(idTallaNew.getClass(), idTallaNew.getIdTalla());
                bajadeinventario.setIdTalla(idTallaNew);
            }
            bajadeinventario = em.merge(bajadeinventario);
            if (idTallaOld != null && !idTallaOld.equals(idTallaNew)) {
                idTallaOld.getBajadeinventarioList().remove(bajadeinventario);
                idTallaOld = em.merge(idTallaOld);
            }
            if (idTallaNew != null && !idTallaNew.equals(idTallaOld)) {
                idTallaNew.getBajadeinventarioList().add(bajadeinventario);
                idTallaNew = em.merge(idTallaNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = bajadeinventario.getIdBajaInventario();
                if (findBajadeinventario(id) == null) {
                    throw new NonexistentEntityException("The bajadeinventario with id " + id + " no longer exists.");
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
            BajaDeInventario bajadeinventario;
            try {
                bajadeinventario = em.getReference(BajaDeInventario.class, id);
                bajadeinventario.getIdBajaInventario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The bajadeinventario with id " + id + " no longer exists.", enfe);
            }
            Talla idTalla = bajadeinventario.getIdTalla();
            if (idTalla != null) {
                idTalla.getBajadeinventarioList().remove(bajadeinventario);
                idTalla = em.merge(idTalla);
            }
            em.remove(bajadeinventario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<BajaDeInventario> findBajadeinventarioEntities() {
        return findBajadeinventarioEntities(true, -1, -1);
    }

    public List<BajaDeInventario> findBajadeinventarioEntities(int maxResults, int firstResult) {
        return findBajadeinventarioEntities(false, maxResults, firstResult);
    }

    private List<BajaDeInventario> findBajadeinventarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(BajaDeInventario.class));
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

    public BajaDeInventario findBajadeinventario(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(BajaDeInventario.class, id);
        } finally {
            em.close();
        }
    }

    public int getBajadeinventarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<BajaDeInventario> rt = cq.from(BajaDeInventario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
