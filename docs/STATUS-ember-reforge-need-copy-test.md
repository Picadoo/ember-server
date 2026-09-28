# STATUS · B2.21 重铸缺料人话批 A · 轻测

**日期：** 2026-09-29 02:06 → 02:13 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 设计 `e81c904` · 批准 `92d6ee3` · 施工 tip `16b5334` · CoreRpg **1.15.27**  
**范围：** `ScrapService.cmdReforge` 缺料 chat →「需要 余烬重铸石 ×1」；count/consume 仍用 `stoneNiId`  
**禁项：** **禁改配置** · 禁 wall-clock/DPS/长本/挑刺 · **不宣称 B0.1 / NI lore 已清** · 勿带 dirty runtime · 禁改 scrap/reforge 逻辑 / TrMenu / NI 物品  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]`）

---

## 一句话

源码缺料 `sendMessage` 已字面「余烬重铸石」、无拼接 `stoneNiId`；hub→拆解→无石点重铸 live chat **`[重铸] 需要 余烬重铸石 ×1`**（无裸 id）；`stone_ni_id` / 消耗×1 / 其它 `[重铸]` 句 / TrMenu / NI **未漂**；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | `rg` 缺料 sendMessage 无拼接 `stoneNiId` / 无字面 `mat_ember_reforge_stone`；count/consume 仍用 `stoneNiId` | **PASS** |
| 2 | live：hub→拆解→无石点重铸 → chat `[重铸] 需要 余烬重铸石 ×1`（无裸 id） | **PASS** |
| 3 | `stone_ni_id` / 消耗×1 / 其它 `[重铸]` 句 / TrMenu / NI **未漂**（相对批前） | **PASS** |
| 4 | 不宣称 B0.1 / NI lore 已清；不叫挑刺 | **PASS** |
| 5 | 写本测报 · commit+push · ops=`[]` | **PASS** |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 插件 tip | `16b5334` fix: localize reforge missing stone message |
| 设计 / 批准 | `e81c904` / `92d6ee3` |
| CoreRpg live | **1.15.27**（Enabling @ 02:12:17 CST · Done 6.056s @ 02:12:19） |
| 账号 | 验收 `NiPkeuh8`（非 OP · Lv.10 · 手持 `gear_ember_t1_blade` · **无**重铸石）· 辅助 `NiOpkeuh8`（临时 OP，已 deop + LP unset） |
| 探针 | `/tmp/b221-reforge-need-copy-test.js` · JSON `/tmp/b221-reforge-need-copy-test.json`（未入 git） |
| 备注 | 测前 play stdin 曾脱 FIFO（`/dev/null`）；FIFO 短重启接回 `console.in` 后测（jar 仍为 tip 部署的 1.15.27） |

---

## 各点证据

### 1 · 静态源码

```
rg -n '需要 .*stoneNiId|需要 mat_ember_reforge_stone' \
  CoreRpg/src/main/java/town/sunshine/corerpg/ScrapService.java
→ (空 · 0 hits · exit 1)
```

| 项 | 现网 |
|----|------|
| L385 缺料句 | `p.sendMessage(ChatColor.RED + "[重铸] 需要 余烬重铸石 ×1")` |
| count | `ni.countInInventory(p, stoneNiId) < 1` **仍在** |
| consume | `ni.consumeExact(p, stoneNiId, 1)` **仍在** |
| tip 范围 | 仅 `ScrapService.java` + `pom.xml`/`plugin.yml` 版本 → **1.15.27**（未碰 scrap.yml / TrMenu / NI） |
| live jar | `ScrapService.class` 含 UTF-8「需要 余烬重铸石」 |

### 2 · UX live · hub→拆解→无石点重铸

| 步骤 | 结果 |
|------|------|
| `/ember` → hub | 标题「余烬 · 冒险枢纽」 |
| 点「分解」 | 标题「余烬 · 分解」· 槽见「分解装备 / 重铸词缀 / 规则速览 / 返回主菜单」 |
| 前置 | `clear` 后 `/ni give … gear_ember_t1_blade 1` · 装备手持 · **不**给重铸石 |
| 点「重铸词缀」 | TrMenu tell「重铸 请确认手持装备且背包有重铸石…」+ CoreRpg **`[重铸] 需要 余烬重铸石 ×1`** |
| 裸 id | chat **无** `mat_ember_reforge_stone` |

（clickWindow 可能报 transaction timeout，属既有 TrMenu/mineflayer 吞事务；**chat 已捕获 CoreRpg 缺料句**，以 chat dump 为准。）

### 3 · 未漂（相对批前 / tip）

| 项 | 证据 |
|----|------|
| `stone_ni_id` | `scrap.yml` 仍 `mat_ember_reforge_stone` |
| 消耗 ×1 | `consumeExact(…, 1)` 未改 |
| 其它 `[重铸]` 句 | 未启用/手持/白名单/词缀池空/扣除失败 **仍在** |
| TrMenu | 消耗行「§8消耗 §6余烬重铸石」· name/tell/`corerpg reforge` 未漂 · 无裸 id |
| NI 物品 | `mat_ember_reforge_stone` 键 + `name: '&6余烬重铸石'` 未漂 |

### 4 · 禁项自检

- **不**宣称 B0.1 已清  
- **不**宣称 NI 物品 lore 内 `&7mat_ember_reforge_stone` 已清（属方案 B soft · 勿与 A 双上）  
- **不叫挑刺**；禁 wall-clock/DPS  
- 本窗 **未改** 配置 / YAML / Java（仅测报文档）

### 5 · ops / 交付

- 测后 `deop` + LP unset · `ops.json` play/login = `[]`  
- 本文件入 git · commit+push  

---

## 回总控

- **总评：** ✅ **PASS**  
- **各点：** 1–5 全 PASS  
- **tip SHA：** `16b5334`（设计 `e81c904` · 批准 `92d6ee3`）  
- **报告路径：** `docs/STATUS-ember-reforge-need-copy-test.md`  
- **ops：** `[]`  
- **阻塞点：** 无  
- **UX：** hub→拆解→无石点重铸 → chat 人话 ×1  
