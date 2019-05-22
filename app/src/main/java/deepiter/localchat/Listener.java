package deepiter.localchat;

import android.util.Log;
import java.io.BufferedReader;

public class Listener extends Thread{
    BufferedReader input;
    boolean listening;

    public Listener(BufferedReader input){
        this.input=input;
    }

    public void run(){
        Log.d("Chat","Started listening to client");
        while(listening){
            try{
                String data;
                if((data=input.readLine())!=null){
                    Log.d("Chat","Listener calling receive");
                    ChatActivity.receive(data);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
