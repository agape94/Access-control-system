package dailyalex.com.openthedoor;

/**
 * Created by alex on 24.02.2018.
 */
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class TcpClient  extends Thread
                        implements TcpSubject {

    private static TcpClient INSTANCE = new TcpClient();

    //Singleton class for a single instance of TCP Client

    private ArrayList<TcpObserver> observers;
    private String mServerMessage; //The message received from the server
    private static boolean mRun = false; //while this is true, the server is running
    private PrintWriter mBufferOut; //used to send messages to the server
    private BufferedReader mBufferIn; //used to receive messages from the server
    Socket socket;

    //==============================================================================================

    private TcpClient(){
        observers = new ArrayList<TcpObserver>();
        mServerMessage = "";
    }

    //==============================================================================================

    public void sendMessageToServer(final String messageToSend){
        final String ms = messageToSend + " " + Constants.MESSAGE_STOP_FLAG;
        try {
            mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if(mBufferOut != null){
                        Log.d("SEND","Sending...");
                        mBufferOut.print(ms);
                        mBufferOut.flush();
                        Log.d("TcpClient:SEND"," Message " + messageToSend + " sent to server!");
                    }
                }
            };
            Thread sendMessageThread = new Thread(runnable);
            sendMessageThread.start();
        }catch (Exception e){
            Log.e("Error! ","Error at creating buffers!");
            stopClient();
            e.printStackTrace();
        }
    }

    //==============================================================================================

    public void stopClient(){
        mRun = false;
        socket = null;
        if(mBufferOut != null){
            mBufferOut.flush();
            mBufferOut.close();
        }
        mBufferIn = null;
        mBufferOut = null;
        mServerMessage = null;
    }

    //==============================================================================================

    public void run(){

        mRun = true;

        try{
            InetAddress serverAddr = InetAddress.getByName(Constants.SERVER_ADDRESS);
            Log.d("TcpClient "," Connecting...");
            socket = new Socket(serverAddr,Constants.TCP_PORT);
            try{
                mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                int charsRead = 0;
                char[] buffer = new char[1024];
                NotifyMessageReceived(Constants.CONNECTION_ESTABLISHED);

                while (mRun){

                    charsRead = mBufferIn.read(buffer);
//                    mServerMessage = new String(buffer).substring(0, charsRead);
                    mServerMessage = mServerMessage + new String(buffer).substring(0, charsRead);

                    if(mServerMessage.contains(Constants.MESSAGE_STOP_FLAG)) {
                        int flagPos = mServerMessage.indexOf(Constants.MESSAGE_STOP_FLAG) - 1;
                        mServerMessage = mServerMessage.substring(0,flagPos);
                        Utils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                NotifyMessageReceived(mServerMessage);
                            }
                        });
//                        mServerMessage = "";
                    }
                }

                Log.e("RESPONSE FROM SERVER: "," Received message "+mServerMessage);
            }catch (Exception e){
                Log.e("Error! ","Error at creating buffers!");
                stopClient();
                e.printStackTrace();
            }
            finally {
                Log.d("Finally","Socket closed!");
                stopClient();
                NotifyMessageReceived(Constants.CONNECTION_LOST);
            }
        }catch (Exception e){
            Log.e("Error! ","Error at connecting to the server!");
            stopClient();
            NotifyMessageReceived(Constants.SERVER_DOWN);
            e.printStackTrace();
        }
    }
    //==============================================================================================
    @Override
    public void Attach(TcpObserver o) {
        observers.add(o);
    }
    //==============================================================================================
    @Override
    public void Detach(TcpObserver o) {
        observers.remove(o);
    }
    //==============================================================================================
    @Override
    public void NotifyMessageReceived(String message) {
        Log.d("ServerMSG",message);
        for(int i = 0 ; i < observers.size() ; i++) {
            observers.get(i).updateTcp(message);
        }
        mServerMessage = "";
    }

    //==============================================================================================

    public static TcpClient getInstance(){
        return INSTANCE;
    }

    //==============================================================================================

}
