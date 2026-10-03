# STATUS · 登录服 + AuthMe MySQL

**日期：** 2026-09-13  
**拓扑：** login `:25566` + ember 游玩服 `:25565` + MariaDB

| 项 | 状态 |
|----|------|
| MariaDB | LIVE · `ember` / `authme` |
| AuthMe 5.4.0 | LIVE · MySQL backend · 表 `authme.authme` |
| login-runtime | `/workspace/minecraft/login-runtime` · `./start.sh` / `./stop.sh` |
| Multiverse 公共世界 | LIVE · `ember_hub` / `ember_afk` / `ember_event` |
| CoreRpg MySQL | 插件岗进行中 |
| Bungee/Velocity 串服 | 未做（下期） |

冒烟：AuthMe 日志 `MySQL setup finished`；`SHOW TABLES` 见 `authme`。
