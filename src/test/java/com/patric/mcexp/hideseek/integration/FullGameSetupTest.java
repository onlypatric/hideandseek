package com.patric.mcexp.hideseek.integration;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import com.patric.mcexp.hideseek.Main;
import com.patric.mcexp.hideseek.configuration.Config;
import com.patric.mcexp.hideseek.configuration.Map;
import com.patric.mcexp.hideseek.configuration.Maps;
import com.patric.mcexp.hideseek.game.util.Status;
import com.patric.mcexp.hideseek.util.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FullGameSetupTest {

    private ServerMock server;
    private Main plugin;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();

        // Ensure basic worlds/directories exist for config + maps
        File worldContainer = server.getWorldContainer();
        new File(worldContainer, "world").mkdirs();
        server.addSimpleWorld("world");

        plugin = MockBukkit.load(Main.class);

        // Avoid file-heavy map save logic during tests
        Config.mapSaveEnabled = false;
        // Simulate a configured exit position
        Config.exitPosition = new Location("world", 0, 64, 0);

        // Create a minimal, valid map tied to the default world
        Map map = new Map("testmap");
        Location spawn = new Location("world", 0, 64, 0);
        map.setSpawn(spawn);
        map.setLobby(spawn);
        map.setSeekerLobby(spawn);
        map.setBoundMin(-10, -10);
        map.setBoundMax(10, 10);
        map.setWorldBorderData(0, 0, 0, 0, 0);
        map.setBlockhunt(false, Collections.emptyList());
        Maps.setMap("testmap", map);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void fullGameSetupAndStartRunsWithoutErrors() {
        // Two players join the lobby
        PlayerMock hider = server.addPlayer("Hider");
        PlayerMock seeker = server.addPlayer("Seeker");
        seeker.setOp(true); // allow /hs start

        // Join via the normal command path
        assertTrue(hider.performCommand("hs join"));
        assertTrue(seeker.performCommand("hs join"));

        // Verify they are tracked on the board
        assertTrue(plugin.getBoard().contains(hider));
        assertTrue(plugin.getBoard().contains(seeker));

        // Start the game
        assertTrue(seeker.performCommand("hs start"));

        // Run a few ticks so scheduled logic executes
        server.getScheduler().performTicks(200);

        // Game should have left standby and be in either STARTING or PLAYING
        Status status = plugin.getGame().getStatus();
        assertFalse(status == Status.STANDBY, "Game should not remain in STANDBY after start");
        assertTrue(status == Status.STARTING || status == Status.PLAYING || status == Status.ENDING);

        // Players should still be tracked in the game
        assertEquals(2, plugin.getBoard().size());
    }
}

