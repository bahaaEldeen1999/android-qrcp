package networking;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

public class udpServer extends Thread{

    private int m_Port;
    public udpServer(int port)
    {
        super();
        m_Port = port;
    }

    public void run()
    {
        try {
            boolean running = true;
            DatagramSocket socket = new DatagramSocket(m_Port);
            Log.d("START UDP", "run: ");
            Log.d("PRT UDP", String.valueOf(m_Port));
            while(running)
            {
                Log.d("Waiting", "waiting for request: ");
                byte[] buffer = new byte[10];

                DatagramPacket p = new DatagramPacket(buffer,buffer.length);

                socket.receive(p);
                InetAddress address = p.getAddress();
                int port = p.getPort();

                Log.d("Request","Request from: " + address + ":" + port + "\n");

                String dString = new Date().toString() + "\n"
                        + "Your address " + address.toString() + ":" + String.valueOf(port) + " Hi from adnroid :)\n";
                buffer = dString.getBytes();
                p = new DatagramPacket(buffer, buffer.length, address, port);
                socket.send(p);

            }

        } catch (Exception e)
        {
            Log.d("Exception in UDPServer ", e.getMessage());
        }



    }

}
