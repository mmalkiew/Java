package pl.malkiewicz.wat.se.view;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import pl.malkiewicz.wat.se.model.AppModel;

public class AppFrame extends JFrame
{

	private AppPanel appPanel;


	/**
	 * Create the frame.
	 */
	public AppFrame(AppModel model)
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 830, 550);
		appPanel = new AppPanel();
		appPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		add(appPanel);
		setVisible(true);
		setResizable(false);
	}


	public AppPanel getAppPanel()
	{
		return appPanel;
	}


	public void setAppPanel(AppPanel appPanel)
	{
		this.appPanel = appPanel;
	}

	
	
}
