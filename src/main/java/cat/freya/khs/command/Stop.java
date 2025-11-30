package cat.freya.khs.command;

import cat.freya.khs.Main;
import cat.freya.khs.command.util.ICommand;
import cat.freya.khs.game.util.Status;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static cat.freya.khs.configuration.Config.abortPrefix;
import static cat.freya.khs.configuration.Config.errorPrefix;
import static cat.freya.khs.configuration.Localization.message;

public class Stop implements ICommand {

	public void execute(Player sender, String[] args) {
		if (Main.getInstance().getGame().checkCurrentMap()) {
			sender.sendMessage(errorPrefix + message("GAME_SETUP"));
			return;
		}
		if (Main.getInstance().getGame().getStatus() == Status.STARTING || Main.getInstance().getGame().getStatus() == Status.PLAYING) {
			Main.getInstance().getGame().broadcastMessage(abortPrefix + message("STOP"));
			Main.getInstance().getGame().end();
		} else {
			sender.sendMessage(errorPrefix + message("GAME_NOT_INPROGRESS"));
		}
	}

	public String getLabel() {
		return "stop";
	}

	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Stops the game";
	}

	public List<String> autoComplete(@NotNull String parameter, @NotNull String typed) {
		return null;
	}

}
