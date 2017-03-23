
package persistencia;

import controladores.ApartadoJpaController;
import controladores.BajaDeInventarioJpaController;
import controladores.ModeloJpaController;
import controladores.MovimientoEnApartadoJpaController;
import controladores.TallaApartadoJpaController;
import controladores.TallaJpaController;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import objetosNegocio.Apartado;
import objetosNegocio.BajaDeInventario;
import objetosNegocio.Modelo;
import objetosNegocio.MovimientoEnApartado;
import objetosNegocio.Talla;
import objetosNegocio.TallaApartado;
import pvcco.interfaces.IntPersistencia;

/**
 *
 * @author Raul Karim Sabag Ballesteros
 */
public class Persistencia implements IntPersistencia{

    private ApartadoJpaController apartadoJpa;
    private MovimientoEnApartadoJpaController movApartadoJpa;
    private ModeloJpaController modeloJpa;
    private TallaJpaController tallaJpa;
    private TallaApartadoJpaController tallaApartadoJpa;
    private BajaDeInventarioJpaController bajaDeInventarioJpaController;
    //private TipoUsuarioJpaController tipousuarioJpaController;
    //private UsuarioJpaController usuarioJpaController;
    //private VentaJpaController ventaJpaController;
    //private MovimientoEnVentaJpaController movimientoenventaJpaController;
    //private VentaTallaJpaController ventatallaJpaController;

    public Persistencia(){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PVCCO_-_1.0PU");
        
        apartadoJpa = new ApartadoJpaController(emf);
        movApartadoJpa = new MovimientoEnApartadoJpaController(emf);
        modeloJpa = new ModeloJpaController(emf);
        tallaJpa = new TallaJpaController(emf);
        tallaApartadoJpa = new TallaApartadoJpaController(emf);
        bajaDeInventarioJpaController = new BajaDeInventarioJpaController(emf);
        //tipousuarioJpaController = new TipoUsuarioJpaController(emf);
        //usuarioJpaController = new UsuarioJpaController(emf);
        //ventaJpaController = new VentaJpaController(emf);
        //movimientoenventaJpaController = new MovimientoEnVentaJpaController(emf);
        //ventatallaJpaController = new VentaTallaJpaController(emf);
    }
    
    @Override
    public void agregar(MovimientoEnApartado mov) throws Exception {
        movApartadoJpa.create(mov);
    }

    @Override
    public void actualizar(MovimientoEnApartado mov) throws Exception {
        movApartadoJpa.edit(mov);
    }

    @Override
    public void eliminar(MovimientoEnApartado mov) throws Exception {
        movApartadoJpa.destroy(mov.getIdMovimientoApartado());
    }

    @Override
    public void agregar(Apartado apartado) throws Exception {
        apartadoJpa.create(apartado);
    }

    @Override
    public void modificar(Apartado apartado) throws Exception {
        apartadoJpa.edit(apartado);
    }

    @Override
    public void eliminar(Apartado apartado) throws Exception {
        apartadoJpa.destroy(apartado.getIdApartado());
    }

    @Override
    public void agregar(Talla talla) throws Exception{
        tallaJpa.create(talla);
    }

    @Override
    public void eliminar(Talla talla) throws Exception{
        tallaJpa.destroy(talla.getIdTalla());
    }

    @Override
    public void actualizar(Talla talla) throws Exception{
        tallaJpa.edit(talla);
    }

    @Override
    public void agregar(Modelo modelo) throws Exception {
        modeloJpa.create(modelo);
    }

    @Override
    public void eliminar(Modelo modelo) throws Exception {
        modeloJpa.destroy(modelo.getIdModelo());
    }

    @Override
    public void actualizar(Modelo modelo) throws Exception {
        modeloJpa.edit(modelo);
    }

    @Override
    public void agregar(BajaDeInventario baja) throws Exception {
        bajaDeInventarioJpaController.create(baja);
    }

    @Override
    public void actualizar(BajaDeInventario baja) throws Exception {
        bajaDeInventarioJpaController.edit(baja);
    }

    @Override
    public void eliminar(BajaDeInventario baja) throws Exception {
        bajaDeInventarioJpaController.destroy(baja.getIdBajaInventario());
    }

    @Override
    public List<BajaDeInventario> obtenBajasDeInventario() throws Exception {
        return bajaDeInventarioJpaController.findBajaDeInventarioEntities();
    }

    @Override
    public List<Modelo> obtenModelos() throws Exception {
        return modeloJpa.findModeloEntities();
    }

    @Override
    public List<Apartado> obtenApartados() throws Exception {
        return apartadoJpa.findApartadoEntities();
    }

    @Override
    public List<Talla> obtenTallas() throws Exception {
        return tallaJpa.findTallaEntities();
    }

    @Override
    public Talla obten(Talla talla) throws Exception {
        return tallaJpa.findTalla(talla.getIdTalla());
    }

    @Override
    public Modelo obten(Modelo modelo) throws Exception {
        return modeloJpa.findModelo(modelo.getIdModelo());
    }

    @Override
    public Apartado obten(Apartado apartado) throws Exception {
        return apartadoJpa.findApartado(apartado.getIdApartado());
    }

    @Override
    public BajaDeInventario obten(BajaDeInventario baja) throws Exception {
        return bajaDeInventarioJpaController.findBajaDeInventario(baja.getIdBajaInventario());
    }

    @Override
    public MovimientoEnApartado obten(MovimientoEnApartado mov) throws Exception {
        return movApartadoJpa.findMovimientoEnApartado(mov.getIdMovimientoApartado());
    }

    @Override
    public List<MovimientoEnApartado> obtenAbonosRegistrados() throws Exception {
       return movApartadoJpa.findMovimientoEnApartadoEntities();
    }

    @Override
    public void agregar(TallaApartado talla) throws Exception {
        tallaApartadoJpa.create(talla);
    }

    @Override
    public void actualizar(TallaApartado talla) throws Exception {
        tallaApartadoJpa.edit(talla);
    }

    @Override
    public void eliminar(TallaApartado talla) throws Exception {
        tallaApartadoJpa.destroy(talla.getIdTallaApartado());
    }

    @Override
    public List<TallaApartado> obtenTallasApartadas() throws Exception {
        return tallaApartadoJpa.findTallaApartadoEntities();
    }

}
