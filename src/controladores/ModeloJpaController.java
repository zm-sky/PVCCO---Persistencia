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
import objetosNegocio.Talla;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import objetosNegocio.Modelo;

/**
 *
 * @author zippy
 */
public class ModeloJpaController implements Serializable {

    public ModeloJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Modelo modelo) throws PreexistingEntityException, Exception {
        if (modelo.getTallaList() == null) {
            modelo.setTallaList(new ArrayList<Talla>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Talla> attachedTallaList = new ArrayList<Talla>();
            for (Talla tallaListTallaToAttach : modelo.getTallaList()) {
                tallaListTallaToAttach = em.getReference(tallaListTallaToAttach.getClass(), tallaListTallaToAttach.getIdTalla());
                attachedTallaList.add(tallaListTallaToAttach);
            }
            modelo.setTallaList(attachedTallaList);
            em.persist(modelo);
            for (Talla tallaListTalla : modelo.getTallaList()) {
                Modelo oldIdModeloOfTallaListTalla = tallaListTalla.getIdModelo();
                tallaListTalla.setIdModelo(modelo);
                tallaListTalla = em.merge(tallaListTalla);
                if (oldIdModeloOfTallaListTalla != null) {
                    oldIdModeloOfTallaListTalla.getTallaList().remove(tallaListTalla);
                    oldIdModeloOfTallaListTalla = em.merge(oldIdModeloOfTallaListTalla);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findModelo(modelo.getIdModelo()) != null) {
                throw new PreexistingEntityException("Modelo " + modelo + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Modelo modelo) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Modelo persistentModelo = em.find(Modelo.class, modelo.getIdModelo());
            List<Talla> tallaListOld = persistentModelo.getTallaList();
            List<Talla> tallaListNew = modelo.getTallaList();
            List<String> illegalOrphanMessages = null;
            for (Talla tallaListOldTalla : tallaListOld) {
                if (!tallaListNew.contains(tallaListOldTalla)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Talla " + tallaListOldTalla + " since its idModelo field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Talla> attachedTallaListNew = new ArrayList<Talla>();
            for (Talla tallaListNewTallaToAttach : tallaListNew) {
                tallaListNewTallaToAttach = em.getReference(tallaListNewTallaToAttach.getClass(), tallaListNewTallaToAttach.getIdTalla());
                attachedTallaListNew.add(tallaListNewTallaToAttach);
            }
            tallaListNew = attachedTallaListNew;
            modelo.setTallaList(tallaListNew);
            modelo = em.merge(modelo);
            for (Talla tallaListNewTalla : tallaListNew) {
                if (!tallaListOld.contains(tallaListNewTalla)) {
                    Modelo oldIdModeloOfTallaListNewTalla = tallaListNewTalla.getIdModelo();
                    tallaListNewTalla.setIdModelo(modelo);
                    tallaListNewTalla = em.merge(tallaListNewTalla);
                    if (oldIdModeloOfTallaListNewTalla != null && !oldIdModeloOfTallaListNewTalla.equals(modelo)) {
                        oldIdModeloOfTallaListNewTalla.getTallaList().remove(tallaListNewTalla);
                        oldIdModeloOfTallaListNewTalla = em.merge(oldIdModeloOfTallaListNewTalla);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = modelo.getIdModelo();
                if (findModelo(id) == null) {
                    throw new NonexistentEntityException("The modelo with id " + id + " no longer exists.");
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
            Modelo modelo;
            try {
                modelo = em.getReference(Modelo.class, id);
                modelo.getIdModelo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The modelo with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Talla> tallaListOrphanCheck = modelo.getTallaList();
            for (Talla tallaListOrphanCheckTalla : tallaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Modelo (" + modelo + ") cannot be destroyed since the Talla " + tallaListOrphanCheckTalla + " in its tallaList field has a non-nullable idModelo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(modelo);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Modelo> findModeloEntities() {
        return findModeloEntities(true, -1, -1);
    }

    public List<Modelo> findModeloEntities(int maxResults, int firstResult) {
        return findModeloEntities(false, maxResults, firstResult);
    }

    private List<Modelo> findModeloEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Modelo.class));
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

    public Modelo findModelo(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Modelo.class, id);
        } finally {
            em.close();
        }
    }

    public int getModeloCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Modelo> rt = cq.from(Modelo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    /**
     * Regresa un modelo el cual el nombre sera igual al nombre del modelo del
     * paramentro.
     *
     * @param modelo
     * @return
     */
    public Modelo getModeloPorNombre(Modelo modelo) {
        EntityManager em = getEntityManager();
        TypedQuery<Modelo> query = em.createNamedQuery("Modelo.findByNombre", Modelo.class);
        query.setParameter("nombre", modelo.getNombre());
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
