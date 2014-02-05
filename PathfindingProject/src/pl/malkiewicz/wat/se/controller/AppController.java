package pl.malkiewicz.wat.se.controller;

import pl.malkiewicz.wat.se.model.AppModel;
import pl.malkiewicz.wat.se.view.AppFrame;

public class AppController
{
	private AppModel model;
	private AppFrame frame;
	private NaviPanelListener naviPanelListener;
	
	public AppController(AppModel model, AppFrame frame)
	{
		this.model = model;
		this.frame = frame;
		
		naviPanelListener = new NaviPanelListener( this );
		frame.getAppPanel().getNaviPanel().addPanelActionListener(naviPanelListener);
		
	}

	public AppModel getModel()
	{
		return model;
	}

	public void setModel(AppModel model)
	{
		this.model = model;
	}

	public AppFrame getFrame()
	{
		return frame;
	}

	public void setFrame(AppFrame frame)
	{
		this.frame = frame;
	}

}
