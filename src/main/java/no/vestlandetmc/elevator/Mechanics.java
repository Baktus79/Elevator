package no.vestlandetmc.elevator;

import no.vestlandetmc.elevator.config.Config;
import no.vestlandetmc.elevator.handler.MessageHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

public class Mechanics {

	public static double tpCoordinate;

	public static boolean detectBlockUp(Player player, World world, Material m) {
		if (standOnBlock(player, world, m)) {
			return elevatorBlockExistUp(player, world);
		}

		return false;
	}

	public static boolean detectBlockDown(Player player, World world, Material m) {
		if (standOnBlock(player, world, m)) {
			return elevatorBlockExistDown(player, world);
		}

		return false;
	}

	public static boolean standOnBlock(Player player, World world, Material m) {
		return world.getBlockAt(player.getLocation().add(0.0D, -1.0D, 0.0D)).getType() == m;
	}

	private static boolean elevatorBlockExistUp(Player player, World world) {
		final int distance = Config.BLOCK_DISTANCE;
		for (double y = 2; y <= distance; y++) {
			if (world.getBlockAt(player.getLocation().add(0.0D, y, 0.0D)).getType() == Config.BLOCK_TYPE) {
				if (dangerBlock(player, world, y)) {
					MessageHandler.sendAction(player, Config.ELEVATOR_LOCALE_DANGER);
					return false;
				} else {
					tpCoordinate = y;
					return true;
				}
			}
		}

		return false;
	}

	private static boolean elevatorBlockExistDown(Player player, World world) {
		final int distance = -Config.BLOCK_DISTANCE;
		for (double y = -2; y >= distance; y--) {
			if (world.getBlockAt(player.getLocation().add(0.0D, y, 0.0D)).getType() == Config.BLOCK_TYPE) {
				if (dangerBlock(player, world, y)) {
					MessageHandler.sendAction(player, Config.ELEVATOR_LOCALE_DANGER);
					return false;
				} else {
					tpCoordinate = y;
					return true;
				}
			}
		}

		return false;
	}

	private static boolean dangerBlock(Player player, World world, double y) {
		final Material getBlock1 = world.getBlockAt(player.getLocation().add(0.0D, (y + 1), 0.0D)).getType();
		final Material getBlock2 = world.getBlockAt(player.getLocation().add(0.0D, (y + 2), 0.0D)).getType();

		if (getBlock1.name().endsWith("SIGN") || getBlock2.name().endsWith("SIGN")) {
			return false;
		}

		return getBlock1.isSolid() || getBlock1 == Material.LAVA || getBlock2.isSolid() || getBlock2 == Material.LAVA;

	}

	public static boolean dangerBlock(Location loc) {
		final Material getBlock1 = loc.getWorld().getBlockAt(loc).getType();
		final Material getBlock2 = loc.getWorld().getBlockAt(loc.add(0.0D, 1.0D, 0.0D)).getType();

		if (getBlock1.name().endsWith("SIGN") || getBlock2.name().endsWith("SIGN")) {
			return false;
		}

		return getBlock1.isSolid() || getBlock1 == Material.LAVA || getBlock2.isSolid() || getBlock2 == Material.LAVA;

	}

	public static boolean blockExistClose(BlockPlaceEvent e) {
		final World w = e.getPlayer().getWorld();
		return w.getBlockAt(e.getBlockPlaced().getLocation().add(0.0D, +1.0D, 0.0D)).getType() != Config.BLOCK_TYPE &&
				w.getBlockAt(e.getBlockPlaced().getLocation().add(0.0D, +2.0D, 0.0D)).getType() != Config.BLOCK_TYPE &&
				w.getBlockAt(e.getBlockPlaced().getLocation().add(0.0D, -1.0D, 0.0D)).getType() != Config.BLOCK_TYPE &&
				w.getBlockAt(e.getBlockPlaced().getLocation().add(0.0D, -2.0D, 0.0D)).getType() != Config.BLOCK_TYPE;
	}

	public static void particles(Player player, Location loc) {
		if (Config.PARTICLE_ENABLED) {
			player.getWorld().spawnParticle(Config.PARTICLE_TYPE, loc, Config.PARTICLE_COUNT);
		}
	}

	public static void teleportUp(Player player) {
		player.teleport(player.getLocation().add(0.0D, (Mechanics.tpCoordinate + 1), 0.0D));
		player.playSound(player.getLocation(), "minecraft:" + Config.TP_SOUND, 1.0F, 0.7F);
	}

	public static void teleportDown(Player player) {
		player.teleport(player.getLocation().add(0.0D, (Mechanics.tpCoordinate + 1), 0.0D));
		player.playSound(player.getLocation(), "minecraft:" + Config.TP_SOUND, 1.0F, 0.7F);
	}
}
