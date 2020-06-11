package fr.entasia.sanctions;

import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {

	public static Main main;

	@Override
	public void onEnable(){
		try{
			main = this;
			getLogger().info("Activation du plugin !");
		}catch(Throwable e){
			e.printStackTrace();
			getLogger().severe("ARRET DU SERVEUR !");
			getProxy().stop();
		}
	}

}
