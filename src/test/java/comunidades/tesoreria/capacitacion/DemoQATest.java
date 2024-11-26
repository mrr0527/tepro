package comunidades.tesoreria.capacitacion;

import framework.generalUtilities.HookPersonas;
import genericos.demoqa.DemoQAGen;
import org.testng.annotations.Test;

public class DemoQATest extends HookPersonas {

    @Test()
    public void DemoQATest(){
        //URL
        getSelenium().startTest("DemoQA");
        DemoQAGen demoQA = new DemoQAGen();

        try {
            demoQA.menuQaDemo("Elements");
            getSelenium().endCaseOk();

        } catch (Exception e) {
            selenium.endCaseError(e);
            throw e;
        }
    }
}
