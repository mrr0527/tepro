package framework.generalUtilities;

import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class GenericListWebElement {
    private static List<WebElement> path;

    public static List<WebElement> getPath(){
        return path;
    }

    public static void setPath(WebElement newpath){
        if (path == null){
            path = new ArrayList<>();
        }

        path.add(newpath);
    }
}
