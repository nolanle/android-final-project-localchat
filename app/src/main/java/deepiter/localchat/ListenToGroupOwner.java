package deepiter.localchat;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;

public class ListenToGroupOwner extends AsyncTask<Void, Void, Void> {
    @SuppressLint("StaticFieldLeak")
    private BufferedReader fromGroupOwner;

    public ListenToGroupOwner(BufferedReader fromGroupOwner) {
        this.fromGroupOwner = fromGroupOwner;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try{
            Log.d("Chat","Started listening to GroupOwner");
            while(true){
                String data;
                if((data = fromGroupOwner.readLine()) != null){
                    Log.d("Chat","Calling receive");
                    ChatActivity.receive(data);
                }
            }
        }catch(Exception e){
            Log.d("Chat","Exception in listenToGroupOwner" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
