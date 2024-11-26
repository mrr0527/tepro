package comunidades.tesoreria.EstructuraTestCase;


import framework.generalUtilities.config.HookServicios;
import locators.Locators;
import framework.MicroServicesUtil;

import org.testng.annotations.Test;

public class TemplateApi extends HookServicios {

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
    public void TemplateApi() throws Exception {



        requests.add("200 OK");
        String script_container = Locators.Platforms.TESOPRO; //fijo
        String env = ""; // variable
        String collection = Locators.Collections.OTP_EMAIL; // variable
        msu = new MicroServicesUtil(env, collection, requests, script_container, this.getClass().getSimpleName(), this.getClass().getPackageName()); // fijo
        ejecucion(this.getClass().getSimpleName(),msu); //fijo

    }

}
