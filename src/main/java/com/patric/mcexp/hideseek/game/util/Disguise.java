package com.patric.mcexp.hideseek.game.util;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.ActionBar;
import com.patric.mcexp.hideseek.util.packet.BlockChangePacket;
import com.patric.mcexp.hideseek.Main;
import com.patric.mcexp.hideseek.configuration.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Disguise {

    final Player hider;
    final Material material;
    AbstractHorse hitBox;
    BlockDisplay display;
    Location blockLocation;
    boolean solid, solidify, solidifying;
    static Team hidden;

    static {
        if(Main.getInstance().supports(9)) {
            Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
            hidden = board.getTeam("KHS_Collision");
            if (hidden == null) {
                hidden = board.registerNewTeam("KHS_Collision");
            }
            hidden.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            hidden.setCanSeeFriendlyInvisibles(false);
        }
    }

    public Disguise(Player player, Material material){
        this.hider = player;
        this.material = material;
        this.solid = false;
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 0,false, false));
        if(Main.getInstance().supports(9)) {
            hidden.addEntry(player.getName());
        }
    }

    public void remove(){
        if(hitBox != null){
            if(Main.getInstance().supports(9))
                hidden.removeEntry(hitBox.getUniqueId().toString());
            hitBox.remove();
        }
        if (display != null) {
            display.remove();
        }
        if(solid)
            sendBlockUpdate(blockLocation, Material.AIR);
        hider.removePotionEffect(PotionEffectType.INVISIBILITY);
        if(Main.getInstance().supports(9)) {
            hidden.removeEntry(hider.getName());
        }
    }

    public int getEntityID() {
        if(display == null) return -1;
        return display.getEntityId();
    }

    public int getHitBoxID() {
        if(hitBox == null) return -1;
        return hitBox.getEntityId();
    }

    public Player getPlayer() {
        return hider;
    }

    public void update(){
        // Ensure the moving BlockDisplay exists
        if (display == null || display.isDead()) {
            if (display != null) display.remove();
            respawnBlockDisplay();
        }

        if (solidify) {
            if (!solid) {
                solid = true;
                Location loc = hider.getLocation();
                // Snap to the exact block the player is currently standing in
                blockLocation = new Location(
                        loc.getWorld(),
                        loc.getBlockX(),
                        loc.getBlockY(),
                        loc.getBlockZ()
                );
                respawnHitbox();
                // Snap the BlockDisplay directly to the solid block position for the hider
                if (display != null) {
                    display.teleport(blockLocation);
                }
            }
            sendBlockUpdate(blockLocation, material);
        } else if(solid){
            solid = false;
            if(Main.getInstance().supports(9))
                hidden.removeEntry(hitBox.getUniqueId().toString());
            hitBox.remove();
            hitBox = null;
            sendBlockUpdate(blockLocation, Material.AIR);
        }
        // Only move BlockDisplay while not solid; keep it visible only to the hider when solid
        if (display != null) {
            toggleEntityVisibility(display, !solid);
            if (!solid) {
                teleportEntity(display, true);
            }
        }
        // Keep the hitbox fixed at the solid block position; do not follow
        // the player while they are moving/unsolid to avoid pushing them.
        if (hitBox != null && solid && blockLocation != null) {
            hitBox.teleport(blockLocation);
        }

        if (Config.debugDisguise && display != null) {
            Location bLoc = display.getLocation();
            Location hLoc = hider.getLocation();
            Main.getInstance().getLogger().info(String.format(
                    "[DISGUISE] solid=%s blockId=%d blockLoc=(%.2f,%.2f,%.2f) hiderLoc=(%.2f,%.2f,%.2f)",
                    solid,
                    display.getEntityId(),
                    bLoc.getX(), bLoc.getY(), bLoc.getZ(),
                    hLoc.getX(), hLoc.getY(), hLoc.getZ()
            ));
        }
    }

    public void setSolidify(boolean value){
        this.solidify = value;
    }

    private void sendBlockUpdate(Location location, Material material){
        BlockChangePacket packet = new BlockChangePacket();
        packet.setBlockPosition(location);
        packet.setMaterial(material);
        Bukkit.getOnlinePlayers().forEach(receiver -> {
            // Do not send the fake solid block to the hider themselves,
            // otherwise their client will think they are inside a block and nudge them.
            if (receiver.equals(hider)) return;
            packet.send(receiver);
        });
    }

    private void teleportEntity(Entity entity, boolean center) {
        if (entity == null) return;
        double x, y, z;
        if (center) {
            x = hider.getLocation().getX() - 0.5;
            y = hider.getLocation().getY();
            z = hider.getLocation().getZ() - 0.5;
        } else {
            x = hider.getLocation().getX();
            y = hider.getLocation().getY();
            z = hider.getLocation().getZ();
        }
        org.bukkit.Location current = entity.getLocation();
        // Keep the block's visual rotation stable and independent of the player's look direction
        org.bukkit.Location target = new org.bukkit.Location(
                current.getWorld(),
                x,
                y,
                z,
                0.0f,
                0.0f
        );
        entity.teleport(target);
    }

    private void toggleEntityVisibility(Entity entity, boolean show){
        if(entity == null) return;
        Bukkit.getOnlinePlayers().forEach(receiver -> {
            if(receiver == hider) return;
            if(show)
                Main.getInstance().getEntityHider().showEntity(receiver, entity);
            else
                Main.getInstance().getEntityHider().hideEntity(receiver, entity);
        });
    }

    private void respawnBlockDisplay() {
        Location base = hider.getLocation().add(-0.5, 1000, -0.5);
        Location spawn = new Location(base.getWorld(), base.getX(), base.getY(), base.getZ(), 0.0f, 0.0f);
        Entity spawned = hider.getWorld().spawnEntity(spawn, EntityType.BLOCK_DISPLAY);
        if (spawned instanceof BlockDisplay) {
            display = (BlockDisplay) spawned;
            display.setBlock(material.createBlockData());
            display.setInvulnerable(true);
            try {
                display.setGravity(false);
            } catch (NoSuchMethodError ignored) {
                // Gravity control not available on older APIs
            }
        }
    }

    private void respawnHitbox(){
        // Spawn the invisible hitbox exactly where the solid block will be
        // so seekers hit the block position rather than a moving entity.
        Location spawnLocation = blockLocation != null
                ? blockLocation.clone()
                : hider.getLocation().add(0, 1000, 0);
        if (Main.getInstance().supports(11)) {
            hitBox = (AbstractHorse) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.SKELETON_HORSE);
        } else {
            hitBox = (AbstractHorse) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.HORSE);
            hitBox.setVariant(Horse.Variant.SKELETON_HORSE);
        }
        if (Main.getInstance().supports(10)) {
            hitBox.setGravity(false);
        }
        hitBox.setAI(false);
        hitBox.setInvulnerable(true);
        hitBox.setCanPickupItems(false);
        hitBox.setCollidable(false);
        hitBox.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 0,false, false));
        if(Main.getInstance().supports(9)){
            hidden.addEntry(hitBox.getUniqueId().toString());
        }
    }

    public void startSolidifying() {
        if (solidifying) return;
        if (solid) return;
        solidifying = true;
        final Location lastLocation = hider.getLocation();
        // Start countdown immediately; 60 ticks ≈ 3 seconds, checked every 5 ticks
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> solidifyUpdate(lastLocation, 60), 0);
    }

    private void solidifyUpdate(Location lastLocation, int remainingTicks) {
        Location currentLocation = hider.getLocation();
        if(lastLocation.getWorld() != currentLocation.getWorld()) {
            solidifying = false;
            return;
        }
        if(lastLocation.distance(currentLocation) > .1) {
            solidifying = false;
            // Restart countdown from the new position so players don't
            // need to jiggle their camera to re-trigger solidification.
            startSolidifying();
            return;
        }
        if(remainingTicks <= 0) {
            ActionBar.clearActionBar(hider);
            setSolidify(true);
            solidifying = false;
        } else {
            // Show a numeric countdown with color: 3 (green), 2 (gold), 1 (red)
            int secondsRemaining = (int) Math.ceil(remainingTicks / 20.0);
            if (secondsRemaining < 1) secondsRemaining = 1;
            ChatColor color;
            switch (secondsRemaining) {
                case 3:
                    color = ChatColor.GREEN;
                    break;
                case 2:
                    color = ChatColor.GOLD;
                    break;
                default:
                    color = ChatColor.RED;
                    break;
            }
            ActionBar.sendActionBar(hider, color + Integer.toString(secondsRemaining));
            // Keep the sound roughly once per second
            if (remainingTicks % 20 == 0) {
                XSound.BLOCK_NOTE_BLOCK_PLING.play(hider, 1, 1);
            }
            // Check movement more frequently for responsiveness
            int interval = 5;
            Bukkit.getScheduler().scheduleSyncDelayedTask(
                    Main.getInstance(),
                    () -> solidifyUpdate(lastLocation, remainingTicks - interval),
                    interval
            );
        }
    }

}
