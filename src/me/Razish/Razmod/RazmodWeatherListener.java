package me.Razish.Razmod;

import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.weather.WeatherListener;

public class RazmodWeatherListener extends WeatherListener
{
	public static Razmod plugin;

	public RazmodWeatherListener( Razmod instance )
	{
		plugin = instance;
	}

	public void onWeatherChange( WeatherChangeEvent e )
	{
		if ( plugin.getConfigBool( "weather.disable" ) && e.toWeatherState() )
			e.setCancelled( true );
	}
}
