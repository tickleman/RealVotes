package fr.crafter.tickleman.realvotes;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Vote
{

	private Integer answerCount = null;

	private Integer answerPercentage = null;

	private ChatColor color = null;

	private String  command;

	private static  Vote global = new Vote("vote", 51, 51, 15, 2, ChatColor.GOLD);

	private String  name;

	private String[] nobodyInWorlds = null;

	private String[] nobodyInRegions = null;

	private Integer reminders = null;

	private Integer time = null;

	private Integer yesCount = null;

	private Integer yesPercentage = null;

	//------------------------------------------------------------------------------------------ Vote
	public Vote(String name)
	{
		setName(name);
	}

	//------------------------------------------------------------------------------------------ Vote
	public Vote(
		String name, Integer answerPercentage, Integer yesPercentage, Integer time, Integer reminders,
		ChatColor color
	) {
		setAnswerPercentage(answerPercentage);
		setName(name);
		setNobodyInRegions(new String[]{});
		setNobodyInWorlds(new String[]{});
		setReminders(reminders);
		setTime(time);
		setYesPercentage(yesPercentage);
		setColor(color);
	}

	//-------------------------------------------------------------------------- checkNobodyInRegions
	public Boolean checkNobodyInRegions(Server server, World world)
	{
		if (getNobodyInRegions().length > 0) {
			RegionManager regionManager = ((WorldGuardPlugin)server.getPluginManager()
				.getPlugin("WorldGuard")).getRegionManager(world);
			for (String regionName : getNobodyInRegions()) {
				ProtectedRegion region = regionManager.getRegion(regionName);
				if (region != null) {
					Location location;
					for (Player player : world.getPlayers()) {
						location = player.getLocation();
						if (region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ())) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	//--------------------------------------------------------------------------- checkNobodyInWorlds
	public Boolean checkNobodyInWorlds(Server server)
	{
		if (getNobodyInWorlds().length > 0) {
			for (String worldName : getNobodyInWorlds()) {
				World world = server.getWorld(worldName);
				if (world != null) {
					if (world.getPlayers().size() > 0) {
						return false;
					}
				}
			}
		}
		return true;
	}

	//-------------------------------------------------------------------------------- getAnswerCount
	public Integer getAnswerCount()
	{
		return (answerCount == null) ? global.answerCount : answerCount;
	}

	//--------------------------------------------------------------------------- getAnswerPercentage
	public Integer getAnswerPercentage()
	{
		return (answerPercentage == null) ? global.answerPercentage : answerPercentage;
	}

	//-------------------------------------------------------------------------------------- getColor
	public ChatColor getColor()
	{
		return (color == null) ? global.color : color;
	}

	//------------------------------------------------------------------------------------ getCommand
	public String getCommand()
	{
		return command;
	}

	//------------------------------------------------------------------------------------ getEndStep
	public Integer getEndStep()
	{
		return getReminders() + 1;
	}

	//------------------------------------------------------------------------------------- getGlobal
	public static Vote getGlobal()
	{
		return global;
	}

	//------------------------------------------------------------------------------ getRemainingTime
	public Integer getRemainingTime(Integer step)
	{
		return (getTime() * (getEndStep() - step)) / getEndStep();
	}

	//------------------------------------------------------------------------------- getReminderTime
	public Integer getReminderTime(Integer step)
	{
		return getRemainingTime(step - 1) - getRemainingTime(step);
	}

	//--------------------------------------------------------------------------------------- getName
	public String getName()
	{
		return name;
	}

	//---------------------------------------------------------------------------- getNobodyInRegions
	public String[] getNobodyInRegions()
	{
		return (nobodyInRegions == null) ? global.nobodyInRegions : nobodyInRegions;
	}

	//----------------------------------------------------------------------------- getNobodyInWorlds
	public String[] getNobodyInWorlds()
	{
		return (nobodyInWorlds == null) ? global.nobodyInWorlds : nobodyInWorlds;
	}

	//---------------------------------------------------------------------------------- getReminders
	public Integer getReminders()
	{
		return (reminders == null) ? global.reminders : reminders;
	}

	//--------------------------------------------------------------------------------------- getTime
	public Integer getTime()
	{
		return (time == null) ? global.time : time;
	}

	//----------------------------------------------------------------------------------- getYesCount
	public Integer getYesCount()
	{
		return (yesCount == null) ? global.yesCount : yesCount;
	}

	//------------------------------------------------------------------------------ getYesPercentage
	public Integer getYesPercentage()
	{
		return (yesPercentage == null) ? global.yesPercentage : yesPercentage;
	}

	//------------------------------------------------------------------------------------- setAnswer
	public Vote setAnswer(String answer)
	{
		if (answer.endsWith("%")) {
			setAnswerPercentage(Integer.parseInt(answer.substring(0, answer.length() - 1)));
		} else {
			setAnswerCount(Integer.parseInt(answer));
		}
		return this;
	}

	//-------------------------------------------------------------------------------- setAnswerCount
	public Vote setAnswerCount(Integer answerCount)
	{
		this.answerCount = answerCount;
		return this;
	}

	//--------------------------------------------------------------------------- setAnswerPercentage
	public Vote setAnswerPercentage(Integer answerPercentage)
	{
		this.answerPercentage = answerPercentage;
		return this;
	}

	//-------------------------------------------------------------------------------------- setColor
	public Vote setColor(ChatColor color)
	{
		this.color = color;
		return this;
	}

	//-------------------------------------------------------------------------------------- setColor
	public Vote setColor(String color)
	{
		if (color.equals("BACK"))         this.color = ChatColor.BLACK;
		if (color.equals("DARK_BLUE"))    this.color = ChatColor.DARK_BLUE;
		if (color.equals("DARK_GREEN"))   this.color = ChatColor.DARK_GREEN;
		if (color.equals("DARK_AQUA"))    this.color = ChatColor.DARK_AQUA;
		if (color.equals("DARK_RED"))     this.color = ChatColor.DARK_RED;
		if (color.equals("DARK_PURPLE"))  this.color = ChatColor.DARK_PURPLE;
		if (color.equals("GOLD"))         this.color = ChatColor.GOLD;
		if (color.equals("GRAY"))         this.color = ChatColor.GRAY;
		if (color.equals("DARK_GRAY"))    this.color = ChatColor.DARK_GRAY;
		if (color.equals("BLUE"))         this.color = ChatColor.BLUE;
		if (color.equals("GREEN"))        this.color = ChatColor.GREEN;
		if (color.equals("AQUA"))         this.color = ChatColor.AQUA;
		if (color.equals("RED"))          this.color = ChatColor.RED;
		if (color.equals("LIGHT_PURPLE")) this.color = ChatColor.LIGHT_PURPLE;
		if (color.equals("YELLOW"))       this.color = ChatColor.YELLOW;
		if (color.equals("WHITE"))        this.color = ChatColor.WHITE;
		if (color.equals("MAGIC"))        this.color = ChatColor.MAGIC;
		return this;
	}

	//------------------------------------------------------------------------------------ setCommand
	public Vote setCommand(String command)
	{
		this.command = command;
		return this;
	}

	//--------------------------------------------------------------------------------------- setName
	public Vote setName(String name)
	{
		this.name = name;
		return this;
	}

	//---------------------------------------------------------------------------- setNobodyInRegions
	public Vote setNobodyInRegions(String regions)
	{
		this.nobodyInRegions = regions.split(",");
		return this;
	}

	//---------------------------------------------------------------------------- setNobodyInRegions
	public Vote setNobodyInRegions(String[] regions)
	{
		this.nobodyInRegions = regions;
		return this;
	}

	//----------------------------------------------------------------------------- setNobodyInWorlds
	public Vote setNobodyInWorlds(String worlds)
	{
		this.nobodyInWorlds = worlds.split(",");
		return this;
	}

	//----------------------------------------------------------------------------- setNobodyInWorlds
	public Vote setNobodyInWorlds(String[] worlds)
	{
		this.nobodyInWorlds = worlds;
		return this;
	}

	//---------------------------------------------------------------------------------- setReminders
	public Vote setReminders(Integer reminders)
	{
		this.reminders = reminders;
		return this;
	}

	//---------------------------------------------------------------------------------- setReminders
	public Vote setReminders(String reminders)
	{
		setReminders(Integer.parseInt(reminders));
		return this;
	}

	//--------------------------------------------------------------------------------------- setTime
	public Vote setTime(Integer time)
	{
		this.time = time;
		return this;
	}

	//--------------------------------------------------------------------------------------- setTime
	public Vote setTime(String time)
	{
		setTime(Integer.parseInt(time));
		return this;
	}

	//---------------------------------------------------------------------------------------- setYes
	public Vote setYes(String yes)
	{
		if (yes.endsWith("%")) {
			setYesPercentage(Integer.parseInt(yes.substring(0, yes.length() - 1)));
		} else {
			setYesCount(Integer.parseInt(yes));
		}
		return this;
	}

	//----------------------------------------------------------------------------------- setYesCount
	public Vote setYesCount(Integer yesCount)
	{
		this.yesCount = yesCount;
		return this;
	}

	//------------------------------------------------------------------------------ setYesPercentage
	public Vote setYesPercentage(Integer yesPercentage)
	{
		this.yesPercentage = yesPercentage;
		return this;
	}

	//-------------------------------------------------------------------------------------- toString
	@Override
	public String toString()
	{
		return name + " " + color + " " + command + " : "
			+ "ans=" + getAnswerCount() + "|" + getAnswerPercentage() + "% "
			+ "yes=" + getYesCount() + "|" + getYesPercentage() + "% "
			+ "(" + time + "s/" + reminders + ")";
	}

}
