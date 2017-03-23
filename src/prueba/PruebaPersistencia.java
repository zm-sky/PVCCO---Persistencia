
package prueba;

import objetosNegocio.Modelo;
import persistencia.Persistencia;
import pvcco.interfaces.IntPersistencia;

/**
 *
 * @author Raul Karim Sabag Ballesteros
 */
public class PruebaPersistencia {
    public static void main(String[] args){
        try{
            IntPersistencia persistencia = new Persistencia();
            Modelo modelo = persistencia.obtenModelos().get(0);
            modelo.setPrecio(33.2f);
            modelo.setNombre("Zapatilla / Rojo");
            persistencia.actualizar(modelo);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
