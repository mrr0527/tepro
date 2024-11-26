package framework.generalUtilities;

import java.io.File;

public class GeneralActions {

    /*
      1. Si encuentra el archivo lo retorna.
      2. Una vez recorrida la lista retornar null.
      3. Si encuentra un directorio con el nombre proporcionado realiza la llamada recursiva, y si ésta retorna distinto de null (busca el archivo dentro de la carpeta), de lo contrario vuelve a buscar en la raiz.
   */

    public static File searchFile(String archivoABuscar, File directorio) {
        File[] archivos = directorio.listFiles();
        for (File archivo : archivos) {
            if (archivo.getName().contains(archivoABuscar + ".xlsx")) {
                return archivo;
            }
            if (archivo.isDirectory()) {
                File resultadoRecursion = searchFile(archivoABuscar, archivo);
                if (resultadoRecursion != null) {
                    return resultadoRecursion;
                }
            }
        }
        return null;
    }

}
