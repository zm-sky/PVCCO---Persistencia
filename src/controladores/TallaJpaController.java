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
        if (talla.getBajadeinventarioList() == null) {
            talla.setBajadeinventarioList(new ArrayList<BajaDeInventario>());
        }
        if (talla.getVentatallaList() == null) {
            talla.setVentatallaList(new ArrayList<VentaTalla>());
        }
        if (talla.getTallaapartadoList() == null) {
            talla.setTallaapartadoList(new ArrayList<TallaApartado>());
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
            List<BajaDeInventario> attachedBajadeinventarioList = new ArrayList<BajaDeInventario>();
            for (BajaDeInventario bajadeinventarioListBajadeinventarioToAttach : talla.getBajadeinventarioList()) {
                bajadeinventarioListBajadeinventarioToAttach = em.getReference(bajadeinventarioListBajadeinventarioToAttach.getClass(), bajadeinventarioListBajadeinventarioToAttach.getIdBajaInventario());
                attachedBajadeinventarioList.add(bajadeinventarioListBajadeinventarioToAttach);
            }
            talla.setBajadeinventarioList(attachedBajadeinventarioList);
            List<VentaTalla> attachedVentatallaList = new ArrayList<VentaTalla>();
            for (VentaTalla ventatallaListVentatallaToAttach : talla.getVentatallaList()) {
                ventatallaListVentatallaToAttach = em.getReference(ventatallaListVentatallaToAttach.getClass(), ventatallaListVentatallaToAttach.getIdVentaTalla());
                attachedVentatallaList.add(ventatallaListVentatallaToAttach);
            }
            talla.setVentatallaList(attachedVentatallaList);
            List<TallaApartado> attachedTallaapartadoList = new ArrayList<TallaApartado>();
            for (TallaApartado tallaapartadoListTallaapartadoToAttach : talla.getTallaapartadoList()) {
                tallaapartadoListTallaapartadoToAttach = em.getReference(tallaapartadoListTallaapartadoToAttach.getClass(), tallaapartadoListTallaapartadoToAttach.getIdTallaApartado());
                attachedTallaapartadoList.add(tallaapartadoListTallaapartadoToAttach);
            }
            talla.setTallaapartadoList(attachedTallaapartadoList);
            em.persist(talla);
            if (idModelo != null) {
                idModelo.getTallaList().add(talla);
                idModelo = em.merge(idModelo);
            }
            for (BajaDeInventario bajadeinventarioListBajadeinventario : talla.getBajadeinventarioList()) {
                Talla oldIdTallaOfBajadeinventarioListBajadeinventario = bajadeinventarioListBajadeinventario.getIdTalla();
                bajadeinventarioListBajadeinventario.setIdTalla(talla);
                bajadeinventarioListBajadeinventario = em.merge(bajadeinventarioListBajadeinventario);
                if (oldIdTallaOfBajadeinventarioListBajadeinventario != null) {
                    oldIdTallaOfBajadeinventarioListBajadeinventario.getBajadeinventarioList().remove(bajadeinventarioListBajadeinventario);
                    oldIdTallaOfBajadeinventarioListBajadeinventario = em.merge(oldIdTallaOfBajadeinventarioListBajadeinventario);
                }
            }
            for (VentaTalla ventatallaListVentatalla : talla.getVentatallaList()) {
                Talla oldIdTallaOfVentatallaListVentatalla = ventatallaListVentatalla.getIdTalla();
                ventatallaListVentatalla.setIdTalla(talla);
                ventatallaListVentatalla = em.merge(ventatallaListVentatalla);
                if (oldIdTallaOfVentatallaListVentatalla != null) {
                    oldIdTallaOfVentatallaListVentatalla.getVentatallaList().remove(ventatallaListVentatalla);
                    oldIdTallaOfVentatallaListVentatalla = em.merge(oldIdTallaOfVentatallaListVentatalla);
                }
            }
            for (TallaApartado tallaapartadoListTallaapartado : talla.getTallaapartadoList()) {
                Talla oldIdTallaOfTallaapartadoListTallaapartado = tallaapartadoListTallaapartado.getIdTalla();
                tallaapartadoListTallaapartado.setIdTalla(talla);
                tallaapartadoListTallaapartado = em.merge(tallaapartadoListTallaapartado);
                if (oldIdTallaOfTallaapartadoListTallaapartado != null) {
                    oldIdTallaOfTallaapartadoListTallaapartado.getTallaapartadoList().remove(tallaapartadoListTallaapartado);
                    oldIdTallaOfTallaapartadoListTallaapartado = em.merge(oldIdTallaOfTallaapartadoListTallaapartado);
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
            List<BajaDeInventario> bajadeinventarioListOld = persistentTalla.getBajadeinventarioList();
            List<BajaDeInventario> bajadeinventarioListNew = talla.getBajadeinventarioList();
            List<VentaTalla> ventatallaListOld = persistentTalla.getVentatallaList();
            List<VentaTalla> ventatallaListNew = talla.getVentatallaList();
            List<TallaApartado> tallaapartadoListOld = persistentTalla.getTallaapartadoList();
            List<TallaApartado> tallaapartadoListNew = talla.getTallaapartadoList();
            List<String> illegalOrphanMessages = null;
            for (BajaDeInventario bajadeinventarioListOldBajadeinventario : bajadeinventarioListOld) {
                if (!bajadeinventarioListNew.contains(bajadeinventarioListOldBajadeinventario)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Bajadeinventario " + bajadeinventarioListOldBajadeinventario + " since its idTalla field is not nullable.");
                }
            }
            for (VentaTalla ventatallaListOldVentatalla : ventatallaListOld) {
                if (!ventatallaListNew.contains(ventatallaListOldVentatalla)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ventatalla " + ventatallaListOldVentatalla + " since its idTalla field is not nullable.");
                }
            }
            for (TallaApartado tallaapartadoListOldTallaapartado : tallaapartadoListOld) {
                if (!tallaapartadoListNew.contains(tallaapartadoListOldTallaapartado)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Tallaapartado " + tallaapartadoListOldTallaapartado + " since its idTalla field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idModeloNew != null) {
                idModeloNew = em.getReference(idModeloNew.getClass(), idModeloNew.getIdModelo());
                talla.setIdModelo(idModeloNew);
            }
            List<BajaDeInventario> attachedBajadeinventarioListNew = new ArrayList<BajaDeInventario>();
            for (BajaDeInventario bajadeinventarioListNewBajadeinventarioToAttach : bajadeinventarioListNew) {
                bajadeinventarioListNewBajadeinventarioToAttach = em.getReference(bajadeinventarioListNewBajadeinventarioToAttach.getClass(), bajadeinventarioListNewBajadeinventarioToAttach.getIdBajaInventario());
                attachedBajadeinventarioListNew.add(bajadeinventarioListNewBajadeinventarioToAttach);
            }
            bajadeinventarioListNew = attachedBajadeinventarioListNew;
            talla.setBajadeinventarioList(bajadeinventarioListNew);
            List<VentaTalla> attachedVentatallaListNew = new ArrayList<VentaTalla>();
            for (VentaTalla ventatallaListNewVentatallaToAttach : ventatallaListNew) {
                ventatallaListNewVentatallaToAttach = em.getReference(ventatallaListNewVentatallaToAttach.getClass(), ventatallaListNewVentatallaToAttach.getIdVentaTalla());
                attachedVentatallaListNew.add(ventatallaListNewVentatallaToAttach);
            }
            ventatallaListNew = attachedVentatallaListNew;
            talla.setVentatallaList(ventatallaListNew);
            List<TallaApartado> attachedTallaapartadoListNew = new ArrayList<TallaApartado>();
            for (TallaApartado tallaapartadoListNewTallaapartadoToAttach : tallaapartadoListNew) {
                tallaapartadoListNewTallaapartadoToAttach = em.getReference(tallaapartadoListNewTallaapartadoToAttach.getClass(), tallaapartadoListNewTallaapartadoToAttach.getIdTallaApartado());
                attachedTallaapartadoListNew.add(tallaapartadoListNewTallaapartadoToAttach);
            }
            tallaapartadoListNew = attachedTallaapartadoListNew;
            talla.setTallaapartadoList(tallaapartadoListNew);
            talla = em.merge(talla);
            if (idModeloOld != null && !idModeloOld.equals(idModeloNew)) {
                idModeloOld.getTallaList().remove(talla);
                idModeloOld = em.merge(idModeloOld);
            }
            if (idModeloNew != null && !idModeloNew.equals(idModeloOld)) {
                idModeloNew.getTallaList().add(talla);
                idModeloNew = em.merge(idModeloNew);
            }
            for (BajaDeInventario bajadeinventarioListNewBajadeinventario : bajadeinventarioListNew) {
                if (!bajadeinventarioListOld.contains(bajadeinventarioListNewBajadeinventario)) {
                    Talla oldIdTallaOfBajadeinventarioListNewBajadeinventario = bajadeinventarioListNewBajadeinventario.getIdTalla();
                    bajadeinventarioListNewBajadeinventario.setIdTalla(talla);
                    bajadeinventarioListNewBajadeinventario = em.merge(bajadeinventarioListNewBajadeinventario);
                    if (oldIdTallaOfBajadeinventarioListNewBajadeinventario != null && !oldIdTallaOfBajadeinventarioListNewBajadeinventario.equals(talla)) {
                        oldIdTallaOfBajadeinventarioListNewBajadeinventario.getBajadeinventarioList().remove(bajadeinventarioListNewBajadeinventario);
                        oldIdTallaOfBajadeinventarioListNewBajadeinventario = em.merge(oldIdTallaOfBajadeinventarioListNewBajadeinventario);
                    }
                }
            }
            for (VentaTalla ventatallaListNewVentatalla : ventatallaListNew) {
                if (!ventatallaListOld.contains(ventatallaListNewVentatalla)) {
                    Talla oldIdTallaOfVentatallaListNewVentatalla = ventatallaListNewVentatalla.getIdTalla();
                    ventatallaListNewVentatalla.setIdTalla(talla);
                    ventatallaListNewVentatalla = em.merge(ventatallaListNewVentatalla);
                    if (oldIdTallaOfVentatallaListNewVentatalla != null && !oldIdTallaOfVentatallaListNewVentatalla.equals(talla)) {
                        oldIdTallaOfVentatallaListNewVentatalla.getVentatallaList().remove(ventatallaListNewVentatalla);
                        oldIdTallaOfVentatallaListNewVentatalla = em.merge(oldIdTallaOfVentatallaListNewVentatalla);
                    }
                }
            }
            for (TallaApartado tallaapartadoListNewTallaapartado : tallaapartadoListNew) {
                if (!tallaapartadoListOld.contains(tallaapartadoListNewTallaapartado)) {
                    Talla oldIdTallaOfTallaapartadoListNewTallaapartado = tallaapartadoListNewTallaapartado.getIdTalla();
                    tallaapartadoListNewTallaapartado.setIdTalla(talla);
                    tallaapartadoListNewTallaapartado = em.merge(tallaapartadoListNewTallaapartado);
                    if (oldIdTallaOfTallaapartadoListNewTallaapartado != null && !oldIdTallaOfTallaapartadoListNewTallaapartado.equals(talla)) {
                        oldIdTallaOfTallaapartadoListNewTallaapartado.getTallaapartadoList().remove(tallaapartadoListNewTallaapartado);
                        oldIdTallaOfTallaapartadoListNewTallaapartado = em.merge(oldIdTallaOfTallaapartadoListNewTallaapartado);
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
            List<BajaDeInventario> bajadeinventarioListOrphanCheck = talla.getBajadeinventarioList();
            for (BajaDeInventario bajadeinventarioListOrphanCheckBajadeinventario : bajadeinventarioListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Talla (" + talla + ") cannot be destroyed since the Bajadeinventario " + bajadeinventarioListOrphanCheckBajadeinventario + " in its bajadeinventarioList field has a non-nullable idTalla field.");
            }
            List<VentaTalla> ventatallaListOrphanCheck = talla.getVentatallaList();
            for (VentaTalla ventatallaListOrphanCheckVentatalla : ventatallaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Talla (" + talla + ") cannot be destroyed since the Ventatalla " + ventatallaListOrphanCheckVentatalla + " in its ventatallaList field has a non-nullable idTalla field.");
            }
            List<TallaApartado> tallaapartadoListOrphanCheck = talla.getTallaapartadoList();
            for (TallaApartado tallaapartadoListOrphanCheckTallaapartado : tallaapartadoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Talla (" + talla + ") cannot be destroyed since the Tallaapartado " + tallaapartadoListOrphanCheckTallaapartado + " in its tallaapartadoList field has a non-nullable idTalla field.");
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
        TypedQuery<Talla> query = em.createNamedQuery("Talla.findByTalla", Talla.class);
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
