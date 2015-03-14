package me.MrBlobman.MultiSell.Boosters;

import me.MrBlobman.MultiSell.MultiSell;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class LongBooster extends BukkitRunnable implements Booster{

	private String reason;
	private Long timeRemaining;
	private Long duration;	//In ticks, ie 20ticks/sec
	private double multiplier;
	private BukkitTask task;
	private Runnable callback;
	
	LongBooster(String reason, double multiplier, Long duration, Long timeRemaining){
		this.reason = reason;
		this.multiplier = multiplier;
		this.duration = duration;
		this.timeRemaining = timeRemaining;
		scheduleTask();
		write(this);
		MultiSell.multiplier = MultiSell.multiplier * multiplier;
	}
	
	LongBooster(String reason, double multiplier, Long duration, Long timeRemaining, Runnable callback){
		this(reason, multiplier, duration, timeRemaining);
		this.setCallback(callback);
	}
	
	public void scheduleTask(){
		this.task = Bukkit.getScheduler().runTaskTimer(MultiSell.plugin, this, 0L, 1200L);
	}
	
	public void reScheduleTask(){
		if (this.task != null){
			this.task.cancel();
		}scheduleTask();
	}
	
	//Task runs every minute, writes to config that minute has passed incase server crash
	@Override
	public void run() {
		if (this.timeRemaining >= 1200L){
			//Will run for atleast another minute
			writeTimeLeft(this);
			this.timeRemaining = this.timeRemaining - 1200L;
		}else{
			//Make a ShortBooster for the remaining time
			remove(this);
			if (this.getCallback() != null){
				new ShortBooster(this, getCallback());
			}else{
				new ShortBooster(this);
			}
			this.task.cancel();
			if (MultiSell.boosters.contains(this)){
				MultiSell.boosters.remove(this);
			}
			MultiSell.multiplier = MultiSell.multiplier / multiplier;
		}
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}
	
	public void increaseDuration(Long duration) {
		this.duration = this.duration + duration;
	}
	
	public Long getTimeRemaining() {
		return timeRemaining;
	}

	public void setTimeRemaining(Long timeRemaining) {
		this.timeRemaining = timeRemaining;
	}

	public double getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(double multiplier) {
		this.multiplier = multiplier;
	}
	
	public void end(){
		if (this.task != null){
			this.task.cancel();
			MultiSell.multiplier = MultiSell.multiplier / multiplier;
		}
		if (MultiSell.boosters.contains(this)){
			MultiSell.boosters.remove(this);
		}
		remove(this);
	}
	
	public void end(boolean runCallback){
		if (this.task != null){
			this.task.cancel();
			if (runCallback && this.getCallback() != null){
				Bukkit.getScheduler().runTask(MultiSell.plugin, this.getCallback());
			}
			MultiSell.multiplier = MultiSell.multiplier / multiplier;
		}
		if (MultiSell.boosters.contains(this)){
			MultiSell.boosters.remove(this);
		}
		remove(this);
	}

	public static void write(LongBooster out){
		Configuration config = MultiSell.plugin.getConfig();
		String key = "ActiveBoosters."+out.getReason();
		config.set(key + ".TotalDuration", out.getDuration());
		config.set(key + ".TimeRemaining", out.getTimeRemaining());
		config.set(key + ".MultiplierAmount", out.getMultiplier());
		MultiSell.plugin.saveConfig();
		MultiSell.plugin.reloadConfig();
	}
	
	public static void writeTimeLeft(LongBooster out){
		Configuration config = MultiSell.plugin.getConfig();
		String key = "ActiveBoosters."+out.getReason();
		config.set(key + ".TimeRemaining", out.getTimeRemaining());
		MultiSell.plugin.saveConfig();
		MultiSell.plugin.reloadConfig();
	}
	
	public static LongBooster read(String reason){
		Configuration config = MultiSell.plugin.getConfig();
		String key = "ActiveBoosters."+reason;
		LongBooster in = new LongBooster(reason, config.getDouble(key + ".MultiplierAmount"), config.getLong(key + ".TotalDuration"), config.getLong(key + ".TimeRemaining"));
		return in;
	}
	
	public static void remove(LongBooster out){
		Configuration config = MultiSell.plugin.getConfig();
		String key = "ActiveBoosters."+out.getReason();
		if (config.contains(key)){
			config.set(key, null);
			MultiSell.plugin.saveConfig();
			MultiSell.plugin.reloadConfig();
		}else{
			MultiSell.plugin.getLogger().warning("[MultiSell] Tried to remove a booster that is not defined in the config.");
		}
	}
	public static boolean alreadyBooster(String reason){
		return MultiSell.plugin.getConfig().contains("ActiveBoosters." + reason);
	}

	public Runnable getCallback() {
		return callback;
	}

	public void setCallback(Runnable callback) {
		this.callback = callback;
	}
	
	public String[] asString(ChatColor color){
		return new String[]{color + this.reason + ": x" + String.valueOf(this.multiplier), color + "  Length: " + String.valueOf(this.duration/20) + "s", color + "  Time Remaining: " + String.valueOf(this.timeRemaining/20) + "s"};
	}
}
