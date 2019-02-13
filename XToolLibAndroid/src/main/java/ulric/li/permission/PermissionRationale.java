package ulric.li.permission;

import android.content.Context;

import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.util.List;

import ulric.li.xlib.R;

/**
 * Created by wanghailong on 2018/3/30.
 */

public final class PermissionRationale implements Rationale<List<String>> {

    @Override
    public void showRationale(Context context, List<String> permissions, final RequestExecutor executor) {
        List<String> permissionNames = TransFormText.transformText(context, permissions);

        String messageTips = context.getResources().getString(R.string.permission_ask) + "\n";
        StringBuilder permissionsTips = new StringBuilder();
        for (int i = 0; i < permissionNames.size(); i++) {
            if (i != permissionNames.size() - 1) {
                permissionsTips.append(permissionNames.get(i)).append(",");
            } else {
                permissionsTips.append(permissionNames.get(i));
            }
        }
        String message = messageTips + permissionsTips.toString();

        DialogHelper.showCustomBtn(context, context.getResources().getString(R.string.permission_request_dialog_title),
                message,
                context.getResources().getString(R.string.ok),
                context.getResources().getString(R.string.cancel),
                false,
                result -> {
                    if (result) {
                        executor.execute();
                    } else {
                        executor.cancel();
                    }
                });
    }

}

