package me.Razish.Razmod;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.ItemStack;

public class RazmodBlockListener extends BlockListener
{
	public static Razmod plugin;

	public RazmodBlockListener( Razmod instance )
	{//Constructor
		plugin = instance;
	}

	public void onBlockBreak( BlockBreakEvent e )
	{
		Block b = e.getBlock();
		Material m = b.getType();
		Player ply = e.getPlayer();
		if ( m == Material.COBBLESTONE_STAIRS || m == Material.WOOD_STAIRS )
		{//If stairs break, drop new stairs!
			e.setCancelled( true );
			b.getWorld().dropItemNaturally( b.getLocation(), new ItemStack( m, 1 ) );
			b.setType( Material.AIR );
		}
		if ( plugin.getConfigBool( "cart.spawnallow" ) && m == plugin.getConfigMaterial( "cart.spawnblock" ) && plugin.hasPermissions( ply, "razmod.cart.spawner" ) )
		{
		}
	}

	public void onBlockRedstoneChange( BlockRedstoneEvent e )
	{
		Block thisBlock = e.getBlock();
		Material spawnerMaterial = plugin.getConfigMaterial( "cart.spawnblock" );
		boolean canSpawn = plugin.getConfigBool( "cart.spawnallow" );
		if ( thisBlock.getType() == Material.STONE_BUTTON && e.getNewCurrent() == 1 )
		{
			Block downBlock = thisBlock.getFace( BlockFace.DOWN );
			if ( canSpawn && 
				 (thisBlock.getFace( BlockFace.NORTH ).getType() == spawnerMaterial || thisBlock.getFace( BlockFace.EAST ).getType() == spawnerMaterial ||
				 thisBlock.getFace( BlockFace.SOUTH ).getType() == spawnerMaterial || thisBlock.getFace( BlockFace.WEST ).getType() == spawnerMaterial) )
			{//MINECART SPAWNER
				if ( downBlock.getType() == Material.RAILS || downBlock.getType() == Material.POWERED_RAIL )
				{
					Location loc = downBlock.getLocation();
					if ( downBlock.getFace( BlockFace.NORTH ).getType() == Material.RAILS || downBlock.getFace( BlockFace.NORTH ).getType() == Material.POWERED_RAIL ) {
						loc.setX( loc.getX() + 0.5 ); //go south
					}
					else if ( downBlock.getFace( BlockFace.EAST ).getType() == Material.RAILS || downBlock.getFace( BlockFace.EAST ).getType() == Material.POWERED_RAIL ) {
						loc.setZ( loc.getZ() + 0.5 ); //go west
					}
					else if ( downBlock.getFace( BlockFace.SOUTH ).getType() == Material.RAILS || downBlock.getFace( BlockFace.SOUTH ).getType() == Material.POWERED_RAIL ) {
						loc.setX( loc.getX() + 0.5 ); //go north
					}
					else if ( downBlock.getFace( BlockFace.WEST ).getType() == Material.RAILS || downBlock.getFace( BlockFace.WEST ).getType() == Material.POWERED_RAIL ) {
						loc.setZ( loc.getZ() + 0.5 ); //go east
					}
					/*Minecart cart =*/ downBlock.getWorld().spawnMinecart( loc );
				//	cart.setMaxSpeed( 3.0 );
					e.setNewCurrent( 0 );
				}
			}
		}
	}

	//Tables by Demonen
	public void onBlockCanBuild( BlockCanBuildEvent e )
	{
		if ( plugin.place.contains( e.getMaterial() ) )
		{
			Block thisBlock = e.getBlock();
			if ( thisBlock.getType().equals( Material.AIR ) && plugin.onTopOf.contains( thisBlock.getFace( BlockFace.DOWN ).getType() ) )
					e.setBuildable( true );
		}
	}
	public void onBlockPhysics( BlockPhysicsEvent e )
	{
		Block affected = e.getBlock();
		if ( plugin.place.contains( affected.getType() ) && plugin.onTopOf.contains( affected.getFace( BlockFace.DOWN ).getType() ) )
				e.setCancelled( true );
	}
}
