package me.MrBlobman.MultiSell;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MultiSellCommandExecutor implements CommandExecutor{
	private MultiSell plugin;
	MultiSellCommandExecutor(MultiSell instance){
		this.plugin = instance;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel,String[] args) {
		if (sender instanceof Player){
			Player player = (Player) sender;
			if (cmdLabel.equalsIgnoreCase("sell")){
				if (args.length == 1){
					String givenShopName = args[0].toUpperCase();
					File shopFile = new File(plugin.getDataFolder()+File.separator+"Shops"+File.separator, givenShopName+".yml");
					if (!shopFile.exists()){
						//player gave an invalid shop name
						Messages.invalidShopType(player, new File(plugin.getDataFolder()+File.separator+"Shops"+File.separator));
					}else if (player.hasPermission("Sell."+givenShopName) || player.isOp()){
						Messages.soldToShop(player, givenShopName);
						Messages.showMultiplier(player);
						boolean hasSold = false;
						FileConfiguration shopConfig = YamlConfiguration.loadConfiguration(shopFile);
						for (String stringMat : shopConfig.getKeys(false)){
							Material mat = null;
							if (stringMat.contains(":")){
								mat = Material.getMaterial((stringMat.substring(0, stringMat.indexOf(":"))));
							}else{
								mat = Material.getMaterial(stringMat);
							}
							int pricePerItem = 0;
							short data = 0;
							if (shopConfig.contains(stringMat+".PricePerItem")){
								pricePerItem = shopConfig.getInt(stringMat+".PricePerItem");
							}if (stringMat.contains(":")){
								data = Short.valueOf(stringMat.substring(stringMat.indexOf(":")+1));
							}
							if (Logic.makeSellTransaction(plugin.getConfig(), player, mat, pricePerItem, data)){
								hasSold = true;
							}
						}if (!hasSold){
							Messages.nothingToSell(player);
						}
					}else{
						Messages.noPermission(player);
					}
				}
			}else if (cmdLabel.equalsIgnoreCase("createshop") && (player.isOp() || player.hasPermission("Sell.create"))){
				if (args.length == 1){
					String givenShopName = args[0].toUpperCase();
					File shopFile = new File(plugin.getDataFolder()+File.separator+"Shops"+File.separator, givenShopName+".yml");
					if (shopFile.exists()){
						player.sendMessage(ChatColor.DARK_RED+"Shop already exists, use /editshop to edit it.");
					}else{
						FileConfiguration shopConfig = YamlConfiguration.loadConfiguration(shopFile);
						try {
							shopConfig.save(shopFile);
							player.sendMessage(ChatColor.GREEN+"Shop created. Use /editshop to add or remove items from it.");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}else if (cmdLabel.equalsIgnoreCase("editshop") && (player.isOp() || player.hasPermission("Sell.edit"))){
				if (args.length == 2){
					String givenShopName = args[0].toUpperCase();
					File shopFile = new File(plugin.getDataFolder()+File.separator+"Shops"+File.separator, givenShopName+".yml");
					if (shopFile.exists()){
						String mat = player.getItemInHand().getType().toString();
						if (player.getItemInHand().getDurability() != 0){
							mat = mat + ":" + String.valueOf(player.getItemInHand().getDurability());
						}
						boolean correctNum = true;
						try{
							Double.parseDouble(args[1]);
						}catch (NumberFormatException e){
							player.sendMessage(args[1]+" is not a valid number.");
							correctNum = false;
						}if (correctNum){
							FileConfiguration shopConfig = YamlConfiguration.loadConfiguration(shopFile);
							try {
								shopConfig.save(shopFile);
								double pricePerItem = Double.parseDouble(args[1]);
								if (pricePerItem != 0){
									shopConfig.set(mat+".PricePerItem", pricePerItem);
								}else{
									shopConfig.set(mat, null);
								}shopConfig.save(shopFile);
								player.sendMessage(ChatColor.GREEN+"Shop edited.");
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}else{
						Messages.invalidShopType(player, new File(plugin.getDataFolder()+File.separator+"Shops"+File.separator));
					}
				}else{
					player.sendMessage(ChatColor.AQUA+"Please use the following format: /editshop <shopname> <pricePerItem(0 to remove)>");
				}
			}else if (cmdLabel.equalsIgnoreCase("shopinfo") && (player.isOp() || player.hasPermission("Sell.info"))){
				if (args.length == 1){
					Messages.listShopContents(args[0].toUpperCase(), new File(plugin.getDataFolder()+File.separator+"Shops"+File.separator, args[0].toUpperCase()+".yml"), player, new File(plugin.getDataFolder()+File.separator+"Shops"+File.separator));
				}
			}
		}
		return true;
	}

}
