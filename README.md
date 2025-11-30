Kenshins Hide And Seek
======

Highly customizable hide and seek plugin

Quick Start (for server owners)
-----------

1. **Install a compatible server**
   - Use a Spigot or Paper server running Minecraft **1.21.1** (or the version this plugin is built against).

2. **Download or build the plugin**
   - EITHER download a release jar from one of the links below,  
   - OR build it yourself with `mvn clean package` and use the jar from `target/`.

3. **Install the plugin**
   - Place the jar in your server’s `plugins/` folder.
   - Start the server once to generate the plugin’s config and folders, then stop it.

4. **Create a map/world**
   - Either use an existing world or create a dedicated hide‑and‑seek world (via your favorite world manager plugin or the built‑in `/hs world` commands).

5. **Basic map setup**
   - Run `/hs map add <name>` to register a new map.
   - Teleport to the map’s world.
   - Set locations:
     - `/hs map set lobby <name>` – lobby/waiting area.
     - `/hs map set spawn <name>` – main game spawn for hiders.
     - `/hs map set seekerlobby <name>` – seeker start area.
     - `/hs map set bounds <name>` – define play area corners.
     - (Optional) `/hs map set border <name>` – world border behavior.

6. **Enable Block Hunt (optional)**
   - Stand in front of blocks you want to allow and use:
     - `/hs map blockhunt add <name>` – add the looking‑at block.
     - `/hs map blockhunt list <name>` – list configured blocks.

7. **Configure permissions**
   - Give players the basics:
     - `hs.join`, `hs.leave`, `hs.help`, `hs.top`, `hs.wins`.
   - Give admins/staff:
     - `hs.reload`, `hs.start`, `hs.stop`, `hs.map.*`, `hs.world.*`, `hs.send`, `hs.confirm`.

8. **Start a game**
   - Have players join with `/hs join` (or your preferred join command/GUI).
   - When enough players are in the lobby, run `/hs start` (or use the lobby start item if enabled in the config).

9. **Tuning and advanced options**
   - Edit the generated config files in `plugins/KenshinsHideAndSeek/` for timings, items, messages, and map‑specific options.
   - Reload with `/hs reload` after changes.

License
-----------

This project is licensed under the GPL v3 license.

Compilation
-----------

We use maven to handle our dependencies.

* Install [Maven 3](http://maven.apache.org/download.html)
* Clone this repo and: `mvn clean install`
