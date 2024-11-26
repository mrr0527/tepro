package framework.generalUtilities;

import java.util.ArrayList;
import java.util.List;

public class GenericString {

    private static List<String> path;

    public static List<String> getPath(){
        return path;
    }

    public static void setPath(String newpath){
        if (path == null){
            path = new ArrayList<>();
        }

        path.add(newpath);
    }
}
