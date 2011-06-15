package me.Razish.Razmod;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleListener;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.ItemStack;

public class RazmodVehicleListener extends VehicleListener
{
	public static Razmod plugin;

	public RazmodVehicleListener( Razmod instance )
	{
		plugin = instance;
	}

	public void onVehicleDestroy( VehicleDestroyEvent event )
	{
		Vehicle v = event.getVehicle();
		Location loc = v.getLocation();
		if ( v instanceof Boat )
		{//If a boat breaks, drop a boat =]
			event.setCancelled( true );
			v.remove();
			v.getWorld().dropItemNaturally( loc, new ItemStack( Material.BOAT, 1 ) );
		}
	}
}
