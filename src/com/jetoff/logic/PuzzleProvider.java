package com.jetoff.logic;

import com.jetoff.gui.SudokuGUI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.TreeSet;

/**
 * Created by Alain on 10/09/2017.
 */
public final class PuzzleProvider
{
	private static int VALUE_UPPER_BOUND = 10;
	private static int INDEX_UPPER_BOUND = 9;

	public static int MINIMUM_CLUES = 17;

	private Integer[][] grid;
	private int cluesNumber;
	DigitProvider n, v;
	TreeSet<Integer> cellIndices;
	private ArrayList<HashSet<Integer>> columns = new ArrayList<>( 9 );
	private ArrayList<HashSet<Integer>> rows    = new ArrayList<>( 9 );
	private ArrayList<HashSet<Integer>> regions = new ArrayList<>( 9 );

	public PuzzleProvider( int clues )
	{
		/**
		 * The constructor generates some numbers
		 * randomly, used to produce a new puzzle.
		 */
		cluesNumber = clues;
		grid = new Integer[9][9];
		cellIndices = new TreeSet<>();
		for( int i = 0; i < 9; i++ )
			for( int j = 0; j < 9; j++ )
				grid[i][j] = 0;
	}

	public void generate()
	{
		switch ( SudokuGUI.command )
		{
			case "noConstraint" :
				break;
			case "emptyBlock" :
			case "reflectionSym" :
			case "rotationalSym" :
			case "translational" :
				;
		}
	}

	public Integer[][] getPuzzle()
	{
		return grid;
	}

	void setValue( int index, int value )
	{
		grid[RowIndex( index )][RowIndex( index )] = value;
		System.out.println( "index: " + index + " [" + RowIndex( index ) + "][" + ColumnIndex( index )+ "]" );
	}

	static public int getCipher()
	{
		return 1;
	}

	private int RowIndex(int index )
	{
		return index > 9 && index%9 == 0 ? index/9 - 1 : Math.floorDiv( index, 9 );
	}

	private int ColumnIndex(int index )
	{
		return index <= 9 ? index - 1 : index%9 == 0 ? 8 : index%9 - 1;
	}

	private class DigitProvider extends Random
	{
		private int generatedInteger = 0;
		private Random rdm;

		DigitProvider() {
			rdm = new Random();
		}

		int getFigure() {
			int i;
			for( ;; ) {
				i = this.nextInt( VALUE_UPPER_BOUND );
				if( i != 0 ) break;
			}
			return i;
		}

		int getIndex()
		{
			return this.nextInt( INDEX_UPPER_BOUND );
		}

		void clear()
		{
			generatedInteger = 0;
		}
	}

}
// *************************************************************************************
// System.out.println( "index: " + index + " [" + i( index ) + "][" + j( index )+ "]" );