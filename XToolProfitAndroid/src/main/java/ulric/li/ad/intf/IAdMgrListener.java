package ulric.li.ad.intf;

public abstract class IAdMgrListener {
    public void onAdLoaded(IAdConfig iAdConfig) {
    }

    public void onAdFailed(IAdConfig iAdConfig, int var1) {
    }

    public void onAdOpened(IAdConfig iAdConfig) {
    }

    public void onAdClosed(IAdConfig iAdConfig) {
    }

    public void onAdClicked(IAdConfig iAdConfig) {
    }

    public void onAdImpression(IAdConfig iAdConfig) {
    }

    public void onAdLeft(IAdConfig iAdConfig) {
    }
}
