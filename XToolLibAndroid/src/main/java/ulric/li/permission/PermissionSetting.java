package ulric.li.permission;


import android.content.Context;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.SettingService;

import java.util.List;
import java.util.Locale;

import ulric.li.utils.UtilsApp;
import ulric.li.xlib.R;

public final class PermissionSetting {

    private Context context;

    public PermissionSetting(Context context) {
        this.context = context;
    }

    public void showSetting(final List<String> permissions, final PermissionHelper.PermissionGrantedCallback callback) {
        if (context == null) return;
        final SettingService settingService = AndPermission.permissionSetting(context);

        List<String> permissionNames = TransFormText.transformText(context, permissions);

        String messageTips = context.getResources().getString(R.string.permission_ask) + "\n";
        StringBuilder permissionsTips = new StringBuilder();
        for (int i = 0; i < permissionNames.size(); i++) {
            if (i != permissionNames.size() - 1) {
                permissionsTips.append(permissionNames.get(i)).append("，");
            } else {
                permissionsTips.append(permissionNames.get(i));
            }
        }
        permissionsTips.append("\n");
        String messageTips2 = String.format(Locale.ENGLISH, context.getResources().getString(R.string.permission_ask_setting2), UtilsApp.getMyAppName(context));

        String message = messageTips + permissionsTips + messageTips2;

        DialogHelper.showCustomBtn(context, context.getResources().getString(R.string.permission_request_dialog_title),
                message,
                context.getResources().getString(R.string.setting),
                context.getResources().getString(R.string.cancel),
                false,
                result -> {
                    if (result) {
                        settingService.execute(1000);
                        if (callback != null) {
                            callback.onOpenSettingPage();
                        }
                    } else {
                        settingService.cancel();
                        if (callback != null) {
                            callback.onCancelWarningDialog();
                        }
                    }
                });
    }
}
