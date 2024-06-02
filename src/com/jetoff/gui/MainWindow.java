package com.jetoff.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;

public class MainWindow extends JFrame //implements ActionListener, ItemListener
{
	static MainWindow AppWindow;
	static Container container;
	static Component glasspane;
	static JLabel StatusBar;
	private JPanel treePanel;
	
	JMenuBar MenuBar = new JMenuBar();
	JMenu Menu_1     = new JMenu( "Do" );
	JMenu Menu_2     = new JMenu( "Get" );
	JMenu Menu_3     = new JMenu( "About" );
	
	JMenuItem Menu_1_Item_1 = new JMenuItem( "Register" );
	JMenuItem Menu_1_Item_2 = new JMenuItem( "Chat" );
	JMenuItem Menu_1_Item_3 = new JMenuItem( "Social Networking" );
	JMenuItem Menu_1_Item_4 = new JMenuItem( "Close" );
	
	JCheckBoxMenuItem Menu_2_Item_1 = new JCheckBoxMenuItem( "Information" );
	JMenuItem         Menu_2_Item_2 = new JMenuItem( "Layout Changed" );
	
	JTabbedPane TabInfo;
	JColorChooser colorChooser;
	JDialog LayoutDialog;
	
	enum LAYOUT { NORTH, SOUTH, CENTER, WEST, EAST };

	JTextArea textZone;

	public MainWindow() 
	{
		this.setTitle( "Groovy" );
		this.setJMenuBar( MenuBar );

		this.Menu_1.add( Menu_1_Item_1 );
		this.Menu_1.add( Menu_1_Item_2 );
		this.Menu_1.add( Menu_1_Item_3 );
		this.Menu_1.add( Menu_1_Item_4 );
		
		this.Menu_2.add( Menu_2_Item_1 );
		this.Menu_2.add( Menu_2_Item_2 );
	
		Menu_1.setMnemonic( KeyEvent.VK_D );
		Menu_2.setMnemonic( KeyEvent.VK_G );
		Menu_3.setMnemonic( KeyEvent.VK_A );
		
		StatusBar = new JLabel( getDate(), JLabel.LEFT );
		StatusBar.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ));
		
		MakeTabInfo();

		Menu_1_Item_1.addActionListener(

				ActionEvent -> {
				System.out.println(" All Right");
				JPanel leftPanel  = MakeSectionPanel();
				//TreeFrame tree = new TreeFrame();
				//leftPanel.add( MakePanelForMe( tree, null ));
				//JScrollPane listView = new JScrollPane( tree );
				JPanel rightPanel = MakeSectionPanel();
				JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
				splitPane.setOneTouchExpandable(true);
			});

		Menu_1_Item_2.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent arg )
			{
				colorChooser = new JColorChooser();
				LayoutDialog = new JDialog( AppWindow, "Define Colors" );
			}
		});

		Menu_1_Item_4.addActionListener(

			ActionEvent -> {
				System.exit(0);
			});

		Menu_2_Item_1.addActionListener(

			ActionEvent -> {
				if ( Menu_2_Item_1.isSelected() )
				{
					container.add( TabInfo, BorderLayout.CENTER );
					TabInfo  .setVisible( true );
					//container.doLayout();
					container.validate();
				}
				if ( !Menu_2_Item_1.isSelected() )
				{
					TabInfo  .setVisible( false );
					container.remove( TabInfo );
				}	
			});

		this.MenuBar.add( Menu_1 );
		this.MenuBar.add( Menu_2 );
		this.MenuBar.add( Menu_3 );

	}

	protected JPanel MakeSectionPanel()
	{
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		return p;
	}

	public JPanel MakePanelForMe(JComponent comp, String title)
	{
		JPanel panel = new JPanel( new BorderLayout() );
		panel.add(comp, BorderLayout.CENTER);
		if (title != null)
		{
			panel.setBorder( BorderFactory.createTitledBorder( title ));
		}
		return panel;
	}

	void MakeTree()
	{

	}


	void MakeTabInfo()
	{
		TabInfo = new JTabbedPane();
		JPanel PanelNetwork = new JPanel();
		JPanel PanelSyst = new JPanel();
		TabInfo.addTab( "Système", null, PanelSyst, "Blablabla");
		TabInfo.addTab( "Network", null, PanelNetwork, "Blablabla");

		textZone = new JTextArea( "System Information: " );
		textZone.append( getTechnicalDetails() );
		JScrollPane textScroll = new JScrollPane( textZone );

		TabInfo.add( textScroll, "Technical Details" );
	}

	String getTechnicalDetails()
	{
		StringBuilder netInformation = new StringBuilder("Description: " );
		netInformation.append("\n-----------------------------------------------------------------");
		netInformation.append("\nNombre processeurs: ");
		netInformation.append( Runtime.getRuntime().availableProcessors() +"\n");
		netInformation.append("-----------------------------------------------------------------");

		try {

			Enumeration<NetworkInterface> netInterf = NetworkInterface.getNetworkInterfaces();

			for (NetworkInterface ni : Collections.list( netInterf ))
			{
				netInformation.append("\nEtiquette:            " );
				netInformation.append( ni.getDisplayName());
				netInformation.append("\nNom:                  " );
				netInformation.append( ni.getName());

				byte[] rawAddress = ni.getHardwareAddress();
				netInformation.append( "\nAdresse hard:        " );
				netInformation.append( Arrays.toString( rawAddress ) );

				Enumeration<InetAddress> ia = ni.getInetAddresses();

				for(InetAddress address : Collections.list( ia )) {
					netInformation.append("\nAdresse:              " );
					netInformation.append( address );
				}
				netInformation.append( "\nMarche              : "+ ni.isUp() );
				netInformation.append( "\nLoopback            : "+ ni.isLoopback() );
				netInformation.append( "\nPoint-à-point       : "+ ni.isPointToPoint() );
				netInformation.append( "\nMulticast           : "+ ni.supportsMulticast() );
				netInformation.append( "\nVirtuel             : "+ ni.isVirtual() );
				netInformation.append( "\nMTU                 : "+ ni.getMTU());
				netInformation.append("\n-----------------------------------------------------------------");
			}

		}
		catch( SocketException exc )
		{
			;
		}

		return netInformation.toString();
	}

	// A utiliser par un JPanel dans sa méthode "add"
	protected static JPanel setPanelForMe( JComponent cp, String title, String Layout )
	{
		JPanel panelToSet = new JPanel( new BorderLayout() );
		panelToSet.add( cp, Layout );
		if ( title != null )
			panelToSet.setBorder( BorderFactory.createTitledBorder( title ));
		if ( title == null )
			panelToSet.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ));
		return panelToSet;
	}

	public static JPanel setStatusBar()
	{
		return setPanelForMe( new JTextArea("blablabla"), "Status", BorderLayout.SOUTH );
	}
	
	String getDate()
	{
		Date CurrentDay = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat( "dd-MM-YYYY" );
		return "Today is: " + sdf.format( CurrentDay ).toString();
	}
	
	public static void showUI()
	{
		AppWindow = new MainWindow();
		container = AppWindow.getContentPane();
		glasspane = AppWindow.getGlassPane();
		container .add( StatusBar, BorderLayout.PAGE_END );
		AppWindow .setBounds( 500, 500, 1000, 500 );
		//AppWindow .setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		AppWindow .setVisible( true );
	}
	
	public static void main( String[] args ) 
	{
		try {
			Thread.currentThread().sleep( 5000 );
		}
		catch (Exception e) {
			;
		}
		javax.swing.SwingUtilities.invokeLater( new Runnable()
		{
			public void run()
			{
				UIManager.put( "swing.boldMetal", Boolean.TRUE );
                showUI();
            }
        });
        
    }
    
}
