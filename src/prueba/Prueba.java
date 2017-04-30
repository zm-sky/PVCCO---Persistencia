/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prueba;

import java.util.ArrayList;
import java.util.List;
import objetosNegocio.Modelo;
import objetosNegocio.Talla;
import persistencia.Persistencia;

/**
 *
 * @author zippy
 */
public class Prueba {
    public static void main(String[] args) throws Exception{
        Modelo m = new Persistencia().obten(new Modelo("0"));
        
        if(m != null)
            System.out.println(new Persistencia().obtenTallasDeModelo(m).size());
        else
            System.out.println("Es nulo");
    }
}
