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
import objetosNegocio.MovimientoEnApartado;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import objetosNegocio.Apartado;
import objetosNegocio.TallaApartado;

/**
 *
 * @author zippy
 */
public class ApartadoJpaController implements Serializable {

    public ApartadoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Apartado apartado) throws PreexistingEntityException, Exception {
        if (apartado.getMovimientoenapartadoList() == null) {
            apartado.setMovimientoenapartadoList(new ArrayList<MovimientoEnApartado>());
        }
        if (apartado.getTallaapartadoList() == null) {
            apartado.setTallaapartadoList(new ArrayList<TallaApartado>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario idUsuario = apartado.getIdUsuario();
            if (idUsuario != null) {
                idUsuario = em.getReference(idUsuario.getClass(), idUsuario.getIdUsuario());
                apartado.setIdUsuario(idUsuario);
            }
            List<MovimientoEnApartado> attachedMovimientoenapartadoList = new ArrayList<MovimientoEnApartado>();
            for (MovimientoEnApartado movimientoenapartadoListMovimientoenapartadoToAttach : apartado.getMovimientoenapartadoList()) {
                movimientoenapartadoListMovimientoenapartadoToAttach = em.getReference(movimientoenapartadoListMovimientoenapartadoToAttach.getClass(), movimientoenapartadoListMovimientoenapartadoToAttach.getIdMovimientoApartado());
                attachedMovimientoenapartadoList.add(movimientoenapartadoListMovimientoenapartadoToAttach);
            }
            apartado.setMovimientoenapartadoList(attachedMovimientoenapartadoList);
            List<TallaApartado> attachedTallaapartadoList = new ArrayList<TallaApartado>();
            for (TallaApartado tallaapartadoListTallaapartadoToAttach : apartado.getTallaapartadoList()) {
                tallaapartadoListTallaapartadoToAttach = em.getReference(tallaapartadoListTallaapartadoToAttach.getClass(), tallaapartadoListTallaapartadoToAttach.getIdTallaApartado());
                attachedTallaapartadoList.add(tallaapartadoListTallaapartadoToAttach);
            }
            apartado.setTallaapartadoList(attachedTallaapartadoList);
            em.persist(apartado);
            if (idUsuario != null) {
                idUsuario.getApartadoList().add(apartado);
                idUsuario = em.merge(idUsuario);
            }
            for (MovimientoEnApartado movimientoenapartadoListMovimientoenapartado : apartado.getMovimientoenapartadoList()) {
                Apartado oldIdApartadoOfMovimientoenapartadoListMovimientoenapartado = movimientoenapartadoListMovimientoenapartado.getIdApartado();
                movimientoenapartadoListMovimientoenapartado.setIdApartado(apartado);
                movimientoenapartadoListMovimientoenapartado = em.merge(movimientoenapartadoListMovimientoenapartado);
                if (oldIdApartadoOfMovimientoenapartadoListMovimientoenapartado != null) {
                    oldIdApartadoOfMovimientoenapartadoListMovimientoenapartado.getMovimientoenapartadoList().remove(movimientoenapartadoListMovimientoenapartado);
                    oldIdApartadoOfMovimientoenapartadoListMovimientoenapartado = em.merge(oldIdApartadoOfMovimientoenapartadoListMovimientoenapartado);
                }
            }
            for (TallaApartado tallaapartadoListTallaapartado : apartado.getTallaapartadoList()) {
                Apartado oldIdApartadoOfTallaapartadoListTallaapartado = tallaapartadoListTallaapartado.getIdApartado();
                tallaapartadoListTallaapartado.setIdApartado(apartado);
                tallaapartadoListTallaapartado = em.merge(tallaapartadoListTallaapartado);
                if (oldIdApartadoOfTallaapartadoListTallaapartado != null) {
                    oldIdApartadoOfTallaapartadoListTallaapartado.getTallaapartadoList().remove(tallaapartadoListTallaapartado);
                    oldIdApartadoOfTallaapartadoListTallaapartado = em.merge(oldIdApartadoOfTallaapartadoListTallaapartado);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findApartado(apartado.getIdApartado()) != null) {
                throw new PreexistingEntityException("Apartado " + apartado + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Apartado apartado) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Apartado persistentApartado = em.find(Apartado.class, apartado.getIdApartado());
            Usuario idUsuarioOld = persistentApartado.getIdUsuario();
            Usuario idUsuarioNew = apartado.getIdUsuario();
            List<MovimientoEnApartado> movimientoenapartadoListOld = persistentApartado.getMovimientoenapartadoList();
            List<MovimientoEnApartado> movimientoenapartadoListNew = apartado.getMovimientoenapartadoList();
            List<TallaApartado> tallaapartadoListOld = persistentApartado.getTallaapartadoList();
            List<TallaApartado> tallaapartadoListNew = apartado.getTallaapartadoList();
            List<String> illegalOrphanMessages = null;
            for (MovimientoEnApartado movimientoenapartadoListOldMovimientoenapartado : movimientoenapartadoListOld) {
                if (!movimientoenapartadoListNew.contains(movimientoenapartadoListOldMovimientoenapartado)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Movimientoenapartado " + movimientoenapartadoListOldMovimientoenapartado + " since its idApartado field is not nullable.");
                }
            }
            for (TallaApartado tallaapartadoListOldTallaapartado : tallaapartadoListOld) {
                if (!tallaapartadoListNew.contains(tallaapartadoListOldTallaapartado)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Tallaapartado " + tallaapartadoListOldTallaapartado + " since its idApartado field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getIdUsuario());
                apartado.setIdUsuario(idUsuarioNew);
            }
            List<MovimientoEnApartado> attachedMovimientoenapartadoListNew = new ArrayList<MovimientoEnApartado>();
            for (MovimientoEnApartado movimientoenapartadoListNewMovimientoenapartadoToAttach : movimientoenapartadoListNew) {
                movimientoenapartadoListNewMovimientoenapartadoToAttach = em.getReference(movimientoenapartadoListNewMovimientoenapartadoToAttach.getClass(), movimientoenapartadoListNewMovimientoenapartadoToAttach.getIdMovimientoApartado());
                attachedMovimientoenapartadoListNew.add(movimientoenapartadoListNewMovimientoenapartadoToAttach);
            }
            movimientoenapartadoListNew = attachedMovimientoenapartadoListNew;
            apartado.setMovimientoenapartadoList(movimientoenapartadoListNew);
            List<TallaApartado> attachedTallaapartadoListNew = new ArrayList<TallaApartado>();
            for (TallaApartado tallaapartadoListNewTallaapartadoToAttach : tallaapartadoListNew) {
                tallaapartadoListNewTallaapartadoToAttach = em.getReference(tallaapartadoListNewTallaapartadoToAttach.getClass(), tallaapartadoListNewTallaapartadoToAttach.getIdTallaApartado());
                attachedTallaapartadoListNew.add(tallaapartadoListNewTallaapartadoToAttach);
            }
            tallaapartadoListNew = attachedTallaapartadoListNew;
            apartado.setTallaapartadoList(tallaapartadoListNew);
            apartado = em.merge(apartado);
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getApartadoList().remove(apartado);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getApartadoList().add(apartado);
                idUsuarioNew = em.merge(idUsuarioNew);
            }
            for (MovimientoEnApartado movimientoenapartadoListNewMovimientoenapartado : movimientoenapartadoListNew) {
                if (!movimientoenapartadoListOld.contains(movimientoenapartadoListNewMovimientoenapartado)) {
                    Apartado oldIdApartadoOfMovimientoenapartadoListNewMovimientoenapartado = movimientoenapartadoListNewMovimientoenapartado.getIdApartado();
                    movimientoenapartadoListNewMovimientoenapartado.setIdApartado(apartado);
                    movimientoenapartadoListNewMovimientoenapartado = em.merge(movimientoenapartadoListNewMovimientoenapartado);
                    if (oldIdApartadoOfMovimientoenapartadoListNewMovimientoenapartado != null && !oldIdApartadoOfMovimientoenapartadoListNewMovimientoenapartado.equals(apartado)) {
                        oldIdApartadoOfMovimientoenapartadoListNewMovimientoenapartado.getMovimientoenapartadoList().remove(movimientoenapartadoListNewMovimientoenapartado);
                        oldIdApartadoOfMovimientoenapartadoListNewMovimientoenapartado = em.merge(oldIdApartadoOfMovimientoenapartadoListNewMovimientoenapartado);
                    }
                }
            }
            for (TallaApartado tallaapartadoListNewTallaapartado : tallaapartadoListNew) {
                if (!tallaapartadoListOld.contains(tallaapartadoListNewTallaapartado)) {
                    Apartado oldIdApartadoOfTallaapartadoListNewTallaapartado = tallaapartadoListNewTallaapartado.getIdApartado();
                    tallaapartadoListNewTallaapartado.setIdApartado(apartado);
                    tallaapartadoListNewTallaapartado = em.merge(tallaapartadoListNewTallaapartado);
                    if (oldIdApartadoOfTallaapartadoListNewTallaapartado != null && !oldIdApartadoOfTallaapartadoListNewTallaapartado.equals(apartado)) {
                        oldIdApartadoOfTallaapartadoListNewTallaapartado.getTallaapartadoList().remove(tallaapartadoListNewTallaapartado);
                        oldIdApartadoOfTallaapartadoListNewTallaapartado = em.merge(oldIdApartadoOfTallaapartadoListNewTallaapartado);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = apartado.getIdApartado();
                if (findApartado(id) == null) {
                    throw new NonexistentEntityException("The apartado with id " + id + " no longer exists.");
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
            Apartado apartado;
            try {
                apartado = em.getReference(Apartado.class, id);
                apartado.getIdApartado();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The apartado with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<MovimientoEnApartado> movimientoenapartadoListOrphanCheck = apartado.getMovimientoenapartadoList();
            for (MovimientoEnApartado movimientoenapartadoListOrphanCheckMovimientoenapartado : movimientoenapartadoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Apartado (" + apartado + ") cannot be destroyed since the Movimientoenapartado " + movimientoenapartadoListOrphanCheckMovimientoenapartado + " in its movimientoenapartadoList field has a non-nullable idApartado field.");
            }
            List<TallaApartado> tallaapartadoListOrphanCheck = apartado.getTallaapartadoList();
            for (TallaApartado tallaapartadoListOrphanCheckTallaapartado : tallaapartadoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Apartado (" + apartado + ") cannot be destroyed since the Tallaapartado " + tallaapartadoListOrphanCheckTallaapartado + " in its tallaapartadoList field has a non-nullable idApartado field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Usuario idUsuario = apartado.getIdUsuario();
            if (idUsuario != null) {
                idUsuario.getApartadoList().remove(apartado);
                idUsuario = em.merge(idUsuario);
            }
            em.remove(apartado);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Apartado> findApartadoEntities() {
        return findApartadoEntities(true, -1, -1);
    }

    public List<Apartado> findApartadoEntities(int maxResults, int firstResult) {
        return findApartadoEntities(false, maxResults, firstResult);
    }

    private List<Apartado> findApartadoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Apartado.class));
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

    public Apartado findApartado(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Apartado.class, id);
        } finally {
            em.close();
        }
    }

    public int getApartadoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Apartado> rt = cq.from(Apartado.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
