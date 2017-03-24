
package prueba;

import objetosNegocio.Modelo;
import objetosNegocio.*;
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
            Modelo modelo = new Modelo("1");
            Talla talla = new Talla("1", "10", 0, 0, "1");
            talla.setIdModelo(modelo);
            
            talla = persistencia.obtenTallaPorTalla(talla);
            System.out.println(talla);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
