package com.jetoff.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**************************************************************
 * A "Sudoku" is a "JPanel" which contains 9 "JTable"(!) - see
 * "Block" inner class to figure out why. Those tables both are
 * the blocks (or regions) and shape the grid of the Sudoku.
 * They are in charge of the rendering, editing (objects GridRenderer
 * and GridEditor), deleting of their own cells, and of the hosted
 * numbers logic (BlockModel, Block).
 **************************************************************/
public class Sudoku extends JPanel implements Observer
{
	public Block grid[][];
	public static boolean [][] isNotFilledCellOnPuzzle = new boolean[9][9];
	static int x, y;
	short cluesCount = 0;
	short cellsFilledCount = 0;
	private byte[][] a;
	static boolean isSettledSudoku = false;
	/******************************************************************************
	* HashSet data structure save us to check whether or not a given integer input
	* is already in a row, column or block (see "Block" class for the last).
	*******************************************************************************/
	public ArrayList<HashSet<Integer>> columns = new ArrayList<>( 9 );
	public ArrayList<HashSet<Integer>> rows    = new ArrayList<>( 9 );
	Block block;

	Sudoku()
	{
		super();
		setLayout( new GridLayout( 3, 3 ));
		/**
		 * A "Sudoku" is made of 9 blocks.
		 * See inner class "Block".
		 */
		grid = new Block[3][3];

		for( int i = 0; i < 3; i++ )
		{
			for( int j = 0; j < 3; j++ )
			{
				block = new Block( i, j );
				block . setDefaultRenderer( String.class, new GridRenderer() );
				block . setDefaultEditor( String.class, new GridEditor() );
				grid[i][j] = block;
				add( grid[i][j] );
			}
		}

		for( int i = 0; i < 9; i++ )
		{
			columns.add( initSet() );
			rows.add( initSet() );
		}
	}

	HashSet<Integer> initSet()
	{
		return new HashSet<>( 9 );
	}

	public void update( Observable obj, Object arg )
	{
		;
	}

	void editCell( Integer digit )
	{
		grid[x][y].editCell( digit );
	}

	void delete()
	{
		grid[x][y].delete();
	}

	void clearAll()
	{
		for( int i = 0; i < 3; i++ )
			for( int j = 0; j < 3; j++ )
				grid[i][j].clearAll();
		for( int i = 0; i < 9; i++ )
		{
			rows.get(i).clear();
			columns.get(i).clear();
		}
	}

	/**
	* Function returning a 2-dimensions array from the current state
	* of the user's Grid. Empty cells are given a zero value. Purpose:
	* to save the game as a file, solve the Sudoku puzzle and so forth.
	**/
	Integer[][] getSudokuGrid()
	{
		Integer[][] a = new Integer[9][9];
		for( int i = 0; i < 3; i++ ) {
			for ( int j = 0; j < 3; j++) {
				for ( int k = 0; k < 3; k++ ) {
					for ( int l = 0; l < 3; l++ ) {
						Object cellValue = grid[i][j].getModel().getValueAt( k, l );
						a[3*i+k][3*j+l] = cellValue.toString().equals( " " ) ? 0 :
							Integer.valueOf( (String) cellValue );
					}
				}
			}
		}
		return a;
	}

	boolean setSudokuGrid( byte[] rawDatas )
	{
		/**
		 * Price to pay to write and read the files in
		 * one shot: a little complexity thereafter...
		 **/
		a = new byte[9][9];
		for( int i = 0; i < 9; i++ )
			System.arraycopy( rawDatas, 9*i, a[i], 0, 9 );

		if( !fillHashSets() )
			return false;

		for( int i = 0; i < 3; i++ ) {
			for( int j = 0; j < 3; j++ ) {
				for( int k = 0; k < 3; k++ ) {
					for( int l = 0; l < 3; l++ )
					{
						grid[i][j].getModel().setValueAt( getCellValue(i, j, k, l) , k, l );
						shapeTheGrid( i, j, k, l );
					}
				}
			}
		}
		a = null;
		isSettledSudoku = true;
		return isSettledSudoku;
	}

	void setSavedGame( byte[] rawDatas )
	{
		a = new byte[9][9];
		for( int i = 0; i < 9; i++ )
			System.arraycopy( rawDatas, 9*i, a[i], 0, 9 );
		fillHashSets();
		for( int i = 0; i < 3; i++ )
			for( int j = 0; j < 3; j++ )
				for( int k = 0; k < 3; k++ )
					for( int l = 0; l < 3; l++ )
						grid[i][j].getModel().setValueAt( getCellValue(i, j, k, l) , k, l );
		a = null;
	}

	private String getCellValue( int i, int j, int k, int l )
	{
		int value = a[3*i+k][3*j+l];
		return ( value == 0 ) ? " " : Integer.toString( value );
	}

	void shapeTheGrid( int i, int j, int k, int l )
	{
		int value = a[3*i+k][3*j+l];
		if( value != 0 ) cluesCount++;
		isNotFilledCellOnPuzzle[3*i+k][3*j+l] = ( value == 0 );
	}

	/**
	 *  Fills the columns, rows and regions of the grid,
	 *  until one value cannot be added, then signals
	 *  failure (returns false), success otherwise (returns true).
	 */
	private boolean fillHashSets()
	{
		int value;
		for( int i = 0; i < 9; i++ )
		{
			for (int j = 0; j < 9; j++)
			{
				value = a[i][j];
				if( value == 0 )
					continue;
				if( rows.get( i ).add( value ))
				{
					if( !columns.get( j ).add( value ))
						return false;
				}
				else
					return false;
			}
		}
		for( int i = 0; i < 3; i++ ) {
			for( int j = 0; j < 3; j++ ) {
				for( int k = 0; k < 3; k++ ) {
					for( int l = 0; l < 3; l++ )
					{
						value = a[3*i+k][3*j+l];
						if( value == 0 )
							continue;
						if( !grid[i][j].region.add( value ))
							return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * The purpose of the three functions below
	 * is to export rows, columns, regions of the
	 * puzzle, for the solver.
	 **/
	public ArrayList<HashSet<Integer>> getColumns()
	{
		return columns;
	}

	public ArrayList<HashSet<Integer>> getRows()
	{
		return rows;
	}

	public ArrayList<HashSet<Integer>> getRegions()
	{
		ArrayList<HashSet<Integer>> regions = new ArrayList<>( 9 );
		for( int i = 0; i < 3; i++ ) {
			for( int j = 0; j < 3; j++ ) {
				regions.add( grid[i][j].region );
			}
		}
		return regions;
	}

	public class Block extends JTable implements FocusListener, MouseListener
	{
		/**
		*  We use 3x3 tables for blocks/regions, so we can:
		*  - check redundancy at that level (see "HashSet" field below)
		*    without having to take care of the coordinates of the cells,
		*  - manipulate the Sodoku with blocks as a logical unit when
   		*    it comes to produce new puzzles.
		**/
		int BORDER_THICKNESS = 1;
		final int ROW        = 1;
		final int COLUMN     = 2;

		int x, y;
		int SelectedRow, SelectedColumn;
		TableColumn column;
		/**
		* A Block, or "region", must not contain the same number twice, hence we use
		* a HashSet Collection, which doesn't allow duplicated items.
		**/
		public HashSet<Integer> region = new HashSet<>( 9 );

		Block( int x, int y )
		{
			super( new BlockModel() );
			this.x = x; this.y = y;
			this.setRowHeight( 27 );
			for( int i = 0 ; i < 3; i++ )
			{
				column = this.getColumnModel().getColumn( i );
				column . setPreferredWidth( 27 );
				column . setResizable( false );
			}
			this.setCellSelectionEnabled( true );
			this.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
			this.setRowSelectionAllowed( false );
			this.setBorder( BorderFactory.createLineBorder( Color.BLACK, BORDER_THICKNESS ));
			addFocusListener( this );
			addMouseListener( this );
		}

		void editCell( Integer value )
		{
			/**
			 * Allows the user to edit a cell already filled
			 * without having to delete the current value first.
			 **/
			String s = (String) this.getModel().getValueAt( SelectedRow, SelectedColumn );
			if( !s.equals( " " ))
			{
				Integer n = new Integer( s );
				if( region.contains( n ))
					region.remove( n );
				if( rows.get( indexOf( ROW )).contains( n ))
					rows.get( indexOf( ROW )).remove( n );
				if( columns.get( indexOf( COLUMN )).contains( n ))
					columns.get( indexOf( COLUMN )).remove( n );
			}
			if( isAllowed( value ))
			{
				this.getModel().setValueAt( value.toString(), SelectedRow, SelectedColumn );
				if( SudokuGUI.isPlayingUser )
					cellsFilledCount++;
				else
					cluesCount++;
			}
			else
				SudokuGUI.beep();
		}

		void delete()
		{
			String s = (String) this.getModel().getValueAt( SelectedRow, SelectedColumn );
			if( s.equals( " " ))
				return;
			Integer n = new Integer( s );
			removeFromAllSets( n );
			this.getModel().setValueAt( " ", SelectedRow, SelectedColumn );
			if( SudokuGUI.isPlayingUser )
				cellsFilledCount--;
			else
				cluesCount--;
		}

		// Eventuellement passer sur les boucles, inutiles
		// lors de l'appel dans "okBtn.addActionListener"
		void clearAll()
		{
			isSettledSudoku = false;
			for( int i = 0; i < 3; i++ ) {
				for (int j = 0; j < 3; j++) {
					this.getModel().setValueAt( " ", i, j );
				}
			}
			region.clear();
		}

		boolean isAllowed( Integer value )
		{
			if( region.add( value ))
			{
				if ( rows.get( indexOf( ROW )).add( value ))
				{
					if ( columns.get( indexOf( COLUMN )).add( value ))
						return true;
					else
					{
						rows.get( indexOf( ROW )).remove( value );
						region.remove( value );
					}
				}
				else
					region.remove( value );
			}
			return false;
		}

		boolean removeFromAllSets( int item )
		{
			if( region.contains( item ))
				region.remove( item );
			if( rows.get( indexOf( ROW )).contains( item ))
				rows.get( indexOf( ROW )).remove( item );
			if( columns.get( indexOf( COLUMN )).contains( item ))
				columns.get( indexOf( COLUMN )).remove( item );
			return true;
		}

		int indexOf( int INDEX_TYPE )
		{
			int index = 0;
			switch( INDEX_TYPE )
			{
				case ROW :
					index = 3*this.x + SelectedRow;
					break;
				case COLUMN :
					index = 3*this.y + SelectedColumn;
			}
			return index;
		}

		public void focusGained( FocusEvent event )
		{
			if( event.isTemporary() )
				return;
			SelectedRow = getSelectedRow();
			SelectedColumn = getSelectedColumn();
			Sudoku.x = this.x;
			Sudoku.y = this.y;
		}

		public void focusLost( FocusEvent event )
		{
			if( event.isTemporary() )
				return;
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
		}
	}

	class BlockModel extends AbstractTableModel
	{
		Object[][] data;
		boolean[][] isLockedCell = new boolean[3][3];
		BlockModel()
		{
			data = new String[3][3];
			for( int i = 0; i < 3; i++ )
				for( int j = 0; j < 3; j++ ) {
					data[i][j] = " ";
					isLockedCell[i][j] = false;
				}
		}
		public int getColumnCount() {
			return 3;
		}
		public int getRowCount() {
			return 3;
		}
		public String getColumnName( int col )
		{
			return "";
		}
		public Object getValueAt( int row, int column )
		{
			return data[row][column];
		}

		public Class getColumnClass( int column )
		{
			return getValueAt(0, column ).getClass();
		}
		public boolean isCellEditable( int row, int column )
		{
			return !isLockedCell[row][column];
		}
		public void setValueAt( Object value, int row, int column )
		{
			if( isSettledSudoku && isLockedCell[row][column] )
				return;
			data[row][column] = value;
			if( value.toString().equals( " " ))
				isLockedCell[row][column] = false;
			else
				if( !isSettledSudoku )
					isLockedCell[row][column] = true;
			fireTableCellUpdated( row, column );
		}
	}

	static class GridEditor extends AbstractCellEditor implements TableCellEditor, ActionListener
	{
		JButton button;
		String currentValue;
		protected static final String EDIT = "edit";

		public GridEditor()
		{
			button = new JButton();
			button.setActionCommand( EDIT );
			button.addActionListener( this );
		}

		public Component getTableCellEditorComponent( JTable table,
													  Object value,
													  boolean isSelected,
													  int row,
													  int column )
		{
			currentValue = value.toString();
			return button;
		}

		@Override
		public Object getCellEditorValue()
		{
			return currentValue;
		}

		@Override
		public void actionPerformed( ActionEvent event )
		{
			if( EDIT.equals( event.getActionCommand() ))
				fireEditingStopped();
			else
				;
		}
	}

	class GridRenderer extends JLabel implements TableCellRenderer
	{
		GridRenderer()
		{
			super();
			setOpaque( true );
			setHorizontalAlignment( JLabel.CENTER );
		}

		public Component getTableCellRendererComponent(	JTable block,
														   Object data,
														   boolean isSelected,
														   boolean hasFocus,
														   int row,
														   int column )
		{
			setText( data.toString() );
			if( isSelected )
				setBackground( Color.GREEN );
			if( hasFocus )
				;
			else
			{
				if( !block.getModel().isCellEditable( row, column ))
					setBackground( Color.LIGHT_GRAY );
				else
					setBackground( Color.WHITE );
			}
			this.setMaximumSize( new Dimension( 27, 27 ));
			return this;
		}
	}

	class Counter extends Observable
	{
		private int i;
		Counter() { i = 0; }
		void add() { i++; }
		void substract() { i--; }

	}
}
