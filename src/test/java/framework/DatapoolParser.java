package framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import xlsx.XlsxParser;
import java.io.FileOutputStream;


/**
 * Script Name   : <b>DatapoolParser</b>
 * Generated     : <b>29-12-2017 09:19:10</b>
 * Description   : Functional Test Script
 * Original Host : WinNT Version 6.3  Build 9600 ()
 *
 * @since  2018/02/06
 * @author CJMS9785
 */
public class DatapoolParser {
    private XlsxParser xlsx;
    private ArrayList<String[]> list;
    private ArrayList<String[]> filterList;
    private String[] columns;


    public DatapoolParser() {
        this.xlsx = new XlsxParser("."+ File.separator+"datapool.xlsx");
        this.list = (ArrayList<String[]>) this.xlsx.getXlsxList();
        this.columns = this.xlsx.getColumns();
    }


    public DatapoolParser(String filename) {
        this.xlsx = new XlsxParser(filename);
        this.list = (ArrayList<String[]>) this.xlsx.getXlsxList();
        this.columns = this.xlsx.getColumns();
    }


    public DatapoolParser(String filename,String sheetName) {
        this.xlsx = new XlsxParser(filename,sheetName);
        this.list = (ArrayList<String[]>) this.xlsx.getXlsxList();
        this.columns = this.xlsx.getColumns();
    }

    public int getColumnIndex(String varName) {
        return Arrays.asList(this.columns).indexOf(varName);
    }


    public String getValue(int index, String varName) {
        try {
            return (String) this.getResults().get(index)[this.getColumnIndex(varName)];
        }catch(IndexOutOfBoundsException e) {
            return "";
        }
    }

    public String getValue(String varName) {
        try {
            return (String) this.getResults().get(0)[this.getColumnIndex(varName)];
        }catch(IndexOutOfBoundsException e) {
            return "";
        }
    }

    public String getValueRenew(String varName) {
        try {
            Integer col_pos = this.getColumnIndex(varName);
            String value = this.filterList.get(0)[col_pos-1];
            return value;
        }catch(IndexOutOfBoundsException e) {
            return "";
        }
    }

    public String getValueFromAll(String varName) {
        try {
            return (String) this.getAll().get(0)[this.getColumnIndex(varName)];
        }catch(IndexOutOfBoundsException e) {
            return "";
        }
    }

    public String getValueFromAll(int position, String varName) {
        try {
            return (String) this.getAll().get(position)[this.getColumnIndex(varName)];
        }catch(IndexOutOfBoundsException e) {
            return "";
        }
    }

    public List<String[]> getResults(){
        return this.filterList;
    }

    public List<String[]> getAll(){
        return this.list;
    }

    public DatapoolParser filter(String varName, String value, boolean caseSensitive){
        List<String> lines=Arrays.asList(value);
        Path file=Paths.get("cpIdTemp.txt");
        try {
            Files.write(file,lines,Charset.forName("UTF-8"));
        } catch (IOException e) {}
        ArrayList<String[]> result = new ArrayList<String[]>();
        Iterator<String[]> iterator = this.getAll().iterator();
        while(iterator.hasNext()) {
            String[] aux = iterator.next();
            @SuppressWarnings("unused")
            boolean check = (caseSensitive)?aux[this.getColumnIndex(varName)].equals(value):aux[this.getColumnIndex(varName)].toUpperCase().equals(value.toUpperCase());
            if(aux[this.getColumnIndex(varName)].equals(value))
                result.add(aux);
        }
        this.filterList = result;
        return this;
    }

    public DatapoolParser filter(String varName, String[] values, boolean caseSensitive){
        ArrayList<String[]> result = new ArrayList<String[]>();
        Iterator<String[]> iterator = this.getAll().iterator();
        while(iterator.hasNext()) {
            String[] aux = iterator.next();
            if(caseSensitive)
                Arrays.sort(values);
            else
                Arrays.sort(values,String.CASE_INSENSITIVE_ORDER);
            int searchResult = Arrays.binarySearch(values,aux[this.getColumnIndex(varName)],String.CASE_INSENSITIVE_ORDER);
            if(searchResult >= 0)
                result.add(aux);
        }
        this.filterList = result;
        return this;
    }


    public String getValuePGP(int  i) {
        try {

            String value = this.filterList.get(0)[i-1];
            return value;
        }catch(IndexOutOfBoundsException e) {
            return "";
        }
    }

    public String lecturaBody() throws Exception  {
        String valorCelda="";
        try {

            String archivoExcel = "." + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "logs" + File.separator + "responseBody.xlsx";
            FileInputStream file = new FileInputStream(new File(archivoExcel));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            // Seleccionar hoja
            XSSFSheet sheet = workbook.getSheetAt(0); // 0 = primera hoja
            // Leer celda
            XSSFRow row = sheet.getRow(1); // 0 = primera fila
            int columnas = row.getLastCellNum();
            XSSFCell cell = row.getCell(columnas-1); // 0 = primera columna
            // Obtener valor de la celda
            valorCelda = cell.getStringCellValue();
            System.out.println("Valor de la celda: " + valorCelda);
            file.close();

        }
        catch (Exception e){
            System.out.println("Error en lectura del body "+e.getMessage());
        }
        return valorCelda;
    }



    public void ingresoDataExcel(int fila, String valor) throws IOException {

            // Archivo Excel
            String archivoExcel = "." + File.separator + "DataPool_Servicios.xlsx";

            // Cargar archivo Excel
            FileInputStream file = new FileInputStream(new File(archivoExcel));
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            // Seleccionar hoja
            XSSFSheet sheet = workbook.getSheetAt(0); // 0 = primera hoja

            // Seleccionar celda
            XSSFRow row = sheet.getRow(fila); // 0 = primera fila
            XSSFCell cell = row.getCell(3); // 0 = primera columna

            // Cambiar valor de la celda
            cell.setCellValue(valor);

            // Guardar cambios
            FileOutputStream out = new FileOutputStream(new File(archivoExcel));
            workbook.write(out);
            out.close();

            // Cerrar archivo
            file.close();

            System.out.println("Celda actualizada exitosamente!");
        }


    public static void busquedaCP(String cpId, String valor) throws Exception {
        int i;
        DatapoolParser poolServicios = new DatapoolParser("."+ File.separator+"DataPool_Servicios.xlsx","Sheet1");
        Integer cantidadCasos = poolServicios.getAll().size();
        poolServicios.filter("CP", cpId, true);
        boolean encontrado=false;
        for (i=0; i<cantidadCasos;i++) {
            String cp = poolServicios.getValueFromAll(i, "CP").trim();
            if(cp.equalsIgnoreCase(cpId)){
                System.out.println("Caso de prueba encontrado en la posicion "+i+1);
                encontrado=true;
                break;
            }
        }
        if(!encontrado){
            throw new Exception("No se encontro el Servicio "+cpId+" en el DataPool_Servicios, favor revisar");
        }


        poolServicios.ingresoDataExcel(i+1,valor);




    }



    public JsonNode parseJsonBody() throws Exception  {
        JsonNode node=null;
        try {

            ObjectMapper mapper = new ObjectMapper();
            String escaped = StringEscapeUtils.escapeJson(lecturaBody());
            String unescaped = StringEscapeUtils.unescapeJson(escaped);
            node = mapper.readTree(unescaped);

        }
        catch (Exception e){
            System.out.println("Error al parsear el Response  Json  Body"+e.getMessage());
        }
        return node;
    }

    public Iterator<JsonNode> pathJsonBody(JsonNode node, String namePath) throws Exception  {
        Iterator<JsonNode> elementsJsonNode =null;
        try {

            JsonNode pathValue = node.path(namePath);
            elementsJsonNode = pathValue.elements();
        }
        catch (Exception e){
            System.out.println("Error al devolver la lista o path del Response  Json  Body"+e.getMessage());
        }
        return elementsJsonNode;
    }
}









