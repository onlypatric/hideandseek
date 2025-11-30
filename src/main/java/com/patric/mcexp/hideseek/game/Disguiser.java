package com.patric.mcexp.hideseek.game;

import static com.patric.mcexp.hideseek.configuration.Config.*;
import static com.patric.mcexp.hideseek.configuration.Localization.message;

import com.patric.mcexp.hideseek.Main;
import com.patric.mcexp.hideseek.game.util.Disguise;
import com.patric.mcexp.hideseek.configuration.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Disguiser {

    private final HashMap<Player, Disguise> disguises;
    private int disguiseTickCounter = 0;

    public Disguiser(){
        this.disguises = new HashMap<>();

    }

    public Disguise getDisguise(Player player){
        return disguises.get(player);
    }

    public boolean disguised(Player player) { return disguises.containsKey(player); }

    @Nullable
    public Disguise getByEntityID(int ID){
        return disguises.values().stream().filter(disguise -> disguise.getEntityID() == ID).findFirst().orElse(null);
    }

    @Nullable
    public Disguise getByHitBoxID(int ID){
        return disguises.values().stream().filter(disguise -> disguise.getHitBoxID() == ID).findFirst().orElse(null);
    }

    public void check(){
        disguiseTickCounter++;
        boolean shouldUpdate = disguiseTickCounter >= disguiseUpdateDelayTicks;
        if (shouldUpdate) {
            disguiseTickCounter = 0;
        }

        Iterator<Map.Entry<Player, Disguise>> iterator = disguises.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Player, Disguise> entry = iterator.next();
            Player player = entry.getKey();
            Disguise disguise = entry.getValue();

            if (!player.isOnline()) {
                disguise.remove();
                iterator.remove();
            } else if (shouldUpdate) {
                disguise.update();
            }
        }
    }

    public void disguise(Player player, Material material, Map map){
        if(!map.isBlockHuntEnabled()){
            player.sendMessage(errorPrefix + message("BLOCKHUNT_DISABLED"));
            return;
        }
        if(disguises.containsKey(player)){
            disguises.get(player).remove();
        }
        Disguise disguise = new Disguise(player, material);
        disguises.put(player, disguise);
        if (debugDisguise) {
            Main.getInstance().getLogger().info(String.format(
                    "[DISGUISE] created for %s material=%s entityId=%d map=%s",
                    player.getName(),
                    material.name(),
                    disguise.getEntityID(),
                    map.getName()
            ));
        }
    }

    public void reveal(Player player){
        if(disguises.containsKey(player))
            disguises.get(player).remove();
        disguises.remove(player);
    }

    public void cleanUp() {
        disguises.values().forEach(Disguise::remove);
    }

}
