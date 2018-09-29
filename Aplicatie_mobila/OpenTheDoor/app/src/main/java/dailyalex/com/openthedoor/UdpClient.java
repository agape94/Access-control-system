package dailyalex.com.openthedoor;


import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by alex on 26.04.2018.
 */

public class UdpClient extends Thread implements UdpSubject {
    private static UdpClient INSTANCE = new UdpClient();

    private DatagramSocket socket;
    private ArrayList<UdpObserver> observers;
    private static boolean mRun = false;
    private InetAddress serverAddr;
    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
    private DatagramPacket recvPacket;

    //Singleton class for a single instance of TCP Client
    private UdpClient() {
        observers = new ArrayList<>();
    }

    public static UdpClient getInstance(){
        return INSTANCE;
    }

    @Override
    public void run() {
        mRun = true;
        byte[] bufReceive = new byte[Constants.UDP_BUFFER];
        int indexPachet = 0;
        int nrPachete = 0;
        int nrBytes = 0;
        int k = 0, j = 0, i = 0;
        try{
            socket = new DatagramSocket(Constants.UDP_PORT);
            serverAddr = InetAddress.getByName(Constants.SERVER_ADDRESS);
            recvPacket = new DatagramPacket(bufReceive,bufReceive.length,serverAddr,Constants.UDP_PORT);

            while (mRun) {
                socket.receive(recvPacket);
                byte[] header = new byte[8];
                byte[] nrPacheteByteArray = new byte[4];
                byte[] nrBytesByteArray = new byte[4];


                header = recvPacket.getData();
                nrPacheteByteArray[3] = header[0];
                nrPacheteByteArray[2] = header[1];
                nrPacheteByteArray[1] = header[2];
                nrPacheteByteArray[0] = header[3];

                nrBytesByteArray[3] = header[4];
                nrBytesByteArray[2] = header[5];
                nrBytesByteArray[1] = header[6];
                nrBytesByteArray[0] = header[7];

                nrPachete = ByteBuffer.wrap(nrPacheteByteArray).getInt();
                nrBytes = ByteBuffer.wrap(nrBytesByteArray).getInt();

                Log.d("Total_Pachete",String.valueOf(nrPachete));
                Log.d("Total_Bytes",String.valueOf(nrBytes));


                if (nrBytes > 0 && nrPachete > 0) {
//                    sendToUdp(Constants.HEADER_RECEIVED);
                    TcpClient.getInstance().sendMessageToServer(Constants.HEADER_RECEIVED);
                }
                //Receiving the image
                byte[] imagine = new byte[nrBytes];//aloc static memorie pentru toata imaginea
                boolean pachetOk = true;
                socket.setSoTimeout(100);
                for (i = 0; i < nrPachete; i++) {

                    try {
                        socket.receive(recvPacket);
                    }catch (SocketTimeoutException e){
                        e.printStackTrace();
                        Log.e("UDP_SOCKET","Timeout..");
                        pachetOk = false;
                        break;
                    }
                    bufReceive = recvPacket.getData();

                    byte[] theIndexInBytes = new byte[4];
                    theIndexInBytes[3] = bufReceive[0];
                    theIndexInBytes[2] = bufReceive[1];
                    theIndexInBytes[1] = bufReceive[2];
                    theIndexInBytes[0] = bufReceive[3];
                    indexPachet = ByteBuffer.wrap(theIndexInBytes).getInt();

                    //copy the data received in the respective position
                    bufReceive = Arrays.copyOfRange(bufReceive, 4, Constants.UDP_BUFFER );
                    k = 0;
                    if (indexPachet < nrPachete - 1) {
                        if(indexPachet != i)
                        {
                            i = indexPachet;
                            pachetOk = false;
                            break;
                        }
                        j = 0;
                        for (j = i * (Constants.UDP_BUFFER - 4) + 1; j < (i + 1) * (Constants.UDP_BUFFER - 4); j++) {
                            imagine[j] = bufReceive[k];
                            k = k + 1;
                        }
                    }
                    if (indexPachet == nrPachete - 1  && indexPachet == i) {
                        for (j = i * (Constants.UDP_BUFFER - 4 + 1); j < imagine.length-1; j++) {
                            imagine[j] = bufReceive[k];
                            k = k + 1;
                        }
                    }
                    Log.d("PACKET_IDX", String.valueOf(indexPachet));

                }
//                TcpClient.getInstance().sendMessageToServer(Constants.IMAGE_RECEIVED);

                if(pachetOk) {
                    NotifyImageReceived(imagine);
                }
            }

        }catch (Exception e)
        {
            e.printStackTrace();
            Log.d("ERROR","=================================");
            Log.d("bufReceive",String.valueOf(bufReceive.length));
            Log.d("nrPachet",String.valueOf(indexPachet));
            Log.d("nrBytes",String.valueOf(nrBytes));
            Log.d("totalPachete",String.valueOf(nrPachete));
            Log.d("j",String.valueOf(j));
            Log.d("k",String.valueOf(k));
            Log.d("ERROR","=================================");

        }
    }

    public void stopClient()
    {
        mRun = false;
    }

    public void sendToUdp(String message){
        byte[] bufSend = (message).getBytes();
        final DatagramPacket packet = new DatagramPacket(bufSend,bufSend.length,serverAddr,Constants.UDP_PORT);

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        socket.send(packet);
                        Log.i("UDP:", "Sent to UDP: " + new String(packet.getData(),0,packet.getLength()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            };
            Thread sendMessageThread = new Thread(runnable);
            sendMessageThread.start();
    }


    public void Attach(UdpObserver o) {
        observers.add(o);
    }

    @Override
    public void Detach(UdpObserver o) {
        observers.remove(o);
    }

    @Override
    public void NotifyImageReceived(byte[] image) {
        for(int i = 0 ; i < observers.size() ; i++) {
            observers.get(i).updateUdp(image);
        }
    }

}
