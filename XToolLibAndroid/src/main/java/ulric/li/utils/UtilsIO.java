package ulric.li.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import android.text.TextUtils;

public class UtilsIO {
    public static String readFileFirstLine(String strFilePath) {
        if (TextUtils.isEmpty(strFilePath))
            return null;

        File file = new File(strFilePath);
        if (!file.exists())
            return null;

        try {
            String str = null;
            BufferedReader br = new BufferedReader(new FileReader(file));
            str = br.readLine();
            br.close();
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String readFileStartWithLine(String strFilePath, String strStartWith) {
        if (TextUtils.isEmpty(strFilePath))
            return null;

        File file = new File(strFilePath);
        if (!file.exists())
            return null;

        try {
            String str = null;
            BufferedReader br = new BufferedReader(new FileReader(file));
            while (!TextUtils.isEmpty(str = br.readLine())) {
                if (str.startsWith(strStartWith))
                    break;
            }

            br.close();
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
