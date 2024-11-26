package framework.generalUtilities.config;

public class ObtainSuperClassHook {
    private static String value;

    public static String getSuperClass() {
        return value;
    }

    public static void setSuperClass(String classname) {
        if (value == null) {
            value = new String();
        }

        value = classname;
    }

    public static String[] getRuteHook(){
        String[] nombreRuta = getSuperClass().split("\\.");
        return nombreRuta;
    }

    public static void setSuperClassClean() {
        value = null;
    }
}
