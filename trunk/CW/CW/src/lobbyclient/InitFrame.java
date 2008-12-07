package lobbyclient;
import cwsource.Options;
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
public class InitFrame extends JFrame implements ActionListener
{
	protected JPanel panel;
	protected JButton ok;
	protected JTextField hostname;
	protected JTextField username;
	protected JTextField channel;
	protected JTextField gameserver;
	protected JPasswordField password;
	protected BigFrame runner;
	public InitFrame(BigFrame runnerin)
	{
		//super("Select Server");
		runner = runnerin;
		hostname = new JTextField("irc.moo.vc");
		username = new JTextField("Username");
                password = new JPasswordField();
                if(Options.isDefaultLoginOn())
                {
		username = new JTextField(Options.getDefaultUsername());
                password = new JPasswordField(Options.getDefaultPassword());
                }
                
		channel = new JTextField("#customwars");
		gameserver = new JTextField("http://battle.customwars.com/list.pl");
		
		ok = new JButton("Ok");
		ok.addActionListener(this);
		panel = new JPanel();
		//panel.setPreferredSize(new Dimension(300,300));
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(new JLabel("Username"));
		panel.add(username);
		panel.add(new JLabel("Password"));
		panel.add(password);
		panel.add(new JLabel("Hostname"));
		panel.add(hostname);
		panel.add(new JLabel("Channel"));
		panel.add(channel);
		panel.add(new JLabel("Game Server"));
		panel.add(gameserver);
		panel.add(ok);
		this.setContentPane(panel);
		this.pack();
		int x = this.getWidth() / 2;
		int y = this.getHeight() / 2;
		Toolkit a = java.awt.Toolkit.getDefaultToolkit();
		this.setLocation((int)(a.getScreenSize().getWidth() / 2 - x),(int)(a.getScreenSize().getHeight() / 2 - y));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == ok)
		{
			String temp = username.getText();
			if(!temp.equalsIgnoreCase("Username"))
			{
				String temp2 = new String(password.getPassword());
				if(!temp2.equalsIgnoreCase("") && temp2 != null)
				{
					runner.init(username.getText(), temp2, hostname.getText(), channel.getText(), gameserver.getText());
					this.setVisible(false);
				}
				else
				{
					JFrame errorbox = new JFrame("Warning");
					errorbox.setContentPane(new JLabel("Please Enter a Password"));
					errorbox.setSize(new Dimension(200,200));
					Toolkit a = java.awt.Toolkit.getDefaultToolkit();
					errorbox.setLocation((int)(a.getScreenSize().getWidth() / 2 - 100),(int)(a.getScreenSize().getHeight() / 2 - 100));
					errorbox.setVisible(true);
				}
				//runner.init(username.getText(), hostname.getText(), channel.getText(), gameserver.getText());
				//this.setVisible(false);
			}
			else
			{
				JFrame errorbox = new JFrame("Warning");
				errorbox.setContentPane(new JLabel("Please Enter a Username"));
				errorbox.setSize(new Dimension(200,200));
				Toolkit a = java.awt.Toolkit.getDefaultToolkit();
				errorbox.setLocation((int)(a.getScreenSize().getWidth() / 2 - 100),(int)(a.getScreenSize().getHeight() / 2 - 100));
				errorbox.setVisible(true);
			}
			
			
		}
	}
}
