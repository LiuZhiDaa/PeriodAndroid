package ulric.li.xout.core.communication;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import ulric.li.utils.UtilsLog;
import ulric.li.xout.core.XOutFactory;

/**
 * Created by WangYu on 2018/9/5.
 */
public class CommunicationImpl implements IOutComponent {

    private static IOutComponent mIOutComponent=null;

    public void setComponentInstance(IOutComponent i) {
        mIOutComponent=i;
    }


    @Override
    public String getConfigUrl() {
        if (mIOutComponent != null) {
           return mIOutComponent.getConfigUrl();
        }
        sendBroadCastReceiver();
        return "";
    }

    @Override
    public String getNativeAdKey(String strScene) {
        if (mIOutComponent != null) {
            return mIOutComponent.getNativeAdKey(strScene);
        }
        sendBroadCastReceiver();
        return "";
    }

    @Override
    public String getBannerAdKey(String strScene) {
        if (mIOutComponent != null) {
            return mIOutComponent.getBannerAdKey(strScene);
        }
        sendBroadCastReceiver();
        return "";
    }

    @Override
    public String getInterstitialAdKey(String strScene) {
        if (mIOutComponent != null) {
            return mIOutComponent.getInterstitialAdKey(strScene);
        }
        sendBroadCastReceiver();
        return "";
    }

    @Override
    public String getSceneName(String adKey) {
        if (mIOutComponent != null) {
            return mIOutComponent.getSceneName(adKey);
        }
        sendBroadCastReceiver();
        return "";
    }

    @Override
    public Class<?> getOutPageClass(String strScene) {
        if (mIOutComponent != null) {
            return mIOutComponent.getOutPageClass(strScene);
        }
        sendBroadCastReceiver();
        return null;
    }

    private void sendBroadCastReceiver() {
        try {
            UtilsLog.statisticsLog("out", "IOutComponent is null", null);
            LocalBroadcastManager manager = LocalBroadcastManager.getInstance(XOutFactory.getApplication());
            Intent intent = new Intent();
            intent.setAction(BROADCAST_ACTION);
            manager.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
