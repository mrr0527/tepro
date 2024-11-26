package framework;


import com.ibm.as400.access.AS400;
import com.ibm.as400.access.* ;
import com.ibm.as400.data.ProgramCallDocument;
import org.testng.annotations.Test;

public class DbAs400Terminal2 {
    @Test
    public void terminal ()  {

            // Crear objeto AS400
            String server = "10.181.148.10";
            String user = "CIGG981";
            String password = "ISERIES";

            String msgId,msgText;

            String clCommand="WRKSPLF";
            String clCommand2="2";
            AS400 system =new AS400(server,user,password);

            CommandCall command= new CommandCall(system);
            System.out.println("Comand object successfully created ");
            try {
                System.out.println("Running the comand " + clCommand);
                if (command.run(clCommand)) System.out.println(clCommand + "run successfully");
                else System.out.println("Command failed");

                ProgramCallDocument pcml =new ProgramCallDocument(system, "6");
                pcml.setValue("TMP006R.PARM1","1");
                pcml.setValue("TMP006R.PARM1","");

                if (pcml.callProgram("TMP006R")!= true ) {
                    AS400Message [] mensaje=pcml.getMessageList("TMP006R");
                    for (int i = 0; i < mensaje.length; i++) {
                        msgId=mensaje[i].getID();
                        msgText=mensaje[i].getText();
                        System.out.println(msgId+" ---- text"+ msgText);
                    }
                }else{
                    System.out.println("PARAM 1 "+ pcml.getStringValue("TMP006R.PARAM1"));
                    System.out.println("PARAM 2 "+ pcml.getStringValue("TMP006R.PARAM2"));

                }





                if (command.run(clCommand2)) System.out.println(clCommand2 + "run successfully");
                else System.out.println("Command failed");

                AS400Message[] messagelist = command.getMessageList();
                if (messagelist.length > 0) {
                    System.out.println(", messages from the command");
                    System.out.println(" ");
                    for (int i = 0; i < messagelist.length; i++) {


                        System.out.println(messagelist[i].getID());
                        System.out.println(" : ");
                        System.out.println(messagelist[i].getText());
                    }


                }

            } catch (Exception e){
                System.out.println("Command "+ command.getCommand() + "issue an exception!");
                e.printStackTrace();
            }
            system.disconnectService(AS400.COMMAND);
            System.out.println("Connection is reset");
            System.exit(0);
    }
}
