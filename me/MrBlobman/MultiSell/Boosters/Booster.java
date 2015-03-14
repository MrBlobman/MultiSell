package me.MrBlobman.MultiSell.Boosters;

import org.bukkit.ChatColor;

public interface Booster {
	public String getReason();

	public void setReason(String reason);

	public Long getDuration();

	public void setDuration(Long duration);
	
	public void increaseDuration(Long duration);
	
	public Long getTimeRemaining();

	public void setTimeRemaining(Long timeRemaining);

	public double getMultiplier();

	public void setMultiplier(double multiplier);
	
	public Runnable getCallback();
	
	public void setCallback(Runnable callback);
	
	public void end();
	
	public void end(boolean runCallback);
	
	public String[] asString(ChatColor color);
}
