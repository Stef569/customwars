package lobbyclient;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.*;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.*;

public class BigFrame extends JFrame implements ActionListener {
	ClientPanel cpanel;

	String channel;

	String hostname;

	String username;

	String gameserver;
	
	String password;

	JPanel buttonpane;

	JButton create = new JButton("Create Game");

	JButton join = new JButton("Join Game");

	JButton login = new JButton("Login to Game");

	GamesPanel gpanel;

	public String getChannel() {
		return channel;
	}

	public String getHost() {
		return hostname;
	}

	public String getUsername() {
		return username;
	}

	public String gameServer() {
		return gameserver;
	}

	public BigFrame() {
		InitFrame bah = new InitFrame(this);
	}

	public void init(String usernamein, String passwordin, String hostnamein, String channelin,
			String gameserverin) {
		username = usernamein;
		hostname = hostnamein;
		channel = channelin;
		gameserver = gameserverin;
		password = passwordin;
		// Downloader dl = new Downloader();
		FileDownload.download(gameServer(), "gamelist.dat");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		parser parse = new parser();

		IRCClient irc = new IRCClient(this);
		irc.init(getUsername(), getHost(), getChannel());

		cpanel = new ClientPanel(irc, this);
		JFrame clientframe = new JFrame();

		buttonpane = new JPanel();
		buttonpane.setLayout(new BoxLayout(buttonpane, BoxLayout.LINE_AXIS));
		buttonpane.add(new JSeparator(SwingConstants.VERTICAL));
		buttonpane.add(create);
		create.addActionListener(this);
		buttonpane.add(Box.createHorizontalStrut(5));
		buttonpane.add(join);
		join.addActionListener(this);
		buttonpane.add(Box.createHorizontalStrut(5));
		buttonpane.add(login);
		login.addActionListener(this);
		join.addActionListener(this);

		// clientframe.setContentPane(cpanel);
		// clientframe.pack();
		// clientframe.setVisible(true);
		JPanel bpanelmain = new JPanel(new BorderLayout());
		gpanel = new GamesPanel(parse.opengames, parse.passwordgames,
				parse.fullgames);
		bpanelmain.add(cpanel, BorderLayout.CENTER);
		bpanelmain.add(buttonpane, BorderLayout.PAGE_END);
		this.setResizable(false);
		this.setSize(new Dimension(730, 510));
		this.setTitle("Customwars Lobby");
		int x = (int) ((Toolkit.getDefaultToolkit().getScreenSize().getWidth() - this
				.getWidth()) / 2);
		int y = (int) ((Toolkit.getDefaultToolkit().getScreenSize().getHeight() - this
				.getHeight()) / 2);
		JSplitPane splitpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				gpanel, bpanelmain);
		splitpane.setDividerLocation(200);
		splitpane.setEnabled(false);
		this.setContentPane(splitpane);
		this.setLocation(x, y);
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == create) {
			CreateGameFrame createframe = new CreateGameFrame(this);
			/*
			this.setVisible(false);
			cpanel.irc.disconnect();
			
			FobbahLauncher.launch();
			FobbahLauncher.CreateGame();
			*/
		}
		if (e.getSource() == join)
		{
			int selectedpane = gpanel.getSelectedIndex();
			System.out.println("Bah:" + selectedpane);
			JTable opentab;
			if (selectedpane == 0) 
			{
				opentab = gpanel.opengames;
			}
			else if(selectedpane == 1)
			{
				opentab = gpanel.passwordgames;
			}
			else
			{
				opentab = gpanel.fullgames;
			}
			
			int i = opentab.getSelectedRow();
			System.out.println("i:" + i);
			if(i == -1)
			{
				return;
			}
			else
			{
				if(selectedpane == 1)
				{
					JoinPrivateGameFrame gframe = new JoinPrivateGameFrame(this, (String)opentab.getValueAt(opentab.getSelectedRow(), opentab.getSelectedColumn()));
				}
				else
				{
					this.setVisible(false);
					cpanel.irc.disconnect();
					FobbahLauncher.launch();
					FobbahLauncher.JoinGame((String)opentab.getValueAt(opentab.getSelectedRow(), opentab.getSelectedColumn()), "", username, password, 2);
				}
				//String gamename = (String)opentab.getValueAt(opentab.getSelectedRow(), opentab.getSelectedColumn());
				//FobbahLauncher.launch();
				//FobbahLauncher.LoginGame(gamename, username, password);
				//cpanel.	irc.disconnect();
				//this.setVisible(false);
			}
		}
		if (e.getSource() == login) {
			int selectedpane = gpanel.getSelectedIndex();
			System.out.println("Bah:" + selectedpane);
			JTable opentab;
			if (selectedpane == 0) 
			{
				opentab = gpanel.opengames;
			}
			else if(selectedpane == 1)
			{
				opentab = gpanel.passwordgames;
			}
			else
			{
				opentab = gpanel.fullgames;
			}
			
			int i = opentab.getSelectedRow();
			System.out.println("i:" + i);
			if(i == -1)
			{
				return;
			}
			else
			{
				String gamename = (String)opentab.getValueAt(opentab.getSelectedRow(), opentab.getSelectedColumn());
				FobbahLauncher.launch();
				FobbahLauncher.LoginGame(gamename, username, password);
				cpanel.irc.disconnect();
				this.setVisible(false);
			}
			
		}
	}
}
