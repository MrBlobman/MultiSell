package me.MrBlobman.MultiSell.Boosters;

import me.MrBlobman.MultiSell.Messages;
import me.MrBlobman.MultiSell.MultiSell;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

//Used for boosters less than a minute
public class ShortBooster extends BukkitRunnable implements Booster{

	private String reason;
	private Long timeRemaining;
	private Long duration;	//In ticks, ie 20ticks/sec
	private double multiplier;
	private BukkitTask task;
	private Runnable callback;
	
	//This task needs to run every second not just once
	ShortBooster(String reason, double multiplier, Long duration, Long timeRemaining){
		this.setReason(reason);
		this.setMultiplier(multiplier);
		this.setDuration(duration);
		this.timeRemaining = timeRemaining;
		this.scheduleTask();
		MultiSell.multiplier = MultiSell.multiplier * multiplier;
		MultiSell.boosters.add(this);
	}
	
	ShortBooster(String reason, double multiplier, Long duration, Long timeRemaining, Runnable callback){
		this(reason, multiplier, duration, timeRemaining);
		this.callback = callback;
	}
	
	ShortBooster(LongBooster booster){
		this(booster.getReason(), booster.getMultiplier(), booster.getDuration(), booster.getTimeRemaining());
	}
	
	ShortBooster(LongBooster booster, Runnable callback){
		this(booster);
		this.callback = callback;
	}
	
	public void end(){
		if (this.task != null){
			this.task.cancel();
			MultiSell.multiplier = MultiSell.multiplier / multiplier;
		}
		if (MultiSell.boosters.contains(this)){
			MultiSell.boosters.remove(this);
		}
	}
	
	public void end(boolean runCallback){
		if (this.task != null){
			this.task.cancel();
			if (runCallback && this.callback != null){
				Bukkit.getScheduler().runTask(MultiSell.plugin, this.callback);
			}
			MultiSell.multiplier = MultiSell.multiplier / multiplier;
		}
		if (MultiSell.boosters.contains(this)){
			MultiSell.boosters.remove(this);
		}
	}

	public void scheduleTask(){
		this.task = Bukkit.getScheduler().runTaskTimer(MultiSell.plugin, this, 0L, 20L);
	}
	
	@Override
	public void run() {
		if (this.timeRemaining >= 0L){
			//Let 1 sec pass and count it down
			this.timeRemaining = this.timeRemaining - 20L;
		}else{
			//This is run when booster ends
			Messages.boosterEnd(this.reason, this.multiplier);
			if (this.callback != null){
				Bukkit.getScheduler().runTask(MultiSell.plugin, this.callback);
			}MultiSell.multiplier = MultiSell.multiplier / multiplier;
			if (MultiSell.boosters.contains(this)){
				MultiSell.boosters.remove(this);
			}this.task.cancel();
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

	public double getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(double multiplier) {
		this.multiplier = multiplier;
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
