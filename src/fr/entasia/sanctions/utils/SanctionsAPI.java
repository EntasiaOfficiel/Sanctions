package fr.entasia.sanctions.utils;

import fr.entasia.sanctions.Utils;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetAddress;
import java.util.Arrays;

public class SanctionsAPI {

	public static BanEntry getBanEntry(String name){
		for(BanEntry se : Utils.bans){
			if(se.on.equals(name))return se;
		}
		return null;
	}

	public static BanEntry getBanEntry(byte[] ip){
		for(BanEntry se : Utils.bans){
			if(Arrays.equals(se.ip, ip))return se;
		}
		return null;
	}



	public static MuteEntry getMuteEntry(String name){
		for(MuteEntry se : Utils.mutes){
			if(se.on.equals(name))return se;
		}
		return null;
	}
}
