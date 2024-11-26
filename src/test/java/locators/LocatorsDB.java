package locators;


import java.util.HashMap;


/**
 * Description   : Functional Test Script
 * @author CJDT1290
 */





public class LocatorsDB
{
    public static class DRIVER {
        public static final String SQL = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        public static final String AS400 = "com.ibm.as400.access.AS400JDBCDriver";
        public static final String JDTS = "net.sourceforge.jtds.jdbc.Driver";
    }

    public static class SERVERS{
        //Server Altamira
        public static final HashMap<String, String> ICALTQAT = new HashMap<String, String>(){{
            put("URL", "jdbc:as400:10.181.148.10");
            put("USER", "CIGG981");//TESTING //NBK7AQR
            put("PASSWORD", "ISERIES");

        }};

        //Server DIBSR
        public static final HashMap<String, String> ICLEGQAT = new HashMap<String, String>(){{
            put("URL", "jdbc:as400:10.181.148.11");
            put("USER", "SQCOMM"); // SQCOMM   //CARG398K
            put("PASSWORD", "AMADEUS1"); // AMADEUS1 //ISERIES
        }};


        //Server ETL
        public static final HashMap<String, String> CLSTGBLSRVDQ18 = new HashMap<String, String>(){{
            put("URL", "jdbc:sqlserver://CLSTGBLSRVDQ18.ITAUCHILE2.CL");
            put("USER", "desa");
            put("PASSWORD", "desa");
        }};

        //Server QA CLSTGSQLQ81.ITAUCHILE2.CL
        public static final HashMap<String, String> CLSTGSQLQ81 = new HashMap<String, String>(){{
            put("URL", "jdbc:sqlserver://CLSTGSQLQ81.ITAUCHILE2.CL");
            put("USER", "desa");
            put("PASSWORD", "desa");
        }};

        //Server QA CLSTGSQLQ56.ITAUCHILE2.CL
        public static final HashMap<String, String> CLSTGSQLQ56 = new HashMap<String, String>(){{
            put("URL", "jdbc:sqlserver://CLSTGSQLQ56.ITAUCHILE2.CL");
            put("USER", "desa");
            put("PASSWORD", "desa");
        }};

        //Server QA CLSTGSQLQ69.ITAUCHILE2.CL
        public static final HashMap<String, String> CLSTGSQLQ69 = new HashMap<String, String>(){{
            put("URL", "jdbc:sqlserver://CLSTGSQLQ69.ITAUCHILE2.CL");
            put("USER", "desa");
            put("PASSWORD", "desa");
        }};

        //Server Desarrollo
        public static final HashMap<String, String> CLSTGPORTD05 = new HashMap<String, String>(){{
            put("URL", "jdbc:sqlserver://CLSTGPORTD05.ITAUCHILE.CL;DatabaseName=MOTOR_SAS_V2");
            put("USER", "desa");
            put("PASSWORD", "desa");
        }};

        //Server Desarrollo CLSTGSQLQ20.ITAUCHILE2.CL
        public static final HashMap<String, String> CLSTGSQLQ20 = new HashMap<String, String>(){{
            //put("URL", "jdbc:sqlserver://10.181.167.205");
              put("URL","jdbc:sqlserver://10.181.131.126:1433");
              put("USER", "desa");
              put("PASSWORD", "desa");

        }};
        //Server Desarrollo CLSTGBLSRVQ06.ITAUCHILE2.CL
        public static final HashMap<String, String> CLSTGBLSRVQ06 = new HashMap<String, String>(){{
            put("URL", "jdbc:sqlserver://CLSTGBLSRVQ06.ITAUCHILE2.CL");
            put("USER", "desa");
            put("PASSWORD", "desa");
        }};

        //Server Corredora de Bolsa
        public static final HashMap<String, String> CLSTGCBOQ01V1 = new HashMap<String, String>(){{
            put("URL", "jdbc:sqlserver://CLSTGCBOQ01V1.ITAUCHILE2.CL");
            put("USER", "desa");
            put("PASSWORD", "desa");
        }};

        //Server Desarrollo CLSTGSQLQ65.ITAUCHILE2.CL
        public static final HashMap<String, String> CLSTGSQLQ65 = new HashMap<String, String>(){{
            put("URL", "jdbc:sqlserver://CLSTGSQLQ65.ITAUCHILE2.CL");
            put("USER", "desa");
            put("PASSWORD", "desa");
        }};

        //Server Log ETL CLSTGSQLPFQV01.itauchile2.cl
        public static final HashMap<String, String> CLSTGSQLPFQV01 = new HashMap<String, String>(){{
            put("URL", "jdbc:sqlserver://CLSTGSQLPFQV01.itauchile2.cl");
            put("USER", "desa");
            put("PASSWORD", "desa");
        }};

        //Server JOB CLSTGETLQ01.itauchile2.cl
        public static final HashMap<String, String> CLSTGETLQ01 = new HashMap<String, String>(){{
            put("URL", "jdbc:sqlserver://CLSTGETLQ01.itauchile2.cl");
            put("USER", "desa");
            put("PASSWORD", "desa");
        }};

        //Server Desarrollo
        public static final HashMap<String, String> Amazonaws = new HashMap<String, String>(){{
            put("URL", "jdbc:jtds:sqlserver://rdssql-dev-vivienda-01.cb3pwx2gwrtn.us-east-1.rds.amazonaws.com:1433/base_prueba;database=base_prueba;domain=itauchile.cl;characterEncoding=UTF-8");
            put("USER", "BKBWEBLAB");
            put("PASSWORD", "BKBENE06");
        }};

        //Server QA CLSTGSQLQ83.ITAUCHILE2.CL
        public static final HashMap<String, String> CLSTGSQLQ83 = new HashMap<String, String>(){{
            put("URL", "jdbc:sqlserver://CLSTGSQLQ83.ITAUCHILE2.CL");
            put("USER", "desa");
            put("PASSWORD", "desa");
        }};

    }

}


