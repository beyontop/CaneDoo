package com.jetoff.learning;

import com.jetoff.gui.Sudoku;
import com.jetoff.gui.SudokuGUI;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Created by Alain on 27/01/2019.
 */
public class Training extends Thread
{
	private Cell[][] previousRunBoard;
	private Cell[][] bestRunBoard;
	private short clueCount;
	private Cell [][] grid;

	private static int TARGET ;
	private static int MAX_POSSIBLE_SCORE = 0;
	private boolean SOLUTION_REACHED = false;
	/**
	 *  The constraints under which the training are the three below,
	 *  according to
	 */
	private ArrayList<HashSet<Integer>> rows    = new ArrayList<>( 9 );
	private ArrayList<HashSet<Integer>> columns = new ArrayList<>( 9 );
	private ArrayList<HashSet<Integer>> regions = new ArrayList<>( 9 );
	//private static HashSet<Cell> pool = new HashSet<>();
	Tree tree;
	private _DecisionSpace dSpace;
	private _StateSpace sSpace;

	zzTop d = new zzTop();
	int ITERATION_COUNT = 0;
	int RANDOM_TRY_BATCH = 100;
	private Agent agent;
	private static Random randomInt = new Random();

	private int indexBestRegion = 0;
	final int INDEX_0 = 0;
	final int INDEX_1 = 1;
	final int INDEX_2 = 2;
	final int INDEX_3 = 3;
	final int INDEX_4 = 4;
	final int INDEX_5 = 5;
	final int INDEX_6 = 6;
	final int INDEX_7 = 7;
	final int INDEX_8 = 8;
	float coolingRate = 0.85f;
	float temperature = 10000f;
	int radius;
	static int graphDiameter = 9;
	int gaphDegree;
	int cost = 0;
	State randomState;
	double r, y;

	public Training( short clueCount )
	{
		this.clueCount = clueCount;
		MAX_POSSIBLE_SCORE = 81 - clueCount;
		//gridModel = new Cell[9][9];
		dSpace = new _DecisionSpace( MAX_POSSIBLE_SCORE );
		sSpace = new _StateSpace();
		//dSpace.leastVisited.hitCount = 0;
		agent = new Agent();
		grid = new Cell[9][9];
	}

	public void run()
	{
		String title = "Maximum score: ";
		d.addText( title + MAX_POSSIBLE_SCORE +"\n" );
		TARGET = MAX_POSSIBLE_SCORE;
		int BEST_SCORE;
		int SCORE;
		int delta;

		prepareData();
		randomState = new State();
		BEST_SCORE = randomState.getScore();
		randomState.bestScore = BEST_SCORE;
		printIt( randomState );

		d.addText( "Target: "+TARGET );

		int bestRegionCellsCount = getBestRegionProperties();
		int transitionsCount = factorial( bestRegionCellsCount );

		d.addText(">>> bestRegionCellsCount: "+bestRegionCellsCount+" <<<" );
		d.addText(">>> Index meilleure region: "+indexBestRegion+" <<<" );
		d.addText(">>> Nombre de combinaisons: "+transitionsCount+" <<<" );
		d.addText(">>> Score: "+BEST_SCORE+" <<<" );
		d.addText(">>> Température: "+temperature+" <<<" );

		/**
		 * Main loop:
		 */
		do
		{
			for ( int i = 100/*transitionsCount*/; i > 0; i-- )
			{
				//prepareData();
				randomState.newState();
				SCORE = randomState.getScore();
				printIt( randomState );
				d.addText(">>> Score: " + SCORE + " <<<");
				delta = SCORE - BEST_SCORE;
				if ( SCORE > BEST_SCORE )
				{
					BEST_SCORE = SCORE;
					d.addText(" ");
					d.addText(">>> Last score: " + BEST_SCORE + " <<<");
				}

				if( SCORE > randomState.bestScore )
				{
					randomState.bestScore = randomState.score;
					randomState.bestState = randomState.currentState;
					randomState.actualState = randomState.currentState;
				}
				else
				{
					y = min( delta, temperature );
					r = java.lang.Math.random();
					if( y > r )
					{
						randomState.actualState = randomState.currentState;
					}
				}
			}
			temperature = temperature*coolingRate;
		}
		while ( temperature > 0 );
	}

	public void setRows( ArrayList<HashSet<Integer>> rows )
	{
		this.rows = rows;
	}
	public void setColumns( ArrayList<HashSet<Integer>> columns )
	{
		this.columns = columns;
	}
	public void setRegions( ArrayList<HashSet<Integer>> regions )
	{
		this.regions = regions;
	}

	private void prepareData()
	{
		int n = 0;
		for( int i = 0; i < 9; i++ )
		{
			for( int j = 0; j < 9; j++ )
			{
				if( Sudoku.isNotFilledCellOnPuzzle[i][j] )
				{
					n += 1;
					//dSpace.gridModel[i][j] = new CellModel();
					//Cell cell = dSpace.gridModel[i][j].cell;
					Cell cell = new Cell();
					cell.setRow(i);
					cell.setColumn(j);
					int k = getRegionIndex(i,j);
					cell.setRegion(k);
					cell.domain = getDomain( rows.get(i), columns.get(j), regions.get(k) );
					grid[i][j] = cell;
					/*
					dSpace.InitialSet.add( cell );
					dSpace.pool.add( cell );
					d.addText( "- "+n+" - ["+cell.row+","+cell.column+"] Domain: "+cell.domain );
					*/
					//d.addText( "Region; "+getComplement(regions.get(k)));
				}
				else
					; //dSpace.gridModel[i][j] = null;
			}
		}
	}

	private static HashSet<Integer> getDomain( HashSet<Integer> row,
											   HashSet<Integer> column,
											   HashSet<Integer> region )
	{
		HashSet<Integer> set = new HashSet<>(9 );
		for ( Integer n : row ) set.add(n);
		for ( Integer n : column ) set.add(n);
		for ( Integer n : region ) set.add(n);
		return getComplement( set );
	}

	private static HashSet<Integer> getRegionDomain( HashSet<Integer> region )
	{
		HashSet<Integer> set = new HashSet<>(9 );
		for ( Integer n : region ) set.add(n);
		return getComplement( set );
	}

	private static HashSet<Integer> getComplement( HashSet<Integer> set )
	{
		HashSet<Integer> s = new HashSet<>(9 );
		for( int i = 1; i <= 9; i++ )
		{
			if( !set.contains( i ))
				s.add( i );
		}
		return s;
	}

	private int getBestRegionProperties()
	{
		int size = regions.get( 0 ).size();
		for( int i = 1; i < 9; i++ )
		{
			if( regions.get( i ).size() > size )
			{
				size = regions.get( i ).size();
				indexBestRegion = i;
			}
		}
		dSpace.regionPivot.addAll( regions.get( indexBestRegion ));
		/**
		 * Returns the count of empty cells
		 * in the region selected
		 */
		return ( 9 - size );
	}

	/**
	 * Needless to have a larger return type,
	 * as we do not need to compute the factorial value
	 * for numbers greater than nine, which would fit
	 * in the "int" type.
	 * @param: the integer to compute the factorial
	 * @return: the value of the factorial
	 */
	private int factorial( int n )
	{
		int result = 1;
		if( n == 0 ) return result;
		while ( n > 1 )
		{
			result = result*n;
			n -= 1;
		}
		return result;
	}

	private static int convert( int index )
	{
		int i = index - 1;
		return i;
	}

	/**
	 * Returns a random integer between 1 and "upperBond".
	 * For 1 to 9, you give 10 to this parameter.
	 **/
	private static int getRandomInt( int upperBond )
	{
		int a;
		a = randomInt.nextInt( upperBond );
		return a;
	}

	private static int getRandomInteger()
	{
		int a;
		int upperBond = 10;
		for( ;; )
		{
			a = randomInt.nextInt( upperBond );
			if( a != 0 ) break;
		}
		return a;
	}

	private int costFunction( int m )
	{
		int cost = 0;

		return m;
	}

	private int newState()
	{
		int [][] matrix;
		return randomState.getScore();
		/*
		switchIndex( indexBestRegion );
		for ( int r = 0; r < 9; r++ )
		{
			if( r == indexBestRegion ) continue;
			switchIndex( r );
		}
		*/
	}

	double min( int delta, float temperature )
	{
		double exp = java.lang.Math.exp( -(delta/temperature) );
		return 1 > exp ? exp : 1;
	}

	/*
	private void switchIndex( int INDEX_REGION )
	{
		Object values [] = getComplement( regions.get(INDEX_REGION )).toArray();
		Object value = values[getRandomInteger(( 10 - regions.get(INDEX_REGION).size() ))];
		switch ( INDEX_REGION )
		{
			case INDEX_0 :
				//d.addText( "Region: "+INDEX_REGION+" -- "+getComplement(regions.get(INDEX_REGION)));
				for ( int i = 0; i < 3; i++ )
				{
					for ( int j = 0; j < 3; j++ )
					{
						if ( !Sudoku.isNotFilledCellOnPuzzle[i][j] )
							continue;
						Cell cell = dSpace.gridModel[i][j].cell;
						if ( hitCell( cell, (Integer)value ))
							return;
						else
							value = values[getRandomInteger(( 10 - regions.get(INDEX_REGION).size() ))];
					}
				}
				break;
			case INDEX_1 :
				for ( int i = 0; i < 3; i++ )
				{
					for ( int j = 3; j < 6; j++ )
					{
						if ( !Sudoku.isNotFilledCellOnPuzzle[i][j] )
							continue;
						Cell cell = dSpace.gridModel[i][j].cell;
						if ( hitCell( cell, (Integer)value ))
							return;
						else
							value = values[getRandomInteger(( 10 - regions.get(INDEX_REGION).size() ))];
					}
				}
				break;
			case INDEX_2 :
				for ( int i = 0; i < 3; i++ )
				{
					for ( int j = 6; j < 9; j++ )
					{
						if ( !Sudoku.isNotFilledCellOnPuzzle[i][j] )
							continue;
						Cell cell = dSpace.gridModel[i][j].cell;
						if ( hitCell( cell, (Integer)value ))
							return;
						else
							value = values[getRandomInteger(( 10 - regions.get(INDEX_REGION).size() ))];
					}
				}
				break;
			case INDEX_3 :
				for ( int i = 3; i < 6; i++ )
				{
					for ( int j = 0; j < 3; j++ )
					{
						if ( !Sudoku.isNotFilledCellOnPuzzle[i][j] )
							continue;
						Cell cell = dSpace.gridModel[i][j].cell;
						if ( hitCell( cell, (Integer)value ))
							return;
						else
							value = values[getRandomInteger(( 10 - regions.get(INDEX_REGION).size() ))];
					}
				}
				break;
			case INDEX_4 :
				for ( int i = 3; i < 6; i++ )
				{
					for ( int j = 3; j < 6; j++ )
					{
						if ( !Sudoku.isNotFilledCellOnPuzzle[i][j] )
							continue;
						Cell cell = dSpace.gridModel[i][j].cell;
						if ( hitCell( cell, (Integer)value ))
							return;
						else
							value = values[getRandomInteger(( 10 - regions.get(INDEX_REGION).size() ))];
					}
				}
				break;
			case INDEX_5 :
				for ( int i = 3; i < 6; i++ )
				{
					for ( int j = 6; j < 9; j++ )
					{
						if ( !Sudoku.isNotFilledCellOnPuzzle[i][j] )
							continue;
						Cell cell = dSpace.gridModel[i][j].cell;
						if ( hitCell( cell, (Integer)value ))
							return;
						else
							value = values[getRandomInteger(( 10 - regions.get(INDEX_REGION).size() ))];
					}
				}
				break;
			case INDEX_6 :
				for ( int i = 6; i < 9; i++ )
				{
					for ( int j = 0; j < 3; j++ )
					{
						if ( !Sudoku.isNotFilledCellOnPuzzle[i][j] )
							continue;
						Cell cell = dSpace.gridModel[i][j].cell;
						if ( hitCell( cell, (Integer)value ))
							return;
						else
							value = values[getRandomInteger(( 10 - regions.get(INDEX_REGION).size() ))];
					}
				}
				break;
			case INDEX_7 :
				for ( int i = 6; i < 9; i++ )
				{
					for ( int j = 3; j < 6; j++ )
					{
						if ( !Sudoku.isNotFilledCellOnPuzzle[i][j] )
							continue;
						Cell cell = dSpace.gridModel[i][j].cell;
						if ( hitCell( cell, (Integer)value ))
							return;
						else
							value = values[getRandomInteger(( 10 - regions.get(INDEX_REGION).size() ))];
					}
				}
				break;
			case INDEX_8 :
				for ( int i = 6; i < 9; i++ )
				{
					for ( int j = 6; j < 9; j++ )
					{
						if ( !Sudoku.isNotFilledCellOnPuzzle[i][j] )
							continue;
						Cell cell = dSpace.gridModel[i][j].cell;
						if ( hitCell( cell, (Integer)value ))
							return;
						else
							value = values[getRandomInteger(( 10 - regions.get(INDEX_REGION).size() ))];
					}
				}
		}
	}
	*/
	Node initTree()
	{
		Node root = new Node();
		root.parent = null;
		return root;
	}

	/**
	 *  Contains the main loop until solution.
	 *  Operates:
	 *  - Roll out
	 *  - Adapt policy
	 *  - Back-propagation
	 **/
	private Node build( Node root )
	{
		Node leaf, lastNode = null;
		Cell cell = dSpace.bestCell( dSpace::getCell );
		cell.hitCount = 1;
		dSpace.init( cell );
		// ...
		Node top = new Node( cell, root );
		//tree = new Tree( root );
		//tree.addToTree( root, top );
		/**
		 * Initialisation
		 */
		if( hitCell( cell ))
		{
			top.score = 1;
			d.addText( "Score = "+top.score+", [" + top.cell.row +"," + top.cell.column +"] = " + top.cell.value );
		}

		leaf = top;
		/**
		 * Real "Main"
		 * */
		while ( !SOLUTION_REACHED && ITERATION_COUNT < 20 )
		{
			lastNode = _rollOut( leaf );				    // Sampling/Expansion.
			if( lastNode.score == MAX_POSSIBLE_SCORE )
				SOLUTION_REACHED = true;
			else
				leaf = applyPolicy( lastNode );				// Selection.
			backPropagate( lastNode );						// Back-propagation.
			ITERATION_COUNT++;
		}

		d.addText( "Nombre d'itérations: "+ ITERATION_COUNT );
		d.addText( "Final score: " + leaf.score );

		return lastNode;
	}

	private Node _rollOut( Node node )
	{
		int score = node.score;
		boolean TERMINAL_STATE = false;
		Node currentNode, parentNode = node;
		do
		{
			//currentNode = new Node( dSpace.bestCell( dSpace::secondRound ), parentNode );
			currentNode = new Node( dSpace.bestCell( dSpace::getCell ), parentNode );
			currentNode.cell.hitCount += 1;
			/**
			 * Cells Essay
			 */
			if( hitCell( currentNode.cell ))
			{
				currentNode.score = parentNode.score + 1;
				update( currentNode.cell );
				if( currentNode.score == MAX_POSSIBLE_SCORE )
					return currentNode;
				parentNode = currentNode;
				d.addText( "Score = "+currentNode.score+", [" + currentNode.cell.row +"," + currentNode.cell.column +"] = "
						+ currentNode.cell.value +" Visits: "+currentNode.cell.hitCount);
			}
			else
			{
				TERMINAL_STATE = true;
				d.addText(" Terminal state, iteration = "+ITERATION_COUNT );
			}
			if( dSpace.pool.size() == 0 ) {
				//mirrorSpaces();
				//pool.addAll( InitialSet );
				d.addText("!!!!!!!!!!!!!!!!! Damn it !!!!!!!!!!!!!!!!");
				//d.addText(" After re-filling, size = "+dSpace.pool.size() );
				return currentNode;
			}
		}
		while(/*( pool.size() != 0 ) && */ !TERMINAL_STATE );
		return currentNode;
	}

	/**
	 *  Hits the given "cell", according to this one domain,
	 *  referenced as row, column, region.
	 */
	private boolean hitCell( Cell cell )
	{
		int i = cell.row, j = cell.column, k = cell.region;
		Object values [] = cell.domain.toArray();
		for( int r = 0; r < cell.domain.size(); r++)
			d.addText( "values "+ values[r]+" ");
		d.addText( "Taille: "+cell.domain.size());
		for( Iterator<Integer> CellDomain = cell.getDomainItems(); CellDomain.hasNext(); )
		{
			Integer value = CellDomain.next();
			// Attention à ne pas laisser la valeur ci-dessous
			// de façon permanete...
			cell.value = value;
			d.addText( "values: "+ cell.value+" ");
			if( rows.get(i).add( value ))
			{
				if( columns.get(j).add( value ))
				{
					if( regions.get(k).add( value ))
					{
						cell.domain.remove( value );
						dSpace.lastTrial = cell;
						cell.raiseHitCount();
						d.addText( "Last Trial: [" + dSpace.lastTrial.row +"," + dSpace.lastTrial.column +"] = "
								+ dSpace.lastTrial.value );
						d.addText( "Domaine restant: "+ cell.domain );
						return true;
					}
					else
					{
						columns.get(j).remove( value );
						rows.get(i).remove( value );
					}
				}
				else
				{
					rows.get(i).remove( value );
				}
			}
		}
		return false;
	}

	private boolean hitCell( Cell cell, Integer value )
	{
		int i = cell.row, j = cell.column, k = cell.region;
		if( rows.get(i).add( value ))
		{
			if( columns.get(j).add( value ))
			{
				if( regions.get(k).add( value ))
				{
					cell.domain.remove(value);
					dSpace.lastTrial = cell;
					cell.raiseHitCount();
					return true;
				}
				else
				{
					columns.get(j).remove(value);
					rows.get(i).remove(value);
				}
			}
			else
			{
				rows.get(i).remove(value);
			}
		}
		return false;
	}

	private Node applyPolicy( Node node )
	{
		// tester "is top of the tree in he node itself
		if( !node.hasParent() ) return node;
		return node.parent;
	}

	private void backPropagate( Node node )
	{
		// To review.
		if( !node.hasParent() ) {
			update( node.cell );
			return;
		}
		node.visitCount += 1;
		node.reward += node.score;
		node.nbrFils += 1;
		backPropagate( node.parent );
	}

	void update( Cell c )
	{
		dSpace.update( c );
		sSpace.update( c );
	}

	void adjustPolicy()
	{
		;
	}

	void mirrorSpaces()
	{
		dSpace.accept( sSpace.mirror() );
	}

	private static int getRegionIndex( int i, int j )
	{
		int k = 0;
		if( i < 3 && j < 3 ) ;
		if( i < 3 && j >= 3 && j < 6 ) k = 1;
		if( i < 3 && j >= 6 ) k = 2;
		if( i >= 3 && i < 6 && j < 3 ) k = 3;
		if( i >= 3 && i < 6 && j >= 3 && j < 6 ) k = 4;
		if( i >= 3 && i < 6 && j >= 6 ) k = 5;
		if( i >= 6 && j < 3 ) k = 6;
		if( i >= 6 && j >= 3 && j < 6 ) k = 7;
		if( i >= 6 && j >= 6 ) k = 8;
		return k;
	}

	class Cell implements Comparable<Cell>
	{
		private Integer value;
		private int row;
		private int column;
		private int region;
		private int hitCount;
		HashSet<Integer> domain;

		Cell()
		{
			domain = new HashSet<>(9 );
			hitCount = 0;
		}
		void setRow( int i ) {
			this.row = i;
		}
		void setColumn( int j ) {
			this.column = j;
		}
		void setRegion( int k ) {
			this.region = k;
		}
		int getDomainSize() { return domain.size(); }

		Iterator<Integer> getDomainItems()
		{
			return domain.iterator();
		}

		@Override
		public int compareTo( Cell o )
		{
			int v = 0;
			if( this.getDomainSize() < o.domain.size() ) v = -1;
			if( this.getDomainSize() > o.domain.size() ) v = +1;
			if( this.getDomainSize() == o.domain.size() ) v = 0;
			return v;
		}
		void raiseHitCount()
		{
			hitCount += 1;
		}
		int getCellRegion()
		{
			return this.region;
		}
		int getHitCount() {
			return hitCount;
		}
	}

	class Node implements Comparable<Node>
	{
		private Cell cell;
		private Node parent;
		private int score;
		private int reward;
		private int visitCount;
		private int nbrFils;
		private HashSet<Node> children = new HashSet<>(9);

		Node()
		{
			this.cell = null;
			this.parent = null;
			score = 0;
			reward = 0;
			visitCount = 0;
			nbrFils = 0;
		}

		Node( Cell cell, Node parent )
		{
			this.cell = cell;
			this.parent = parent;
			score = 0;
			reward = 0;
			visitCount = 0;
			nbrFils = 0;
		}

		void addChild( Node child )
		{
			children.add( child );
		}

		boolean isLeaf()
		{
			return this.children.isEmpty();
		}

		boolean hasParent(  )
		{
			if( this.parent == null )
				return false;
			else
				return true;
		}

		@Override
		public int compareTo( Node o )
		{
			int k = 0;
			if( this.score == o.score )
			{
				if( this.reward < o.reward ) k = -1;
				if( this.reward == o.reward ) k = 0;
				if( this.reward > o.reward ) k = 1;
			}
			if( this.score < o.score ) k = -1;
			if( this.score > o.score ) k = 1;
			return k;
		}
	}

	interface _Space<T> {
		void cloneData();
		boolean insert(T t);
		void classify();
		void merge();
		void split();
	}

	interface _Functor {
		Function<Cell, Node> map();
	}

	class _DecisionSpace implements _Space, _Functor
	{
		Node n;
		protected HashSet<Cell> pool;
		protected HashSet<Cell> InitialSet;
		protected Cell leastVisited, lastTrial;
		private boolean miRRor[][] = new boolean[9][9];
		protected HashSet<Integer> regionPivot = new HashSet<>( 9 );
		protected CellModel[][] gridModel;

		_DecisionSpace( int capacity )
		{
			n = new Node();
			pool = new HashSet<>( capacity );
			InitialSet = new HashSet<>( capacity );
			for( int i = 0; i < 9; i++ )
				for( int j = 0; j < 9; j++ ) {
					miRRor[i][j] = false;
				}
			gridModel = new CellModel[9][9];
		}

		void update( Cell c )
		{
			if( c.hitCount < leastVisited.hitCount )
				leastVisited = c;
			miRRor[c.row][c.column] = ( c.value != 0 );
		}

		Cell bestCell( Supplier<Cell> c )
		{
			return c.get();
		}

		Cell getCell()
		{
			Cell c = Collections.min( dSpace.pool );
			return c;
		}

		Cell secondRound() {

			Cell c = Collections.min( pool );
			if( c.hitCount < leastVisited.hitCount)
			{
				pool.remove(c);
				return c;
			}
			else
				return leastVisited;
		}

		void init( Cell c )
		{
			leastVisited = c;
			leastVisited.hitCount = c.hitCount;
		}

		void accept( boolean[][] b )
		{
			Iterator<Cell> I = InitialSet.iterator();
			while( I.hasNext() )
			{
				Cell c = I.next();
				int i = c.row, j = c.column;
				if( b[i][j] )
					pool.add(c);
			}
		}

		@Override
		public void cloneData() {
			/*pool = new HashSet<>( InitialSet )*/;
		}
		@Override
		public boolean insert( Object o ) {
			return false;
		}
		@Override
		public void classify() {

		}
		@Override
		public void merge() {

		}
		@Override
		public void split() {

		}
		@Override
		public Function<Cell, Node> map() {
			return null;
		}


	}

	private class _StateSpace implements _Space, _Functor
	{
		Cell[][] solution;
		boolean controlMatrix[][];

		_StateSpace() {
			solution = new Cell[9][9];
			controlMatrix = Sudoku.isNotFilledCellOnPuzzle;
		}

		void update( Cell c )
		{
			int i = c.row, j = c.column;
			solution[i][j] = c;

		}
		void valueHit( Cell c )
		{
			solution[c.row][c.column] = c;
		}

		boolean [][] mirror()
		{
			boolean [][] b = new boolean[9][9];
			for( int i = 0; i < 9; i++ )
				for( int j = 0; j < 9; j++ ) {
					if( solution[i][j] != null )
						b[i][j] = false;
					else
						b[i][j] = true;
				}
			return b;
		}
		@Override
		public void cloneData() {

		}
		@Override
		public boolean insert(Object o) {
			return false;
		}
		@Override
		public void classify() {

		}
		@Override
		public void merge() {

		}
		@Override
		public void split() {

		}
		@Override
		public Function<Cell, Node> map() {
			return null;
		}
	}

	class Tree
	{
		private Node root;
		private Cell leastVisitedCell;
		private Set<Cell> pool;
		Tree( Node root )
		{
			this.root = root;
		}
		void addToTree( Node parentNode, Node childNode )
		{
			parentNode.addChild( childNode );
		}
		void insertCellInTree( Cell c )
		{

		}
		Cell getLeastVisitedCell()
		{
			return leastVisitedCell;
		}
	}

	class Agent
	{
		private int lastScore;
		private boolean EXPLORATORY_MODE;
		Agent() {
			lastScore = 0;
			EXPLORATORY_MODE = false;
		}
		void greedyMode() {
			EXPLORATORY_MODE = false;
		}
		void exploratoryMode() {
			EXPLORATORY_MODE = true;
		}
		int unRoll()
		{
			if( EXPLORATORY_MODE )
				lastScore = tryOut();
			else
				lastScore = iWannaScore();

			dSpace.pool = dSpace.InitialSet;
			return lastScore;
		}

		int tryOut()
		{
			int hitCount = 0;
			Cell cell;
			while( !dSpace.pool.isEmpty() )
			{
				cell = dSpace.bestCell( dSpace::getCell );
				d.addText("Best cell: ["+cell.row+","+cell.column+"]" );
				if( hitCell( cell )) {
					hitCount += 1;
					dSpace.pool.remove( cell );
					dSpace.gridModel[cell.row][cell.column].raiseCountHit( cell.value );
					dSpace.gridModel[cell.row][cell.column].setReward();
					int u = dSpace.gridModel[cell.row][cell.column].getCountHit( cell.value );
					dSpace.gridModel[cell.row][cell.column].update_Q( cell.value, u );
					d.addText("Hit count: " + u );
					d.addText("Q value: "+dSpace.gridModel[cell.row][cell.column].qValue.getQvalue(cell.value)+" pour ["+cell.row+","+cell.column+"]" );
				}
				else
					return hitCount;
			}
			return hitCount;
		}

		int iWannaScore()
		{
			int hitCount = 0;
			int digit = 0;
			Cell cell;
			int row =0;
			int column = 0;
			int max = 0;

			while( !dSpace.pool.isEmpty() )
			{
				for( int i = 0; i < 9; i++ )
				{
					for( int j = 0; j < 9; j++ )
					{
						for( int k = 1; k <= 9; k++ )
						{
							if( Sudoku.isNotFilledCellOnPuzzle[i][j] )
							{
								int v = dSpace.gridModel[i][j].qValue.getQvalue(k);
								//d.addText("Q value: "+v+" pour ["+i+","+j+"]" );
								if( v >= max ) {
									max = v;
									row = i;
									column = j;
									digit = k;
								}
							}
						}
					}
				}

				cell = dSpace.gridModel[row][column].cell;
				d.addText("Wanna Score Q value: "+dSpace.gridModel[row][column].qValue.getQvalue(digit)+" pour: "+digit+" à: ["+row+","+column+"]" );
				if( !hitCell( cell, digit ))
					return hitCount;

				hitCount++;
				dSpace.pool.remove( cell );
			}
			return hitCount;
		}
	}

	class CellModel
	{
		Cell cell;
		int reward;
		int hitAccountByInt[];
		Q_value qValue;

		CellModel()
		{
			cell = new Cell();
			reward = 0;
			hitAccountByInt = new int[10]; // 1 to 9
			qValue = new Q_value();

			for( int i = 0; i <= 9; i++ )
				this.hitAccountByInt[i] = 0;
		}
		void setReward() {
			this.reward += 1;
		}
		void raiseCountHit( int i ) {
			this.hitAccountByInt[i] += 1;
		}

		/**
		 * @param i: indicates the number in the
		 *          list: {1, 2, ... 9}
		 */
		void update_Q( int i, int k ) {
			int Qvalue = qValue.getQvalue(i) + (1/k)*( this.reward - qValue.getQvalue(i) );
			qValue.setQValue( i, Qvalue );
		}
		int getCountHit( int i ) {
			return this.hitAccountByInt[i];
		}
	}

	class Q_value
	{
		private int digits[] = new int[10];
		Q_value() {
			for( int i = 0; i < 9; i++ )
				digits[i] = 0;
		}
		int getQvalue( int i ) {
			return digits[convert(i)];
		}
		void setQValue(int i, int value ) {
			digits[convert(i)] = value;
		}
	}

	class zzTop
	{
		JFrame w;
		JTextArea textToDisplay;
		JScrollPane resultsPane;
		zzTop() {
			w = SudokuGUI.getWindow( 8888, "Résultats" );
			w.setLocation( 700, 300 );
			textToDisplay = new JTextArea( "Comments:\n" );
			//textToDisplay.setFont( Font.ROMAN_BASELINE );
			resultsPane = new JScrollPane( textToDisplay );
			w.add( resultsPane );
			w.setVisible( true );
		}
		void addText( String text )
		{
			textToDisplay.append( text + "\n" );
		}
	}

	class State
	{
		private int score;
		private int bestScore;
		private Cell cell;
		public Integer[][] currentState;
		protected Integer[][] bestState;
		protected Integer[][] actualState;
		HashSet<Integer> regionValues;
		HashSet<Integer> ValuesByState;

		ArrayList<HashSet<Integer>> regionsByState = new ArrayList<>( 9 );
		//ArrayList<ArrayList<Integer>> regionsByState = new ArrayList<>( 9 );
		Integer[][] arrayOfValues;
		int[] regionSize;

		State()
		{
			currentState = new Integer[9][9];
			actualState = new Integer[9][9]; // ?
			score = 0;
			regionSize = new int[9];
			arrayOfValues = new Integer[9][9];
		/*
			for( int k = 0; k < 9; k++ )
			{
				regionValues = getHashSet();
				regionValues = getRegionDomain( regions.get(k) );
				regionSize[k] = regionValues.size();
				regionsByState.add( k, regionValues );
				regionsByState.get(k).toArray( arrayOfValues[k] );
			}

			for( int i = 0; i < 9; i++ )
			{
				for( int j = 0; j < 9; j++ )
				{
					if( !Sudoku.isNotFilledCellOnPuzzle[i][j] )
						continue;
					int r = grid[i][j].getCellRegion();
					Cell cell = grid[i][j];
					//Object values [] = cell.domain.toArray();

					currentState[i][j] = arrayOfValues[r];
					ValuesByState = regionsByState.get(r);
					//currentState[i][j] = ValuesByState.
					if( hitCell( cell, currentState[i][j] ))
						raiseScore();
				}
			}
			*/
			// TODO: check the value of "n", index of "values".
			for( int i = 0; i < 3; i++ ) {
				for( int j = 0; j < 3; j++ ) {
					regionValues = getHashSet();
					regionValues = getRegionDomain( regions.get(i+j+2*i) );
					Object values [] = regionValues.toArray();
					int n = 0;
					for( int k = 0; k < 3; k++ ) {
						for( int l = 0; l < 3; l++ )
						{
							if( !Sudoku.isNotFilledCellOnPuzzle[3*i+k][3*j+l] )
								continue;
							Integer value = (Integer) Array.get( values, n++ );
							currentState[3*i+k][3*j+l] = value;
						}
					}
				}
			}
			for( int i = 0; i < 9; i++ ) {
				for (int j = 0; j < 9; j++) {
					if( !Sudoku.isNotFilledCellOnPuzzle[i][j] )
						continue;
					Cell cell = grid[i][j];
					if( hitCell( cell, currentState[i][j] )) {
						raiseScore();
					}
				}
			}
			bestState = currentState;
			actualState = currentState;
			bestScore = this.score;
		}

		HashSet<Integer> getHashSet()
		{
			return new HashSet<>(9);
		}

		int getScore()
		{
			return this.score;
		}

		void raiseScore()
		{
			this.score += 1;
		}

		void resetScore()
		{
			this.score = 0;
		}

		void newState()
		{
			int i, j, k, l, m, n;
			Integer swapValue;
			resetScore();
			for( ;; )
			{
				// Once we have two indices and
				// an empty cell for these, we're done
				i = getRandomInt(9);
				j = getRandomInt(9);
				if( Sudoku.isNotFilledCellOnPuzzle[i][j] )
					break;
			}
			k = grid[i][j].getCellRegion();
			for( ;; )
			{
				m = getRandomInt(9);
				n = getRandomInt(9);
				if( !Sudoku.isNotFilledCellOnPuzzle[m][n] )
					continue;
				l = grid[m][n].getCellRegion();
				if( l != k )
					continue;
				if( m != i || n != j )
					break;
			}
			swapValue = currentState[i][j];

			if ( rows.get(i).remove( swapValue ) && columns.get(j).remove( swapValue ) && regions.get(k).remove( swapValue ) )
				d.addText(" ok 1");
			else
				d.addText( "ko 1");

			if( rows.get(m).remove( currentState[m][n] ) && columns.get(n).remove( currentState[m][n] ) && regions.get(l).remove(  currentState[m][n] ))
				d.addText(" ok 2");
			else
				d.addText( "ko 2");

			if( rows.get(i).add( currentState[m][n] ) && columns.get(j).add( currentState[m][n] ) && regions.get(k).add( currentState[m][n] ) )
				d.addText(" ok 3");
			else
				d.addText( "ko 3");

			if( rows.get(m).add( swapValue ) && columns.get(n).add( swapValue ) && regions.get(l).add( swapValue ))
				d.addText(" ok 4");
			else
				d.addText( "ko 4");

			currentState[i][j] = currentState[m][n];
			currentState[m][n] = swapValue;

			for( int a = 0; a < 9; a++ )
			{
				for( int b = 0; b < 9; b++ )
				{
					if( !Sudoku.isNotFilledCellOnPuzzle[a][b] )
						continue;
					Cell cell = grid[a][b];
					cell.domain.clear();
					cell.domain = getDomain( rows.get(a), columns.get(b), regions.get( getRegionIndex(a,b)) );
					if( hitCell( cell, currentState[a][b] ))
						raiseScore();
				}
			}
		}
	}

	void printIt( State state )
	{
		System.out.println( "" );
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++) {
				if( !Sudoku.isNotFilledCellOnPuzzle[i][j] )
					System.out.print( 0 + " " );
				else
					System.out.print( state.currentState[i][j] + " " );
				if (j == 8) System.out.println( "" );
			}
		System.out.println( "" );
	}
}
