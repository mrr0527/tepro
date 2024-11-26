package framework;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.ibm.as400.access.AS400JDBCDriver;
import org.testng.annotations.Test;

public class DbAs400 {



    @Test
    public void conexion() throws Exception {
        try {
            // Registro del driver
            DriverManager.registerDriver(new AS400JDBCDriver());

            // Parámetros de conexión
            String url = "jdbc:as400:10.181.148.10";
            String usuario = "CIGG981";
            String contraseña = "ISERIES";

            // Establecer conexión
            Connection conn = DriverManager.getConnection(url, usuario, contraseña);

            System.out.println("Conexión establecida con éxito");

            // Cerrar conexión
            conn.close();
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
    }
}
