package me.MrBlobman.MultiSell.Boosters;

import me.MrBlobman.MultiSell.Messages;
import me.MrBlobman.MultiSell.MultiSell;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BoosterCommandExecutor implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("addBooster")){
			if (args.length == 3){
				double multiplier = 1;
				long length = 0;
				try{
					multiplier = Double.parseDouble(args[0]);
				}catch (NumberFormatException e){
					sender.sendMessage(ChatColor.RED+"ERROR: " + args[0] + "is not a valid decimal value.");
					return true;
				}try{
					length = Long.parseLong(args[1]);
					length = length*20;
				}catch (NumberFormatException e){
					sender.sendMessage(ChatColor.RED+"ERROR: " + args[1] + "is not a valid number of seconds.");
					return true;
				}
				String reason = args[2];
				if (length > 60){
					MultiSell.boosters.add(new LongBooster(reason, multiplier, length, length));
				}else{
					MultiSell.boosters.add(new ShortBooster(reason, multiplier, length, length));
				}
			}else{
				Messages.showHelpAddBooster(sender);
			}
		}else if (label.equalsIgnoreCase("removeBooster")){
			if (args.length == 1){
				String reason = args[0];
				Booster boosterToCancel = null;
				for (Booster booster : MultiSell.boosters){
					if (booster.getReason().equals(reason)){
						if (boosterToCancel != null){
							if (boosterToCancel.getTimeRemaining() > booster.getTimeRemaining()){
								boosterToCancel = booster;
							}
						}else{
							boosterToCancel = booster;
						}
					}
				}if (boosterToCancel != null){
					boosterToCancel.end(true);
					sender.sendMessage(ChatColor.GREEN + reason + " booster removed.");
					return true;
				}
				sender.sendMessage(ChatColor.RED + "A booster with the reason specified does not exist!");
			}else{
				Messages.showHelpRemoveBooster(sender);
			}
		}else if (label.equalsIgnoreCase("getBoosters")){
			if (args.length == 0){
				sender.sendMessage(ChatColor.AQUA+"Currently active boosters: ");
				int switcher = 0;
				for (Booster booster : MultiSell.boosters){
					sender.sendMessage(booster.asString(switcher++%2 == 0 ? ChatColor.YELLOW : ChatColor.GOLD));
				}
			}else{
				Messages.showHelpGetBoosters(sender);
			}
		}
		return true;
	}

}
