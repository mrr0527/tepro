package locators.tesopro;

import org.openqa.selenium.By;

public class LocatorsCuentas {

    public static class Generico_Cuentas {
     //   public static final By ICONO_CALENDARIO = ;
     public static final By BTN_NEXT_RIGHT_OFF = By.xpath("//*[contains(@src,\"next_right_off\")]");
     public static final By BTN_NEXT_RIGHT_ON = By.xpath("//*[contains(@src,\"next_right_on\")]");


    }
    public static class Sub_Menu_Cuentas{
        public static final By BTN_CTA_CORRIENTE = By.id("arrow-https://banking.dbs-platform-sbx.itauchile2.cl/homehttps://banking.dbs-platform-sbx.itauchile2.cl/home/cuentas/pesos-option");
        public static final By BTN_VISTA = By.id("arrow-https://banking.dbs-platform-sbx.itauchile2.cl/home/cuentas/vista-option");
        public static final By BTN_DOLAR = By.id("arrow-https://banking.dbs-platform-sbx.itauchile2.cl/home/cuentas/dolar-option");
        public static final By BTN_EURO = By.id("arrow-https://banking.dbs-platform-sbx.itauchile2.cl/home/cuentas/euro-option");
    }
    public static class Sub_Menu_Cuenta_Corriente{
        public static final By BTN_CARTOLA_HISTORICA = By.xpath("//div[1]/div/div/a[@title = 'Cartola histórica']/section/p[contains(text(),'Cartola histórica')]");

    }

    public static class Pantalla_Cartola_Historica{
        public static final By LBL_TITULO = By.xpath("//h3[contains(text(), 'Cartola histórica')]");
        public static final By LBL_MENSAJE_ELIGE_CUENTA_Y_FECHA = By.xpath("//*[contains(text(), 'Elige una cuenta y una fecha para visualizar las cartolas disponibles')]");
        public static final By CBO_PERIODO_MES_ANNO = By.id("itau-one__input_:r1:");
        public static final By CBO_CARTOLA = By.id("itau-one__select_:r2:");
        public static final By BTN_CONSULTAR = By.xpath("//button[contains(text(), 'Consultar')]");

        public static final By LBL_NO_EXISTEN_DATOS_PARA_PERIODO_SELECCIONADO = By.xpath("//*[contains(text(), 'No existen datos para el periodo seleccionado')]");
        public static final By BTN_ANNO_ANTERIOR = By.xpath("//*[@class = 'react-calendar__navigation__arrow react-calendar__navigation__prev-button']");
        public static final By LST_CARTOLA_DISPONIBLE = By.xpath("//*[@class = '_1aRJ2SYv6XEAXLuuAfcunV']/div/div[1]");
        public static final By LST_CARTOLAS = By.xpath("//*[@class = '_1aRJ2SYv6XEAXLuuAfcunV']/div/div/span");
        public static final By LBL_RESUMEN_DE_SALDOS = By.xpath("//div[1]/h4[contains(text(), 'Resumen de saldos ')]");
        public static final By LBL_RESUMEN_DE_SALDOS_LINEA_DE_CREDITO = By.xpath("//div[1]/h4[contains(text(), 'Resumen de saldos ')]");
        public static final By LBL_SALDOS = By.xpath("//*[@class = '_1L-L_8DB_yObpx5HQrWW-7']/tr/*[ contains(text(), 'Saldos')]");
    }



}
