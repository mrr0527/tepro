package framework;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Log {
    private List<String> textLog = new ArrayList<>();
    private List<BufferedImage> imageLog = new ArrayList<>();
    private List<String []> resumeInfo = new ArrayList<>();
    private List<String []> tableInfo = new ArrayList<>();
    private WebDriver driver;
  //TestScriptWebSelenium selenium= null;
    private String Ejecucion = null;

    public void setDriver(WebDriver driver) {
        this.driver=driver;
    }
    public void setEjecucion(String Ejecucion) {
        this.Ejecucion=Ejecucion;
    }

    /**
     * @Description: Añade paso a la evidencia con screenshoot obligatorio
     * driver
     * @Ejemplo: "Se realiza login de Persona"
     **/

    public void addStepHeadless(String action) {

        try {
            if(Ejecucion.toUpperCase().equals("HEADLESS"))
            {
                getTextLog().add(action);
                File screenshot = ((TakesScreenshot) (driver)).getScreenshotAs(OutputType.FILE);
                BufferedImage image = ImageIO.read(screenshot);
                getImageLog().add(image);
            }else if(Ejecucion.toUpperCase().equals("DEFAULT")){
                getTextLog().add(action);
                Robot robot = new Robot();
                Thread.sleep((long) 0.5);
                Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
                getImageLog().add(screenFullImage);
            }
        } catch (InterruptedException | IOException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void addStep(String action) {

           try {
               if(Ejecucion.toUpperCase().equals("HEADLESS"))
               {
                   getTextLog().add(action);
                   File screenshot = ((TakesScreenshot) (driver)).getScreenshotAs(OutputType.FILE);
                   BufferedImage image = ImageIO.read(screenshot);
                   getImageLog().add(image);
               }else if(Ejecucion.toUpperCase().equals("DEFAULT")){
                   getTextLog().add(action);
                   Robot robot = new Robot();
                   Thread.sleep((long) 0.5);
                   Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                   BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
                   getImageLog().add(screenFullImage);
               }
        } catch (InterruptedException | IOException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description: Añade paso a la evidencia con screenshoot a elección
     * @param action Paso que se realiza
     * @param take_screenshoot True o False para tomar screenshoot
     * @Ejemplo: "Se realiza login de Persona", false
     **/

    public void addStep(String action, boolean take_screenshoot) {

        try {
            if(Ejecucion.toUpperCase().equals("HEADLESS"))
            {
                getTextLog().add(action);
                if(take_screenshoot) {
                    getTextLog().add(action);
                    File screenshot = ((TakesScreenshot) (driver)).getScreenshotAs(OutputType.FILE);
                    BufferedImage image = ImageIO.read(screenshot);
                    getImageLog().add(image);
                }
            }else if(Ejecucion.toUpperCase().equals("DEFAULT")){
                Robot robot = new Robot();
                getTextLog().add(action);
                if (take_screenshoot) {
                    Thread.sleep((long) 0.5);
                    Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                    BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
                    getImageLog().add(screenFullImage);
                }
            }
        } catch (InterruptedException | IOException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }




    /**
     * @Description: Añade paso a la evidencia con Locator utilizado
     * @param action Paso que se realiza
     * @param locator Objeto sobre el que se realizo la action
     * @Ejemplo: "Click Sobre:", locator
     **/

    public void addStepBy(String action, By locator) {
        try {
            if(Ejecucion.toUpperCase().equals("HEADLESS"))
            {
                String descriptor = getObjectFromLocatorBy(locator);
                getTextLog().add(action + descriptor);
                File screenshot = ((TakesScreenshot) (driver)).getScreenshotAs(OutputType.FILE);
                BufferedImage image = ImageIO.read(screenshot);
                getImageLog().add(image);
            }else if(Ejecucion.toUpperCase().equals("DEFAULT")){
                Robot robot = new Robot();
                String descriptor = getObjectFromLocatorBy(locator);
                getTextLog().add(action + descriptor);
                Thread.sleep((long) 0.5);
                Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
                getImageLog().add(screenFullImage);
            }

        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description:
     * @param:
     **/

    public String getObjectFromLocatorBy(By locator) {
        String campo = null;
        if ((locator.toString()).length() == 2) {
            campo = locator.toString();
        } else if ((locator.toString()).length() >= 3) {
            campo = locator.toString();
        }
        return campo;
    }


    public void inicioTestAutomation()  {
        String ruta = "./Evidence_Pipeline/estadoEjecucion.txt";
        String contenido = "*******************************Se inicia la ejecucion de los scripts automatizados***********************************";
        File file = new File(ruta);

      try {
          // Si el archivo no existe es creado
          if (!file.exists()) {
              FileWriter fw = new FileWriter(file);
              BufferedWriter bw = new BufferedWriter(fw);
              file.createNewFile();
              bw.write(contenido);
              bw.close();
          }
      }
      catch (IOException e){
               }
    }

    public void ingresoTestCase(String testCase)  {
        String ruta = "./Evidencia_Pipeline/estadoEjecucion.txt";

        File file = new File(ruta);
        String sCadena;

        try {
            String dateTime = DateTimeFormatter.ofPattern("MMM dd yyyy, hh:mm:ss a")
                    .format(LocalDateTime.now());
            Calendar fecha = Calendar.getInstance();
            int anio = fecha.get(Calendar.YEAR);
            String mes = String.format("%02d", (fecha.get(Calendar.MONTH) + 1));
            String dia = String.format("%02d",fecha.get(Calendar.DAY_OF_MONTH));
            String hora = String.format("%02d",fecha.get(Calendar.HOUR_OF_DAY));
            String minuto = String.format("%02d",fecha.get(Calendar.MINUTE));
            String segundo = String.format("%02d",fecha.get(Calendar.SECOND));
            FileReader fr = new FileReader(file);
            BufferedReader bf = new BufferedReader(fr);
            String line="";
            long lNumeroLineas = 1;
            while ((line = bf.readLine()) != null) {
                if (line.contains("Test Nro")) {
                    lNumeroLineas++;
                }
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(ruta, true));
            bw.newLine();
            String contenido =  dia+"-"+mes+"-"+anio+ " - " + hora+":"+minuto+":"+segundo+" - Test Nro ("+lNumeroLineas+") iniciado  OK: "+ testCase  ;
            bw.append(contenido);
            bw.close();

        }
        catch (IOException e){
        }

    }

    public void finTestCase(String testCase,boolean estado)  {
        String ruta = "./resultados/casosFallidos.txt";
        File file = new File(ruta);
        String sCadena;

        try {
            FileReader fr = new FileReader(file);
            BufferedReader bf = new BufferedReader(fr);
            BufferedWriter bw = new BufferedWriter(new FileWriter(ruta, true));
            //bw.newLine();
            String resultado = (estado)?"OK":"NOK";
            if(resultado=="NOK"){
           //     String contenido1 =  dia+"-"+mes+"-"+anio+ "-" + hora+":"+minuto+":"+segundo+"-Class:" + rutaScript + "\n";
           //     String contenido2 =  dia+"-"+mes+"-"+anio+ "-" + hora+":"+minuto+":"+segundo+"-Test Finalizado "+resultado+": "+ testCase  ;
           // String contenido1 =  rutaScript + "\n";
            String contenido2 =  testCase +"\n";
           // bw.append(contenido1);
            bw.append(contenido2);
            bw.close();
            }
        }
        catch (IOException e){
        }

    }


    public List<String> getTextLog() {
        return textLog;
    }
    public void setTextLog(List<String> textLog) {
        this.textLog = textLog;
    }
    public List<BufferedImage> getImageLog() {
        return imageLog;
    }
    public void setImageLog(List<BufferedImage> imageLog) {
        this.imageLog = imageLog;
    }
    public List<String []> getTableInfo() {
        return tableInfo;
    }
    public void setTableInfo(List<String []> tableInfo) {
        this.tableInfo = tableInfo;
    }
    public List<String []> getResumeInfo() {
        return resumeInfo;
    }
    public void setResumeInfo(List<String []> resumeInfo) {
        this.resumeInfo = resumeInfo;
    }
}
