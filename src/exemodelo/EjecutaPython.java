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

/**
 *
 * @author User
 */
public class EjecutaPython {
    
    
    // --------------------- Ejecuta Procesos python ---------------------------   
// 1. Recibe el nombre y path del proceso matematico que esta en .py
// 2. Desde el Python deben cambiar el estado del Modelo para que solo se ejecute una sola vez.
// 3. Cerrar la conexion.    
    
public int ejecutar( String programaPython, String parametros   ) 
{
  
   // TODO code application logic here
      System.out.println("-- INICIO ---------- EjecutaPython -> ejecutar ---------------- ");    
      System.out.println("-- INICIO ---------- Programa Python -----------------> "+programaPython);    
      System.out.println("-- INICIO ---------- Parametro Python ----------------> "+parametros);    
      
      try {
            // Execute a command without arguments
            String command = "python C:\\DRLA\\python\\ModeloV1.py PARAMETRO-01";
            command = "python "+programaPython+ " " + parametros;
            //command = "cmd /c dir";
            System.out.println("-- EJECUCION COMMANDO----> "+command);    

            
            Process p = Runtime.getRuntime().exec(command);
    
            // Se obtiene el stream de salida del programa
            InputStream is = p.getInputStream();
            
            /* Se prepara un bufferedReader para poder leer la salida más comodamente. */
            BufferedReader br = new BufferedReader (new InputStreamReader (is));
            
            // Se lee la primera linea
            String aux = br.readLine();
            
            // Mientras se haya leido alguna linea
            while (aux!=null)
            {
                // Se escribe la linea en pantalla
                System.out.println (aux);
                
                // y se lee la siguiente.
                aux = br.readLine();
            }
    
    } catch (IOException e) {
      System.out.println("---------- Error en la ejecucion ");    
        
    }

  System.out.println("-- FIN ---------- EjecutaPython -> ejecutar ---------------- ");    
  System.out.println("-- FIN ---------- Programa Python -----------------> "+programaPython);    
  System.out.println("-- FIN ---------- Parametro Python ----------------> "+parametros);    
      
      
  return 1;
}

    
    
    
    
    
    
}
