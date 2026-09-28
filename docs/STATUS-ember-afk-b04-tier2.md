# STATUS · B0.4/B2.5 挂机二档施工 A

**日期：** 2026-09-28 21:25 CST（Asia/Shanghai）  
**岗：** 余烬-插件  
**依据：** 总控【派活 · B0.4/B2.5 挂机二档施工 A】· tip `02d964b` · 设计 `a288791` · `docs/design-ember-afk-b04-tier2.md`  
**Verdict：** ✅ CoreRpg **1.15.22** 短重启已载 · **未 push**

---

## 一句话

一档 `over_chance` **仍 0.25**；新增 `over_chance_2: 0.08`；`periodCount ≥ 2×cap` 切二档；材料 `afkCapped` 与击杀币同源；二档灰字提示（零指令）。

---

## 钉死曲线

| 阶段 | 条件 | 乘子 |
|------|------|------|
| 满速 | `count < cap` | 1.0 |
| 一档 | `cap ≤ count < 2×cap` | **0.25**（未改） |
| 二档 | `count ≥ 2×cap` | **0.08** |

币：`kill_coin` cap=150 → 2×=300 同逻辑。

### ④ 2h 外推对照（设计 A2 · 短样口径）

| 口径 | 2h 合计 shard | vs 日顶 150 |
|------|---------------|-------------|
| 现网仅 0.25 | ~490～501 | ≈3.3× |
| 本窗双档 | ~360～365 | ≈2.4× |

**禁宣称封死通胀**；对外「双软顶，长挂显著低于满速」。

---

## 改动文件

| 路径 | 内容 |
|------|------|
| `CoreRpg/.../CoreRpgPlugin.java` | `afkOverChance`；`afkCapped` 按 count 选 over + t1/t2 通知集合；击杀币分支同切 |
| `plugins/CoreRpg/config.yml` | `over_chance_2: 0.08`（runtime 同 inode） |
| `CoreRpg/src/main/resources/config.yml` | 同步 |
| `pom.xml` / `plugin.yml` | **1.15.21 → 1.15.22** |
| `plugins/CoreRpg.jar` | Maven 重编部署（runtime 同 inode） |

文案：
- 一档：沿用「…掉率降为 25%…」
- 二档：`§8[余烬] 挂机收益再降，建议去打日常/周本。`（`ChatColor.DARK_GRAY`；key `uuid:today:t2`）

---

## 热更

短重启 play（FIFO `console.in` + keeper）· **21:25:35** Enabling CoreRpg v1.15.22 · **Done 21:25:37** CST。

---

## 禁项

`over_chance` 仍 0.25；未动体力/日常/MM/票；无墙钟 2h；未宣称封死通胀。

## 验收（交测岗短样）

1. config 有 `over_chance_2: 0.08`；一档仍 0.25  
2. `[cap, 2×cap)` 出货率≈0.25；`≥2×cap`≈0.08（二项波动注明）  
3. 跨层合并三项回归（合并/文案/超限降率）  
4. 短样外推对照上表  

## Git

本地 commit；**未 push**（交总控代推后派测岗短样）。
