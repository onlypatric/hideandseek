package com.patric.mcexp.hideseek.game.listener;

import com.google.common.collect.Sets;
import com.patric.mcexp.hideseek.game.listener.events.PlayerJumpEvent;
import com.patric.mcexp.hideseek.Main;
import com.patric.mcexp.hideseek.configuration.Map;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Set;
import java.util.UUID;

public class MovementHandler implements Listener {

    private final Set<UUID> prevPlayersOnGround = Sets.newHashSet();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent event) {

        if (event.getTo() == null || event.getTo().getWorld() == null) return;
        checkJumping(event);
        checkBounds(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJump(PlayerJumpEvent event) {
        if(Main.getInstance().getBoard().isSpectator(event.getPlayer()) && event.getPlayer().getAllowFlight()) {
            event.getPlayer().setFlying(true);
        }
    }

    private void checkJumping(PlayerMoveEvent event){
        if (event.getPlayer().getVelocity().getY() > 0) {
            if (event.getPlayer().getLocation().getBlock().getType() != Material.LADDER && prevPlayersOnGround.contains(event.getPlayer().getUniqueId())) {
                if (!event.getPlayer().isOnGround()) {
                    Main.getInstance().getServer().getPluginManager().callEvent(new PlayerJumpEvent(event.getPlayer()));
                }
            }
        }
        if (event.getPlayer().isOnGround()) {
            prevPlayersOnGround.add(event.getPlayer().getUniqueId());
        } else {
            prevPlayersOnGround.remove(event.getPlayer().getUniqueId());
        }
    }

    private void checkBounds(PlayerMoveEvent event){
        if (!Main.getInstance().getBoard().contains(event.getPlayer())) return;
        Map currentMap = Main.getInstance().getGame().getCurrentMap();
        if (currentMap == null || currentMap.isNotSetup()) return;
        String gameWorld = currentMap.getGameSpawnName();
        if (!event.getPlayer().getWorld().getName().equals(gameWorld)) return;
        if (!event.getTo().getWorld().getName().equals(gameWorld)) return;
        if (event.getPlayer().hasPermission("hs.leavebounds")) return;
        if (event.getTo().getBlockX() < currentMap.getBoundsMin().getBlockX()
                || event.getTo().getBlockX() > currentMap.getBoundsMax().getBlockX()
                || event.getTo().getBlockZ() < currentMap.getBoundsMin().getZ()
                || event.getTo().getBlockZ() > currentMap.getBoundsMax().getZ()) {
            event.setCancelled(true);
        }
    }

}
