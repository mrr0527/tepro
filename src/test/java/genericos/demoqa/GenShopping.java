package genericos.demoqa;

import framework.generalUtilities.HookPersonas;
import locators.cursoaut.locatorsShopping;

public class GenShopping  extends HookPersonas {

    public void PageRegistroUser() throws Exception {

        try {

            selenium.existElement(locatorsShopping.registrarShopping.TITULOREG,5);
            



        } catch (Exception e) {
            selenium.endCaseError(e);
            throw e;
        }

    }

    public void PageSpiker(){
        try {





        } catch (Exception e) {
            selenium.endCaseError(e);
            throw e;
        }
    }

    public void PageTablet(){

    }

    public void PageLAPTORS(){

    }
    public void Pagemice(){

    }

    public void OFERT(){

    }

    public void PHONES(){

    }

    public void Carritoo(){

    }
}
