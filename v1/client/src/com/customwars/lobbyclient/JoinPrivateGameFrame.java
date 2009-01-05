package com.customwars.lobbyclient;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.event.*;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
public class JoinPrivateGameFrame extends JFrame implements ActionListener
{
	protected JPanel panel;
	protected JButton ok;
	protected JPasswordField passwordfield;
	protected JTextField slotnumber;
	protected BigFrame runner;
	protected String gamename;
	
	public JoinPrivateGameFrame(BigFrame runnerin, String gamenamein)
	{
		this.setTitle("Join Private Game");
		//super("Select Server");
		gamename = gamenamein;
		runner = runnerin;
		passwordfield = new JPasswordField();
		slotnumber = new JTextField("2");
		ok = new JButton("Ok");
		ok.addActionListener(this);
		panel = new JPanel();
		//panel.setPreferredSize(new Dimension(300,300));
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		panel.add(new JLabel("Private Game Password"));
		panel.add(passwordfield);
		panel.add(new JLabel("Slot Number"));
		panel.add(slotnumber);
		panel.add(ok);
		this.setContentPane(panel);
		this.pack();
		int x = this.getWidth() / 2;
		int y = this.getHeight() / 2;
		Toolkit a = java.awt.Toolkit.getDefaultToolkit();
		this.setLocation((int)(a.getScreenSize().getWidth() / 2 - x),(int)(a.getScreenSize().getHeight() / 2 - y));
		//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == ok)
		{
			String temp = new String(passwordfield.getPassword());
			if(!temp.equalsIgnoreCase("") && temp != null)
			{
					this.setVisible(false);
					runner.setVisible(false);
					runner.cpanel.irc.disconnect();
					//runner.init(gamename.getText(), temp2, gamename.getText(), channel.getText(), gameserver.getText());
					FobbahLauncher.launch();
					FobbahLauncher.JoinGame(gamename, new String(passwordfield.getPassword()), runner.username, runner.password, Integer.parseInt(slotnumber.getText()));

					

				//runner.init(username.getText(), hostname.getText(), channel.getText(), gameserver.getText());
				//this.setVisible(false);
			}
			else
			{
				JFrame errorbox = new JFrame("Warning");
				errorbox.setContentPane(new JLabel("Please Enter a Password for Private Game"));
				errorbox.setSize(new Dimension(200,200));
				Toolkit a = java.awt.Toolkit.getDefaultToolkit();
				errorbox.setLocation((int)(a.getScreenSize().getWidth() / 2 - 100),(int)(a.getScreenSize().getHeight() / 2 - 100));
				errorbox.setVisible(true);
			}
			
			
		}
	}
}
