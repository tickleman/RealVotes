package fr.crafter.tickleman.realvotes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class RealVotes extends JavaPlugin
{

	private String[] params = {"", "", "", "", "", "", "", "", ""};

	private Map<Player, Boolean> results = new HashMap<Player, Boolean>();

	private Vote vote = null;

	private Map<String, Vote> votes = new HashMap<String, Vote>();

	private Player player = null;

	private Map<String, String> translations = new HashMap<String, String>();

	private World world = null;

	//--------------------------------------------------------------------------------------- addVote
	public void addVote(Vote vote)
	{
		votes.put(vote.getName(), vote);
	}

	//------------------------------------------------------------------------------------- clearVote
	public void clearVote()
	{
		params = new String[]{"", "", "", "", "", "", "", "", ""};
		player = null;
		results.clear();
		vote = null;
		world = null;
	}

	//--------------------------------------------------------------------------------------- endVote
	public boolean endVote()
	{
		Integer playersCount = 0;
		for (Player player : world.getPlayers()) {
			if (player.getClass().getSimpleName().equals("CraftPlayer")) {
				playersCount ++;
			}
		}
		boolean result = true;
		Integer yesCount = 0;
		for (Boolean voted : results.values()) {
			if (voted) yesCount ++;
		}
		System.out.println(
			"end " + vote.toString() + " yes=" + yesCount + " votes=" + results.size()
			+ " players=" + playersCount
		);
		if (result && (vote.getAnswerCount() != null)) {
			if (vote.getAnswerCount() > results.size()) {
				Utils.broadcastWorld(
					world, tr("Vote for +vote had no enough answers: only +count / +total")
					.replace("+vote", vote.getName())
					.replace("+count", new Integer(results.size()).toString())
					.replace("+total", vote.getAnswerCount().toString())
				);
				result = false;
			}
		}
		if (result && (vote.getAnswerPercentage() != null)) {
			if (
				(playersCount == 0)
				|| (vote.getAnswerPercentage() > ((results.size() * 100) / playersCount))
			) {
				Utils.broadcastWorld(
					world, tr("Vote for +vote had no enough answers: only +count / +total")
					.replace("+vote", vote.getName())
					.replace("+count", ((results.size() * 100) / playersCount) + "%")
					.replace("+total", vote.getAnswerPercentage() + "%")
				);
				result = false;
			}
		}
		if (result && (vote.getYesCount() != null)) {
			if (vote.getYesCount() > yesCount) {
				Utils.broadcastWorld(
					world, "Vote for +vote had no enough yes: only +count / +total"
					.replace("+vote", vote.getName())
					.replace("+count", yesCount.toString())
					.replace("+total", vote.getYesCount().toString())
				);
				result = false;
			}
		}
		if (result && (vote.getYesPercentage() != null)) {
			if (vote.getYesPercentage() > ((yesCount * 100) / results.size())) {
				Utils.broadcastWorld(
					world, tr("Vote for +vote had no enough yes: only +count / +total")
					.replace("+vote", vote.getName())
					.replace("+count", ((yesCount * 100) / results.size()) + "%")
					.replace("+total", vote.getYesPercentage() + "%")
				);
				result = false;
			}
		}
		if (result) {
			Utils.broadcastWorld(
				world, tr("Vote for +vote result is yes")
				.replace("+vote", vote.getName())
			);
			executeCommand();
		}
		clearVote();
		return result;
	}

	//-------------------------------------------------------------------------------- executeCommand
	private void executeCommand()
	{
		String command = vote.getCommand();
		if (command != null) {
			command = command
				.replace("$1", params[0])
				.replace("$2", params[1])
				.replace("$3", params[2])
				.replace("$4", params[3])
				.replace("$5", params[4])
				.replace("$6", params[5])
				.replace("$7", params[6])
				.replace("$8", params[7])
				.replace("$9", params[8])
				.replace("$player", player.getName())
				.replace("$world", world.getName());
			getLogger().info("Vote for " + vote.getName() + " accepted: executes " + command);
			getServer().dispatchCommand(getServer().getConsoleSender(), command.substring(1));
		}
	}

	//-------------------------------------------------------------------------------------- getVotes
	public Map<String, Vote> getVotes()
	{
		return votes;
	}

	//-------------------------------------------------------------------------- loadTranslationsFile
	private void loadTranslationsFile()
	{
		translations.clear();
		String buffer = null;
		String fileName = getDataFolder().getPath() + "/messages.txt";
		try {
			buffer = Utils.fileToString(fileName);
		} catch (IOException e) {
		}
		if (buffer != null) {
			for (String line : buffer.split("\n")) {
				if (line.contains("=")) {
					String key = line.substring(0, line.indexOf("="));
					String val = line.substring(line.indexOf("=") + 1);
					translations.put(key, val);
				}
			}
		}
	}

	//--------------------------------------------------------------------------------- loadVotesFile
	private void loadVotesFile()
	{
		String buffer = null;
		String fileName = getDataFolder().getPath() + "/config.txt";
		try {
			buffer = Utils.fileToString(fileName);
		} catch (IOException e) {
			getLogger().severe("Could not load " + fileName);
			e.printStackTrace();
		}
		if (buffer != null) {
			Vote vote = Vote.getGlobal();
			for (String line : buffer.split("\n")) {
				line = line.trim();
				if (line.endsWith(":")) {
					addVote(vote);
					vote = new Vote(line.substring(0, line.length() - 1));
				} else if (line.contains("=")) {
					String[] conf = line.split("=");
					if (conf[0].equals("answer")) vote.setAnswer(conf[1]);
					if (conf[0].equals("color")) vote.setColor(conf[1]);
					if (conf[0].equals("command")) vote.setCommand(conf[1]);
					if (conf[0].toLowerCase().equals("nobodyinregions")) vote.setNobodyInRegions(conf[1]);
					if (conf[0].toLowerCase().equals("nobodyinworlds")) vote.setNobodyInWorlds(conf[1]);
					if (conf[0].equals("reminders")) vote.setReminders(conf[1]);
					if (conf[0].equals("time")) vote.setTime(conf[1]);
					if (conf[0].equals("yes")) vote.setYes(conf[1]);
				}
			}
			addVote(vote);
		}
	}

	//------------------------------------------------------------------------------------- onCommand
	@Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		String cmd = command.getName();
		Player player = (Player) sender;
		if (cmd.equals("vote") && (args.length == 0) && sender.hasPermission("vote.list")) {
			voteList(player);
		}
		if (player instanceof Player) {
			if (cmd.equals("votereload") && sender.hasPermission("vote.reload")) {
				onEnable();
			}
			if (cmd.equals("vote") && (args.length > 0)  && sender.hasPermission("vote." + args[0])) {
				vote(player, args);
			}
			if (cmd.equals("no") && sender.hasPermission("vote.no")) {
				voteNo(player);
			}
			if (cmd.equals("yes") && sender.hasPermission("vote.yes")) {
				voteYes(player);
			}
		} else {
			sender.sendMessage(tr("Only players can vote"));
		}
		return true;
	}

	//------------------------------------------------------------------------------------- onDisable
	@Override
	public void onDisable()
	{
		clearVote();
	}

	//-------------------------------------------------------------------------------------- onEnable
	@Override
	public void onEnable()
	{
		clearVote();
		loadTranslationsFile();
		loadVotesFile();
	}

	//-------------------------------------------------------------------------------------- reminder
	private void reminder(Integer step)
	{
		if (vote != null) {
			if (step < vote.getEndStep()) {
				Utils.broadcastWorld(
					world, tr("Vote for +vote: +remain seconds left")
					.replace("+vote", vote.getName())
					.replace("+remain", vote.getRemainingTime(step).toString())
				);
				scheduleVote(step + 1);
			} else {
				endVote();
			}
		}
	}

	//------------------------------------------------------------------------------------- setParams
	private void setParams(String[] args)
	{
		params = new String[]{"", "", "", "", "", "", "", "", ""};
		for (int i = 1; i < Math.min(args.length, params.length + 1); i ++) {
			if (args[i] != null) {
				params[i - 1] = args[i];
			}
		}
	}

	//---------------------------------------------------------------------------------- scheduleVote
	private void scheduleVote(final Integer step)
	{
		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				reminder(step);
			}
		}, vote.getReminderTime(step) * 20);
	}

	//------------------------------------------------------------------------------------- startVote
	public void startVote(Player player, Vote vote)
	{
		if (!vote.checkNobodyInRegions(getServer(), player.getWorld())) {
			player.sendMessage(tr("You can't vote: there are people into a protected region"));
			clearVote();
		} else if (!vote.checkNobodyInWorlds(getServer())) {
			player.sendMessage(tr("You can't vote: there are people into a protected world"));
			clearVote();
		} else {
			this.vote = vote;
			this.player = player;
			world = player.getWorld();
			results.put(player, true);
			Utils.broadcastWorld(
				player, tr("+player votes for +vote: +remain seconds left")
				.replace("+player", player.getName())
				.replace("+vote", voteString())
				.replace("+remain", vote.getTime().toString())
			);
			Utils.broadcastWorld(player, tr("You can vote with /yes or /no"));
			scheduleVote(1);
			System.out.println("start " + vote.toString());
		}
	}

	//-------------------------------------------------------------------------------------------- tr
	public String tr(String text)
	{
		String color = ((vote == null) ? Vote.getGlobal().getColor() : vote.getColor()).toString();
		String tr = translations.get(text);
		return color + ((tr == null) ? text : tr);
	}

	//------------------------------------------------------------------------------------------ vote
	private void vote(Player player, String[] args)
	{
		if (vote == null) {
			vote = getVotes().get(args[0]);
			if (vote == null) {
				player.sendMessage(tr("Unknown vote +vote").replace("+vote", args[0]));
				voteList(player);
			} else {
				setParams(args);
				startVote(player, vote);
			}
		} else {
			player.sendMessage(
				tr("Vote for +vote in progress. You can't start another vote")
				.replace("+vote", vote.getName())
			);
		}
	}

	//-------------------------------------------------------------------------------------- voteList
	private void voteList(Player player)
	{
		player.sendMessage(
			tr("You can vote for +votes").replace("+votes", Utils.join(", ", getVotes().keySet()))
		);
	}

	//---------------------------------------------------------------------------------------- voteNo
	private void voteNo(Player player)
	{
		if (vote != null) {
			if (world.equals(player.getWorld())) {
				results.put(player, false);
				player.sendMessage(tr("You vote no for +vote").replace("+vote", vote.getName()));
			} else {
				player.sendMessage(tr("You have nothing to vote for"));
			}
		} else {
			player.sendMessage(tr("You have nothing to vote for"));
		}
	}

	//------------------------------------------------------------------------------------ voteString
	private String voteString()
	{
		StringBuilder string = new StringBuilder(vote.getName());
		for (String param : params) {
			if (!param.isEmpty()) {
				string.append(" ").append(param);
			}
		}
		return string.toString();
	}

	//--------------------------------------------------------------------------------------- voteYes
	private void voteYes(Player player)
	{
		if (vote != null) {
			if (world.equals(player.getWorld())) {
				results.put(player, true);
				player.sendMessage(tr("You vote yes for +vote").replace("+vote", vote.getName()));
			} else {
				player.sendMessage(tr("You have nothing to vote for"));
			}
		} else {
			player.sendMessage(tr("You have nothing to vote for"));
		}
	}

}
