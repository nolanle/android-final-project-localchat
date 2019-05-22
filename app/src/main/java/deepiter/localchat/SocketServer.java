package deepiter.localchat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class SocketServer extends AsyncTask<Void, Void, Void> {
    @SuppressLint("StaticFieldLeak")
    private BroadcastActivity callerContext;

    public SocketServer(BroadcastActivity callerContext) {
        this.callerContext = callerContext;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            ServerSocket server = new ServerSocket(8888);
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
                        //For now is accepts automatically
                        String ack = "";
                        String TAG = "##BoadcastReceiverAct";

                        try {
                            ack = new JSONObject().put("type", "ack").toString();
                        } catch (JSONException e) {
                            Log.d(TAG, "Creating ack failed: " + e.getMessage());
                        }

                        dataOut.println(ack);
                        Intent intent = new Intent(callerContext, ChatActivity.class);

                        // Give necessary info to intent.
                        callerContext.startActivity(intent);
                        Log.d(TAG, "Transitioning to chat activity");
                        break;
                    }
                }
            }
        }
        catch (IOException e) {
            Log.e("Error>> ", e.getMessage() );
        }
        return null;
    }

}
