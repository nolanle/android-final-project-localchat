package deepiter.localchat;

import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectToOwner extends AsyncTask<Void, Void, Void> {
    private Socket socket;
    private WifiP2pInfo info;
    private BufferedReader fromGroupOwner;
    private PrintWriter toGroupOwner;
    private String username;

    @Override
    protected Void doInBackground(Void... voids)
    {
        InetAddress groupOwner = info.groupOwnerAddress;
        socket = new Socket();
        try
        {
            socket.connect(new InetSocketAddress(groupOwner.getHostAddress(), 8000));
            fromGroupOwner = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            toGroupOwner = new PrintWriter(socket.getOutputStream(), true);
            toGroupOwner.println(username);
            Log.d("Chat","Name sent to Owner");

            String partnerName = fromGroupOwner.readLine();

        }
        catch (IOException e)
        {
            Log.d("Chat","Exception in connectToOwner" + e.getMessage());
            e.printStackTrace();
        }
        return null;

    }
    @Override
    protected void onPostExecute(Void v){
        ListenToGroupOwner listenToGroupOwner = new ListenToGroupOwner(fromGroupOwner);
        listenToGroupOwner.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
