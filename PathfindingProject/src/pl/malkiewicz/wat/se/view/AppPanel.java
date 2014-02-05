package pl.malkiewicz.wat.se.view;

import java.awt.BorderLayout;
import javax.swing.JPanel;



public class AppPanel extends JPanel
{
	private NaviPanel naviPanel;
	private BoardPanel boardPanel;

	/**
	 * Create the panel.
	 */
	public AppPanel()
	{
		setLayout(new BorderLayout(0, 0));
		
		naviPanel = new NaviPanel();
		add(naviPanel, BorderLayout.WEST);
		
		boardPanel = new BoardPanel();
		add(boardPanel, BorderLayout.CENTER);
	}

	public NaviPanel getNaviPanel()
	{
		return naviPanel;
	}

	public void setNaviPanel(NaviPanel naviPanel)
	{
		this.naviPanel = naviPanel;
	}

	public BoardPanel getBoardPanel()
	{
		return boardPanel;
	}

	public void setBoardPanel(BoardPanel boardPanel)
	{
		this.boardPanel = boardPanel;
	}
	
	
}
