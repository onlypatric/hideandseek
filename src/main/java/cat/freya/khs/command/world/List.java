package cat.freya.khs.command.world;

import cat.freya.khs.Main;
import cat.freya.khs.configuration.Config;
import cat.freya.khs.configuration.Localization;
import cat.freya.khs.command.util.ICommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class List implements ICommand {

    public void execute(Player sender, String[] args) {
        java.util.List<String> worlds = Main.getInstance().getWorlds();
        if(worlds.isEmpty()) {
            sender.sendMessage(Config.errorPrefix + Localization.message("NO_WORLDS"));
        } else {
            StringBuilder response = new StringBuilder(Config.messagePrefix + Localization.message("LIST_WORLDS"));
            for (String world : worlds) {
                String status = ChatColor.GRAY + "NOT LOADED";
                World bukkit_world = Bukkit.getWorld(world);
                if(bukkit_world != null) {
                    if(bukkit_world.getEnvironment() == World.Environment.NETHER) {
                        status = ChatColor.RED + "NETHER";
                    } else if(bukkit_world.getEnvironment() == World.Environment.THE_END) {
                        status = ChatColor.YELLOW + "END";
                    } else {
                        status = ChatColor.GREEN + "OVERWORLD";
                    }
                }
                response.append("\n    ").append(world).append(": ").append(status).append(ChatColor.WHITE);
            }
            sender.sendMessage(response.toString());
        }
    }

    public String getLabel() {
        return "list";
    }

    public String getUsage() {
        return "";
    }

    public String getDescription() {
        return "List all worlds in the server";
    }

    public java.util.List<String> autoComplete(@NotNull String parameter, @NotNull String typed) {
        return null;
    }

}
