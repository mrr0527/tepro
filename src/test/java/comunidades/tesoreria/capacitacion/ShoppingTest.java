package comunidades.tesoreria.capacitacion;

import framework.generalUtilities.HookPersonas;
import genericos.demoqa.DemoQAGen;
import org.testng.annotations.Test;

public class ShoppingTest extends HookPersonas {
    @Test
    public void ShoppingTest() throws Exception {

        try {
            getSelenium().startTest("Shopping");
            selenium.loginshopping(pool);


            getSelenium().endCaseOk();

        } catch (Exception e) {
            selenium.endCaseError(e);
            throw e;
        }
    }
}
