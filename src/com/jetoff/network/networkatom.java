package com.jetoff.network;

/**
 * Created by Alain on 27/04/2017.
 */
// * -----------------
// * Network specifics
// * -----------------
import java.net.InetAddress;
import java.net.UnknownHostException;


class NetworkAtom
{
    String LocalHost;
    byte [] RawAddress;

    InetAddress localAddress;

    public NetworkAtom()
    {

        try
        {
            localAddress = InetAddress.getLocalHost();
            RawAddress = localAddress.getAddress();
            LocalHost = new String( InetAddress.getByName("localhost").toString() );
        }
        catch(UnknownHostException e)
        {
            e.getCause();
        }

        int n = RawAddress.length;
        Byte [] Address = new Byte[n];

        for ( int i = 0; i < n; i++ )
        {
            Address[i] = RawAddress[n--];
        }
    }

    public String getCrunchy()
    {
        String Host = new String( "IP address: " + localAddress.getHostAddress()
                //+ "\nRaw: " + RawAdress
                + "\nBy name: " + LocalHost
        );
        return Host;
    }
}

class FullStackAtom extends NetworkAtom
{
    // ----------------------
    // Standards port numbers
    // ----------------------
    static int FTP_PORT    = 21;
    static int TELNET_PORT = 23;
    static int SMTP_PORT   = 25;
    static int HTTP_PORT   = 80;
    static int POP3_PORT   = 110;

    //IPAdress = "0.0.0.0.";
    //HostName = 'Unknown';

    enum NetworkStack
    {
        UserDatagramProtocol,
        TransmissionControlProtocol,
        HyperTextTransferProtocol,
        TCPIP,
        FTP
    }

    static int PrimaryPort;


}