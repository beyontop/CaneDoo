package com.jetoff.gui;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class TreeFrame extends JPanel
{
    private JTree Tree;
    private DefaultMutableTreeNode Root, TNode;
    File[] Drive;
    int pass = 0;
    public ArrayList<ArrayList<File>> matrix;

    public TreeFrame()
    {
    	super();
        this.Root = new DefaultMutableTreeNode();
        Build();
    }


    void Build()
    {

        Drive = File.listRoots();

        for( int k = 0; k < Drive.length; k++ )
        {
            DefaultMutableTreeNode NDrive = new DefaultMutableTreeNode( Drive[k].getAbsolutePath() );

            try
            {

                long EndTime, StartTime;
                StartTime = System.currentTimeMillis();

                Date Start = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");

                File [] file = Drive[k].listFiles();

                for ( int i = 0; i < file.length; i++ )
                {
                    Browse( file[i], 0 );
                }

                Date Stop = new Date();
                EndTime = System.currentTimeMillis();
				/*
                System.out.println( "Temps écoulé (ms): " + (EndTime - StartTime) );
                System.out.println( "Heure début      : " + sdf.format(Start) );
                System.out.println( "Heure fin        : " + sdf.format(Stop) );
                System.out.println( "Nbre répertoires : " + pass );
				*/
                for ( int i = 0; i < matrix.size(); i++ )
                {
                    TNode = new DefaultMutableTreeNode( matrix.get(i).get(0).getName() );
                    NDrive.add( TNode );
                    for ( File child : matrix.get(i) )
                    {
                        DefaultMutableTreeNode subNode = new DefaultMutableTreeNode( child.getName() );
                        subNode.add(TNode);
                    }
                }

            }
            catch( NullPointerException excep )
            {
                ;
            }

            this.Root.add( NDrive );
        }

        Tree = new JTree( this.Root );
        Tree.setRootVisible( false );
		Tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		/*
        Tree.addTreeSelectionListener(
        		TreeSelectionEvent -> {
                	if(Tree.getLastSelectedPathComponent() != null)
                	{
                    	System.out.println(Tree.getLastSelectedPathComponent().toString());
                	}
            	});
		*/
    }

    public void Browse( File Directory, int Layer )
    {

        int CurrentLayer = Layer;

        if ( CurrentLayer == 4 )
            return;

        try
        {
            File [] Item = Directory.listFiles();
            //if( Item == null )
            //	return;

            for( int i = 0; i < Item.length; i++ )
            {
                if( Item[i].isDirectory() )
                {
                    Browse( Item[i], CurrentLayer + 1 );
                    pass++;
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

    public void BuidTree()
    {


    }

}

