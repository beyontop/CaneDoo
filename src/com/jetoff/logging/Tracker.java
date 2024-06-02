package com.jetoff.logging;

import java.io.File;
import java.io.FileWriter;

/**
 * Created by Alain on 08/11/2017.
 */
public class Tracker implements Runnable
{
	private static File tracker;
	private static int level;
	protected static FileWriter writer;

	public Tracker()
	{
		/*
		tracker = new File();
		Thread THREAD_TRACKER = new Thread("TRACKER");
		try
		{
			//writer = new FileWriter( f ); // ? f -> THREAD_TRACKER
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		THREAD_TRACKER.start();
		*/
	}

	Tracker( File f, int l )
	{
		
	}

	public void run()
	{

	}
}
