/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exemodelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import qo.control.general.cConnection;

/**
 *
 * @author User
 */
public class ExeModelo {

    
    
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        
         cConnection conx = new cConnection();
         
         conx.ejecutarModeloPython("Inicio");

        
        System.out.println("---------- Ejecutar MODELO ---------------- ");    
        
    }
    
}
