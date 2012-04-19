package fr.crafter.tickleman.realvotes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;

import org.bukkit.World;
import org.bukkit.entity.Player;

public class Utils
{

	//-------------------------------------------------------------------------------- broadcastWorld
	public static void broadcastWorld(World world, String message)
	{
		for (Player toPlayer : world.getPlayers()) {
			toPlayer.sendMessage(message);
		}
	}

	//-------------------------------------------------------------------------------- broadcastWorld
	public static void broadcastWorld(Player player, String message)
	{
		broadcastWorld(player.getWorld(), message);
	}

	//---------------------------------------------------------------------------------- fileToString
	public static String fileToString(String filename) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		StringBuilder builder = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			builder.append(line + "\n");
		}
		return builder.toString().replace("\r", "");
	}

	//------------------------------------------------------------------------------------------ join
	public static String join(String glue, Collection<String> collection)
	{
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (String element : collection) {
			if (!element.isEmpty()) {
				if (first) first = false; else builder.append(", "); 
				builder.append(element);
			}
		}
		return builder.toString();
	}

}
