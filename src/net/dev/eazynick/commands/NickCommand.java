package net.dev.eazynick.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.PlayerUnnickEvent;
import net.dev.eazynick.utils.FileUtils;
import net.dev.eazynick.utils.LanguageFileUtils;
import net.dev.eazynick.utils.StringUtils;
import net.dev.eazynick.utils.Utils;

public class NickCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		FileUtils fileUtils = eazyNick.getFileUtils();
		LanguageFileUtils languageFileUtils = eazyNick.getLanguageFileUtils();
		
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(p.hasPermission("nick.use")) {
				if((utils.getCanUseNick().get(p.getUniqueId()))) {
					if(utils.getNickedPlayers().contains(p.getUniqueId()))
						Bukkit.getPluginManager().callEvent(new PlayerUnnickEvent(p));
					else {
						if(args.length == 0)
							utils.performNick(p, "RANDOM");
						else {
							if(p.hasPermission("nick.customnickname")) {
								String name = args[0].replace("\"", "");
								boolean isCancelled = false;
								
								if(new StringUtils(name).removeColorCodes().getString().length() <= 16) {
									if(!(utils.getBlackList().contains(args[0].toUpperCase()))) {
										boolean nickNameIsInUse = false;
										
										for (String nickName : utils.getPlayerNicknames().values()) {
											if(nickName.toUpperCase().equalsIgnoreCase(name.toUpperCase()))
												nickNameIsInUse = true;
										}

										if(!(nickNameIsInUse) || fileUtils.cfg.getBoolean("AllowPlayersToUseSameNickName")) {
											boolean playerWithNameIsKnown = false;
											
											for (Player all : Bukkit.getOnlinePlayers()) {
												if(all.getName().toUpperCase().equalsIgnoreCase(name.toUpperCase()))
													playerWithNameIsKnown = true;
											}
												
											if(Bukkit.getOfflinePlayers() != null) {
												for (OfflinePlayer all : Bukkit.getOfflinePlayers()) {
													if((all != null) && (all.getName() != null) && all.getName().toUpperCase().equalsIgnoreCase(name.toUpperCase()))
														playerWithNameIsKnown = true;
												}
											}
											
											if(!(fileUtils.cfg.getBoolean("AllowPlayersToNickAsKnownPlayers")) && playerWithNameIsKnown)
												isCancelled = true;
											
											if(!(isCancelled)) {
												if(!(name.equalsIgnoreCase(p.getName())))
													utils.performNick(p, ChatColor.translateAlternateColorCodes('&', eazyNick.getVersion().equals("1_7_R4") ? ((eazyNick.getUUIDFetcher_1_7().getUUID(name) != null) ? eazyNick.getUUIDFetcher_1_7().getName(eazyNick.getUUIDFetcher_1_7().getUUID(name)) : name) : (eazyNick.getVersion().equals("1_8_R1") ? ((eazyNick.getUUIDFetcher_1_8_R1().getUUID(name) != null) ? eazyNick.getUUIDFetcher_1_8_R1().getName(eazyNick.getUUIDFetcher_1_8_R1().getUUID(name)) : name) : ((eazyNick.getUUIDFetcher().getUUID(name) != null) ? eazyNick.getUUIDFetcher().getName(eazyNick.getUUIDFetcher().getUUID(name)) : name))));
												else
													p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.CanNotNickAsSelf"));
											} else
												p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.PlayerWithThisNameIsKnown"));
										} else
											p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.NickNameAlreadyInUse"));
									} else
										p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.NameNotAllowed"));
								} else
									p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.NickTooLong"));
							} else
								p.sendMessage(utils.getNoPerm());
						}
					}
				} else
					p.sendMessage(utils.getPrefix() + languageFileUtils.getConfigString("Messages.NickDelay"));
			} else
				p.sendMessage(utils.getNoPerm());
		} else
			utils.sendConsole(utils.getNotPlayer());
		
		return true;
	}
	
}
