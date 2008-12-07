package lobbyclient;
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
public class CreateGameFrame extends JFrame implements ActionListener
{
	protected JPanel panel;
	protected JButton ok;
	protected JTextField gamename;
	protected JPasswordField password;
	protected BigFrame runner;
	public CreateGameFrame(BigFrame runnerin)
	{
		//super("Select Server");
		this.setTitle("Create Game");
		runner = runnerin;
		gamename = new JTextField();
		password = new JPasswordField();
		ok = new JButton("Ok");
		ok.addActionListener(this);
		panel = new JPanel();
		//panel.setPreferredSize(new Dimension(300,300));
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(new JLabel("Game Name"));
		panel.add(gamename);
		panel.add(new JLabel("Password (If Playing Private Game)"));
		panel.add(password);
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
			String temp = gamename.getText();
			if(!temp.equalsIgnoreCase("") && temp != null)
			{
					this.setVisible(false);
					runner.setVisible(false);
					runner.cpanel.irc.disconnect();
					//runner.init(gamename.getText(), temp2, gamename.getText(), channel.getText(), gameserver.getText());
					FobbahLauncher.launch();
					FobbahLauncher.CreateGame(runner.username, runner.password, temp, new String(password.getPassword()));

					

				//runner.init(username.getText(), hostname.getText(), channel.getText(), gameserver.getText());
				//this.setVisible(false);
			}
			else
			{
				JFrame errorbox = new JFrame("Warning");
				errorbox.setContentPane(new JLabel("Please Enter a Game Name"));
				errorbox.setSize(new Dimension(200,200));
				Toolkit a = java.awt.Toolkit.getDefaultToolkit();
				errorbox.setLocation((int)(a.getScreenSize().getWidth() / 2 - 100),(int)(a.getScreenSize().getHeight() / 2 - 100));
				errorbox.setVisible(true);
			}
			
			
		}
	}
}
