package framework;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Inet4Address;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LogServicios {
    // -------------------------Log Vars--------------------------
    private String EXTENSION_PDF;
    private String EXTENSION_ARCHIVO_TEXTO;
    private String CARPETA_DISPOSITIVO;
    private String RUTA_CARPETA_EVIDENCIA;
    private String EVIDENCE_CONTAINER;
    @SuppressWarnings("unused")
    private String IMG_ENCABEZADO_PDF;
    private Integer indice;
    private Calendar c;
    private String fecha;
    private File logTxt;
    private PrintWriter pw;
    private Document logPDF;
    private PdfContentByte pdfCB;
    private String current_case;

    //Variable Evidencia
    private String evidencia;
    // Start time of the execution instance
    protected long startTime;
    // End time of the execution instance
    protected long endTime;
    private List<LogPortion> logPortions;

    public LogServicios(String script_container) {
        // Log
        EXTENSION_PDF = ".pdf";
        EXTENSION_ARCHIVO_TEXTO = ".txt";
        CARPETA_DISPOSITIVO = "service"+File.separator;
        RUTA_CARPETA_EVIDENCIA =   "."+File.separator+"src"+File.separator+"test"+File.separator+"java"+File.separator+"evidence"+File.separator;
        IMG_ENCABEZADO_PDF = "."+File.separator+"src"+File.separator+"test"+File.separator+"assets"+File.separator+"bar.png";
        indice = 0;
        c = Calendar.getInstance();
        logPortions = new ArrayList<>();
        EVIDENCE_CONTAINER = script_container;
    }

    /**
     * Obtiene la fecha de hoy.
     */
    private void generarFecha() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
        fecha = sdf.format(new Date(c.getTimeInMillis()));
    }

    /**
     * Inicia o abre el txt usado como log.
     */
    public void crearLog(String idCaso) {
         String rutaEvidenciaDispositivo="";
        String rutaEvidenciaDispositivoPDF="";
        startTime = System.currentTimeMillis();
        current_case = idCaso;
        int indiceCodigoFunc = idCaso.lastIndexOf("_");
        rutaEvidenciaDispositivo = RUTA_CARPETA_EVIDENCIA + EVIDENCE_CONTAINER + CARPETA_DISPOSITIVO;
        if (System.getProperty("pipeline") == null || System.getProperty("pipeline").equals("")
                || System.getProperty("pipeline").equalsIgnoreCase("false")) {
            rutaEvidenciaDispositivoPDF = RUTA_CARPETA_EVIDENCIA + EVIDENCE_CONTAINER + CARPETA_DISPOSITIVO;
        } else if (System.getProperty("pipeline").equalsIgnoreCase("true")) {
            rutaEvidenciaDispositivoPDF = "." + File.separator +"Evidencia_Pipeline"+ File.separator;

        }


        File dir = new File(rutaEvidenciaDispositivo);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        generarFecha();
        String nombreArchivoCompleto = rutaEvidenciaDispositivo + idCaso + "_" + fecha;
        String nombreArchivoCompletoPDF = rutaEvidenciaDispositivoPDF + idCaso + "_" + fecha;
        logTxt = new File(nombreArchivoCompleto + EXTENSION_ARCHIVO_TEXTO);
        try {
            Files.deleteIfExists(logTxt.toPath());
            if (logTxt.createNewFile()) {
                System.out.println(
                        "El log se ha creado correctamente [" + nombreArchivoCompleto + EXTENSION_ARCHIVO_TEXTO + "]");
            } else {
                System.out.println(
                        "No se pudo crear archivo log [" + nombreArchivoCompleto + EXTENSION_ARCHIVO_TEXTO + "]");
            }
            crearLogPDF(nombreArchivoCompletoPDF);
        } catch (IOException ioe) {
            System.out.println("[abrirTxt] Error en la creación de log");
            ioe.printStackTrace();
        }
        addStep("Inicio prueba " + idCaso);
    }

    /**
     * Crea el archivo físico para el log PDF.
     *
     * @param nombreArchivo
     *            nombre del archivo pdf
     */
    private void crearLogPDF(String nombreArchivo) {
        try {
            File evidence_file = new File(nombreArchivo + EXTENSION_PDF);
            Files.deleteIfExists(evidence_file.toPath());
            Document document = new Document(PageSize.A4, 20, 20, 5, 20);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(evidence_file));
            pdfCB = new PdfContentByte(writer);
            document.open();
            logPDF = document;
        } catch (Exception e) {
            addErrorStep("[crearLogPDF] Error al crear archivo PDF");
            System.out.println("[crearLogPDF] Error al crear archivo PDF");
        }
    }

    /**
     * Agrega un paso al log que solo consta de descripcion
     *
     * @param descripcion
     *            texto de lo realizado en un paso
     */
    public void addStep(String descripcion) {
        indice = indice + 1;
        String description = "Paso " + indice + " - " + descripcion;
        LogPortion lp = new LogPortion(description);
        logPortions.add(lp);
    }


    /**
     * Agrega un error al log.
     *
     * @param descripcion
     *            del error ocurrido
     */
    public void addErrorStep(String descripcion) {
        indice = indice + 1;
        String description = "Paso " + indice + " - Error: " + descripcion;
        System.out.println("Paso " + indice + " - Error: " + descripcion);
        LogPortion lp = new LogPortion(description);
        logPortions.add(lp);
    }

    /**
     * Agrega una alerta al log.
     *
     * @param descripcion
     *            texto con la advertencia
     */
    public void addWarningStep(String descripcion) {
        indice = indice + 1;
        String description = "Paso " + indice + " - Alerta: " + descripcion;
        System.out.println("Paso " + indice + " - Alerta: " + descripcion);
        LogPortion lp = new LogPortion(description);
        logPortions.add(lp);
    }

    /**
     * Agrega información al log.
     *
     * @param descripcion
     *            texto con la información adicional
     */
    public void addInfoStep(String descripcion) {
        indice = indice + 1;
        String description = "Paso " + indice + " - Info: " + descripcion;
        System.out.println("Paso " + indice + " - Info: " + descripcion);
        LogPortion lp = new LogPortion(description);
        logPortions.add(lp);
    }

    /**
     * Registra que la ejecución del caso de prueba terminó en error.
     *
     * @param e
     *            excepción ocurrida durante el fallo
     * @see Exception
     */
    public void endCaseError(Exception e) {
        String description = "Fail: Caso Finalizado";
        LogPortion lp = new LogPortion(description);
        logPortions.add(lp);

        String exception = "\n" + "Exception message:\n" + e.getMessage() + "\n" + "Line number:\n"
                + e.getStackTrace()[0];
        LogPortion lp_e = new LogPortion(exception);
        logPortions.add(lp_e);
        System.out.println(description);

        if (System.getProperty("pipeline") == null || System.getProperty("pipeline").equals("")
                || System.getProperty("pipeline").equalsIgnoreCase("false")) {
            System.out.println("SIN_PIPE");
        } else if (System.getProperty("pipeline").equalsIgnoreCase("true")) {
            this.finTestCase(current_case);
        }

    }

    /**
     * Registra el término correcto de la ejecución del caso de prueba.
     */
    public void endCaseOk() {
        indice = indice + 1;
        String mensaje = "Paso " + indice + " - Success: Caso Finalizado";
        LogPortion lp = new LogPortion(mensaje);
        logPortions.add(lp);

     // RationalTestScript.logTestResult(mensaje, true);
    }

    /**
     * Cierra archivo log txt y log PDF.
     */
    public void saveLog() {
        try {
            endTime = System.currentTimeMillis();
            createReportResume(current_case);
            pw = new PrintWriter(logTxt);
            for (LogPortion portion : logPortions) {
                // Write to txt report
                pw.println(portion.getDescription());
                // Write to pdf report
                createReportPortion(portion);
            }

            if (logTxt != null) {
                pw.close();
            }
            if (logPDF != null) {
                logPDF.close();
            }
        } catch (Exception e) {
            System.out.println("[saveLog] Error writting log \n");
            e.printStackTrace();
        }
    }

    public void createReportPortion(LogPortion portion) throws DocumentException, IOException {
        if (portion.getImage() != null) {
            Image edited = portion.getImage();
            edited.scalePercent(35);
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new int[] { 2, 2 });
            table.addCell(createTextCell(portion.getDescription()));
            table.addCell(createImageCell(edited));
            logPDF.add(table);
        } else {
            if (portion.getDescription().contains("Fail:")) {
                addFailParagraph(portion.getDescription());
            } else if (portion.getDescription().contains("Success:")) {
                addSuccessParagraph(portion.getDescription());
            } else if (portion.getDescription().contains("message:")) {
                addExceptionTable(portion.getDescription());
            } else {
                Paragraph p = new Paragraph(portion.getDescription());
                logPDF.add(new Paragraph(p));
            }
        }
    }

    public PdfPCell createImageCell(Image img) throws DocumentException, IOException {
        PdfPCell cell = new PdfPCell(img, false);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    public PdfPCell createTextCell(String text) throws DocumentException, IOException {
        PdfPCell cell = new PdfPCell();
        Paragraph p = new Paragraph(text);
        p.setAlignment(Element.ALIGN_LEFT);
        cell.addElement(p);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    public void addFailParagraph(String text) throws DocumentException {
        Font font = FontFactory.getFont(FontFactory.COURIER_BOLD, 24, BaseColor.RED);
        Paragraph p = new Paragraph(text, font);
        logPDF.add(new Paragraph(p));
        Font fontd = FontFactory.getFont(FontFactory.COURIER_OBLIQUE, 10, BaseColor.GRAY);
        Paragraph descr = new Paragraph("Log del fallo en la siguiente pagina", fontd);
        logPDF.add(new Paragraph(descr));
    }

    public void addSuccessParagraph(String text) throws DocumentException {
        BaseColor color = new BaseColor(16, 140, 37);
        Font font = FontFactory.getFont(FontFactory.COURIER_BOLD, 24, color);
        Paragraph p = new Paragraph(text, font);
        logPDF.add(new Paragraph(p));
        Font fontd = FontFactory.getFont(FontFactory.COURIER_OBLIQUE, 10, BaseColor.GRAY);
        Paragraph descr = new Paragraph("Prueba de servicio finalizada exitosamente", fontd);
        logPDF.add(new Paragraph(descr));
    }

    public void addExceptionTable(String text) throws DocumentException {
        logPDF.newPage();
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[] { 1, 1 });
        String[] content = text.split("message:");

        // Add head
        PdfPCell cellh = new PdfPCell();
        cellh.setBackgroundColor(BaseColor.ORANGE);
        Font fonth = FontFactory.getFont(FontFactory.COURIER_BOLD, 20, BaseColor.WHITE);
        Paragraph ph = new Paragraph(content[0] + " Message", fonth);
        ph.setAlignment(Element.ALIGN_LEFT);
        cellh.addElement(ph);
        cellh.setColspan(2);
        table.addCell(cellh);

        // Add content
        PdfPCell cellc = new PdfPCell();
        cellh.setBackgroundColor(BaseColor.GRAY);
        Font fontc = FontFactory.getFont(FontFactory.COURIER_BOLD, 12, BaseColor.RED);
        Paragraph pc = new Paragraph(content[1], fontc);
        pc.setAlignment(Element.ALIGN_LEFT);
        cellc.addElement(pc);
        cellc.setColspan(2);
        table.addCell(cellc);
        logPDF.add(table);
    }

    /*
     * Agrega formato a la primera página del PDF
     *
     * @param idCaso id caso de prueba
     *
     * @throws Exception en caso que falle la creacion
     */
    private void createReportResume(String idCaso) {
        try {
            BufferedImage bf_bar = ImageIO
                    .read(new File(    System.getProperty("user.dir")+File.separator+"src"+File.separator+"test"+File.separator+"java"+File.separator+"assets"+File.separator+"bar.png"));
            Image bar = Image.getInstance(pdfCB, bf_bar, 1);
            bar.setAlignment(Image.ALIGN_CENTER);
            bar.scalePercent(25);
            logPDF.add(bar);

            Font fontTitle = FontFactory.getFont(FontFactory.COURIER_BOLD, 24, BaseColor.BLACK);
            Paragraph helper_paragraph = new Paragraph("INFORME EVIDENCIA PRUEBA AUTOMATIZADA", fontTitle);
            helper_paragraph.setAlignment(Paragraph.ALIGN_CENTER);
            helper_paragraph.setSpacingAfter(100);
            logPDF.add(helper_paragraph);

            fontTitle = FontFactory.getFont(FontFactory.COURIER, 22, BaseColor.BLACK);
            logPDF.add(new Paragraph("Plataforma : " + " Itaú -" + "Services", fontTitle));
            logPDF.add(new Paragraph("Caso: " + idCaso, fontTitle));
            logPDF.add(new Paragraph("Tipo: " + "Services", fontTitle));
            logPDF.add(new Paragraph("Fecha: " + fecha, fontTitle));
            logPDF.add(new Paragraph("Host: " + Inet4Address.getLocalHost().getHostAddress(), fontTitle));

            // Add other information to the report
            Date startDate = new Date(startTime);
            Date endDate = new Date(endTime);
            DateFormat df = new SimpleDateFormat("HH:mm:ss");
            long elipseTime = endTime - startTime;
            long elipseSeconds = elipseTime / 1000;
            long min = elipseSeconds / 60;
            long sec = elipseSeconds % 60;
            logPDF.add(new Paragraph("Hora Inicio: " + df.format(startDate), fontTitle));
            logPDF.add(new Paragraph("Hora Fin: " + df.format(endDate), fontTitle));
            logPDF.add(new Paragraph("Tiempo de Ejecución: " + min + " Min " + sec + " Segundos", fontTitle));
            logPDF.add(new Paragraph("Resultado: ", fontTitle));
            addResultStamp();
            logPDF.newPage();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @throws IOException
     * @throws DocumentException
     */
    private void addResultStamp() throws IOException, DocumentException {
        try {
            boolean success = false;
            for (int i = 0; i < logPortions.size(); i++) {
                if (logPortions.get(i).getDescription().contains("Success:")) {
                    success = true;
                    break;
                }
            }

            if (success) {
                BufferedImage bfstampok = ImageIO
                        .read(new File( "."+File.separator+"src"+File.separator+"test"+File.separator+"java"+File.separator+"assets"+File.separator+"stampok.png"));
                Image stampok = Image.getInstance(pdfCB, bfstampok, 1);
                stampok.setAlignment(Image.ALIGN_CENTER);
                stampok.scalePercent(35);
                logPDF.add(stampok);
            } else {
                BufferedImage bfstampnook = ImageIO
                        .read(new File( "."+File.separator+"src"+File.separator+"test"+File.separator+"java"+File.separator+"assets"+File.separator+"stampnook.png"));
                Image stampnook = Image.getInstance(pdfCB, bfstampnook, 1);
                stampnook.setAlignment(Image.ALIGN_CENTER);
                stampnook.scalePercent(35);
                logPDF.add(stampnook);
            }

            // Create Footer of Presentation
            Font fontTitle = FontFactory.getFont(FontFactory.COURIER_OBLIQUE, 10, BaseColor.GRAY);
            Paragraph footer = new Paragraph("Generado Dinámicamente por Regresion Manager & Caex Automation Framework",
                    fontTitle);
            footer.setAlignment(Paragraph.ALIGN_CENTER);
            footer.setSpacingBefore(60);
            logPDF.add(footer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BufferedImage resize(BufferedImage img, int newW, int newH, boolean force) throws IOException {
        if (force) {
            return Thumbnails.of(img).forceSize(newW, newH).asBufferedImage();
        }
        return Thumbnails.of(img).size(newW, newH).asBufferedImage();
    }

    public void finTestCase(String testCase)  {

        String ruta =  "."+File.separator+"resultados/casosFallidos.txt";
        File file = new File(ruta);
        String sCadena;

        try {
            FileReader fr = new FileReader(file);
            BufferedReader bf = new BufferedReader(fr);
            BufferedWriter bw = new BufferedWriter(new FileWriter(ruta, true));
            //bw.newLine();
            String contenido2 =  testCase +"\n";
            // bw.append(contenido1);
            bw.append(contenido2);
            bw.close();
        }
        catch (IOException e){
        }

    }
}
