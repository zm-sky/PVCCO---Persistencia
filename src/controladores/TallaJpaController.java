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
import objetosNegocio.Modelo;
import objetosNegocio.BajaDeInventario;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import objetosNegocio.Talla;
import objetosNegocio.VentaTalla;
import objetosNegocio.TallaApartado;

/**
 *
 * @author zippy
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
        if (talla.getBajaDeInventarioList() == null) {
            talla.setBajaDeInventarioList(new ArrayList<BajaDeInventario>());
        }
        if (talla.getVentaTallaList() == null) {
            talla.setVentaTallaList(new ArrayList<VentaTalla>());
        }
        if (talla.getTallaApartadoList() == null) {
            talla.setTallaApartadoList(new ArrayList<TallaApartado>());
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
            List<BajaDeInventario> attachedBajaDeInventarioList = new ArrayList<BajaDeInventario>();
            for (BajaDeInventario bajaDeInventarioListBajaDeInventarioToAttach : talla.getBajaDeInventarioList()) {
                bajaDeInventarioListBajaDeInventarioToAttach = em.getReference(bajaDeInventarioListBajaDeInventarioToAttach.getClass(), bajaDeInventarioListBajaDeInventarioToAttach.getIdBajaInventario());
                attachedBajaDeInventarioList.add(bajaDeInventarioListBajaDeInventarioToAttach);
            }
            talla.setBajaDeInventarioList(attachedBajaDeInventarioList);
            List<VentaTalla> attachedVentaTallaList = new ArrayList<VentaTalla>();
            for (VentaTalla ventaTallaListVentaTallaToAttach : talla.getVentaTallaList()) {
                ventaTallaListVentaTallaToAttach = em.getReference(ventaTallaListVentaTallaToAttach.getClass(), ventaTallaListVentaTallaToAttach.getIdVentaTalla());
                attachedVentaTallaList.add(ventaTallaListVentaTallaToAttach);
            }
            talla.setVentaTallaList(attachedVentaTallaList);
            List<TallaApartado> attachedTallaApartadoList = new ArrayList<TallaApartado>();
            for (TallaApartado tallaApartadoListTallaApartadoToAttach : talla.getTallaApartadoList()) {
                tallaApartadoListTallaApartadoToAttach = em.getReference(tallaApartadoListTallaApartadoToAttach.getClass(), tallaApartadoListTallaApartadoToAttach.getIdTallaApartado());
                attachedTallaApartadoList.add(tallaApartadoListTallaApartadoToAttach);
            }
            talla.setTallaApartadoList(attachedTallaApartadoList);
            em.persist(talla);
            if (idModelo != null) {
                idModelo.getTallaList().add(talla);
                idModelo = em.merge(idModelo);
            }
            for (BajaDeInventario bajaDeInventarioListBajaDeInventario : talla.getBajaDeInventarioList()) {
                Talla oldIdTallaOfBajaDeInventarioListBajaDeInventario = bajaDeInventarioListBajaDeInventario.getIdTalla();
                bajaDeInventarioListBajaDeInventario.setIdTalla(talla);
                bajaDeInventarioListBajaDeInventario = em.merge(bajaDeInventarioListBajaDeInventario);
                if (oldIdTallaOfBajaDeInventarioListBajaDeInventario != null) {
                    oldIdTallaOfBajaDeInventarioListBajaDeInventario.getBajaDeInventarioList().remove(bajaDeInventarioListBajaDeInventario);
                    oldIdTallaOfBajaDeInventarioListBajaDeInventario = em.merge(oldIdTallaOfBajaDeInventarioListBajaDeInventario);
                }
            }
            for (VentaTalla ventaTallaListVentaTalla : talla.getVentaTallaList()) {
                Talla oldIdTallaOfVentaTallaListVentaTalla = ventaTallaListVentaTalla.getIdTalla();
                ventaTallaListVentaTalla.setIdTalla(talla);
                ventaTallaListVentaTalla = em.merge(ventaTallaListVentaTalla);
                if (oldIdTallaOfVentaTallaListVentaTalla != null) {
                    oldIdTallaOfVentaTallaListVentaTalla.getVentaTallaList().remove(ventaTallaListVentaTalla);
                    oldIdTallaOfVentaTallaListVentaTalla = em.merge(oldIdTallaOfVentaTallaListVentaTalla);
                }
            }
            for (TallaApartado tallaApartadoListTallaApartado : talla.getTallaApartadoList()) {
                Talla oldIdTallaOfTallaApartadoListTallaApartado = tallaApartadoListTallaApartado.getIdTalla();
                tallaApartadoListTallaApartado.setIdTalla(talla);
                tallaApartadoListTallaApartado = em.merge(tallaApartadoListTallaApartado);
                if (oldIdTallaOfTallaApartadoListTallaApartado != null) {
                    oldIdTallaOfTallaApartadoListTallaApartado.getTallaApartadoList().remove(tallaApartadoListTallaApartado);
                    oldIdTallaOfTallaApartadoListTallaApartado = em.merge(oldIdTallaOfTallaApartadoListTallaApartado);
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
            List<BajaDeInventario> bajaDeInventarioListOld = persistentTalla.getBajaDeInventarioList();
            List<BajaDeInventario> bajaDeInventarioListNew = talla.getBajaDeInventarioList();
            List<VentaTalla> ventaTallaListOld = persistentTalla.getVentaTallaList();
            List<VentaTalla> ventaTallaListNew = talla.getVentaTallaList();
            List<TallaApartado> tallaApartadoListOld = persistentTalla.getTallaApartadoList();
            List<TallaApartado> tallaApartadoListNew = talla.getTallaApartadoList();
            List<String> illegalOrphanMessages = null;
            for (BajaDeInventario bajaDeInventarioListOldBajaDeInventario : bajaDeInventarioListOld) {
                if (!bajaDeInventarioListNew.contains(bajaDeInventarioListOldBajaDeInventario)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain BajaDeInventario " + bajaDeInventarioListOldBajaDeInventario + " since its idTalla field is not nullable.");
                }
            }
            for (VentaTalla ventaTallaListOldVentaTalla : ventaTallaListOld) {
                if (!ventaTallaListNew.contains(ventaTallaListOldVentaTalla)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain VentaTalla " + ventaTallaListOldVentaTalla + " since its idTalla field is not nullable.");
                }
            }
            for (TallaApartado tallaApartadoListOldTallaApartado : tallaApartadoListOld) {
                if (!tallaApartadoListNew.contains(tallaApartadoListOldTallaApartado)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain TallaApartado " + tallaApartadoListOldTallaApartado + " since its idTalla field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idModeloNew != null) {
                idModeloNew = em.getReference(idModeloNew.getClass(), idModeloNew.getIdModelo());
                talla.setIdModelo(idModeloNew);
            }
            List<BajaDeInventario> attachedBajaDeInventarioListNew = new ArrayList<BajaDeInventario>();
            for (BajaDeInventario bajaDeInventarioListNewBajaDeInventarioToAttach : bajaDeInventarioListNew) {
                bajaDeInventarioListNewBajaDeInventarioToAttach = em.getReference(bajaDeInventarioListNewBajaDeInventarioToAttach.getClass(), bajaDeInventarioListNewBajaDeInventarioToAttach.getIdBajaInventario());
                attachedBajaDeInventarioListNew.add(bajaDeInventarioListNewBajaDeInventarioToAttach);
            }
            bajaDeInventarioListNew = attachedBajaDeInventarioListNew;
            talla.setBajaDeInventarioList(bajaDeInventarioListNew);
            List<VentaTalla> attachedVentaTallaListNew = new ArrayList<VentaTalla>();
            for (VentaTalla ventaTallaListNewVentaTallaToAttach : ventaTallaListNew) {
                ventaTallaListNewVentaTallaToAttach = em.getReference(ventaTallaListNewVentaTallaToAttach.getClass(), ventaTallaListNewVentaTallaToAttach.getIdVentaTalla());
                attachedVentaTallaListNew.add(ventaTallaListNewVentaTallaToAttach);
            }
            ventaTallaListNew = attachedVentaTallaListNew;
            talla.setVentaTallaList(ventaTallaListNew);
            List<TallaApartado> attachedTallaApartadoListNew = new ArrayList<TallaApartado>();
            for (TallaApartado tallaApartadoListNewTallaApartadoToAttach : tallaApartadoListNew) {
                tallaApartadoListNewTallaApartadoToAttach = em.getReference(tallaApartadoListNewTallaApartadoToAttach.getClass(), tallaApartadoListNewTallaApartadoToAttach.getIdTallaApartado());
                attachedTallaApartadoListNew.add(tallaApartadoListNewTallaApartadoToAttach);
            }
            tallaApartadoListNew = attachedTallaApartadoListNew;
            talla.setTallaApartadoList(tallaApartadoListNew);
            talla = em.merge(talla);
            if (idModeloOld != null && !idModeloOld.equals(idModeloNew)) {
                idModeloOld.getTallaList().remove(talla);
                idModeloOld = em.merge(idModeloOld);
            }
            if (idModeloNew != null && !idModeloNew.equals(idModeloOld)) {
                idModeloNew.getTallaList().add(talla);
                idModeloNew = em.merge(idModeloNew);
            }
            for (BajaDeInventario bajaDeInventarioListNewBajaDeInventario : bajaDeInventarioListNew) {
                if (!bajaDeInventarioListOld.contains(bajaDeInventarioListNewBajaDeInventario)) {
                    Talla oldIdTallaOfBajaDeInventarioListNewBajaDeInventario = bajaDeInventarioListNewBajaDeInventario.getIdTalla();
                    bajaDeInventarioListNewBajaDeInventario.setIdTalla(talla);
                    bajaDeInventarioListNewBajaDeInventario = em.merge(bajaDeInventarioListNewBajaDeInventario);
                    if (oldIdTallaOfBajaDeInventarioListNewBajaDeInventario != null && !oldIdTallaOfBajaDeInventarioListNewBajaDeInventario.equals(talla)) {
                        oldIdTallaOfBajaDeInventarioListNewBajaDeInventario.getBajaDeInventarioList().remove(bajaDeInventarioListNewBajaDeInventario);
                        oldIdTallaOfBajaDeInventarioListNewBajaDeInventario = em.merge(oldIdTallaOfBajaDeInventarioListNewBajaDeInventario);
                    }
                }
            }
            for (VentaTalla ventaTallaListNewVentaTalla : ventaTallaListNew) {
                if (!ventaTallaListOld.contains(ventaTallaListNewVentaTalla)) {
                    Talla oldIdTallaOfVentaTallaListNewVentaTalla = ventaTallaListNewVentaTalla.getIdTalla();
                    ventaTallaListNewVentaTalla.setIdTalla(talla);
                    ventaTallaListNewVentaTalla = em.merge(ventaTallaListNewVentaTalla);
                    if (oldIdTallaOfVentaTallaListNewVentaTalla != null && !oldIdTallaOfVentaTallaListNewVentaTalla.equals(talla)) {
                        oldIdTallaOfVentaTallaListNewVentaTalla.getVentaTallaList().remove(ventaTallaListNewVentaTalla);
                        oldIdTallaOfVentaTallaListNewVentaTalla = em.merge(oldIdTallaOfVentaTallaListNewVentaTalla);
                    }
                }
            }
            for (TallaApartado tallaApartadoListNewTallaApartado : tallaApartadoListNew) {
                if (!tallaApartadoListOld.contains(tallaApartadoListNewTallaApartado)) {
                    Talla oldIdTallaOfTallaApartadoListNewTallaApartado = tallaApartadoListNewTallaApartado.getIdTalla();
                    tallaApartadoListNewTallaApartado.setIdTalla(talla);
                    tallaApartadoListNewTallaApartado = em.merge(tallaApartadoListNewTallaApartado);
                    if (oldIdTallaOfTallaApartadoListNewTallaApartado != null && !oldIdTallaOfTallaApartadoListNewTallaApartado.equals(talla)) {
                        oldIdTallaOfTallaApartadoListNewTallaApartado.getTallaApartadoList().remove(tallaApartadoListNewTallaApartado);
                        oldIdTallaOfTallaApartadoListNewTallaApartado = em.merge(oldIdTallaOfTallaApartadoListNewTallaApartado);
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
            List<BajaDeInventario> bajaDeInventarioListOrphanCheck = talla.getBajaDeInventarioList();
            for (BajaDeInventario bajaDeInventarioListOrphanCheckBajaDeInventario : bajaDeInventarioListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Talla (" + talla + ") cannot be destroyed since the BajaDeInventario " + bajaDeInventarioListOrphanCheckBajaDeInventario + " in its bajaDeInventarioList field has a non-nullable idTalla field.");
            }
            List<VentaTalla> ventaTallaListOrphanCheck = talla.getVentaTallaList();
            for (VentaTalla ventaTallaListOrphanCheckVentaTalla : ventaTallaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Talla (" + talla + ") cannot be destroyed since the VentaTalla " + ventaTallaListOrphanCheckVentaTalla + " in its ventaTallaList field has a non-nullable idTalla field.");
            }
            List<TallaApartado> tallaApartadoListOrphanCheck = talla.getTallaApartadoList();
            for (TallaApartado tallaApartadoListOrphanCheckTallaApartado : tallaApartadoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Talla (" + talla + ") cannot be destroyed since the TallaApartado " + tallaApartadoListOrphanCheckTallaApartado + " in its tallaApartadoList field has a non-nullable idTalla field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Modelo idModelo = talla.getIdModelo();
            if (idModelo != null) {
                idModelo.getTallaList().remove(talla);
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
        TypedQuery<Talla> query = em.createNamedQuery("Talla.findTallaByTalla", Talla.class);
        query.setParameter("talla", talla.getTalla());
        query.setParameter("idModelo", talla.getIdModelo());
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Este metodo regresa las tallas de un modelo en especifico.
     * @param modelo
     * @return tallas de modelo
     */
    public List<Talla> obtenTallasDeModelo(Modelo modelo) {
        try {
            List<Talla> listaTallas = new ArrayList<>();
            EntityManager em = getEntityManager();
            TypedQuery<Talla> query = em.createNamedQuery("Talla.findByModelo", Talla.class);
            query.setParameter("idModelo", modelo);
            listaTallas = query.getResultList();
            return listaTallas;
        } catch (Exception e) {
            return null;
        }
    }    
}
