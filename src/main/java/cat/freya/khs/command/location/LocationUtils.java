package cat.freya.khs.command.location;

import cat.freya.khs.Main;
import cat.freya.khs.configuration.Config;
import cat.freya.khs.configuration.Localization;
import cat.freya.khs.configuration.Map;
import cat.freya.khs.configuration.Maps;
import cat.freya.khs.game.util.Status;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * @author bobby29831
 */
public class LocationUtils {

    public static void setLocation(@NotNull Player player, @NotNull Locations place, String mapName, @NotNull Consumer<Map> consumer) {

        if (Main.getInstance().getGame().getStatus() != Status.STANDBY) {
            player.sendMessage(Config.errorPrefix + Localization.message("GAME_INPROGRESS"));
            return;
        }

        if (player.getLocation().getBlockX() == 0 || player.getLocation().getBlockZ() == 0 || player.getLocation().getBlockY() == 0){
            player.sendMessage(Config.errorPrefix + Localization.message("NOT_AT_ZERO"));
            return;
        }

        Map map = null;
        if(mapName != null) {
            map = Maps.getMap(mapName);
            if (map == null) {
                player.sendMessage(Config.errorPrefix + Localization.message("INVALID_MAP"));
                return;
            }
        }

        try {
            consumer.accept(map);
        } catch (Exception e) {
            player.sendMessage(Config.errorPrefix + e.getMessage());
            return;
        }

        if(map != null)
            Maps.setMap(mapName, map);
        player.sendMessage(Config.messagePrefix + Localization.message(place.message()));
    }

}