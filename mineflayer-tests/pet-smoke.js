/**
 * CoreRpg pet / 使魔 smoke — DESIGN-ember-pet-bestiary.md
 * NI eggs: pet_ember_ashling · pet_ember_cinder (ember-pets.yml)
 * Expect: /corerpg pet · list · summon ashling|cinder (or full id) · dismiss
 * Do NOT run until plugins/CoreRpg.jar contains PetService + pet.yml.
 */
const mineflayer = require('mineflayer')
const bot = mineflayer.createBot({
  host: '127.0.0.1',
  port: 25565,
  username: 'RpgBot',
  version: '1.12.2',
  auth: 'offline'
})
bot.on('message', m => console.log('[chat]', m.toString()))
const wait = ms => new Promise(r => setTimeout(r, ms))
async function chat(c) {
  console.log('[cmd]', c)
  bot.chat(c)
  await wait(1200)
}
bot.once('spawn', async () => {
  await wait(2500)

  // NI pet eggs (full IDs from DESIGN / ember-pets.yml)
  await chat('/ni give RpgBot pet_ember_ashling 1')
  await chat('/ni give RpgBot pet_ember_cinder 1')
  await wait(600)

  // root + list
  await chat('/corerpg pet')
  await chat('/corerpg pet list')

  // short aliases expected by smoke plan (plugin may also accept full ids)
  await chat('/corerpg pet summon ashling')
  await chat('/corerpg pet dismiss')
  await chat('/corerpg pet summon cinder')
  await chat('/corerpg pet dismiss')

  // full NI / pet.yml ids (source PetService keys)
  await chat('/corerpg pet summon pet_ember_ashling')
  await chat('/corerpg pet list')
  await chat('/corerpg pet dismiss')
  await chat('/corerpg pet summon pet_ember_cinder')
  await chat('/corerpg pet dismiss')

  // optional unlock path if eggs remain
  await chat('/corerpg pet unlock pet_ember_ashling')
  await chat('/corerpg pet feed')

  console.log('PET_SMOKE_DONE')
  process.exit(0)
})
bot.on('kicked', r => { console.error('kicked', r); process.exit(1) })
bot.on('error', e => { console.error(e); process.exit(1) })
setTimeout(() => { console.error('timeout'); process.exit(2) }, 90000)
