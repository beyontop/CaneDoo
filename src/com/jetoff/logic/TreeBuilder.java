package com.jetoff.logic;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class TreeBuilder implements Runnable
{
	private File[] Drive;
	private LinkedList<Path> queue;
	private LinkedList<File> filesQueue;
	public ArrayList<File> FileList;
	public ArrayList<File> DirList;
	DirectoryStream<Path> stream;
	//Thread tb;
	int visited = 0;
	RootedFiles routedFiles;
	private HashSet<File> fileSet;
	public ArrayList<ArrayList<Path>> Matrix;

	TreeBuilder()
	{
		queue = new LinkedList<>();
		FileList = new ArrayList<>();
		DirList  = new ArrayList<>();
		fileSet  = new HashSet<>();
		Matrix   = new ArrayList<ArrayList<Path>>();
		Thread tb = new Thread( this );
		tb.start();
	}

	@Override
	public void run()
	{
		Drive = File.listRoots();
		for ( File root : Drive ) {
			System.out.println( root.getName() );
			BuildMatrix(root);
		}
		System.out.println( "Nombre de répertoires: " + visited );//DirList.size() );

	}

	void BuildMatrix( File drive )
	{
		long EndTime, StartTime;
		StartTime = System.currentTimeMillis();
		Date Start = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");

		for ( File item : drive.listFiles() )
		{
			if( item.isDirectory() ) {
				AttachItems(0, item);
				Browse(0, item);
			}
		}
		Date Stop = new Date();
		EndTime = System.currentTimeMillis();

		System.out.println( "Temps écoulé (ms): " + (EndTime - StartTime) );
		System.out.println( "Heure début      : " + sdf.format(Start) );
		System.out.println( "Heure fin        : " + sdf.format(Stop) );
	}

	void Browse( int Layer, File Directory )
	{

		if ( Layer > 4 )
			return;

		try
		{
			File [] Item = Directory.listFiles();
			if( Item == null )
				return;

			for( int i = 0; i < Item.length; i++ )
			{
				if( Item[i].isDirectory() )
				{
					//AttachItems( Layer, Item[i] );
					Browse( Layer + 1, Item[i] );
				}
			}

		}
		catch( Exception e )
		{
			/*
			System.out.println( " -- CurrentLayer: "+ CurrentLayer );
			e.printStackTrace();
			System.exit(ERROR);
			*/
			;
		}

	}

	void AttachItems( int Layer, File father )
	{
		ArrayList<Path> pathList = new ArrayList<>();
		Path path = Paths.get( father.getName() );
		pathList.add( path );
		try
		{
			DirectoryStream<Path> stream = Files.newDirectoryStream( path );
			for ( Path entry : stream )
			{
				pathList.add( entry );
			}
			stream.close();
		}
		catch( IOException e )
		{
			;//System.out.println( father.getName() );
		}
		MakeFileSet( father );
		MakeMatrix( pathList );
	}

	void MakeFileSet( File file )
	{
		visited++;
		fileSet.add( file );
	}

	void MakeMatrix( ArrayList<Path> pathList )
	{
		Matrix.add( pathList );
	}

	class RootedFiles
	{
		private String Father;
		private ArrayList<Path> Children;

		RootedFiles( String father, ArrayList<Path> children )
		{
			this.Father = father;
			this.Children = new ArrayList<Path>( children );
		}
	}

	void breadthFirstSearch( File drive )
	{
		filesQueue = new LinkedList<>() ;
		filesQueue.add( drive );
		int nbRep = 0;
		try {

			for ( ; ; )
			{
				File [] Item = filesQueue.getFirst().listFiles();

				if( Item == null )
				{
					filesQueue.pollFirst();
					continue;
				}

				for( int i = 0; i < Item.length; i++ )
				{
					if( Item[i].isDirectory() )
					{
						filesQueue.add( Item[i] );
					}
				}

				filesQueue.pollFirst();
				if ( filesQueue.isEmpty() )
					break;
			}
		}
		catch( Exception xp ) {
			;
		}
	}

	public static void main( String[] args )
	{
		new TreeBuilder();
	}
}

/*
Files.isDirectory( entry, NOFOLLOW_LINKS )

*/