package ulric.li.billing.impl;

import android.app.Activity;
import android.text.TextUtils;

import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

import ulric.li.billing.intf.IBillingTool;
import ulric.li.billing.intf.IBillingToolListener;
import ulric.li.billing.core.BillingManager;
import ulric.li.xlib.impl.XObserverAutoRemove;

public class BillingTool extends XObserverAutoRemove<IBillingToolListener> implements IBillingTool {
    private boolean mInit = false;
    private BillingManager mBillingManager = null;
    private SkuDetailsResponseListener mSkuDetailsResponseListener = null;
    private BillingManager.BillingUpdatesListener mBillingUpdatesListener = null;

    public BillingTool() {
        _init();
    }

    private void _init() {
        mSkuDetailsResponseListener = new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                for (IBillingToolListener listener : getListenerList())
                    listener.onQuerySkuDetailsAsyncComplete(skuDetailsList);
            }
        };
        mBillingUpdatesListener = new BillingManager.BillingUpdatesListener() {
            @Override
            public void onBillingClientSetupFinished() {
                for (IBillingToolListener listener : getListenerList())
                    listener.onBillingToolInitAsyncComplete();
            }

            @Override
            public void onConsumeFinished(String token, int result) {
                for (IBillingToolListener listener : getListenerList())
                    listener.onConsumeAsyncComplete(token);
            }

            @Override
            public void onPurchasesUpdated(List<Purchase> purchases, int nResultCode) {
                for (IBillingToolListener listener : getListenerList())
                    listener.onQueryPurchasesAsyncComplete(purchases, nResultCode);
            }
        };
    }

    @Override
    public boolean initAsync(Activity activity) {
        if (mInit || null == activity)
            return false;

        mInit = true;
        mBillingManager = new BillingManager(activity, mBillingUpdatesListener);
        return true;
    }

    @Override
    public boolean querySkuDetailsAsync(String strBillingType, List<String> listSKUID) {
        if (TextUtils.isEmpty(strBillingType) || null == listSKUID)
            return false;

        mBillingManager.querySkuDetailsAsync(strBillingType, listSKUID, mSkuDetailsResponseListener);
        return true;
    }

    @Override
    public boolean queryPurchasesAsync() {
        mBillingManager.queryPurchases();
        return true;
    }

    @Override
    public boolean initiatePurchaseFlowAsync(String strSKUID, ArrayList<String> listOldSKUID, String strBillingType) {
        mBillingManager.initiatePurchaseFlow(strSKUID, listOldSKUID, strBillingType);
        return true;
    }

    @Override
    public boolean consumeAsyn(String strPurchaseToken) {
        if (TextUtils.isEmpty(strPurchaseToken))
            return false;

        mBillingManager.consumeAsync(strPurchaseToken);
        return true;
    }

    @Override
    public boolean isSubscriptionsSupported() {
        return mBillingManager.areSubscriptionsSupported();
    }

    @Override
    public boolean release() {
        if (!mInit)
            return false;

        mInit = false;
        mBillingManager.destroy();
        mBillingManager = null;
        return true;
    }
}
