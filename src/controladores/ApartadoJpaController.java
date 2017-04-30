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
        if (apartado.getMovimientoEnApartadoList() == null) {
            apartado.setMovimientoEnApartadoList(new ArrayList<MovimientoEnApartado>());
        }
        if (apartado.getTallaApartadoList() == null) {
            apartado.setTallaApartadoList(new ArrayList<TallaApartado>());
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
            List<MovimientoEnApartado> attachedMovimientoEnApartadoList = new ArrayList<MovimientoEnApartado>();
            for (MovimientoEnApartado movimientoEnApartadoListMovimientoEnApartadoToAttach : apartado.getMovimientoEnApartadoList()) {
                movimientoEnApartadoListMovimientoEnApartadoToAttach = em.getReference(movimientoEnApartadoListMovimientoEnApartadoToAttach.getClass(), movimientoEnApartadoListMovimientoEnApartadoToAttach.getIdMovimientoApartado());
                attachedMovimientoEnApartadoList.add(movimientoEnApartadoListMovimientoEnApartadoToAttach);
            }
            apartado.setMovimientoEnApartadoList(attachedMovimientoEnApartadoList);
            List<TallaApartado> attachedTallaApartadoList = new ArrayList<TallaApartado>();
            for (TallaApartado tallaApartadoListTallaApartadoToAttach : apartado.getTallaApartadoList()) {
                tallaApartadoListTallaApartadoToAttach = em.getReference(tallaApartadoListTallaApartadoToAttach.getClass(), tallaApartadoListTallaApartadoToAttach.getIdTallaApartado());
                attachedTallaApartadoList.add(tallaApartadoListTallaApartadoToAttach);
            }
            apartado.setTallaApartadoList(attachedTallaApartadoList);
            em.persist(apartado);
            if (idUsuario != null) {
                idUsuario.getApartadoList().add(apartado);
                idUsuario = em.merge(idUsuario);
            }
            for (MovimientoEnApartado movimientoEnApartadoListMovimientoEnApartado : apartado.getMovimientoEnApartadoList()) {
                Apartado oldIdApartadoOfMovimientoEnApartadoListMovimientoEnApartado = movimientoEnApartadoListMovimientoEnApartado.getIdApartado();
                movimientoEnApartadoListMovimientoEnApartado.setIdApartado(apartado);
                movimientoEnApartadoListMovimientoEnApartado = em.merge(movimientoEnApartadoListMovimientoEnApartado);
                if (oldIdApartadoOfMovimientoEnApartadoListMovimientoEnApartado != null) {
                    oldIdApartadoOfMovimientoEnApartadoListMovimientoEnApartado.getMovimientoEnApartadoList().remove(movimientoEnApartadoListMovimientoEnApartado);
                    oldIdApartadoOfMovimientoEnApartadoListMovimientoEnApartado = em.merge(oldIdApartadoOfMovimientoEnApartadoListMovimientoEnApartado);
                }
            }
            for (TallaApartado tallaApartadoListTallaApartado : apartado.getTallaApartadoList()) {
                Apartado oldIdApartadoOfTallaApartadoListTallaApartado = tallaApartadoListTallaApartado.getIdApartado();
                tallaApartadoListTallaApartado.setIdApartado(apartado);
                tallaApartadoListTallaApartado = em.merge(tallaApartadoListTallaApartado);
                if (oldIdApartadoOfTallaApartadoListTallaApartado != null) {
                    oldIdApartadoOfTallaApartadoListTallaApartado.getTallaApartadoList().remove(tallaApartadoListTallaApartado);
                    oldIdApartadoOfTallaApartadoListTallaApartado = em.merge(oldIdApartadoOfTallaApartadoListTallaApartado);
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
            List<MovimientoEnApartado> movimientoEnApartadoListOld = persistentApartado.getMovimientoEnApartadoList();
            List<MovimientoEnApartado> movimientoEnApartadoListNew = apartado.getMovimientoEnApartadoList();
            List<TallaApartado> tallaApartadoListOld = persistentApartado.getTallaApartadoList();
            List<TallaApartado> tallaApartadoListNew = apartado.getTallaApartadoList();
            List<String> illegalOrphanMessages = null;
            for (MovimientoEnApartado movimientoEnApartadoListOldMovimientoEnApartado : movimientoEnApartadoListOld) {
                if (!movimientoEnApartadoListNew.contains(movimientoEnApartadoListOldMovimientoEnApartado)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain MovimientoEnApartado " + movimientoEnApartadoListOldMovimientoEnApartado + " since its idApartado field is not nullable.");
                }
            }
            for (TallaApartado tallaApartadoListOldTallaApartado : tallaApartadoListOld) {
                if (!tallaApartadoListNew.contains(tallaApartadoListOldTallaApartado)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain TallaApartado " + tallaApartadoListOldTallaApartado + " since its idApartado field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getIdUsuario());
                apartado.setIdUsuario(idUsuarioNew);
            }
            List<MovimientoEnApartado> attachedMovimientoEnApartadoListNew = new ArrayList<MovimientoEnApartado>();
            for (MovimientoEnApartado movimientoEnApartadoListNewMovimientoEnApartadoToAttach : movimientoEnApartadoListNew) {
                movimientoEnApartadoListNewMovimientoEnApartadoToAttach = em.getReference(movimientoEnApartadoListNewMovimientoEnApartadoToAttach.getClass(), movimientoEnApartadoListNewMovimientoEnApartadoToAttach.getIdMovimientoApartado());
                attachedMovimientoEnApartadoListNew.add(movimientoEnApartadoListNewMovimientoEnApartadoToAttach);
            }
            movimientoEnApartadoListNew = attachedMovimientoEnApartadoListNew;
            apartado.setMovimientoEnApartadoList(movimientoEnApartadoListNew);
            List<TallaApartado> attachedTallaApartadoListNew = new ArrayList<TallaApartado>();
            for (TallaApartado tallaApartadoListNewTallaApartadoToAttach : tallaApartadoListNew) {
                tallaApartadoListNewTallaApartadoToAttach = em.getReference(tallaApartadoListNewTallaApartadoToAttach.getClass(), tallaApartadoListNewTallaApartadoToAttach.getIdTallaApartado());
                attachedTallaApartadoListNew.add(tallaApartadoListNewTallaApartadoToAttach);
            }
            tallaApartadoListNew = attachedTallaApartadoListNew;
            apartado.setTallaApartadoList(tallaApartadoListNew);
            apartado = em.merge(apartado);
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getApartadoList().remove(apartado);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getApartadoList().add(apartado);
                idUsuarioNew = em.merge(idUsuarioNew);
            }
            for (MovimientoEnApartado movimientoEnApartadoListNewMovimientoEnApartado : movimientoEnApartadoListNew) {
                if (!movimientoEnApartadoListOld.contains(movimientoEnApartadoListNewMovimientoEnApartado)) {
                    Apartado oldIdApartadoOfMovimientoEnApartadoListNewMovimientoEnApartado = movimientoEnApartadoListNewMovimientoEnApartado.getIdApartado();
                    movimientoEnApartadoListNewMovimientoEnApartado.setIdApartado(apartado);
                    movimientoEnApartadoListNewMovimientoEnApartado = em.merge(movimientoEnApartadoListNewMovimientoEnApartado);
                    if (oldIdApartadoOfMovimientoEnApartadoListNewMovimientoEnApartado != null && !oldIdApartadoOfMovimientoEnApartadoListNewMovimientoEnApartado.equals(apartado)) {
                        oldIdApartadoOfMovimientoEnApartadoListNewMovimientoEnApartado.getMovimientoEnApartadoList().remove(movimientoEnApartadoListNewMovimientoEnApartado);
                        oldIdApartadoOfMovimientoEnApartadoListNewMovimientoEnApartado = em.merge(oldIdApartadoOfMovimientoEnApartadoListNewMovimientoEnApartado);
                    }
                }
            }
            for (TallaApartado tallaApartadoListNewTallaApartado : tallaApartadoListNew) {
                if (!tallaApartadoListOld.contains(tallaApartadoListNewTallaApartado)) {
                    Apartado oldIdApartadoOfTallaApartadoListNewTallaApartado = tallaApartadoListNewTallaApartado.getIdApartado();
                    tallaApartadoListNewTallaApartado.setIdApartado(apartado);
                    tallaApartadoListNewTallaApartado = em.merge(tallaApartadoListNewTallaApartado);
                    if (oldIdApartadoOfTallaApartadoListNewTallaApartado != null && !oldIdApartadoOfTallaApartadoListNewTallaApartado.equals(apartado)) {
                        oldIdApartadoOfTallaApartadoListNewTallaApartado.getTallaApartadoList().remove(tallaApartadoListNewTallaApartado);
                        oldIdApartadoOfTallaApartadoListNewTallaApartado = em.merge(oldIdApartadoOfTallaApartadoListNewTallaApartado);
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
            List<MovimientoEnApartado> movimientoEnApartadoListOrphanCheck = apartado.getMovimientoEnApartadoList();
            for (MovimientoEnApartado movimientoEnApartadoListOrphanCheckMovimientoEnApartado : movimientoEnApartadoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Apartado (" + apartado + ") cannot be destroyed since the MovimientoEnApartado " + movimientoEnApartadoListOrphanCheckMovimientoEnApartado + " in its movimientoEnApartadoList field has a non-nullable idApartado field.");
            }
            List<TallaApartado> tallaApartadoListOrphanCheck = apartado.getTallaApartadoList();
            for (TallaApartado tallaApartadoListOrphanCheckTallaApartado : tallaApartadoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Apartado (" + apartado + ") cannot be destroyed since the TallaApartado " + tallaApartadoListOrphanCheckTallaApartado + " in its tallaApartadoList field has a non-nullable idApartado field.");
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
