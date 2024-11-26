package framework.generalUtilities;

import framework.DatapoolParser;
import framework.TestScriptWebSelenium;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.awt.*;
import java.lang.reflect.Method;
import java.nio.file.Paths;

import static framework.generalUtilities.GeneralActions.searchFile;
import static framework.generalUtilities.GenericString.setPath;



public class HookPersonas {
    public static TestScriptWebSelenium selenium;
    public static DatapoolParser pool;
    @BeforeMethod(groups = {"regresion"},description = "Acciones antes del test")
    public void setup(Method method) throws AWTException {
        System.out.println("Test name: " + method.getName());
        //System.out.println("class name " + this.getClass().getSimpleName());
        String cpID = this.getClass().getSimpleName();
        System.out.println("name " + cpID);
        pool = new DatapoolParser(String.valueOf(searchFile("DataPool", Paths.get(".").toFile()) ));
        pool.filter("CP", cpID, true);
        System.out.println("navegaro " + pool.getValue("BROWSER"));
        selenium = new TestScriptWebSelenium(cpID, pool.getValue("BROWSER"));
    }
    @AfterMethod(groups = {"regresion"},description = "Acciones despues del test")
    public void afterMain(ITestContext context) throws AWTException {
        setPath(String.valueOf(this.getClass().getPackage()));
        selenium.saveLog();
        if (selenium != null){
            selenium.getDriver().quit();
        }
    }
    public TestScriptWebSelenium getSelenium() {
        return selenium;
    }

    public DatapoolParser getPool() {
        return pool;
    }
}
