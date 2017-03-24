package controladores;

import controladores.exceptions.IllegalOrphanException;
import controladores.exceptions.NonexistentEntityException;
import controladores.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import objetosNegocio.Modelo;
import objetosNegocio.BajaDeInventario;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import objetosNegocio.Talla;
import objetosNegocio.VentaTalla;
import objetosNegocio.TallaApartado;

/**
 *
 * @author Raul Karim Sabag Ballesteros
 */
public class TallaJpaController implements Serializable {

    public TallaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Talla talla) throws PreexistingEntityException, Exception {
        if (talla.getBajaDeInventarioCollection() == null) {
            talla.setBajaDeInventarioCollection(new ArrayList<BajaDeInventario>());
        }
        if (talla.getVentaTallaCollection() == null) {
            talla.setVentaTallaCollection(new ArrayList<VentaTalla>());
        }
        if (talla.getTallaApartadoCollection() == null) {
            talla.setTallaApartadoCollection(new ArrayList<TallaApartado>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Modelo idModelo = talla.getIdModelo();
            if (idModelo != null) {
                idModelo = em.getReference(idModelo.getClass(), idModelo.getIdModelo());
                talla.setIdModelo(idModelo);
            }
            Collection<BajaDeInventario> attachedBajaDeInventarioCollection = new ArrayList<BajaDeInventario>();
            for (BajaDeInventario bajaDeInventarioCollectionBajaDeInventarioToAttach : talla.getBajaDeInventarioCollection()) {
                bajaDeInventarioCollectionBajaDeInventarioToAttach = em.getReference(bajaDeInventarioCollectionBajaDeInventarioToAttach.getClass(), bajaDeInventarioCollectionBajaDeInventarioToAttach.getIdBajaInventario());
                attachedBajaDeInventarioCollection.add(bajaDeInventarioCollectionBajaDeInventarioToAttach);
            }
            talla.setBajaDeInventarioCollection(attachedBajaDeInventarioCollection);
            Collection<VentaTalla> attachedVentaTallaCollection = new ArrayList<VentaTalla>();
            for (VentaTalla ventaTallaCollectionVentaTallaToAttach : talla.getVentaTallaCollection()) {
                ventaTallaCollectionVentaTallaToAttach = em.getReference(ventaTallaCollectionVentaTallaToAttach.getClass(), ventaTallaCollectionVentaTallaToAttach.getIdVentaTalla());
                attachedVentaTallaCollection.add(ventaTallaCollectionVentaTallaToAttach);
            }
            talla.setVentaTallaCollection(attachedVentaTallaCollection);
            Collection<TallaApartado> attachedTallaApartadoCollection = new ArrayList<TallaApartado>();
            for (TallaApartado tallaApartadoCollectionTallaApartadoToAttach : talla.getTallaApartadoCollection()) {
                tallaApartadoCollectionTallaApartadoToAttach = em.getReference(tallaApartadoCollectionTallaApartadoToAttach.getClass(), tallaApartadoCollectionTallaApartadoToAttach.getIdTallaApartado());
                attachedTallaApartadoCollection.add(tallaApartadoCollectionTallaApartadoToAttach);
            }
            talla.setTallaApartadoCollection(attachedTallaApartadoCollection);
            em.persist(talla);
            if (idModelo != null) {
                idModelo.getTallaCollection().add(talla);
                idModelo = em.merge(idModelo);
            }
            for (BajaDeInventario bajaDeInventarioCollectionBajaDeInventario : talla.getBajaDeInventarioCollection()) {
                Talla oldIdTallaOfBajaDeInventarioCollectionBajaDeInventario = bajaDeInventarioCollectionBajaDeInventario.getIdTalla();
                bajaDeInventarioCollectionBajaDeInventario.setIdTalla(talla);
                bajaDeInventarioCollectionBajaDeInventario = em.merge(bajaDeInventarioCollectionBajaDeInventario);
                if (oldIdTallaOfBajaDeInventarioCollectionBajaDeInventario != null) {
                    oldIdTallaOfBajaDeInventarioCollectionBajaDeInventario.getBajaDeInventarioCollection().remove(bajaDeInventarioCollectionBajaDeInventario);
                    oldIdTallaOfBajaDeInventarioCollectionBajaDeInventario = em.merge(oldIdTallaOfBajaDeInventarioCollectionBajaDeInventario);
                }
            }
            for (VentaTalla ventaTallaCollectionVentaTalla : talla.getVentaTallaCollection()) {
                Talla oldIdTallaOfVentaTallaCollectionVentaTalla = ventaTallaCollectionVentaTalla.getIdTalla();
                ventaTallaCollectionVentaTalla.setIdTalla(talla);
                ventaTallaCollectionVentaTalla = em.merge(ventaTallaCollectionVentaTalla);
                if (oldIdTallaOfVentaTallaCollectionVentaTalla != null) {
                    oldIdTallaOfVentaTallaCollectionVentaTalla.getVentaTallaCollection().remove(ventaTallaCollectionVentaTalla);
                    oldIdTallaOfVentaTallaCollectionVentaTalla = em.merge(oldIdTallaOfVentaTallaCollectionVentaTalla);
                }
            }
            for (TallaApartado tallaApartadoCollectionTallaApartado : talla.getTallaApartadoCollection()) {
                Talla oldIdTallaOfTallaApartadoCollectionTallaApartado = tallaApartadoCollectionTallaApartado.getIdTalla();
                tallaApartadoCollectionTallaApartado.setIdTalla(talla);
                tallaApartadoCollectionTallaApartado = em.merge(tallaApartadoCollectionTallaApartado);
                if (oldIdTallaOfTallaApartadoCollectionTallaApartado != null) {
                    oldIdTallaOfTallaApartadoCollectionTallaApartado.getTallaApartadoCollection().remove(tallaApartadoCollectionTallaApartado);
                    oldIdTallaOfTallaApartadoCollectionTallaApartado = em.merge(oldIdTallaOfTallaApartadoCollectionTallaApartado);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTalla(talla.getIdTalla()) != null) {
                throw new PreexistingEntityException("Talla " + talla + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Talla talla) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Talla persistentTalla = em.find(Talla.class, talla.getIdTalla());
            Modelo idModeloOld = persistentTalla.getIdModelo();
            Modelo idModeloNew = talla.getIdModelo();
            Collection<BajaDeInventario> bajaDeInventarioCollectionOld = persistentTalla.getBajaDeInventarioCollection();
            Collection<BajaDeInventario> bajaDeInventarioCollectionNew = talla.getBajaDeInventarioCollection();
            Collection<VentaTalla> ventaTallaCollectionOld = persistentTalla.getVentaTallaCollection();
            Collection<VentaTalla> ventaTallaCollectionNew = talla.getVentaTallaCollection();
            Collection<TallaApartado> tallaApartadoCollectionOld = persistentTalla.getTallaApartadoCollection();
            Collection<TallaApartado> tallaApartadoCollectionNew = talla.getTallaApartadoCollection();
            List<String> illegalOrphanMessages = null;
            for (BajaDeInventario bajaDeInventarioCollectionOldBajaDeInventario : bajaDeInventarioCollectionOld) {
                if (!bajaDeInventarioCollectionNew.contains(bajaDeInventarioCollectionOldBajaDeInventario)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain BajaDeInventario " + bajaDeInventarioCollectionOldBajaDeInventario + " since its idTalla field is not nullable.");
                }
            }
            for (VentaTalla ventaTallaCollectionOldVentaTalla : ventaTallaCollectionOld) {
                if (!ventaTallaCollectionNew.contains(ventaTallaCollectionOldVentaTalla)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain VentaTalla " + ventaTallaCollectionOldVentaTalla + " since its idTalla field is not nullable.");
                }
            }
            for (TallaApartado tallaApartadoCollectionOldTallaApartado : tallaApartadoCollectionOld) {
                if (!tallaApartadoCollectionNew.contains(tallaApartadoCollectionOldTallaApartado)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain TallaApartado " + tallaApartadoCollectionOldTallaApartado + " since its idTalla field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idModeloNew != null) {
                idModeloNew = em.getReference(idModeloNew.getClass(), idModeloNew.getIdModelo());
                talla.setIdModelo(idModeloNew);
            }
            Collection<BajaDeInventario> attachedBajaDeInventarioCollectionNew = new ArrayList<BajaDeInventario>();
            for (BajaDeInventario bajaDeInventarioCollectionNewBajaDeInventarioToAttach : bajaDeInventarioCollectionNew) {
                bajaDeInventarioCollectionNewBajaDeInventarioToAttach = em.getReference(bajaDeInventarioCollectionNewBajaDeInventarioToAttach.getClass(), bajaDeInventarioCollectionNewBajaDeInventarioToAttach.getIdBajaInventario());
                attachedBajaDeInventarioCollectionNew.add(bajaDeInventarioCollectionNewBajaDeInventarioToAttach);
            }
            bajaDeInventarioCollectionNew = attachedBajaDeInventarioCollectionNew;
            talla.setBajaDeInventarioCollection(bajaDeInventarioCollectionNew);
            Collection<VentaTalla> attachedVentaTallaCollectionNew = new ArrayList<VentaTalla>();
            for (VentaTalla ventaTallaCollectionNewVentaTallaToAttach : ventaTallaCollectionNew) {
                ventaTallaCollectionNewVentaTallaToAttach = em.getReference(ventaTallaCollectionNewVentaTallaToAttach.getClass(), ventaTallaCollectionNewVentaTallaToAttach.getIdVentaTalla());
                attachedVentaTallaCollectionNew.add(ventaTallaCollectionNewVentaTallaToAttach);
            }
            ventaTallaCollectionNew = attachedVentaTallaCollectionNew;
            talla.setVentaTallaCollection(ventaTallaCollectionNew);
            Collection<TallaApartado> attachedTallaApartadoCollectionNew = new ArrayList<TallaApartado>();
            for (TallaApartado tallaApartadoCollectionNewTallaApartadoToAttach : tallaApartadoCollectionNew) {
                tallaApartadoCollectionNewTallaApartadoToAttach = em.getReference(tallaApartadoCollectionNewTallaApartadoToAttach.getClass(), tallaApartadoCollectionNewTallaApartadoToAttach.getIdTallaApartado());
                attachedTallaApartadoCollectionNew.add(tallaApartadoCollectionNewTallaApartadoToAttach);
            }
            tallaApartadoCollectionNew = attachedTallaApartadoCollectionNew;
            talla.setTallaApartadoCollection(tallaApartadoCollectionNew);
            talla = em.merge(talla);
            if (idModeloOld != null && !idModeloOld.equals(idModeloNew)) {
                idModeloOld.getTallaCollection().remove(talla);
                idModeloOld = em.merge(idModeloOld);
            }
            if (idModeloNew != null && !idModeloNew.equals(idModeloOld)) {
                idModeloNew.getTallaCollection().add(talla);
                idModeloNew = em.merge(idModeloNew);
            }
            for (BajaDeInventario bajaDeInventarioCollectionNewBajaDeInventario : bajaDeInventarioCollectionNew) {
                if (!bajaDeInventarioCollectionOld.contains(bajaDeInventarioCollectionNewBajaDeInventario)) {
                    Talla oldIdTallaOfBajaDeInventarioCollectionNewBajaDeInventario = bajaDeInventarioCollectionNewBajaDeInventario.getIdTalla();
                    bajaDeInventarioCollectionNewBajaDeInventario.setIdTalla(talla);
                    bajaDeInventarioCollectionNewBajaDeInventario = em.merge(bajaDeInventarioCollectionNewBajaDeInventario);
                    if (oldIdTallaOfBajaDeInventarioCollectionNewBajaDeInventario != null && !oldIdTallaOfBajaDeInventarioCollectionNewBajaDeInventario.equals(talla)) {
                        oldIdTallaOfBajaDeInventarioCollectionNewBajaDeInventario.getBajaDeInventarioCollection().remove(bajaDeInventarioCollectionNewBajaDeInventario);
                        oldIdTallaOfBajaDeInventarioCollectionNewBajaDeInventario = em.merge(oldIdTallaOfBajaDeInventarioCollectionNewBajaDeInventario);
                    }
                }
            }
            for (VentaTalla ventaTallaCollectionNewVentaTalla : ventaTallaCollectionNew) {
                if (!ventaTallaCollectionOld.contains(ventaTallaCollectionNewVentaTalla)) {
                    Talla oldIdTallaOfVentaTallaCollectionNewVentaTalla = ventaTallaCollectionNewVentaTalla.getIdTalla();
                    ventaTallaCollectionNewVentaTalla.setIdTalla(talla);
                    ventaTallaCollectionNewVentaTalla = em.merge(ventaTallaCollectionNewVentaTalla);
                    if (oldIdTallaOfVentaTallaCollectionNewVentaTalla != null && !oldIdTallaOfVentaTallaCollectionNewVentaTalla.equals(talla)) {
                        oldIdTallaOfVentaTallaCollectionNewVentaTalla.getVentaTallaCollection().remove(ventaTallaCollectionNewVentaTalla);
                        oldIdTallaOfVentaTallaCollectionNewVentaTalla = em.merge(oldIdTallaOfVentaTallaCollectionNewVentaTalla);
                    }
                }
            }
            for (TallaApartado tallaApartadoCollectionNewTallaApartado : tallaApartadoCollectionNew) {
                if (!tallaApartadoCollectionOld.contains(tallaApartadoCollectionNewTallaApartado)) {
                    Talla oldIdTallaOfTallaApartadoCollectionNewTallaApartado = tallaApartadoCollectionNewTallaApartado.getIdTalla();
                    tallaApartadoCollectionNewTallaApartado.setIdTalla(talla);
                    tallaApartadoCollectionNewTallaApartado = em.merge(tallaApartadoCollectionNewTallaApartado);
                    if (oldIdTallaOfTallaApartadoCollectionNewTallaApartado != null && !oldIdTallaOfTallaApartadoCollectionNewTallaApartado.equals(talla)) {
                        oldIdTallaOfTallaApartadoCollectionNewTallaApartado.getTallaApartadoCollection().remove(tallaApartadoCollectionNewTallaApartado);
                        oldIdTallaOfTallaApartadoCollectionNewTallaApartado = em.merge(oldIdTallaOfTallaApartadoCollectionNewTallaApartado);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = talla.getIdTalla();
                if (findTalla(id) == null) {
                    throw new NonexistentEntityException("The talla with id " + id + " no longer exists.");
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
            Talla talla;
            try {
                talla = em.getReference(Talla.class, id);
                talla.getIdTalla();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The talla with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<BajaDeInventario> bajaDeInventarioCollectionOrphanCheck = talla.getBajaDeInventarioCollection();
            for (BajaDeInventario bajaDeInventarioCollectionOrphanCheckBajaDeInventario : bajaDeInventarioCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Talla (" + talla + ") cannot be destroyed since the BajaDeInventario " + bajaDeInventarioCollectionOrphanCheckBajaDeInventario + " in its bajaDeInventarioCollection field has a non-nullable idTalla field.");
            }
            Collection<VentaTalla> ventaTallaCollectionOrphanCheck = talla.getVentaTallaCollection();
            for (VentaTalla ventaTallaCollectionOrphanCheckVentaTalla : ventaTallaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Talla (" + talla + ") cannot be destroyed since the VentaTalla " + ventaTallaCollectionOrphanCheckVentaTalla + " in its ventaTallaCollection field has a non-nullable idTalla field.");
            }
            Collection<TallaApartado> tallaApartadoCollectionOrphanCheck = talla.getTallaApartadoCollection();
            for (TallaApartado tallaApartadoCollectionOrphanCheckTallaApartado : tallaApartadoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Talla (" + talla + ") cannot be destroyed since the TallaApartado " + tallaApartadoCollectionOrphanCheckTallaApartado + " in its tallaApartadoCollection field has a non-nullable idTalla field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Modelo idModelo = talla.getIdModelo();
            if (idModelo != null) {
                idModelo.getTallaCollection().remove(talla);
                idModelo = em.merge(idModelo);
            }
            em.remove(talla);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Talla> findTallaEntities() {
        return findTallaEntities(true, -1, -1);
    }

    public List<Talla> findTallaEntities(int maxResults, int firstResult) {
        return findTallaEntities(false, maxResults, firstResult);
    }

    private List<Talla> findTallaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Talla.class));
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

    public Talla findTalla(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Talla.class, id);
        } finally {
            em.close();
        }
    }

    public int getTallaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Talla> rt = cq.from(Talla.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    /**
     * Este metodo recibe una talla, toma el atributo talla del objeto y lo
     * compara contra las tallas de la base de datos. Si una talla es igual,
     * toma la talla de la base de datos y la regresa.
     *
     * @param talla
     * @param modelo
     * @return Talla de Base de datos
     */
    public Talla obtenTallaPorTalla(Talla talla) {
        EntityManager em = getEntityManager();
        TypedQuery<Talla> query = em.createNamedQuery("Talla.findByTalla", Talla.class);
        query.setParameter("talla", talla.getTalla());
        query.setParameter("idModelo",talla.getIdModelo());
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

}
