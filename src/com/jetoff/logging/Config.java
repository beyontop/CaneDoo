package com.jetoff.logging;

/**
 * Created by Alain on 22/05/2018.
 */
public class Config
{
	public static String javaVersion        = System.getProperty( "java.version" );
	public static String javaRunTimeName    = System.getProperty( "java.runtime.name" );
	public static String javaRunTimeVersion = System.getProperty( "java.runtime.version" );
	public static String javaClassPath      = System.getProperty( "java.class.path" );
	public static String javaIOtmpDir       = System.getProperty( "java.io.tmpdir" );
	public static String osName             = System.getProperty( "os.name" );
	public static String osArch             = System.getProperty( "os.arch" );
	public static String osVersion          = System.getProperty( "os.version" );
	public static String fileSeparator      = System.getProperty( "file.separator" );
	public static String pathSeparator      = System.getProperty( "path.separator" );
	public static String lineSeparator      = System.getProperty( "line.separator" );
	public static String userName           = System.getProperty( "user.name" );
	public static String userHome           = System.getProperty( "user.home" );
	public static String userDir            = System.getProperty( "user.dir" );

	Config() {
	}

	public static String getConfig()
	{
		return "Java Version: " + javaVersion + "\n" +
			"Java RunTime Name: " +  javaRunTimeName + "\n" +
			"Java RunTime Version: " + javaRunTimeVersion  + "\n" +
			"Java Class Path: " + javaClassPath + "\n" +
			"Java IO tmp dir: " + javaIOtmpDir + "\n" +
			"OS Name: " + osName + "\n" +
			"OS Architecture: " + osArch + "\n" +
			"OS Version: " + osVersion + "\n" +
			"File Separator: " + fileSeparator + "\n" +
			"Path Separator: " + pathSeparator + "\n" +
			"Line Separator: " + lineSeparator + "\n" +
			"User Name: " + userName + "\n" +
			"User Home: " + userHome + "\n" +
			"User Dir: " + userDir + "\n";
	}
}
