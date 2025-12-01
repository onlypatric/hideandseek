import mineflayer from 'mineflayer';

const BOT_NAME = process.env.CI_BOT_NAME || 'CiBot1';
const HOST = process.env.CI_BOT_HOST || '127.0.0.1';
const PORT = Number(process.env.CI_BOT_PORT || 25565);

function createBot(name, isLeader) {
  console.log(`[BOT] Starting bot "${name}" targeting ${HOST}:${PORT}`);

  const bot = mineflayer.createBot({
    host: HOST,
    port: PORT,
    username: name,
    version: false
  });

  bot.on('login', () => {
    console.log('[BOT] Logged in as', name);
  });

  bot.on('spawn', () => {
    console.log(`[BOT ${name}] Spawned at`, bot.entity.position);
    console.log(`[BOT ${name}] Sending /hs join`);
    bot.chat('/hs join');

    if (isLeader) {
      setTimeout(() => {
        const targetSeeker = `${BOT_NAME}_2`;
        console.log(`[BOT ${name}] Sending /hs start ${targetSeeker}`);
        bot.chat(`/hs start ${targetSeeker}`);
      }, 5000);

      // After hiding should be over, move around a bit as a disguised block
      setTimeout(() => {
        console.log(`[BOT ${name}] Moving forward to simulate block movement`);
        bot.setControlState('forward', true);
        setTimeout(() => {
          bot.setControlState('forward', false);
          console.log(`[BOT ${name}] Stopped moving`);
        }, 5000);
      }, 15000);

      // Mid-game: use the block-change wand to reopen the selector and pick a new block
      setTimeout(() => {
        try {
          const slots = bot.inventory.slots;
          let wandSlot = null;
          for (let i = 0; i < slots.length; i++) {
            const item = slots[i];
            if (!item) continue;
            // In CI config the wand is a BLAZE_ROD; match by item name
            if (item.name && item.name.toLowerCase().includes('blaze_rod')) {
              wandSlot = i;
              break;
            }
          }
          if (wandSlot == null) {
            console.log(`[BOT ${name}] Could not find block-change wand in inventory`);
          } else {
            const hotbarIndex = wandSlot % 9;
            console.log(`[BOT ${name}] Using block-change wand from slot ${hotbarIndex}`);
            bot.setQuickBarSlot(hotbarIndex);
            bot.activateItem(); // right-click with held item
          }
        } catch (e) {
          console.error(`[BOT ${name}] Error while using block-change wand:`, e);
        }
      }, 12000);
    } else {
      // Seeker periodically attacks nearest player to trigger hit logic
      setInterval(() => {
        const target = bot.nearestEntity((e) => e.type === 'player' && e.username !== name);
        if (target) {
          console.log(`[BOT ${name}] Attacking ${target.username || target.name}`);
          try {
            bot.attack(target);
          } catch (e) {
            console.error(`[BOT ${name}] Error during attack:`, e);
          }
        }
      }, 5000);
    }

    setTimeout(() => {
      console.log(`[BOT ${name}] Sending /hs leave`);
      bot.chat('/hs leave');
    }, 20000);

    setTimeout(() => {
      console.log(`[BOT ${name}] Finished test window, quitting`);
      bot.quit();
    }, 30000);
  });

  bot.on('move', () => {
    // Log coarse-grained movement occasionally
    if (Math.random() < 0.01) {
      console.log(`[BOT ${name}] Position`, bot.entity.position);
    }
  });

  bot.on('message', (message) => {
    console.log('[BOT][CHAT]', message.toString());
  });

  let blockSelectionCount = 0;

  bot.on('windowOpen', (window) => {
    const rawTitle = window?.title;
    const title =
      typeof rawTitle === 'string'
        ? rawTitle
        : rawTitle
          ? JSON.stringify(rawTitle)
          : '';
    console.log(`[BOT ${name}] Window opened with title: ${title}`);

    // In CI, the only meaningful GUI we expect is the BlockHunt picker.
    // First time: click slot 0; second time (after using the wand): click slot 1 to change block.
    if (title.includes('Select a Block:')) {
      blockSelectionCount += 1;
      const slotToClick = blockSelectionCount === 1 ? 0 : 1;
      console.log(`[BOT ${name}] Attempting to click window slot ${slotToClick}`);
      setTimeout(() => {
        try {
          bot.clickWindow(slotToClick, 0, 0, (err) => {
            if (err) {
              console.error(`[BOT ${name}] Error clicking window slot ${slotToClick}:`, err);
            } else {
              console.log(`[BOT ${name}] Clicked window slot ${slotToClick}`);
            }
          });
        } catch (e) {
          console.error(`[BOT ${name}] Exception while clicking window slot ${slotToClick}:`, e);
        }
      }, 1000);
    }
  });

  bot.on('kicked', (reason, loggedIn) => {
    console.log(`[BOT ${name}] Kicked. loggedIn=`, loggedIn, 'reason=', reason);
  });

  bot.on('end', () => {
    console.log(`[BOT ${name}] Disconnected, exiting.`);
    process.exit(0);
  });

  bot.on('error', (err) => {
    console.error('[BOT] Error:', err);
  });
}

// Create two bots so the game can start (minPlayers >= 2)
createBot(BOT_NAME, true);         // leader, issues /hs start <seeker>
createBot(`${BOT_NAME}_2`, false); // follower
