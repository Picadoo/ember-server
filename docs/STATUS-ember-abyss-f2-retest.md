# STATUS · 深渊 F2 卡死修复后短冒烟

**日期：** 2026-09-28 00:01～00:06（Asia/Shanghai / CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 总控 — CoreRpg **1.15.11** 已部署（刷点外移 CZ±6、洞口缩小加铁栏、F2 scattered 0.5；commit `31f2f2c`）；前债 B0.2 深渊 L1/L2 因 F2 卡死 SKIP（`STATUS-ember-b0-ttk-test.md` / `docs/STATUS-ember-abyss-f2-stuck.md`）  
**脚本：** `/tmp/abyss-f2-retest.js` · 日志 `/tmp/abyss-f2-retest.log` · JSON `/tmp/abyss-f2-retest.json`  
**账号：** 主测 **`Af2R2497`**（非 op）；临时管理 **`Af2OpBot`**（play 启前写入 ops + LP `admin`，测后已清）  
**CoreRpg：** **1.15.11**（`latest.log`：`CoreRpg 1.15.11 enabled` @ 00:01:39 CST）  
**未改：** 怪物/掉落/票价/YAML 数值 / jar / MM mtime（`EmberAbyss.yml` 仍 17:25；本窗未触）

---

## 总评

# **PASS**

| 项 | 结果 | 摘要 |
|----|------|------|
| CoreRpg 版本 | **PASS** | 日志 `CoreRpg 1.15.11 enabled`；jar/plugin.yml=1.15.11 |
| 进本 | **PASS** | 菜单 `/ember` → 深渊 → 开始下潜；扣票×1；`—— 第 1 层 ——` |
| F1 清完 | **PASS** | 进本后 **~5s** 见 `—— 第 2 层 ——`；服日志 `本局层 0 → 1` @ 00:05:02 |
| F2 坐标 | **PASS** | TP 实测 **(-41.4, 77.8, 261.3)** ≈ 期望 **(-42, 80, 264)**（Δ≤3） |
| 怪是否掉井 | **PASS** | F2 抵达采样 4 只，**minY=80**，fell=false（Y≥79） |
| 升层证据 | **PASS** | 服日志 **`本局层 1 → 2`** @ 00:05:23；客户端 **`—— 第 3 层 —— 骨潮加压`** @ +26s |
| 卡死 | **PASS** | 无；F1→F2→F3 横幅共 **26s**（对照债窗 ~9min 无 progress） |
| 死亡 | **PASS** | deaths=**0** |
| ops | **PASS** | play+login **`[]`**；`De-opped Af2OpBot` |

**本岗不改数值/票/MM。** F2 封口修复 live 短冒烟成立；B0.2 深渊 TTK L1/L2 对照仍待另窗重采。

---

## 环境

| 项 | 值 |
|----|----|
| play | `server-runtime` · `./start.sh custom`（本窗为加载 Af2OpBot ops 曾 stop→restart；日志仍见 1.15.11） |
| 端口 | 25565 proxy / 25566 login / 25567 play |
| 登录 | `mineflayer-tests/lib/proxy-login.js` |
| 装等 | Lv.**41** · 誓约 blaze · 天赋 L1 满 · T2 刃 **+7** · 护符 **+4** · 无战斗 buff（`/effect clear`） |

---

## 时序（CST）

| 时间 | 事件 |
|------|------|
| 00:01:39 | `CoreRpg 1.15.11 enabled` · Done |
| 00:03:18 | 脚本启动 · Af2OpBot / Af2R2497 |
| 00:04:51 | 进本成功 · `本局层 0 → 0` · `—— 第 1 层 —— 潮尸×3` |
| 00:05:02 | Instant Health · **`本局层 0 → 1`**（F1 清） |
| ~00:05:07 | 客户端 `—— 第 2 层 —— 混潮+骨潮` · TP≈(-41.4,77.8,261.3) · 怪×4 minY=80 |
| 00:05:23 | Instant Health · **`本局层 1 → 2`** · `—— 第 3 层 —— 骨潮加压` → **停** |
| 00:05:32 | `De-opped Af2OpBot` · ops.json=`[]` |

---

## 记录字段

| 字段 | 值 |
|------|----|
| 进本是否成功 | **是**（menu） |
| F1 清完时间 | **~5s**（进本→F2 横幅） |
| F2 坐标 | **(-41.4, 77.8, 261.3)** |
| 怪是否掉井 | **否**（F2 抵达 minY=80） |
| 升层聊天/横幅原文 | 服：`[深渊] Af2R2497 本局层 1 → 2`；客：`[深渊] 本局最高层更新为 2` · `—— 第 3 层 —— 骨潮加压` |
| 是否卡死 | **否** |
| 死亡数 | **0** |
| hits / combat_s | 14 / 27 |

---

## 脚本自动 FAIL 说明（已人工校正）

脚本初判 `verdict=FAIL` 两项为假阳性，不改总评：

1. **「missing 本局层 1→2」** — `本局层 X → Y` 只打 **服 INFO**，客户端可见为 `本局最高层更新为 N` + 层横幅。服日志已有完整链 `0→0` / `0→1` / `1→2`。
2. **「mobs fell Y&lt;79 minY=75」** — 停在 F3 横幅后继续采样，把 **F3 刷点**（`monster.yml` `-38,75,276`）的骨潮 Y≈75 算进「掉井」。F2 抵达瞬间采样 **minY=80 / fell=false**。

---

## 产物

| 路径 | 说明 |
|------|------|
| `docs/STATUS-ember-abyss-f2-retest.md` | 本报告 |
| `/tmp/abyss-f2-retest.json` | 原始+校正 JSON |
| `/tmp/abyss-f2-retest.log` | bot 侧全文 |
| `server-runtime/logs/latest.log` | 服侧 `本局层` / CoreRpg 版本 |

---

## 回报摘要

- **总评 PASS**
- **CoreRpg 1.15.11**
- **F2 坐标 (-41.4, 77.8, 261.3)** · **掉井否：否**
- **升层证据：** 服 `本局层 1 → 2` + 客 `—— 第 3 层 ——`
- **ops：** play=`[]` login=`[]`
- **报告：** `/workspace/minecraft/docs/STATUS-ember-abyss-f2-retest.md`
