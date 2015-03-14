package me.MrBlobman.MultiSell;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Logic {
	public static boolean makeSellTransaction(Configuration config, Player player, Material mat, double pricePerItem, short data){
		//Resposible for making one item type sale at a time
		ItemStack item = new ItemStack(mat, 1);
		if (data != 0){
			//Used to set values for lapis (inksac), charcoal, log types etc.
			item.setDurability(data);
		}
		if (!player.getInventory().containsAtLeast(item, 1)){
			return false;
		}else{
			int amtRemoved = 0;
			while (player.getInventory().containsAtLeast(item, 1)){
				amtRemoved++;
				player.getInventory().removeItem(item);
			}double moneyEarned = amtRemoved * pricePerItem * MultiSell.multiplier;
			Messages.soldItem( player, amtRemoved, moneyEarned, mat);
			MultiSell.econ.depositPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), moneyEarned);
			return true;
		}
	}
}
