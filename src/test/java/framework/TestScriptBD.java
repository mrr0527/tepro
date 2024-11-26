package framework;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



public class TestScriptBD {


    // Store execution log contais steps and images of the TC
    Log log = new Log();
    // Current Browser name - extracted from process
    protected String cpID;
    DBManager dbm=new DBManager();
    //Driver

    public void executeGenericSQLSP(String server, String user, String pass, String sp) {
        String classForName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String driverManager = "jdbc:sqlserver://" + server;
        boolean rs;
        try {
            Class.forName(classForName);
            Connection conn = DriverManager.getConnection(driverManager, user, pass);
            Statement statement = conn.createStatement();
            rs = statement.execute(sp);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Descripcion Este metodo marca si hay un error en la BD.
     * Esta consulta contiene registros de acceso a módulos en el portal personal
     */
    public String Get_Mark_Error_WebMonitor_BD(String coderr, String rut)
    {
        DBManager db = new DBManager();
        List<List<String>> results = null;
        ResultSet rs=null;
        String res;
        try
        {
            String query = "SELECT TOP 1 RUTO"
                    + "                , FECHA_registro "
                    + "                , FECHA"
                    + "                , NOMDES"
                    + "                , CODERR "
                    + "                , NOMBANCDES"
                    + " FROM WMBDP000.dbo.LOGWEB_hot"
                    + " WHERE RUTO = '"+rut+"'"
                    + "   AND CODERR = '"+coderr+"'"
                    + " ORDER BY FECHA_registro DESC ";
            System.out.println(query);
            db.getStatementCLSTGSQLQ69().executeQuery(query);
            String[] columns = { "RUTO", "FECHA_registro", "FECHA", "NOMDES", "CODERR", "NOMBANCDES" };
            while (rs.next()) {
                List<String> column_data = new ArrayList<>();
                for (String column : columns) {
                    column_data.add(rs.getObject(column).toString());
                }
                results.add(column_data);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        res = results.toString().replace("[[", "").replace("]]", "").trim();
        return res;
    }

    /**
     * Descripcion Método que Consulta error en la BD CLSTGSQLQ69
     * @param  RUT
     * @return Retorna query
     */
    public List<String> WebMonitorSearchError(String RUT) {
        DBManager db = new DBManager();
        log.addStep("Consultando error en la Base de Datos CLSTGSQLQ69");

        List<String> rsList = new ArrayList<>();

        try {
            ResultSet rs = dbm.getStatementCLSTGSQLQ69().executeQuery("SELECT TOP (1) RUTO,FECHA_registro, FECHA, NOMDES, CODERR, NOMBANCDES "
                    + "FROM [WMBDP000].[dbo].[LOGWEB_hot] where RUTO = '" + RUT
                    + "' order by FECHA_registro DESC;");

            while (rs.next()) {
                rsList.add(rs.getString(1));
                rsList.add(rs.getString(2));
                rsList.add(rs.getString(3));
                rsList.add(rs.getString(4));
                rsList.add(rs.getString(5));
                rsList.add(rs.getString(6));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rsList;
    }

    /**
     * Descripcion Obtener  la clave de la tarjeta de debito
     * @return Ultimos 4 digitos de la TD
     */
    public String Get_4Digit_By_TD(String rut, String numTarjeta){
        List<List<String>> results = new ArrayList<List<String>>();
        DBManager db= new DBManager();
        ResultSet rs;
        String res="";
        //Divide tarjeta en 3 partes y la busca en query segun campos.
        String tarj_p1 = numTarjeta.substring(0, 4);
        String tarj_p2 = numTarjeta.substring(4, 7);
        String tarj_p3 = numTarjeta.substring(7, 14);
        try
        {

            String query = " SELECT ATM_INDBIN, ATM_INDBANCO, ATM_NUMTARJ, ATM_VERSTARJ, ATM_DVTARJ"
                    +" FROM ALTPDBA.ATDTATM, ALTPDBA.ATDTCTA, ALTPDBA.PEDT001 C "
                    +" WHERE ATM_INDBIN = CTA_INDBIN"
                    +"   AND ATM_NUMCLITIT = C.NUMCLIEN"
                    +"	AND ATM_INDBANCO = CTA_INDBANCO"
                    +"   AND ATM_NUMTARJ = CTA_NUMTARJ"
                    +"   AND ATM_VERSTARJ = CTA_VERSTARJ"
                    +"   AND ATM_DVTARJ = CTA_DVTARJ"
                    +"	AND CTA_ESTADOCTA = '01'"
                    +"   AND ATM_CLATARJ = '-2'"
                    +"	AND ATM_ESTATARJ = 'RC'"
                    +"   AND ATM_DISCRIM = 'T'"
                    +"   AND ATM_FECALTA > '2017-05-12'"
                    +"   AND ATM_INDBIN IN ('5901','5896')"
                    +"   AND c.claident like '%"+rut+"' "
                    +" 	AND ATM_INDBIN IN ("+tarj_p1+") "
                    +" 	AND ATM_INDBANCO IN ("+tarj_p2+") "
                    +" 	AND ATM_NUMTARJ like '%"+tarj_p3+"%' "
                    +" GROUP BY ATM_INDBIN , ATM_INDBANCO, ATM_NUMTARJ, ATM_VERSTARJ, ATM_DVTARJ";

            String[] columns = { "ATM_INDBIN", "ATM_INDBANCO", "ATM_NUMTARJ", "ATM_VERSTARJ", "ATM_DVTARJ" }; //, ATM_INDBANCO, ATM_NUMTARJ, ATM_VERSTARJ, ATM_DVTARJ" };
            rs = db.getStatementICALTQAT().executeQuery(query);
            while (rs.next()) {
                List<String> column_data = new ArrayList<>();
                for (String column : columns) {
                    column_data.add(rs.getObject(column).toString());
                }
                results.add(column_data);
            }
            res = results.toString().replace("[[", "").replace(",", "").replace(" ", "").replace("]]", "");
            int largo = res.length();
            int l_1 = largo - 4;
            res = res.substring(l_1, largo);
            return res;
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
            return res;
        }
    }

    /**
     * Descripcion Obtener los 4 ultimos digitos de la tarjeta de credito
     * @return ultimos 4 digitos de la tarjeta de credito
     */
    public String Get_Four_Latest_Digit_TC(String rut, String numTar) {
        List<List<String>> results = null;
        String res = "";
        ResultSet rs;
        try
        {
            String query = " SELECT A.AOMC_NBR_CARD "
                    + " FROM SAFILES.TCM601 C, SAFILES.TCM602 A, SAFILES.TCM679 B "
                    +"  WHERE C.MCFVEN > 20170228 "
                    +"     AND  C.BSYFLG = 'A' "
                    +"     AND C.COBLAC NOT IN ('P', 'V', 'S', 'F', 'N', 'O') "
                    +"	 AND C.MCBLO2 NOT IN ('P', 'V', 'S', 'F', 'N', 'O') "
                    +" 	 AND A.MTNRCT = C.MCNRCT "
                    +"	 AND A.MTFAPE > 20000101 "
                    +"	 AND A.BSYFLG = 'A' "
                    +"	 AND A.COBLAC NOT IN ('P', 'V', 'S', 'F', 'N', 'O') "
                    +"	 AND SUBSTR (A.MTFIL3, 48, 1) = ' ' "
                    +"	 AND B.ID9LOG = A.MTLOGO "
                    +"	 AND B.ID9AFI = A.GRUAFI "
                    +"	 AND B.ID9SPR = SUBSTR (A.MTPCT ,  2, 1) "
                    +"	 AND A.AOMC_TIPO_TARJ IN ('T', 'A') "
                    +"     AND A.AOMC_EST_TARJ = 0 "
                    +"     AND A.AOMC_PINOFF_TARJ = -2 "
                    +"	 AND C.AOMC_NRUT = '"+rut.trim()+"'"
                    +"	 AND A.AOMC_NBR_CARD LIKE '%"+numTar.trim()+"%'";

            String[] columns = { "AOMC_NBR_CARD" };
            rs= dbm.getStatementICLEGQAT().executeQuery(query);
            while (rs.next()) {
                List<String> column_data = new ArrayList<>();
                for (String column : columns) {
                    column_data.add(rs.getObject(column).toString());
                }
                results.add(column_data);
            }
            res = results.toString().replace("[[", "").replace(",", "").replace(" ", "").replace("]]", "");

            int largo = res.length();
            int l_1 = largo - 4;
            res = res.substring(l_1, largo);
            return res;
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
            return res;
        }
    }

    /**
     * Descripcion Servicio que Consulta OTP en IDG
     * @return Codigo OTP
     */
    public String consultaIDG(DatapoolParser pool) throws  Exception
    {
        System.out.println(pool.getValue("RUT"));
        String idg  ="";
        URL url = new URL("http://soaqa.itauchile2.cl:10120");
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        String xmlBodyStr = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://itau.cl/xmlns/CrossChannel/PartyAuthentication/PartyAuthentication/GetOtpChallenge/1\">"
                + "\n   <soapenv:Header/>\n   <soapenv:Body>\n      <ns:GetOtpChallengeRq>\n         <MsgRqHdr>\n            <ContextRqHdr>"
                + "\n   <!--Canal:-->\n               <ChnlId>11</ChnlId>\n               <!--CodigoApp:-->\n               <AppId>10</AppId>"
                + "\n   </ContextRqHdr>\n            <SvcIdent>\n               <!--NombreApp:-->\n               <SvcName>GetOtpChallengeRq</SvcName>\n            </SvcIdent>"
                + "\n   </MsgRqHdr>\n         <PartySel>\n            <PersonIndicator>personas</PersonIndicator>"
                + "\n   <!--GrupoUsuario:-->\n            <IssuedIdentValue>"+ pool.getValue("RUT") +"</IssuedIdentValue>"
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
                    if(line.contains("<Pswd>"))
                    {
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
     * Descripcion metodo que retorna un rut con cuenta desde la BD
     * @return cta y rut
     */
    public String[] getDestBO() throws SQLException {
        String rut = "";
        String account = "";

        try {
            String query = "SELECT TRIM(L '0' FROM SAFILES.PEDT001.CLAIDE) AS RUT, SAFILES.\"SF.PERPRO\".PERNUM "
                    + "FROM SAFILES.PEDT001 "
                    + "INNER JOIN SAFILES.\"SF.PERPRO\" ON TRIM(L '0' FROM SAFILES.PEDT001.CLAIDE) = (SAFILES.\"SF.PERPRO\".PERRUT || SAFILES.\"SF.PERPRO\".PERDIG) "
                    + "WHERE SAFILES.PEDT001.SEGMEN ='301' "
                    + "AND SAFILES.PEDT001.CANORI IN ('01','02') "
                    + "AND PERTIP = 'CA' "
                    + "ORDER BY RAND() "
                    + "LIMIT 1";

            ResultSet rs = null;
            rs = dbm.getStatementICLEGQAT().executeQuery(query);
            while (rs.next()) {
                rut = rs.getString(1).trim();
                account = rs.getString(2).trim();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new String[]{rut, account};
    }

    /**
     * Descripcion Método que carga documento para funcionalidad Zona de Descarga a fecha actual de sistema menos un día
     * @param  RUT
     */
    public void updateArchivoZonaDescarga (String RUT) {
        DBManager db = new DBManager();

        Calendar calendar = Calendar.getInstance();
        Date fecha = calendar.getTime();
        calendar.setTime(fecha);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String fecha_Creacion = format.format(calendar.getTime());


        log.addStep("Cargando archivo para funcionalidad Zona de Descarga por Servidor clstgsqlq56.itauchile2.cl");

        try {
            ResultSet rs = dbm.getStatementCLSTGSQLQ56().executeQuery("UPDATE ITAWEBPOR00.dbo.tblZD_RegFiles "
                    +"SET FECHA_CREACION = '"+fecha_Creacion+"'"
                    +" WHERE NOMBRE_DOC in ("
                    +"'CartolaMM_"+ RUT +"_14092021_2027216.dat',"
                    +"'CartolaMM_"+ RUT +"_14092021_20272153.dat',"
                    +"'CartolaMM_"+ RUT +"_07092021_11142937.dat',"
                    +"'CartolaMM_"+ RUT +"_17082021_1306188.dat',"
                    +"'CartolaMM_"+ RUT +"_31072021_16264128.dat',"
                    +"'CartolaMM_"+ RUT +"_31072021_1626413.dat',"
                    +"'CartolaMM_"+ RUT +"_31072021_16264126.dat',"
                    +"'CartolaMM_"+ RUT +"_31072021_16264125.dat',"
                    +"'CartolaMM_"+ RUT +"_31072021_1626411.dat',"
                    +"'CartolaMM_"+ RUT +"_20072021_0713374.dat',"
                    +"'CartolaMM_"+ RUT +"_07072021_0648369.dat',"
                    +"'CartolaMM_"+ RUT +"_06072021_16482727.dat',"
                    +"'CartolaMM_"+ RUT +"_06072021_16482726.dat',"
                    +"'CartolaMM_"+ RUT +"_06072021_16482724.dat',"
                    +"'CartolaMM_"+ RUT +"_06072021_16482726.dat',"
                    +"'CartolaMM_"+ RUT +"_06072021_16482723.dat',"
                    +"'CartolaMM_"+ RUT +"_06072021_16482721.dat',"
                    +"'CartolaMM_"+ RUT +"_06072021_1648272.dat',"
                    +"'CartolaMM_"+ RUT +"_06072021_16482718.dat',"
                    +"'CartolaMM_"+ RUT +"_06072021_1648272.dat',"
                    +"'CartolaMM_"+ RUT +"_06072021_16482704.dat',"
                    +"'CartolaMM_"+ RUT +"_01072021_18244722.dat',"
                    +"'CartolaMM_"+ RUT +"_01072021_1824472.dat',"
                    +"'CartolaMM_"+ RUT +"_01072021_18244722.dat',"
                    +"'CartolaMM_"+ RUT +"_01072021_18244719.dat',"
                    +"'CartolaMM_"+ RUT +"_01072021_18244717.dat',"
                    +"'CartolaMM_"+ RUT +"_01072021_18244711.dat',"
                    +"'CartolaMM_"+ RUT +"_23062021_08452355.dat',"
                    +"'CartolaMM_"+ RUT +"_18062021_08185462.dat',"
                    +"'CartolaMM_"+ RUT +"_01052021_19324634.dat')");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
        /**
         * Descripcion Método que carga documento para funcionalidad Zona de Descarga a fecha un año atras
         * @param  RUT
         */
        public void updateArchivoZonaDescargaAñoAtras (String RUT) throws ParseException {
            DBManager db = new DBManager();

            Calendar calendar = Calendar.getInstance();
            Date fecha = calendar.getTime();
            calendar.setTime(fecha);
            calendar.add(Calendar.YEAR, -1);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String fecha_Creacion = format.format(calendar.getTime());


            log.addStep("Cargando archivo para funcionalidad Zona de Descarga por Servidor clstgsqlq56.itauchile2.cl");

            try {
                ResultSet rs = dbm.getStatementCLSTGSQLQ56().executeQuery("UPDATE ITAWEBPOR00.dbo.tblZD_RegFiles "
                        +"SET FECHA_CREACION = '"+fecha_Creacion+"'"
                        +" WHERE NOMBRE_DOC in ("
                        +"'CartolaMM_"+ RUT +"_14092021_2027216.dat',"
                        +"'CartolaMM_"+ RUT +"_14092021_20272153.dat',"
                        +"'CartolaMM_"+ RUT +"_07092021_11142937.dat',"
                        +"'CartolaMM_"+ RUT +"_17082021_1306188.dat',"
                        +"'CartolaMM_"+ RUT +"_31072021_16264128.dat',"
                        +"'CartolaMM_"+ RUT +"_31072021_1626413.dat',"
                        +"'CartolaMM_"+ RUT +"_31072021_16264126.dat',"
                        +"'CartolaMM_"+ RUT +"_31072021_16264125.dat',"
                        +"'CartolaMM_"+ RUT +"_31072021_1626411.dat',"
                        +"'CartolaMM_"+ RUT +"_20072021_0713374.dat',"
                        +"'CartolaMM_"+ RUT +"_07072021_0648369.dat',"
                        +"'CartolaMM_"+ RUT +"_06072021_16482727.dat',"
                        +"'CartolaMM_"+ RUT +"_06072021_16482726.dat',"
                        +"'CartolaMM_"+ RUT +"_06072021_16482724.dat',"
                        +"'CartolaMM_"+ RUT +"_06072021_16482726.dat',"
                        +"'CartolaMM_"+ RUT +"_06072021_16482723.dat',"
                        +"'CartolaMM_"+ RUT +"_06072021_16482721.dat',"
                        +"'CartolaMM_"+ RUT +"_06072021_1648272.dat',"
                        +"'CartolaMM_"+ RUT +"_06072021_16482718.dat',"
                        +"'CartolaMM_"+ RUT +"_06072021_1648272.dat',"
                        +"'CartolaMM_"+ RUT +"_06072021_16482704.dat',"
                        +"'CartolaMM_"+ RUT +"_01072021_18244722.dat',"
                        +"'CartolaMM_"+ RUT +"_01072021_1824472.dat',"
                        +"'CartolaMM_"+ RUT +"_01072021_18244722.dat',"
                        +"'CartolaMM_"+ RUT +"_01072021_18244719.dat',"
                        +"'CartolaMM_"+ RUT +"_01072021_18244717.dat',"
                        +"'CartolaMM_"+ RUT +"_01072021_18244711.dat',"
                        +"'CartolaMM_"+ RUT +"_23062021_08452355.dat',"
                        +"'CartolaMM_"+ RUT +"_18062021_08185462.dat',"
                        +"'CartolaMM_"+ RUT +"_01052021_19324634.dat')");

            } catch (SQLException e) {
                e.printStackTrace();
            }

    }

    /**
     * Descripcion Método que Consulta marca en la BD CLSTGSQLQ69
     * @param  RUT
     * @return Retorna resultado query
     */
    public List<String> WebMonitorSearchMarca(String RUT, String CODTRAN) {
        DBManager db = new DBManager();
        log.addStep("Consultando marca de registro en la Base de Datos CLSTGSQLQ69", false);

        List<String> rsList = new ArrayList<>();

        try {
            ResultSet rs = dbm.getStatementCLSTGSQLQ69().executeQuery("SELECT TOP (1) RUTO, FECHA_registro, FECHA, CODTRAN, NOMDES, CODERR, NOMBANCDES, MONTO "
                    + " FROM [WMBDP000].[dbo].[LOGWEB_hot] "
                    + " where RUTO = '" + RUT + "'"
                    + " and CODTRAN IN ('" + CODTRAN + "')"
                    + " order by FECHA_registro DESC");

            while (rs.next()) {
                rsList.add(rs.getString(1));
                rsList.add(rs.getString(2));
                rsList.add(rs.getString(3));
                rsList.add(rs.getString(4));
                rsList.add(rs.getString(5));
                rsList.add(rs.getString(6));
                rsList.add(rs.getString(7));
                rsList.add(rs.getString(8));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rsList;
    }
    //Validar marca KyC no aprobado en flujo póliza externa (se agregan dos campos necesarios)
    public List<String> WebMonitorSearchMarcaKYC(String RUT, String CODTRAN, String CTAORI, String CTADES) {
        DBManager db = new DBManager();
        log.addStep("Consultando marca de registro en la Base de Datos CLSTGSQLQ69", false);

        List<String> rsList = new ArrayList<>();

        try {
            ResultSet rs = dbm.getStatementCLSTGSQLQ69().executeQuery("SELECT TOP (1) RUTO, FECHA, CODTRAN, NOMDES, CODERR, CTAORI, CTADES "
                    + " FROM [WMBDP000].[dbo].[LOGWEB_hot] "
                    + " where RUTO = '" + RUT + "'"
                    + " and CODTRAN = '" + CODTRAN + "'"
                    + " and CTAORI = '" + CTAORI + "'"
                    + " and CTADES = '" + CTADES + "'"
                    + " order by FECHA_registro DESC;");

            while (rs.next()) {
                rsList.add(rs.getString(1));
                rsList.add(rs.getString(2));
                rsList.add(rs.getString(3));
                rsList.add(rs.getString(4));
                rsList.add(rs.getString(5));
                rsList.add(rs.getString(6));
                rsList.add(rs.getString(7));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rsList;
    }


    //Validar marca RDPA en flujo póliza externa (se agrega un campo necesario)
    public List<String> WebMonitorSearchMarcaConRDPA(String RUT, String CODTRAN, String CTAORI) {
        DBManager db = new DBManager();
        log.addStep("Consultando marca de registro en la Base de Datos CLSTGSQLQ69", false);

        List<String> rsList = new ArrayList<>();

        try {
            ResultSet rs = dbm.getStatementCLSTGSQLQ69().executeQuery("SELECT TOP (1) RUTO, FECHA, CODTRAN, NOMDES, CODERR, CTAORI, CTADES "
                    + " FROM [WMBDP000].[dbo].[LOGWEB_hot] "
                    + " where RUTO = '" + RUT + "'"
                    + " and CODTRAN = '" + CODTRAN + "'"
                    + " and CTADES = '" + CTAORI + "'"
                    + " order by FECHA_registro DESC;");

            while (rs.next()) {
                rsList.add(rs.getString(1));
                rsList.add(rs.getString(2));
                rsList.add(rs.getString(3));
                rsList.add(rs.getString(4));
                rsList.add(rs.getString(5));
                rsList.add(rs.getString(6));
                rsList.add(rs.getString(7));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rsList;
    }
    public List<String> WebMonitorSearchMarcaSinRDPA(String RUT, String CODTRAN, String CTADES) {
        DBManager db = new DBManager();
        log.addStep("Consultando marca de registro en la Base de Datos CLSTGSQLQ69", false);

        List<String> rsList = new ArrayList<>();

        try {
            ResultSet rs = dbm.getStatementCLSTGSQLQ69().executeQuery("SELECT TOP (1) RUTO, FECHA, CODTRAN, NOMDES, CODERR, CTAORI, CTADES "
                    + " FROM [WMBDP000].[dbo].[LOGWEB_hot] "
                    + " where RUTO = '" + RUT + "'"
                    + " and CODTRAN = '" + CODTRAN + "'"
                    + " and CTADES = '" + CTADES + "'"
                    + " order by FECHA_registro DESC;");

            while (rs.next()) {
                rsList.add(rs.getString(1));
                rsList.add(rs.getString(2));
                rsList.add(rs.getString(3));
                rsList.add(rs.getString(4));
                rsList.add(rs.getString(5));
                rsList.add(rs.getString(6));
                rsList.add(rs.getString(7));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rsList;
    }

    /**
     * This function return the rut with K and account number for BO
     *
     * @return
     */
    public String[] getDestBOWithK() throws SQLException
    {
        String rut = "";
        String account = "";
        DBManager dbm = new DBManager();

        try {
            String query = "SELECT TRIM(L '0' FROM SAFILES.PEDT001.CLAIDE) AS RUT, SAFILES.\"SF.PERPRO\".PERNUM "
                    + "FROM SAFILES.PEDT001 "
                    + "INNER JOIN SAFILES.\"SF.PERPRO\" ON TRIM(L '0' FROM SAFILES.PEDT001.CLAIDE) = (SAFILES.\"SF.PERPRO\".PERRUT || SAFILES.\"SF.PERPRO\".PERDIG) "
                    + "WHERE SAFILES.PEDT001.SEGMEN ='301' "
                    + "AND SAFILES.PEDT001.CANORI IN ('01','02') "
                    + "AND PERTIP = 'CA' "
                    + "AND TRIM(L '0' FROM SAFILES.PEDT001.CLAIDE) LIKE '%K'"
                    + "ORDER BY RAND() "
                    + "LIMIT 1";
            ResultSet rs = null;
            rs = dbm.getStatementICLEGQAT().executeQuery(query);
            while (rs.next())
            {
                rut = rs.getString(1).trim();
                account = rs.getString(2).trim();
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return new String[]{rut, account};
    }

    /**
     * Descripcion Obtener  la clave de la tarjeta de crédito
     * @return Ultimos 4 digitos de la TC
     */
    public String get4DigitByTC(String rut, String numTarjeta){
        String dato = "";
        DBManager db= new DBManager();
        ResultSet rs;
        String res="";
        int largo = 0;
        int l_1 = 0;
        try
        {
            largo = rut.length()-1;
            rut = rut.substring(0, largo);
            String query = " SELECT NUMTAR"
                    +" FROM SAFILES.TCM602"
                    +" WHERE AOMC_RUT_ADI like '%"+rut+"'"
                    +" AND NUMTAR like '%"+numTarjeta+"%'";

            String column = "NUMTAR";
            rs = db.getStatementICLEGQAT().executeQuery(query);
            while (rs.next()) {
                List<String> column_data = new ArrayList<>();
                dato = rs.getObject(column).toString();
            }
            res = dato.replace(",", "").replace(" ", "");
            largo = res.length();
            l_1 = largo - 4;
            res = res.substring(l_1, largo);
            return res;
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
            return res;
        }
    }

    /**
     * Descripcion se busca Obtener '0' si esta en black list por ComportamientoSbifPortabilidad
     * @return ComportamientoSbifPortabilidad = '0'
     */
    public String listaNegraComportamientoSbifPortabilidad(String rut){
        String dato = "";
        DBManager db= new DBManager();
        ResultSet rs;
        int largo = 0;
        try
        {
            largo = rut.length();
            rut = rut.substring(0, largo);
            String query = "SELECT ComportamientoSbifPortabilidad "
                    +"FROM [RIESGOCOM].[dbo].[ClienteRiesgo] "
                    +"WHERE ComportamientoSbifPortabilidad = 0 "
                    +"AND RutClienteRiesgo = '"+rut+"'";

            String column = "ComportamientoSbifPortabilidad";
            rs = db.getStatementCLSTGSQLQ83().executeQuery(query);
            while (rs.next()) {
                dato = rs.getObject(column).toString();
            }
            return dato;
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
            return dato;
        }
    }

    /**
     * Descripcion se busca Obtener '0' si esta en black list por Castigo Interno
     * @return CastigoInterno = '0'
     */
    public String listaNegraCastigoInterno(String rut){
        String dato = "";
        DBManager db= new DBManager();
        ResultSet rs;
        int largo = 0;
        try
        {
            largo = rut.length()-1;
            rut = rut.substring(0, largo);
            String query = "SELECT CastigoInterno "
                    +"FROM [RIESGOCOM].[dbo].[ClienteRiesgo] "
                    +"WHERE CastigoInterno = 0 "
                    +"AND RutClienteRiesgo = "+rut+"";

            String column = "CastigoInterno";
            rs = db.getStatementCLSTGSQLQ83().executeQuery(query);
            while (rs.next()) {
                dato = rs.getObject(column).toString();
            }
            return dato;
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
            return dato;
        }
    }

    /**
     * Descripcion se busca Obtener 'VH' si esta en black list por Segmento Riesgo Portabilidad
     * @return SegmentoRiesgoPortabilidad = 'VH'
     */
    public String listaNegraSegmentoRiesgoPortabilidad(String rut){
        String dato = "";
        DBManager db= new DBManager();
        ResultSet rs;
        int largo = 0;
        try
        {
            largo = rut.length()-1;
            rut = rut.substring(0, largo);
            String query = "SELECT SegmentoRiesgoPortabilidad "
                    +"FROM [RIESGOCOM].[dbo].[ClienteRiesgo] "
                    +"WHERE SegmentoRiesgoPortabilidad = 'VH' "
                    +"AND RutClienteRiesgo = "+rut+"";

            String column = "SegmentoRiesgoPortabilidad";
            rs = db.getStatementCLSTGSQLQ83().executeQuery(query);
            while (rs.next()) {
                dato = rs.getObject(column).toString();
            }
            return dato;
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
            return dato;
        }
    }

    /**
     * Descripcion se busca Obtener '0' si esta en black list por Bancarización
     * @return Bancarizacion = '0'
     */
    public String listaNegraBancarización(String rut){
        String dato = "";
        DBManager db= new DBManager();
        ResultSet rs;
        int largo = 0;
        try
        {
            largo = rut.length()-1;
            rut = rut.substring(0, largo);
            String query = "SELECT Bancarizacion "
                    +"FROM [RIESGOCOM].[dbo].[ClienteRiesgo] "
                    +"WHERE Bancarizacion = 0 "
                    +"AND RutClienteRiesgo = "+rut+"";

            String column = "Bancarizacion";
            rs = db.getStatementCLSTGSQLQ83().executeQuery(query);
            while (rs.next()) {
                dato = rs.getObject(column).toString();
            }
            return dato;
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
            return dato;
        }
    }

    /**
     * Descripcion se busca Obtener un rut que no este en black list
     * @return debe devolver Vacio si no esta en lista negra
     */
    public String noEstaEnlistaNegra(String rut){
        List<List<String>> results = new ArrayList<List<String>>();
        DBManager db= new DBManager();
        ResultSet rs;
        String res="";
        int largo = 0;
        try
        {
            largo = rut.length()-1;
            rut = rut.substring(0, largo);
            String query = "SELECT RutClienteRiesgo "
                    +",ComportamientoSbifPortabilidad "
                    +",ComportamientoSbifDigitalTc "
                    +",ComportamientoSbifDigital "
                    +",CastigoInterno "
                    +",MarcaFraude "
                    +",SegmentoRiesgoPortabilidad "
                    +",SegmentoRiesgoDigital "
                    +",Bancarizacion "
                    +",Fallecidos "
                    +",BoletinConsursal "
                    +"FROM [RIESGOCOM].[dbo].[ClienteRiesgo] "
                    +"WHERE RutClienteRiesgo = "+rut+"";

            String[] columns = { "RutClienteRiesgo", "ComportamientoSbifPortabilidad", "ComportamientoSbifDigitalTc", "ComportamientoSbifDigital", "CastigoInterno","MarcaFraude","SegmentoRiesgoPortabilidad","SegmentoRiesgoDigital","Bancarizacion","Fallecidos","BoletinConsursal" };
            rs = db.getStatementICALTQAT().executeQuery(query);
            while (rs.next()) {
                List<String> column_data = new ArrayList<>();
                for (String column : columns) {
                    column_data.add(rs.getObject(column).toString());
                }
                results.add(column_data);
            }
            res = results.toString().replace("[[", "").replace(",", "").replace(" ", "").replace("]]", "");
            return res.trim();
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
            return res;
        }
    }
}