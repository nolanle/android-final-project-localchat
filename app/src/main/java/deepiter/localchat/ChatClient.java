package deepiter.localchat;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class ChatClient {
    private BufferedReader dataIn;
    private PrintWriter dataOut;
    private String name;
    private Listener listener;

    public ChatClient(String name,BufferedReader dataIn,PrintWriter dataOut){
        this.dataIn=dataIn;
        this.dataOut=dataOut;
        this.name=name;

        listener=new Listener(dataIn);
    }

    public String getName(){
        return name;
    }

    public BufferedReader getDataIn(){
        return dataIn;
    }

    public PrintWriter getDataOut(){
        return dataOut;
    }

    public void startListening(){
        listener.listening = true;
        listener.start();
    }

    public void stopListening(){
        listener.listening = false;
    }
}
