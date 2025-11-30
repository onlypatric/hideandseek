import mineflayer from 'mineflayer';

const BOT_NAME = 'CiBot1';

function createBot() {
  const bot = mineflayer.createBot({
    host: '127.0.0.1',
    port: 25565,
    username: BOT_NAME,
    version: false
  });

  bot.on('login', () => {
    console.log('[BOT] Logged in as', BOT_NAME);
  });

  bot.on('spawn', () => {
    console.log('[BOT] Spawned, sending commands');
    bot.chat('/hs join');
    setTimeout(() => {
      bot.chat('/hs start');
    }, 5000);
    setTimeout(() => {
      console.log('[BOT] Finished test, quitting');
      bot.quit();
    }, 30000);
  });

  bot.on('kicked', (reason) => {
    console.log('[BOT] Kicked:', reason);
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

