package com.jetoff.logic;

/**
 * Created by Alain on 29/12/2018.
 */
public class SudokuGraph
{
	private boolean[][] a = new boolean[81][81]; // Adjacency matrix.
	private Vertex[][] grid = new Vertex[9][9];
	private char[] colors = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i' };

	public SudokuGraph()
	{
		// Go all over the vertices of the graph,
		// as a grid (hence the 81 vertices).
		for( int i = 0; i < 9; i++ )
			for( int j = 0; j < 9; j++ )
				fillAdjacencyMatrix( i, j );
	}

	private void fillAdjacencyMatrix( int x, int y )
	{
		// For each vertex of the graph/grid, one has to decide
		// weather to set an edge with each of the others.
		for( int i = 1; i < 9; i++ )
		{
			for( int j = 1; j < 9; j++ )
			{
				if( x == i || y == j || (Math.floorDiv(x, 3) == Math.floorDiv(i, 3) && Math.floorDiv(y, 3) == Math.floorDiv(j,3)))
					;
				else
					;
			}
		}
	}

	public static int map( int x, int y )
	{
		return 8*x + y;
	}

	private class Vertex
	{
		int row;
		int column;
		char color;

		public Vertex( int row, int column, char color )
		{
			this.row = row;
			this.column = column;
			this.color = color;
		}
	}
}
