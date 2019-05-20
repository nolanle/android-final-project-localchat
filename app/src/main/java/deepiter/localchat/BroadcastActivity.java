package deepiter.localchat;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class BroadcastActivity extends AppCompatActivity implements WifiP2pManager.ConnectionInfoListener {
    /**
     * Wifi management
     *
     * @var WifiP2pManager
     */
    private WifiP2pManager wifiManager;

    /**
     * Local Channel
     *
     * @var Channel
     */
    private Channel localChannel;

    /**
     * Broadcast Receiver
     *
     * @var BroadcastReceiver
     */
    private BroadcastReceiver bcReceiver;

    /**
     * List of clients
     *
     * @var WifiP2pDeviceList
     */
    private WifiP2pDeviceList clients;

    /**
     * List view of clients
     *
     * @var ListView
     */
    private ListView clientsList;

    /**
     * Wifi Array Adapter
     *
     * @var ArrayAdapter<String>
     */
    private ArrayAdapter<String> wifiArrayAdapter;

    /**
     * Wifi Array Adapter
     *
     * @var ArrayAdapter<String>
     */
    private WifiP2pDevice connectedPartner;
    private int PORT = 8888;
    private String TAG = "##BoadcastReceiverAct";
    IntentFilter intentFilter;

    /**
     * Client Username
     *
     * @var String
     */
    String username;

    /**
     * Client List Listener
     *
     * @var WifiP2pManager.PeerListListener
     */
    private WifiP2pManager.PeerListListener clientListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList clientList) {
            Log.d("INPeerListListener", "Works");
            // Out with the old, in with the new.
            ArrayList<WifiP2pDevice> clientsNameFixed = new ArrayList<WifiP2pDevice>();

            for (WifiP2pDevice client : clientList.getDeviceList()) {
                client.deviceName = client.deviceName.replace("[Phone]","");
            }
            clients = new WifiP2pDeviceList(clientList);

            wifiArrayAdapter.clear();
            for (WifiP2pDevice client : clientList.getDeviceList()) {

                wifiArrayAdapter.add(client.deviceName); //+ "\n" + peer.deviceAddress
                Log.d("INPeerListListenerNAME:", client.deviceName);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);

        // get name entered by user in MainActivity
        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");
        String welcome = getResources().getText(R.string.broadcast_welcome) + " " + username;

        TextView lblWelcome = findViewById(R.id.lblWelcome);
        lblWelcome.setText(welcome);

        wifiManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        localChannel =wifiManager.initialize(this, getMainLooper(), null);

        //Setting up Wifi Receiver
        bcReceiver = new WifiBroadcastReceiver(wifiManager, localChannel, this, clientListListener);

        if (wifiManager != null && localChannel != null) {
            wifiManager.requestGroupInfo(localChannel, new WifiP2pManager.GroupInfoListener() {
                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group) {
                    if (group != null && wifiManager != null && localChannel != null) {
                        wifiManager.removeGroup(localChannel, new WifiP2pManager.ActionListener() {

                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "removeGroup onSuccess -");
                            }

                            @Override
                            public void onFailure(int reason) {
                                Log.d(TAG, "removeGroup onFailure - " + reason);
                            }
                        });
                    }
                }
            });
        }

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        try {
            Method method = wifiManager.getClass().getMethod("setDeviceName", Channel.class, String.class, WifiP2pManager.ActionListener.class);

            method.invoke(wifiManager, localChannel, username, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "Thay đổi tên thành công.");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d(TAG, "Thay đổi tên thất bại: " + reason);
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "No such method");
        }

        wifiManager.discoverPeers(localChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //will not provide info about who it discovered
            }

            @Override
            public void onFailure(int reasonCode) {

            }
        });
        clientsList = findViewById(R.id.ListView);
        wifiArrayAdapter = new ArrayAdapter<String>(this, R.layout.fragment_client, R.id.lblUsername);

        clientsList.setAdapter(wifiArrayAdapter);
        clientsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Log.d(TAG, "item clicked");

                //Get string from textview
                TextView tv = ((LinearLayout) arg1).findViewById(R.id.lblUsername);
                WifiP2pDevice device = null;
                for(WifiP2pDevice wd : clients.getDeviceList()) {
                    if(wd.deviceName.equals(tv.getText()))
                        device = wd;
                }
                if(device != null) {
                    Log.d(TAG, " calling connectToPeer");
                    //Connect to selected peer
                    connectToClient(device);
                }
                else {
                    //dialog.setMessage("Failed");
                    //dialog.show();
                }
                //Log.d("############","Items " +  MoreItems[arg2] );
            }

        });
        receiveConnectRequest.execute();
    }

    /**
     * The requested connection info is available
     *
     * @param info Wi-Fi p2p connection info
     */
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) { }

    /**
     * Called when pointer capture is enabled or disabled for the current window.
     *
     * @param hasCapture True if the window has pointer capture.
     */
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) { }

    public void connectToClient(final WifiP2pDevice wifiPeer)
    {
        this.connectedPartner = wifiPeer;
        final WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = wifiPeer.deviceAddress;
        wifiManager.connect(localChannel, config, new WifiP2pManager.ActionListener()  {
            public void onSuccess() {
            }

            public void onFailure(int reason) {
                //setClientStatus("Connection to " + targetDevice.deviceName + " failed");
                //TODO: Notify the user the connection failed.
            }
        });

    }

    @SuppressLint("StaticFieldLeak")
    AsyncTask<Void, Void, Void> receiveConnectRequest = new AsyncTask<Void, Void, Void>() {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ServerSocket server = new ServerSocket();
                Socket client = server.accept();
                BufferedReader dataIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter dataOut = new PrintWriter(client.getOutputStream(), true);
                String in;
                while (true) {
                    if ((in = dataIn.readLine()) != null) {
                        String request;
                        String name;
                        try {
                            JSONObject json = new JSONObject(in);
                            request = json.getString("request");
                            name = json.getString("name");
                        } catch (JSONException e) {
                            request = "";
                            name = "";
                        }
                        if (request.equals("connection request")) {
                            //TODO: *name* wants to connect to you. (Accept/Decline)
                            //For now is accepts automatically
                            String ack = "";
                            try {
                                ack = new JSONObject().put("type", "ack").toString();
                            } catch (JSONException e) {
                                Log.d(TAG, "creating ack failed :" + e.getMessage());
                            }
                            dataOut.println(ack);
                            Intent intent = new Intent(BroadcastActivity.this, ChatActivity.class);
                            //TODO: Give necessary info to intent.
                            startActivity(intent);
                            Log.d(TAG, "Transitioning to Chat Activity");
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    };
}
