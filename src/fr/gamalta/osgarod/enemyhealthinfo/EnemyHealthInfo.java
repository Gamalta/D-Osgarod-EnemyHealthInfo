package fr.gamalta.osgarod.enemyhealthinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import fr.gamalta.osgarod.enemyhealthinfo.commands.EnemyHealthInfoCmd;
import fr.gamalta.osgarod.enemyhealthinfo.listeners.onEntityDamageByEntityEvent;
import fr.gamalta.osgarod.enemyhealthinfo.listeners.onPlayerMoveEvent;
import fr.gamalta.osgarod.enemyhealthinfo.utils.Configuration;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class EnemyHealthInfo extends JavaPlugin {

	public Configuration settingsCFG = new Configuration(this, "EnemyHealthInfo", "Settings");
	public Configuration playersCFG = new Configuration(this, "EnemyHealthInfo", "Players");
	private Map<Player, BossBar> players = new HashMap<>();
	private Map<Player, Long> time = new HashMap<>();
	public HashSet<Material> materials = new HashSet<>();
	public int ray = 0;

	@Override
	public void onEnable() {

		settingsCFG.loadConfig();
		playersCFG.loadConfig();
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new onEntityDamageByEntityEvent(this), this);
		pm.registerEvents(new onPlayerMoveEvent(this), this);
		getCommand("EnemyHealthInfo").setExecutor(new EnemyHealthInfoCmd(this));
		getCommand("EnemyHealthInfo").setTabCompleter(new EnemyHealthInfoCmd(this));

		materials.add(Material.AIR);

		for (String string : settingsCFG.getStringList("EnemyHealthInfo.TransparentBlock")) {

			materials.add(Material.getMaterial(string));

		}

		ray = settingsCFG.getInt("EnemyHealthInfo.Radius");
	}

	public void sendEnemyInfo(Player player, LivingEntity entity, double health) {

		if (!entity.hasPotionEffect(PotionEffectType.INVISIBILITY) && !(entity instanceof EnderDragon) && !(entity instanceof Wither)) {
			if (!entity.isInvulnerable()) {
				if (entity instanceof Player && !player.canSee((Player) entity)) {
					if (((Player) entity).getGameMode().equals(GameMode.CREATIVE) || ((Player) entity).getGameMode().equals(GameMode.SPECTATOR)) {
						return;
					}
				}

				if (playersCFG.contains("EnemyHealthInfo." + player.getName() + ".ActionBar") ? playersCFG.getBoolean("EnemyHealthInfo." + player.getName() + ".ActionBar") : settingsCFG.getBoolean("EnemyHealthInfo.ActionBar.Default")) {

					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', getActionBar(health, player, entity))));

				}

				if (playersCFG.contains("EnemyHealthInfo." + player.getName() + ".BossBar") ? playersCFG.getBoolean("EnemyHealthInfo." + player.getName() + ".BossBar") : settingsCFG.getBoolean("EnemyHealthInfo.BossBar.Default")) {

					long instanceTime = System.currentTimeMillis() / 1000;
					time.put(player, instanceTime);

					if (players.containsKey(player)) {

						BossBar bossBar = players.get(player);
						BossBar newBossBar = getBossBar(health, player, entity);
						bossBar.setTitle(newBossBar.getTitle());
						bossBar.setProgress(newBossBar.getProgress());
						bossBar.setColor(newBossBar.getColor());
						bossBar.setStyle(newBossBar.getStyle());
						bossBar.setVisible(true);

					} else {

						BossBar bossBar = getBossBar(health, player, entity);
						players.put(player, bossBar);
						bossBar.addPlayer(player);
					}

					Bukkit.getScheduler().runTaskLater(this, (Runnable) () -> {

						if (instanceTime == time.get(player)) {

							players.get(player).setVisible(false);
						}
					}, 40);
				}
			}
		}
	}

	public String getActionBar(double health, Player player, LivingEntity entity) {

		if (health < 0.0 || entity.isDead()) {
			health = 0.0;
		}

		String name = entity.getCustomName() == null ? entity.getName() : entity.getCustomName();
		String actionbar = settingsCFG.getString("EnemyHealthInfo.Entity.Default.ActionBar");
		double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

		double live = 0;

		if (health % 1 == 0) {

			live = (int) health;

		} else {

			live = (int) health + 1;

		}

		live = live / 2;

		Object obj = settingsCFG.get("EnemyHealthInfo.Entity." + entity.getName());

		if (obj instanceof String) {

			name = (String) obj;

		} else if (obj instanceof ConfigurationSection) {

			ConfigurationSection section = (ConfigurationSection) obj;

			if (section.contains("Name")) {

				name = section.getString("Name");

			}

			if (section.contains("ActionBar")) {

				actionbar = section.getString("ActionBar");
			}
		}

		actionbar = actionbar.replace("%name%", name);
		actionbar = actionbar.replace("%health%", "" + live);
		actionbar = actionbar.replace("%maxhealth%", "" + maxHealth / 2);
		actionbar = actionbar.replace(".0", "");

		if (actionbar.contains("%usestyle%")) {

			String style = "";

			for (int i = 0; i < maxHealth / 2; i++) {

				if (live >= 1) {

					style = style + settingsCFG.getString("EnemyHealthInfo.HealthIcon.Full");
					live--;

				} else if (live == 0.5) {

					style = style + settingsCFG.getString("EnemyHealthInfo.HealthIcon.Half");
					live = 0;

				} else {

					style = style + settingsCFG.getString("EnemyHealthInfo.HealthIcon.Empty");
				}

			}

			actionbar = actionbar.replace("%usestyle%", style);
		}
		return actionbar;
	}

	public BossBar getBossBar(double health, Player player, LivingEntity entity) {

		if (health < 0.0 || entity.isDead()) {
			health = 0.0;
		}

		String name = entity.getCustomName() == null ? entity.getName() : entity.getCustomName();
		String title = settingsCFG.getString("EnemyHealthInfo.Entity.Default.BossBar");
		double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		BarColor color = BarColor.RED;
		BarStyle style = BarStyle.SEGMENTED_10;
		BarFlag[] flags = new BarFlag[] {};

		double live = 0;

		if (health % 1 == 0) {

			live = (int) health;

		} else {

			live = (int) health + 1;

		}

		live = live / 2;

		Object obj = settingsCFG.get("EnemyHealthInfo.Entity." + entity.getName());

		if (obj instanceof String) {

			name = (String) obj;

		} else if (obj instanceof ConfigurationSection) {

			ConfigurationSection section = (ConfigurationSection) obj;

			if (section.contains("Name")) {

				name = ((ConfigurationSection) obj).getString("Name");
			}

			if (section.contains("Color")) {

				color = BarColor.valueOf(section.getString("Color"));
			}

			if (section.contains("Style")) {

				style = BarStyle.valueOf(section.getString("Style"));
			}

			if (section.contains("Flags")) {

				Object flag = section.get("Flags");

				if (flag instanceof String) {

					flags = new BarFlag[] {
							BarFlag.valueOf(section.getString("Flags")) };

				} else if (flag instanceof List<?> && !((List<?>) flag).isEmpty() && ((List<?>) flag).get(0) instanceof String) {

					List<BarFlag> flagList = new ArrayList<>();

					for (String string : section.getStringList("Flags")) {

						flagList.add(BarFlag.valueOf(string));

					}

					flags = (BarFlag[]) flagList.toArray();

				}
			}

			if (section.contains("BossBar")) {

				title = section.getString("BossBar");
			}
		}

		title = title.replace("%name%", name);
		title = title.replace("%health%", "" + live);
		title = title.replace("%maxhealth%", "" + maxHealth / 2);

		BossBar bossBar = Bukkit.getServer().createBossBar(ChatColor.translateAlternateColorCodes('&', title), color, style, flags);
		bossBar.setVisible(true);
		bossBar.setProgress(health / maxHealth);

		return bossBar;
	}
}