package deepiter.localchat;

import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    static private List<ChatMessage> Messages;
    static private ArrayAdapter<ChatMessage> adapter;
    static private WifiP2pInfo info;
    private WifiP2pManager.Channel channel;
    private WifiP2pManager manager;
    static private List<ChatClient> clients;
    private ServerSocket serverSocket;
    private Socket socket;
    private String name;
    InetAddress groupOwner = null;
    private int PORT = 8000;
    BufferedReader fromGroupOwner;
    PrintWriter toGroupOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    }

    public static void receive(String data){
        ChatMessage chatMessage = new ChatMessage(data);
        Log.d("Chat","received something");
        Log.d("Chat",chatMessage.getText());
        Messages.add(chatMessage);
        updateOnMain();
    }

    public static Handler UIHandler = new Handler(Looper.getMainLooper());
    public static void updateOnMain() {
        UIHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("Chat","notifying adapter");
                adapter.notifyDataSetChanged();
                Log.d("Chat","adapter notified");
            }
        });
    }
}
