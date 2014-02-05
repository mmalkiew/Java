package pl.malkiewicz.wat.se.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import pl.malkiewicz.wat.se.model.Edge;
import pl.malkiewicz.wat.se.model.Vertex;



public class BoardPanel extends JPanel
{
	private List<Vertex> nodes;
	private List<Edge> edges;
	private List<Vertex> path;
	
	private Vertex startVertex;
	private Vertex targetVertex;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int EMPTY = 0;
	public static final int START = 1;
	public static final int TARGET = 2;
	public static final int BLOCKED = 3;
	public static final int VISITED = 4;
	public static final int ROUTE = 5;
	
	private boolean isStartCellSelected = false;
	private boolean isTargetCellSelected = false;
	
	private int[][] currGrid;
	private int sizeBoard = 500;
	private int cellsInRow = 50;
	private int squareSize = sizeBoard/cellsInRow;
	/**
	 * Create the panel.
	 */
	public BoardPanel()
	{
		nodes = new ArrayList<Vertex>();
	    edges = new ArrayList<Edge>();
		setVisible(true);
		currGrid = new int[cellsInRow][cellsInRow];
		for (int r = 0; r < cellsInRow; r++)
		{
			for (int c = 0; c < cellsInRow; c++)
			{
				currGrid[r][c] = EMPTY;
			}
		}
		addMouseListener(new MouseHandler());
		addMouseMotionListener(new MouseHandler());
	}
	public void clearData() {
		nodes.clear();
		edges.clear();
	}
	
	private void deleteLastStartPoint() {
		for( int i = 0; i < currGrid.length; i++ ) {
			for( int j = 0; j < currGrid.length; j++ ) {
				if( currGrid[i][j] == START) {
					currGrid[i][j] = EMPTY;
				}
			}
		}
	}
	
	private void deleteLastTargetPoiont() {
		for( int i = 0; i < currGrid.length; i++ ) {
			for( int j = 0; j < currGrid.length; j++ ) {
				if( currGrid[i][j] == TARGET) {
					currGrid[i][j] = EMPTY;
				}
			}
		}
	}
	
	public void showPath( List<Vertex> path) {
		for( int i = 0; i < path.size(); i++ ) {
			Vertex v = path.get(i);
			currGrid[v.getX()][v.getY()] = ROUTE;
		}
		repaint();
	}
	
	public void clearBorad() {
		for( int i = 0; i < currGrid.length; i++ ) {
			for( int j = 0; j < currGrid.length; j++ ) {
				currGrid[i][j] = EMPTY;
			}
		}
		repaint();
	}
	
	public void changeCellsInRow(int cells) {
		this.cellsInRow = cells;
		currGrid = null;
		currGrid = new int[cellsInRow][cellsInRow];
		squareSize = sizeBoard/cellsInRow;
		clearBorad();
	}
	
	public void getAllVertex() {
		for( int i = 0; i < currGrid.length; i++ ) {
			for( int j = 0; j < currGrid.length; j++ ) {
				Vertex location = new Vertex(Integer.toString(i)+"_"+Integer.toString(j),i,j, currGrid[i][j]);
				nodes.add(location);
				System.out.println("Added Vertex: "+location.getId()+"_"+location.getStatus());
			}
		}
	}
	
	public int getStartVertexPosition() {
		for( int i = 0; i < nodes.size() ; i++ ) {
			Vertex v = nodes.get(i);
			if( v.getStatus() == START) {
				return i;
			}
		}
		return -1;
	}
	
	public int getTargetVertexPosition() {
		for( int i = 0; i < nodes.size() ; i++ ) {
			Vertex v = nodes.get(i);
			if( v.getStatus() == TARGET) {
				return i;
			}
		}
		return -1;
	}
	
	private Vertex searchVertex( int x, int y ) {
		for( int i = 0; i < nodes.size() ; i++ ) {
			Vertex v = nodes.get(i);
			if( v.getX() == x && v.getY() == y) {
				return v;
			}
		}
		return null;
	}
	
	public void createNodeLines() {
		for( int i = 0; i < nodes.size(); i++ ) {
			Vertex v = nodes.get(i);
			if( v.getStatus() != BLOCKED )
			{
				int x = v.getX();
				int y = v.getY();
				// przypadek ogolny
				if( x > 0 && y > 0 && x < cellsInRow && y < cellsInRow )
				{
					Vertex tmp = searchVertex(x-1, y);
					if( tmp != null && tmp.getStatus() != BLOCKED )
					{
						addLane(v, tmp,1);
						System.out.println("1 Added Lane: "+v.getId()+"---- >"+tmp.getId());
					}
					tmp = searchVertex(x, y-1);
					if( tmp != null && tmp.getStatus() != BLOCKED )
					{
						addLane(v, tmp,1);
						System.out.println("31Added Lane: "+v.getId()+"---- >"+tmp.getId());
					}
					tmp = searchVertex(x, y+1);
					if( tmp != null && tmp.getStatus() != BLOCKED )
					{
						addLane(v, tmp,1);
						System.out.println("2 Added Lane: "+v.getId()+"---- >"+tmp.getId());
					}
					tmp = searchVertex(x+1, y);
					if( tmp != null && tmp.getStatus() != BLOCKED )
					{
						addLane(v, tmp,1);
						System.out.println("3 Added Lane: "+v.getId()+"---- >"+tmp.getId());
					}
					
				}
				else if( x == 0 && y == 0 )
				{
					Vertex tmp = searchVertex(x+1, y);
					if( tmp != null && tmp.getStatus() != BLOCKED )
					{
						addLane(v, tmp,1);
						System.out.println("5 Added Lane: "+v.getId()+"---- >"+tmp.getId());
					}
					tmp = searchVertex(x, y+1);
					if( tmp != null && tmp.getStatus() != BLOCKED )
					{
						addLane(v, tmp,1);
						System.out.println("6 Added Lane: "+v.getId()+"---- >"+tmp.getId());
					}
				}
				else if( y == 0 && x < cellsInRow) 
				{
					Vertex tmp = searchVertex(x-1, y);
					if( tmp != null && tmp.getStatus() != BLOCKED )
					{
						addLane(v, tmp,1);
						System.out.println("7 Added Lane: "+v.getId()+"---- >"+tmp.getId());
					}
					tmp = searchVertex(x+1, y);
					if( tmp != null && tmp.getStatus() != BLOCKED )
					{
						addLane(v, tmp,1);
						System.out.println("8 Added Lane: "+v.getId()+"---- >"+tmp.getId());
					}
					tmp = searchVertex(x, y+1);
					if( tmp != null && tmp.getStatus() != BLOCKED )
					{
						addLane(v, tmp,1);
						System.out.println("9 Added Lane: "+v.getId()+"---- >"+tmp.getId());
					}
				}
				else if( x == 0 && y < cellsInRow )
				{
					Vertex tmp = searchVertex(x, y-1);
					if( tmp != null && tmp.getStatus() != BLOCKED )
					{
						addLane(v, tmp,1);
						System.out.println("10Added Lane: "+v.getId()+"---- >"+tmp.getId());
					}
					tmp = searchVertex(x, y+1);
					if( tmp != null && tmp.getStatus() != BLOCKED )
					{
						addLane(v, tmp,1);
						System.out.println("11Added Lane: "+v.getId()+"---- >"+tmp.getId());
					}
					tmp = searchVertex(x+1, y);
					if( tmp != null && tmp.getStatus() != BLOCKED )
					{
						addLane(v, tmp,1);
						System.out.println("12Added Lane: "+v.getId()+"---- >"+tmp.getId());
					}
				}
			}
		}
	}
	
	
	
	private void addLane(Vertex sourceLocNo, Vertex destLocNo,
		      int duration) {
		    Edge lane = new Edge(sourceLocNo,destLocNo, 1);
		    edges.add(lane);
		  }
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);  // Fills the background color.
		//setLayout(new GridLayout(20, 20, 20,20));
		
		for (int r = 0; r < cellsInRow; r++)
		{
			for (int c = 0; c < cellsInRow; c++)
			{
				if( currGrid[r][c] == EMPTY ) {
					g.setColor(Color.WHITE);
				}
				else if (currGrid[r][c] == START) {
                    g.setColor(Color.RED);
                } else if (currGrid[r][c] == TARGET) {
                    g.setColor(Color.GREEN);
                } else if (currGrid[r][c] == ROUTE) {
                    g.setColor(Color.RED);
                } else if (currGrid[r][c] == VISITED) {
                    g.setColor(Color.BLACK);
                } 
				g.fillRect(sizeBoard/cellsInRow*c, sizeBoard/cellsInRow*r, sizeBoard/cellsInRow, sizeBoard/cellsInRow);
				g.setColor(Color.BLACK);
				g.drawRect(sizeBoard/cellsInRow*c, sizeBoard/cellsInRow*r, sizeBoard/cellsInRow, sizeBoard/cellsInRow);
			}
		}
	}
	
	private class MouseHandler implements MouseListener, MouseMotionListener
	{
		private int cur_row, cur_col, cur_val;
		@Override
		public void mouseClicked(MouseEvent e)
		{
			
		}
		@Override
		public void mouseEntered(MouseEvent e)
		{
			
		}
		@Override
		public void mouseExited(MouseEvent e)
		{
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mousePressed(MouseEvent e)
		{
			  int row = (e.getY()) / squareSize;
	          int col = (e.getX()) / squareSize;
	          if (row >= 0 && row < cellsInRow && col >= 0 && col < cellsInRow ) {
	              cur_row = row;
	              cur_col = col;
	              cur_val = currGrid[row][col];
	              if( e.isMetaDown() && !isStartCellSelected )
	              {
	            	  if( cur_val != TARGET )
	            	  {
	            		  deleteLastStartPoint();
	            		  currGrid[row][col] = START;
	            		  isStartCellSelected = true;
	            		  isTargetCellSelected = false;
	            	  }
	              }
	              else if( e.isMetaDown() && !isTargetCellSelected )
	              {
	            	  if( cur_val != START )
	            	  {
	            		  deleteLastTargetPoiont();
	            		  currGrid[row][col] = TARGET;
	            		  isTargetCellSelected = true;
	            		  isStartCellSelected = false;
	            	  }
	              }
	              else if( !e.isMetaDown() )
	              {
	            	  if (cur_val == EMPTY){
		            	  currGrid[row][col] = BLOCKED;
		              }
	              }
	              
	          }
	          repaint();
			
		}
		@Override
		public void mouseReleased(MouseEvent e)
		{
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseDragged(MouseEvent e)
		{
			int row = (e.getY()) / squareSize;
	         int col = (e.getX() ) / squareSize;
	         if (row >= 0 && row < cellsInRow && col >= 0 && col < cellsInRow ) {
	             cur_row = row;
	             cur_col = col;
	             cur_val = currGrid[row][col];
	             if( !e.isMetaDown() )
	              {
	            	  if (cur_val == EMPTY){
		            	  currGrid[row][col] = BLOCKED;
		              }
	              }
	         }
	         repaint();
			
		}
		@Override
		public void mouseMoved(MouseEvent e)
		{
			
		}
	}

	public boolean isStartCellSelected()
	{
		return isStartCellSelected;
	}
	public void setStartCellSelected(boolean isStartCellSelected)
	{
		this.isStartCellSelected = isStartCellSelected;
	}
	public boolean isTargetCellSelected()
	{
		return isTargetCellSelected;
	}
	public void setTargetCellSelected(boolean isTargetCellSelected)
	{
		this.isTargetCellSelected = isTargetCellSelected;
	}
	public int[][] getCurrGrid()
	{
		return currGrid;
	}
	public void setCurrGrid(int[][] currGrid)
	{
		this.currGrid = currGrid;
	}
	public int getSizeBoard()
	{
		return sizeBoard;
	}
	public void setSizeBoard(int sizeBoard)
	{
		this.sizeBoard = sizeBoard;
	}
	public int getCellsInRow()
	{
		return cellsInRow;
	}
	public void setCellsInRow(int cellsInRow)
	{
		this.cellsInRow = cellsInRow;
	}
	public int getSquareSize()
	{
		return squareSize;
	}
	public void setSquareSize(int squareSize)
	{
		this.squareSize = squareSize;
	}

	public List<Vertex> getNodes()
	{
		return nodes;
	}

	public void setNodes(List<Vertex> nodes)
	{
		this.nodes = nodes;
	}

	public List<Edge> getEdges()
	{
		return edges;
	}

	public void setEdges(List<Edge> edges)
	{
		this.edges = edges;
	}

	public List<Vertex> getPath()
	{
		return path;
	}

	public void setPath(List<Vertex> path)
	{
		this.path = path;
	}
	public Vertex getTargetVertex()
	{
		return targetVertex;
	}
	public void setTargetVertex(Vertex targetVertex)
	{
		this.targetVertex = targetVertex;
	}
	public Vertex getStartVertex()
	{
		return startVertex;
	}
	public void setStartVertex(Vertex startVertex)
	{
		this.startVertex = startVertex;
	}
}
