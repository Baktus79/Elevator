package no.vestlandetmc.elevator.Listener;

import no.vestlandetmc.elevator.Mechanics;
import no.vestlandetmc.elevator.config.Config;
import no.vestlandetmc.elevator.handler.*;
import no.vestlandetmc.elevator.hooks.GriefDefenderHook;
import no.vestlandetmc.elevator.hooks.GriefPreventionHook;
import no.vestlandetmc.elevator.hooks.WorldGuardHook;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class ElevatorListener implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent e) {
		if (!e.getPlayer().isOnGround() && e.getPlayer().getVelocity().getY() > 0.0D) {
			final Location tpLocation = Mechanics.getElevatorLocationUp(e.getPlayer());

			if (tpLocation != null) {
				if (!e.getPlayer().hasPermission("elevator.use")) {
					return;
				}

				if (Config.COOLDOWN_ENABLED) {
					if (Cooldown.elevatorUsed(e.getPlayer())) {
						return;
					}
				}

				if (GriefPreventionHook.gpHook) {
					if (!GPHandler.haveTrust(e.getPlayer())) return;
				}
				if (WorldGuardHook.wgHook) {
					if (!WGHandler.haveTrust(e.getPlayer())) return;
				}
				if (GriefDefenderHook.gdHook) {
					if (!GDHandler.haveTrust(e.getPlayer())) return;
				}

				Mechanics.teleport(e.getPlayer(), tpLocation);
				MessageHandler.sendAction(e.getPlayer(), Config.ELEVATOR_LOCALE_UP);
				Mechanics.setParticles(e.getPlayer());
			}
		}
	}

	@EventHandler
	public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent e) {
		if (!e.getPlayer().isSneaking()) {
			final Location tpLocation = Mechanics.getElevatorLocationDown(e.getPlayer());

			if (tpLocation != null) {
				if (!e.getPlayer().hasPermission("elevator.use")) {
					return;
				}
				if (Config.COOLDOWN_ENABLED) {
					if (Cooldown.elevatorUsed(e.getPlayer())) {
						return;
					}
				}

				if (GriefPreventionHook.gpHook) {
					if (!GPHandler.haveTrust(e.getPlayer())) return;
				}
				if (WorldGuardHook.wgHook) {
					if (!WGHandler.haveTrust(e.getPlayer())) return;
				}
				if (GriefDefenderHook.gdHook) {
					if (!GDHandler.haveTrust(e.getPlayer())) return;
				}

				Mechanics.teleport(e.getPlayer(), tpLocation);
				MessageHandler.sendAction(e.getPlayer(), Config.ELEVATOR_LOCALE_DOWN);
				Mechanics.setParticles(e.getPlayer());
			}
		}
	}

	@EventHandler
	public void BlockPlaceEvent(BlockPlaceEvent e) {
		final World w = e.getPlayer().getWorld();

		if (blockExistClose(e)) {
			if (e.getBlockPlaced().getType() == Config.BLOCK_TYPE) {
				if (!e.getPlayer().hasPermission("elevator.use")) {
					return;
				}
				for (double y = 50.0D; y > -51.0D; y--) {
					if (y + e.getBlockPlaced().getLocation().getY() > e.getBlockPlaced().getLocation().getY() + 2.0D ||
							y + e.getBlockPlaced().getLocation().getY() < e.getBlockPlaced().getLocation().getY() - 2.0D) {
						if (w.getBlockAt(e.getBlockPlaced().getLocation().add(0.0D, y, 0.0D)).getType() == Config.BLOCK_TYPE) {
							MessageHandler.sendAction(e.getPlayer(), Config.ELEVATOR_LOCALE_ACTIVATED);
							e.getPlayer().playSound(e.getPlayer().getLocation(), "minecraft:" + Config.SOUND_ACTIVATED, 1.0F, 1.0F);
							break;
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void playerJoin(PlayerJoinEvent p) {
		final Player player = p.getPlayer();

		if (player.isOp()) {
			if (UpdateNotification.isUpdateAvailable()) {
				MessageHandler.sendMessage(player, "&a------------------------------------");
				MessageHandler.sendMessage(player, "&aElevator is outdated. Update is available!");
				MessageHandler.sendMessage(player, "&aYour version is &a&l " + UpdateNotification.getCurrentVersion() + " &aand can be updated to version &a&l" + UpdateNotification.getLatestVersion());
				MessageHandler.sendMessage(player, "&aGet the new update at https://www.spigotmc.org/resources/" + UpdateNotification.getProjectId());
				MessageHandler.sendMessage(player, "&a------------------------------------");
			}
		}
	}

	private boolean blockExistClose(BlockPlaceEvent e) {
		final World w = e.getPlayer().getWorld();
		return w.getBlockAt(e.getBlockPlaced().getLocation().add(0.0D, +1.0D, 0.0D)).getType() != Config.BLOCK_TYPE &&
				w.getBlockAt(e.getBlockPlaced().getLocation().add(0.0D, +2.0D, 0.0D)).getType() != Config.BLOCK_TYPE &&
				w.getBlockAt(e.getBlockPlaced().getLocation().add(0.0D, -1.0D, 0.0D)).getType() != Config.BLOCK_TYPE &&
				w.getBlockAt(e.getBlockPlaced().getLocation().add(0.0D, -2.0D, 0.0D)).getType() != Config.BLOCK_TYPE;
	}
}
