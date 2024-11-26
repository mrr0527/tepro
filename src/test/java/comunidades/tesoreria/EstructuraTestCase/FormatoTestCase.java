package comunidades.tesoreria.EstructuraTestCase;

import comunidades.tesoreria.api.TemplateApi;
import framework.DatapoolParser;
import framework.TestScriptWebSelenium;
import locators.tesopro.Locators;
import locators.tesopro.LocatorsCuentas;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.awt.*;
import java.io.File;

/**
 * 1.- 1.- Ingresar a Plataforma DBS Empresa url: https://banking.dbs-platform-sbx.itau.cl/
 * 2.- Ingresar credenciales "RUT" y "Clave", y luego presionar botón "Ingresar"
 * 3.- Seleccionar en menú lateral izquierdo la opción "Cuentas"
 * 4.- Seleccionar sub-opción "Cuenta Peso"
 *
 * RE: Visualizar sub-opción "Cartola Histórica"
 *
 * @Autor: EFGC7520
 * @Fecha: 02-05-2024
 **/
public class FormatoTestCase {

    static TestScriptWebSelenium selenium = null;
    DatapoolParser pool = null;
    String cpID ;
    TemplateApi api=new TemplateApi();

    @BeforeClass()
    public void setup() throws Exception {
        try {
            pool = new DatapoolParser("." + File.separator + "DataPool.xlsx");
            cpID = this.getClass().getSimpleName();
            pool.filter("CP", cpID, true);
            selenium = new TestScriptWebSelenium(cpID, pool.getValue("BROWSER"));
            selenium.startTest("Plataforma_Mayorista");

        } catch (Exception e) {

            System.out.println(e.getMessage());
        }
    }

    @Test()
    public void FormatoTestCase() throws Exception {
        try {

         // Formato para ingresar valores para los servicios

            String valor="[\n" +
                    "    {\n" +
                    "        \"rut\": \"1111111\",\n" +
                    "        \"password\": \"certi148\",\n" +
                    "        \"method\": \"POST\"\n" +
                    "    }\n" +
                    "]";

       /* Se debe ingresar la Api y el valor que quieres ingresar en el DataPool_Servicios
       esto permitira que al ejecutar el Servicio vaya a leer el DataPool_Servicios
        y ejecutar con esos valores
       */
            DatapoolParser.busquedaCP("TemplateApi",valor);

       // Ejecucion del Servicio (Todos los servicios deben estar en el package 'api')


            api.TemplateApi();

      /* Metodo que permite la lectura del body de respuesta una vez ejecutado el servicio
       */
            String bodyResponse= pool.lecturaBody();

            selenium.loginPortaMayorista(pool);
            selenium.click(Locators.MenuLateral.BTN_CUENTAS);
            selenium.click(LocatorsCuentas.Sub_Menu_Cuentas.BTN_CTA_CORRIENTE);
            selenium.existElement(LocatorsCuentas.Sub_Menu_Cuenta_Corriente.BTN_CARTOLA_HISTORICA,5);
            selenium.manualCheck("La opción Cartola Histórica para cuenta corriente en la nueva Plataforma Mayorista se visualizan correctamente " + cpID, true);
            selenium.endCaseOk();

        } catch (Exception e) {
            selenium.endCaseError(e);
            throw e;
        }
    }

    @AfterClass()
    public void afterMain() throws AWTException {
        selenium.saveLog();
        selenium = null;
    }




}
