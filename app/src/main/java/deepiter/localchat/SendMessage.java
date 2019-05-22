package deepiter.localchat;

import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.List;

public class SendMessage extends AsyncTask<ChatMessage,Void,Void> {
    private List<ChatClient> clients;
    private String username;
    private WifiP2pInfo info;
    private BufferedReader fromGroupOwner;
    private PrintWriter toGroupOwner;

    public SendMessage(List<ChatClient> clients, String username, WifiP2pInfo info, BufferedReader fromGroupOwner, PrintWriter toGroupOwner) {
        this.clients = clients;
        this.username = username;
        this.info = info;
        this.fromGroupOwner = fromGroupOwner;
        this.toGroupOwner = toGroupOwner;
    }

    @Override
    protected Void doInBackground(ChatMessage... chatMessage) {
        Log.d("Chat","Sending Message");
        if(info.isGroupOwner){
            for (ChatClient client:clients) {
                PrintWriter dataOut = client.getDataOut();
                dataOut.println(chatMessage[0].getJSONString(username));
                Log.d("Chat","Group Owner sent Message");
            }
        } else {
            toGroupOwner.println(chatMessage[0].getJSONString(username));
            Log.d("Chat","Client sent message");
        }
        return null;
    }
}
