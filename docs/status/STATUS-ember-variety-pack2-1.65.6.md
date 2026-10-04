# STATUS · Variety Pack 2 → CoreRpg 1.65.6（D176，2026-10-04）

## 结论

Stage A+B of D171 shipped as **CoreRpg 1.65.6** / `balance_version` **30**. Stage C p1sim gate **DEFERRED** (`COORD-gear-stage0` / mainline growthrun holds `tools/p1sim/**`).

## 改动摘要

| 区域 | 内容 |
|---|---|
| 词缀 | +`regen` / `charge` / `frost`（保留 blazing/split/shield）；KNOWN=6；否决 reflect |
| 房间事件 | 池 `timed` / `crystal` / `escort` 等权；限时行为保留 |
| 闸门 | 不变：NORMAL Q01–Q07 且全员已首通 |
| 奖励 | 不变：affix_shard=2、event_core=1 |
| 委托 | kind `timed` 文案 →「房间事件达标」（id 兼容）；crystal/escort 成功同样计数 |
| 冲锋 | 条带预警伤害；主体不真冲出 leash |
| 护宝兔 | treasure 角色 + `varietyEscort`；不进 room-clear 计数；不发宝藏币 |
| 砸晶 | SEA_LANTERN；Break MONITOR 强制 AIR + 左键交互 + tick poll；清房/失败/卸载 AIR；无掉落 |
| 菜单 | 图录 / 冒险页短说明更新 |

## 测试

- `mvn -o package`（JDK8）：**243 / 0**（含 D171 单测一组）
- 冒烟 FreshQ121+：
  - FreshQ121 `regen`：PASS（聊天「再生」、force 日志、结算）
  - FreshQ122 `charge`：PASS
  - FreshQ123 `frost`：PASS
  - FreshQ124/133–135 `crystal:r1`：放置 3 块 + 打碎计数路径 OK；创意模式 bot 挖块在副本防护下不稳定，实机左键/生存挖可走 MONITOR+poll；清房清理无残留
  - FreshQ126/128 `escort:r2`：PASS（护宝兔聊天 + `escort spawned` + `event escort done`）
- 资产路径：未改 vault 交付 → 跳过 full persist-roundtrip
- MySQL：CoreRpg + CoreGacha connected；SEVERE 0

## 协调

- `COORD-variety-1714` → DONE
- 未触碰 `COORD-mainline-unlocks` / `tools/p1sim/**`（未杀 growthrun PID）
- Stage C p1sim 门禁 deferred
