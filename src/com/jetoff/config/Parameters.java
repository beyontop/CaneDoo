package com.jetoff.config;

import java.nio.file.Path;
import java.util.Properties;

/**
 * Created by Alain on 07/11/2017.
 */
public class Parameters
{
	public Properties rawInformation;
	public static final String configurationFileName       = "jetoff.conf";
	public static final String PersonalInformationFileName = "jetoff.prik";

	protected Properties globalConfig, privateConfig;
	protected enum items
	{
		ATOMIC_ITEM,
		ATOMIC_PUNK,
		NETWORK_SEED,
		NETWORK_INFO
	};

	//private File globalConfigFile, privateConfigFile;
	private Path configFilePath;

	Parameters()
	{
		rawInformation = new Properties();
		/*
		try ( ; )
		{
			rawInformation.load( new FileInputStream( configurationFileName ));
		}
		catch( FileNotFoundException e)
		{
			;
		}
		*/
	}
}
