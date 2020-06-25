package qo.control.general;

import exemodelo.EjecutaPython;
import isahc.propiedades.Propiedades;
import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Properties;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.*;  
 

import org.apache.log4j.Logger;


public class cConnection {
 
    
    static Logger logger = Logger.getLogger(cConnection.class);
    
    private Propiedades Configuracion = new Propiedades(); 	
    
    private Properties properties = Configuracion.getProperties();

    
    /*Atributos*/
    
    
    private String url = properties.getProperty("connectbd.url");          // "jdbc:mysql://localhost/";
 
    private String driver =  properties.getProperty("connectbd.driver");   // "isahc";
 
    private String usr = properties.getProperty("connectbd.usr");   // "root";
 
    private String pswd = properties.getProperty("connectbd.pswd");  // "drla0404";
 
    private Connection con;
    
    public String nameFile = "";
    public String directorio = "";
 
    /*Constructor, carga puente JDBC-ODBC*/
 
    public cConnection()
    {
       loadDriver();
    }
 
    /**
    * Carga el driver de la conexión a la base de datos
    */
    private void loadDriver()
    {
        try
        {
            //Instancía de una nueva clase para el puente
            //sun.jdbc.odbc.JdbcOdbcDriver
            //El puente sirve entre la aplicación y el driver.    
            Class.forName( "sun.jdbc.odbc.JdbcOdbcDriver" );
        }
        catch(ClassNotFoundException e)
        {
            logger.error("Error al crear el puente JDBC-ODBC"); 
        }
    }
 
    /**
    *Obtiene una conexión con el nombre del driver especificado
    *@param driverName Nombre del driver de la base de datos
    *@return
    */
    public Connection mkConection()
    {
        url = url + driver;
       // logger.info("Estableciendo conexión con " + url);
        try
        {
            //Obtiene la conexión
            con = DriverManager.getConnection( url,usr,pswd);
        }
        catch(SQLException sqle)
        {
            logger.error("No se pudo establecer la conexión"+sqle.getMessage());
            return null;
        }

        //Regresa la conexión </span>
        return con;
    }
 
    /* Cerrar la conexión.*/
 
    public boolean closeConecction()
    {
        try
        {
            con.close();
        }
        catch(SQLException sqle)
        {
            logger.error("No se cerro la conexión");
            return false;
        }
 
        // logger.info("Conexión cerrada con éxito ");
        return true;
    }

    
    
    public int ejecutarProcedimiento( String procedimiento ) throws SQLException
    {
    cConnection conx = new cConnection();

       System.out.println("Procedimiento a Ejecutar   "+procedimiento);

    
        try (Connection con = conx.mkConection()) {
            if (con == null) {
                System.out.println("Error en la conexion   ");
            } else {
                System.out.println("Conexion OK ");
                String query = "SELECT uni_codigo, uni_nombre FROM uni_universidad";
                
                Statement st = con.createStatement();
                
                ResultSet rs = st.executeQuery(query);
                while (rs.next())
                {
                    String codigo = rs.getString("uni_codigo");
                    String nombre = rs.getString("uni_nombre");
                    
                    // print the results
                    System.out.format("%s, %s \n", codigo, nombre);
                }
                System.out.println(" Inicio Procedimiento ejecutado .................");
                
                CallableStatement cStmt = con.prepareCall("{call "+procedimiento+"}");
                cStmt.execute();
                rs = cStmt.getResultSet();
                System.out.println(" Fin Procedimiento ejecutado .................");
                
                // Llamar procedimiento almacenado -----------   purge_idle_connections
            }
            // purge_idle_connections
        }
         
        return 1;
    }

    




    public int leerCarpeta(  ) 
    {
         
        String pathFile = properties.getProperty("archivo.path");  
        
        File carpeta = new File(pathFile);
        String nameFile = "";
        String[] listado = carpeta.list();
        if (listado == null || listado.length == 0) 
        {
          System.out.println("No hay elementos dentro de la carpeta actual");
          return -1; 
        }
        else 
        {
           // Valida que solo exista un solo archivo 
           if ( listado.length > 1  ) 
           {
              System.out.println("Solo Debe haber un archivo en esta carpeta. Actualmente existen  "+listado.length);
              return -2;                
           }    
           
           // Validar que el archivo sea .csv
           nameFile = listado[0].toUpperCase();
           
           if ( nameFile.indexOf(".CSV") == -1 ) 
           {
              System.out.println("El archivo no tiene una extension valida. Debe ser .CSV  "+nameFile);
              return -3;                
           }    
           this.directorio = pathFile; 
           this.nameFile = nameFile;
           
        }
        return 1;
    }

// --------------------- METODOS PARA MANEJO DE LOS MODELOS MATEMATICOS ---------------------------   
// 1. Leer los modelos pendientes
// 2. Llamar a la funcion que ejecuta el python.
// 3. Cerrar la conexion.    
    
    public int ejecutarModeloPython( String procedimiento ) 
    {
        
    EjecutaPython ejecutarPython = new EjecutaPython();
    cConnection conx = new cConnection();
    Connection con = null;
    
       System.out.println("---- INICIA PROCESO ejecutarModeloPython ---- "+procedimiento);

    
        try  {
             con = conx.mkConection();
            if (con == null) {
                System.out.println("Error en la conexion   ");
                return -1;
            } else {
                System.out.println("Conexion OK ");
                String query = " select mod_nombre, mod_proceso, mexe_exemodelo.MEXE_SECUE  from mod_modelo , mexe_exemodelo \n" +
                                 "where mod_modelo.MOD_SECUE =  mexe_exemodelo.MOD_SECUE\n" +
                                 "and mexe_exemodelo.MEXE_ESTADO = \"Pendiente\"  ";
                
                Statement st = con.createStatement();
                
                ResultSet rs = st.executeQuery(query);
                while (rs.next())
                {
                    String modelo = rs.getString("mod_nombre");
                    String proceso = rs.getString("mod_proceso");
                    String codEjecucion = rs.getString("mexe_secue");
                    
                    ejecutarPython.ejecutar(proceso, codEjecucion);
                    
                    
                    // print the results
                    System.out.format("%s, %s %s \n", modelo, proceso, codEjecucion);
                }
                
                System.out.println(" Fin Consulta Modelo .................");
                
                // Llamar procedimiento almacenado -----------   purge_idle_connections
            }
            // purge_idle_connections
        }
        catch (Exception e) {
          System.out.println(" Error del SQL");
            
            
        }
         
        return 1;
    }

    
    


public static void main(String[] args) throws SQLException {
 
    

           cConnection conx = new cConnection();
           
           conx.ejecutarModeloPython("Buscar Modelos");
           
           System.out.println(" Nombre del archivo capturado " + conx.nameFile);
            
    
  } // fin del Main

}  
   
    
    




    





