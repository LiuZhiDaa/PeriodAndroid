package ulric.li.xui.utils;

import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class UtilsView {
    public static void setViewBlowStatusBar(View view) {
        try {
            Class<?>[] arrayclass = new Class[1];
            arrayclass[0] = Integer.TYPE;
            Method methodSetSystemUiVisibility = View.class.getMethod("setSystemUiVisibility", arrayclass);
            Field field = View.class.getField("SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN");
            Object[] arrayobject = new Object[1];
            arrayobject[0] = field.get(0);
            methodSetSystemUiVisibility.invoke(view, arrayobject);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
