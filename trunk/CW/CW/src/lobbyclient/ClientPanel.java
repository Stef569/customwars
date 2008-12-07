package lobbyclient;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Timer;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.JScrollBar;
import java.awt.*;

import pbot.*;

import java.awt.event.*;


public class ClientPanel extends JPanel  implements ActionListener
{
	public BigFrame chub;
	protected IRCClient irc;
	protected JTextArea chatview;
	protected JTextField chatinput;
	protected JList clientsview;
	public boolean haslist = false;
	public User[] userlist;
	JScrollPane chatviewpane;
	public ClientPanel(IRCClient ircin, BigFrame chin)
	{
		chub = chin;
		irc = ircin;
		init();
	}
	public void init()
	{
		chatinput = new JTextField();
		chatinput.addActionListener(this);
		clientsview = new JList();
		
		chatview = new JTextArea();
		chatview.setEditable(false);
		chatview.setLineWrap(true);
		chatview.setWrapStyleWord(true);
		chatview.setAutoscrolls(true);
		chatviewpane = new JScrollPane(chatview);
		chatviewpane.setAutoscrolls(true);
		
		//this.setLayout(new GridBagLayout());
		//GridBagConstraints c = new GridBagConstraints();
		//irc = new IRCClient(chub);
		
		
		JPanel rightside = new JPanel(new BorderLayout());
		rightside.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		rightside.add(chatviewpane, BorderLayout.CENTER);
		rightside.add(chatinput, BorderLayout.PAGE_END);
		clientsview.setPreferredSize(new Dimension(150,235));
		rightside.setPreferredSize(new Dimension(550, 235));
		/*
		c.weighty = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;*/
		JSplitPane bottomhalf = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,clientsview, rightside);
		bottomhalf.setDividerLocation(150);
		this.add(bottomhalf, BorderLayout.CENTER);
	}
	public void updateUsers()
	{
		String[] users = new String[userlist.length];
		for(int i = 0 ; i < userlist.length; i++)
		{
			System.out.println("USR:" + users[i]);
			users[i] = userlist[i].getNick();
		}
		clientsview.setListData(users);
		clientsview.repaint();	
		haslist = true;
	}
	public void updateUsers(User[] usersin)
	{
		//if(haslist)
		//{
		User[] userlist = irc.getUsers(chub.getChannel());
		String[] users = new String[userlist.length];
		for(int i = 0 ; i < userlist.length; i++)
		{
			//System.out.println("USR:" + users[i]);
			if(userlist[i].isOp())
			{
				users[i] = "@" + userlist[i].getNick();
			}
			else
			{
				users[i] = userlist[i].getNick();
			}
		}
		clientsview.setListData(users);
		clientsview.repaint();
		//}
	}

	public void handleMessage(String message, String sender)
	{
//		 Determine whether the scrollbar is currently at the very bottom position.
		JScrollBar vbar = chatviewpane.getVerticalScrollBar();
		boolean autoScroll = ((vbar.getValue() + vbar.getVisibleAmount()) == vbar.getMaximum());
		
		char colours = message.charAt(0);
		if(colours == 3)
		{
			chatview.append("[" + sender + "]: " + message.substring(3) + "\n");
		}
		else
		{
			chatview.append("[" + sender + "]: " + message + "\n");
		}
//		 now scroll if we were already at the bottom.
		if( autoScroll ) chatview.setCaretPosition( chatview.getDocument().getLength() );	

	}
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == chatinput)
		{
			System.out.println("ch:" + chub.getChannel() + " :" + chatinput.getText());
			irc.sendMessage(chub.getChannel(),"" + chatinput.getText());
			
//			 Determine whether the scrollbar is currently at the very bottom position.
			JScrollBar vbar = chatviewpane.getVerticalScrollBar();
			boolean autoScroll = ((vbar.getValue() + vbar.getVisibleAmount()) == vbar.getMaximum());
			
			chatview.append("[" +  chub.getUsername() + "]: " + chatinput.getText() + "\n");
			
//			 now scroll if we were already at the bottom.
			if( autoScroll ) chatview.setCaretPosition( chatview.getDocument().getLength() );	
			
			chatview.repaint();
			chatinput.setText("");
		}
	}
	
	
}
