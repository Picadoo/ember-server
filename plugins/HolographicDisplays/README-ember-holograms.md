# Ember holograms

Do **not** hand-edit `database.yml` (plugin-managed file store).

Ops paste commands from:

`/workspace/minecraft/docs/ember-holograms.md`

Boards: `ember_ladder_power` / `ember_ladder_abyss` / `ember_ladder_speed`  
Suggested coords (AFK): `-44 67 265` / `-40 67 265` / `-36 67 265`

### PlaceholderAPI lines (`%ember_ladder_*%`)

| Board | Placeholders (N = rank) |
|-------|-------------------------|
| power TOP10 | `%ember_ladder_power_N_name%` / `%ember_ladder_power_N_value%` (N=1…10) |
| abyss TOP5 | `%ember_ladder_abyss_N_name%` / `%ember_ladder_abyss_N_value%` (N=1…5) |
| speed TOP5 | `%ember_ladder_speed_N_name%` / `%ember_ladder_speed_N_value%` (N=1…5；单位秒) |
| self | `%ember_power_score%` · `%ember_abyss_best%` · `%ember_weekly_best_sec%` |

Live create: `mineflayer-tests/hd-create-ember.js` (RpgBot OP).  
PAPI smoke (after CoreRpg ladder deploy): `mineflayer-tests/ladder-papi-smoke.js`.  
Note: `%corerpg_coin%` already works; ladder `%ember_ladder_*%` pending CoreRpg wiring.
