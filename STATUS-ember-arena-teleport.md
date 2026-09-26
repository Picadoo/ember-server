# STATUS — CoreRpg arena teleport + settle (1.3.11)

**时间：** 2026-09-13 ~12:41 Asia/Shanghai  
**约束：** Paper 未改 · tickets/pet/auction/guild 保留

| 项 | 状态 |
|----|------|
| CoreRpg **1.3.11** | OK `plugins/CoreRpg.jar` **217939** bytes |
| Restart | OK `./start.sh custom` · log `CoreRpg 1.3.11 enabled (.../arena/auction)` |
| Paper | 未改 |

## Arena 对战流
- 1v1：队列满 → 存 return Location → TP `pad_a`/`pad_b` → ActiveMatch
- 结算：死亡（杀手或对方胜）/ 退出（认输方负）/ 超时平局 / `forfeit`
- 结算后 `return_delay_ticks=40` TP 回存点；heal + clear fire；防双结算
- 2v2：同垫 ±2；团灭结算（非首杀）
- world 缺失 → stub 即时结算 + warning

## Pads (`arena.yml`)
- world: `world`
- pad_a: 100.5, 65, 100.5 yaw 0
- pad_b: 110.5, 65, 100.5 yaw 180
- timeout_seconds: 120 · return_delay_ticks: 40

## Commands
- `/corerpg arena` queue|leave|stats|claim|forfeit
- `/corerpg pvp` 同 arena（queue|leave|stats|claim|forfeit 透传）
