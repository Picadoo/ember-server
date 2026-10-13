# 状态 · TIP-CM 抽测 · D13318 短征 R3 Boss 半血轻添头

**日期：** 2026-10-14（上海时间）  
**tip：** `ee36f5af9` · jar/mm 热更已做（189 怪 / 124 技能）  
**裁决：** **PASS**  
**证据：** `/workspace/tmp/tip-cm-boss-aid/`  
**toplevel：** `/workspace/minecraft` @ `main`

## 人话

终厅 Warden 打到约半血会喊「半血添头——先清！」并刷 1 只本本 R1；同场只添一次。BossCast 仍按 timer 放。

## 旁注（验收口径）

现网 MM 4.11 **无** `healthpercent`；条件用 `health{h=<120}`（Warden Health 240 → ≈半血）。**不因此判红**（tip/STATUS 已记）。

## 必验

| ID | 结果 | 摘录 |
|----|------|------|
| V0 Stage2 三开 · daily_kills=2400 · ×0.97 | **PASS** | six_slot enabled/migrate/set_bonus；taken_tele 0.97 |
| V1 BossAid ×19 · health{h=<120} · CD9999 · summon1 | **PASS** | sx01 Zombie / sx02 Guard / sx03 Patrol / sx04–19 Guard |
| V1 Warden ~onDamaged + SoftHit + BossCast ~onTimer:70 | **PASS** | 19/19 hooks |
| V1 Cast 数值未拧（对照 TIP-CF/CI/BZ） | **PASS** | Archer 0.25 · Guard 0.32 · Elite 0.52 · BossCast 0.85 |
| V1 无 sx20 / 零改日帽 afk.tiers | **PASS** | tip diff 仅 Skills 追加 + Warden +1 行 |
| V1 mm reload 189/124 · 0 err | **PASS** | 终态 reload 钉死 |
| V2 sx01 半血喊话 + 场上 +1 R1 | **PASS** | `【烬门】半血添头——先清！` · newHostiles=1 |
| V2 sx01 同场再打 ≤1（CD9999） | **PASS** | secondAidN=0 |
| V2 sx01 BossCast 仍放 | **PASS** | `哨长蓄力——拉开！` |
| V2 sx07（另抽）同上 | **PASS** | `【烬塔】半血添头——先清！` · +1 · CD一次 · `塔冠坠焰` |
| V8 gate_daily=no | **PASS** | console `no` |

## 联机要点

- bot `TipCmYes` · joinPlay@25565 · 进 sx01 实例后 `mm mobs spawn EmberSxNNWarden`
- 须握 **余烬之刃**（`ni give … gear_ember_blade`）；钻石剑不吃伤（「主手没有刃」）
- 证据：`72-sx01-blade.json` · `72-sx07-blade.json` · `05-mm-reload-final.json` · `72-results.json`

## 不动

零改 YAML/jar（测岗只验）。Stage2 / daily_kills / gate_daily / Cast 伤表 / afk.tiers 未拧。

*TIP-CM · D13318 · BossAid 抽测 PASS · tip ee36f5af9。*
