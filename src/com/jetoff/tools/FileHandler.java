package com.jetoff.tools;

import com.jetoff.logging.Config;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHandler implements Serializable
{
	private Path path;
	private static String FILE_PATTERN_1 = "sdk_";
	private static String FILE_PATTERN_2 = "game_";
	private static String fileMapping    = "mapping.dat";

	public FileHandler() {
	}

	public FileHandler( Path path )
	{
		this.path = path;
	}

	public String[] listFiles()
	{
		Operate operate = new Operate();
		File f = new File( Config.userHome );
		return f.list( operate::puzzleFilter );
	}

	public String[] listGames()
	{
		Operate operate = new Operate();
		File f = new File( Config.userHome );
		return f.list( operate::gamesFilter );
	}

	public void writeAll( byte[] bytes )
	{
		try {
			Files.write( this.path, bytes );
		}
		catch( IOException e ) {
			;
		}
	}

	public static byte[] readAll( String fileName ) throws IOException
	{
		return Files.readAllBytes( Paths.get( Config.userHome, fileName ));
	}

	public static boolean existMappingFile()
	{
		return Files.exists( Paths.get( Config.userHome, fileMapping ));
	}

	public static void createMappingFile()
	{
		try {
			Files.createFile( Paths.get( Config.userHome, fileMapping ));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class Operate
	{
		boolean puzzleFilter( File f, String s )
		{
			return s.startsWith( FILE_PATTERN_1 );
		}
		boolean gamesFilter( File f, String s )
		{
			return s.startsWith( FILE_PATTERN_2 );
		}
	}
}
