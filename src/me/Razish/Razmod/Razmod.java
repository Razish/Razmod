package me.Razish.Razmod;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Logger;

/*
import net.minecraft.server.EntityFireball;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.MathHelper;
import net.minecraft.server.Vec3D;
import net.minecraft.server.World;
*/

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
//import org.bukkit.craftbukkit.CraftWorld;
//import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.server.ServerListener;
import org.bukkit.event.vehicle.VehicleListener;
import org.bukkit.event.weather.WeatherListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
//import org.bukkit.util.Vector;

import com.nijikokun.bukkit.Permissions.Permissions;

public class Razmod extends JavaPlugin
{
	private PlayerListener playerListener;
	private BlockListener blockListener;
	private VehicleListener vehicleListener;
	private WeatherListener weatherListener;
	private ServerListener serverListener;
	private PluginManager pm;
	
	//Tables by Demonen - https://github.com/DemmyDemon/Tables/
	public HashSet<Material> place = new HashSet<Material>();
	public HashSet<Material> onTopOf = new HashSet<Material>();

	public Logger log;
	boolean permissionsEnabled = false;
	
	//CONFIGURATION start (thanks Pandarr for this awesome method =3)
	private static final Map<String, Object> CONFIG_DEFAULTS = new HashMap<String, Object>();
	private Configuration myConfig;
	static {
		CONFIG_DEFAULTS.put( "welcome.motd", "This is the default Razmod MotD, change it in config.yml!" );
		CONFIG_DEFAULTS.put( "welcome.showmotd", true );
		CONFIG_DEFAULTS.put( "welcome.showworld", true );
		CONFIG_DEFAULTS.put( "cart.spawnblock", Material.SPONGE.toString() );
		CONFIG_DEFAULTS.put( "cart.spawnallow", true );
		CONFIG_DEFAULTS.put( "portal.notify", true );
		CONFIG_DEFAULTS.put( "weather.disable", true );
	}

	private void loadConfig()
	{
		this.getDataFolder().mkdir();//hack? lol, works..
		File configFile = new File( this.getDataFolder(), "config.yml" );
		if ( configFile.exists() )
		{
			myConfig = new Configuration( configFile );
			myConfig.setHeader( "# Configuration file for Razmod v"+this.getDescription().getVersion()+"\r\n" );
			myConfig.load();
			for ( String prop : CONFIG_DEFAULTS.keySet() )
			{
				if ( myConfig.getProperty( prop ) == null )
					myConfig.setProperty( prop, CONFIG_DEFAULTS.get( prop ) );
			}
		}
		else
		{
			try {
				configFile.createNewFile();
				myConfig = new Configuration( configFile );
				// default values
				myConfig.setHeader( "# Configuration file for Razmod v"+this.getDescription().getVersion()+"\r\n" );
				for ( String prop : CONFIG_DEFAULTS.keySet() ) {
					myConfig.setProperty( prop, CONFIG_DEFAULTS.get( prop ) );
				}
				myConfig.save();
			} catch ( IOException e ) {
				log.warning( e.toString() );
			}
		}
	}
	public int getConfigInt( final String path ) {
		return myConfig.getInt( path, (Integer)CONFIG_DEFAULTS.get( path ) );
	}
	public Boolean getConfigBool( final String path ) {
		return myConfig.getBoolean( path, (Boolean)CONFIG_DEFAULTS.get( path ) );
	}
	public String getConfigString( final String path ) {
		return myConfig.getString( path, (String)CONFIG_DEFAULTS.get( path ) );
	}
	public Material getConfigMaterial( final String path ) {
		return Material.getMaterial( getConfigString( path ) );
	}
	//CONFIGURATION end

	public void onLoad()
	{
		log = Logger.getLogger( "Minecraft" );
		pm = this.getServer().getPluginManager();
		playerListener = new RazmodPlayerListener( this );
		blockListener = new RazmodBlockListener( this );
		vehicleListener = new RazmodVehicleListener( this );
		weatherListener = new RazmodWeatherListener( this );
		serverListener = new RazmodServerListener( this );
	}

	public void onEnable()
	{
		permissionsEnabled = pm.isPluginEnabled( "Permissions" );
		loadConfig();
		pm.registerEvent( Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Normal, this );
		pm.registerEvent( Event.Type.PLAYER_QUIT, playerListener, Event.Priority.Normal, this );
		pm.registerEvent( Event.Type.PLAYER_KICK, playerListener, Event.Priority.Normal, this );
		pm.registerEvent( Event.Type.PLAYER_PORTAL, playerListener, Event.Priority.Normal, this );

		pm.registerEvent( Event.Type.BLOCK_BREAK, blockListener, Event.Priority.Normal, this );
		pm.registerEvent( Event.Type.REDSTONE_CHANGE, blockListener, Event.Priority.Normal, this );
		//Tables by Demonen
		pm.registerEvent( Event.Type.BLOCK_CANBUILD, blockListener, Event.Priority.Normal, this );
		pm.registerEvent( Event.Type.BLOCK_PHYSICS, blockListener, Event.Priority.Normal, this );
		
		pm.registerEvent( Event.Type.VEHICLE_DESTROY, vehicleListener, Event.Priority.Normal, this );
		
		pm.registerEvent( Event.Type.WEATHER_CHANGE, weatherListener, Event.Priority.Normal, this );
		
		pm.registerEvent( Event.Type.PLUGIN_ENABLE, serverListener, Event.Priority.Normal, this );
		pm.registerEvent( Event.Type.PLUGIN_DISABLE, serverListener, Event.Priority.Normal, this );
		
		//Tables by Demonen
		place.clear();
		place.add( Material.WOOD_PLATE );
		place.add( Material.STONE_PLATE );
		place.add( Material.RAILS );
		place.add( Material.POWERED_RAIL );
		onTopOf.clear();
		onTopOf.add( Material.FENCE );
		onTopOf.add( Material.GLASS );
		onTopOf.add( Material.WOOD_STAIRS );
		onTopOf.add( Material.COBBLESTONE_STAIRS );
		onTopOf.add( Material.STEP );

		log.info( "[Razmod]: Hello world! Version " + this.getDescription().getVersion() );
		if ( permissionsEnabled )
			log.info( "[Razmod]: Permissions plugin detected, version " + Permissions.instance.getDescription().getVersion() );
	}

	public void onDisable()
	{
		myConfig.save();
		log.info( "[Razmod]: Goodbye world! Version " + this.getDescription().getVersion() );
	}

	public static Player asPlayer( CommandSender commandSender)
	{//Return a Player from a CommandSender
		if ( !(commandSender instanceof Player) )
			return null;
        
        return (Player)commandSender;
	}
	
	public boolean hasPermissions( Player ply, String permissionNode )
	{
		return ( ply.isOp() || (permissionsEnabled && Permissions.Security.has( ply, permissionNode )) );
	}
	
	public boolean onCommand( CommandSender sender, Command cmd, String commandLabel, String[] args )
	{
		/*
		if ( cmd.getName().equalsIgnoreCase( "fb" ) )
		{
			Player ply = asPlayer( sender );
			World world = (World) (((CraftWorld)ply.getLocation().getWorld()).getHandle());
			EntityFireball localEntityFireball = new EntityFireball( world, (EntityLiving) ((CraftPlayer) ply ).getHandle(), 0.0, 0.0, 0.0 );
			float f1 = MathHelper.cos( -ply.getLocation().getYaw() * 0.01745329f - (float)Math.PI );
			float f2 = MathHelper.sin( -ply.getLocation().getYaw() * 0.01745329f - (float)Math.PI );
			float f3 = -MathHelper.cos( -ply.getLocation().getPitch() * 0.01745329f );
			float f4 = MathHelper.sin( -ply.getLocation().getPitch() * 0.01745329f );
			Vec3D localVec3D = Vec3D.create( f2 * f3, f4, f1 * f3 );
			localEntityFireball.locX = ( ply.getLocation().getX() + localVec3D.a * 4 );
			localEntityFireball.locY = ( ply.getLocation().getY() + 2 + 0.5 );
			localEntityFireball.locZ = ( ply.getLocation().getZ() + localVec3D.c * 4 );
			world.addEntity( localEntityFireball );

			return true;
		}
		*/

		if ( cmd.getName().equalsIgnoreCase( "players" ) )
		{//Print a list of connected players
			Player[] players = sender.getServer().getOnlinePlayers();

			if ( players.length == 0 )
			{//OOB and sender==console check
				sender.sendMessage( "There are no players online" );
				return true;
			}

			String message = ChatColor.GRAY.toString() + Integer.toString( players.length ) + ((players.length > 1) ? " players are connected: " : " player is connected: " ) + players[0].getDisplayName();
			for ( int i=1; i<players.length; i++ )
				message += ", " + players[i].getDisplayName();
			sender.sendMessage( message );
			return true;
		}

		if ( cmd.getName().equalsIgnoreCase( "wb" ) )
		{
			Player ply = asPlayer( sender );
			if ( ply == null )
				sender.sendMessage( "This command must be run as a player" );
			else if ( hasPermissions( ply, "razmod.workbench" ) )
				ply.getInventory().addItem( new ItemStack( Material.WORKBENCH, 1 ) );
			return true;
		}
		
		if ( cmd.getName().equalsIgnoreCase( "seed" ) )
		{//TODO: Send the seed of the specified world
			Player ply = asPlayer( sender );
			if ( ply == null )
			{//From the console
				if ( args.length != 1 )
				{
					sender.sendMessage( "Please specify a world to show the seed of" );
					return false;
				}
				else
				{
					World world = getServer().getWorld( args[0] );
					if ( world == null )
						sender.sendMessage( "World '" + args[0] + "' does not exist" );
					else
						sender.sendMessage( "Seed for world '" + world.getName() + "' is '" + world.getSeed() + "'" );
					return true;
				}
			}
			else
			{//From a player
				World world = (args.length==1) ? getServer().getWorld( args[0] ) : ply.getWorld();
				if ( world == null )
					ply.sendMessage( "World '" + args[0] + "' does not exist" );
				else
					ply.sendMessage( "Seed for world '" + world.getName() + "' is '" + world.getSeed() + "'" );
				return true;
			}
		}
		
		if ( cmd.getName().equalsIgnoreCase( "motd" ) )
		{
			if ( !getConfigBool( "welcome.showmotd" ) && sender.isOp() )
				sender.sendMessage( "MotD is currently disabled!" );
			sender.sendMessage( "MotD is '" + getConfigString( "welcome.motd" ) + "'" );
			return true;
		}
		
		if ( cmd.getName().equalsIgnoreCase( "setmotd" ) )
		{
			Player ply = asPlayer( sender );
			String motd;
			if ( args.length > 0 &&
					(sender.isOp() || (ply != null && permissionsEnabled && hasPermissions( ply, "razmod.setmotd" ) ))
				)
			{
				motd = args[0];
				for ( int i=1; i<args.length; i++ )
					motd += " " + args[i];
				myConfig.setProperty( "welcome.motd", motd );
				sender.sendMessage( "[Razmod] MotD is now '"+getConfigString( "welcome.motd" )+"'" );
				return true;
			}
			return false;
		}

		return false;
	}
}
