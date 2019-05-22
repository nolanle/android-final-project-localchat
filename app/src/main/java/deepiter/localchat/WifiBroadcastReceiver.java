package deepiter.localchat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

public class WifiBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager wifiManager;
    private Channel localChannel;
    private Activity activity;
    PeerListListener clientListListener;

    public WifiBroadcastReceiver(WifiP2pManager manager, Channel channel, Activity activity, PeerListListener PLL) {
        super();
        this.wifiManager = manager;
        this.localChannel = channel;
        this.activity = activity;
        this.clientListListener = PLL;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

            // Check to see if Wi-Fi is enabled and notify appropriate activity
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
                Log.d("WIFI_P2P_STATE_ENABLED:", "TRUE");
            } else {
                // Wi-Fi P2P is not enabled
                Log.d("WIFI_P2P_STATE_ENABLED:", "FALSE");
            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (wifiManager != null) {
                Log.d("INWIFIBRECV", "requestedpeers");
                wifiManager.requestPeers(localChannel, clientListListener);
            }
        }
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            String TAG = "##WifiBR";
            Log.d(TAG, "connection changed");
            wifiManager.requestConnectionInfo(localChannel, (WifiP2pManager.ConnectionInfoListener) activity);
            // Respond to new connection or disconnections

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }
}

