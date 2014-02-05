package pl.malkiewicz.wat.se.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;

import pl.malkiewicz.wat.se.model.DijkstraAlgorithm;
import pl.malkiewicz.wat.se.model.Edge;
import pl.malkiewicz.wat.se.model.Graph;
import pl.malkiewicz.wat.se.model.Vertex;

public class NaviPanelListener  implements ActionListener 
{
	private AppController controller;

	public NaviPanelListener(AppController appController)
	{
		this.controller = appController;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JButton clickedButton = (JButton)e.getSource();
		String actionName = clickedButton.getText();
		if( actionName.equals("Repaint Board") ) 
		{
			String newNumbCells = controller.getFrame().getAppPanel().getNaviPanel().getTextFieldNumRowCol().getText();
			if( !newNumbCells.equals("") )
			{
				controller.getFrame().getAppPanel().getBoardPanel().changeCellsInRow( Integer.parseInt(newNumbCells) );
			}
		}
		else if( actionName.equals( "Clear" ) )
		{
			controller.getFrame().getAppPanel().getBoardPanel().clearBorad();
			controller.getFrame().getAppPanel().getBoardPanel().clearData();
		}
		else if( actionName.equals( "Run" ) )
		{
			controller.getFrame().getAppPanel().getBoardPanel().getAllVertex();
			controller.getFrame().getAppPanel().getBoardPanel().createNodeLines();
			List<Vertex> nodes = controller.getFrame().getAppPanel().getBoardPanel().getNodes();
			List<Edge> edges = controller.getFrame().getAppPanel().getBoardPanel().getEdges();
			 // Lets check from location Loc_1 to Loc_10
		    Graph graph = new Graph(nodes, edges);
		    DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
		    
		    int startNode = controller.getFrame().getAppPanel().getBoardPanel().getStartVertexPosition();
		    int targetNode = controller.getFrame().getAppPanel().getBoardPanel().getTargetVertexPosition();
		    
		    dijkstra.execute(nodes.get( startNode ));
		    LinkedList<Vertex> path = dijkstra.getPath(nodes.get(targetNode));
		    controller.getFrame().getAppPanel().getBoardPanel().showPath(path);
		    
		    for( int i = 0; i < path.size(); i++ ){
		     System.out.println(path.get(i).getId());
		    }
		}
		
		
	}

}
