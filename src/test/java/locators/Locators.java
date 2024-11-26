package locators;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Description   : Functional Test Script
 * @author CJDT1290
 */
public class Locators
{

    public static Path currentRelativePath = Paths.get("");
    public static String ruta = currentRelativePath.toAbsolutePath().toString();

    public static class Process {
        public static final String NODE = "node";
        //public static final String NODE_RUNNER = "'.//src//test//java//framework//NodeRunner'";

 //       public static final String NODE_RUNNER = "'"+System.getProperty("user.dir").replace("\\","//")+"//src//test//java//framework//NodeRunner'";
        public static  String NODE_RUNNER = "'"+ruta.replace("\\","//")+"//src//test//java//framework//NodeRunnerV2.0'";

    }
    public static class Dirs {
        public static final String PROJECT = "."+File.separator+"src"+ File.separator+"test"+ File.separator+"java"+ File.separator;
        public static final String PLATAFORM = "comunidades" + File.separator;
        public static final String LOGs = "logs"+ File.separator;
        public static final String EXECUTION_DATA = "execution_data"+ File.separator;
    }

    public static class DataPool {
        public static final String MAIN = "."+File.separator+"DataPool_Servicios.xlsx";
    }

    public static class Platforms {
        public static final String SOAP = "soap"+File.separator;
        public static  String TESOPRO = "tesoreria"+File.separator;

    }
    public static class GeneralFiles {
        public static final String LAST_REPORT_GENERATED_FILE = "last_report.txt";
    }

    public static class Enviroments {
        public static final String Login_API_Test01 = "Login_API_Test01.postman_environment.json";
        public static final String API_Rappi_Env = "Api_Rappi.postman_environment.json";
        public static final String APIRappiContrado_Env = "APIRappiContrado.postman_environment.json";
        public static final String MS_Rappi_Env = "MS_Rappi.postman_environment.json";
        public static final String TestCase_001 = "TestCase_001.postman_environment.json";
        public static  String TestCase_002 = "TestCase_002.postman_environment.json";
        public static  String TestCase_004 = "";
        public static  String TestCase_003 = "TestCase_003.postman_environment.json";
        public static  String CasoPrueba = "CasoPrueba.postman_environment.json";
        public static  String Api_Connect_Outer_BFF_QA = "Api_Connect_Outer_BFF_QA.postman_environment.json";
        public static  String Api_Connect_Outer_Access = "Outer_QA.postman_environment.json";
        public static  String Api_Connect_BFF = "BFF_QA.postman_environment.json";
        public static  String CurseWeb = "CurseWeb.postman_environment.json";
        public static  String ID_RIE_RCM_2298CUSTOMERFACILITY_003 = "ID_RIE_RCM_2298CUSTOMERFACILITY_003.postman_environment.json";
        public static  String ID_RIE_RCM_2298CUSTOMERFACILITY_004 = "ID_RIE_RCM_2298CUSTOMERFACILITY_004.postman_environment.json";
        public static  String ID_RIE_RCM_2298CUSTOMERFACILITY_005 = "ID_RIE_RCM_2298CUSTOMERFACILITY_005.postman_environment.json";
        public static  String ID_RIE_RCM_2298CUSTOMERFACILITY_006 = "ID_RIE_RCM_2298CUSTOMERFACILITY_006.postman_environment.json";
        public static  String ID_RIE_RCM_2298CUSTOMERFACILITY_007 = "ID_RIE_RCM_2298CUSTOMERFACILITY_007.postman_environment.json";
        public static  String ID_RIE_RCM_2298CUSTOMERFACILITY_008 = "ID_RIE_RCM_2298CUSTOMERFACILITY_008.postman_environment.json";
        public static  String ID_RIE_RCM_2298CUSTOMERFACILITY_009 = "ID_RIE_RCM_2298CUSTOMERFACILITY_009.postman_environment.json";
        public static  String ID_RIE_RCM_2298CUSTOMERFACILITY_010 = "ID_RIE_RCM_2298CUSTOMERFACILITY_010.postman_environment.json";
        public static  String ID_RIE_RCM_2298CUSTOMERFACILITY_011 = "ID_RIE_RCM_2298CUSTOMERFACILITY_011.postman_environment.json";
        public static  String ID_RIE_RCM_2298CUSTOMERFACILITY_012 = "ID_RIE_RCM_2298CUSTOMERFACILITY_012.postman_environment.json";

        public static  String CHANNELS_CURWEB = "CurseWeb_api_channels.postman_environment.json";
    }

    public static class Collections {
        public static final String Login_API_Test01 = "Login_API_Test01.postman_collection.json";
        public static final String API_Rappi_Coll = "Api_Rappi.postman_collection.json";
        public static final String APIRappiContrato_Coll = "APIRappiContrato.postman_collection.json";
        public static final String MS_Rappi_Coll = "MS_Rappi.postman_collection.json";
        public static final String TestCase_001 = "TestCase_001.postman_collection.json";
        public static  String TestCase_002 = "TestCase_002.postman_collection.json";
        public static  String TestCase_003 = "TestCase_003.postman_collection.json";
        public static  String CasoPrueba = "CasoPrueba.postman_collection.json";
        public static String OTP_EMAIL = "OTP_Email.postman_collection.json";
        public static String Flujo_Simulacion_QA = "Flujo_Simulacion_QA.postman_collection.json";
        public static String Inner_BCL_QA = "Api_Connect_Inner_BCL_QA.postman_collection.json";
        public static String Api_Connect_Outer_BFF_QA = "Api_Connect_Outer_BFF_QA.postman_collection.json";
        public static String Api_Connect_BFF_QA = "Api_Connect_BFF_QA.postman_collection.json";
        public static String Lambda_Pricing_QA = "Lambda_Pricing_QA.postman_collection.json";
        public static String Api_Connect_Outer_Access_QA = "Api_Connect_Outer_Access_QA.postman_collection.json";
        public static String API_INNER_ACL_CurseWeb = "API_INNER_ACL_CurseWeb_Automation.postman_collection.json";
        public static String ID_RIE_RCM_2298CUSTOMERFACILITY_003 = "ID_RIE_RCM_2298CUSTOMERFACILITY_003.postman_collection.json";
        public static String ID_RIE_RCM_2298CUSTOMERFACILITY_004 = "ID_RIE_RCM_2298CUSTOMERFACILITY_004.postman_collection.json";
        public static String ID_RIE_RCM_2298CUSTOMERFACILITY_005 = "ID_RIE_RCM_2298CUSTOMERFACILITY_005.postman_collection.json";
        public static String ID_RIE_RCM_2298CUSTOMERFACILITY_006 = "ID_RIE_RCM_2298CUSTOMERFACILITY_006.postman_collection.json";
        public static String ID_RIE_RCM_2298CUSTOMERFACILITY_007 = "ID_RIE_RCM_2298CUSTOMERFACILITY_007.postman_collection.json";
        public static String ID_RIE_RCM_2298CUSTOMERFACILITY_008 = "ID_RIE_RCM_2298CUSTOMERFACILITY_008.postman_collection.json";
        public static String ID_RIE_RCM_2298CUSTOMERFACILITY_009 = "ID_RIE_RCM_2298CUSTOMERFACILITY_009.postman_collection.json";
        public static String ID_RIE_RCM_2298CUSTOMERFACILITY_010 = "ID_RIE_RCM_2298CUSTOMERFACILITY_010.postman_collection.json";
        public static String ID_RIE_RCM_2298CUSTOMERFACILITY_011 = "ID_RIE_RCM_2298CUSTOMERFACILITY_011.postman_collection.json";
        public static String ID_RIE_RCM_2298CUSTOMERFACILITY_012 = "ID_RIE_RCM_2298CUSTOMERFACILITY_012.postman_collection.json";

        public static String CHANNELS_CURWEB = "API_CHANNELS_CURSEWEB.postman_collection.json";
        public static String Lambda_Pricing_Tenor0 = "Lambda_Pricing_Tenor_0.postman_collection.json";

        public static String ID_VIV_HAB_APROBADAAPREEVALUADOR_002 = "ID_VIV_HAB_APROBADAAAJUSTADAPREEVALUADOR_002.postman_collection.json";

    }

    public static class Logs {
        public static final String Login_API_Test01_LOG = "Login_API_Test01.postman_collection.log.json";
        public static final String API_Rappi_Logs = "Api_Rappi.postman_collection.log.json";
        public static final String ApiRappiContrato_Logs = "ApiRappiContrato.postman_collection.log.json";
        public static final String MS_Rappi_Logs = "MS_Rappi.postman_collection.log.json";
        public static final String TestCase_001_Logs = "TestCase_001.postman_collection.log.json";
        public static final String TestCase_002_Logs = "TestCase_002.postman_collection.log.json";
        public static final String TestCase_003_Logs = "TestCase_003.postman_collection.log.json";
        public static final String CasoPrueba = "CasoPrueba.postman_collection.log.json";
        public static  String OTP_Email_logs = "OTP_Email.postman_collection.log.json";
        public static  String Flujo_Simulacion_QA_logs = "Flujo_Simulacion_QA.postman_collection.log.json";
        public static String Inner_BCL_QA_logs = "Api_Connect_Inner_BCL_QA.postman_collection.log.json";
        public static String Api_Connect_Outer_BFF_QA_logs = "Api_Connect_Outer_BFF_QA.postman_collection.log.json";
        public static String Lambda_Pricing_QA_logs = "Lambda_Pricing_QA.postman_collection.log.json";
    }



}