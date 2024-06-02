package com.jetoff.gui;

import com.jetoff.learning.Training;
import com.jetoff.logging.Config;
import com.jetoff.logic.PuzzleProvider;
import com.jetoff.tools.Clock;
import com.jetoff.tools.FileHandler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**********************************************************
 * This class is the entry point in the game: offers menus,
 * tuning and so forth.
 **********************************************************/
public class SudokuGUI extends JFrame
		implements ChangeListener, ListSelectionListener, ItemListener, ActionListener
{
	private Container container;
	private Component glassPane;
	private JFrame aboutWindow;

	private JetSetter levelSelector;
	private Sudoku grid;
	/***********
	 * Main Menu
	 ***********/
	private JButton setLevelButton, playButton, LoadPuzzleBtn, aboutButton, LoadGameBtn, RegisterButton, backBtnToLife;
	private JPanel mainPanel, northPanel, leftPanel, rightPanel, southPanel, SetLevelMenu;
	private Dimension dimButtons;
	/***************
	 * Game controls
	 ***************/
	private JButton btnGenerate, btnSetLevel, btnSetOne, start_Btn, del_Btn, cancel_Btn, clear_Btn, shot_Btn, solve_Btn, return_Btn, save_Btn, backBtnButton, doItNow;
	private JButton n_1, n_2, n_3, n_4, n_5, n_6, n_7, n_8, n_9;
	private JPanel shapePanel, statusBar, menuPlay, setLevelPanel, networkPanel, menuNetworking;

	private JButton netButt_1, netButt_2, netButt_3, netButt_4;

	public JPanel numControlsPanel, headerPanel;
	private JTextArea levelDisplay;

	static final boolean PROCESS_KO = true;
	static final boolean PROCESS_OK = true;

	static final int MENU_SET_LEVEL = 1;
	static final int MENU_PLAY      = 2;
	static final int MENU_ABOUT     = 3;
	static final int MENU_NETWORK   = 4;
	static final int MENU_PRINCIPAL = 5;
	static final int INIT           = 6;
	static final int MENU_PLAY_BACK = 7;
	static final int NETWORK_ID     = 8;
	static final int SET_SUDOKU     = 9;
	static final int SUDOKU_DONE    = 10;
	static final int LOAD_SUDOKU    = 11;
	static final int START_GAME     = 12;
	static final int USER_CANCELLED = 13;
	static final int FILE_CHOOSER   = 14;
	static final int CLOCK_PURPOSE  = 15;
	static final int BACK_SIGNAL    = 1000;
	static final int THICKNESS      = 1;
	static final int CallNumber     = 0;

	int selectedLevel = 0;
	boolean settingInProcess = false;
	static boolean isLoadedSudoku = false;
	static boolean isPlayingUser = false;
	static boolean isOntopFile = false;

	/*****************
	 *  File "Chooser"
	 *****************/
	JList<String> listOfFiles;
	JButton okBtn, cancelBtn;
	JPanel fileChooser;
	String fileName;
	StringBuilder fileNamePattern;
	String currentFile;
	String puzzleFileName;
	String gameFileName;
	JFrame puzzleView;
	Sudoku gridView;
	public static String command = "noConstraint";
	JPanel gameInProcess;
	JButton backBtn, forwardBtn;

	public enum Commands {
		noConstraint, emptyBlock, reflectionSym, rotationalSym, translational;
	}

	public SudokuGUI()
	{
		super(); //!!!
		this.setTitle( "Sudoku" );
		this.setSize(600,400 );
		this.setLocation( 50, 50 );
		this.setResizable( false );
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		container = this.getContentPane();
		glassPane = this.getGlassPane();
		grid = new Sudoku();
		grid . setOpaque( true );

		glassPane.addMouseListener(
				new MouseAdapter() {
					public void mousePressed( MouseEvent event ) {
						int mouseX = event.getX();
						int mouseY = event.getY();
					}
				}
		);
		/*****************
		 *  Menu principal
		 *****************/
		mainPanel  = new JPanel();
		northPanel = new JPanel();
		leftPanel  = new JPanel();
		rightPanel = new JPanel();
		southPanel = new JPanel();
		shapePanel = new JPanel();
		menuPlay   = new JPanel();
		statusBar  = new JPanel();

		northPanel .setPreferredSize( new Dimension(600, 40 ));
		leftPanel  .setPreferredSize( new Dimension(80, 400 ));
		rightPanel .setPreferredSize( new Dimension(80, 400 ));
		southPanel .setPreferredSize( new Dimension(600, 50 ));
		mainPanel  .setPreferredSize( new Dimension(440, 400 ));
		mainPanel  .setForeground( Color.GRAY );

		setLevelButton = new JButton( "Set Level - Preferences" );
		playButton     = new JButton( "Set or Generate a Sudoku" );
		LoadPuzzleBtn  = new JButton( "Load A Sudoku Puzzle" );
		LoadGameBtn    = new JButton( "Load A Saved Game" );
		aboutButton    = new JButton( "About Sudoku" );
		RegisterButton = new JButton( "Register" );
		backBtnToLife  = new JButton( "Back To Life" );
		dimButtons     = new Dimension(300, 30 );
		//networkButton  = new JButton( "Load A Saved Game" );

		setLevelButton .setPreferredSize( dimButtons );
		playButton     .setPreferredSize( dimButtons );
		LoadPuzzleBtn  .setPreferredSize( dimButtons );
		LoadGameBtn    .setPreferredSize( dimButtons );
		aboutButton    .setPreferredSize( dimButtons );
		RegisterButton .setPreferredSize( dimButtons );
		backBtnToLife  .setPreferredSize( dimButtons );
		//networkButton  .

		mainPanel .add( setLevelButton );
		mainPanel .add( playButton );
		mainPanel .add( LoadPuzzleBtn );
		mainPanel .add( LoadGameBtn );
		mainPanel .add( RegisterButton );
		mainPanel .add( aboutButton );
		mainPanel .add( backBtnToLife );

		container .add( northPanel, BorderLayout.NORTH );
		container .add( leftPanel, BorderLayout.WEST );
		container .add( mainPanel, BorderLayout.CENTER );
		container .add( rightPanel, BorderLayout.EAST );
		container .add( southPanel, BorderLayout.SOUTH );

		setLevelButton.addActionListener(

				ActionEvent -> {
					setSudokuLevel( MENU_PRINCIPAL );
					setBottomMenu( MENU_SET_LEVEL );
				}
		);

		playButton.addActionListener(

				ActionEvent -> {
					playSudoku( MENU_PRINCIPAL );
					setBottomMenu( MENU_PLAY );
					setButtonsState( MENU_PLAY );
				}
		);

		LoadPuzzleBtn.addActionListener(

				ActionEvent -> {
					Point p = this.getLocation();
					puzzleView = displayGrid( "Sudoku", p.x+600, p.y );
					gridView = new Sudoku();
					puzzleView.add( gridView );
					loadSavedPuzzle();
					setFileChooser();
				}
		);

		LoadGameBtn.addActionListener(

				ActionEvent -> {
					Point p = this.getLocation();
					puzzleView = displayGrid("Sudoku", p.x+600, p.y );
					gridView = new Sudoku();
					puzzleView.add( gridView );
					loadSavedGame();
					setFileChooser();
				}
		);


		RegisterButton.addActionListener(

				ActionEvent -> {
					neTworKing( MENU_PRINCIPAL );
					setBottomMenu( MENU_NETWORK );
				}
		);

		aboutButton.addActionListener(

				ActionEvent -> aboutSudoku()
		);

		backBtnToLife.addActionListener( ActionEvent -> System.exit( 0 ));

		/******************
		 * "Set Level" menu
		 ******************/
		levelSelector  = new JetSetter();
		JLabel level_1 = new JLabel( " Select The Number Of Clues: " );
		level_1.setHorizontalAlignment( JLabel.CENTER );
		JLabel level_2 = new JLabel( "Leaving the cursor to 1 would produce a 17 clues Sudoku," );
		JLabel level_3 = new JLabel( "Selecting 9, a 25 clues one." );

		JPanel prettyPanel         = new JPanel( new GridLayout( 2, 3 ));
		JRadioButton noConstraint  = new JRadioButton("No Constraint", true );
		JRadioButton emptyBlock    = new JRadioButton("Empty Block" );
		JRadioButton autoMorphic   = new JRadioButton("Automorphic" );
		JRadioButton reflectionSym = new JRadioButton("Reflection" );
		JRadioButton rotationalSym = new JRadioButton("Rotational" );
		JRadioButton dihedralSym   = new JRadioButton("Dihedral" );
		JRadioButton translational = new JRadioButton("translational" );

		noConstraint.addActionListener(this);
		noConstraint.setActionCommand("noConstraint");
		emptyBlock.addActionListener(this);
		emptyBlock.setActionCommand("emptyBlock");
		reflectionSym.addActionListener(this);
		reflectionSym.setActionCommand("reflectionSym");
		rotationalSym.addActionListener(this);
		rotationalSym.setActionCommand("rotationalSym");
		translational.addActionListener(this);
		translational.setActionCommand("translational");

		ButtonGroup gr = new ButtonGroup();
		gr.add( noConstraint );
		gr.add( emptyBlock );
		gr.add( reflectionSym );
		gr.add( rotationalSym );
		gr.add( translational );
		//gr.add( dihedralSym );
		//gr.add( autoMorphic );
		prettyPanel.setBorder( BorderFactory.createEtchedBorder() );
		prettyPanel.add( noConstraint );
		prettyPanel.add( emptyBlock );
		prettyPanel.add( reflectionSym );
		prettyPanel.add( rotationalSym );
		prettyPanel.add( translational );
		//prettyPanel.add( dihedralSym );
		//prettyPanel.add( autoMorphic );
		level_1.setFont( new Font( "TRUETYPE_FONT", Font.BOLD, 13 ));
		level_2.setFont( new Font( "TRUETYPE_FONT", Font.BOLD, 13 ));
		level_3.setFont( new Font( "TRUETYPE_FONT", Font.BOLD, 13 ));

		backBtnButton = new JButton("Back Off" );
		doItNow       = new JButton( "Generate" );
		setLevelPanel = new JPanel();
		setLevelPanel . add( level_1 );
		setLevelPanel . setLayout( new GridLayout( 4, 1 ));
		setLevelPanel . add( levelSelector );
		setLevelPanel . add( prettyPanel );

		SetLevelMenu  = new JPanel();
		SetLevelMenu  . setPreferredSize( new Dimension(600, 70 ));
		SetLevelMenu  . add( backBtnButton );
		SetLevelMenu  . add( doItNow );
		levelSelector . addChangeListener( this );

		backBtnButton.addActionListener(

				ActionEvent -> {
					clearSpace( setLevelPanel );
					clearBottom( SetLevelMenu );
					setSpace( mainPanel );
					this.repaint();
				}
		);

		doItNow.addActionListener(

				ActionEvent -> {
					invokeLogic();
					playSudoku( MENU_SET_LEVEL );
					clearBottom( SetLevelMenu );
					setBottomMenu( MENU_PLAY );
					setButtonsState( MENU_SET_LEVEL );
				}
		);

		/**************
		 *  Menu "Play"
		 **************/
		// Control Buttons for the "Bottom Menu"
		start_Btn  = new JButton("Start");
		del_Btn    = new JButton("Del");
		cancel_Btn = new JButton("Cancel");
		clear_Btn  = new JButton("Clear");
		shot_Btn   = new JButton("Shot");
		solve_Btn  = new JButton("Solve");
		return_Btn = new JButton("Return");
		save_Btn   = new JButton("Save");

		btnSetLevel = new JButton( "Set Level" );
		btnGenerate = new JButton( "Generate" );
		btnSetOne   = new JButton( "Set A Grid" );

		btnSetLevel.addActionListener(

				ActionEvent -> {
					setSudokuLevel( MENU_PLAY );
					setBottomMenu( MENU_SET_LEVEL );
				}
		);

		btnGenerate.addActionListener(

				ActionEvent -> invokeLogic()
		);

		btnSetOne.addActionListener(

				ActionEvent -> SetOneSudoku()
		);

		setButtonsState( INIT );
		initControls();

		shapePanel .setPreferredSize( new Dimension(440, 400 ));
		shapePanel .add( grid );
		shapePanel .add( numControlsPanel );
		menuPlay   .setMinimumSize( new Dimension( 580, 60 ));
		menuPlay   .setLayout( new FlowLayout() );
		menuPlay   .add( start_Btn );
		menuPlay   .add( cancel_Btn );
		menuPlay   .add( del_Btn );
		menuPlay   .add( clear_Btn );
		menuPlay   .add( shot_Btn );
		menuPlay   .add( solve_Btn );
		menuPlay   .add( save_Btn );
		menuPlay   .add( return_Btn );

		start_Btn.addActionListener(

				ActionEvent -> {
					setButtonsState( START_GAME );
					setHeader( MENU_PLAY );
					zzTop();
					isPlayingUser = true;
					this.repaint();
				}
		);

		del_Btn.addActionListener(

				ActionEvent -> {
					delete();
					this.repaint();
				}
		);

		cancel_Btn.addActionListener(

				ActionEvent -> {

					clearAll();
					levelDisplay.setEnabled( true );
					settingInProcess = false;
					isLoadedSudoku = false;
					Sudoku.isSettledSudoku = false;
					gameInProcess.setVisible( false );
					setButtonsState( MENU_PLAY );
					grid.cluesCount = 0;
					levelDisplay.setText( "" + grid.cluesCount );
					this.repaint();
					// doit tenir compte du fait que l'utilisateur
					// peut être en train de disposer un sudoku...
				}
		);

		clear_Btn.addActionListener(

				ActionEvent -> {
					clearAll();
					grid.cluesCount = 0;
					levelDisplay.setText( "" + grid.cluesCount );
					isLoadedSudoku = false;
					this.repaint();
				}
		);

		shot_Btn.addActionListener(

				ActionEvent -> {
					JFrame shotWindow = getWindow( MENU_PLAY, "Your Shot" );
					shotWindow.setVisible( true );
				}
		);

		solve_Btn.addActionListener(

				ActionEvent -> {
					Solve();
					this.repaint();
				}
		);

		return_Btn.addActionListener(

				ActionEvent -> {

					setButtonsState( MENU_PLAY_BACK );
					clearSpace( shapePanel );
					clearBottom( menuPlay );
					setHeader( BACK_SIGNAL );
					gameInProcess.setVisible( false );
					setSpace( mainPanel );
					this.repaint();
				}
		);

		save_Btn.addActionListener(

				ActionEvent -> {

					if( isPlayingUser ) saveGame();
					else SavePuzzle();
				}
		);

		/*******************
		 *  Menu "A propos"
		 *******************/
		aboutWindow = getWindow( MENU_PRINCIPAL, "About Sudoku" );
		/*********************
		 *  Menu "Socializing"
		 *********************/
		menuNetworking = new JPanel();

		netButt_1  = new JButton( "ID Yourself" );
		netButt_2  = new JButton( "Connect" );
		netButt_3  = new JButton( "Play" );
		netButt_4  = new JButton( "Back To Basics" );

		menuNetworking.setMinimumSize( new Dimension( 580, 60 ));
		menuNetworking.setLayout( new FlowLayout() );
		menuNetworking.add( netButt_1 );
		menuNetworking.add( netButt_2 );
		menuNetworking.add( netButt_3 );
		menuNetworking.add( netButt_4 );

		setButtonsState( NETWORK_ID );
		networkPanel = new JPanel();

		netButt_1.addActionListener(

				ActionEvent -> {
					getIdForm();
					this.repaint();
				}
		);

		netButt_2.addActionListener(

				ActionEvent -> {
					connect();
					this.repaint();
				}
		);

		netButt_3.addActionListener(

				ActionEvent -> {
					playNetwork();
					this.repaint();
				}
		);

		netButt_4.addActionListener(
				// Switch to the usual game.
				ActionEvent -> {
					setHeader( MENU_NETWORK );
					clearSpace( networkPanel );
					clearBottom( menuNetworking );
					setSpace( shapePanel );
					setBottomMenu( MENU_PLAY );
					this.repaint();
				}
		);

		this.setVisible( true );
		/*****************
		 * End constructor
		 *****************/
	}

	/***************************
	 * Functions Shaping The UI
	 ***************************/
	void setSpace( JPanel panelToSet )
	{
		container.add( panelToSet );
		container.validate();
	}

	void clearSpace( JPanel panelToClear )
	{
		container.remove( panelToClear );
		container.validate();
	}

	void setBottomMenu( int MENU_ITEM )
	{
		switch( MENU_ITEM )
		{
			case MENU_SET_LEVEL :
				southPanel.add( SetLevelMenu );
				break;
			case MENU_PLAY :
			case LOAD_SUDOKU :
				southPanel.add( menuPlay );
				break;
			case MENU_NETWORK :
				southPanel.add( menuNetworking );
				break;
			case MENU_ABOUT :
				;
		}
		southPanel.validate();
		this.repaint();
	}

	void clearBottom( JPanel panelToRemove )
	{
		southPanel.remove( panelToRemove );
		southPanel.validate();
	}

	void setButtonsState( int CONTEXT )
	{
		switch( CONTEXT )
		{
			case MENU_PLAY :
				if( settingInProcess | isLoadedSudoku | Sudoku.isSettledSudoku ) break;
			case USER_CANCELLED :
				start_Btn.setEnabled( false );
				del_Btn.setEnabled( false );
				cancel_Btn.setEnabled( false );
				clear_Btn.setEnabled( false );
				shot_Btn.setEnabled( false );
				solve_Btn.setEnabled( false );
				return_Btn.setEnabled( true );
				save_Btn.setEnabled( false );
				btnSetLevel.setEnabled( true );
				btnGenerate.setEnabled( true );
				btnSetOne.setText( "Set A Grid" );
				btnSetOne.setEnabled( true );
				n_1.setEnabled( false );
				n_2.setEnabled( false );
				n_3.setEnabled( false );
				n_4.setEnabled( false );
				n_5.setEnabled( false );
				n_6.setEnabled( false );
				n_7.setEnabled( false );
				n_8.setEnabled( false );
				n_9.setEnabled( false );
				break;
			case MENU_PRINCIPAL :
			case MENU_SET_LEVEL :
				start_Btn.setEnabled( false ); // A supprimer
				del_Btn.setEnabled( false );
				cancel_Btn.setEnabled( false );
				clear_Btn.setEnabled( false );
				shot_Btn.setEnabled( false );
				solve_Btn.setEnabled( false );
				return_Btn.setEnabled( true );
				save_Btn.setEnabled( false );
				btnGenerate.setEnabled( true );
				btnGenerate.setEnabled( true );
				btnSetOne.setEnabled( true );
				break;
			case LOAD_SUDOKU :
				start_Btn.setEnabled( true );
				del_Btn.setEnabled( false );
				cancel_Btn.setEnabled( true );
				clear_Btn.setEnabled( false );
				shot_Btn.setEnabled( false );
				solve_Btn.setEnabled( true );
				save_Btn.setEnabled( false );
				btnSetLevel.setEnabled( false );
				btnGenerate.setEnabled( false );
				btnSetOne.setEnabled( false );
				break;
			case SET_SUDOKU :
				btnSetOne.setText( "Done" );
				btnGenerate.setEnabled( false );
				btnSetLevel.setEnabled( false );
				start_Btn.setEnabled( false );
				del_Btn.setEnabled( true );
				cancel_Btn.setEnabled( true );
				clear_Btn.setEnabled( true );
				shot_Btn.setEnabled( true );
				solve_Btn.setEnabled( false );
				return_Btn.setEnabled( false );
				save_Btn.setEnabled( false );
				n_1.setEnabled( true );
				n_2.setEnabled( true );
				n_3.setEnabled( true );
				n_4.setEnabled( true );
				n_5.setEnabled( true );
				n_6.setEnabled( true );
				n_7.setEnabled( true );
				n_8.setEnabled( true );
				n_9.setEnabled( true );
				break;
			case START_GAME :
				btnGenerate.setEnabled( false );
				btnSetLevel.setEnabled( false );
				btnSetOne.setText( "Set A Grid" );
				btnSetOne.setEnabled( false );
				start_Btn.setEnabled( false );
				del_Btn.setEnabled( true );
				cancel_Btn.setEnabled( true );
				clear_Btn.setEnabled( true );
				shot_Btn.setEnabled( true );
				solve_Btn.setEnabled( true );
				save_Btn.setEnabled( true );
				n_1.setEnabled( true );
				n_2.setEnabled( true );
				n_3.setEnabled( true );
				n_4.setEnabled( true );
				n_5.setEnabled( true );
				n_6.setEnabled( true );
				n_7.setEnabled( true );
				n_8.setEnabled( true );
				n_9.setEnabled( true );
				break;
			case SUDOKU_DONE :
				btnGenerate.setEnabled( false );
				btnSetLevel.setEnabled( false );
				start_Btn.setEnabled( true );
				del_Btn.setEnabled( false );
				cancel_Btn.setEnabled( true );
				clear_Btn.setEnabled( false );
				shot_Btn.setEnabled( false );
				solve_Btn.setEnabled( false );
				return_Btn.setEnabled( true );
				save_Btn.setEnabled( true );
				btnSetOne.setText( "Set A Grid" );
				btnSetOne.setEnabled( false );
				n_1.setEnabled( false );
				n_2.setEnabled( false );
				n_3.setEnabled( false );
				n_4.setEnabled( false );
				n_5.setEnabled( false );
				n_6.setEnabled( false );
				n_7.setEnabled( false );
				n_8.setEnabled( false );
				n_9.setEnabled( false );
				break;
			case NETWORK_ID :
				netButt_1.setEnabled( true );
				netButt_2.setEnabled( false );
				netButt_3.setEnabled( false );
				netButt_4.setEnabled( true );
				break;
			default:
				/*start_Btn.setEnabled( false );
				del_Btn.setEnabled( false );
				cancel_Btn.setEnabled( false );
				clear_Btn.setEnabled( false );*/
				//save_Btn.setEnabled( false );
				;
		}
	}

	void setHeader( int CONTEXT )
	{
		switch( CONTEXT )
		{
			case MENU_NETWORK :
			case MENU_PRINCIPAL :
			case MENU_PLAY :
				northPanel.add( headerPanel );
				break;
			case BACK_SIGNAL :
				northPanel.remove( headerPanel );
			default :
				;
		}
		northPanel.validate();
	}
	/****************************
	 * Several useful functions.
	 ****************************/
	static void beep()
	{
		Toolkit.getDefaultToolkit().beep();
	}

	static byte[] toOneDimArray( Integer[][] a )
	{
		int k = 0;
		byte[] b = new byte[81];
		for( int i = 0; i < 9; i++ )
			for( int j = 0; j < 9; j++ )
				b[k++] = a[i][j].byteValue();
		return b;
	}

	public void showDialog( String text, String title, int option )
	{
		JOptionPane.showMessageDialog( this, text, title, option );
	}

	public static JFrame getWindow( int CONTEXT, String title )
	{
		JFrame w = new JFrame();
		w.setTitle( title );

		switch( CONTEXT )
		{
			case MENU_PRINCIPAL :
				w.setSize(1000,700 );
				w.setResizable( true );
				w.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
				break;
			case MENU_PLAY :
				w.setSize(600,500 );
				w.setLocation( 450, 350 );
				w.setResizable( false );
				w.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
				break;
			case MENU_NETWORK :
				break;
			case FILE_CHOOSER :
				w.setSize( 265, 275 );
				w.setResizable( false );
				//w.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
				//w.setLocation( p[0], p[1] );
				//w.setType( Type.UTILITY );
				//w.setVisible( true );
				break;
			case CLOCK_PURPOSE :
				w.setSize(600, 60);
				w.setResizable( false );
				//w.setLocation(400, 50 );
				//w.setLocation( this.getAlignmentX(), this.getAlignmentY() + 405 );
				break;
			default:
				w.setSize(350, 400 );
				w.setResizable( true );
				w.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
		}
		return w;
	}

	public JFrame displayGrid( String title, int ... p )
	{
		JFrame w = new JFrame();
		w.setTitle( title );
		w.setSize( 265, 275 );
		w.setResizable( false );
		//w.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
		w.setLocation( p[0], p[1] );
		//w.setType( Type.UTILITY );
		//w.setVisible( true );
		return w;
	}

	public JPanel getPanel( JComponent oneComponent, String title )
	{
		JPanel panel = new JPanel( new BorderLayout() );
		panel.add( oneComponent, BorderLayout.CENTER );
		if( title != null )
			panel.setBorder( BorderFactory.createTitledBorder( title ));
		return panel;
	}

	private void initControls()
	{
		Dimension dimBtn = new Dimension( 27, 18 );
		numControlsPanel = new JPanel( new GridLayout( 9, 1));
		numControlsPanel .setSize( new Dimension( 30, 270 ));
		n_1 = new JButton( "1" ); n_1.setEnabled( false ); n_1.setSize( dimBtn ); n_1.addActionListener( ActionEvent -> editCell(1) );
		n_2 = new JButton( "2" ); n_2.setEnabled( false ); n_2.setSize( dimBtn ); n_2.addActionListener( ActionEvent -> editCell(2) );
		n_3 = new JButton( "3" ); n_3.setEnabled( false ); n_3.setSize( dimBtn ); n_3.addActionListener( ActionEvent -> editCell(3) );
		n_4 = new JButton( "4" ); n_4.setEnabled( false ); n_4.setSize( dimBtn ); n_4.addActionListener( ActionEvent -> editCell(4) );
		n_5 = new JButton( "5" ); n_5.setEnabled( false ); n_5.setSize( dimBtn ); n_5.addActionListener( ActionEvent -> editCell(5) );
		n_6 = new JButton( "6" ); n_6.setEnabled( false ); n_6.setSize( dimBtn ); n_6.addActionListener( ActionEvent -> editCell(6) );
		n_7 = new JButton( "7" ); n_7.setEnabled( false ); n_7.setSize( dimBtn ); n_7.addActionListener( ActionEvent -> editCell(7) );
		n_8 = new JButton( "8" ); n_8.setEnabled( false ); n_8.setSize( dimBtn ); n_8.addActionListener( ActionEvent -> editCell(8) );
		n_9 = new JButton( "9" ); n_9.setEnabled( false ); n_8.setSize( dimBtn ); n_9.addActionListener( ActionEvent -> editCell(9) );
		JButton delButton = new JButton( "Del" ); delButton.setMaximumSize( dimBtn );
		numControlsPanel.add( n_1 );
		numControlsPanel.add( n_2 );
		numControlsPanel.add( n_3 );
		numControlsPanel.add( n_4 );
		numControlsPanel.add( n_5 );
		numControlsPanel.add( n_6 );
		numControlsPanel.add( n_7 );
		numControlsPanel.add( n_8 );
		numControlsPanel.add( n_9 );
		n_1.setSelected( true );
		headerPanel = new JPanel( new FlowLayout() );
		JLabel levelText = new JLabel( "Number of Clues: " );
		levelDisplay = new JTextArea( "" + selectedLevel );
		levelDisplay.setEditable( false );
		levelDisplay.setBorder( BorderFactory.createEtchedBorder() );
		JLabel inviteText = new JLabel(" Generate new puzzle?  " );

		headerPanel.add( levelText );
		headerPanel.add( levelDisplay );
		headerPanel.add( inviteText );
		headerPanel.add( btnSetLevel );
		headerPanel.add( btnGenerate );
		headerPanel.add( btnSetOne );
		/****************
		 * File chooser.
		 ****************/
		listOfFiles = new JList<>();
		listOfFiles.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		listOfFiles.addListSelectionListener( this );

		okBtn = new JButton( "OK" );
		okBtn . setSize( 27, 27 );
		okBtn . setEnabled( false );
		cancelBtn = new JButton( "Cancel" );
		cancelBtn . setSize( 27, 27 );
		fileChooser = new JPanel( new GridLayout( 2, 1 ));

		okBtn.addActionListener(

				ActionEvent -> {

					String selectedFile = listOfFiles.getSelectedValue();
					if( selectedFile.startsWith( "game_" ))
					{
						fileName = "sdk_" + selectedFile.substring(5,11) + ".dat";
						setPuzzle();
						setUserGame( selectedFile );
						currentFile = selectedFile;
						isOntopFile = true;
					}
					else
					{
						fileName = selectedFile;
						setPuzzle();
						isOntopFile = false;
					}
					puzzleView.setVisible( false );
				}
		);

		cancelBtn.addActionListener(

			ActionEvent -> {
				clearSpace( fileChooser );
				setSpace( mainPanel );
				this.repaint();
				if( puzzleView.isShowing() )
					puzzleView.setVisible( false );
			}
		);

		JScrollPane s = new JScrollPane( listOfFiles );
		s.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED );
		s.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
		s.setForeground( Color.LIGHT_GRAY );

		JPanel fileList = getPanel( s, "  Choose a sudoku  " );
		fileList.setPreferredSize( new Dimension( 440, 300 ));
		JPanel panelBtn = new JPanel( new FlowLayout() );
		panelBtn.setPreferredSize( new Dimension(  440, 50 ));
		panelBtn.add( okBtn );
		panelBtn.add( cancelBtn );
		fileChooser.add( fileList );
		fileChooser.add( panelBtn );

		gameInProcess = new JPanel( new FlowLayout() );
		backBtn       = new JButton( "<<" );
		forwardBtn    = new JButton( ">>" );
		gameInProcess . add( new Clock() );
		gameInProcess . add( backBtn );
		gameInProcess . add( forwardBtn );
		//gameInProcess . setVisible( false );
		//shapePanel    . add( gameInProcess );
	}
	/**********************************
	 * Functions for internal purpose.
	 **********************************/
	void editCell( int digit )
	{
		grid.editCell( digit );
	}

	void setSudokuLevel( int CONTEXT )
	{
		switch( CONTEXT )
		{
			case MENU_PLAY :
				setHeader( BACK_SIGNAL );
				clearSpace( shapePanel );
				clearBottom( menuPlay );
				rightPanel.remove( numControlsPanel );
				break;
			case MENU_PRINCIPAL :
				clearSpace( mainPanel );
				break;
			default :
				rightPanel.remove( numControlsPanel );
		}
		setSpace( setLevelPanel );
	}

	void SetOneSudoku()
	{
		if( !settingInProcess )
		{
			setButtonsState( SET_SUDOKU );
			settingInProcess = true;
			levelDisplay.setEnabled( false );
		}
		else
		{
			if( grid.cluesCount < 17 )
			{
				showDialog("Need 17 clues minimum", "Sudoku puzzles", JOptionPane.WARNING_MESSAGE );
				return;
			}
			setButtonsState( SUDOKU_DONE );
			Sudoku.isSettledSudoku = true;
			settingInProcess = false;
		}
	}

	void playSudoku( int MENU_ITEM )
	{
		switch( MENU_ITEM )
		{
			case MENU_PRINCIPAL :
				clearSpace( mainPanel );
				break;
			case MENU_SET_LEVEL :
				clearSpace( setLevelPanel );
				break;
			case LOAD_SUDOKU :
				clearSpace( fileChooser );
		}
		setHeader( MENU_PRINCIPAL );
		setSpace( shapePanel );
		this.repaint();
	}

	void SavePuzzle()
	{
		Integer[][] gridToSave = grid.getSudokuGrid();
		byte[] b;
		Date today = new Date();
		String filename = String.format( "%1$s%2$tH%2$tM%2$tS%3$s" , "sdk_", today, ".dat" );
		/**
		 * We want the data to be serial:
		 */
		b = toOneDimArray( gridToSave );
		FileHandler f = new FileHandler( Paths.get( Config.userHome, filename ));
		f.writeAll( b );
		showDialog( filename, "File Name", JOptionPane.INFORMATION_MESSAGE );
		fileNamePattern = new StringBuilder( filename.substring(4,10) );
		setButtonsState( 1000 ); // A revoir
		System.out.println( fileNamePattern );
	}

	void saveGame()
	{
		//System.out.println( fileName );
		Integer[][] gridToSave = grid.getSudokuGrid();
		byte[] b;
		for( int i = 0; i < 9; i++ )
			for( int j = 0; j < 9; j++ )
				gridToSave[i][j] =  Sudoku.isNotFilledCellOnPuzzle[i][j] ? gridToSave[i][j] : 0;
		/**
		 * We want the data to be serial:
		 */
		b = toOneDimArray( gridToSave );
		if( !isOntopFile )
		{
			Date today = new Date();
			String file = String.format("%1$s%2$s.%3$tH%3$tM%3$tS%4$s", "game_", fileName.substring(4, 10), today, ".dat");
			FileHandler f = new FileHandler( Paths.get( Config.userHome, file ));
			f.writeAll( b );
			showDialog( file, "File Name", JOptionPane.INFORMATION_MESSAGE );
			currentFile = file;
		}
		else
		{
			FileHandler f = new FileHandler( Paths.get( Config.userHome, currentFile ));
			f.writeAll( b );
		}

		setButtonsState( 1000 ); // A revoir
	}

	/**
	 * Functions to have a compounded game/saved-game system.
	 */
	void loadSavedPuzzle()
	{
		FileHandler f = new FileHandler();
		String[] files = f.listFiles();
		listOfFiles.setListData( files );
		grid.cluesCount = 0;
		levelDisplay.setText( "" + grid.cluesCount );
	}

	void loadSavedGame()
	{
		FileHandler f = new FileHandler();
		String[] files = f.listGames();
		listOfFiles.setListData( files );
	}

	void setFileChooser()
	{
		this.repaint();
		clearSpace( mainPanel );
		setSpace( fileChooser );
		setButtonsState( LOAD_SUDOKU );
		Sudoku.isSettledSudoku = false;
	}

	void setPuzzle()
	{
		byte[] rawData = new byte[81];
		try {
			rawData = FileHandler.readAll( fileName );
		}
		catch( IOException e ) {
			e.printStackTrace();
		}
		/**
		 * Below, we clear all the data sets in case
		 * an other puzzle was already loaded.
		 */
		grid.clearAll();
		if( grid.setSudokuGrid( rawData ))
		{
			playSudoku( LOAD_SUDOKU );
			setBottomMenu( LOAD_SUDOKU );
			levelDisplay.setText( "" + grid.cluesCount );
			isLoadedSudoku = true;
		}
		else
			showDialog( "Not a valid Sudoku",
					"Loading A Sudoku",
					JOptionPane.ERROR_MESSAGE
			);
		printIt();
	}

	void setUserGame( String selectedFile )
	{
		byte[] rawData = new byte[81];
		try {
			rawData = FileHandler.readAll( selectedFile );
		}
		catch( IOException e ) {
			e.printStackTrace();
		}
		grid.setSavedGame( rawData );
	}

	void dealWithView()
	{
		byte[] rawData = new byte[81];
		String selectedFile = listOfFiles.getSelectedValue();
		if( selectedFile.startsWith( "game_" ))
		{
			fileName = "sdk_" + selectedFile.substring(5,11) + ".dat";
			//setPuzzle();
			try {
				rawData = FileHandler.readAll( fileName );
			}
			catch( IOException e ) {
				e.printStackTrace();
			}
			gridView.setSudokuGrid( rawData );
			try {
				rawData = FileHandler.readAll( selectedFile );
			}
			catch( IOException e ) {
				e.printStackTrace();
			}
			gridView.setSavedGame( rawData );
		}
		else
		{
			fileName = selectedFile;
			try {
				rawData = FileHandler.readAll( fileName );
			}
			catch( IOException e ) {
				e.printStackTrace();
			}
			gridView.clearAll();
			gridView.setSudokuGrid( rawData );
		}
	}

	boolean neTworKing( int MENU_ITEM )
	{
		switch( MENU_ITEM )
		{
			case MENU_PRINCIPAL :
				clearSpace( mainPanel );
				break;
			case MENU_PLAY :
				//southPanel.add( menuPlay );
				break;
			default:
				;
		}
		setBottomMenu( MENU_NETWORK );

		this.repaint();
		MainWindow.showUI();// w = new MainWindow();
		return PROCESS_OK;
	}

	void makeNetworkingUI( int CONTEXT )
	{
		switch( CONTEXT ) {
			case MENU_NETWORK:
				break;
			default:
				;
		}
	}

	boolean aboutSudoku()
	{
		aboutWindow.setLocation( 350, 250 );
		aboutWindow.setVisible( true );
		JTextArea config = new JTextArea( Config.getConfig() );
		JScrollPane configPane = new JScrollPane( config );
		aboutWindow.add( configPane );
		return PROCESS_OK;
	}

	/*********************
	 *  Listeners Corner.
	 *********************/
	public void stateChanged( ChangeEvent event )
	{
		JSlider source = ( JSlider ) event.getSource();
		selectedLevel  =  source.getValue();
		levelDisplay.setText( "" + selectedLevel );
	}

	public void valueChanged( ListSelectionEvent event )
	{
		if( !event.getValueIsAdjusting() )
		{
			if( listOfFiles.getSelectedIndex() == -1 )
				okBtn.setEnabled( false );
			else
			{
				okBtn.setEnabled( true );
				if( !puzzleView.isShowing() )
					puzzleView.setVisible( true );
				dealWithView();
			}
		}
	}

	/****************************************
	 * Interface Functions To Logical Units.
	/****************************************/
	void invokeLogic()
	{
		PuzzleProvider puzzle = new PuzzleProvider( selectedLevel );
		puzzle.generate();
		//grid.setSudokuGrid( toOneDimArray( puzzle.getPuzzle() ));
	}

	void delete()
	{
		grid.delete();
	}

	void clearAll()
	{
		grid.clearAll();
	}

	void Solve()
	{
		if( grid.cluesCount < 17 ) return;
		Training ts = new Training( grid.cluesCount );
		ts.setColumns( grid.getColumns() );
		ts.setRows( grid.getRows() );
		ts.setRegions( grid.getRegions() );
		ts.start();
		/*
		Point p = this.getLocation();
		Sudoku g = new Sudoku();
		JFrame trialView = displayGrid( "Sudoku", p.x+600, p.y );
		g.setSudokuGrid( toOneDimArray( grid.getSudokuGrid() ));
		g.setSavedGame( toOneDimArray( ts.getGrid() ));
		trialView.add( gridView );
		trialView.setVisible(true);
		*/
	}

	void zzTop()
	{
		JFrame w;
		JTextArea textToDisplay;
		JScrollPane resultsPane;
		w = SudokuGUI.getWindow( CLOCK_PURPOSE, "" );
		//w.setLocation( 700, 300 );
		float x = this.getAlignmentX();
		float y = this.getAlignmentY() + 405;
		w.add( gameInProcess );
		w.setLocation( (int) x, (int) y );
		w.setVisible( true );
	}

	void getIdForm()
	{

	}

	void connect()
	{

	}

	void playNetwork()
	{

	}

	void printIt()
	{
		Integer[][] sudoku;
		sudoku = grid.getSudokuGrid();
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++) {
				System.out.print(sudoku[i][j] + " ");
				if (j == 8) System.out.println("");
			}
	}

	@Override
	public void itemStateChanged( ItemEvent e )
	{
		Object o = e.getSource();
		System.out.println( o.toString() );
	}

	@Override
	public void actionPerformed( ActionEvent e ) {
		command = e.getActionCommand();
	}
}
