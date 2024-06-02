package com.jetoff.logging;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Formatter;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
//import sun.jvmstat.monitor.MonitoredHost;



public class Monitor extends Thread
{
    static int ERROR_LEVEL_1 = 1;
    static int ERROR_LEVEL_2 = 2;
    static int ERROR_LEVEL_3 = 3;

    long StartTime;

    LocalTime TimeLocal;

    FileInputStream LogIn = null;
    FileOutputStream LogOut = null;

    BufferedWriter Log;
    StringBuilder stb;
    //Calendar stamp;

    Formatter fmt;

    public Monitor()
    {
        StartTime = System.currentTimeMillis();
        DataOutputStream DoS;

        Calendar stamp = Calendar.getInstance();
        fmt = new Formatter();
        fmt.format( "%tF - %tT%n", stamp, stamp );

        //myString = DateFormat.getDateInstance().format(myDate);


        try
        {

            //LogIn =  new FileInputStream(new File("crunch.log"));
            //LogOut =  new FileOutputStream(new File("crunch.log"), true);

            Log = new BufferedWriter( new FileWriter("jetoff.log", true ));
            byte[] buffer = new byte[8];
            //int n = 0;

            DoS = new DataOutputStream(LogOut);

            String s = "- Début du traitement: ";

            //DoS.writeChars(s);
            //DoS.writeLong(StartTime);

            //Log.write(Long.toString(StartTime));

            Log.write( s );
            Log.write( fmt.toString() );

            //System.out.println(Long.toString(stamp.getTime()));
            //System.out.println(StartTime);

        }
        catch( FileNotFoundException e )
        {
            e.printStackTrace();
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }
        finally
        {
            ;
        }
    }

    public void initializeAll()
    {
        try( FileInputStream fis = new FileInputStream( "jetoff.log" ))
		{
            //i = fin.read();
        }
        catch( FileNotFoundException e )
        {
			;
        }
        catch( IOException e )
        {
			;
        }
    }

    public int CloseOpps()
    {

        try
        {
            //if(LogIn != null) LogIn.close();
            String s = "- Fin du traitement -\n\n";
            Log.write(s);

            if(Log != null)
                Log.close();

            fmt.close();

            return 1;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return 0;
        }
        //return 0;
    }
}
