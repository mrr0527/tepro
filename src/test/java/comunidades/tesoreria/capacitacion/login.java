package comunidades.tesoreria.capacitacion;

import framework.generalUtilities.HookPersonas;
import org.junit.Test;

import static framework.generalUtilities.HookPersonas.selenium;

public class login extends HookPersonas {


    @Test
    public void LoginShopping() throws Exception
    {
        try
        {
            getSelenium().startTest("Shopping");
            selenium.loginshopping(pool);

        }
            catch (Exception e)
        {        getSelenium().endCaseError(e);
            throw e;
        }
    }
}
