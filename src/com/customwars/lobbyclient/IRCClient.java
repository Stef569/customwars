package com.customwars.lobbyclient;
import javax.swing.JScrollBar;

import com.customwars.pbot.*;

public class IRCClient extends PircBot
{
	BigFrame chub;
	public IRCClient(BigFrame runner)
	{
		chub = runner;
	}
	public void init(String username, String hostname, String channel)
	{
		System.out.println("U:" + username);
		System.out.println("H:" + hostname);
		System.out.println("C:" + channel);
		this.setName(username);
		setVerbose(true);
        try
        {
        // Connect to the IRC server.
        	connect(hostname);
        }
        catch(Exception e)
        {
        	System.out.println(e);
        }
        // Join the #pircbot channel.
        joinChannel(channel);
	}
	protected void onMessage(String channel, String sender, String login, String hostname, String message)
	{
		chub.cpanel.handleMessage(message, sender);
		
		chub.cpanel.chatview.repaint();
	}
	protected void onConnect()
	{
		//chub.frame.panel.updateUsers();
		this.sendRawLine("LIST #customwars");
		this.sendRawLine("nickserv identify " + chub.password);
	}
	protected void onUserList(String channel, User[] users)
	{
		chub.cpanel.updateUsers(users);
	}
	protected void onPart(String channel, String sender, String login, String hostname)
	{
		//getUserList();
		//chub.cpanel.updateUsers();
		JScrollBar vbar = chub.cpanel.chatviewpane.getVerticalScrollBar();
		boolean autoScroll = ((vbar.getValue() + vbar.getVisibleAmount()) == vbar.getMaximum());
		
		chub.cpanel.chatview.append("User " + sender + " left the channel.\n");
		
		if( autoScroll ) chub.cpanel.chatview.setCaretPosition( chub.cpanel.chatview.getDocument().getLength() );	
		
		this.sendRawLine("NAMES #customwars");
	}
	protected void onJoin(String channel, String sender, String login, String hostname)
	{
		JScrollBar vbar = chub.cpanel.chatviewpane.getVerticalScrollBar();
		boolean autoScroll = ((vbar.getValue() + vbar.getVisibleAmount()) == vbar.getMaximum());
		
		chub.cpanel.chatview.append("User " + sender + " joined the channel.\n");
		if( autoScroll ) chub.cpanel.chatview.setCaretPosition( chub.cpanel.chatview.getDocument().getLength() );
		this.sendRawLine("NAMES #customwars");
	}
	protected void onNickChange(String oldNick, String login, String hostname, String newNick)
	{
		JScrollBar vbar = chub.cpanel.chatviewpane.getVerticalScrollBar();
		boolean autoScroll = ((vbar.getValue() + vbar.getVisibleAmount()) == vbar.getMaximum());
		
		chub.cpanel.chatview.append("User " + oldNick + " changed name to " + newNick + "\n");
		if( autoScroll ) chub.cpanel.chatview.setCaretPosition( chub.cpanel.chatview.getDocument().getLength() );
		this.sendRawLine("NAMES #customwars");
	}
	protected void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason)
	{
		JScrollBar vbar = chub.cpanel.chatviewpane.getVerticalScrollBar();
		boolean autoScroll = ((vbar.getValue() + vbar.getVisibleAmount()) == vbar.getMaximum());
		
		chub.cpanel.chatview.append("User " + recipientNick + " was kicked by " + kickerNick + ". Reason: " + reason + "\n");
		if( autoScroll ) chub.cpanel.chatview.setCaretPosition( chub.cpanel.chatview.getDocument().getLength() );
		this.sendRawLine("NAMES #customwars");
	}
	protected void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason)
	{
		JScrollBar vbar = chub.cpanel.chatviewpane.getVerticalScrollBar();
		boolean autoScroll = ((vbar.getValue() + vbar.getVisibleAmount()) == vbar.getMaximum());
		if( autoScroll ) chub.cpanel.chatview.setCaretPosition( chub.cpanel.chatview.getDocument().getLength() );
		this.sendRawLine("NAMES #customwars");
	}
    protected void onPrivateMessage(String sender, String login, String hostname, String message)
    {
		JScrollBar vbar = chub.cpanel.chatviewpane.getVerticalScrollBar();
		boolean autoScroll = ((vbar.getValue() + vbar.getVisibleAmount()) == vbar.getMaximum());
		
		chub.cpanel.chatview.append("[WHISPER][" + sender + "]: " + message + "\n");
		if( autoScroll ) chub.cpanel.chatview.setCaretPosition( chub.cpanel.chatview.getDocument().getLength() );
		chub.cpanel.chatview.repaint();
    }
    protected void onServerResponse(int code, String response) 
    {
    	if(code == 332)
    	{
    		JScrollBar vbar = chub.cpanel.chatviewpane.getVerticalScrollBar();
    		boolean autoScroll = ((vbar.getValue() + vbar.getVisibleAmount()) == vbar.getMaximum());
    		
    		chub.cpanel.chatview.append("MOTD: " + response + "\n");
    		if( autoScroll ) chub.cpanel.chatview.setCaretPosition( chub.cpanel.chatview.getDocument().getLength() );
    	}
    	
    }
}
