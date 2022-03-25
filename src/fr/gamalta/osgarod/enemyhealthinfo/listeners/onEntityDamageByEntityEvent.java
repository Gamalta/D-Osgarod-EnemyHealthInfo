package fr.gamalta.osgarod.enemyhealthinfo.listeners;

import fr.gamalta.osgarod.enemyhealthinfo.EnemyHealthInfo;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class onEntityDamageByEntityEvent implements Listener {

	EnemyHealthInfo main;

	public onEntityDamageByEntityEvent(EnemyHealthInfo main) {

		this.main = main;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

		Entity damaged = event.getEntity();
		Player player = null;

		if (!damaged.getType().equals(EntityType.ARMOR_STAND)) {

			if (event.getDamager() instanceof Player) {

				player = (Player) event.getDamager();

			} else if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player) {

				player = (Player) ((Projectile) event.getDamager()).getShooter();

			}

			if (player != null) {

				if (player.getUniqueId() == damaged.getUniqueId() || event.getEntity() instanceof Player && player.hasMetadata("NPC")) {
					return;
				}
				if (damaged instanceof LivingEntity) {

					main.sendEnemyInfo(player, (LivingEntity) damaged, ((LivingEntity) damaged).getHealth() - event.getFinalDamage());
				}
			}
		}
	}
}