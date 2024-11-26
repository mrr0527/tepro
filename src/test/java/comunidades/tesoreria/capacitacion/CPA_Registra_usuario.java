package comunidades.tesoreria.capacitacion;

import framework.generalUtilities.HookPersonas;
import genericos.demoqa.GenShopping;
import org.testng.annotations.Test;

import static framework.generalUtilities.HookPersonas.selenium;

public class CPA_Registra_usuario extends HookPersonas {

    @Test
    public void CPA_Registra_usuario() throws Exception {

        GenShopping genShopping =new GenShopping();

        try {
            getSelenium().startTest("Shopping");
            selenium.loginshopping(pool);
            genShopping.PageRegistroUser();







            getSelenium().endCaseOk();

        } catch (Exception e) {
            selenium.endCaseError(e);
            throw e;
        }
    }
}
