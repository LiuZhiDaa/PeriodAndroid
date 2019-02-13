package ulric.li.billing.intf;

import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;

import java.util.List;

public interface IBillingToolListener {
    void onBillingToolInitAsyncComplete();

    void onQuerySkuDetailsAsyncComplete(List<SkuDetails> listSkuDetails);

    void onQueryPurchasesAsyncComplete(List<Purchase> listPurchases, int nResultCode);

    void onConsumeAsyncComplete(String strPurchaseToken);
}
