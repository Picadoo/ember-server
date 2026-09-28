# STATUS · Boss 前压扩线（断塔 / 焦骨 / 地窖）

**日期：** 2026-09-28 10:35 CST（Asia/Shanghai）  
**岗：** 余烬-插件  
**依据：** 总控【派活 · Boss前压扩线 · 方案 A】· `docs/design-ember-daily-boss-prep-expand.md`（`3336efd`）§4/§7  
**Verdict：** ✅ · `dp reload` ✅ 10:35:07 CST · **未 push**

---

## 一句话

断塔 / 焦骨 / 地窖：`wave2b.end`→`boss_prep`×2 delay=2→boss；品种卫尸贴 / 燃矢近战 / 誓印弓；房2链式与门/Boss 零动；合计 +6。

---

## 改动

| 路径 | prep |
|------|------|
| `EmberDailySpire/monster.yml` | 断塔卫尸×2 @ (±3,76,2) |
| `EmberDailyAsh/monster.yml` | 燃矢骷×2 @ (±2,65,38) |
| `EmberDailyCrypt/monster.yml` | 誓印骷×2 @ (±3,60,42) |
| `docs/STATUS-ember-daily-boss-prep-expand.md` | 本 STATUS |

链：door2 AIR（坐标不动）+ heal → `boss_prep;delay=2` → prep.end heal → `boss;delay=2`。  
Boss 组坐标 / `$kill` / COMPLETE **零 diff**。

文案 §4.2～4.4：门槛贴脸/点射；门槛已清 · 守望/焦核/残誓现身；wave2b.end 补「· 门槛压」。

---

## §7 施工勾选

| # | 动作 | 勾 |
|---|------|----|
| 1 | EmberDailySpire boss_prep 卫尸×2 | [x] |
| 2 | EmberDailyAsh boss_prep 燃矢×2 | [x] |
| 3 | EmberDailyCrypt boss_prep 誓印×2 | [x] |
| 4 | server-runtime 同 inode（symlink/同目录），勿另写 | [x] |
| 5 | 小写 stub 未改 | [x] |
| 6 | MM / option / 门坐标 / 房1房2 零动 | [x] |
| 7 | 本地 commit；**未 push**（交总控） | [x] |

---

## 硬禁遵守

零碰房1/房2链式/门坐标/Boss/HP/庭院潮蚀霜晶锈轨；禁 kill-any；未改小写 stub；未新挖。

## 热更

FIFO `dp reload` · **10:35:07** CST · 插件重载完毕 · Spire / Ash / Crypt 初始化完毕。

## 验收

1. door2 后先 prep×2，清完才 Boss  
2. 三线可分：卫尸贴 / 燃矢近战 / 誓印射  
3. 房2链式仍在；门仍挂 wave2b.end  
4. 庭院/潮蚀/霜晶/锈轨零 diff  

## Git

未 push（交总控代推）· `ops.json=[]`
