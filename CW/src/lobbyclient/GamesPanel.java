package lobbyclient;
import java.awt.Dimension;
import javax.swing.*;
import java.util.LinkedList;
import java.awt.*;
import javax.swing.table.*;
public class GamesPanel extends JTabbedPane
{
	//JTabbedPane tabpane = new JTabbedPane();
	
	JTable opengames;
	JTable passwordgames;
	JTable fullgames;
	
	public GamesPanel(LinkedList open, LinkedList passworded, LinkedList full)
	{
		System.out.println("BLARGH");
		initiate(open, passworded, full);
		this.add(opengames);
		opengames.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane opengamespanel = new JScrollPane(opengames);
		opengamespanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		passwordgames.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane passwordgamespanel = new JScrollPane(passwordgames);
		passwordgamespanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		fullgames.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane fullgamespanel = new JScrollPane(fullgames);
		fullgamespanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);


		this.addTab("Open Games",opengamespanel);
		this.addTab("Passworded Games",passwordgamespanel);
		this.addTab("Full Games",fullgamespanel);
		

	}
	public void setWidths(JTable table)
	{
		table.getColumnModel().getColumn(0).setPreferredWidth(170);
		table.getColumnModel().getColumn(1).setPreferredWidth(50);
		table.getColumnModel().getColumn(2).setPreferredWidth(50);
		table.getColumnModel().getColumn(3).setPreferredWidth(140);
		table.getColumnModel().getColumn(4).setPreferredWidth(140);
		table.getColumnModel().getColumn(5).setPreferredWidth(180);
		table.getColumnModel().getColumn(6).setPreferredWidth(180);
		table.getColumnModel().getColumn(7).setPreferredWidth(400);
		
		/*
		for(int ix = 0; ix < table.getColumnCount(); ix++);
		{
			for(int iy = 0; iy < table.getRowCount(); iy++);
			{
				table.getCellEditor()
			}
		}*/

//		JTextArea area = table.getEditorComponent();
//		area.setEditable(false);
	}
	
	public void initiate(LinkedList open, LinkedList passworded, LinkedList full)
	{
		String[][] opendata = new String[open.size()][8];
		String[][] pwdata = new String[passworded.size()][8];
		String[][] fulldata = new String[full.size()][8];
		//String[] columnnames = new String[8];
		
		String[] columnnames = {"Name",
								"Players",
								"Turn",
								"Map Name",
								"Version",	
								"Player Names",
								"Last Action",
								"Comment"};
		
		filldata(opendata, open);
		filldata(pwdata, passworded);
		filldata(fulldata, full);
		
		DefaultTableModel openmodel = new DefaultTableModel(open.size(),8)
		{
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		openmodel.setDataVector(opendata, columnnames);
		opengames = new JTable(openmodel);
		setWidths(opengames);
		DefaultTableModel passmodel = new DefaultTableModel(open.size(),8)
		{
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		passmodel.setDataVector(pwdata, columnnames);
		passwordgames = new JTable(passmodel);
		setWidths(passwordgames);
		
		DefaultTableModel fullmodel = new DefaultTableModel(open.size(),8)
		{
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		fullmodel.setDataVector(fulldata, columnnames);
		fullgames = new JTable(fullmodel);
		setWidths(fullgames);
	}
	
	public void filldata(String[][] data, LinkedList input)
	{
		for(int i = 0; i < input.size(); i++)
		{
			Game temp = (Game)(input.get(i));
			data[i][0] = temp.gamename;
			data[i][1] = "" + temp.numplayers + "/" + temp.maxplayers;
			data[i][2] = "" + temp.turnnumber + "/" + temp.maxturns;
			data[i][3] = temp.mapname;
			data[i][4] = temp.version;
			String temp1 = "";
			for(int i2 = 0; i2 < temp.playerlist.size(); i2++)
			{
				temp1 = temp1 + temp.playerlist.get(i2) + " ";
			}
			data[i][5] = temp1;
			data[i][6] = temp.lastaction;
			data[i][7] = temp.comment;
		}

	}
}
