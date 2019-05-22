package deepiter.localchat;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class GetClientInfo extends AsyncTask<Void, Void, Void> {
    private String username;
    private ServerSocket serverSocket;
    private List<ChatClient> clients;

    public GetClientInfo(ServerSocket serverSocket, String username, List<ChatClient> clients) {
        this.serverSocket = serverSocket;
        this.username = username;
        this.clients = clients;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try{
            Log.d("Chat","waiting for client");
            Socket clientSocket = serverSocket.accept();
            Log.d("Chat","Client found");
            BufferedReader dataIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter dataOut = new PrintWriter(clientSocket.getOutputStream(),true);
            String clientName = dataIn.readLine();
            dataOut.println(username);
            ChatClient client = new ChatClient(clientName, dataIn, dataOut);
            client.startListening();

            clients.add(client);

            // update

            Log.d("Chat", "client added");
        }catch(Exception e){
            Log.d("Chat","Exception in getClientInfo" + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
