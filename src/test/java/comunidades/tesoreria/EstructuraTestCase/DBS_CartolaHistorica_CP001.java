package comunidades.tesoreria.EstructuraTestCase;

import com.fasterxml.jackson.databind.JsonNode;
import framework.DatapoolParser;
import framework.TestScriptWebSelenium;
import locators.tesopro.Locators;
import locators.tesopro.LocatorsCuentas;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.awt.*;
import java.io.File;
import java.util.Iterator;

/**
 * 1.- 1.- Ingresar a Plataforma DBS Empresa url: https://banking.dbs-platform-sbx.itau.cl/
 * 2.- Ingresar credenciales "RUT" y "Clave", y luego presionar botón "Ingresar"
 * 3.- Seleccionar en menú lateral izquierdo la opción "Cuentas"
 * 4.- Seleccionar sub-opción "Cuenta Peso"
 *
 * RE: Visualizar sub-opción "Cartola Histórica"
 *
 * @Autor: CNGR3093
 * @Fecha: 02-05-2024
 **/
public class DBS_CartolaHistorica_CP001 {
    static TestScriptWebSelenium selenium = null;
    DatapoolParser pool = null;
    String cpID = "TC_CARTOLA_HISTORICA_CP001";

    @BeforeClass()
    public void setup() throws AWTException {
        pool = new DatapoolParser("." + File.separator + "DataPool.xlsx");

        pool.filter("CP", cpID, true);
        selenium = new TestScriptWebSelenium(cpID, pool.getValue("BROWSER"));
        selenium.startTest("Plataforma_Mayorista");
    }

    @Test()
    public void Acceso_A_La_Plataforma() throws Exception {
        try {

            TestExplory api=new TestExplory();
            api.TestExplory();
            String bodyResponse= pool.lecturaBody();
            JsonNode node=pool.parseJsonBody();
            System.out.println("!body response es**************** "+bodyResponse);
            System.out.println("*************el valor del campo status es ********: " + node.get("status").asText());
            System.out.println("*************el valor del campo resolutionCode es ********: " + node.get("resolutionCode").asText());
            System.out.println("*************el valor del campo description es ********: " + node.get("description").asText());
            System.out.println("*************el valor del campo simulationId es ********: " + node.get("simulationId").asText());

            Iterator<JsonNode> elementsJsonNode=pool.pathJsonBody(node,"resetValues");
            while(elementsJsonNode.hasNext()){
                JsonNode elements = elementsJsonNode.next();
                System.out.println("los valores del array son = "+elements.asLong());

            }


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
