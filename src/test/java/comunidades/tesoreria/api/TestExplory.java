package comunidades.tesoreria.api;

import framework.MicroServicesUtil;
import framework.generalUtilities.config.HookServicios;
import locators.Locators;
import org.testng.annotations.Test;

public class TestExplory extends HookServicios {

    /**
     * Script Name   : <b>Login_API_Test01</b>
     * Generated     : <b>30-07-2024 11:58:39</b>
     * Description   : Functional Test Script
     * Original Host : WinNT Version 6.1  Build 7601 (S)
     *
     * Se genera un nuevo template de servicios
     * @since  2024/07/31
     * @author CNGR3093
     * @throws Exception
     */


    @Test
    public void TestExplory() throws Exception {

        requests.add("Aprobado politica A");
        String script_container = Locators.Platforms.TESOPRO; //fijo
        String env = ""; // variable
        String collection = Locators.Collections.ID_VIV_HAB_APROBADAAPREEVALUADOR_002; // variable
        msu = new MicroServicesUtil(env, collection, requests, script_container, this.getClass().getSimpleName(), this.getClass().getPackageName()); // fijo
        ejecucion(this.getClass().getSimpleName(),msu); //fijo

    }

}
