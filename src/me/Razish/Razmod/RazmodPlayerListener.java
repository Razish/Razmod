package me.Razish.Razmod;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class RazmodPlayerListener extends PlayerListener
{
	public static Razmod plugin;

	public RazmodPlayerListener( Razmod instance )
	{//Constructor
		plugin = instance;
	}

	public void onPlayerJoin( PlayerJoinEvent e )
	{//PLAYER_JOIN event handler
		Player ply = e.getPlayer();
		e.setJoinMessage( null );

		plugin.getServer().broadcastMessage( ChatColor.GOLD.toString() + ply.getDisplayName() + ChatColor.DARK_GREEN.toString() + " joined on world " + ply.getWorld().getName() );

		if ( plugin.getConfigBool( "welcome.showmotd" ) == true )
			ply.sendMessage( plugin.getConfigString( "welcome.motd" ) );

		//TODO: Check Permissions for group name
		if ( ply.isOp() )
			ply.sendMessage( ChatColor.AQUA.toString() + "You have operator privileges" );

		//Print a list of connected players
		Player[] players = plugin.getServer().getOnlinePlayers();
		String message = ChatColor.GRAY.toString() + Integer.toString( players.length ) + ((players.length > 1) ? " players are connected: " : " player is connected: ") + players[0].getDisplayName();
		for ( int i=1; i<players.length; i++ )
			message += ", " + players[i].getDisplayName();
		ply.sendMessage( message );
	}

	public void onPlayerQuit( PlayerQuitEvent e )
	{//PLAYER_QUIT event handler
		Player ply = e.getPlayer();
		e.setQuitMessage( null );

		plugin.getServer().broadcastMessage( ChatColor.GREEN.toString() + ply.getDisplayName() + ChatColor.GRAY.toString() + " left" );
	}

	public void onPlayerPortal( PlayerPortalEvent e )
	{//PLAYER_PORTAL event handler
		if ( !(e.getTo().getWorld().getName().equals( e.getFrom().getWorld().getName() )) )
			plugin.getServer().broadcastMessage( ChatColor.AQUA.toString() + e.getPlayer().getDisplayName() + " is portaling to: " + e.getTo().getWorld().getName() /*+ " from " + e.getFrom().getWorld().getName()*/ );
	}
}
