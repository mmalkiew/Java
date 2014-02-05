package pl.malkiewicz.wat.se.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

public class NaviPanel extends JPanel
{
	private JTextField textFieldNumRowCol;
	private JButton btnRun;
	private JButton btnClear;
	private JButton btnRepaintBoard;

	/**
	 * Create the panel.
	 */
	public NaviPanel()
	{
		setSize(100, 500);
		setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		
		
		JLabel lblNumberOfRowscolumns = new JLabel("Number of rows/columns:");
		add(lblNumberOfRowscolumns, "2, 4, right, default");
		
		textFieldNumRowCol = new JTextField();
		add(textFieldNumRowCol, "4, 4, fill, default");
		textFieldNumRowCol.setColumns(10);
		
		btnRepaintBoard = new JButton("Repaint Board");
		add(btnRepaintBoard, "2, 6");
		
		btnClear = new JButton("Clear");
		add(btnClear, "2, 8");
		
		btnRun = new JButton("Run");
		add(btnRun, "2, 10");

	}
	
	public void addPanelActionListener( ActionListener listener ) {
		btnClear.addActionListener(listener);
		btnRepaintBoard.addActionListener(listener);
		btnRun.addActionListener(listener);
	}

	public JTextField getTextFieldNumRowCol()
	{
		return textFieldNumRowCol;
	}

	public void setTextFieldNumRowCol(JTextField textFieldNumRowCol)
	{
		this.textFieldNumRowCol = textFieldNumRowCol;
	}
	
	

}
