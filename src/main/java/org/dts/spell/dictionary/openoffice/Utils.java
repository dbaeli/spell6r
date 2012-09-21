/*
 * Created on 27/12/2004
 * 
 */
package org.dts.spell.dictionary.openoffice;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ResourceBundle;

import com.swabunga.spell.engine.Word;

/**
 * 
 * @author DreamTangerine
 * 
 */
public final class Utils {
    private Utils() {
    }

    // /////////////////////////////////////////////////

    public static int SETSIZE = 2048;
    public static int MAXAFFIXES = 2048;
    public static int MAXWORDLEN = 100;
    public static int XPRODUCT = (1 << 0);

    public static int MAXLNLEN = 1024;

    public static boolean TestAff(String a, char b, int c) {
        for (int i = 0; i < c; ++i)
            if (a.charAt(i) == b)
                return true;

        return false;
    }

    public static String myRevStrDup(String s) {
        StringBuffer builder = new StringBuffer(s);

        return builder.reverse().toString();
    }

    public static boolean isSubset(String s1, String s2) {
        return s2.startsWith(s1);
    }

    public static void close(Reader rd) throws IOException {
        if (null != rd)
            rd.close();
    }

    public static void close(Writer wt) throws IOException {
        if (null != wt)
            wt.close();
    }

    public static void close(InputStream in) throws IOException {
        if (null != in)
            in.close();
    }

    public static Word mkInitCap(Word word) {
        StringBuffer bd = new StringBuffer(word.toString());
        bd.setCharAt(0, Character.toUpperCase(bd.charAt(0)));
        return new Word(bd.toString(), word.getCost());
    }

    public static String mkInitCap(String word) {
        StringBuffer bd = new StringBuffer(word.toString());
        bd.setCharAt(0, Character.toUpperCase(bd.charAt(0)));
        return bd.toString();
    }

    // /////////////////////////////////////////////////

    private static ResourceBundle boundle = null;

    static {
        try {
            boundle = ResourceBundle.getBundle("org.dts.spell.dictionary.openoffice.messages");
        } catch (Exception ex) {
            boundle = null;
        }
    }

    public static String getString(String str) {
        if (null != boundle)
            return boundle.getString(str);
        else
            return str;
    }
}
