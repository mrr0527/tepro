package framework;


import locators.cursoaut.locatorsShopping;
import locators.tesopro.Locators;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v124.network.Network;
import org.openqa.selenium.devtools.v124.network.model.Request;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;


public class Login extends TestScriptUtil
{
    //Driver

    private WebDriver driver;
    TestScriptWebSelenium selenium = null;
    DBManager dbm = new DBManager();

    static String token;

    public static String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setDriver(WebDriver driver, Log log) {
        this.driver = driver;
        selenium = new TestScriptWebSelenium(driver, log);
    }

    /**
     * Descripcion URL de los portales
     *
     * @param enviroment recibe nombre del portal a levantar para la prueba
     * @return retorna ambiente solicitado para la prueba
     */
    public String enviromentTest(String enviroment) {
        switch (enviroment) {
            case "Portal":
                enviroment = "https://portalqa.itauchile2.cl/wps/portal/BICPublico/home";
                break;
            case "Plataforma_Mayorista":
                enviroment = "https://banking.dbs-platform-sbx.itauchile2.cl/";
                break;
            case "DemoQA":
                enviroment = "https://demoqa.com/";
                break;
            case "Saucedemo":
                enviroment = "https://www.saucedemo.com/v1/index.html";
                break;
            case "Shopping":
                enviroment = "http://advantageonlineshopping.com/#/";
                break;
            default:
                break;
        }
        return enviroment;
    }


    /**
     * Login Portal Mayorista
     * Descripción: Login pra portal empresas que extrae del DataPool las credenciales necesarias
     *
     * @param pool datapool
     */
    public void loginPortaMayorista(DatapoolParser pool) throws Exception {
        if (!pool.getValue("RUT").equalsIgnoreCase("")) {
            try {
                String rutApoderado = pool.getValue("RUT");
                String rutEmpresa = pool.getValue("PASSWORD");

                selenium.setText(Locators.Login.TXT_RUT_USUARIO, rutApoderado);
                selenium.setText(Locators.Login.TXT_CLAVE, pool.getValue("PASSWORD"));
                selenium.click(Locators.Login.TXT_CLAVE);
                selenium.click(Locators.Login.BTN_INGRESAR);
                selenium.log.addStep("Login Rut Apoderado: [" + rutApoderado + "] " + "Rut Empresa: [" + rutEmpresa + "] " + "ejecutado en plataforma:Empresas".toUpperCase());
            } catch (Exception e) {
                e.getMessage();
            }
        } else {
            throw new Exception("No se encuentra el caso de prueba en Datapool");
        }

    }



    public void loginshopping(DatapoolParser pool) throws Exception {


        if (!pool.getValue("USER").equalsIgnoreCase("")) {
            try {

                String user = pool.getValue("USER");
                String pass = pool.getValue("PASSWORD");

                //REALIZO CLICK EN EL ICONO LOGIN
                selenium.existeElementoSinEvidencia(locatorsShopping.loginShopping.ICONOUSER,5);
                selenium.clickR(locatorsShopping.loginShopping.ICONOUSER);

                ///selenium.existElement(locatorsShopping.loginShopping.TXTUSER,5);
                selenium.hoverR(locatorsShopping.loginShopping.TXTUSER);
                selenium.setText(locatorsShopping.loginShopping.TXTUSER,user);

                selenium.existeElementoSinEvidencia(locatorsShopping.loginShopping.TXTPASSW,5);
                selenium.setText(locatorsShopping.loginShopping.TXTPASSW,pass);
                selenium.existeElementoSinEvidencia(locatorsShopping.loginShopping.BTNINGRESAR,5);

                selenium.click(locatorsShopping.loginShopping.BTNINGRESAR);
                selenium.existElement(locatorsShopping.loginShopping.CHECKRECORDAR,5);

                /// siempre y cuando no exista el usuario y se debe crear
                if (selenium.existElement(locatorsShopping.loginShopping.LaberError, 5)){

                    selenium.existElement(locatorsShopping.loginShopping.LINKCREATE,5);
                    selenium.click(locatorsShopping.loginShopping.LINKCREATE);

                }



            } catch (Exception e) {
                e.getMessage();
            }
        } else {
            throw new Exception("No se encuentra el caso de prueba en Datapool");
        }

    }
}


