package locators.tesopro;

import org.openqa.selenium.By;

public class Locators {
    public static class Login {
        public static final By TXT_RUT_USUARIO = By.id("login-rut-input");
        public static final By TXT_CLAVE = By.id("login-password-input");
        public static final By BTN_INGRESAR = By.id("login-button");
    }

    public static class HomeLineadeCredito {

        public static final By BTN_MENU_LINEA_DE_CREDITO = By.xpath("//div[1]/div/div[2]/div[1]/div[2]/button[@data-testid ='menu-LNACRE-menulist']");
        public static final By OPC_CARTOLA_HISTORICA = By.xpath("//div[@id = 'menu-LNACRE']/div[1]/ul[1]/li[contains(text(), 'Cartola histórica')]");
        public static final By BTN_MENU_TARJE_LI_CRE_CARTOLA_HISTORICA = By.xpath("//*[@id='menu-LNACRE']/div[1]/ul/li[contains(text(), 'Cartola histórica')]");
    }

    public static class HomeCuentaCorriente {

        public static final By BTN_MENU_CUENTA_CORRIENTE = By.xpath("//div/div[3]/div[1]/div/div[1]/div[1]/div[2]/button[@data-testid='menu-CTACTE-menulist']");
         public static final By OPC_CARTOLA_HISTORICA_CTA_CTE = By.xpath("//div[@id = 'menu-CTACTE']/div[1]/ul[1]/li[contains(text(), 'Cartola histórica')]");
    }

    public static class HomeCuentaDolar {

        public static final By BTN_MENU_CUENTA_DOLAR = By.xpath("//div/div[3]/div[1]/div/div[3]/div[1]/div[2]/button[@data-testid='menu-CTADOL-menulist']");
        public static final By OPC_CARTOLA_HISTORICA_CTA_DOLAR = By.xpath("//div[@id = 'menu-CTADOL']/div[1]/ul[1]/li[contains(text(), 'Cartola histórica')]");
    }

    public static class MenuLateral {
        public static final By BTN_INICIO = By.xpath("//*[contains(text(), 'Inicio')]");

        public static final By BTN_CUENTAS = By.id("arrow-https://banking.dbs-platform-sbx.itauchile2.cl/home/cuentas-option");

    }

}
