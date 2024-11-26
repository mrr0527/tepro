package framework;



import java.io.*;

import com.opencsv.CSVReader;
import locators.Locators;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.Test;


/**
 * Description : Functional Test Script
 *
 * @author CJDT1290
 */
public class MicroServicesUtil {
    //
    String env;
    String collection;
    ArrayList<String> requests;
    String script_container;
    String node_script;
    String output_json_log;
    private String execution_data;
    private LogServicios log;
    String working_directory;

    public MicroServicesUtil(String env,
                             String collection,
                             ArrayList<String> requests,
                             String script_container,
                             String script_name,
                             String output_json_log, String directorio) throws IOException {
        this.env = env;
        this.collection = collection;
        this.requests = requests;
        this.script_container = script_container;
        this.node_script = script_name + ".js";
        this.output_json_log = "response.postman_collection.log.json";
        //     this.output_json_log = output_json_log;
        this.execution_data = "[]";
        this.log = new LogServicios(script_container);
        String[] working = directorio.split("\\.", 3);
        directorio = (working.length < 3) ? "" : working[2];
        this.working_directory = directorio.replace(".", "/");
        log.crearLog(script_name);
        deleteFilelogs(this.output_json_log);
        crearLastReport();
        Locators.Process.NODE_RUNNER = Locators.Process.NODE_RUNNER.replace("//", File.separator + File.separator);

        if (System.getProperty("pipeline") == null || System.getProperty("pipeline").equals("")
                || System.getProperty("pipeline").equalsIgnoreCase("false")) {
            System.out.println("SIN_PIPE");
        } else if (System.getProperty("pipeline").equalsIgnoreCase("true")) {
            //Crear archivo fallidos
            archivoFallidos();
        }

    }


    public MicroServicesUtil(String env,
                             String collection,
                             ArrayList<String> requests,
                             String script_container,
                             String script_name,
                             String directorio) throws IOException {
        this.env = env;
        this.collection = collection;
        this.requests = requests;
        this.script_container = script_container;
        this.node_script = script_name + ".js";
        this.output_json_log = "response.postman_collection.log.json";
        //     this.output_json_log = output_json_log;
        this.execution_data = "[]";
        this.log = new LogServicios(script_container);
        String[] working = directorio.split("\\.", 3);
        directorio = (working.length < 3) ? "" : working[2];
        this.working_directory = directorio.replace(".", "/");
        log.crearLog(script_name);
        deleteFilelogs(this.output_json_log);
        Locators.Process.NODE_RUNNER = Locators.Process.NODE_RUNNER.replace("//", File.separator + File.separator);

    }

    public void runServiceTest(String cpID) {
        try {
            // Create data for execution
            writeExecutionData();

            // Create node script
            writeExecutionNodeScript(cpID);

            // Execute automation
            log.addStep("Node process ready to be executed");
            ProcessBuilder pb = new ProcessBuilder(Locators.Process.NODE, Locators.Dirs.PROJECT + Locators.Dirs.PLATAFORM + script_container + node_script);
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            Process p = pb.start();
            p.waitFor();
            log.addStep("Services automation test executed");

            FileInputStream inputStream = new FileInputStream(Locators.Dirs.PROJECT + Locators.Dirs.LOGs + output_json_log);
            String content = IOUtils.toString(inputStream, "UTF-8");

            FileInputStream inputStreamFullReport = new FileInputStream("." + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "logs" + File.separator + Locators.GeneralFiles.LAST_REPORT_GENERATED_FILE);
            String last_report = IOUtils.toString(inputStreamFullReport, "UTF-8");

            JSONObject execution_log = new JSONObject(content);
//          RationalTestScript.logTestResult("Contiene Informacion de Ejecución", execution_log.has("run"));
            log.addStep("Contiene Informacion de Ejecución " + Boolean.toString(execution_log.has("run")));

//          RationalTestScript.logTestResult("Contiene Informacion Estadistica",execution_log.getJSONObject("run").has("stats"));
            log.addStep("Contiene Informacion Estadistica " + Boolean.toString(execution_log.getJSONObject("run").has("stats")));

            String iterations = "Iteraciones de Validación" + execution_log.getJSONObject("run").getJSONObject("stats").getJSONObject("iterations").toString();
//          RationalTestScript.logInfo(iterations);
            log.addStep(iterations);

            String scripts = "Script de POSTMAN" + execution_log.getJSONObject("run").getJSONObject("stats").getJSONObject("scripts").toString();
//          RationalTestScript.logInfo(scripts);
            log.addStep(scripts);

            String assertions = "Verificaciones" + execution_log.getJSONObject("run").getJSONObject("stats").getJSONObject("assertions").toString();
//          RationalTestScript.logInfo(assertions);
            log.addStep(assertions);

            String testScripts = "Script de Prueba" + execution_log.getJSONObject("run").getJSONObject("stats").getJSONObject("testScripts").toString();
//          RationalTestScript.logInfo(testScripts);
            log.addStep(testScripts);

            JSONArray executions = execution_log.getJSONObject("run").getJSONArray("executions");
            String assertion_msg = "";
            String Endpoint = "";

            log.addStep(responseBody());

            for (int i = 0; i < executions.length(); i++) {
                JSONObject execution = (JSONObject) executions.get(i);
                JSONArray execution_assertions = execution.getJSONArray("assertions");

                String protocol = "";
                String host = "";
                String path = "";
                String port = "";
                try {
                    protocol = execution.getJSONObject("item").getJSONObject("request").getJSONObject("url").getString("protocol");
                    host = execution.getJSONObject("item").getJSONObject("request").getJSONObject("url").getJSONArray("host").join(".");
                    port = ":" + execution.getJSONObject("item").getJSONObject("request").getJSONObject("url").getString("port");
                    path = execution.getJSONObject("item").getJSONObject("request").getJSONObject("url").getJSONArray("path").join("/");
                } catch (Exception e) {
                    System.out.println("Warning missing prop:" + e.getMessage());
                }

                Endpoint = (protocol + "://" + host + port + "/" + path).replace("\"", "");
                assertion_msg += "Endpoint = " + Endpoint + "</br>";
                log.addStep("Endpoint: " + Endpoint);

                for (int j = 0; j < execution_assertions.length(); j++) {
                    JSONObject assertion = (JSONObject) execution_assertions.get(j);
                    assertion_msg += assertion.getString("assertion") + "</br>";
                    log.addStep("Test : " + assertion.getString("assertion"));
                }
                assertion_msg += "</br>";
            }

//          RationalTestScript.logInfo(assertion_msg);

            String fullReport = "More Details :" + "<a href=\"" + "." + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "reports" + File.separator + last_report + "\">OPEN FULL REPORT</a>";
//          RationalTestScript.logInfo(fullReport);
            log.addStep("Full Report : " + "." + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "reports" + File.separator + last_report);

            JSONArray failures = execution_log.getJSONObject("run").getJSONArray("failures");
//          this.vpManual("MAIN_VERIFICATION_POINT", true, failures.length() == 0).performTest();

            this.writeVerificationEntry("MAIN_VERIFICATION_POINT" + assertions);

        } catch (IOException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        } catch (InterruptedException | JSONException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        }
    }

    public String readLog() throws IOException {
        @SuppressWarnings("resource")
        BufferedReader br = new BufferedReader(new FileReader("." + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "logs" + File.separator + output_json_log));
        String reading = "";
        while ((reading = br.readLine()) != null) {
            if (reading.contains("\"failures\": [],")) {
                reading = "OK";
                break;
            } else if (reading.contains("\"error\":")) {
                reading = "NOK";
                break;
            } else {
                reading = "NOK";
            }
        }
        return reading;
    }

    public void writeExecutionData() throws FileNotFoundException, UnsupportedEncodingException {
        // JSONObject execution_data = new JSONObject(this.execution_data);
        String carpeta = Locators.Dirs.PROJECT + Locators.Dirs.EXECUTION_DATA + script_container;

        String file = Locators.Dirs.PROJECT + Locators.Dirs.EXECUTION_DATA + script_container + node_script.replace(".js", ".json");
        File directory = new File(carpeta);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String lines[] = this.execution_data.split("\\r?\\n");
        PrintWriter writer = new PrintWriter(file, "UTF-8");
//		for (int i = 0; i < lines.length; i++) {
//			writer.println(lines[i]);
//		}
        writer.println(this.execution_data);
        writer.close();
        log.addStep("Execution data file created: " + file);
    }

    public void writeExecutionNodeScript(String test_name) throws FileNotFoundException, UnsupportedEncodingException {
        String file = Locators.Dirs.PROJECT + Locators.Dirs.PLATAFORM + script_container + node_script;
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        writer.println("const NodeRunner = require(" + Locators.Process.NODE_RUNNER + ");");
        writer.println("let test_name = '" + test_name + "';");
        writer.println("let collection = '" + this.collection + "';");
        writer.println("let enviroment = '" + this.env + "';");
        writer.println("let directory = '" + this.working_directory + "';");
        String request_string = "";
        for (String request : this.requests) {
            request_string = request_string + "'" + request + "',";
        }
        request_string = request_string.substring(0, request_string.length() - 1);
        writer.println("let request = [" + request_string + "];");
        String edn = "let execution_data = '" + (script_container + node_script.replace(".js", ".json")) + "';";
        String ed = edn.replace("\\", "/");
        writer.println(ed);
        writer.println("NodeRunner.TestMicroService(test_name,collection,enviroment,directory,request,execution_data);");
        writer.close();
        log.addStep("Node postman connector file created: " + file);
    }

    public void writeHeadingOrFooter(boolean heading) {
        File report_file = new File("." + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "reports" + File.separator + "transactions" + File.separator + "test_list.txt");
        try {
            report_file.createNewFile();
            // if file already exists will do
            // nothing
            try (FileWriter fw = new FileWriter(report_file, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                if (heading) {
                    out.println("************************************************");
                    out.println("------------------------------------------------");
                    out.close();
                } else {
                    out.println("************************************************");
                    out.close();
                }

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } catch (IOException e1) {
            // TODO Bloque catch generado automáticamente
            e1.printStackTrace();
        }
    }

    public void writeVerificationEntry(String data) {
        try {
            File report_file = new File("." + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "reports" + File.separator + "transactions" + File.separator + "test_list.txt");
            report_file.createNewFile(); // if file already exists will do
            // nothing
            try (FileWriter fw = new FileWriter(report_file, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();

                out.println(dtf.format(now));
                out.println(data);
                out.println("------------------------------------------------");
                out.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            // exception handling left as an exercise for the reader
        }
    }

    public void generateGlobalStats() {
        int total_hours = 0;
        int total_transactions_test = 0;
        int total_transactions_valid_test = 0;
        int total_transactions_invalid_test = 0;

        int total_limit_rate_test = 0;
        int total_limit_rate_valid_test = 0;
        int total_limit_rate_invalid_test = 0;

        try {
            File report_file = new File("." + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "reports" + File.separator + "transactions" + File.separator + "test_list.txt");
            File global_stats_file = new File("." + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "reports" + File.separator + "transactions" + File.separator + "global_stats.txt");
            global_stats_file.createNewFile();
            PrintWriter writer = new PrintWriter(global_stats_file);
            writer.print("");
            writer.close();

            FileWriter fw = new FileWriter(global_stats_file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);

            try (BufferedReader br = new BufferedReader(new FileReader(report_file))) {
                boolean start_iteration = true;
                int line_aster = 0;

                int tests_t_v = 0;
                int tests_t_i = 0;
                int tests_lo_v = 0;
                int tests_lo_i = 0;

                for (String line; (line = br.readLine()) != null; ) {
                    if (line.contains("*")) {
                        line_aster++;
                    }

                    if (line.contains("*") && start_iteration) {
                        start_iteration = false;
                        out.println("-------------------------------------------------");
                        out.println("Verificacion API Hora: " + total_hours);
                        total_hours++;
                    }

                    if (line.contains("TR_")) {
                        total_transactions_test++;
                        int failed_start_pos = line.lastIndexOf(":");
                        int end_sentece = line.lastIndexOf("}");
                        int failed_value = Integer.parseInt(line.substring(failed_start_pos + 1, end_sentece));
                        if (failed_value == 0) {
                            total_transactions_valid_test++;
                            tests_t_v++;
                        } else {
                            total_transactions_invalid_test++;
                            tests_t_i++;
                        }
                    }

                    if (line.contains("RL_")) {
                        total_limit_rate_test++;
                        int failed_start_pos = line.lastIndexOf(":");
                        int end_sentece = line.lastIndexOf("}");
                        int failed_value = Integer.parseInt(line.substring(failed_start_pos + 1, end_sentece));
                        if (failed_value == 0) {
                            total_limit_rate_valid_test++;
                            tests_lo_v++;
                        } else {
                            total_limit_rate_invalid_test++;
                            tests_lo_i++;
                        }
                    }

                    if (line.contains("*") && !start_iteration && (line_aster > 1)) {
                        out.println("Pruebas:" + (tests_t_v + tests_t_i + tests_lo_v + tests_lo_i));
                        out.println("Transacciones - Válidas: " + tests_t_v);
                        out.println("Transacciones - Inálidas: " + tests_t_i);
                        out.println("Limite de Operaciones - Válidas: " + tests_lo_v);
                        out.println("Limite de Operaciones - Inválidas: " + tests_lo_i);
                        out.println("-------------------------------------------------");
                        tests_t_v = 0;
                        tests_t_i = 0;
                        tests_lo_v = 0;
                        tests_lo_i = 0;
                        start_iteration = true;
                        line_aster = 0;
                    }
                }
                // line is not visible here.
            }

            out.println("Horas Totales : " + total_hours);
            out.println("");
            out.println("Transacciones:");
            out.println("Total Pruebas :" + total_transactions_test);
            out.println("Total Pruebas Válidas :" + total_transactions_valid_test);
            out.println("Total Pruebas Invalidas :" + total_transactions_invalid_test);
            out.println("");
            out.println("Límite de Operaciones:");
            out.println("Total Pruebas :" + total_limit_rate_test);
            out.println("Total Pruebas Válidas :" + total_limit_rate_valid_test);
            out.println("Total Pruebas Invalidas :" + total_limit_rate_invalid_test);
            out.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public String getExecutionData() {
        return execution_data;
    }

    public void setExecutionData(String execution_data) {
        log.addStep("Setting Execution Data");
        this.execution_data = execution_data;
    }

    public void endCaseOk() throws Exception {
        log.endCaseOk();

    }

    public void endCaseError(Exception e) throws Exception {
        log.endCaseError(e);

    }


    public static Properties loadProp(String archivo) throws InterruptedException {
        Properties prop = new Properties();
        InputStream is = null;
        try {
            is = new FileInputStream("src" + File.separator + "test" + File.separator + "java" + File.separator + "resources" + File.separator + archivo + ".properties");
            prop.load(is);
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        return prop;
    }

    public void saveLog() {
        try {
            log.saveLog();
            deleteFileJs();
            deleteFileExecuteData();

        } catch (Exception e) {

        }

    }

    /**
     * Descripción: Método que elimina los archivos logs
     *
     * @param archivo
     */
    public void deleteFilelogs(String archivo) throws IOException {
        File arhivoDescargado = new File(new File("." + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "logs" + File.separator + archivo).getAbsolutePath());
        boolean estatus = arhivoDescargado.delete();
        try {
            if (!estatus) {

                log.addStep("archivo log no encontrado");

            } else {

                log.addStep("archivo log borrado para evitar un falso positivo");

            }
        } catch (Exception e) {

            log.addStep("archivo log no encontrado");

        }
    }


    /**
     * Descripción: Método que elimina los archivos Js
     */
    public void deleteFileJs() {


        File arhivoDescargado = new File(new File("." + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "comunidades/tesoreria" + File.separator + this.node_script).getAbsolutePath());
        boolean estatus = arhivoDescargado.delete();
        try {
            if (!estatus) {

                log.addStep("archivo JS no encontrado");

            } else {

                log.addStep("archivo JS borrado");

            }
        } catch (Exception e) {

            log.addStep("archivo JS no encontrado");

        }
    }


    /**
     * Descripción: Método que elimina los archivos Js
     */
    public void deleteFileExecuteData() {


        File arhivoDescargado = new File(new File("." + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "execution_data/tesoreria" + File.separator + node_script.replace(".js", ".json")).getAbsolutePath());
        boolean estatus = arhivoDescargado.delete();
        try {
            if (!estatus) {

                log.addStep("archivo JS no encontrado");

            } else {

                log.addStep("archivo JS borrado");

            }
        } catch (Exception e) {

            log.addStep("archivo JS no encontrado");

        }
    }


    public void archivoFallidos() {

        File f = new File("." + File.separator + "resultados", "casosFallidos.txt");
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
     * Descripción: Método que elimina los archivos logs
     */
    public void crearLastReport() throws IOException {
        String last_report = "." + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "logs" + File.separator + "last_report.txt";
        File file = new File(last_report);
        try {
            if (!file.exists()) {
                file.createNewFile();
                log.addStep("archivo last_report no encontrado se crea automaticamente");

            } else {

                log.addStep("archivo last_report encontrado no es necesario su creacion");

            }
        } catch (Exception e) {

            log.addStep("archivo last_report no encontrado");

        }
    }

    public void convertirCsvXlsx() throws Exception {


        try {
            String csvFile = "." + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "logs" + File.separator + "body.csv";
            // Archivo XLSX de salida
            String xlsxFile = "." + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "logs" + File.separator + "responseBody.xlsx";
            // Leer archivo CSV
            CSVReader reader = new CSVReader(new FileReader(csvFile));
            // Crear archivo XLSX
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("body");
            // Iterar sobre filas del CSV
            String[] fila;
            int rowNum = 0;
            while ((fila = reader.readNext()) != null) {
                XSSFRow row = sheet.createRow(rowNum++);
                for (int i = 0; i < fila.length; i++) {
                    XSSFCell cell = row.createCell(i);
                    cell.setCellValue(fila[i]);
                }
            }
            // Guardar archivo XLSX
            FileOutputStream out = new FileOutputStream(new File(xlsxFile));
            workbook.write(out);
            out.close();
            System.out.println("Conversion exitosa!");
        } catch (Exception e) {
            System.out.println("Error en convertir el archivo csv a xlsx"+e.getMessage());
        }
    }

    public String responseBody(){
        try {
            String csvFile = "." + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "logs" + File.separator + "body.csv";
            CSVReader reader = new CSVReader(new FileReader(csvFile));
            String body="";
            String[] fila;
            int rowNum = 0;
            while ((fila = reader.readNext()) != null) {
                rowNum++;
                for (int i = 0; i < fila.length; i++) {
                    if(i== fila.length-1 && rowNum==2){
                        body= fila[i];
                    }
                }
            }
            //  System.out.println("**************response body: "+ body);
            return  "Response Body"+body;
        }
        catch (Exception e){
            return "Error al devolver el response body "+e;
        }

    }



}




