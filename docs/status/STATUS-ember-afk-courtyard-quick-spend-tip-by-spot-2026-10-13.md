# 状态 · TIP-BY 挂机庭顺手花料（E/e）抽测

**裁决：** **PASS** · tip `e214b0399` · 证据 `/workspace/tmp/tip-by-afk-spend/`  
**范围：** `ember_p1_afk` Layout E/e · gap_enhance1 亮/灰 · Open/S 半行 · **零 jar** · **≠关观察** · **未改** afk.tiers / daily_kills / Stage2 三开关 / gate_daily / ×0.97

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | Layout 含 E/e；gap≤0 亮 enhance / enhance confirm；gap≥1 灰进 forge | **PASS** | `30-afk-window-base.json`（灰）· `follow-afk-bright.json`（亮）· `follow-99-summary.json` |
| 2 | Open「仓够时可在本页顺手花」；S「本页也可顺手强化」 | **PASS** | `10-menu-static.json` · live Open tell / S lore |
| 3 | 零改 afk.tiers / 产量 | **PASS** | `90-endstate.json` · afk_tiers_md5 `51e2e286df1c` · daily_kills=2400 |

## duplicate keys WARN

- reload 见 YamlConstructor `duplicate keys found : priority/display`（全量 77 菜单加载时）
- **非 E/e 结构坏：** 单文件自动重载 `ember_p1_afk.yml` 为「良好」无 WARN；Python YAML 解析 E/e 完整（base display/actions + nested icons）；live E/e 亮灰与点击路径生效
- 同构 WARN 亦见于既有 F/1–4 嵌套 icons（预存）

## 旁注

- 抽测窗并行落地 **TIP-CA** `68544f966`（U/u 顺手升阶）：Layout 现 `#US4 V E#` / `#ueI F H#`；**E/e TIP-BY 键与命令仍在**（e 槽位=29）
- S lore 被 TIP-CA 扩成「本页也可顺手强化/升阶」——仍覆盖 TIP-BY 半行
- AuthMe 池超时曾踢 bot → **仅重启 login-runtime** 恢复（≠动 MariaDB 配置/数据）

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · afk.tiers 未拧 · ≠关观察 · 未切分支/stash/reset/force-push · 未碰 login 配置与 MariaDB
