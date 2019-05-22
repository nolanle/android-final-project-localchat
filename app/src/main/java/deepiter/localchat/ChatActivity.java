package deepiter.localchat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView listView;
    static private List<ChatMessage> Messages;
    static private ArrayAdapter<ChatMessage> adapter;
    static private WifiP2pInfo info;
    private WifiP2pManager.Channel localChannel;
    private WifiP2pManager wifiManager;
    static private List<ChatClient> clients;
    private ServerSocket serverSocket;
    private Socket socket;
    private String username;
    InetAddress groupOwner = null;
    BufferedReader fromGroupOwner;
    PrintWriter toGroupOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FA8F16")));

        //Set Button listener
        ImageButton send = (ImageButton) findViewById(R.id.send);
        send.setOnClickListener(this);

        Messages = new ArrayList<>();
        listView = (ListView) findViewById(R.id.chat);

        //set ListView adapter first
        adapter = new ChatMessageAdapter(this, R.layout.foreign_bubble, Messages);
        listView.setAdapter(adapter);

        clients = new ArrayList<>();

        //force portrait view
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /* TODO: Get the P2pManager and Channel Objects and your own name for the connection information */
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        info = (WifiP2pInfo) bundle.get("info");
        username = bundle.getString("username");
        initialize();
    }

    public static void receive(String data){
        ChatMessage chatMessage = new ChatMessage(data);
        Log.d("Chat","received something");
        Log.d("Chat",chatMessage.getText());
        Messages.add(chatMessage);
        updateOnMain();
    }

    public void initialize (){
        Log.d("Chat", "is GroupOwner?" + Boolean.toString(info.isGroupOwner));

        if(clients != null) {
            clients.clear();
        }

        if(info.isGroupOwner){
            //wait for clients to make contact and add them to a list
            try {
                serverSocket = new ServerSocket(8000);
            }
            catch(Exception e){
                Log.d("Chat", "afterServerSocket: "+ e.getMessage());

            }

            GetClientInfo getClientInfo = new GetClientInfo(serverSocket, username, clients);
            getClientInfo.execute();
        }
        else {
            //make contact with group owner
            try{
                ConnectToOwner connectToOwner = new ConnectToOwner();
                connectToOwner.execute();
            }
            catch(Exception e) {
                Log.d("Chat", "notGroupOwner "+ e.getMessage());
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.send) {
            //get the text
            EditText editText = (EditText) findViewById(R.id.editText);
            String text = editText.getText().toString();

            //get current time ( I used the deprecated Class Time to ensure backwards compatability)
            Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();
            String minute;
            if (today.minute < 10) {
                minute = "0" + String.valueOf(today.minute);
            } else {
                minute = String.valueOf(today.minute);
            }

            String time = today.hour + ":" + minute;

            //add message to the list
            ChatMessage chatMessage = new ChatMessage(text.toString(), true, "", time);
            Messages.add(chatMessage);
            adapter.notifyDataSetChanged();
            editText.setText("");

            Log.d("Chat","Executing sendMessage");
            SendMessage sendMessage = new SendMessage(clients, username, info, fromGroupOwner, toGroupOwner);
            sendMessage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, chatMessage);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            if(info.isGroupOwner) {
                serverSocket.close();
            }else{
                socket.close();
            }
        }
        catch(Exception e){
            Log.d("DESTROYERROR", e.getMessage());
        }

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
