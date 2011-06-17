package me.Razish.Razmod;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

public class RazmodServerListener extends ServerListener
{
	public static Razmod plugin;

	public RazmodServerListener( Razmod instance )
	{
		plugin = instance;
	}

	public void onPluginEnable( PluginEnableEvent e )
	{
		if ( e.getPlugin().getDescription().getName().equalsIgnoreCase( "Permissions" ) )
			plugin.permissionsEnabled = true;
	}
	public void onPluginDisable( PluginDisableEvent e )
	{
		if ( e.getPlugin().getDescription().getName().equalsIgnoreCase( "Permissions" ) )
			plugin.permissionsEnabled = false;
	}
}
