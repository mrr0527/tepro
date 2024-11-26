package framework;

import locators.LocatorsDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBManager{

    private Connection connectionICALTQAT = null;
    private Statement statementICALTQAT = null;

    private Connection connectionICLEGQAT = null;
    private Statement statementICLEGQAT = null;

    private Connection connectionCLSTGPORTD05 = null;
    private Statement statementCLSTGPORTD05 = null;

    private Connection connectionCLSTGSQLQ81 = null;
    private Statement statementCLSTGSQLQ81 = null;

    private Connection connectionCLSTGBLSRVDQ18 = null;
    private Statement statementCLSTGBLSRVDQ18 = null;

    private Connection connectionCLSTGSQLQ20 = null;
    private Statement statementCLSTGSQLQ20 = null;

    private Connection connectionCLSTGBLSRVQ06 = null;
    private Statement statementCLSTGBLSRVQ06 = null;

    private Connection connectionCLSTGCBOQ01V1 = null;
    private Statement statementCLSTGCBOQ01V1 = null;

    private Connection connectionCLSTGSQLQ65 = null;
    private Statement statementCLSTGSQLQ65 = null;

    private Connection connectionCLSTGSQLQ69 = null;
    private Statement statementCLSTGSQLQ69 = null;

    private Connection connectionCLSTGSQLQ56 = null;
    private Statement statementCLSTGSQLQ56 = null;

    private Connection connectionCLSTGSQLPFQV01 = null;
    private Statement statementCLSTGSQLPFQV01 = null;

    private Connection connectionCLSTGETLQ01 = null;
    private Statement statementCLSTGETLQ01 = null;

    private Connection connectionAmazonaws = null;
    private Statement statementAmazonaws = null;
    private Connection connectionCLSTGSQLQ83 = null;
    private Statement statementCLSTGSQLQ83 = null;

    public DBManager() {
        //connectToAmazonaws();
        connectToICALTQAT();
        connectToICLEGQAT();
        connectToCLSTGPORTD05();
        connectToCLSTGSQLQ81();
        connectToCLSTGBLSRVDQ18();
        connectToCLSTGSQLQ20();
        connectToCLSTGSQLQ06();
        connectToCLSTGCBOQ01V1();
        connectToCLSTGSQLQ65();
        connectToCLSTGSQLQ69();
        connectToCLSTGSQLQ56();
        connectToCLSTGSQLPFQV01();
        connectToCLSTGSQLQ83();
        //connectToCLSTGETLQ01();
    }


    /**
     * Init connection to SQL Amazonaws server
     */
    public void connectToAmazonaws() {
        // Load class into memory
        try {
            Class.forName(LocatorsDB.DRIVER.JDTS);
            // Establish connection
            connectionAmazonaws = (Connection) DriverManager.getConnection(
                    LocatorsDB.SERVERS.Amazonaws.get("URL"), LocatorsDB.SERVERS.Amazonaws.get("USER"),
                    LocatorsDB.SERVERS.Amazonaws.get("PASSWORD"));
            setStatementAmazonaws(connectionAmazonaws.createStatement());
            getStatementAmazonaws().setQueryTimeout(60);
        } catch (ClassNotFoundException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        }
    }

    /**
     * Init connection to AS400 ICALTQAT server
     */
    private void connectToICALTQAT() {
        // Load class into memory
        try {
            Class.forName(LocatorsDB.DRIVER.AS400);
            // Establish connection
            connectionICALTQAT = (Connection) DriverManager.getConnection(LocatorsDB.SERVERS.ICALTQAT.get("URL"),
                    LocatorsDB.SERVERS.ICALTQAT.get("USER"), LocatorsDB.SERVERS.ICALTQAT.get("PASSWORD"));
            setStatementICALTQAT(connectionICALTQAT.createStatement());
            getStatementICALTQAT().setQueryTimeout(220);
        } catch (ClassNotFoundException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        }
    }

    /**
     * Init connection to AS400 DIBS server
     */
    private void connectToICLEGQAT() {
        // Load class into memory
        try {
            Class.forName(LocatorsDB.DRIVER.AS400);
            // Establish connection
            connectionICLEGQAT = (Connection) DriverManager.getConnection(LocatorsDB.SERVERS.ICLEGQAT.get("URL"),
                    LocatorsDB.SERVERS.ICLEGQAT.get("USER"), LocatorsDB.SERVERS.ICLEGQAT.get("PASSWORD"));
            setStatementICLEGQAT(connectionICLEGQAT.createStatement());
            getStatementICLEGQAT().setQueryTimeout(60);
        } catch (ClassNotFoundException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        }
    }

    /**
     * Init connection to SQL CLSTGPORTD05 server
     */
    private void connectToCLSTGPORTD05() {
        // Load class into memory
        try {
            Class.forName(LocatorsDB.DRIVER.SQL);
            // Establish connection
            connectionCLSTGPORTD05 = (Connection) DriverManager.getConnection(
                    LocatorsDB.SERVERS.CLSTGPORTD05.get("URL"), LocatorsDB.SERVERS.CLSTGPORTD05.get("USER"),
                    LocatorsDB.SERVERS.CLSTGPORTD05.get("PASSWORD"));
            setStatementCLSTGPORTD05(connectionCLSTGPORTD05.createStatement());
            getStatementCLSTGPORTD05().setQueryTimeout(60);
        } catch (ClassNotFoundException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        }
    }

    /**
     * Init connection to SQL CLSTGSQLQ81 server
     */
    private void connectToCLSTGSQLQ81() {
        // Load class into memory
        try {
            Class.forName(LocatorsDB.DRIVER.SQL);
            // Establish connection
            connectionCLSTGSQLQ81 = (Connection) DriverManager.getConnection(LocatorsDB.SERVERS.CLSTGSQLQ81.get("URL"),
                    LocatorsDB.SERVERS.CLSTGSQLQ81.get("USER"), LocatorsDB.SERVERS.CLSTGSQLQ81.get("PASSWORD"));
            setStatementCLSTGSQLQ81(connectionCLSTGSQLQ81.createStatement());
            getStatementCLSTGSQLQ81().setQueryTimeout(60);
        } catch (ClassNotFoundException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        }
    }


    /**
     * Init connection to SQL CLSTGSQLQ56 server
     */
    private void connectToCLSTGSQLQ56() {
        // Load class into memory
        try {
            Class.forName(LocatorsDB.DRIVER.SQL);
            // Establish connection
            connectionCLSTGSQLQ56 = (Connection) DriverManager.getConnection(LocatorsDB.SERVERS.CLSTGSQLQ56.get("URL"),
                    LocatorsDB.SERVERS.CLSTGSQLQ56.get("USER"), LocatorsDB.SERVERS.CLSTGSQLQ56.get("PASSWORD"));
            setStatementCLSTGSQLQ56(connectionCLSTGSQLQ56.createStatement());
            getStatementCLSTGSQLQ56().setQueryTimeout(60);
        } catch (ClassNotFoundException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        }
    }



    /**
     * Init connection to SQL CLSTGBLSRVDQ18 server
     */
    private void connectToCLSTGBLSRVDQ18() {
        // Load class into memory
        try {
            Class.forName(LocatorsDB.DRIVER.SQL);
            // Establish connection
            connectionCLSTGBLSRVDQ18 = (Connection) DriverManager.getConnection(
                    LocatorsDB.SERVERS.CLSTGBLSRVDQ18.get("URL"), LocatorsDB.SERVERS.CLSTGBLSRVDQ18.get("USER"),
                    LocatorsDB.SERVERS.CLSTGBLSRVDQ18.get("PASSWORD"));
            setStatementCLSTGBLSRVDQ18(connectionCLSTGBLSRVDQ18.createStatement());
            getStatementCLSTGBLSRVDQ18().setQueryTimeout(60);
        } catch (ClassNotFoundException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        }
    }

    /**
     * Init connection to SQL CLSTGBLSRVDQ20 server
     */
    private void connectToCLSTGSQLQ20() {
        // Load class into memory
        try {
            Class.forName(LocatorsDB.DRIVER.SQL);
            // Establish connection
            connectionCLSTGSQLQ20 = (Connection) DriverManager.getConnection(LocatorsDB.SERVERS.CLSTGSQLQ20.get("URL"),
              LocatorsDB.SERVERS.CLSTGSQLQ20.get("USER"), LocatorsDB.SERVERS.CLSTGSQLQ20.get("PASSWORD"));
            // give time for establish connection
            setStatementCLSTGSQLQ20(connectionCLSTGSQLQ20.createStatement());
            getStatementCLSTGSQLQ20().setQueryTimeout(60);

            /*
            if (connectionCLSTGSQLQ20.isValid(5)) {
                System.out.println("OK_CON");
            }
            else {
                System.out.println("NOK");
            }
             */

        } catch (ClassNotFoundException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        }
    }


    /**
     * Init connection to SQL CLSTGBLSRVQ06 server
     */
    private void connectToCLSTGSQLQ06() {
        // Load class into memory
        try {
            Class.forName(LocatorsDB.DRIVER.SQL);
            // Establish connection
            connectionCLSTGBLSRVQ06 = (Connection) DriverManager.getConnection(LocatorsDB.SERVERS.CLSTGBLSRVQ06.get("URL"),
                    LocatorsDB.SERVERS.CLSTGBLSRVQ06.get("USER"), LocatorsDB.SERVERS.CLSTGBLSRVQ06.get("PASSWORD"));
            // give time for establish connection
            setStatementCLSTGSQLQ06(connectionCLSTGBLSRVQ06.createStatement());
            getStatementCLSTGSQLQ06().setQueryTimeout(60);
        } catch (ClassNotFoundException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        }
    }


    /**
     * Init connection to SQL CLSTGCBOQ01V1 server
     */
    private void connectToCLSTGCBOQ01V1() {
        // Load class into memory
        try {
            Class.forName(LocatorsDB.DRIVER.SQL);
            // Establish connection
            connectionCLSTGCBOQ01V1 = (Connection) DriverManager.getConnection(LocatorsDB.SERVERS.CLSTGCBOQ01V1.get("URL"),
                    LocatorsDB.SERVERS.CLSTGCBOQ01V1.get("USER"), LocatorsDB.SERVERS.CLSTGCBOQ01V1.get("PASSWORD"));
            // give time for establish connection
            setStatementCLSTGCBOQ01V1(connectionCLSTGCBOQ01V1.createStatement());
            getStatementCLSTGCBOQ01V1().setQueryTimeout(60);
        } catch (ClassNotFoundException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        }
    }


    /**
     * Init connection to SQL CLSTGSQLQ65 server
     */
    private void connectToCLSTGSQLQ65() {
        // Load class into memory
        try {
            Class.forName(LocatorsDB.DRIVER.SQL);
            // Establish connection
            connectionCLSTGSQLQ65 = (Connection) DriverManager.getConnection(LocatorsDB.SERVERS.CLSTGSQLQ65.get("URL"),
                    LocatorsDB.SERVERS.CLSTGSQLQ65.get("USER"), LocatorsDB.SERVERS.CLSTGSQLQ65.get("PASSWORD"));
            // give time for establish connection
            setStatementCLSTGSQLQ65(connectionCLSTGSQLQ65.createStatement());
            getStatementCLSTGSQLQ65().setQueryTimeout(60);
        } catch (ClassNotFoundException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        }
    }

    /**
     * Init connection to SQL CLSTGSQLQ69 server
     */
    private void connectToCLSTGSQLQ69() {
        // Load class into memory
        try {
            Class.forName(LocatorsDB.DRIVER.SQL);
            // Establish connection
            connectionCLSTGSQLQ69 = (Connection) DriverManager.getConnection(LocatorsDB.SERVERS.CLSTGSQLQ69.get("URL"),
                    LocatorsDB.SERVERS.CLSTGSQLQ69.get("USER"), LocatorsDB.SERVERS.CLSTGSQLQ69.get("PASSWORD"));
            // give time for establish connection
            setStatementCLSTGSQLQ69(connectionCLSTGSQLQ69.createStatement());
            getStatementCLSTGSQLQ69().setQueryTimeout(60);
        } catch (ClassNotFoundException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        }
    }

    /**
     * Init connection to SQL CLSTGSQLPFQV01 server
     */
    private void connectToCLSTGSQLPFQV01() {
        // Load class into memory
        try {
            Class.forName(LocatorsDB.DRIVER.SQL);
            // Establish connection
            connectionCLSTGSQLPFQV01 = (Connection) DriverManager.getConnection(
                    LocatorsDB.SERVERS.CLSTGSQLPFQV01.get("URL"), LocatorsDB.SERVERS.CLSTGSQLPFQV01.get("USER"),
                    LocatorsDB.SERVERS.CLSTGSQLPFQV01.get("PASSWORD"));
            setStatementCLSTGSQLPFQV01(connectionCLSTGSQLPFQV01.createStatement());
            getStatementCLSTGSQLPFQV01().setQueryTimeout(60);
        } catch (ClassNotFoundException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        }
    }

    /**
     * Init connection to SQL CLSTGETLQ01 server
     */
    private void connectToCLSTGETLQ01() {
        // Load class into memory
        try {
            Class.forName(LocatorsDB.DRIVER.SQL);
            // Establish connection
            connectionCLSTGETLQ01 = (Connection) DriverManager.getConnection(
                    LocatorsDB.SERVERS.CLSTGETLQ01.get("URL"), LocatorsDB.SERVERS.CLSTGETLQ01.get("USER"),
                    LocatorsDB.SERVERS.CLSTGETLQ01.get("PASSWORD"));
            setStatementCLSTGETLQ01(connectionCLSTGETLQ01.createStatement());
            getStatementCLSTGETLQ01().setQueryTimeout(60);
        } catch (ClassNotFoundException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        }
    }

    /**
     * Init connection to SQL CLSTGSQLQ83 server
     */
    private void connectToCLSTGSQLQ83() {
        // Load class into memory
        try {
            Class.forName(LocatorsDB.DRIVER.SQL);
            // Establish connection
            connectionCLSTGSQLQ83 = (Connection) DriverManager.getConnection(LocatorsDB.SERVERS.CLSTGSQLQ83.get("URL"),
                    LocatorsDB.SERVERS.CLSTGSQLQ83.get("USER"), LocatorsDB.SERVERS.CLSTGSQLQ83.get("PASSWORD"));
            setStatementCLSTGSQLQ83(connectionCLSTGSQLQ83.createStatement());
            getStatementCLSTGSQLQ83().setQueryTimeout(60);
        } catch (ClassNotFoundException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Bloque catch generado automáticamente
            e.printStackTrace();
        }
    }

    /**
     * @return el statementICALTQAT
     */
    public Statement getStatementICALTQAT() {
        return statementICALTQAT;
    }

    /**
     * @param statementICALTQAT el statementICALTQAT a establecer
     */
    private void setStatementICALTQAT(Statement statementICALTQAT) {
        this.statementICALTQAT = statementICALTQAT;
    }

    /**
     * @return el statementICLEGQAT
     */
    public Statement getStatementICLEGQAT() {
        return statementICLEGQAT;
    }

    /**
     * @param statementICLEGQAT el statementICLEGQAT a establecer
     */
    private void setStatementICLEGQAT(Statement statementICLEGQAT) {
        this.statementICLEGQAT = statementICLEGQAT;
    }

    /**
     * @return el statementCLSTGPORTD05
     */
    public Statement getStatementCLSTGPORTD05() {
        return statementCLSTGPORTD05;
    }

    /**
     * @param statementCLSTGPORTD05 el statementCLSTGPORTD05 a establecer
     */
    private void setStatementCLSTGPORTD05(Statement statementCLSTGPORTD05) {
        this.statementCLSTGPORTD05 = statementCLSTGPORTD05;
    }

    /**
     * @return el statementCLSTGBLSRVDQ18
     */
    public Statement getStatementCLSTGBLSRVDQ18() {
        return statementCLSTGBLSRVDQ18;
    }

    /**
     * @param statementCLSTGBLSRVDQ18 el statementCLSTGBLSRVDQ18 a establecer
     */
    private void setStatementCLSTGBLSRVDQ18(Statement statementCLSTGBLSRVDQ18) {
        this.statementCLSTGBLSRVDQ18 = statementCLSTGBLSRVDQ18;
    }

    /**
     * @return el statementCLSTGSQLQ20
     */
    public Statement getStatementCLSTGSQLQ20() {
        return statementCLSTGSQLQ20;
    }

    /**
     * @param statementCLSTGSQLQ20 el statementCLSTGSQLQ20 a establecer
     */
    private void setStatementCLSTGSQLQ20(Statement statementCLSTGSQLQ20) {
        this.statementCLSTGSQLQ20 = statementCLSTGSQLQ20;
    }

    /**
     * @return el statementCLSTGSQLQ81
     */
    public Statement getStatementCLSTGSQLQ81() {
        return statementCLSTGSQLQ81;
    }

    /**
     * @param statementCLSTGSQLQ81 el statementCLSTGSQLQ81 a establecer
     */
    private void setStatementCLSTGSQLQ81(Statement statementCLSTGSQLQ81) {
        this.statementCLSTGSQLQ81 = statementCLSTGSQLQ81;
    }


    //----
    /**
     * @return el statementCLSTGSQLQ56
     */
    public Statement getStatementCLSTGSQLQ56() {
        return statementCLSTGSQLQ56;
    }

    /**
     * @param statementCLSTGSQLQ56 el statementCLSTGSQL56 a establecer
     */
    private void setStatementCLSTGSQLQ56(Statement statementCLSTGSQLQ56) {
        this.statementCLSTGSQLQ56 = statementCLSTGSQLQ56;
    }


    /**
     * @return el statementCLSTGBLSRVQ06
     */
    public Statement getStatementCLSTGSQLQ06() {
        return statementCLSTGBLSRVQ06;
    }

    /**
     * @param statementCLSTGBLSRVQ06 el statementCLSTGBLSRVQ06 a establecer
     */
    private void setStatementCLSTGSQLQ06(Statement statementCLSTGBLSRVQ06) {
        this.statementCLSTGBLSRVQ06 = statementCLSTGBLSRVQ06;
    }

    /**
     * @return el statementCLSTGCBOQ01V1
     */
    public Statement getStatementCLSTGCBOQ01V1() {
        return statementCLSTGCBOQ01V1;
    }

    /**
     * @param statementCLSTGCBOQ01V1 el statementCLSTGCBOQ01V1 a establecer
     */
    private void setStatementCLSTGCBOQ01V1(Statement statementCLSTGCBOQ01V1) {
        this.statementCLSTGCBOQ01V1 = statementCLSTGCBOQ01V1;
    }

    /**
     * @return el statementCLSTGSQLQ65
     */
    public Statement getStatementCLSTGSQLQ65() {
        return statementCLSTGSQLQ65;
    }

    /**
     * @param statementCLSTGSQLQ65 el statementCLSTGSQLQ65 a establecer
     */
    private void setStatementCLSTGSQLQ65(Statement statementCLSTGSQLQ65) {
        this.statementCLSTGSQLQ65 = statementCLSTGSQLQ65;
    }

    /**
     * @return el statementCLSTGSQLQ69
     */
    public Statement getStatementCLSTGSQLQ69() {
        return statementCLSTGSQLQ69;
    }

    /**
     * @param statementCLSTGSQLQ69 el statementCLSTGSQLQ69 a establecer
     */
    private void setStatementCLSTGSQLQ69(Statement statementCLSTGSQLQ69) {
        this.statementCLSTGSQLQ69 = statementCLSTGSQLQ69;
    }

    /**
     * @param statementCLSTGSQLPFQV01 el statementCLSTGSQLPFQV01 a establecer
     */
    private void setStatementCLSTGSQLPFQV01(Statement statementCLSTGSQLPFQV01) {
        this.statementCLSTGSQLPFQV01 = statementCLSTGSQLPFQV01;
    }

    /**
     * @return el statementCLSTGSQLPFQV01
     */
    public Statement getStatementCLSTGSQLPFQV01() {
        return statementCLSTGSQLPFQV01;
    }


    /**
     * @param statementCLSTGETLQ01 el statementCLSTGETLQ01 a establecer
     */
    private void setStatementCLSTGETLQ01(Statement statementCLSTGETLQ01) {
        this.statementCLSTGETLQ01 = statementCLSTGETLQ01;
    }

    /**
     * @return el statementCLSTGETLQ01
     */
    public Statement getStatementCLSTGETLQ01() {
        return statementCLSTGETLQ01;
    }



    /**
     * @return el statementAmazonaws
     */
    public Statement getStatementAmazonaws() {
        return statementAmazonaws;
    }

    /**
     * @param statementAmazonaws el statementAmazonaws a establecer
     */
    private void setStatementAmazonaws(Statement statementAmazonaws) {
        this.statementAmazonaws = statementAmazonaws;
    }

    /**
     * @return el statementCLSTGSQLQ83
     */
    public Statement getStatementCLSTGSQLQ83() {
        return statementCLSTGSQLQ83;
    }

    /**
     * @param statementCLSTGSQLQ83 el statementCLSTGSQLQ83 a establecer
     */
    private void setStatementCLSTGSQLQ83(Statement statementCLSTGSQLQ83) {
        this.statementCLSTGSQLQ83 = statementCLSTGSQLQ83;
    }

}
