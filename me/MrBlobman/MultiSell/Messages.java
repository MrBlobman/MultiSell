package me.MrBlobman.MultiSell;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Messages {
	public static final String HEADER;
	public static final String INVALID_SHOP;
	public static final String NOTHING_TO_SELL;
	public static final String SOLD_ITEM;
	public static final String NO_PERMISSION_FOR_SHOP;
	public static final String PRICE_MULTIPLIER;
	public static final String BOOSTER_END;
	
	static {
		Configuration config = MultiSell.plugin.getConfig();
		if (config.contains("Messages.ChatPrefix")){
			HEADER = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.ChatPrefix"));
		}else{
			HEADER = "";
		}
		
		if (config.contains("Messages.InvalidShop")){
			INVALID_SHOP = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.InvalidShop"));
		}else{
			INVALID_SHOP = ChatColor.RED + "%listOfShops%";
		}
		
		if (config.contains("Messages.NothingToSell")){
			NOTHING_TO_SELL = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.NothingToSell"));
		}else{
			NOTHING_TO_SELL = ChatColor.RED+"Your inventory does not contain any items to sell.";
		}
		
		if (config.contains("Messages.SoldItem")){
			SOLD_ITEM = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.SoldItem"));
		}else{
			SOLD_ITEM = ChatColor.GREEN+"You sold %amount% %itemType% for $%moneyEarned%.";
		}
		
		if (config.contains("Messages.NoPermissionForShop")){
			NO_PERMISSION_FOR_SHOP = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.NoPermissionForShop"));
		}else{
			NO_PERMISSION_FOR_SHOP = ChatColor.RED+"You dont have permission to use this shop.";
		}
		
		if (config.contains("Messages.PriceMultiplier")){
			PRICE_MULTIPLIER = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.PriceMultiplier"));
		}else{
			PRICE_MULTIPLIER = ChatColor.AQUA + "Current multiplier x%multiplierAmount% thanks to %reason%. (%length%)";
		}
		
		if (config.contains("Messages.BoosterEnd")){
			BOOSTER_END = ChatColor.translateAlternateColorCodes('&', config.getString("Messages.BoosterEnd"));
		}else{
			BOOSTER_END = ChatColor.AQUA + "%reason%'s %multiplierAmount% multiplier has ended.";
		}
	}
	
	public static void invalidShopType(Player player, File shopFolder){
		String listOfShops = "EMPTY(No shops defined)";
		if (shopFolder.exists()){
			if (shopFolder.isDirectory()){
				listOfShops = "";
				File[] shops = shopFolder.listFiles();
				for(File shop : shops){
					listOfShops = listOfShops + shop.getName().replace(".yml", "") + ",";
				}listOfShops = listOfShops.substring(0, listOfShops.length()-1);
			}
		}
		player.sendMessage(HEADER + INVALID_SHOP.replace("%listOfShops%", listOfShops));
	}
	
	public static void nothingToSell(Player player){
		player.sendMessage(NOTHING_TO_SELL.replace("%player%", player.getName()));
	}
	
	public static void soldItem(Player player, int amtSold, double moneyEarned, Material itemType){
		String item = itemType.toString();
		String money = Util.format(moneyEarned);
		String amt = String.valueOf(amtSold);
		player.sendMessage(SOLD_ITEM.replace("%player%", player.getName()).replace("%amount%", amt).replace("%itemType%", item).replace("%moneyEarned%", money));
	}
	
	public static void soldToShop( Player player, String shopName){
		player.sendMessage(HEADER + ChatColor.AQUA + "Selling items at " + shopName);
	}
	
	public static void noPermission(Player player){
		player.sendMessage(HEADER + NO_PERMISSION_FOR_SHOP.replace("%player%", player.getName()));
	}
	
	public static void listShopContents(String shopName, File shopFile, Player player, File shopFolder){
		if (shopFile.exists()){
			FileConfiguration shopConfig = YamlConfiguration.loadConfiguration(shopFile);
			player.sendMessage(ChatColor.AQUA+"Info about "+ shopName);
			for (String mat : shopConfig.getKeys(false)){
				String msg = ChatColor.RED+"Sell 1 "+ChatColor.YELLOW+mat+ChatColor.RED+" for "+ChatColor.GREEN+"$"+shopConfig.getString(mat+".PricePerItem");
				player.sendMessage(msg);
			}
		}else{
			Messages.invalidShopType(player, shopFolder);
		}
	}
	
	public static void showMultiplier(Player player){
		if (MultiSell.boosters.isEmpty()){
			return;
		}player.sendMessage(HEADER + PRICE_MULTIPLIER.replace("%multiplierReason%", Util.boosterCollToReasonCSV(MultiSell.boosters)).replace("%length%", Util.boosterCollToLowestLength(MultiSell.boosters)).replace("%multiplierAmount%", String.format("%.2f", MultiSell.multiplier)));
	}
	
	public static void boosterEnd(String reason, double multiplier){
		Bukkit.getServer().broadcastMessage(HEADER + BOOSTER_END.replace("%reason%", reason).replace("%multiplierAmount%", String.valueOf(multiplier)));
	}
	
	public static void showHelpAddBooster(CommandSender sender){
		sender.sendMessage(HEADER + ChatColor.YELLOW + " The AddBooster command: "+ChatColor.GOLD + "Add a booster with the given info.");
		sender.sendMessage(ChatColor.AQUA + "/addbooster <multiplier> <length(in sec)> <reason>");
		sender.sendMessage(ChatColor.YELLOW + "multiplier " +ChatColor.AQUA + "The amount this booster will multiply the current multiplier by.");
		sender.sendMessage(ChatColor.YELLOW + "length " +ChatColor.AQUA + "The length in seconds this booster will last.");
		sender.sendMessage(ChatColor.YELLOW + "reason " +ChatColor.AQUA + "The reason for the booster being active. Usually to say thanks to a player or milestone etc.");
	}
	
	public static void showHelpRemoveBooster(CommandSender sender){
		sender.sendMessage(HEADER + ChatColor.YELLOW + " The removeBooster command: "+ChatColor.GOLD + "Ends the booster with the given reason.");
		sender.sendMessage(ChatColor.AQUA + "/removebooster <reason>");
		sender.sendMessage(ChatColor.YELLOW + "reason " +ChatColor.AQUA + "Used to reference a booster. If two boosters are active with the same reason the one with the shortest time remaining will be canceled.");
	}
	
	public static void showHelpGetBoosters(CommandSender sender){
		sender.sendMessage(HEADER + ChatColor.YELLOW + " The getBooster command: "+ChatColor.GOLD + "Lists all currently active boosters.");
		sender.sendMessage(ChatColor.AQUA + "/getboosters");
	}
}
