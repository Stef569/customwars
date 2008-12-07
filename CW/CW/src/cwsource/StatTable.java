package cwsource;

public abstract class StatTable 
{
	public static final int NOT_FOUND = Integer.MIN_VALUE;
	
	protected int maxCol = 0;
	protected int maxRow = 0;
	protected int[][] statTable;
	
	protected StatTable(int mCol, int mRow)
	{
		maxCol = mCol;
		maxRow = mRow;
		statTable = new int[maxCol][maxRow];
	}
	
	public abstract void setup();
	public abstract void restoreStatTable();
	
	public final int[] getCol(int col)
	{
		if(withinColBounds(col))
			return statTable[col];
		
		return null;
	}
	
	public final int[] getRow(int row)
	{
		if(withinRowBounds(row))
		{			
			int[] result = new int[maxRow];
		
			for(int col = 0; col < maxCol; col++)
				result[col] = statTable[col][row];
			
			return result;
		}

		return null;
	}
	
	public final int getStat(int col, int row)
	{
		if(withinBounds(col, row))
			return statTable[col][row];
		
		return NOT_FOUND;
	}
	
	public final boolean withinRowBounds(int row)
	{
		return (row >= 0 && row < maxRow);
	}
	
	public final boolean withinColBounds(int col)
	{
		return (col >= 0 && col < maxCol);
	}
	
	public final boolean withinBounds(int col, int row)
	{
		return (withinColBounds(col) && withinRowBounds(row));
	}
}
