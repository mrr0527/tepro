package framework.generalUtilities.config;

import locators.Locators;
import framework.DatapoolParser;
import framework.MicroServicesUtil;

import java.io.File;
import java.util.ArrayList;


public class HookServicios {



    public ArrayList<String> requests =  new ArrayList<String>();
    public MicroServicesUtil msu = null;

    public void ejecucion(String cpID,MicroServicesUtil msu) throws Exception {

        //Automation Execution
        try {
            DatapoolParser pool = new DatapoolParser(Locators.DataPool.MAIN);
            File f = new File(Locators.DataPool.MAIN);
            if(f.exists()){
                pool.filter("CP", cpID, false);
                String execution_data = pool.getValue("DATA");
                msu.setExecutionData(execution_data);
            }
            msu.runServiceTest(cpID);
            if(msu.readLog().equalsIgnoreCase("OK")){
                msu.endCaseOk();
                msu.convertirCsvXlsx();
            }else{
                System.out.println("Test fallido el servicio arrojo errores");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            msu.endCaseError(e);
            throw e;
        } finally {
            msu.saveLog();
        }
    }
}


