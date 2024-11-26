package genericos.demoqa;

import framework.Log;
import framework.generalUtilities.HookPersonas;
import org.openqa.selenium.By;

import java.util.logging.Logger;

import static locators.demoqa.DemoQALocators.MENU;
import static locators.demoqa.DemoQALocators.MENU_SCROLL;

public class DemoQAGen extends HookPersonas {

    static Log log = new Log();
    public static final Logger LOGGER = Logger.getLogger("TestScriptWebSelenium");


    public void menuQaDemo(String menu) {

        try {
            getSelenium().scrollElement(By.xpath(MENU_SCROLL));
            getSelenium().switchDefaultIFrame();
            getSelenium().click(By.xpath(MENU));
        } catch (Exception ex) {
            LOGGER.info("*******NO SE ENCONTRO ELEMENTO********* ");
            log.addStep("Mensaje de excepción : \n" + ex.getMessage(), false);
        }

    }
}
