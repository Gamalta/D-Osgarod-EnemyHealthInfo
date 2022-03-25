package fr.gamalta.osgarod.enemyhealthinfo.commands;

import fr.gamalta.osgarod.enemyhealthinfo.EnemyHealthInfo;
import fr.gamalta.osgarod.enemyhealthinfo.utils.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnemyHealthInfoCmd implements CommandExecutor, TabCompleter {

	EnemyHealthInfo main;

	public EnemyHealthInfoCmd(EnemyHealthInfo main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender instanceof Player) {

			Player player = (Player) sender;

			if (args.length > 0) {

				if (args[0].equalsIgnoreCase("actionbar")) {

					if (args.length > 1) {

						if (args[1].equalsIgnoreCase("enable")) {

							main.playersCFG.set("EnemyHealthInfo." + player.getName() + ".ActionBar", true);
							player.spigot().sendMessage(new Messages(main, main.settingsCFG, "EnemyHealthInfo.ActionBar.Enable").create());
							return true;

						} else if (args[1].equalsIgnoreCase("disable")) {

							main.playersCFG.set("EnemyHealthInfo." + player.getName() + ".ActionBar", false);
							player.spigot().sendMessage(new Messages(main, main.settingsCFG, "EnemyHealthInfo.ActionBar.Disable").create());
							return true;

						} else {

							player.spigot().sendMessage(new Messages(main, main.settingsCFG, "EnemyHealthInfo.Usage").create());
							return true;
						}
					} else {

						if (main.playersCFG.contains("EnemyHealthInfo." + player.getName() + ".ActionBar")) {

							if (main.playersCFG.getBoolean("EnemyHealthInfo." + player.getName() + ".ActionBar")) {

								main.playersCFG.set("EnemyHealthInfo." + player.getName() + ".ActionBar", false);
								player.spigot().sendMessage(new Messages(main, main.settingsCFG, "EnemyHealthInfo.ActionBar.Disable").create());
								return true;

							} else {

								main.playersCFG.set("EnemyHealthInfo." + player.getName() + ".ActionBar", true);
								player.spigot().sendMessage(new Messages(main, main.settingsCFG, "EnemyHealthInfo.ActionBar.Enable").create());
								return true;

							}
						} else {

							main.playersCFG.set("EnemyHealthInfo." + player.getName() + ".ActionBar", false);
							player.spigot().sendMessage(new Messages(main, main.settingsCFG, "EnemyHealthInfo.ActionBar.Disable").create());
							return true;

						}
					}
				} else if (args[0].equalsIgnoreCase("bossbar")) {

					if (args.length > 1) {

						if (args[1].equalsIgnoreCase("enable")) {

							main.playersCFG.set("EnemyHealthInfo." + player.getName() + ".BossBar", true);
							player.spigot().sendMessage(new Messages(main, main.settingsCFG, "EnemyHealthInfo.BossBar.Enable").create());
							return true;

						} else if (args[1].equalsIgnoreCase("disable")) {

							main.playersCFG.set("EnemyHealthInfo." + player.getName() + ".BossBar", false);
							player.spigot().sendMessage(new Messages(main, main.settingsCFG, "EnemyHealthInfo.BossBar.Disable").create());
							return true;

						}
					} else {

						if (main.playersCFG.contains("EnemyHealthInfo." + player.getName() + ".BossBar")) {

							if (main.playersCFG.getBoolean("EnemyHealthInfo." + player.getName() + ".BossBar")) {

								main.playersCFG.set("EnemyHealthInfo." + player.getName() + ".BossBar", false);
								player.spigot().sendMessage(new Messages(main, main.settingsCFG, "EnemyHealthInfo.BossBar.Disable").create());
								return true;

							} else {

								main.playersCFG.set("EnemyHealthInfo." + player.getName() + ".BossBar", true);
								player.spigot().sendMessage(new Messages(main, main.settingsCFG, "EnemyHealthInfo.BossBar.Enable").create());
								return true;

							}
						} else {

							main.playersCFG.set("EnemyHealthInfo." + player.getName() + ".BossBar", false);
							player.spigot().sendMessage(new Messages(main, main.settingsCFG, "EnemyHealthInfo.BossBar.Disable").create());
							return true;

						}
					}
				} else if (args[0].equalsIgnoreCase("reload")) {

					if (player.hasPermission(main.settingsCFG.getString("EnemyHealthInfo.Reload.Permission"))) {

						main.playersCFG.loadConfig();
						main.settingsCFG.loadConfig();
						player.spigot().sendMessage(new Messages(main, main.settingsCFG, "EnemyHealthInfo.Reload.Text").create());
						return true;
					}
				}
			}

			player.spigot().sendMessage(new Messages(main, main.settingsCFG, "EnemyHealthInfo.Usage").create());
		}

		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

		ArrayList<String> tabComplete = new ArrayList<>();

		if (args.length == 1) {

			if (args[0].equals("")) {

				tabComplete.add("ActionBar");
				tabComplete.add("BossBar");

			} else {

				if ("actionbar".startsWith(args[0].toLowerCase())) {

					tabComplete.add("ActionBar");
				}

				if ("bossbar".startsWith(args[0].toLowerCase())) {

					tabComplete.add("BossBar");
				}
			}

		} else if (args.length == 2) {

			if (args[1].equals("")) {

				tabComplete.add("Enable");
				tabComplete.add("Disable");

			} else {

				if ("enable".startsWith(args[0].toLowerCase())) {

					tabComplete.add("Enable");
				}

				if ("disable".startsWith(args[0].toLowerCase())) {

					tabComplete.add("Disable");
				}
			}
		}

		Collections.sort(tabComplete);

		return tabComplete;

	}
}