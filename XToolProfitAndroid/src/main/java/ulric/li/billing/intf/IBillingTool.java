package ulric.li.billing.intf;

import android.app.Activity;

import com.android.billingclient.api.BillingClient;

import java.util.ArrayList;
import java.util.List;

import ulric.li.xlib.intf.IXObject;
import ulric.li.xlib.intf.IXObserver;

public interface IBillingTool extends IXObject, IXObserver<IBillingToolListener> {
    boolean initAsync(Activity activity);

    boolean querySkuDetailsAsync(@BillingClient.SkuType String strBillingType, List<String> listSKUID);

    boolean queryPurchasesAsync();

    boolean initiatePurchaseFlowAsync(String strSKUID, ArrayList<String> listOldSKUID, @BillingClient.SkuType String strBillingType);

    boolean consumeAsyn(String strPurchaseToken);

    boolean isSubscriptionsSupported();

    boolean release();
}
