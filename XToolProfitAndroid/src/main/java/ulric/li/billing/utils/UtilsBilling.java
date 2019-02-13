package ulric.li.billing.utils;

public class UtilsBilling {
    private static String sBillingPublicKey = "CONSTRUCT_YOUR";

    public static void init(String strBillingPublicKey) {
        sBillingPublicKey = strBillingPublicKey;
    }

    public static String getBillingPbulicKey() {
        return sBillingPublicKey;
    }
}
