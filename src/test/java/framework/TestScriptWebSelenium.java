package framework;

import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import io.github.bonigarcia.wdm.WebDriverManager;
import locators.tesopro.LocatorsCuentas;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;

public class TestScriptWebSelenium extends Login {

    //Variable Ejecucion Headles o Default
    String ejecucion;
    //Variable Evidencia
    String evidencia;
    // Searching max wait time
    protected int timeWait;
    // Store execution log contais steps and images of the TC

    // Current Browser name - extracted from process
    protected String current_browser;
    // Id of the current TestCase
    protected String cpID;
    // Start time of the execution instance
    protected long startTime;
    // End time of the execution instance
    protected long endTime;
    // Flag that contains the state of the case, by delfaut is failed
    // until caseEndOk is called
    protected boolean ended_ok = false;
    //DataBase Connections
    DBManager dbm;
    //Driver
    private WebDriver driver;
    //WebDriverWait
    private WebDriverWait wait;
    //
    String windowTitle = "Not";

    // Name of the execution script used for log purposes
    //protected String ruteIdCAEx;
    private String testName="";
    public static final Logger LOGGER = Logger.getLogger("TestScriptWebSelenium");
    String sistemaOperativo = "Windows";

    boolean nameRegresion=false;
    boolean nombreExterno=false;

    /**
     * Descripcion: Constructor de la Clase
     *
     * @param cpID      ID del caso de prueba, es requerido y usado para información enviada al Log
     * @param navegador driver con el cual se trabajará, Chrome, Firefox , IE y EDGE, El navegador viene de la columna "BROWSER" del DataPool.xlsx
     **/

    public TestScriptWebSelenium(String cpID, String navegador) {
        if (navegador.equals("ETL") || navegador.equals("SP")) {
            dbm = new DBManager();
            this.cpID = cpID;
            //      log.addStep("Inicio de prueba " + this.cpID);
            this.timeWait = 30;
            //      setColor(Color.RED);
            ruteIdCAEx = Thread.currentThread().getStackTrace()[2].getClassName();
            current_browser = navegador;
            startTime = System.currentTimeMillis();

            if (System.getProperty("pipeline") == null || System.getProperty("pipeline").equals("")
                    || System.getProperty("pipeline").equalsIgnoreCase("false")) {
                this.ejecucion = "DEFAULT";
                this.evidencia = "false";
            } else if (System.getProperty("pipeline").equalsIgnoreCase("true")) {
                this.evidencia = "true";
                this.ejecucion = "HEADLESS";
                //Crear archivo fallidos
                archivoFallidos();
            }
            log.setEjecucion(this.ejecucion);

        } else {
            dbm = new DBManager();
            if (System.getProperty("browser") == null || (System.getProperty("browser").equals(""))) {
                LOGGER.info("*******Navegador Por Datapool*********");

            } else if (!System.getProperty("browser").equalsIgnoreCase("")) {
                switch (System.getProperty("browser").toUpperCase()) {
                    case "CHROME":
                    case "FIREFOX":
                    case "EDGE":
                        navegador = System.getProperty("browser");
                        break;
                    default:
                        LOGGER.info("*******Navegador no Encontrado*********");
                        break;
                }
            }

            if (System.getProperty("pipeline") == null || System.getProperty("pipeline").equals("")
                    || System.getProperty("pipeline").equalsIgnoreCase("false")) {
                this.ejecucion = "DEFAULT";
                this.evidencia = "false";
            } else if (System.getProperty("pipeline").equalsIgnoreCase("true")) {
                this.evidencia = "true";
                this.ejecucion = "HEADLESS";
                this.sistemaOperativo=System.getProperty("os.name");
                archivoFallidos();
            }

            log.setEjecucion(this.ejecucion);

            //abrir el navegador
            this.driver = driverConnection(navegador);
            System.out.println(driver);

            this.cpID = cpID;
            //log.addStep("Inicio de prueba " + this.cpID);

            this.timeWait = 30;
            //String data = "";

            ruteIdCAEx = Thread.currentThread().getStackTrace()[2].getClassName();
            current_browser = navegador;
            startTime = System.currentTimeMillis();
            log.setDriver(this.driver);
        }

        setDriver(this.driver, this.log);

    }



    public TestScriptWebSelenium(WebDriver driver, Log log) {
        this.driver = driver;
        this.log = log;
    }

    /**
     * Descripcion Metodo que levanta el navegador y sus opciones
     *
     * @param navegador el cual Chrome, Firefox , IE o Edge
     * @return retorna el driver levantado en el navegador llamado
     */
    public WebDriver driverConnection(String navegador) {


        HashMap<String, Object> chromePreferences = new HashMap<String, Object>();
        new File(new File("downloads").getAbsolutePath()).mkdirs();
        switch (navegador.toUpperCase()) {
            case "CHROME":
                System.setProperty("webdriver.chrome.driver", "./chromedriver.exe");
                //WebDriverManager.chromedriver().setup();
                ChromeOptions opcionesChrome = new ChromeOptions();
                opcionesChrome.addArguments("--no-sandbox");
                opcionesChrome.addArguments("--disable-dev-shm-usage");
                opcionesChrome.addArguments("--incognito");
                opcionesChrome.addArguments("--remote-allow-origins=*", "ignore-certificate-errors");
                opcionesChrome.addArguments("--disable-blink-features=AutomationControlled");

                if (ejecucion.equalsIgnoreCase("HEADLESS")) {
                    opcionesChrome.addArguments("--headless=new");
                }
                chromePreferences.put("profile.default_content_settings.popups", 0);
                chromePreferences.put("download.prompt_for_download", "false");
                chromePreferences.put("download.default_directory", new File("downloads").getAbsolutePath());
                opcionesChrome.setExperimentalOption("prefs", chromePreferences);
                opcionesChrome.addArguments("--kiosk-printing");//Evitará que se abran ventanas emergentes al hacer clic en cualquier botón de impresión
                opcionesChrome.addArguments("disable-features=DownloadBubble,DownloadBubbleV2");

                //Setear propiedades para no abrir ventana de JFileChooser

                driver = new ChromeDriver(opcionesChrome);
                break;

            case "CHROME_DEFAULT":
                try{
                    Path tempDir = Files.createTempDirectory("tempProfile");
                    String tempProfilePath = tempDir.toString();
                    String User = System.getProperty("user.home");
                    String[] arrSplit = User.split("\\\\");
                    String userMachine = arrSplit[2];
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions opcionesChrome2 = new ChromeOptions();
                    opcionesChrome2.addArguments("user-data-dir=C:"+File.separator+"Users"+File.separator+ userMachine + File.separator+"AppData"+File.separator+"Local"+File.separator+"Google"+File.separator+"Chrome"+File.separator+"User Data"+File.separator+"Default","--remote-allow-origins=*");
                    //opcionesChrome2.addArguments("user-data-dir=" + System.getenv("LOCALAPPDATA") + File.separator + "Google" + File.separator + "Chrome" + File.separator + "User Data" + File.separator + "Default", "--remote-allow-origins=*");
                    opcionesChrome2.addArguments("user-data-dir=" + tempProfilePath);
                    chromePreferences.put("profile.default_content_setting_values.clipboard", 1);
                    chromePreferences.put("profile.default_content_settings.popups", 0);
                    chromePreferences.put("download.prompt_for_download", "false");
                    chromePreferences.put("download.default_directory", new File("downloads").getAbsolutePath());
                    opcionesChrome2.setExperimentalOption("prefs", chromePreferences);
                    driver = new ChromeDriver(opcionesChrome2);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            case "FIREFOX":
                WebDriverManager.firefoxdriver().setup();
                //System.setProperty("webdriver.gecko.driver", "."+File.separator+"geckodriver.exe");
                //FirefoxBinary binary = new FirefoxBinary(new File("." + File.separator + "Navegador.exe"));
                FirefoxOptions options = new FirefoxOptions();
                ProfilesIni profileIni = new ProfilesIni();
                FirefoxProfile profile = profileIni.getProfile("default");
                profile.setPreference("browser.privatebrowsing.autostart", true);

                profile.setPreference("browser.download.folderList", 2);
                profile.setPreference("browser.download.manager.showWhenStarting", false);
                profile.setPreference("browser.download.dir", new File("downloads").getAbsolutePath());
                options.setProfile(profile);
                if (ejecucion.equalsIgnoreCase("HEADLESS")) {
                    //      options.setHeadless(true);
                }
                //    options.setBinary(binary);
                driver = new FirefoxDriver(options);
                break;

            case "IE":
                InternetExplorerOptions optionsIE = new InternetExplorerOptions().setPageLoadStrategy(PageLoadStrategy.NONE);
                optionsIE.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
                optionsIE.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
                System.setProperty("webdriver.ie.driver", "C:" + File.separator + "Workspaces" + File.separator + "RFT" + File.separator + "IEDriverServer.exe");
                driver = new InternetExplorerDriver(optionsIE);
                break;

            case "EDGE":
                HashMap<String, Object> edgePrefs = new HashMap<String, Object>();
                System.setProperty("webdriver.edge.driver", "."+File.separator+"msedgedriver.exe");
                edgePrefs.put("download.default_directory", new File("downloads").getAbsolutePath());
                edgePrefs.put("profile.default_content_setting_values.clipboard", 1);
                //     WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.setCapability("inPrivate", true);
                if (ejecucion.equalsIgnoreCase("HEADLESS")) {
                    edgeOptions.addArguments("--headless=new");
                    edgeOptions.addArguments("--no-sandbox");
                }
                edgeOptions.addArguments("--disable-dev-shm-usage");
                edgeOptions.addArguments("--disable-blink-features=AutomationControlled");
                edgeOptions.addArguments("--remote-allow-origins=*", "ignore-certificate-errors");
                edgeOptions.setExperimentalOption("prefs", edgePrefs);
                driver = new EdgeDriver();
                break;
            default:
                LOGGER.info("*******NO SE ENCONTRO NAVEGADOR EN DATAPOOL*********");
                break;
        }
        return driver;
    }


    /**
     * Descripcion Se utiliza en script para no crear nuevos llamados
     *
     * @return retorna el driver al script para usar funciones selenium como: driver.findByElement...
     */
    public WebDriver getDriver() {
        return this.driver;
    }


    /**
     * Descripción: Primer método de inicio de script.
     * 1.-Levantar Navegador señalado en DataPool(Chrome, IE, Firefox o Edge) Uso en driverConnection(String navegador);
     * 2.-Navegador se levanta como INCOGNITO.
     * 3.-Maximizado de pantalla antes de ingreso de url.
     * 4.-Ingreso de URL según portal
     */
    public void startTest(String portal) {
        try {

            if (System.getProperty("pipeline") == null || System.getProperty("pipeline").equals("")
                    || System.getProperty("pipeline").equalsIgnoreCase("false")) {
                driver.manage().window().setSize(new Dimension(1280, 720));
                driver.manage().window().maximize();
                driver.get(enviromentTest(portal));
                log.addStep("Navegando a " + portal + " en browser " + current_browser + "");
            } else if (System.getProperty("pipeline").equalsIgnoreCase("true")) {
                driver.manage().window().setSize(new Dimension(1280, 720));
                driver.get(enviromentTest(portal));
                log.addStep("Navegando a " + portal + " en browser " + current_browser + "");
            }

        } catch (Exception e) {

            System.out.println("Excepción durante la operación del inicio de Navegador " + current_browser);
            throw e;
        }
    }

    /**
     * Descripción Punto de Finalización a la ejecucion, por convención cualquier caso donde esta funcion sea llamada indica que el
     * caso termino con una ejecución correcta.
     **/
    public void endCaseOk() {
        this.ended_ok = true;
        log.addStep("Success: Caso Finalizado", false);
        generateDataPoolPassed(this.cpID);
    }

    /**
     * Descripción Método de finalización de scrip por error, generando una excepción.
     **/
    public void endCaseError(Exception e) {
        log.addStep("Fallido: Caso Finalizado", true);
        log.addStep("\n", false);
        log.addStep("Mensaje de excepción : \n" + e.getMessage(), false);
        String excep = "None";
        String[] rutaNombre = getRute();

        for (int i = 0; i < e.getStackTrace().length; i++) {
            String temp = e.getStackTrace()[i].toString();
            if (temp.contains(rutaNombre[2])) {
                excep = temp;
                break;
            }
        }

        log.addStep("Line number : \n" + excep, false);
        log.addStep("Exception name : \n" + e, false);
        log.addStep("Exception stack : \n" + e.getStackTrace()[0], false);
    }

    /**
     * Descripción Método realiza insersión de un String por medio de la definición de un locator
     *
     * @param locator Objeto al que se le escribe el texto
     * @param texto   Banco Itau
     */
    public void setText(By locator, String texto) throws Exception {
        try {
            existElementWithOutLog(locator);
            wait.until(elementToBeClickable(locator)).clear();
            driver.findElement(locator).sendKeys(texto);
            log.addStepBy("Escribe valor '" + texto + "' en: ", locator);
        } catch (Exception e) {
            throw new Exception("Test Fallido Objeto No Encontrado");
        }
    }

    /**
     * Descripción Método de finalización de scrip por error, generando una excepción.
     **/
    public void endCaseErrorETL(Exception e) {
        log.addStep("Fallido: Caso Finalizado", false);
        log.addStep("\n", false);
        log.addStep("Mensaje de excepción : \n" + e.getMessage(), false);
        String excep = "None";
        String[] rutaNombre = getRute();

        for (int i = 0; i < e.getStackTrace().length; i++) {
            String temp = e.getStackTrace()[i].toString();
            if (temp.contains(rutaNombre[2])) {
                excep = temp;
                break;
            }
        }

        log.addStep("Line number : \n" + excep, false);
        log.addStep("Exception name : \n" + e, false);
        log.addStep("Exception stack : \n" + e.getStackTrace()[0], false);
    }

    /**
     * Descripción Método realiza insersión de un String con un enter por medio de la definición de un locator
     *
     * @param locator Objeto al que se le escribe el texto
     * @param texto   texto a escribir
     * @throws Exception
     */
    public void setTextWithEnter(By locator, String texto) throws Exception {
        try {
            existElementWithOutLog(locator);
            wait.until(elementToBeClickable(locator)).clear();
            WebElement searchBox = driver.findElement(locator);
            searchBox.sendKeys(texto);
            searchBox.sendKeys(Keys.ENTER);
            log.addStepBy("Escribe valor '" + texto + "' en: ", locator);
        } catch (Exception e) {
            throw new Exception("Test Fallido Objeto No Encontrado");
        }
    }

    /**
     * Descripción Método realiza insersión de un String por medio de la definición de un locator, sin LOG
     *
     * @param locator Objeto al que se le escribe el texto
     * @param texto   Banco Itau
     */
    public void setTextWithOutLog(By locator, String texto) throws Exception {
        try {
            existElementWithOutLog(locator);
            wait.until(elementToBeClickable(locator)).clear();
            driver.findElement(locator).sendKeys(texto);
        } catch (Exception e) {
            throw new Exception("Test Fallido Objeto No Encontrado");
        }
    }


    /**
     * Descripcion Click a un elemento, validando antes que este desplegado.
     *
     * @param locator Objeto al que se realiza click
     **/
    public void click(By locator) throws Exception {
        try {
            existElementWithOutLog(locator);
            wait.until(elementToBeClickable(locator));
            log.addStepBy("Click sobre : ", locator);
            driver.findElement(locator).click();
        } catch (NoSuchElementException e) {
            throw new Exception("Test Fallido Objeto No Encontrado");
        }
    }

    public void printLogETL(String operation) {
        System.out.println(operation);
        log.addStep(operation, false);
    }

    /**
     * Descripcion: Click a un elemento, validando antes que este desplegado y realizando un hover resaltando
     *
     * @param locator
     */

    public void clickR(By locator) throws Exception {

        try {
            existElementWithOutLog(locator);
            wait.until(elementToBeClickable(locator));
            hoverR(locator);
            log.addStepBy("Click sobre : ", locator);
//			wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
            driver.findElement(locator).click();
        } catch (NoSuchElementException e) {
            throw new Exception("Test Fallido Objeto No Encontrado");
        }
    }


    /**
     * Descripcion Click a un elemento, validando antes que este desplegado.
     *
     * @param locator Objeto al que se realiza click
     **/
    public void clickWithOutLog(By locator) throws Exception {
        try {
            existElementWithOutLog(locator);
            driver.findElement(locator).click();
        } catch (NoSuchElementException e) {
            throw new Exception("Test Fallido Objeto No Encontrado");
        }
    }

    /**
     * Descripcion Click a un elemento por JS
     *
     * @param locator Objeto esperado
     */
    public void clickJs(By locator) throws Exception {
        try {
            existElementWithOutLog(locator);
            log.addStepBy("Click JS sobre : ", locator);
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("arguments[0].click();", driver.findElement(locator));
        } catch (NoSuchElementException e) {
            new Exception("Test Fallido Objeto No Encontrado");
        }
    }


    /**
     * Click a un elemento, validando antes que este desplegado y realizando un hover resaltado
     * @param locator
     */

    /**
     * Descripcion Selecciona valor de un ComboBox que tenga el TagName "Select" según el texto exacto.
     *
     * @param texto   a buscar
     * @param locator objeto esperado
     */
    public void selectComboBoxByText(By locator, String texto) throws Exception {
        try {
            boolean existe = false;
            existElementWithOutLog(locator);
            WebElement mySelectElement = driver.findElement(locator);
            Select dropdown = new Select(mySelectElement);
            List<WebElement> options = dropdown.getOptions();
            for (WebElement option : options) {
                if (option.getText().equals(texto)) {
                    wait.until(elementToBeClickable(option)).click();
                    log.addStepBy("Selección Texto Encontrado: " + texto + " ", locator);
                    existe = true;
                    break;
                }
            }
            if (!existe) {
                throw new WebDriverException();
            }
        } catch (WebDriverException e) {
            log.addStepBy("Selección Texto No Encontrado: " + texto + " ", locator);
            throw new Exception("Test Fallido Selección No Encontrada");
        }
    }

    /**
     * Descripcion Selecciona valor de un ComboBox que tenga el TagName "Select" según el index.
     *
     * @param index   valor del elemento a clickear
     * @param locator Objeto esperado
     */
    public void selectComboBoxByIndex(By locator, int index) throws Exception {
        try {
            //existElementMethod(locator);
            WebElement mySelectElement = driver.findElement(locator);
            Select dropdown = new Select(mySelectElement);
            dropdown.selectByIndex(index);
            log.addStepBy("Selección Index Encontrado: " + index + " ", locator);
        } catch (WebDriverException e) {
            log.addStepBy("Selección Index No Encontrado: " + index + " ", locator);
            throw new Exception("Test Fallido Selección Index No Encontrado");
        }
    }

    /**
     * Descripcion Selecciona valor de un ComboBox que tenga el TagName "Select" según el texto que Contenga no exacto.
     *
     * @param texto   a buscar en elemento
     * @param locator Objeto esperado
     */
    public void selectComboBoxByTextContais(By locator, String texto) throws Exception {
        try {
            boolean existe = false;
            existElementWithOutLog(locator);
            WebElement mySelectElement = driver.findElement(locator);
            Select dropdown = new Select(mySelectElement);
            List<WebElement> options = dropdown.getOptions();
            System.out.println(options);
            for (WebElement option : options) {
                if (option.getText().contains(texto)) {
                    option.click();
                    log.addStepBy("Selección Texto Encontrado: " + texto + " ", locator);
                    existe = true;
                    break;
                }
            }
            if (!existe) {
                throw new WebDriverException();
            }

        } catch (WebDriverException e) {
            log.addStepBy("Selección Texto No Encontrado: " + texto + " ", locator);
            throw new Exception("Test Fallido Selección No Encontrada");
        }
    }

    /**
     * Descripcion Selecciona valor de un ComboBox de tipo "List" según el texto que Contenga, puede ser no exacto.
     *
     * @param texto   a Buscar
     * @param locator Objeto esperado
     */
    public void selectComboBoxSP(By locator, String texto) throws Exception {
        try {
            //existElementWithOutLog(locator);
            List<WebElement> list = driver.findElements(locator);
            for (WebElement element : list) {
                System.out.println("Imprimir");
                System.out.println(element.getText());
                if (element.getText().contains(texto)) {
                    WebElement m = driver.findElement(By.xpath("//*[contains(text(),'" + texto + "')]"));
                    m.click();
                    sleep(1);
                    log.addStepBy("Selección Texto Encontrado: " + texto + " ", locator);
                } else {
                    throw new WebDriverException();
                }
            }
        } catch (WebDriverException e) {
            log.addStepBy("Selección Texto No Encontrado: " + texto + " ", locator);
            throw new Exception("Test Fallido Selección No Encontrada");
        }
    }

    /**
     * Descripcion Selecciona valor de un ComboBox de tipo "List" según el texto que Contenga, puede ser no exacto, y realiza el click con JS
     *
     * @param texto   a buscar
     * @param locator Objeto esperado
     */
    public void selectComboBoxJS(By locator, String texto) throws Exception {
        try {
            existElementWithOutLog(locator);
            List<WebElement> list = driver.findElements(locator);
            for (WebElement element : list) {
                //System.out.println(element.getText());
                if (element.getText().contains(texto)) {
                    //moveClickJs(By.xpath("//*[@class='dijitReset dijitMenuItem' and contains(.,'"+texto+"')]"));
                    log.addStepBy("Selección Texto Encontrado: " + texto + " ", locator);
                } else {
                    throw new WebDriverException();
                }
            }
        } catch (WebDriverException e) {
            log.addStepBy("Selección Texto No Encontrado: " + texto + " ", locator);
            throw new Exception("Test Fallido Selección No Encontrada");
        }
    }

    /**
     * Descripcion: Obtiene la lista a través del Locator y halla los elementos con los TagName= li
     *
     * @param locator Lista
     * @param texto   a buscar dentro de la lista
     */
    public void selectComboBoxTagName(By locator, String texto) throws Exception {
        try {
            existElementWithOutLog(locator);
            List<WebElement> list = driver.findElement(locator).findElements(By.tagName("li"));
            int contador = 0;
            System.out.println(list.size());
            for (WebElement element : list) {
                System.out.println(element.getText());
                if (element.getText().contains(texto)) {
                    element.click();
                    sleep(1);
                    log.addStepBy("Selección Texto Encontrado: " + texto + " ", locator);
                    break;
                } else {
                    contador++;
                    if (contador == list.size()) {
                        throw new WebDriverException();
                    }
                }
            }
        } catch (WebDriverException e) {
            log.addStepBy("Selección Texto No Encontrado: " + texto + " ", locator);
            throw new Exception("Test Fallido Selección No Encontrada");
        }
    }

    /**
     * Descripcion Posiciona el puntero del mouse sobre elemento entregado.
     *
     * @param locator Objeto esperado
     */
    public void hover(By locator) throws Exception {

        existElementWithOutLog(locator);
        Actions actions = new Actions(driver);
        actions.moveToElement(driver.findElement(locator)).perform();
        log.addStepBy("Posicionando sobre : ", locator);
    }

    /***
     * Descripcion: Posiciona el puntero del mouse sobre elemento entregado y resalta con recuadro
     * @param locator
     */
    public void hoverR(By locator) throws Exception {
        existElementWithOutLog(locator);
        Actions actions = new Actions(driver);
        actions.moveToElement(driver.findElement(locator)).perform();
        WebElement element = driver.findElement(locator);
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("arguments[0].style.border='5px solid red'", element);
        sleep(1);
        log.addStepBy("Posicionando sobre : ", locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].style.border=''", element);
        return;
    }


    /**
     * Descripcion Posiciona el puntero del mouse sobre elemento entregado, sin dejar Log.
     *
     * @param locator Objeto esperado
     */
    public void hoverWithOutLog(By locator) throws Exception {
        existElementWithOutLog(locator);
        Actions actions = new Actions(driver);
        actions.moveToElement(driver.findElement(locator)).perform();
    }

    /**
     * Descripcion Se usa para esperar objeto con tiempos prolongados
     *
     * @param locators Objeto esperado
     * @param seg      tiempo maximo de espera
     */
    public void waitLocator(By locators, int seg) throws Exception {
        int x = 0;
        do {
            //sleep(0.5);
            if (existElementWithOutException(locators)) {
                break;
            } else {
                x++;
            }
        } while (x < seg);
    }

    /**
     * Descripcion Solo para Metodos sin registro de LOG que valida si un objeto esta desplegado en pantalla
     *
     * @param locator Objeto esperado
     */
    public Boolean existElementWithOutLog(By locator) throws Exception {
        boolean exist = false;
        try {
            wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            if (wait.until(visibilityOfElementLocated(locator)).isDisplayed()) {
                exist = true;
            } else {
                throw new Exception("Exception message: elemto no desplegado " + locator.toString());
            }
        } catch (TimeoutException e) {
            throw new TimeoutException("Test Fallido Objeto No Encontrado");
        }
        return exist;
    }

    /**
     * Descripcion Metodo SOBRECARGADO que valida si un objeto esta desplegado en pantalla
     * si el objeto no existe, NO SE LEVANTA LA EXCEPCIÓN.
     *
     * @param locator Objeto esperado
     * @return exist
     */
    public Boolean existElementWithOutException(By locator) {
        boolean exist = false;
        try {
            wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            if (wait.until(visibilityOfElementLocated(locator)).isDisplayed()) {
                exist = true;
                log.addStepBy("Elemento Encontrado : ", locator);
            } else {
                log.addStepBy("Elemento No Desplegado : ", locator);
            }
        } catch (TimeoutException e) {
            log.addStepBy("Elemento NO Encontrado : ", locator);
            exist = false;
        }
        return exist;
    }


    /**
     * Descripcion Metodo SOBRECARGADO que valida si un objeto esta desplegado en pantalla
     * si el objeto no existe, NO SE LEVANTA LA EXCEPCIÓN.
     *
     * @param locator Objeto esperado
     * @param time    tiempo de espera
     * @return exist
     */
    public Boolean existElementWithOutException(By locator, int time) {
        boolean exist = false;
        try {
            wait = new WebDriverWait(driver, Duration.ofSeconds(time));
            if (wait.until(visibilityOfElementLocated(locator)).isDisplayed()) {
                exist = true;
            }
        } catch (TimeoutException e) {
            exist = false;
        }
        return exist;
    }

    /**
     * Descripcion Metodo que valida si un objeto esta desplegado en pantalla, segun el parametro de tiempo dado.
     *
     * @param locator objeto esperado
     * @param seg     a esperar
     */
    public Boolean existElement(By locator, int seg) throws Exception {
        boolean exist = false;
        try {
            wait = new WebDriverWait(driver, Duration.ofSeconds(seg));
            if (wait.until(visibilityOfElementLocated(locator)).isDisplayed()) {
                exist = true;
                log.addStepBy("Elemento Encontrado : ", locator);
            } else {
                log.addStepBy("Elemento No Desplegado : ", locator);
                throw new Exception("Test Fallido Objeto No Desplegado");
            }
        } catch (TimeoutException e) {
            log.addStep("Error TimeOut Tiempo : " + seg + " Seg.");
            log.addStepBy("Elemento NO Encontrado : ", locator);
            throw new Exception("Test Fallido Objeto No Encontrado");
        }
        return exist;
    }


    /**
     * Se agrega un tercer parametro a este metodo para saber el nombre del elemento a identificar
     *
     * @param locator        Locator elemento a encontrar
     * @param seg            tiempo a esperar por el elemento
     * @param nombreElemento Nombre del elemento a encontrar
     * @return
     * @throws Exception
     */
    public Boolean existeElemento(By locator, int seg, String nombreElemento) throws Exception {
        boolean exist = false;
        try {
            wait = new WebDriverWait(driver, Duration.ofSeconds(seg));
            if (wait.until(visibilityOfElementLocated(locator)).isDisplayed()) {
                exist = true;
                log.addStepBy("Elemento Encontrado : " + nombreElemento + " ", locator);
            } else {
                log.addStepBy("Elemento No Desplegado : " + nombreElemento + " ", locator);
                throw new Exception("Test Fallido Objeto No Desplegado");
            }
        } catch (TimeoutException e) {
            log.addStep("Error TimeOut Tiempo : " + seg + " Seg.");
            log.addStepBy("Elemento NO Encontrado : " + nombreElemento + " ", locator);
            throw new Exception("Test Fallido Objeto No Encontrado");
        }
        return exist;
    }

    /**
     * Metodo para validar si exite un elemento sin generar evidencia
     *
     * @param locator Elemento a encontrar
     * @param seg     Tiempo en segundos a esperar por el elemento
     * @return boolean exist
     * @throws Exception
     */
    public Boolean existeElementoSinEvidencia(By locator, int seg) throws Exception {
        boolean exist = false;
        try {
            wait = new WebDriverWait(driver, Duration.ofSeconds(seg));
            wait.until(visibilityOfElementLocated(locator)).isDisplayed();
            exist = true;
        } catch (TimeoutException e) {
            throw new Exception("Test Fallido Objeto No Encontrado");
        } finally {
            return exist;
        }
    }

    /**
     * Descripcion Imprime texto y lo añade al log.
     */
    public void printLog(String operation) {
        System.out.println(operation);
        log.addStep(operation, false);
    }

    /**
     * Descripcion Metodo que valida si un objeto esta desplegado en pantalla, segun el parametro de tiempo dado.
     */
    public void printError(String operation) {
        System.err.println(operation);
        log.addStep(operation, false);
    }

    /**
     * Descripción: Metodo que genera la evidencia en PDF
     */
    public void saveLog() {
        endTime = System.currentTimeMillis();
        String[] rutaNombre = null;
        String platform = "";

        rutaNombre = getRute();
        platform = rutaNombre[1];


        platform = platform.substring(0, 1).toUpperCase() + platform.substring(1).toLowerCase();
        int longitudRuta = rutaNombre.length;
        String testCase = "";
        String name = "";

        if(cpID.equalsIgnoreCase(rutaNombre[longitudRuta - 1]))
        {
            name = rutaNombre[longitudRuta - 1];
        }
        else if (this.testName.equalsIgnoreCase("")) {

            name = cpID + "_" + rutaNombre[longitudRuta - 1];
            nameRegresion=true;

        } else if (nombreExterno) {
            name = cpID + "_"  + testName;
            nameRegresion=true;

        }


        else {
            name = cpID + "_" + rutaNombre[longitudRuta - 1] + "." + testName;
            nameRegresion=true;

        }

        try {
            if (!(System.getProperty("pipeline") == null) && System.getProperty("pipeline").equalsIgnoreCase("true")) {
                if (rutaNombre[0].equalsIgnoreCase("comunidades")) {
                    testCase = rutaNombre[longitudRuta - 1];
                    System.out.println(testCase);
                } else if (rutaNombre[0].equalsIgnoreCase("regresion")) {
                    testCase = rutaNombre[3];
                }
            }
        } catch (Exception e) {
            System.out.println("Falso");
        }

        try {
            if (evidencia.equalsIgnoreCase("true") && (current_browser.toUpperCase().equals("CHROME") || current_browser.toUpperCase().equals("IE") ||
                    current_browser.toUpperCase().equals("FIREFOX") || current_browser.toUpperCase().equals("EDGE") ||
                    current_browser.toUpperCase().equals("CHROME_DEFAULT"))) {
                writeLogToTxtPipeLine(platform, name, current_browser);
                writeLogToPdfPipeline(platform, name, current_browser);
                log.finTestCase(testCase, this.ended_ok);
            } else if (evidencia.equalsIgnoreCase("false") && (current_browser.toUpperCase().equals("CHROME") || current_browser.toUpperCase().equals("IE") ||
                    current_browser.toUpperCase().equals("FIREFOX") || current_browser.toUpperCase().equals("EDGE") ||
                    current_browser.toUpperCase().equals("CHROME_DEFAULT"))) {
                writeLogToTxt(platform, name, current_browser);
                writeLogToPdf(platform, name, current_browser);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        switch (current_browser.toUpperCase()) {

            case "CHROME_DEFAULT":
            case "CHROME":
                driver.close();
                driver.quit();
                if  ((System.getProperty("paralelo") == null )) {
                    try {
                        Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
                        Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe /T");
                        Runtime.getRuntime().exec("taskkill /F /IM runtimebroker.exe /T");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "IE":
                if  ((System.getProperty("paralelo") == null )) {
                    try {
                        Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "FIREFOX":
                driver.quit();
                if  ((System.getProperty("paralelo") == null )) {
                    try {
                        Runtime.getRuntime().exec("taskkill /F /IM geckodriver.exe /T");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "EDGE":
                driver.quit();
                if  ((System.getProperty("paralelo") == null )) {
                    try {
                        Runtime.getRuntime().exec("taskkill /F /IM msedgedriver.exe");
                        Runtime.getRuntime().exec("taskkill /F /IM runtimebroker.exe /T");
                        //Runtime.getRuntime().exec("taskkill /F /IM msedge.exe /T");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            default:
                break;
        }
        log = null;
    }

    /**
     * Descripción: Metodo que genera la evidencia en TXT
     */
    public void writeLogToTxt(String platform, String className, String nav) {
        boolean noLines = false;
        boolean valid_brower = nav.equalsIgnoreCase("CHROME") || nav.equalsIgnoreCase("IE")
                || nav.equalsIgnoreCase("FIREFOX") || nav.equalsIgnoreCase("EDGE") || nav.equalsIgnoreCase("CHROME_DEFAULT");
        // Try one more time to find the current browser
        if (!valid_brower)
            //	nav = getBrowser();
            nav = current_browser;

        String fecha = getFormatDate("'_'YYYY'_'MM'_'dd");
        String directoryName = "." + File.separator + "Evidencia" + File.separator + platform + File.separator + nav;
        String fileName = className + fecha + ".txt";

        String[] carpetaRuta = className.split("_");
        String nameCarpeta = (nameRegresion) ? carpetaRuta[3] : carpetaRuta[0];

        switch (nameCarpeta) {

            case "CRE":
                nameCarpeta = "Creditos";
                directoryName = directoryName + File.separator + nameCarpeta;
                break;
            default:
                directoryName = directoryName + File.separator + nameCarpeta;
                break;
        }

        File directory = new File(directoryName);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        try {
            // Delete the file first to save a new file with new log information
            File evidence_file = new File(directory.getAbsolutePath() + File.separator + fileName);
            Files.deleteIfExists(evidence_file.toPath());

            // Then Create new empty file
            evidence_file.createNewFile();

            // Then write that file
            PrintWriter pw = new PrintWriter(directoryName + File.separator + fileName);
            for (int i = 0; i < log.getTextLog().size(); i++) {
                if (log.getTextLog().get(i).contains("Fail"))
                    noLines = true;
                if (noLines)
                    pw.println(log.getTextLog().get(i));
                else
                    pw.println("Paso " + (i + 1) + " - " + log.getTextLog().get(i));
            }
            pw.close();


        } catch (IOException e) {
            e.printStackTrace();

        }
    }


    /**
     * Descripción: Metodo que genera y escribe la evidencia en PDF en Carpeta Evidencia_Pipeline
     */
    public void writeLogToTxtPipeLine(String platform, String className, String nav) {
        boolean noLines = false;
        boolean valid_brower = nav.equalsIgnoreCase("CHROME") || nav.equalsIgnoreCase("IE")
                || nav.equalsIgnoreCase("FIREFOX") || nav.equalsIgnoreCase("EDGE") || nav.equalsIgnoreCase("CHROME_DEFAULT");
        // Try one more time to find the current browser
        if (!valid_brower)
            nav = current_browser;

        String fecha = getFormatDate("'_'YYYY'_'MM'_'dd");
        String directoryName = "." + File.separator + "Evidencia_Pipeline";
        String fileName = className + fecha + ".txt";

        File directory = new File(directoryName);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        try {
            // Delete the file first to save a new file with new log information
            File evidence_file = new File(directory.getAbsolutePath() + File.separator + fileName);
            Files.deleteIfExists(evidence_file.toPath());

            // Then Create new empty file
            evidence_file.createNewFile();

            // Then write that file
            PrintWriter pw = new PrintWriter(directoryName + File.separator + fileName);
            for (int i = 0; i < log.getTextLog().size(); i++) {
                if (log.getTextLog().get(i).contains("Fail"))
                    noLines = true;
                if (noLines)
                    pw.println(log.getTextLog().get(i));
                else
                    pw.println("Paso " + (i + 1) + " - " + log.getTextLog().get(i));
            }
            pw.close();


        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    /**
     * Descripción: Metodo que genera y escribe la evidencia en PDF
     */
    public void writeLogToPdf(String platform, String className, String nav) {
        boolean noLines = false;
        boolean valid_brower = nav.equalsIgnoreCase("CHROME") || nav.equalsIgnoreCase("IE") || nav.equalsIgnoreCase("FIREFOX")
                || nav.equalsIgnoreCase("EDGE") || nav.equalsIgnoreCase("CHROME_DEFAULT");
        // Try one more time to find the current browser
        if (!valid_brower) {
            //	nav = getBrowser();
            nav = current_browser;
        }
        String fecha = getFormatDate("'_'YYYY'_'MM'_'dd");
        String directoryName = "." + File.separator + "Evidencia" + File.separator + platform + File.separator + nav;
        String fileName = className + fecha + ".pdf";

        String[] carpetaRuta = className.split("_");
        String nameCarpeta = (nameRegresion) ? carpetaRuta[3] : carpetaRuta[0];

        switch (nameCarpeta) {

            case "CRE":
                nameCarpeta = "Creditos";
                directoryName = directoryName + File.separator + nameCarpeta;
                break;
            default:
                directoryName = directoryName + File.separator + nameCarpeta;
                break;
        }

        File directory = new File(directoryName);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        try {

            // Delete the file first to save a new file with new log information
            File evidence_file = new File(directory.getAbsolutePath() + File.separator + fileName);
            Files.deleteIfExists(evidence_file.toPath());

            // Configure the pdf document
            Document document = new Document(PageSize.A4, 20, 20, 5, 20);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(evidence_file));
            PdfContentByte pdfCB = new PdfContentByte(writer);
            document.open();

            // Count how many step with images are added to the document
            int step_screen_count = 0;

            // Write Presentation in First Page
            document.newPage();

            BufferedImage bf_bar = ImageIO
                    .read(new File("." + File.separator + "resources" + File.separator + "bar.png"));
            Image bar = Image.getInstance(pdfCB, bf_bar, 1);
            bar.setAlignment(Image.ALIGN_CENTER);
            bar.scalePercent(25);
            document.add(bar);

            Font fontTitle = FontFactory.getFont(FontFactory.COURIER_BOLD, 24, BaseColor.BLACK);
            Paragraph helper_paragraph = new Paragraph("INFORME EVIDENCIA PRUEBA AUTOMATIZADA", fontTitle);
            helper_paragraph.setAlignment(Paragraph.ALIGN_CENTER);
            helper_paragraph.setSpacingAfter(100);
            document.add(helper_paragraph);

            fontTitle = FontFactory.getFont(FontFactory.COURIER, 22, BaseColor.BLACK);
            document.add(new Paragraph("Plataforma : " + " Itaú - " + platform, fontTitle));
            document.add(new Paragraph("Caso: " + className, fontTitle));
            document.add(new Paragraph("Browser: " + this.current_browser, fontTitle));
            document.add(new Paragraph("Fecha: " + fecha, fontTitle));
            document.add(new Paragraph("Host: " + Inet4Address.getLocalHost().getHostAddress(), fontTitle));

            // Add other information to the report
            Date startDate = new Date(startTime);
            Date endDate = new Date(endTime);
            DateFormat df = new SimpleDateFormat("HH:mm:ss");
            long elipseTime = endTime - startTime;
            long elipseSeconds = elipseTime / 1000;
            long min = elipseSeconds / 60;
            long sec = elipseSeconds % 60;
            document.add(new Paragraph("Hora Inicio: " + df.format(startDate), fontTitle));
            document.add(new Paragraph("Hora Fin: " + df.format(endDate), fontTitle));
            document.add(new Paragraph("Tiempo de Ejecución : " + min + " Min " + sec + " Segundos", fontTitle));
            document.add(new Paragraph("Resultado: ", fontTitle));

            boolean success = false;
            for (int i = 0; i < log.getTextLog().size(); i++) {
                if (log.getTextLog().get(i).contains("Success:"))
                    success = true;
            }

            if (success) {
                BufferedImage bfstampok = ImageIO
                        .read(new File("." + File.separator + "resources" + File.separator + "stampok.png"));
                Image stampok = Image.getInstance(pdfCB, bfstampok, 1);
                stampok.setAlignment(Image.ALIGN_CENTER);
                stampok.scalePercent(35);
                document.add(stampok);
            } else {
                BufferedImage bfstampnook = ImageIO
                        .read(new File("." + File.separator + "resources" + File.separator + "stampnook.png"));
                Image stampnook = Image.getInstance(pdfCB, bfstampnook, 1);
                stampnook.setAlignment(Image.ALIGN_CENTER);
                stampnook.scalePercent(35);
                document.add(stampnook);
            }

            // Create Footer of Presentation
            fontTitle = FontFactory.getFont(FontFactory.COURIER_OBLIQUE, 10, BaseColor.GRAY);
            Paragraph footer = new Paragraph("Generado Dinámicamente por Regresion Manager & Caex Automation Framework",
                    fontTitle);
            footer.setAlignment(Paragraph.ALIGN_CENTER);
            footer.setSpacingBefore(60);
            document.add(footer);

            // Create the second page
            document.newPage();

            // Then write content the pdf file
            Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
            for (int i = 0; i < log.getTextLog().size(); i++) {

                if (log.getTextLog().get(i).contains("Fail"))
                    noLines = true;

                if (noLines) {
                    document.add(new Paragraph(log.getTextLog().get(i), font));
                    try {
                        BufferedImage img = log.getImageLog().get(i);
                        Image image = Image.getInstance(pdfCB, img, 1);
                        image.scalePercent(35);
                        image.setAlignment(Image.ALIGN_CENTER);
                        image.setSpacingBefore(100);
                        document.add(image);
                    } catch (Exception e) {
                        // capture exception thrown as result of no screenshot
                        // for the step, in this case do nothing
                        // System.out.println("NO IMAGE FOR ACTION "+ i);
                    }
                } else {
                    if (step_screen_count % 2 == 0 && step_screen_count != 0 && (log.getImageLog().size() > 2))
                        document.newPage();
                    document.add(new Paragraph("Paso " + (i + 1) + " - " + log.getTextLog().get(i), font));
                    step_screen_count++;
                    try {
                        // Every two steps add a new page
                        BufferedImage img = log.getImageLog().get(i);
                        Image image = Image.getInstance(pdfCB, img, 1);
                        image.scalePercent(35);
                        image.setAlignment(Image.ALIGN_CENTER);
                        image.setSpacingBefore(100);
                        document.add(image);
                    } catch (Exception e) {
                        // capture exception thrown as result of no screenshot
                        // for the step, in this case do nothing
                        // System.out.println("NO IMAGE FOR ACTION "+ i);
                    }
                }
            }

            //Add stamp to the final page also
            if (success) {
                BufferedImage bfstampok = ImageIO
                        .read(new File("." + File.separator + "resources" + File.separator + "stampok.png"));
                Image stampok = Image.getInstance(pdfCB, bfstampok, 1);
                stampok.setAlignment(Image.ALIGN_CENTER);
                stampok.scalePercent(35);
                document.add(stampok);
            } else {
                BufferedImage bfstampnook = ImageIO
                        .read(new File("." + File.separator + "resources" + File.separator + "stampnook.png"));
                Image stampnook = Image.getInstance(pdfCB, bfstampnook, 1);
                stampnook.setAlignment(Image.ALIGN_CENTER);
                stampnook.scalePercent(35);
                document.add(stampnook);
            }

            // Create Footer of Presentation
            fontTitle = FontFactory.getFont(FontFactory.COURIER_OBLIQUE, 10, BaseColor.GRAY);
            footer.setAlignment(Paragraph.ALIGN_CENTER);
            footer.setSpacingBefore(60);
            document.add(footer);

            // Add table resumen
            if (log.getResumeInfo().size() > 0) {
                PdfPTable table_resumen = new PdfPTable(2); // 2 columns.
                float[] columnWidths = {3f, 1f};
                table_resumen.setWidths(columnWidths);
                table_resumen.setWidthPercentage(90);
                for (String[] row : log.getResumeInfo()) {
                    int i = 1;
                    for (String value : row) {
                        if (i % 2 == 0) {
                            PdfPCell cell_data = new PdfPCell(new Paragraph(value));
                            table_resumen.addCell(cell_data);
                        } else {
                            PdfPCell cell_data = new PdfPCell(new Paragraph(value));
                            cell_data.setBackgroundColor(BaseColor.YELLOW);
                            cell_data.setBorder(Rectangle.NO_BORDER);
                            table_resumen.addCell(cell_data);
                        }

                    }
                }
                document.add(table_resumen);
            }

            // Add table info
            if (log.getTableInfo().size() > 0) {
                Font font1 = new Font(Font.FontFamily.COURIER, 10);
                Font font2 = new Font(Font.FontFamily.COURIER, 14, Font.BOLD, BaseColor.WHITE);
                Font font3 = new Font(Font.FontFamily.COURIER, 16, Font.BOLD);
                PdfPTable table_info = new PdfPTable(6); // 6 columns.
                table_info.setWidthPercentage(90);
                // headers
                PdfPCell cell1 = new PdfPCell(new Paragraph("RUT", font2));
                cell1.setBackgroundColor(BaseColor.DARK_GRAY);
                PdfPCell cell2 = new PdfPCell(new Paragraph("ACCOUNT", font2));
                cell2.setBackgroundColor(BaseColor.DARK_GRAY);
                PdfPCell cell3 = new PdfPCell(new Paragraph("TYPE", font2));
                cell3.setBackgroundColor(BaseColor.DARK_GRAY);
                PdfPCell cell4 = new PdfPCell(new Paragraph("BALANCE BEFORE", font2));
                cell4.setBackgroundColor(BaseColor.DARK_GRAY);
                PdfPCell cell5 = new PdfPCell(new Paragraph("BALANCE AFTER", font2));
                cell5.setBackgroundColor(BaseColor.DARK_GRAY);
                PdfPCell cell6 = new PdfPCell(new Paragraph("RECHARGED", font2));
                cell6.setBackgroundColor(BaseColor.DARK_GRAY);
                table_info.addCell(cell1);
                table_info.addCell(cell2);
                table_info.addCell(cell3);
                table_info.addCell(cell4);
                table_info.addCell(cell5);
                table_info.addCell(cell6);

                for (String[] row : log.getTableInfo()) {
                    for (String value : row) {
                        PdfPCell cell_data = new PdfPCell(new Paragraph(value, font1));
                        table_info.addCell(cell_data);
                    }
                    if (row[row.length - 1].equalsIgnoreCase(row[row.length - 2])) {
                        PdfPCell cell_data = new PdfPCell(new Paragraph("NO", font3));
                        cell_data.setBackgroundColor(BaseColor.RED);
                        cell_data.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table_info.addCell(cell_data);
                    } else {
                        PdfPCell cell_data = new PdfPCell(new Paragraph("YES", font3));
                        cell_data.setBackgroundColor(BaseColor.GREEN);
                        cell_data.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table_info.addCell(cell_data);
                    }
                }
                document.add(table_info);
            }
            document.close();

        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            //kill(true);
        }
    }

    /**
     * Descripción: Metodo que genera y escribe la evidencia en PDF en carpeta Evidencia_pipeline
     */
    public void writeLogToPdfPipeline(String platform, String className, String nav) {
        boolean noLines = false;
        boolean valid_brower = nav.equalsIgnoreCase("CHROME") || nav.equalsIgnoreCase("IE") || nav.equalsIgnoreCase("FIREFOX")
                || nav.equalsIgnoreCase("EDGE") || nav.equalsIgnoreCase("CHROME_DEFAULT");

        if (!valid_brower) {

            nav = current_browser;
        }
        String fecha = getFormatDate("'_'YYYY'_'MM'_'dd");
        String directoryName = "." + File.separator + "Evidencia_Pipeline";
        String fileName = className + fecha + ".pdf";

        File directory = new File(directoryName);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        try {

            // Delete the file first to save a new file with new log information
            File evidence_file = new File(directory.getAbsolutePath() + File.separator + fileName);
            Files.deleteIfExists(evidence_file.toPath());

            // Configure the pdf document
            Document document = new Document(PageSize.A4, 20, 20, 5, 20);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(evidence_file));
            PdfContentByte pdfCB = new PdfContentByte(writer);
            document.open();

            // Count how many step with images are added to the document
            int step_screen_count = 0;

            // Write Presentation in First Page
            document.newPage();

            BufferedImage bf_bar = ImageIO
                    .read(new File("." + File.separator + "resources" + File.separator + "bar.png"));
            Image bar = Image.getInstance(pdfCB, bf_bar, 1);
            bar.setAlignment(Image.ALIGN_CENTER);
            bar.scalePercent(25);
            document.add(bar);

            Font fontTitle = FontFactory.getFont(FontFactory.COURIER_BOLD, 24, BaseColor.BLACK);
            Paragraph helper_paragraph = new Paragraph("INFORME EVIDENCIA PRUEBA AUTOMATIZADA", fontTitle);
            helper_paragraph.setAlignment(Paragraph.ALIGN_CENTER);
            helper_paragraph.setSpacingAfter(100);
            document.add(helper_paragraph);

            fontTitle = FontFactory.getFont(FontFactory.COURIER, 22, BaseColor.BLACK);
            document.add(new Paragraph("Plataforma : " + " Itaú - " + platform, fontTitle));
            document.add(new Paragraph("Caso: " + className, fontTitle));
            document.add(new Paragraph("Browser: " + this.current_browser, fontTitle));
            document.add(new Paragraph("Fecha: " + fecha, fontTitle));
            document.add(new Paragraph("Host: " + Inet4Address.getLocalHost().getHostAddress(), fontTitle));

            // Add other information to the report
            Date startDate = new Date(startTime);
            Date endDate = new Date(endTime);
            DateFormat df = new SimpleDateFormat("HH:mm:ss");
            long elipseTime = endTime - startTime;
            long elipseSeconds = elipseTime / 1000;
            long min = elipseSeconds / 60;
            long sec = elipseSeconds % 60;
            document.add(new Paragraph("Hora Inicio: " + df.format(startDate), fontTitle));
            document.add(new Paragraph("Hora Fin: " + df.format(endDate), fontTitle));
            document.add(new Paragraph("Tiempo de Ejecución : " + min + " Min " + sec + " Segundos", fontTitle));
            document.add(new Paragraph("Resultado: ", fontTitle));

            boolean success = false;
            for (int i = 0; i < log.getTextLog().size(); i++) {
                if (log.getTextLog().get(i).contains("Success:"))
                    success = true;
            }

            if (success) {
                BufferedImage bfstampok = ImageIO
                        .read(new File("." + File.separator + "resources" + File.separator + "stampok.png"));
                Image stampok = Image.getInstance(pdfCB, bfstampok, 1);
                stampok.setAlignment(Image.ALIGN_CENTER);
                stampok.scalePercent(35);
                document.add(stampok);
            } else {
                BufferedImage bfstampnook = ImageIO
                        .read(new File("." + File.separator + "resources" + File.separator + "stampnook.png"));
                Image stampnook = Image.getInstance(pdfCB, bfstampnook, 1);
                stampnook.setAlignment(Image.ALIGN_CENTER);
                stampnook.scalePercent(35);
                document.add(stampnook);
            }

            // Create Footer of Presentation
            fontTitle = FontFactory.getFont(FontFactory.COURIER_OBLIQUE, 10, BaseColor.GRAY);
            Paragraph footer = new Paragraph("Generado Dinámicamente por Regresion Manager & Caex Automation Framework",
                    fontTitle);
            footer.setAlignment(Paragraph.ALIGN_CENTER);
            footer.setSpacingBefore(60);
            document.add(footer);

            // Create the second page
            document.newPage();

            // Then write content the pdf file
            Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
            for (int i = 0; i < log.getTextLog().size(); i++) {

                if (log.getTextLog().get(i).contains("Fail"))
                    noLines = true;

                if (noLines) {
                    document.add(new Paragraph(log.getTextLog().get(i), font));
                    try {
                        BufferedImage img = log.getImageLog().get(i);
                        Image image = Image.getInstance(pdfCB, img, 1);
                        image.scalePercent(35);
                        image.setAlignment(Image.ALIGN_CENTER);
                        image.setSpacingBefore(100);
                        document.add(image);
                    } catch (Exception e) {
                        // capture exception thrown as result of no screenshot
                        // for the step, in this case do nothing
                        //System.out.println("NO IMAGE FOR ACTION "+ i);
                    }
                } else {
                    if (step_screen_count % 2 == 0 && step_screen_count != 0 && (log.getImageLog().size() > 2))
                        document.newPage();
                    document.add(new Paragraph("Paso " + (i + 1) + " - " + log.getTextLog().get(i), font));
                    step_screen_count++;
                    try {
                        // Every two steps add a new page
                        BufferedImage img = log.getImageLog().get(i);
                        Image image = Image.getInstance(pdfCB, img, 1);
                        image.scalePercent(35);
                        image.setAlignment(Image.ALIGN_CENTER);
                        image.setSpacingBefore(100);
                        document.add(image);
                    } catch (Exception e) {
                        // capture exception thrown as result of no screenshot
                        // for the step, in this case do nothing
                        // System.out.println("NO IMAGE FOR ACTION "+ i);
                    }
                }
            }

            //Add stamp to the final page also
            if (success) {
                BufferedImage bfstampok = ImageIO
                        .read(new File("." + File.separator + "resources" + File.separator + "stampok.png"));
                Image stampok = Image.getInstance(pdfCB, bfstampok, 1);
                stampok.setAlignment(Image.ALIGN_CENTER);
                stampok.scalePercent(35);
                document.add(stampok);
            } else {
                BufferedImage bfstampnook = ImageIO
                        .read(new File("." + File.separator + "resources" + File.separator + "stampnook.png"));
                Image stampnook = Image.getInstance(pdfCB, bfstampnook, 1);
                stampnook.setAlignment(Image.ALIGN_CENTER);
                stampnook.scalePercent(35);
                document.add(stampnook);

            }

            // Create Footer of Presentation
            fontTitle = FontFactory.getFont(FontFactory.COURIER_OBLIQUE, 10, BaseColor.GRAY);
            footer.setAlignment(Paragraph.ALIGN_CENTER);
            footer.setSpacingBefore(60);
            document.add(footer);

            // Add table resumen
            if (log.getResumeInfo().size() > 0) {
                PdfPTable table_resumen = new PdfPTable(2); // 2 columns.
                float[] columnWidths = {3f, 1f};
                table_resumen.setWidths(columnWidths);
                table_resumen.setWidthPercentage(90);
                for (String[] row : log.getResumeInfo()) {
                    int i = 1;
                    for (String value : row) {
                        if (i % 2 == 0) {
                            PdfPCell cell_data = new PdfPCell(new Paragraph(value));
                            table_resumen.addCell(cell_data);
                        } else {
                            PdfPCell cell_data = new PdfPCell(new Paragraph(value));
                            cell_data.setBackgroundColor(BaseColor.YELLOW);
                            cell_data.setBorder(Rectangle.NO_BORDER);
                            table_resumen.addCell(cell_data);
                        }

                    }
                }
                document.add(table_resumen);
            }

            // Add table info
            if (log.getTableInfo().size() > 0) {
                Font font1 = new Font(Font.FontFamily.COURIER, 10);
                Font font2 = new Font(Font.FontFamily.COURIER, 14, Font.BOLD, BaseColor.WHITE);
                Font font3 = new Font(Font.FontFamily.COURIER, 16, Font.BOLD);
                PdfPTable table_info = new PdfPTable(6); // 6 columns.
                table_info.setWidthPercentage(90);
                // headers
                PdfPCell cell1 = new PdfPCell(new Paragraph("RUT", font2));
                cell1.setBackgroundColor(BaseColor.DARK_GRAY);
                PdfPCell cell2 = new PdfPCell(new Paragraph("ACCOUNT", font2));
                cell2.setBackgroundColor(BaseColor.DARK_GRAY);
                PdfPCell cell3 = new PdfPCell(new Paragraph("TYPE", font2));
                cell3.setBackgroundColor(BaseColor.DARK_GRAY);
                PdfPCell cell4 = new PdfPCell(new Paragraph("BALANCE BEFORE", font2));
                cell4.setBackgroundColor(BaseColor.DARK_GRAY);
                PdfPCell cell5 = new PdfPCell(new Paragraph("BALANCE AFTER", font2));
                cell5.setBackgroundColor(BaseColor.DARK_GRAY);
                PdfPCell cell6 = new PdfPCell(new Paragraph("RECHARGED", font2));
                cell6.setBackgroundColor(BaseColor.DARK_GRAY);
                table_info.addCell(cell1);
                table_info.addCell(cell2);
                table_info.addCell(cell3);
                table_info.addCell(cell4);
                table_info.addCell(cell5);
                table_info.addCell(cell6);

                for (String[] row : log.getTableInfo()) {
                    for (String value : row) {
                        PdfPCell cell_data = new PdfPCell(new Paragraph(value, font1));
                        table_info.addCell(cell_data);
                    }
                    if (row[row.length - 1].equalsIgnoreCase(row[row.length - 2])) {
                        PdfPCell cell_data = new PdfPCell(new Paragraph("NO", font3));
                        cell_data.setBackgroundColor(BaseColor.RED);
                        cell_data.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table_info.addCell(cell_data);
                    } else {
                        PdfPCell cell_data = new PdfPCell(new Paragraph("YES", font3));
                        cell_data.setBackgroundColor(BaseColor.GREEN);
                        cell_data.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table_info.addCell(cell_data);
                    }
                }
                document.add(table_info);
            }
            document.close();

        } catch (IOException | DocumentException e) {
            e.printStackTrace();

        }
    }


    /**
     * Descripcion Cambio de Frame
     *
     * @param frame el cual es el nombre del iframe al cual se debe switchear el driver
     * @return retorna el driver switcheado al iframe indicado
     */
    public WebDriver switchToiframe(String frame) {
        driver.switchTo().frame(frame);
        return driver;
    }

    /**
     * Descripcion Cambio de Frame
     *
     * @param index el cual es el index del iframe al cual se debe switchear el driver
     * @return retorna el driver switcheado al iframe indicado
     */
    public WebDriver switchToiframeByIndex(int index) {
        driver.switchTo().frame(index);
        return driver;
    }

    /**
     * Descripción: Cambio de iFrame a default.
     */
    public void switchDefaultIFrame() {
        driver.switchTo().defaultContent();
    }

    /**
     * Descripción: Método valida la carga completa de una página.
     */
    public void existPage() throws Exception {
        ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString().equals("complete");
            }
        };
        try {
            wait = new WebDriverWait(driver, Duration.ofSeconds(60));
            if (wait.until(expectation)) {
                log.addStep("Carga de Página: COMPLETA");
            }

        } catch (Exception error) {
            throw new Exception("Test Fallido - Página no cargada");
        }
    }

    /**
     * @Deprecado Descripcion Metodo Ingresar el icode el cual es seteado con el valor 11 11 11
     */
    public void icodeByID() throws Exception {

        String[] icode_input = {"iCode1_value", "iCode2_value", "iCode3_value"};
        for (int i = 0; i < 3; i++)
            try {
                WebElement icode = driver.findElement(By.id(icode_input[i]));
                icode.clear();
                icode.sendKeys("11");
                log.addStep("Escribiendo valor '" + (driver.findElement(By.id(icode_input[i])).getText()) + "' en: " + driver.findElement(By.id(icode_input[i])).getText());
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.addStep("Problema en InterruptedException ");
            }
        log.addStep("Se realizo correctamente el ingreso del ICODE ");
    }

    /**
     * @Deprecado Descripcion Metodo Ingresar el icode el cual es seteado con el valor 11 11 11
     */
    public void icodeByIDv2() throws Exception {

        String[] icode_input = {"iCode_1_value", "iCode_2_value", "iCode_3_value"};
        for (int i = 0; i < 3; i++)
            try {
                WebElement icode = driver.findElement(By.id(icode_input[i]));
                icode.clear();
                icode.sendKeys("11");
                log.addStep("Escribiendo valor '" + (driver.findElement(By.id(icode_input[i])).getText()) + "' en: " + driver.findElement(By.id(icode_input[i])).getText());
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.addStep("Problema en InterruptedException ");
            }
        log.addStep("Se realizo correctamente el ingreso del ICODE ");
    }

    /**
     * Descripcion Metodo Ingresar el icode el cual es seteado con el valor 11 11 11
     */
    public void icode(String rut) throws Exception {
        String[] icode_input = {"iCode_1_value", "iCode_2_value", "iCode_3_value"};

        for (int i = 0; i < 3; i++) {
            WebElement icode = driver.findElement(By.id(icode_input[i]));
            icode.clear();
            icode.sendKeys("11");
            log.addStep("Escribiendo valor '" + icode.getAttribute("value") + "' en: " + driver.findElement(By.id(icode_input[i])).getText());
            sleep(3);
        }
        log.addStep("Se realizo correctamente el ingreso del ICODE ");
    }





    /**
     * Descripción: Validación manual, conforme a una condición verdadera o falsa dependiendo de la validación que se requiera.
     *
     * @param condition si cumple o no True or False
     */
    public void manualCheck(String texto, boolean condition) throws Exception {

        if (condition) {
            log.addStep(texto);
        } else {
            log.addStep(texto);
            throw new Exception("Fallo en el caso de prueba debido " + texto);
        }
    }

    /**
     * Descripcion: Retorna el atributo del locator solicitado .
     *
     * @param locator Objeto y el atributo que se desea retornar
     **/
    public String getAtribute(By locator, String atributo) throws Exception {
        String resultado;
        try {
            existElementWithOutLog(locator);
            //    log.addStepBy("Se busca el atributo ", locator);
            switch (atributo) {
                case "text":
                    resultado = driver.findElement(locator).getText();
                    break;
                default:
                    resultado = driver.findElement(locator).getAttribute(atributo);
                    break;
            }
            return resultado;
        } catch (NoSuchElementException e) {
            throw new Exception("Objeto no encontrado ");
        }
    }

    /**
     * Descripcion: Retorna el atributo del locator solicitado cuando este no es visible para el usuario.
     *
     * @param locator Objeto y el atributo que se desea retornar
     **/
    public String getAtributeObjectNotVisible(By locator, String atributo) throws Exception {
        String resultado;
        try {
            log.addStepBy("Se busca el atributo ", locator);
            switch (atributo) {
                case "text":
                    resultado = driver.findElement(locator).getText();
                    break;
                default:
                    resultado = driver.findElement(locator).getAttribute(atributo);
                    break;
            }
            return resultado;
        } catch (NoSuchElementException e) {
            throw new Exception("Objeto no encontrado ");
        }
    }

    /**
     * Descripcion Obtiene la URL actual usada por el navegador.
     *
     * @return URL
     */
    public String getURL() {
        String url = driver.getCurrentUrl();
        log.addStep("Obteniendo URL : " + url);
        return url;
    }

    /**
     * Descripcion: Permite cambiar la URL dentro del navegador
     * #param url: Url nueva a navegar
     */
    public void navigateToUrl(String url) {
        driver.navigate().to(url);
        log.addStep("Cambiada la URL a: " + url);
    }


    /**
     * Descripción: Método valida la carga completa de una página SIN LOG.
     */
    public void existPageMethod() throws Exception {
        ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString().equals("complete");
            }
        };
        try {
            wait = new WebDriverWait(driver, Duration.ofSeconds(60));
            if (wait.until(expectation)) ;
        } catch (Throwable error) {
            throw new Exception("Fallo en carga de pagina");
        }
    }

    /**
     * =====================================================
     * Descripción:
     * 1.-Se conecta a la base de datos SLQServer CLSTGSQLQ20
     * 2.-Ejecuta query con validación de rut para uso de ICODE
     *
     * @param locator objeto a buscar
     * @param rut     rut a validar
     * @return codigo
     */
    public String setVerificationICode(By locator, String rut) throws Exception {
        ResultSet rs;
        String CODE = "";
        String query = "select top(1) token from BKBCTN000.dbo.enrolamiento where act_rut = '" + rut
                + "' and autorizacion like 'solicitado' order by fecha_evento DESC";
        try {
            rs = dbm.getStatementCLSTGSQLQ20().executeQuery(query);
            while (rs.next()) {
                CODE = rs.getString(1).trim();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("fallo en la consulta en la Bd: " + query);
        }
        this.setText(locator, CODE);
        log.addStep("Ingreso de Token sms '" + CODE + "'");
        return CODE;
    }

    public String setVerificationOther(By locator, String RUT) throws Exception {
        ResultSet rs;
        String CODE = "";
        try {
            rs = dbm.getStatementCLSTGSQLQ20().executeQuery("select top(1) Token from BKBCTN000.dbo.tblInternetDestinatarioFusion where Rut = '"
                    + RUT + "' and Estado like '1' order by FechaIniToken DESC");
            while (rs.next()) {
                CODE = rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.setText(locator, CODE);
        return CODE;
    }

    /**
     * Descripción: Limpiar campo y dejar vacío
     */
    public void cleanInput(By locator) throws Exception {
        existElementWithOutLog(locator);
        driver.findElement(locator).clear();
    }

    /**
     * Descripción: Método que escribe catpcha en portal publico
     *
     * @return
     */
    public void solveCaptchaPublic() throws AWTException, IOException, InterruptedException {
        WebElement Image = driver.findElement(By.xpath("//img[@id='catpcha']"));
        String response = "";
        do {
            deleteAllExceptGitFile();
            sleep(1);
            Actions actions = new Actions(driver);
            actions.click(driver.findElement(By.xpath("//*[@id='solicitarForm']/fieldset/ul/li[9]/a/img"))).perform();
            sleep(3);
            actions.contextClick(Image).build().perform();
            sleep(1);
            saveCaptcha();
            try {
                response = getCaptchaString();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                response = null;
            }
        } while (response == null);
        //setText(locators.Personas.Selenium.TXT_CAPTCHA, response);
    }


    /**
     * Descripción: Realiza el click con el método actions para una mejor
     *
     * @param locator
     */
    public void clickForced(By locator) throws Exception {
        try {
            existElementWithOutLog(locator);
            Actions actions = new Actions(driver);
            actions.click(driver.findElement(locator)).perform();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Elemento no encontrado, verificar si se encuentra activo " + locator.toString());
        }
    }

    /**
     * Descripción: Genera un screenshoot en evidencia final, se puede asignar una descripción al paso.
     *
     * @param vpName texto a mostrar en evidencia con el screenshoot
     */
    public void takeScreenshot(String vpName) {
        log.addStep(vpName);
    }


    /**
     * Descripción: Finaliza o cierra los procesos de navegadores abiertos previa ejecución.
     */
    public void finNavegadoresInicial() throws Exception {
        try {
            Set<String> identificador = driver.getWindowHandles();
            Iterator<String> iter = identificador.iterator();
            String[] winNames = new String[identificador.size()];
            int i = 0;
            while (iter.hasNext()) {
                winNames[i] = iter.next();
                i++;
            }
            if (winNames.length > 0) {
                for (i = winNames.length; i > 0; i--) {
                    driver.switchTo().window(winNames[i - 1]);
                    driver.close();
                    //valdiar cierre
                    log.addStep("CIERRE DE NAVEGADORES ABIERTOS");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Descripción: Método de finalización de script por error, generando una excepción.
     */
    public void captureException(Exception e) {
        log.addStep("FALLO: Caso Finalizado", true);
        log.addStep("\n", false);
        log.addStep("Mensaje de Excepción : \n" + e.getMessage(), false);
        String excep = "None";
        String[] rutaNombre = getRute();

        for (int i = 0; i < e.getStackTrace().length; i++) {
            String temp = e.getStackTrace()[i].toString();
            if (temp.contains(rutaNombre[2])) {
                excep = temp;
                break;
            }
        }
        log.addStep("Línea Número         : \n" + excep, false);
        log.addStep("Nombre Excepción     : \n" + e.toString(), false);
        log.addStep("Conjunto Excepciones : \n" + e.getStackTrace()[0], false);
    }

    /**
     * Descripción: Retorno a ventana principal según nombre del título de la ventana
     *
     * @param tituloVentana
     */
    public void realSwitchWindow(String tituloVentana) {
        driver.switchTo().window(tituloVentana);
    }

    /**
     * Descripción: Retorna a ultima pestaña o ventana abierta de forma dinámica
     */
    public void switchToLastWindow() {
        Set<String> handles = getDriver().getWindowHandles();
        for (String handle : handles) {
            getDriver().switchTo().window(handle);
        }
    }

    /**
     * Obtiene texto contenido en un locator utilizado
     *
     * @param locator --> locator en formato By
     * @return
     */
    public String getTextLocator(By locator) {
        WebElement tabla = driver.findElement(locator);
        String contenido = tabla.getText();
        return contenido;

    }

    /***
     * Descripción: Verifica que el botón está habilitado.
     */

    public boolean estadoBoton(By locator) {
        try {
            WebElement elem = driver.findElement(locator);
            if (!elem.isEnabled()) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
    }

    /**
     * Descripción: Método para subir o bajar en una pantalla.
     *
     * @param direccion > 0 baja
     * @param direccion < 0 sube
     */

    public void scroll(int direccion) {
        JavascriptExecutor scroll = (JavascriptExecutor) driver;
        scroll.executeScript("window.scrollBy(0," + String.valueOf(direccion * 75) + ")", "");

    }

    /**
     * Scroll para bajar a un elemento mandado por parámetros
     */
    public void scrollElement(By locator) throws Exception {
        try {
            WebElement element = driver.findElement(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        } catch (NoSuchElementException e) {
            throw new Exception("Fallo Scroll: Objeto No Encontrado");
        }

    }

    /**
     * Descripción: Edita un texto HTML de la página.
     */

    public void editHTML(By locator, String texto) throws Exception {
        try {
            existElementWithOutLog(locator);
            log.addStepBy("Encuentra el locator : ", locator);
            WebElement datoactual = driver.findElement(locator);
            String script = "arguments[0].innerHTML='" + texto + "'";
            ((JavascriptExecutor) driver).executeScript(script, datoactual); // Nuevo texto /  Locators de donde es cambiado
        } catch (NoSuchElementException e) {
            throw new Exception("Test Fallido Objeto No Encontrado");
        }
    }

    /**
     * Descripción: Devuelve la fecha del sistema en adicion de dias que se requiera sumarle
     *
     * @param dias
     * @return calendar.getTime()
     */

    public Date addDaysToSystemDate(int dias) {
        Calendar calendar = Calendar.getInstance();
        Date fecha = calendar.getTime();
        calendar.setTime(fecha);
        calendar.add(Calendar.DAY_OF_YEAR, dias + 1);
        return calendar.getTime();
    }


    public void selectCampana(String campaña) throws Exception {
        String var = "";
        String textoCamp = "";
        String textoBoton = "";

        switch (campaña) {
            case "AUMLP":
                textoCamp = "Línea de crédito";
                textoBoton = "Ir a Aumentar";
                break;
            case "AUMMP":
                textoCamp = "Crédito de consumo";
                textoBoton = "Simular crédito";
                break;
            case "AUMTC":
                textoCamp = "Aumenta el cupo de tu tarjeta ";
                textoBoton = "Aumentar cupo";
                break;
            case "AVNCE":
                textoCamp = "Avance";
                textoBoton = "Avance";
                break;
            default:
                // code block
        }

        try {
            if (existElementWithOutException(By.id("slick-slide-control00"))) {
                WebElement carruselBanner = driver.findElement(By.id("slick-slide-control00"));
                var = carruselBanner.getAttribute("aria-label");
                int v1 = Integer.parseInt((var.substring(var.length() - 1)));
                for (int i = 0; i < v1; i++) {
                    click(By.id("slick-slide-control0" + i + ""));
                    TimeUnit.SECONDS.sleep(1);
                    if (getAtribute(By.id("slick-slide0" + i + ""), "text").contains(textoCamp)) {
                        manualCheck("Campaña Encontrada OK ", true);
                        click(By.xpath("//*[@id=\"aceptar\" and contains(text(),'" + textoBoton + "')]"));
                        break;
                    }
                    if (i == v1 - 1) {
                        manualCheck("Campaña No Encontrada, Verificar si esta Cargada", false);
                    }
                }
            } else {
                //System.out.println(selenium.getAtribute(By.xpath("//*[@id=\"idItem1\"]"),"text"));
                if (selenium.getAtribute(By.xpath("//*[@id=\"sliderCampanaNewHome\"]"), "text").contains(textoCamp)) {
                    manualCheck("Campaña Encontrada OK ", true);
                    selenium.click(By.xpath("//*[@id=\"aceptar\" and contains(text(),'" + textoBoton + "')]"));
                } else {
                    manualCheck("Campaña No Encontrada, Verificar si esta Cargada", false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            manualCheck("Campaña No Encontrada, Verificar si esta Cargada", false);
        }
    }

    public void validarCampana(String campaña) throws Exception {
        String var = "";
        String textoCamp = "";
        String textoBoton = "";


        switch (campaña) {
            case "AUMLP":
                textoCamp = "Línea de crédito";
                break;
            case "AUMMP":
                textoCamp = "Crédito de consumo";
                break;
            case "AUMTC":
                textoCamp = "Aumenta el cupo de tu tarjeta ";
                break;
            case "AVNCE":
                textoCamp = "Avance";
                break;
            default:
                // code block
        }

        try {
            if (existElementWithOutException(By.id("slick-slide-control00"))) {
                WebElement carruselBanner = driver.findElement(By.id("slick-slide-control00"));
                var = carruselBanner.getAttribute("aria-label");
                int v1 = Integer.parseInt((var.substring(var.length() - 1)));
                for (int i = 0; i < v1; i++) {
                    click(By.id("slick-slide-control0" + i + ""));
                    TimeUnit.SECONDS.sleep(1);
                    if (getAtribute(By.id("slick-slide0" + i + ""), "text").contains(textoCamp)) {
                        manualCheck("Campaña Aun Existe NOK ", false);
                        break;
                    }
                    if (i == v1 - 1) {
                        manualCheck("Campaña No Encontrada OK ", true);
                    }
                }
            } else {
                //System.out.println(selenium.getAtribute(By.xpath("//*[@id=\"idItem1\"]"),"text"));
                if (selenium.existElementWithOutException(By.xpath("//*[@id=\"sliderCampanaNewHome\"]"))) {
                    if (selenium.getAtribute(By.xpath("//*[@id=\"sliderCampanaNewHome\"]"), "text").contains(textoCamp)) {
                        manualCheck("Campaña Aun Existe NOK ", false);
                    } else {
                        manualCheck("Campaña No Encontrada OK ", true);
                    }
                } else manualCheck("Campaña No Encontrada OK ", true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Campaña Aun Existe NOK ");
        }
    }

    /**
     * Descripción: Metodo que recibe un parametro en boolean mas dos valores string. Si se cumple la condicion
     * se realiza un manual check en true junto al pasoOK si no genera una exepcion junto al valor pasoNoOK
     *
     * @param validacion
     */
    public void ifElse(boolean validacion, String pasoOk, String pasoNoOK) throws Exception {

        if (validacion) {
            manualCheck(pasoOk, true);
        } else {

            manualCheck("Fallo en caso de prueba debido a lo siguiente: " + pasoNoOK, false);

        }
    }

    /**
     * Descripción: Metodo que recibe un parametro string locator. Valida si un checkbox está seleccionado
     * sin generar excepción
     *
     * @param locator
     */
    public boolean isCheckboxSelected(By locator) throws Exception {
        WebElement checkBoxElement = driver.findElement(locator);
        return checkBoxElement.isSelected();
    }

    /**
     * Descripción: Método que extrae el hijo específico de un locator. Recibe como parámetros un locator y la posición del hijo que se quiere localizar
     *
     * @param locator
     * @param childPosition
     */
    public String getChildText(By locator, int childPosition) throws Exception {
        try {
            existElementWithOutLog(locator);
            WebElement element = driver.findElement(locator);
            return element.findElement(By.xpath("./child::*[" + childPosition + "]")).getText();
        } catch (Exception e) {
            throw new Exception("locator no encontrado, verificar si existe el elemento ");
        }
    }

    /**
     * Descripción: Método que extrae el hijo específico de un locator. Recibe como parámetros un locator y la posición del hijo que se quiere localizar
     *
     * @param locator
     * @param childPosition1
     * @param childPosition2
     */
    public String getChildText2(By locator, int childPosition1, int childPosition2) throws Exception {
        try {
            existElementWithOutLog(locator);
            WebElement element = driver.findElement(locator);
            return element.findElement(By.xpath("./child::*[" + childPosition1 + "]/child::*[" + childPosition2 + "]")).getText();
        } catch (Exception e) {
            throw new Exception("locator no encontrado, verificar si existe el elemento ");
        }
    }

    /**
     * Descripción: Método para remover atributos del html.
     *
     * @param locator
     * @param attributeToRemove
     */
    public void removeAttribute(By locator, String attributeToRemove) throws Exception {
        try {
            existElementWithOutLog(locator);
            WebElement element = driver.findElement(locator);
            JavascriptExecutor jse = (JavascriptExecutor) driver;
            jse.executeScript("arguments[0].removeAttribute('" + attributeToRemove + "')", element);
        } catch (Exception e) {
            throw new Exception("locator no encontrado, verificar si existe el elemento ");
        }
    }

    /**
     * Descripción: método para setear atributos de una etiqueta html
     *
     * @param locator
     * @param attribute
     * @param valueAttribute
     * @throws Exception
     */
    public void setAttribute(By locator, String attribute, String valueAttribute) throws Exception {
        try {
            existElementWithOutLog(locator);
            log.addStepBy("Encuentra el locator : ", locator);
            WebElement datoactual = driver.findElement(locator);
            String script = "arguments[0].setAttribute('" + attribute + "', '" + valueAttribute + "')";
            ((JavascriptExecutor) driver).executeScript(script, datoactual);
        } catch (Exception e) {
            throw new Exception("locator no encontrado, verificar si existe el elemento ");
        }
    }

    /**
     * Descripcion: Metodo que valida si un objeto esta desplegado en pantalla, segun el parametro de tiempo dado.
     *
     * @param locator objeto esperado
     * @param seg     a esperar
     */
    public Boolean existElementWithOutLog(By locator, int seg) throws Exception {
        boolean exist = false;
        try {
            wait = new WebDriverWait(driver, Duration.ofSeconds(seg));
            if (wait.until(visibilityOfElementLocated(locator)).isDisplayed()) {
                exist = true;

            } else {
                throw new Exception("Test Fallido Objeto No Desplegado");
            }
        } catch (TimeoutException e) {
            throw new Exception("Test Fallido Objeto No Encontrado");
        }
        return exist;
    }

    /**
     * Descripcion: Metodo para Look & Feel, recibe locator junto con atributo (color,background-color, border color)
     *
     * @param locator
     * @param atributo
     * @return
     */

    public String getCssValueColor(By locator, String atributo) throws Exception {
        String hex = "";
        try {
            existElementWithOutLog(locator);
            //Obtener color RGBA
            String rgba = driver.findElement(locator).getCssValue(atributo);
            //Convertir RGBA a HEX
            String color1[];
            color1 = rgba.replace("rgba(", "").split(",");
            hex = String.format("#%02x%02x%02x", Integer.parseInt(color1[0].trim()), Integer.parseInt(color1[1].trim()), Integer.parseInt(color1[2].trim()));
        } catch (Exception ex) {
            throw new Exception("fallo en el metodo de extraer el color en el elemento debido a " + ex.getMessage());
        }
        return hex.toUpperCase();

    }

    /**
     * Descripción: método para seleccionar una tarjeta de una lista desplegable.
     *
     * @param locatorList
     * @param cardPosition
     * @throws Exception
     */
    public void selectCardFromListByPosition(By locatorList, int cardPosition) throws Exception {
        try {
            existElementWithOutLog(locatorList);
            WebElement element = driver.findElement(locatorList);
            element.click();
            WebElement card = driver.findElement(By.xpath("//*/tbody[@class='dijitReset']/child::*[" + cardPosition + "]"));
            scrollElement(By.xpath("//*/tbody[@class='dijitReset']/child::*[" + cardPosition + "]"));
            card.click();
        } catch (Exception e) {
            throw new Exception("locator no encontrado, verificar si existe el elemento ");
        }
    }



    /*
     * Descripción: Método para simular tecla ENTER del teclado sobre la pantalla
     */
    public void enter() throws Exception {
        try {
            Thread.sleep(1500);
            Actions action = new Actions(driver);
            action.sendKeys(Keys.ENTER);
            action.perform();
            log.addStep("Se acciona la tecla Enter sobre la pantalla");
        } catch (Exception e) {
            throw new Exception("No se pudo realizar Enter sobre la pagina");
        }
    }

    /**
     * Descripción: Método para simular flecha del teclado hacia abajo sobre la pantalla
     */
    public void arrowDown() throws Exception {
        try {
            Thread.sleep(1500);
            Actions action = new Actions(driver);
            action.sendKeys(Keys.ARROW_DOWN);
            action.perform();
            log.addStep("Se acciona Flecha hacia abajo del teclado sobre la pantalla");
        } catch (Exception e) {
            throw new Exception("No se pudo accionar la flecha hacia abajo del teclado");
        }
    }

    public void arrowUP() throws Exception {
        try {
            Thread.sleep(1500);
            Actions action = new Actions(driver);
            action.sendKeys(Keys.ARROW_UP);
            action.perform();
            log.addStep("Se acciona Flecha hacia arriba del teclado sobre la pantalla");
        } catch (Exception e) {
            throw new Exception("No se pudo accionar la flecha hacia abajo del teclado");
        }
    }

    /**
     * Descripción: Método para obtener el valor de una propiedad CSS en los estilos de un locator
     *
     * @param locator
     * @param propiedad
     * @return
     */
    public String getCSSValue(By locator, String propiedad) throws Exception {
        try {
            existElementWithOutLog(locator);
            WebElement element = driver.findElement(locator);
            log.addStep("Obteniendo propiedad CSS: " + propiedad + " del elemento: " + locator);
            return element.getCssValue(propiedad);
        } catch (Exception e) {
            throw new Exception("Error al obtener la propiedad: " + propiedad + " del locator: " + locator);
        }
    }

    /**
     * Descripción: método que obtiene el tamaño de un array de web elements.
     *
     * @param Locator
     * @return
     * @throws Exception
     */
    public int sizeWebElementsArray(By Locator) throws Exception {
        try {
            List<WebElement> array = driver.findElements(Locator);
            log.addStep("Tamaño del array: " + array.size());
            return array.size();
        } catch (Exception e) {
            throw new Exception("locator no encontrado, verificar si existe el elemento ");
        }
    }

    /**
     * Descripción: Método para simular tecla Tab del teclado sobre la pantalla
     */
    public void tab() throws Exception {
        try {
            Thread.sleep(1500);
            Actions action = new Actions(driver);
            action.sendKeys(Keys.TAB);
            action.perform();
            log.addStep("Se acciona tecla TAB del teclado sobre la pantalla");
        } catch (Exception e) {
            throw new Exception("No se pudo accionar la tecla TAB");
        }
    }

    /**
     * Método para saber si un elemento no existe sin generar excepción.
     *
     * @param Locator
     * @return Retorna true si el elemento no existe. Retorna false si el elemento existe.
     */
    public boolean notExist(By Locator) {
        List<WebElement> element = driver.findElements(Locator);
        return element.isEmpty();
    }

    /**
     * Descripción: método para validar fecha en formato DD/MM/AAAA
     *
     * @param dateToValidate
     * @return Retorna true si cumple formato DD/MM/AAAA
     */
    public boolean isValidDate(String dateToValidate) {
        String regex = "^(1[0-2]|0[1-9])/(3[01]|[12][0-9]|0[1-9])/[0-9]{4}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher((CharSequence) dateToValidate);
        return matcher.matches();
    }

    /***
     * Use robot to click n time
     *
     * @param times
     */
    public void robotClick(int times) {
        try {
            Robot robot = new Robot();
            for (int i = 1; i <= times; i++) {
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK); //click izquierdo
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            }
        } catch (AWTException e) {
        }
    }

    /**
     * Permite cambiar los meses en un tipo de componente de tipo Widtget Date,
     * que se muestra por defecto bloqueado. Recibe la cantidad de meses a moverse desde la fecha
     * que tenga seteada el componente, y el sentido.
     */
    public void setMonthToRareDojoDateSelector(By Witget_Date_Locator, int month_number, boolean go_fowward) throws Exception {

        boolean first_time = true;
        while (month_number > 0) {
            this.clickForced(Witget_Date_Locator);
            this.arrowDown();
            sleep(1);
            if (!go_fowward) {
                if (first_time) {
                    this.arrowUp();
                    sleep(1);
                    first_time = false;
                    this.enter();
                    sleep(1);
                } else {
                    for (int i = 0; i < 14; i++) {
                        this.arrowUp();
                        sleep(1);
                    }
                    this.enter();
                    sleep(1);
                }
            }
            // if direction is true, go foward
            else {

                for (int i = 0; i < 5; i++) {
                    this.arrowDown();

                    sleep(1);
                }
                this.enter();

                sleep(1);

            }
            month_number--;
        }
    }

    /**
     * Descripción: Devuelve la fecha del sistema en sustración de dias que se requiera sumarle
     *
     * @param dias
     * @return calendar.getTime()
     */

    public Date sustDaysToSystemDate(int dias) {
        Calendar calendar = Calendar.getInstance();
        Date fecha = calendar.getTime();
        calendar.setTime(fecha);
        calendar.add(Calendar.DAY_OF_YEAR, -dias);
        return calendar.getTime();
    }

    /**
     * Descripción: Selecciona archivo de carga Cheque Seguro
     *
     * @param browser, locator, id_component
     * @return
     */
    public void openFileUploader(String browser, By locator, String id_component) throws Exception {
        selenium.clickForced(locator);
        Robot r = new Robot();
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        //StringSelection ss = new StringSelection("document.getElementById('" + id_component + "').click()");
        StringSelection ss = new StringSelection(id_component);
        cb.setContents(ss, ss);

        if (browser.equalsIgnoreCase("IE")) {
            // open devtool ie
            r.keyPress(KeyEvent.VK_F12);
            sleep(1);
            r.keyRelease(KeyEvent.VK_F12);

            // Move To console tab
            r.keyPress(KeyEvent.VK_CONTROL);
            r.keyPress(KeyEvent.VK_2);
            r.keyRelease(KeyEvent.VK_CONTROL);
            r.keyRelease(KeyEvent.VK_2);

            pasteCodeAndConfirm(r);
        } else {
            TimeUnit.SECONDS.sleep(2);
            pasteCodeAndConfirm(r);
            TimeUnit.SECONDS.sleep(2);
        }
    }

    public void pasteCodeAndConfirm(Robot r) throws InterruptedException {
        // paste code of execution
        r.keyPress(KeyEvent.VK_CONTROL);
        r.keyPress(KeyEvent.VK_V);
        r.keyRelease(KeyEvent.VK_CONTROL);
        r.keyRelease(KeyEvent.VK_V);
        TimeUnit.SECONDS.sleep(2);
        // press enter to confirm code
        r.keyPress(KeyEvent.VK_ENTER);
        r.keyRelease(KeyEvent.VK_ENTER);
    }

    /***
     * Get the browser name based on the current browser propety
     *
     * @return
     */
    public String getNav() {
        return current_browser;
    }

    /**
     * Descripcion Metodo que valida que un elemento no este visible en pantalla por el tiempo indicado.
     *
     * @param locator objeto esperado
     * @param seg     a esperar que no este visible
     */
    public Boolean invisibilityElement(By locator, int seg) throws Exception {
        boolean notExist = false;
        try {
            wait = new WebDriverWait(driver, Duration.ofSeconds(seg));
            if (wait.until(invisibilityOfElementLocated(locator))) {
                notExist = true;
                Thread.sleep(1500);
                log.addStepBy("Elemento no se muestra en pantalla : ", locator);
            } else {
                log.addStepBy("Elemento Desplegado : ", locator);
                throw new Exception("Test Fallido Objeto Desplegado");
            }
        } catch (TimeoutException e) {
            log.addStep("Error TimeOut Tiempo : " + seg + " Seg.");
            log.addStepBy("Elemento no ha desaparecido : ", locator);
            throw new Exception("Test Fallido Objeto Encontrado");
        }
        return notExist;
    }

    /**
     * Descripcion Metodo que valida que un elemento no este visible en pantalla por el tiempo indicado sin generar excepcion.
     *
     * @param locator objeto esperado
     * @param seg     a esperar que no este visible
     */
    public Boolean invisibilityElementOutException(By locator, int seg) throws Exception {
        boolean notExist = false;
        try {
            wait = new WebDriverWait(driver, Duration.ofSeconds(seg));
            if (wait.until(invisibilityOfElementLocated(locator))) {
                notExist = true;
                Thread.sleep(1500);
                log.addStepBy("Elemento no se muestra en pantalla : ", locator);
            } else {
                log.addStepBy("Elemento Desplegado : ", locator);
                throw new Exception("Test Fallido Objeto Desplegado");
            }
        } catch (TimeoutException e) {
            log.addStepBy("Elemento no ha desaparecido : ", locator);
            notExist = false;
        }
        return notExist;
    }

    /**
     * Descripcion Metodo que valida que el pop "Espere un momento ..." no este visible en pantalla por el tiempo indicado.
     *
     * @param seg a esperar que no este visible
     */
    public Boolean invisibilityElementWaitAMoment(int seg) throws Exception {
        boolean notExist = false;
        By locator = By.xpath("//div[@id='dojoProgressIndicator']//i[@id='dojoProgressLoadingMessage'] | //div[@id='showWaitMessage']");
        //By locator = By.xpath("//div[@id='dojoProgressIndicator']");
        if (selenium.existElementWithOutException(locator, 3)) {
            notExist = invisibilityElement(locator, seg);
        } else notExist = true;
        return notExist;
    }

    /**
     * Descripción: Se conecta a la BD y Ejecuta query que obtiene codigo de seguridad que se envia como SMS para Empresas Pyme
     *
     * @param locator objeto a buscar
     * @param rut     rut a validar
     * @return codigo
     */
    public String setVerificationOtherPYME(By locator, String rut) throws Exception {

        //DBManager dbm = new DBManager();
        ResultSet rs;
        String CODE = "";
        String query = "select IdEstadoOportunidad from  SASBD_NEW_2.dbo.Cmp_Oportunidad  where IdOportunidad = 2796496 ";
        printLog("query de ejecucion en metodo setVerificationOtherPYME " + query);
        try {
            rs = dbm.getStatementCLSTGSQLQ81().executeQuery(query);
            while (rs.next()) {
                CODE = rs.getString(1).trim();
                printLog(CODE);
                printLog("el codigo retornado es  " + CODE);
            }

            printLog("BD en:" + dbm.getStatementCLSTGSQLQ20().isClosed());

            query = "select top(1) token from BKBCTN000.dbo.enrolamiento_Pyme where act_rut ='" + rut + "' order by fecha_evento DESC";
            printLog("query de ejecucion en metodo setVerificationOtherPYME " + query);
            rs = dbm.getStatementCLSTGSQLQ20().executeQuery(query);
            while (rs.next()) {
                CODE = rs.getString(1).trim();
                printLog(CODE);
                printLog("el codigo retornado es  " + CODE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            printLog(e.getMessage());
            throw new Exception("Error en la consulta en la Bd: " + query);
        }
        this.setText(locator, CODE);
        return CODE;
    }

    /**
     * Descripción: Obtenie marcas WebMonitor Pago TC
     *
     * @param rut a validar
     * @return codigos
     */
    public String[] webMonitorPagoTC(String rut) throws Exception {
        DBManager dbm = new DBManager();
        ResultSet rs;
        String[] CODE = new String[3];
        String query = "SELECT TOP (1) CODTRAN, MONTO, FECHA " +
                "FROM [WMBDP000].[dbo].[LOGWEB_hot] " +
                "WHERE RUTO= '" + rut + "' " +
                "AND CODTRAN IN ('TCPN60','TCPI77','TCPI81') " +
                "ORDER BY FECHA DESC";
        try {
            rs = dbm.getStatementCLSTGSQLQ69().executeQuery(query);
            while (rs.next()) {
                CODE[0] = rs.getString(1).trim();
                CODE[1] = rs.getString(2).trim();
                CODE[2] = rs.getString(3).trim();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error en la consulta en la Bd: " + query);
        }
        return CODE;
    }

    public String[] webMonitorConsultaLeyPensiones(String rut) throws Exception {
        DBManager dbm = new DBManager();
        ResultSet rs;
        String[] CODE = new String[6];
        String query = "SELECT TOP (1) CODTRAN, CODERR, CTADES, NOMDES, CTAORI, FECHA " +
                "FROM [WMBDP000].[dbo].[LOGWEB_hot] " +
                "WHERE RUTO= '" + rut + "' " +
                "AND CODTRAN IN ('MPMR29') " +
                "ORDER BY FECHA DESC;";
        try {
            rs = dbm.getStatementCLSTGSQLQ69().executeQuery(query);
            while (rs.next()) {
                CODE[0] = rs.getString(1).trim();
                CODE[1] = rs.getString(2).trim();
                CODE[2] = rs.getString(3).trim();
                CODE[3] = rs.getString(4).trim();
                CODE[4] = rs.getString(5).trim();
                CODE[5] = rs.getString(6).trim();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error en la consulta en la Bd: " + query);
        }
        return CODE;
    }

    /**
     * Método para seleccionar una opción de un combobox por su value
     *
     * @param locatorButtonCombobox
     * @param idLocator
     * @param value
     * @throws Exception
     */
    public void selectWithValue(By locatorButtonCombobox, String idLocator, String value) throws Exception {
        try {
            WebElement button = driver.findElement(locatorButtonCombobox);
            selenium.printLog("Paso 1 check");
            button.click();
            TimeUnit.SECONDS.sleep(1);
            selenium.printLog("Paso 2 check");
            driver.findElement(By.xpath("//*[@id='" + idLocator + "']"));
            selenium.printLog("Paso 2.1 check" + "//*[@id='" + idLocator + "']");
            driver.findElement(By.xpath("//*[@id='" + idLocator + "']/following-sibling::input"));
            selenium.printLog("Paso 3 check" + "//*[@id='" + idLocator + "']/following-sibling::input");
            TimeUnit.SECONDS.sleep(1);
            String valueCombobox1 = selenium.getAtribute(By.xpath("//*[@id='" + idLocator + "']/following-sibling::input"), "value");
            selenium.printLog("Paso 4.1 check" + valueCombobox1);
            selenium.setAttribute(By.xpath("//*[@id='" + idLocator + "']/following-sibling::input"), "value", value);
            String valueCombobox2 = selenium.getAtribute(By.xpath("//*[@id='" + idLocator + "']/following-sibling::input"), "value");
            selenium.printLog("Paso 4.2 check" + valueCombobox2);
            TimeUnit.SECONDS.sleep(3);
            WebElement element = driver.findElement(By.xpath("//*[contains(@value,'" + value + "')]"));
            selenium.printLog("Paso 5 check");
            element.click();
        } catch (Exception e) {
            throw new Exception("locator no encontrado, verificar si existe el elemento ");
        }
    }

    /**
     * Descripción: seleccionar mes en un calendario
     *
     * @param locator del calendario a cofnigurar
     * @param mes     que se desea seleccionar (1-12)
     * @throws Exception
     */
    public void selectMonthByNumber(By locator, int mes) throws Exception {
        this.clickForced(locator);
        LocalDateTime local = LocalDateTime.now();
        local = local.withMonth(mes);
        int mMonth = local.getMonthValue();
        this.printLog(this.getAtribute(By.id("m" + mMonth), "class"));
        if (mMonth != 12)
            if (this.getAtribute(By.id("m" + mMonth), "class").contains("disableMonth")) {
                this.clickForced(By.id("pre"));
                this.clickForced(By.id("m" + mMonth));
            } else {
                this.clickForced(By.id("m" + mMonth));
            }
        else {
            this.click(By.id("pre"));
            this.click(By.id("m12"));
        }
    }

    /**
     * Descripción: Valida si el usuario Pyme al evaluar la no exitencia del nombre empresa
     *
     * @throws Exception
     */
    public void validatePyme() throws Exception {
        if (selenium.existElementWithOutException(By.xpath("//*[@id='nuevoHeader']/div[2]/div/div[2]/div[1]/p"))) {
            selenium.manualCheck("Visualización de elemento perteneciente a Usuario Empresa NOK", false);
        } else {
            selenium.manualCheck("Validación de no visualización de elementos pertenecientes a Usuario Empresa OK", true);
            selenium.manualCheck("Validación usuario Pyme OK", true);

        }
    }





    /**
     * Método para obtener todos los elementos de una tabla
     *
     * @param tableElements
     * @return arreglo de strings con los elementos buscados
     * @throws Exception
     */
    public ArrayList<String> getArrayWebElements(By tableElements) throws Exception {

        List<WebElement> myList;
        List<String> myTextList = new ArrayList<>();

        myList = driver.findElements(tableElements);

        for (int i = 0; i < myList.size(); i++) {
            myTextList.add(myList.get(i).getText());
        }
        return (ArrayList<String>) myTextList;
    }

    /**
     * Seleccionar opción en combobox por su index
     *
     * @param element_founder
     * @param elementPosition
     * @throws Exception
     */
    public void selectOptionInComboByIndex(By element_founder, int elementPosition) throws Exception {
        this.clickForced(element_founder);
        TimeUnit.SECONDS.sleep(1);
        this.arrowDown();
        while (elementPosition > 0) {
            this.arrowDown();
            elementPosition--;
        }
        this.enter();
    }

    /**
     * Método para obtener un web element de un determinado locator
     *
     * @param locator
     * @return
     */
    public WebElement getWebElement(By locator) {
        WebElement element = driver.findElement(locator);
        return element;
    }

    /**
     * Ejecutar script de javascript en el browser
     *
     * @param script
     * @return
     */
    public void runScript(String script, By locator) {
        try {
            existElementWithOutLog(locator);
            this.printLog("Ejecutando script... : " + script);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(script, driver.findElement(locator));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Descripcion Metodo que valida si un objeto esta presente en pantalla, segun el parametro de tiempo dado. Un elemento puede estar presente y no ser visible
     *
     * @param locator
     * @param segundos
     */
    public Boolean isPresentElement(By locator, int segundos) throws Exception {
        boolean exist = false;
        try {
            wait = new WebDriverWait(driver, Duration.ofSeconds(segundos));
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            exist = true;
            log.addStepBy("Elemento Encontrado : ", locator);
        } catch (TimeoutException e) {
            log.addStep("Error TimeOut Tiempo : " + segundos + " Seg.");
            throw new Exception("Test Fallido Objeto No Esta Presente");
        }
        return exist;
    }

    /*
     * Método para seleccionar un radiobutton dependiendo en una tabla si una fila tiene un texto a buscar
     * @param arrayElements
     * @param text
     * @throws Exception
     */
    public void selectRadioTableIfContainText(By arrayElements, String text) throws Exception {
        try {
            ArrayList<String> listaUsuario = this.getArrayWebElements(arrayElements);
            int indexOK = listaUsuario.indexOf(text) + 1;
            String element = "(//*[@name='radioBT'])[" + indexOK + "]/parent::div";
            this.clickR(By.xpath(element));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para realizar un doble click sobre un elemento
     *
     * @param locator
     * @throws Exception
     */
    public void doubleClick(By locator) throws Exception {
        existElementWithOutLog(locator);
        Actions actions = new Actions(driver);
        actions.doubleClick(driver.findElement(locator)).perform();
    }

    /**
     * Valida url de certificación esperada
     *
     * @return
     * @throws InterruptedException
     */
    public boolean urlElementCert() throws InterruptedException {
        String urlCert = "cert-";
        boolean encontrado = false;
        String validateurl = driver.getCurrentUrl();
        log.addStep("Obteniendo URL : " + validateurl);
        takeScreenshot(validateurl);
        if (validateurl.contains(urlCert)) {
            encontrado = true;
            log.addStep("url cert encontrada ");
        } else log.addStep("url cert no encontrada");

        return encontrado;
    }

    /**
     * Esta función realiza Tab en certificado y pulsa en botón Continuar en el Nuevo sitio público
     *
     * @param browser
     * @return
     * @throws AWTException
     */
    public void makeTabCertificatePublic(String browser) throws SQLException, AWTException {
        Robot r = new Robot();
        try {
            switch (browser) {
                case "EDGE":
                    r.delay(5000);
                    r.keyPress(KeyEvent.VK_TAB);
                    r.keyRelease(KeyEvent.VK_TAB);
                    r.delay(1000);
                    r.keyPress(KeyEvent.VK_TAB);
                    r.keyRelease(KeyEvent.VK_TAB);
                    r.delay(1000);
                    r.keyPress(KeyEvent.VK_TAB);
                    r.keyRelease(KeyEvent.VK_TAB);
                    r.delay(1000);
                    r.keyPress(KeyEvent.VK_TAB);
                    r.keyRelease(KeyEvent.VK_TAB);
                    r.delay(1000);
                    r.keyPress(KeyEvent.VK_TAB);
                    r.keyRelease(KeyEvent.VK_TAB);
                    r.delay(1000);
                    r.keyPress(KeyEvent.VK_ENTER);
                    r.keyRelease(KeyEvent.VK_ENTER);
                    r.delay(3000);
                    // para el certificado en q
                    break;
                case "CHROME_DEFAULT":
                    r.delay(5000);
                    r.keyPress(KeyEvent.VK_TAB);
                    r.keyRelease(KeyEvent.VK_TAB);
                    r.delay(1000);
                    r.keyPress(KeyEvent.VK_TAB);
                    r.keyRelease(KeyEvent.VK_TAB);
                    r.delay(1000);
                    r.keyPress(KeyEvent.VK_ENTER);
                    r.keyRelease(KeyEvent.VK_ENTER);
                    r.delay(3000);
                    // para el certificado en q
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage().toString());
        }

    }

    /***
     * Valida url esperada
     * @param url
     */
    public void validateUrlExist(String url) throws Exception {
        String validateurl = driver.getCurrentUrl();
        log.addStep("Obteniendo URL : " + validateurl);
        takeScreenshot(validateurl);

        if (validateurl.contains(url)) {
            log.addStep("url esperada correctamente");
        } else {
            log.addStep("url fallida");
            throw new Exception("se esperaba la siguiente url**" + url + " pero retorna la siguiente***" + validateurl + "**Falal test");
        }
    }

    /***
     * Valida si existe elemento y que el texto sea correcto
     * @param locator, texto
     */
    public void validateText(By locator, String texto) throws Exception {

        if (selenium.existElement(locator, 15)) {
            if (selenium.getAtribute(locator, "outerText").equals(texto)) {
                selenium.manualCheck("Existe y es correcto el texto" + texto, true);
            } else {
                selenium.manualCheck("Texto no es correcto, debería decir: " + texto, false);
            }
        } else {
            selenium.manualCheck("No existe texto" + texto, false);
        }
    }

    /**
     * Descripción: Devuelve un int de una cadena de texto (string)
     * Se aplica para montos en pesos ejem: $100.000
     */
    public int getNumberInt(By locator) {
        int soloNumeros;
        String numeroSinTexto = driver.findElement(locator).getText().replace("$", "").replace("USD", "").replace(".", "").replaceAll("\\s", "");
        soloNumeros = Integer.parseInt(numeroSinTexto);
        return soloNumeros;
    }


    /**
     * Descripción: Devuelve un double de una cadena de texto (string)
     * Se aplica para montos en dólares con decimales ejem: USD 100,00
     */
    public double getNumberDouble(By locator) {
        double soloNumeros;
        String numeroSinTexto = driver.findElement(locator).getText().replace("$", "").replace("USD", "").replace(".", "").replace(",", ".").replaceAll("\\s", "");
        soloNumeros = Double.parseDouble(numeroSinTexto);
        return soloNumeros;
    }


    /**
     * Descripción: Primer método de inicio de script.
     * Funciones:
     * 1.-Levantar Navegador señalado en DataPool(CH = Chrome, IE = Internet Explorer, FF = Firefox)--> Uso en driverConnection(String navegador);
     * 2.-Navegador se levanta como INCOGNITO.
     * 3.-Maximizado de pantalla antes de ingreso de url.
     * 4.-Ingreso de URL según portal
     */
    public void switchToOtherDevice(String device) {
        try {
            switch (device.toLowerCase()) {
                case "telefono":
                    driver.manage().window().setSize(new Dimension(320, 700));
                    break;
                case "tablet":
                    driver.manage().window().setSize(new Dimension(810, 700));
                    break;
                default:
                    break;
            }
            log.addStep("Navegando en Dispositivo '" + device + "' en browser '" + current_browser + "'");
            return;
        } catch (Exception e) {
            System.out.println("Excepción durante la operación del inicio de Navegador " + current_browser);
        }
    }


    /**
     * Permite cambiar los meses en un tipo de componente de tipo Widtget Date,
     * Recibe la cantidad de meses a moverse desde la fecha
     * que tenga seteada el componente, y el sentido.
     */
    public void setMonthToCalendarWidtgetDate(By witgetDateLocator, int monthNumber, boolean goFowward) throws Exception {
        if (monthNumber > 0) {
            this.clickForced(witgetDateLocator);
            while (monthNumber > 0) {
                if (goFowward) {
                    Thread.sleep(1500);
                    this.clickForced(By.xpath("//th[contains(@data-dojo-attach-point,'increment')]//span[contains(@class,'Increase')]"));
                } else {
                    Thread.sleep(1500);
                    this.clickForced(By.xpath("//th[contains(@data-dojo-attach-point,'decrement')]//span[contains(@class,'Decrease')]"));
                }
                monthNumber--;
            }
            System.out.println("Esto es una prueba");
            selenium.enter();
        }
    }

    /**
     * Descripción: Método para simular flecha del teclado hacia arriba sobre la pantalla
     */
    public void arrowUp() throws Exception {
        try {
            Thread.sleep(1500);
            Actions action = new Actions(driver);
            action.sendKeys(Keys.ARROW_UP);
            action.perform();
            log.addStep("Se acciona Flecha hacia arriba del teclado sobre la pantalla");
        } catch (Exception e) {
            throw new Exception("No se pudo accionar la flecha hacia arriba del teclado");
        }
    }

    public void archivoFallidos() {

        File f = new File("./resultados", "casosFallidos.txt");
        if (f.exists()) {
        } else {
            try {
                f.createNewFile();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Descripción: Método para seleccion Random de bancos
     **/
    public String getRandomBancoSimulado() {
        String bs = "";
        Random rand = new Random();
        int bs_value = rand.nextInt(4) + 1;
        switch (bs_value) {
            case 1:
                bs = "Banco Paris";
                break;
            case 2:
                bs = "Banco Rabobank";
                break;
            case 3:
                bs = "Banco Estado";
                break;
            case 4:
                bs = "Banco del Desarrollo";
                break;
            default:
                break;
        }
        return bs;
    }

    /**
     * Descripción: Selecciona valor de un ComboBox de tipo "List" según el texto que contenga, puede no ser exacto.
     *
     * @param texto       a Buscar
     * @param listLocator Objeto esperado
     */
    public void selectComboBoxListLocator(By selectLocator, By listLocator, String texto) throws Exception {
        try {
            this.click(selectLocator);
            this.waitLocator(listLocator, 5);
            List<WebElement> list = driver.findElements(listLocator);
            for (WebElement element : list) {
                String texElement = element.getText();
                if (texElement.equalsIgnoreCase(texto)) {
                    element.click();
                    log.addStepBy("Selección Texto Encontrado: " + texto + " ", listLocator);
                    break;
                }
            }
        } catch (WebDriverException e) {
            log.addStepBy("Selección Texto No Encontrado: " + texto + " ", listLocator);
            throw new Exception("Test Fallido Selección No Encontrada");
        }
    }

    /**
     * Descripción: Devuelve una lista de elementos, dado un locator
     */
    public List<WebElement> listaDeElementos(By locator) throws Exception {
        try {
            List<WebElement> listaElementos = driver.findElements(locator);
            return listaElementos;
        } catch (NoSuchElementException e) {
            throw new Exception("Test Fallido Objeto No Encontrado");
        }
    }

    /**
     * Descripcion Se usa para esperar objeto con tiempos prolongados
     * Metodo sin Log
     * @param locators Objeto esperado
     * @param seg      tiempo maximo de espera
     */
    public void waitLocatorWithOutLog(By locators, int seg) throws Exception {
        int x = 0;
        do {
            //sleep(0.5);
            if (existElement(locators, seg)) {
                break;
            } else {
                x++;
            }
        } while (x < seg);
    }

    /**
     * Descripcion Metodo que espera hasta que un locator deje de estar activo
     *
     * @param locator Objeto esperado
     * @return exist
     */
    public Boolean notExistElementWithOutException(By locator) {
        boolean exist = true;
        int i = 0;
        do {
            try {
                wait = new WebDriverWait(driver, Duration.ofSeconds(3));
                if (wait.until(visibilityOfElementLocated(locator)).isDisplayed()) {
                    exist = true;
                } else {
                    exist = false;
                }
                i++;
            } catch (TimeoutException e) {
                exist = false;
                i++;
            }

        } while (i <= 15 && exist == true);
        return exist;
    }

    /**
     * Descripcion Se usa para esperar objeto con tiempos prolongados
     * Metodo sin Exeption
     * @param locators Objeto esperado
     * @param seg      tiempo maximo de espera
     */
    public boolean waitLocatorWithOutExeption(By locators, int seg) throws Exception {
        int x = 0;
        boolean estado = false;
        do {
            if (existElementWithOutException(locators, seg)) {
                estado = true;
                break;
            } else {
                x++;
            }
        } while (x < seg);
        return estado;
    }

    /**
     * Descripcion Click a un elemento, validando antes que este desplegado.
     *
     * @param locator Objeto al que se realiza click
     **/
    public void clickWithOutExeption(By locator) throws Exception {
        try {
            existElementWithOutLog(locator);
            driver.findElement(locator).click();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * Devuelve el valor de una propiedad para un pseudoelemento ::after (Se genera con CSS) a traves de un querySelector de Javascript
     * @param selectorCSS sentencia de tipo CSS
     * @param propiedad
     * @return valor de la propieadad recibida
     */
    public String getPropertyQuerySelector(String selectorCSS, String propiedad) throws Exception {
        String script = "return window.getComputedStyle(document.querySelector('" + selectorCSS + "'),':after').getPropertyValue('" + propiedad + "')";
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        return (String) executor.executeScript(script);
    }


    /**
     * Obtiene valor contenido en un locator utilizado
     *
     * @param locator --> locator en formato By
     * @return
     */
    public String getValueLocator(By locator) {
        WebElement tabla = driver.findElement(locator);
        String contenido = tabla.getAttribute("value");
        return contenido;

    }

    /**
     * Valida que un elemento este en estado Habilitado
     *
     * @param locator --> locator en formato By
     * @return
     */
    public Boolean enableElementMethod(By locator) throws Exception {
        Boolean exist = false;
        int x = 0;
        do {
            x = x + 1;
            sleep(1);
            try {
                WebElement element = driver.findElement(locator);
                if (element.isEnabled() && element.isDisplayed()) {
                    exist = true;
                    break;
                }
            } catch (Exception e) {

            }
        } while (x < 8);
        sleep(1);

        return exist;

    }


    /**
     * Obtiene valor contenido en un locator utilizado , Solicitud de tarjeta adicional STCA
     *
     * @param texto --> Region
     * @return
     **/
    public Boolean seleccionarRegionTarAdicional(String texto) {
        Boolean res = false;
        try {    // *[@id="RegionForm"]/tbody/tr/td[2]
            List<WebElement> listRegion = driver.findElements(By.xpath("//table[@id='RegionForm_menu']/tbody/tr"));
            int i = 0;
            for (WebElement item : listRegion) {
                i++;
                if ((item.getAttribute("aria-label").contains(texto)) || (item.getAttribute("aria-label").equalsIgnoreCase(texto))) {
                    item.click();
                    sleep(2);
                    res = true;
                    break;
                }


            }
        } catch (Exception ex) {
            return res;
        }
        return res;
    }



    public Boolean seleccionarComunaAdicional(String texto) {
        Boolean res = false;
        try {    // *[@id="RegionForm"]/tbody/tr/td[2]
            List<WebElement> listComuna = driver.findElements(By.xpath("//*[@id='dijit_MenuItem_18_text']"));
            int i = 0;
            for (WebElement item : listComuna) {
                i++;
                if ((item.getAttribute("aria-label").contains(texto)) || (item.getAttribute("aria-label").equalsIgnoreCase(texto))) {
                    item.click();
                    sleep(2);
                    res = true;
                    break;
                }

            }
        } catch (Exception ex) {
            return res;
        }
        return res;
    }

    /**
     * Pulsar tecla Tabulador
     * @param locator --> locator en formato By
     **/
    public void KeyPress(By locator) throws Exception {
        try {
            WebElement textbox = driver.findElement(locator);
            textbox.sendKeys(Keys.TAB);

        } catch (NoSuchElementException e) {
            throw new Exception("Test Fallido Objeto No Encontrado");
        }
    }


    /**
     * existElementBool
     **/
    public Boolean existElementBool(By locator) throws Exception {
        Boolean exist = false;
        try {
            wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            if (wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed()) {
                exist = true;
                log.addStepBy("Elemento Encontrado : ", locator);
            } else {
                log.addStepBy("Elemento No Desplegado : ", locator);
                //throw new TestManagerException("Test Fallido Objeto No Desplegado");
            }
        } catch (TimeoutException e) {
            log.addStep("Error TimeOut Tiempo : " + 5 + " Seg.");
            log.addStepBy("Elemento NO Encontrado : ", locator);
            //throw new TestManagerException("Test Fallido Objeto No Encontrado");
        }
        return exist;

    }

    /**
     * Obtiene un atributo de un elemento
     * @param locator --> locator en formato By
     * @atributo --> texto
     **/
    public String getAttribute(By locator, String atributo) {
        WebElement propiedad = driver.findElement(locator);
        return propiedad.getAttribute(atributo);
    }

    /**
     *
     * Metodos relacionados con comunidad Inversiones
     *
     * ---------- Inicio -------------
     * @throws Exception
     */

    /**
     * Se selecciona todos los fondos
     **/
    public void seleccionFiltroTodosInvAutomaticas() throws Exception {
        By selector = By.id("dijit_MenuItem_0_text");
        selenium.isPresentElement(selector, 10);
        WebElement element = getDriver().findElement(selector);
        element.click();

    }

    public void setRuteIdCAEx(String testName) {
        this.testName = testName;

    }

    /**
     * Selecciona dia del aporte
     *
     **/
    public void seleccionFiltroDias() throws Exception {
        WebElement listbox = driver.findElement((By.id("selectDiaDelMes")));
        listbox.click();
        By selector = By.xpath("//*[contains(text(),'a 3 del mes')]");
        selenium.isPresentElement(selector, 10);
        WebElement element = getDriver().findElement(selector);
        element.click();

    }

    /**
     * Selecciona motivo de la inversión
     *
     **/
    public void seleccionFiltroMotivo() throws Exception {
        WebElement listbox = driver.findElement((By.id("selectMotivoInversion")));
        listbox.click();
        By selector = By.xpath("//*[contains(text(),'ahorro')]");
        selenium.isPresentElement(selector, 10);
        WebElement element = getDriver().findElement(selector);
        element.click();

    }

    /**
     * Valida estado habilitado o deshabilitado del boton Aportar
     *
     **/
    public String getClassBtnAporte() throws Exception {
        WebElement btnAportar = driver.findElement((By.id("CA_btnAbonar")));
        return btnAportar.getAttribute("class");
    }

    /**
     * Selección de una cuenta corriente para realizar aporte inversion
     *
     **/
    public void seleccionCuentaOrigen() throws Exception {
        WebElement listbox = driver.findElement((By.id("CM_selectCuentaOrigen")));
        listbox.click();
        try {
            By selector = By.xpath("(//*[contains(text(),'Cuenta Corriente')])[3]");
            selenium.isPresentElement(selector, 10);
            WebElement element = driver.findElement(selector);
            element.click();
        } catch (Exception e) {
            System.out.println("Usario NO POSEE 2 cuentas corrientes ");
        }

    }

    /**
     * Realiza click en el boton Pausar de la seccion ACTIVIDAD DEL APORTE
     *
     **/
    public void checkboxPausar() throws Exception {
        By selector = By.xpath("//*[contains(@id,'divSwitch')]/label");
        WebElement element = driver.findElement(selector);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);

    }

    /**
     * Realiza click en el boton Guardar cambios en la seccion Modifica tu inversion
     *
     **/
    public void btnModificar() throws Exception {
        By selector = By.id("CM_btnGuardarCambios");
        WebElement element = driver.findElement(selector);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    /**
     * Seleccion de la frecuencia del aporte a modificar
     *
     **/
    public void checkboxPeriodo() throws Exception {
        By selector = By.xpath("(//*[@name='CM_rbFrecuenciaAbono'])[2]");
        WebElement element = driver.findElement(selector);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    /**
     * Seleccion del dia a realizar el aporte modificado
     *
     **/
    public void seleccionFiltroDiasModAporte() throws Exception {
        WebElement listbox = driver.findElement((By.id("CM_selectDiaDelMes")));
        listbox.click();
        By selector = By.xpath("//*[contains(text(),'a 5 del mes')]");
        selenium.isPresentElement(selector, 10);
        WebElement element = getDriver().findElement(selector);
        element.click();

    }

    /**
     *  ---------- Fin de metodos de comunidad Inversiones -------------
     *      Busca textos en una lista de elementos
     */

    public Boolean existTextoList(By locator, String Txt) throws Exception {
        boolean exist = false;
        try {
            List<WebElement> listaElementos = driver.findElements(locator);
            for(WebElement e : listaElementos) {
                if(e.getText().contains(Txt) || e.getText().equalsIgnoreCase(Txt)){
                    exist = true;
                    break;
                }
            }

        } catch (TimeoutException e) {
            throw new TimeoutException("Test Fallido Objeto No Encontrado");
        }
        return exist;
    }

    /*
     * Hacer clic en el element de la lista
     * */
    public  Boolean chickElemenList(By locator, String Txt) throws Exception {
        boolean exist = false;
        try {
            List<WebElement> listaElementos = driver.findElements(locator);
            for(WebElement e : listaElementos) {
                if(e.getText().contains(Txt) || e.getText().equalsIgnoreCase(Txt)){
                    e.click();
                    exist = true;
                    break;
                }
            }

        } catch (TimeoutException e) {
            throw new TimeoutException("Test Fallido Objeto No Encontrado");
        }
        return exist;
    }
    /*
     * Hacer clic en el element de la lista que no contenga texto
     * */
    public  Boolean chickElemenListNoText(By locator, String Txt) throws Exception {
        boolean exist = false;
        try {
            List<WebElement> listaElementos = driver.findElements(locator);
            for(WebElement e : listaElementos) {
                if(!e.getText().contains(Txt)){
                    e.click();
                    exist = true;
                    break;
                }
            }

        } catch (TimeoutException e) {
            throw new TimeoutException("Test Fallido Objeto No Encontrado");
        }
        return exist;
    }
    /*
     * Devuelve tamaño de la lista
     * */
    public  int sizeList(By locator) throws Exception {
        int count = 0;
        try {
            List<WebElement> listaElementos = driver.findElements(locator);
            if(!listaElementos.isEmpty()){
                count = listaElementos.size();
            }

        } catch (TimeoutException e) {
            throw new TimeoutException("Test Fallido Objeto No Encontrado");
        }
        return count;
    }
    /*
    Valida que el archivo  que se pasa por parametro se descargo correctamente en la carpeta downloads del proyecto
     */
    public Boolean validarDescarga(String nombreArchivo) throws Exception{
        boolean exist = false;
        try {
            Thread.sleep(5000);
            File arhivoDescargado = new File(new File("downloads"+File.separator+ nombreArchivo).getAbsolutePath());
            System.out.println(arhivoDescargado.getName());
            if(arhivoDescargado.getName().contains(nombreArchivo)){
                exist = true;
            }

        } catch (TimeoutException e) {
            throw new TimeoutException("Test Fallido Objeto No Encontrado");
        }
        return exist;
    }/**
     * Obtiene texto contenido en un atributo de un locator
     *
     * @param locator --> locator en formato By
     * @return
     */
    public String getTextAttribute(By locator, String atributo) {
        WebElement tabla = driver.findElement(locator);
        String contenido = tabla.getAttribute(atributo);
        return contenido;
    }

    /**
     * Método para obtener todos los elementos de una tabla con paginado
     *
     * @param tableElements
     * @return arreglo de strings con los elementos buscados
     * @throws Exception
     */
    public ArrayList<String> getArrayWebElementsInPaginations(By tableElements) throws Exception {
        List<String> all_elements_text = new ArrayList<>();
        boolean btnActive;
        do {
            List<WebElement> myList;
            List<String> current_elements = new ArrayList<>();

            myList = driver.findElements(tableElements);

            for (int i = 0; i < myList.size(); i++) {
                current_elements.add(myList.get(i).getText());
            }
            all_elements_text.addAll(current_elements);
            btnActive = selenium.notExist(LocatorsCuentas.Generico_Cuentas.BTN_NEXT_RIGHT_OFF); //true si el botón todavía no existe
            if (btnActive) {
                if (existElementWithOutException(LocatorsCuentas.Generico_Cuentas.BTN_NEXT_RIGHT_ON, 2)) {
                    selenium.scroll(1);
                    selenium.clickWithOutLog(LocatorsCuentas.Generico_Cuentas.BTN_NEXT_RIGHT_ON);
                    TimeUnit.SECONDS.sleep(5);
                } else {
                    break;
                }

            } else {
                break;
            }
        } while (btnActive);
        return (ArrayList<String>) all_elements_text;
    }

    public String getText(By locator) throws Exception {
        return driver.findElement(locator).getText();
    }

}