package framework;


import com.ibm.as400.access.AS400;
import com.ibm.as400.access.* ;

import org.testng.annotations.Test;



public class DbAs400Terminal {

    public static void main (String[] args)  {

        // Crear objeto AS400
        String server = "10.181.148.10";
        String user = "CIGG981";
        String password = "ISERIES";

        String clCommand="WRKSPLF";

        AS400 system =new AS400(server,user,password);

        CommandCall command= new CommandCall(system);
        System.out.println("Comand object successfully created ");
        try {
            System.out.println("Running the comand " + clCommand);
            if (command.run(clCommand)) System.out.println(clCommand + "run successfully");
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
