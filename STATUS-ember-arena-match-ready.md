# STATUS · Ember 竞技 1v1 匹配 / 传送 smoke — ready / waiting for CoreRpg 1.3.11

**日期：** 2026-09-13 12:39（Asia/Shanghai）  
**规格：** `DESIGN-ember-arena-auction.md` §1 · `STATUS-ember-arena-auction-impl.md` · `STATUS-ember-rpg-progress.md`  
**约束：** 未改 Paper；**未覆写** `CoreRpg.jar`；**未跑** server smoke

---

## CoreRpg 部署检查

| 项 | 值 |
|----|-----|
| `plugins/CoreRpg.jar` | **208404** bytes · mtime **12:36 CST**（2026-09-13 04:36 UTC） |
| `plugin.yml` version（已部署） | **1.3.10** |
| `ArenaService` · `arena.yml` | **有** · 内存队列 1v1/2v2 · **stub 胜负结算**（匹配 tell，**无**传送 / 独立世界） |
| `/corerpg arena queue 1v1` | **LIVE**（1.3.8+；满 2 人 stub 匹配） |
| `/corerpg pvp …` 别名 | **无**（`onCommand` 仅 `arena`；usage 无 pvp） |
| 匹配传送 / 位点变化 | **无**（`resolveStubMatch` 仅消息 + 积分/币；无 `teleport`） |
| 源码 `plugin.yml` | **1.3.10**（与已部署一致；**尚无** 1.3.11 传送 / pvp） |

**结论：** 1.3.10 可做双人 stub 匹配 tell，但 **无位置变化验收面**、**无 pvp 别名** → **不跑** `arena-match-smoke.js`，等插件岗正式部署 **CoreRpg 1.3.11**（1v1 匹配传送或等价位点变化 + `/corerpg pvp` 别名）后再测。

---

## 已准备

| 路径 | 内容 |
|------|------|
| `mineflayer-tests/arena-match-smoke.js` | **ArenaA + ArenaB** · 双人 `/corerpg arena queue 1v1` → 等匹配/传送 tell → 断言位点变化 → leave/stats settle → `/corerpg pvp queue 1v1` 别名同路径 |
| `DESIGN-ember-arena-auction.md` | 1v1/2v2 队列 · 日箱 · 真实对战世界后接 |
| `STATUS-ember-arena-auction-impl.md` | 1.3.8 stub 匹配 PASS（积分结算，无 TP） |

### 脚本期望命令面（1.3.11）

```
# 双 bot 同时在线
/corerpg arena                      # 可选：积分/排队状态
/corerpg arena queue 1v1            # ArenaA 与 ArenaB 各一次
# 期望：匹配成功 tell（或传送 tell）+ 双方 entity.position 变化（Δ≥~2.5 或跨维度）
/corerpg arena leave                # 若仍排队则离开；否则「不在队列」可接受
/corerpg arena stats                # settle：胜/负/积分

/corerpg pvp queue 1v1               # arena 别名（须接线）
/corerpg pvp leave
/corerpg pvp stats
```

验收要点：双人入队后出现 **匹配**（或传送）消息；**至少一个 bot 位点变化**（真实 TP / 竞技世界）；`pvp` 别名与 `arena` 行为一致。纯 stub 无 TP 时脚本记 `POS_UNCHANGED_STUB_OK_IF_MATCHED`，但 **1.3.11 目标是有位点变化**。

---

## 等待插件岗

1. 将 **1v1 匹配传送**（或独立竞技世界出生点）编入 **`CoreRpg.jar` 1.3.11**，使双人 queue 后 mineflayer 可测到 position 变化  
2. 接线 **`/corerpg pvp`** 为 `arena` 别名（`queue` / `leave` / `stats` / `claim`）；`plugin.yml` version **1.3.11**，usage 含 pvp  
3. **不要**用本机随意覆盖 jar；由插件岗正式部署后再测  
4. 服重启（新类不可热重载）后跑 smoke；确保无其它 ArenaA/ArenaB 在线  

---

## 部署后复跑

```bash
cd /workspace/minecraft/mineflayer-tests
# 确保无其它 ArenaA/ArenaB 在线；CoreRpg.jar == 1.3.11 且含匹配传送 + pvp 别名
node arena-match-smoke.js
```

期望：日志 `MATCH_SNIP_ARENA` / `MATCH_SNIP_PVP` 含匹配 tell；`POS_CHANGED`；结尾 `ARENA_MATCH_SMOKE_PASS`。仅 stub 匹配、无 TP → `ARENA_MATCH_SMOKE_INCOMPLETE` 或记 NOTE。

---

## 未改

Paper / `CoreRpg.jar` / 其它 jar · **未执行** arena-match smoke · 未改 `arena.yml` / TrMenu 竞技壳
