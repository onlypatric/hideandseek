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
    }

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

  bot.on('windowOpen', (window) => {
    const title = window?.title?.toString?.() || String(window?.title || '');
    console.log(`[BOT ${name}] Window opened with title: "${title}"`);

    // Look for the BlockHunt selection GUI: "Select a Block: <map>"
    if (title.startsWith('Select a Block:')) {
      console.log(`[BOT ${name}] Detected BlockHunt GUI, selecting first block slot`);
      // Small delay to ensure contents are ready
      setTimeout(() => {
        try {
          // Click the first slot (0) with left-click
          bot.clickWindow(0, 0, 0, (err) => {
            if (err) {
              console.error(`[BOT ${name}] Error clicking BlockHunt slot:`, err);
            } else {
              console.log(`[BOT ${name}] Clicked BlockHunt slot 0`);
            }
          });
        } catch (e) {
          console.error(`[BOT ${name}] Exception while clicking BlockHunt slot:`, e);
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
