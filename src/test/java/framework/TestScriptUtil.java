package framework;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.joda.time.DateTime;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.List;
import java.util.*;

import static java.lang.Thread.sleep;


public class TestScriptUtil extends TestScriptBD {


    // Store execution log contais steps and images of the TC

    // Id of the current TestCase
    protected String cpID;
    // Name of the execution script used for log purposes
    protected String ruteIdCAEx;

    /**
     * Descripción: Separa un string ruta
     * @return nombreRuta
     */
    public String[] getRute() {
        String[] nombreRuta = ruteIdCAEx.split("\\.");
        return nombreRuta;
    }

    /**
     * Descripción: Data formato a fecha
     *
     * @return fecha
     */
    public String getFormatDate(String format) {
        Locale spanish = new Locale("es", "ES");
        SimpleDateFormat formater = new SimpleDateFormat(format, spanish);
        Date today = new Date();
        String fecha = formater.format(today).toUpperCase();
        return fecha;
    }

    public String getRandomRutPersonas() {
        int randomNum = new Random().nextInt((21000000 - 10000000) + 1) + 10000000;
        String rut = String.valueOf(randomNum);
        int cantidad = rut.length();
        int factor = 2;
        int suma = 0;
        String dVerificador;
        for (int i = cantidad; i > 0; i--) {
            if (factor > 7) {
                factor = 2;
            }
            suma += (Integer.parseInt(rut.substring((i - 1), i))) * factor;
            factor++;
        }
        dVerificador = String.valueOf(11 - suma % 11);
        if (dVerificador.equals("11")) {
            dVerificador = "0";
        } else if (dVerificador.equals("10")) {
            dVerificador = "K";
        }
        return rut + dVerificador;
    }


    /**
     * Descripcion Servicio que Consulta OTP en IDG
     *
     * @return Codigo OTP
     */
    public String consultaIDG(DatapoolParser pool) throws Exception {
        //System.out.println(pool.getValue("RUT"));
        String idg = "";
        URL url = new URL("http://soaqa.itauchile2.cl:10120");
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        String xmlBodyStr = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://itau.cl/xmlns/CrossChannel/PartyAuthentication/PartyAuthentication/GetOtpChallenge/1\">"
                + "\n   <soapenv:Header/>\n   <soapenv:Body>\n      <ns:GetOtpChallengeRq>\n         <MsgRqHdr>\n            <ContextRqHdr>"
                + "\n   <!--Canal:-->\n               <ChnlId>11</ChnlId>\n               <!--CodigoApp:-->\n               <AppId>10</AppId>"
                + "\n   </ContextRqHdr>\n            <SvcIdent>\n               <!--NombreApp:-->\n               <SvcName>GetOtpChallengeRq</SvcName>\n            </SvcIdent>"
                + "\n   </MsgRqHdr>\n         <PartySel>\n            <PersonIndicator>personas</PersonIndicator>"
                + "\n   <!--GrupoUsuario:-->\n            <IssuedIdentValue>" + pool.getValue("RUT") + "</IssuedIdentValue>"
                + "\n   <!--rut:-->\n         </PartySel>\n      </ns:GetOtpChallengeRq>\n   </soapenv:Body>\n</soapenv:Envelope>";

        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
            outputStream.write(xmlBodyStr.getBytes());
            outputStream.flush();
        }
        //SE VALIDA RESPONSE CODE OK
        if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    // SE BUSCA LA LINEA QUE CONTIENE EL ONETIMEPASS
                    if (line.contains("<Pswd>")) {
                        idg = line.substring(line.indexOf(">") + 1, line.indexOf("</Pswd>"));
                    }
                }
            }
        } else {
            // ... do something with unsuccessful response
            System.out.println("Error en Response Code");
        }
        return idg;
    }

    /**
     * Descripción: Validación de texto en documento PDF
     *
     * @param ruta  Del archivo pdf
     * @param texto a buscar en el archivo
     * @return true o false
     */
    public boolean getMatchTextFromPdf(String ruta, String texto) {
        PdfReader reader;
        String result;

        try {
            reader = new PdfReader(ruta);
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                result = PdfTextExtractor.getTextFromPage(reader, i);
                if (result.contains(texto)) {
                    log.addStep("Se ha encontrado el texto: " + texto);
                    return true;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Descripción: Método que busca en archivo Excel un texto especificado.
     *
     * @param ruta  donde se encuentra el archivo
     * @param texto se debe pasar un valor como String para Cadena o Numero
     * @param tipo  Especificar si el valor a buscar es texto o num
     * @return respuesta
     * Ejemplo selenium.findTextInExcel(excelFilePath, "13488","num")
     */
    public boolean findTextInExcel(String ruta, String texto, String tipo) throws IOException {
        FileInputStream inputStream = new FileInputStream(ruta);
        boolean respuesta = false;

        try {
            Workbook workbook = getWorkbook(inputStream, ruta);

            Sheet firstSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = firstSheet.iterator();

            double valor = 0;
            try {
                if (tipo.equalsIgnoreCase("num")) {
                    valor = Double.parseDouble(texto);
                }
            } catch (Exception e) {
                valor = 0;
            }
            while (iterator.hasNext()) {
                Row nextRow = iterator.next();
                Iterator<Cell> cellIterator = nextRow.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch (cell.getCellTypeEnum()) {
                        case STRING:
                            if (cell.getStringCellValue().contains(texto) && tipo.equalsIgnoreCase("texto")) {
                                log.addStep("Se ha encontrado el texto: " + cell.getStringCellValue());
                                respuesta = true;
                                workbook.close();
                                inputStream.close();
                            }
                            break;
                        case NUMERIC:
                            if (cell.getNumericCellValue() == valor && tipo.equalsIgnoreCase("num")) {
                                log.addStep("Se ha encontrado el valor numerico: " + cell.getNumericCellValue());
                                respuesta = true;
                            }
                            break;
                        case BLANK:
                            break;
                    }
                    if (respuesta) break;
                }
                if (respuesta) break;
            }
            workbook.close();
            inputStream.close();
            return respuesta;
        } catch (Exception ex) {
            return respuesta;
        }
    }

    /**
     * Descripción: Método que busca en archivo Excel un texto especificado. Sin dejar registro LOG
     *
     * @param ruta  donde se encuentra el archivo
     * @param texto se debe pasar un valor como String para Cadena o Numero
     * @param tipo  Especificar si el valor a buscar es texto o num
     * @return respuesta
     * Ejemplo selenium.findTextInExcel(excelFilePath, "13488","num")
     */
    public boolean findTextInExcelWithOutLog(String ruta, String texto, String tipo) throws IOException {
        FileInputStream inputStream = new FileInputStream(ruta);
        boolean respuesta = false;

        try {
            Workbook workbook = getWorkbook(inputStream, ruta);
            Sheet firstSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = firstSheet.iterator();
            double valor = 0;
            try {
                if (tipo.equalsIgnoreCase("num")) {
                    valor = Double.parseDouble(texto);
                }
            } catch (Exception e) {
                valor = 0;
            }

            while (iterator.hasNext()) {
                Row nextRow = iterator.next();
                Iterator<Cell> cellIterator = nextRow.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch (cell.getCellTypeEnum()) {
                        case STRING:
                            if (cell.getStringCellValue().contains(texto) && tipo.equalsIgnoreCase("texto")) {
                                respuesta = true;
                                workbook.close();
                                inputStream.close();
                            }
                            break;
                        case NUMERIC:
                            if (cell.getNumericCellValue() == valor && tipo.equalsIgnoreCase("num")) {
                                respuesta = true;
                            }
                            break;
                        case BLANK:
                            break;
                    }
                    if (respuesta) break;
                }
                if (respuesta) break;
            }
            workbook.close();
            inputStream.close();

            return respuesta;
        } catch (Exception ex) {
            return respuesta;
        }
    }

    /**
     * Descripción: Busca un archivo especifico por nombre, en la carpeta de descargas, se debe agrega el tipo de archivo (nombre.xls, nombre.pdf,etc).
     * @param archivo a buscar
     * @return existe
     */
    public boolean FindFileFromDownload(String archivo) throws Exception {
        boolean existe = false;
        String usuario = System.getProperty("user.name");
        //File arhivoDescargado = new File("C:"+File.separator+"Users"+File.separator+ usuario +File.separator+ "downloads"+File.separator+ archivo);
        File arhivoDescargado = new File(new File("downloads"+File.separator+ archivo).getAbsolutePath());
        if (arhivoDescargado.exists()) {
            existe = true;
            log.addStep("Archivo Encontrado: " + archivo + " ");
        } else {
            log.addStep("Archivo No Encontrado: " + archivo + " ");
            throw new Exception("Test Fallido Archivo No Encontrado");
        }
        return existe;
    }

    /**
     * Verify if a readable file (csv or txt)  contains a string term
     * @return
     */
    public boolean findTextInFile(String csvfile, String term) throws IOException {
        FileReader fileReader = new FileReader(csvfile);
        BufferedReader bufReader = new BufferedReader(fileReader);
        boolean exist = false;
        List<String> lines = new ArrayList<String>();
        String line = null;
        while ((line = bufReader.readLine()) != null) {
            lines.add(line);
        }
        bufReader.close();

        for (String line_content : lines) {
            exist = line_content.contains(term);
            if (exist) break;
        }

        return exist;
    }

    private Workbook getWorkbook(FileInputStream inputStream, String excelFilePath) throws IOException {
        Workbook workbook;
        if (excelFilePath.endsWith("xlsx") || excelFilePath.endsWith("XLSX")) {
            workbook = new XSSFWorkbook(inputStream);
        } else if (excelFilePath.endsWith("xls") || excelFilePath.endsWith("XLS")) {
            workbook = new HSSFWorkbook(inputStream);
        } else {
            throw new IllegalArgumentException("The specified file is not Excel file");
        }
        return workbook;
    }

    /**
     * Descripción: Elimina todos los archivos en la carpeta "descargas" del equipo local
     */
    public void deleteAllFilesFromDownload() throws Exception {
        String usuario = System.getProperty("user.name");
        //String directorio = "C:"+File.separator+"Users"+File.separator+ usuario +File.separator+ "Downloads"+File.separator;
        String directorio = new File("downloads"+File.separator).getAbsolutePath();
        File f = new File(directorio);
        String[] carpetas;
        try {
            if (f.isDirectory()) {
                carpetas = f.list();
                for (int i = 0; i < carpetas.length; i++) {
                    File file = new File(f, carpetas[i]);
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Descripción: Crea un destinatario con rut y nombre random
     */
    public String[] createDest() {
        String rut = getRandomRutPersonas();
        String name = getRandomString();
        List<String> lines = Arrays.asList(rut, name);
        Path file = Paths.get("destTemp.txt");
        try {
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String[]{rut, name};
    }

    /**
     * Descripción: Generación de caractéres RANDOM
     *
     * @return String random
     */
    public String getRandomString() {
        String characters = "qwertyuiopasdfghjklzxcvbnm";
        StringBuilder constructor = new StringBuilder();
        Random rand = new Random();
        while (constructor.length() < 10) {
            int index = (int) (rand.nextFloat() * characters.length());
            constructor.append(characters.charAt(index));
        }
        String saltString = constructor.toString();
        return saltString;
    }


    /**
     * Descripción: Generación de caractéres donde se le asigna el largo necesario
     * @param largo
     * @return
     */

    public String getRandomString(int largo) {
        String characters = "qwertyuiopasdfghjklzxcvbnm";
        StringBuilder constructor = new StringBuilder();
        Random rand = new Random();
        while (constructor.length() < largo) {
            int index = (int) (rand.nextFloat() * characters.length());
            constructor.append(characters.charAt(index));
        }
        String saltString = constructor.toString();
        return saltString;
    }

    /**
     * Descripción: Generación de Nombre completo RANDOM
     * @return String random
     */
    public String getRandomPersonFullName() {
        String name = getRandomString(1).toUpperCase() + getRandomString(5);
        String lastname = getRandomString(1).toUpperCase() + getRandomString(5);
        String lastname2 = getRandomString(1).toUpperCase() + getRandomString(5);

        return name + " " + lastname + " " + lastname2;
    }

    /**
     * Descripción: Generación de Nombre RANDOM
     * @return String random
     */
    public String getRandomPersonName() {
        String name = getRandomString(1).toUpperCase() + getRandomString(5);
        return name;
    }

    /**
     * Descripción: Generación de Apellido RANDOM
     * @return String random
     */
    public String getRandomPersonLastName() {
        String lastname = getRandomString(1).toUpperCase() + getRandomString(5);
        return lastname;
    }

    /**
     * Descripción: Generación de entero donde 'n' define la cantidad de digitos
     * @param n
     * @return
     */
    public String getRandomInt(int n) {
        String characters = "0123456789";
        StringBuilder constructor = new StringBuilder();
        Random rand = new Random();
        while (constructor.length() < n) {
            int index = (int) (rand.nextFloat() * characters.length());
            constructor.append(characters.charAt(index));
        }
        String saltString = constructor.toString();
        return saltString;
    }

    /**
     * Descripción: Generación de entero entre N y M incluidos
     * @param n
     * @return
     */
    public String getRandomBetweenInt(int n, int m) {
        StringBuilder constructor = new StringBuilder();
        Random rand = new Random();
        while (constructor.length() < n) {
            int valorEntero = (int) (Math. floor(Math. random()*(n-m+1)+m));
            constructor.append(valorEntero);
        }
        String saltString = constructor.toString();
        return saltString;
    }

    public String getRandomEmail() {
        int limiteIZQ = 48; // numero '0'
        int limiteDCH = 122; // letra 'z'
        int largoAN = 10; //largo alfanumérico
        Random random = new Random();
        String emailRandom = random.ints(limiteIZQ, limiteDCH + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(largoAN)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        String emailNuevo = emailRandom.toLowerCase() + "@test.cl";
        return emailNuevo;
    }

    /**
     * Descripción: Metodo de lectura de un documento word
     *
     * @param ruta
     * @param texto
     */
    public boolean findTextInWord(String ruta, String texto) throws FileNotFoundException {
        boolean encontrado = false;
        try (FileInputStream fis = new FileInputStream(ruta)) {
            XWPFDocument file = new XWPFDocument(OPCPackage.open(fis));
            XWPFWordExtractor ext = new XWPFWordExtractor(file);
            if (ext.getText().contains(texto))
                encontrado = true;
            return encontrado;
        } catch (Exception e) {
            System.out.println(e);
            return encontrado;
        }
    }

    /**
     * Execute a python call that run captcha solver script this algorithm split
     * captcha img and then try to identify character in that image, if the
     * certainty is greather than 70% the descifer captcha text is retuned
     *
     * @return
     * @throws IOException
     */
    public String getCaptchaString() throws IOException {
        try {
            sleep(2);
            Process runPythonProccess = Runtime.getRuntime().exec("python run.py", null,
                    new File("C:"+File.separator+"Workspaces"+File.separator+"RFT"+File.separator+"AutomatizacionWeb"+File.separator+"captchaSolver"+File.separator));

            BufferedReader in = new BufferedReader(new InputStreamReader(runPythonProccess.getInputStream()));
            String line = null;
            line = in.readLine();
            if (line.split(",").length == 2) {
                return Integer.valueOf(line.split(",")[1].split("\\.")[0]) >= 70 ? line.split(",")[0] : null;
            }
            return null;
        } catch (IOException | InterruptedException e) {
            System.err.println("Error : " + e.getMessage());
        }
        return null;
    }

    /**
     * Descripción: Método que elimina los archivos de una ruta, dejando uno especifico.
     *
     * @return
     */
    public void deleteAllExceptGitFile() throws IOException {
        String ruta = "C:"+File.separator+"Workspaces"+File.separator+"RFT"+File.separator+"AutomatizacionWeb"+File.separator+"captchaSolver"+File.separator+"img"+File.separator;
        File folders = new File(ruta);
        if (folders.exists()) {
            for (File file : folders.listFiles()) {
                if (file.isDirectory() || !file.isDirectory()) {
                    file.delete();
                    File f = new File(ruta + "noborrar.png");
                    FileWriter fw = new FileWriter(f);
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.close();
                }
            }
        }
    }

    /**
     * Descripción: Método que guarda la imagen del captcha para portal publico.
     *
     * @return
     */
    public void saveCaptcha() throws AWTException, InterruptedException {
        String location = "C:"+File.separator+"Workspaces"+File.separator+"RFT"+File.separator+"AutomatizacionWeb"+File.separator+"captchaSolver"+File.separator+"img"+File.separator;
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection ss = new StringSelection(location);
        cb.setContents(ss, ss);
        Robot r = new Robot();
        sleep(0, 5);
        // Two key downs to save file option
        r.keyPress(KeyEvent.VK_DOWN);
        r.keyRelease(KeyEvent.VK_DOWN);
        sleep(0, 5);
        r.keyPress(KeyEvent.VK_DOWN);
        r.keyRelease(KeyEvent.VK_DOWN);
        sleep(0, 5);
        // One enter to accept
        r.keyPress(KeyEvent.VK_ENTER);
        r.keyRelease(KeyEvent.VK_ENTER);
        sleep(0, 5);
        // Locate on the search bar
        r.keyPress(KeyEvent.VK_CONTROL);
        r.keyPress(KeyEvent.VK_F);
        r.keyRelease(KeyEvent.VK_CONTROL);
        r.keyRelease(KeyEvent.VK_F);
        sleep(0, 5);
        // Go back to dir bar
        r.keyPress(KeyEvent.VK_SHIFT);
        r.keyPress(KeyEvent.VK_TAB);
        sleep(0, 5);
        r.keyRelease(KeyEvent.VK_SHIFT);
        r.keyRelease(KeyEvent.VK_TAB);
        sleep(0, 5);
        r.keyPress(KeyEvent.VK_ENTER);
        r.keyRelease(KeyEvent.VK_ENTER);
        sleep(0, 5);
        // Paste the file dir
        r.keyPress(KeyEvent.VK_CONTROL);
        r.keyPress(KeyEvent.VK_V);
        r.keyRelease(KeyEvent.VK_CONTROL);
        r.keyRelease(KeyEvent.VK_V);
        sleep(0, 5);
        // Acept
        r.keyPress(KeyEvent.VK_ENTER);
        r.keyRelease(KeyEvent.VK_ENTER);
        sleep(0, 5);
        // Save the dir
        r.keyPress(KeyEvent.VK_ENTER);
        r.keyRelease(KeyEvent.VK_ENTER);
        sleep(2);
        // Save the form
        r.keyPress(KeyEvent.VK_ENTER);
        r.keyRelease(KeyEvent.VK_ENTER);
        sleep(2);
        // Exit
        r.keyPress(KeyEvent.VK_ENTER);
        r.keyRelease(KeyEvent.VK_ENTER);
        sleep(3);
    }

    /**
     * Descripción: Metodo para leer archivos properties, estos archivos por defecto estaran dentro de la carpeta src/test/java/resources/
     *
     * @param archivo
     */

    public Properties loadProp(String archivo) throws IOException {
        Properties prop = new Properties();
        try {
            InputStream is = new FileInputStream("src/test/java/resources/" + archivo + ".properties");
            prop.load(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return prop;
    }


    /**
     * Descripción: Método que elimina un archivo en especifico de la carpeta download
     *
     * @param archivo
     */
    public void deleteFileFromDownloads(String archivo) throws IOException {
        String usuario = System.getProperty("user.name");
        //File arhivoDescargado = new File("C:"+File.separator+"Users"+File.separator+ usuario + File.separator+"Downloads"+File.separator+ archivo);
        File arhivoDescargado = new File(new File("downloads"+File.separator+ archivo).getAbsolutePath());
        boolean estatus = arhivoDescargado.delete();
        try {
            if (!estatus) {

                log.addStep("archivo no encontrado");

            } else {

                log.addStep("archivo borrado exitosamente");

            }
        } catch (Exception e) {

            log.addStep("Archivo no encontrado");

        }
    }

    /*******************
     * Descripción: Devuelve solo los números de la cadena enviada
     * @param textoConNumeros
     */

    public String getNumbers(String textoConNumeros) {
        char[] cadena = textoConNumeros.toCharArray();
        String soloNumeros = "";
        for (int i = 0; i < cadena.length; i++) {
            if (Character.isDigit(cadena[i])) {
                soloNumeros += cadena[i];
            }
        }
        return soloNumeros;
    }

    /**
     * Descripción: Método que busca en archivo Excel un texto especificado, sin dejar registro en Log.
     * @param ruta
     * @param texto
     * @return respuesta
     */

    public boolean findTextInExcelMethod(String ruta, String texto) throws IOException {
        FileInputStream inputStream = new FileInputStream(new File(ruta));
        boolean respuesta = false;

        try {
            Workbook workbook = getWorkbook(inputStream, ruta);

            Sheet firstSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = firstSheet.iterator();

            int valor = 0;
            try {
                valor = Integer.parseInt(texto);
            } catch (Exception e) {
                valor = 0;
            }

            while (iterator.hasNext()) {
                Row nextRow = iterator.next();
                Iterator<Cell> cellIterator = nextRow.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            if (cell.getStringCellValue().contains(texto)) {
                                respuesta = true;
                                workbook.close();
                                inputStream.close();
                            }
                            break;
                        case Cell.CELL_TYPE_NUMERIC:

                            if (cell.getNumericCellValue() == valor) {
                                respuesta = true;
                            }
                            break;
                    }
                }
            }
            workbook.close();
            inputStream.close();

            return respuesta;
        } catch (Exception ex) {
            return respuesta;
        }
    }

    /**
     * Método para ordenar un arreglo de fechas (DD-MM-AAAA) de forma descendente
     * @param arr
     * @return
     */
    public ArrayList<String> sortDatesDescending(ArrayList<String> arr)
    {
        Collections.sort(arr, (date1, date2) -> {
            String day1 = date1.substring(0, 2);
            String month1 = date1.substring(3, 5);
            String year1 = date1.substring(6);

            String day2 = date2.substring(0, 2);
            String month2 = date2.substring(3, 5);
            String year2 = date2.substring(6);

            // Condition to check the year
            if (year2.compareTo(year1) > 0)
                return -1;
            else if (year2.compareTo(year1) < 0)
                return 1;

                // Condition to check the month
            else if (month2.compareTo(month1) > 0)
                return -1;
            else if (month2.compareTo(month1) < 0)
                return 1;

                // Condition to check the day
            else if (day2.compareTo(day1) > 0)
                return -1;
            else
                return 1;
        });

        // Loop to print the dates
        for(int i = arr.size()-1;  i >= 0; i--)
            System.out.println(arr.get(i));
        return arr;
    }

    /**
     * Método para obtener la fecha estado transferencia diaria
     * @param rut
     * @param destino
     * @return
     */
    public String getFechaEstadoTransferenciaDiaria(String rut, String destino) {
        String jdbcClassName = "com.ibm.as400.access.AS400JDBCDriver";
        String url = "jdbc:as400:10.181.148.11";
        String user = "CMLM2074";
        String password = "ISERIES";
        ResultSet rs = null;
        String resultado = "";
        String result = "";
        Connection connection = null;
        String dest = "";

        switch (destino.toUpperCase()) {
            case "PROVEEDORES":
                dest = "1";
                break;
            case "REMUNERACIONES":
                dest = "2";
                break;
            default:
                System.out.println("Destinatario no identificado");
        }

        try {
            // Load class into memory
            Class.forName(jdbcClassName);
            // Establish connection
            connection = (Connection) DriverManager.getConnection(url, user, password);
            Statement statement = connection.createStatement();

            rs = statement.executeQuery("SELECT RMFECR FROM SAFILES.wbm728 WHERE RMRUTM LIKE '%" + rut
                    + "%' AND RMRECM = '' AND RMTSUP = " + dest + " ORDER BY RMFECR DESC LIMIT 1");
            while (rs.next()) {
                resultado = rs.getString(1);
            }

            String[] arreglo = resultado.split("");
            result = arreglo[4];
            result += arreglo[5];
            result += "/" + arreglo[2];
            result += arreglo[3];
            result += "/20" + arreglo[0];
            result += arreglo[1];

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * Le resta los dias requeridos a la fecha del sistema.
     * @param dias
     * @return Date
     */
    public Date restarDiasAFechaSistema(int dias){
        Calendar calendar = Calendar.getInstance();
        Date fecha = calendar.getTime();
        calendar.setTime(fecha);
        calendar.add(Calendar.DAY_OF_YEAR, -dias);
        return calendar.getTime();
    }

    /**
     * Devuelve el número de cuenta EN PESOS según el rut ingresado
     * Si el rut tiene asociado mas de una cuenta, el resultado de la query traerá la primera cuenta asociada al rut
     *
     * @param rut --> rut a consultar su cuenta
     * @return
     */
    public String getNumeroCuentaEmpresa(String rut) {
        String jdbcClassName = "com.ibm.as400.access.AS400JDBCDriver";
        String url = "jdbc:as400:10.181.148.11";
        String user = "FUNCIONAL";
        String password = "ISERIES";
        ResultSet rs = null;
        String resultado = "";
        Connection connection = null;
        try {
            int rutSinDv = Integer.parseInt(getRutNoverificationCode(rut));
            // Load class into memory
            Class.forName(jdbcClassName);
            // Establish connection
            connection = (Connection) DriverManager.getConnection(url, user, password);
            Statement statement = connection.createStatement();
            rs = statement.executeQuery("SELECT DISTINCT PERRUT||PERDIG AS RUT, PERTIP AS CUENTA, PERNUM AS NUMERO FROM SAFILES.\"SF.PERPRO\" "
                    + "WHERE PERRUT IN ('" + rutSinDv + "') AND PERTIP IN ('CA') ORDER BY NUMERO DESC");
            while (rs.next()) {
                resultado = rs.getString(3);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultado.trim();

    }

    /**
     * Descripción: devuelve rut sin digito verificador
     */
    public String getRutNoverificationCode(String rut) {
        int m = Math.max(0, rut.length() - 1);
        String rutMod = rut.subSequence(0, m).toString();
        return rutMod;
    }

    /**
     * MODIFICA EL EXCEL SEGUN LA RUTA MENCIONADA PARA NOMINAS
     * La nómina debe ser en formato para remunaraciones o proveedores.
     *
     * @param origenNomina --> ruta de nómina( por default \\Upload\\CargaNominasCDP\\CDP_"R O P"\\ segun corresponda
     * @param rutEmpresa --> rut completo empresa
     * @param nCuenta --> número de cuenta asociado al rut de empresa
     * @throws IOException
     */
    public void cargaNominaRyP(String origenNomina, String rutEmpresa, String nCuenta) throws IOException {
        FileInputStream excelFileInputStream = new FileInputStream(origenNomina);
        HSSFWorkbook workbook = new HSSFWorkbook(excelFileInputStream);
        excelFileInputStream.close();

        HSSFSheet sheet1 = workbook.getSheetAt(1);

        HSSFCell colRutEmpresa = sheet1.getRow(2).getCell(1);
        colRutEmpresa.setCellValue(rutEmpresa);
        HSSFCell colNumCuenta = sheet1.getRow(8).getCell(1);
        colNumCuenta.setCellValue(nCuenta.trim());

        workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
        FileOutputStream excelFileOutPutStream = new FileOutputStream(origenNomina);
        workbook.write(excelFileOutPutStream);
        excelFileOutPutStream.flush();
        excelFileOutPutStream.close();
    }

     /**
     * Le resta los meses requeridos a la fecha actual del sistema.
     * @param meses
     * @return Date
     */
    public Date restarMesesAFechaSistema(int meses){
        Calendar fcalendario = GregorianCalendar.getInstance();
        Date fecha = new Date();
        fecha = new DateTime(fecha).minusMonths(meses).toDate();
        fcalendario.setTime(fecha);
        return fcalendario.getTime();
    }

    /**
     * Le resta los meses requeridos a la fecha actual del sistema y obtiene el numero del mes.
     * @param meses
     * @return int
     */
    public int obtenerMesRestadoAFechaSistema(int meses){
        Calendar fcalendario = GregorianCalendar.getInstance();
        Date fecha = new Date();
        fecha = new DateTime(fecha).minusMonths(meses).toDate();
        fcalendario.setTime(fecha);
        return fcalendario.get(Calendar.MONTH)+1;
    }

    /**
     * Despliega la ventana de impresión de Chrome y luego descarga el archivo que se esta imprimiendo con el nombre: 'prueba.pdf' que enviemos como parametro al metodo
     * @param nombre
     * @return
     */
    public void printAndDownloadWindow(String nombre) throws Exception {
        Robot r = new Robot();
        StringSelection s = new StringSelection(new File("downloads"+File.separator+nombre).getAbsolutePath());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(s, null);
        r.delay(8000);
        r.keyPress(KeyEvent.VK_CONTROL);
        r.keyPress(KeyEvent.VK_V);
        r.keyRelease(KeyEvent.VK_V);
        r.keyRelease(KeyEvent.VK_CONTROL);
        r.keyPress(KeyEvent.VK_ENTER);
        r.keyRelease(KeyEvent.VK_ENTER);
        r.delay(5000);
        if(FindFileFromDownload(nombre)){
            log.addStep("Se despliega impresión y descarga archivo exitosamente "+ cpID);
        }else
        {
            log.addStep("No se logra validar impresión y descarga de archivo "+ cpID);
            throw new Exception("No se descargo el archivo "+nombre);
        }
        deleteFileFromDownloads(nombre);
    }

    /**
     * Le suma los dias requeridos a la fecha del sistema.
     * @param dias
     * @return Date
     */
    public Date sumarDiasAFechaSistema(int dias){
        Calendar calendar = Calendar.getInstance();
        Date fecha = calendar.getTime();
        calendar.setTime(fecha);
        calendar.add(Calendar.DAY_OF_YEAR, +dias);
        return calendar.getTime();
    }

    /**
     * Guarda en un txt y en xlsx los datos de los casos de prueba que hayan finalizado
     * en estado Passed.
     * El formato del excel es identico al DataPool para que pueda ser utiliado directamentamente para la ejecucion
     * de los casos de pruebas. La ruta de este DataPool se encuentra en la raiz de este proyecto en la carpeta: Evidencia\\Resultados
     */
    protected void generateDataPoolPassed(String cpID)  {
        try{
            DatapoolParser pool = new DatapoolParser("."+File.separator+"DataPool.xlsx");
            pool.filter("CP", cpID, false);

            String[] IDCaex = this.ruteIdCAEx.split("[, ?.@]+");
            LocalDateTime dateTime = LocalDateTime.now();
            DateTimeFormatter formatters = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            Month mes = LocalDate.now().getMonth();
            String nombreMes = mes.getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
            String primeraLetra=nombreMes.substring(0,1).toUpperCase();
            nombreMes=nombreMes.substring(1, nombreMes.length());
            nombreMes=primeraLetra+nombreMes;

            String filePathString = new File("Evidencia"+File.separator+"Resultados"+File.separator+"DataPool_Passed_"+pool.getValue("PLATAFORMA")+"_"+nombreMes+".txt").getAbsolutePath();
            String  filePathExel = new File("Evidencia"+File.separator+"Resultados"+File.separator+"DataPool_Passed_"+pool.getValue("PLATAFORMA")+"_"+nombreMes+".xlsx").getAbsolutePath();

            File fileText = new File(filePathString);

            if (!fileText.exists()) {
                fileText.getParentFile().mkdirs();
                fileText.createNewFile();
            }

            if (fileText.exists() && !fileText.isDirectory()) {

                String cabecera = "CP;RUT;PASSWORD;DATA;ID_CAEx;PLATAFORMA;FUNCIONALIDAD;DETALLE;BROWSER;DATE";
                String dataLine = cpID + ";" + pool.getValue("RUT") + ";"+pool.getValue("PASSWORD") +";" + pool.getValue("DATA") + ";" + pool.getValue("ID_CAEx") + ";" + pool.getValue("PLATAFORMA") + ";"
                        + pool.getValue("FUNCIONALIDAD") + ";" +pool.getValue("DETALLE")+ ";" +pool.getValue("BROWSER")+";"+dateTime.format(formatters);

                FileReader fr = new FileReader(filePathString);
                BufferedReader bf = new BufferedReader(fr);
                long numLineas = bf.lines().count();

                if (numLineas > 0) {
                    try {
                        BufferedWriter bw = new BufferedWriter(new FileWriter(filePathString, true));
                        bw.newLine();
                        bw.append(dataLine);
                        bw.close();
                    } catch (IOException e) {
                    }

                } else {

                    try {
                        BufferedWriter bw = new BufferedWriter(new FileWriter(filePathString, true));
                        bw.append(cabecera);
                        bw.newLine();
                        bw.append(dataLine);
                        bw.close();
                    } catch (IOException e) {
                    }

                }
                bf.close();
            }
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("DataPool");
            try (BufferedReader br = new BufferedReader(new FileReader(filePathString))) {
                String line;
                Row row;
                Cell cell;
                int rowIndex = 0;
                while ((line = br.readLine()) != null) {
                    row = sheet.createRow(rowIndex);
                    String[] tokens = line.split(";");
                    for(int iToken = 0; iToken < tokens.length; iToken++) {
                        cell = row.createCell(iToken);
                        cell.setCellValue(tokens[iToken]);
                    }
                    rowIndex++;
                }
            }
            try (FileOutputStream outputStream = new FileOutputStream(filePathExel)) {
                workbook.write(outputStream);
                workbook.close();
            }

        } catch(Exception e){
            e.printStackTrace();
        }

    }

}