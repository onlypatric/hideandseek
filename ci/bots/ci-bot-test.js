import mineflayer from 'mineflayer';

const BOT_NAME = process.env.CI_BOT_NAME || 'CiBot1';
const HOST = process.env.CI_BOT_HOST || '127.0.0.1';
const PORT = Number(process.env.CI_BOT_PORT || 25565);

function createBot() {
  console.log(`[BOT] Starting bot "${BOT_NAME}" targeting ${HOST}:${PORT}`);

  const bot = mineflayer.createBot({
    host: HOST,
    port: PORT,
    username: BOT_NAME,
    version: false
  });

  bot.on('login', () => {
    console.log('[BOT] Logged in as', BOT_NAME);
  });

  bot.on('spawn', () => {
    console.log('[BOT] Spawned at', bot.entity.position);
    console.log('[BOT] Sending /hs join');
    bot.chat('/hs join');

    setTimeout(() => {
      console.log('[BOT] Sending /hs start');
      bot.chat('/hs start');
    }, 5000);

    setTimeout(() => {
      console.log('[BOT] Finished test window, quitting');
      bot.quit();
    }, 30000);
  });

  bot.on('move', () => {
    // Log coarse-grained movement occasionally
    if (Math.random() < 0.01) {
      console.log('[BOT] Position', bot.entity.position);
    }
  });

  bot.on('message', (message) => {
    console.log('[BOT][CHAT]', message.toString());
  });

  bot.on('kicked', (reason, loggedIn) => {
    console.log('[BOT] Kicked. loggedIn=', loggedIn, 'reason=', reason);
  });

  bot.on('end', () => {
    console.log('[BOT] Disconnected, exiting.');
    process.exit(0);
  });

  bot.on('error', (err) => {
    console.error('[BOT] Error:', err);
  });
}

createBot();
