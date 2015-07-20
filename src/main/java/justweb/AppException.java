package justweb;

import java.lang.Object;import java.lang.RuntimeException;import java.lang.String;import java.lang.Throwable;public class AppException extends RuntimeException {

    public AppException(String message) {
        super(message);
    }

    public AppException(Throwable cause) {
        super(cause);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void ifNull(Object object, String name, String... comments) {
        if (object == null)
            throw new AppException("Variable '"+ name +"' is null." + addComments(comments));
    }

    public static void ifNotNull(Object object, String name, String... comments) {
        if (object != null)
            throw new AppException("Variable '"+ name +"' is not null." + addComments(comments));
    }

    public static void ifNullOrEmpty(String object, String name) {
        if (object == null)
            throw new AppException("String '"+ name +"' (String) is null or empty.");
    }

    public static <T> void ifNullOrEmpty(T[] array, String name) {
        AppException.ifNull(array, name);
        if (array.length == 0)
            throw new AppException("Array '"+ name +"' empty.");
    }

    public static void ifNullOrEmpty(byte[] array, String name) {
        AppException.ifNull(array, name);
        if (array.length == 0)
            throw new AppException("Array '"+ name +"' empty.");
    }

    public static void ifEqual(Object object1, String name1, Object object2, String name2) {
        if (object2.equals(object1))
            throw new AppException(name1.substring(0, 1).toUpperCase() + name1.substring(1) + " does equal " + name2);
    }

    public static void ifNotEqual(Object object1, String name1, Object object2, String name2) {
        ifNull(object1, name1);
        ifNull(object2, name2);
        if (! object1.equals(object2))
            throw new AppException(name1.substring(0, 1).toUpperCase() + name1.substring(1) + " does not equal " + name2);
    }

    public static void ifContained(boolean contained, String containedName) {
        if (contained)
            throw new AppException("Given " + containedName + " already contained.");
    }

    public static void ifNotContained(boolean contained, String containedName) {
        if (! contained)
            throw new AppException("Given " + containedName + " not contained.");
    }

    private static String addComments(String... comments) {
        String commentString = "";
        if (comments != null)
            for (int i = 0; i < comments.length; i++)
                commentString += " " + comments[i] + ".";
        return commentString;
    }

}
