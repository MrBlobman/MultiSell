package me.MrBlobman.MultiSell;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import me.MrBlobman.MultiSell.Boosters.Booster;

public class Util {
	public static String format(double amt){
		if (amt >= 1000000000000.0){
			return String.format("%.2fT", amt/ 1000000000000.0);
		}else if (amt >= 1000000000.0){
			return String.format("%.2fB", amt/ 1000000000.0);
		}else if (amt >= 1000000.0){
			return String.format("%.2fM", amt/ 1000000.0);
		}else if (amt >= 10000.0){
			return String.format("%.2fK", amt/ 1000.0);
		}else{
			return NumberFormat.getNumberInstance(Locale.US).format(amt);
		}
	}
	
	public static String listToCSV(List<String> list){
		String msg = "";
		for (String element : list){
			msg = msg + element + ", ";
		}
		msg = msg.substring(0, msg.length()-2);
		return msg;
	}
	
	public static String boosterCollToReasonCSV(Collection<Booster> coll){
		List<String> reasons = new ArrayList<String>();
		for (Booster booster : MultiSell.boosters){
			reasons.add(booster.getReason());
		}
		return listToCSV(reasons);
	}
	
	public static String boosterCollToLowestLength(Collection<Booster> coll){
		if (coll.isEmpty()){
			return "";
		}
		Long lowestTimeLeft = 0L; //In ticks
		String formattedLength = "";
		for(Booster booster : coll){
			Long timeLeft = booster.getTimeRemaining();
			if (timeLeft < lowestTimeLeft || lowestTimeLeft.equals(0L)){
				lowestTimeLeft = timeLeft;
			}
		}
		if (lowestTimeLeft > 720000){
			formattedLength = formattedLength + String.valueOf((int) (lowestTimeLeft/72000L)) + "h ";
			lowestTimeLeft = lowestTimeLeft%72000;
		}else if (lowestTimeLeft > 1200){
			formattedLength = formattedLength + String.valueOf((int) (lowestTimeLeft/1200)) + "m ";
			lowestTimeLeft = lowestTimeLeft%1200;
		}else if (lowestTimeLeft > 20){
			formattedLength = formattedLength + String.valueOf((int) (lowestTimeLeft/20)) + "s ";
			lowestTimeLeft = lowestTimeLeft%20;
		}
		if (formattedLength.length() > 0){
			formattedLength = formattedLength.substring(0, formattedLength.length());
		}
		return formattedLength;
	}
}
