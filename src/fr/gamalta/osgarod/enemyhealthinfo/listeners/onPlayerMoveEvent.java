package fr.gamalta.osgarod.enemyhealthinfo.listeners;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import fr.gamalta.osgarod.enemyhealthinfo.EnemyHealthInfo;

public class onPlayerMoveEvent implements Listener {

	EnemyHealthInfo main;

	public onPlayerMoveEvent(EnemyHealthInfo main) {
		this.main = main;
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {

		Player player = event.getPlayer();
		HashMap<LivingEntity, Double> scalaires = new HashMap<>();

		for (Entity entity : player.getNearbyEntities(main.ray, main.ray, main.ray)) {

			if (entity instanceof LivingEntity && !entity.getType().equals(EntityType.ARMOR_STAND)) {

				LivingEntity livingEntity = (LivingEntity) entity;
				Location mobLoc = livingEntity.getLocation();
				mobLoc.setY((mobLoc.getY() + livingEntity.getEyeLocation().getY()) / 2);
				Location playerLoc = player.getEyeLocation();
				Vector d1 = new Vector(mobLoc.getX() - playerLoc.getX(), mobLoc.getY() - playerLoc.getY(), mobLoc.getZ() - playerLoc.getZ()).normalize();
				Vector d2 = playerLoc.getDirection();
				double scalaire = d1.getX() * d2.getX() + d1.getY() * d2.getY() + d1.getZ() * d2.getZ();

				if (scalaire > main.settingsCFG.getDouble("EnemyHealthInfo.Scalaire")) {

					World world = player.getWorld();
					boolean hasBlock = false;
					double distance = playerLoc.distance(mobLoc);
					double x = playerLoc.getX();
					double y = playerLoc.getY();
					double z = playerLoc.getZ();
					double xt = d1.getX();
					double yt = d1.getY();
					double zt = d1.getZ();
					int xe = (int) x;
					int ye = (int) y;
					int ze = (int) z;

					for (double i = 0; i < distance; i += 0.1) {

						double xn = x + xt * i;
						double yn = y + yt * i;
						double zn = z + zt * i;
						if (xe != (int) xn || ye != (int) yn || ze != (int) zn) {

							xe = (int) xn;
							ye = (int) yn;
							ze = (int) zn;

							Location location = new Location(world, xn, yn, zn);

							if (!main.materials.contains(world.getBlockAt(location).getType())) {

								hasBlock = true;
								break;
							}
						}
					}

					if (!hasBlock) {

						scalaires.put(livingEntity, scalaire);
					}
				}
			}
		}

		LivingEntity livingEntity = null;
		Double scalaire = -1D;

		for (Entry<LivingEntity, Double> entry : scalaires.entrySet()) {

			if (entry.getValue() > scalaire) {

				livingEntity = entry.getKey();
				scalaire = entry.getValue();

			}
		}

		if (livingEntity != null) {

			main.sendEnemyInfo(player, livingEntity, livingEntity.getHealth());
		}
	}
}