# STATUS · 消 kill-any 剩债（方案 A）

**日期：** 2026-09-28 10:01 CST（Asia/Shanghai）  
**岗：** 余烬-插件  
**依据：** 总控【派活 · 消 kill-any 剩债 · 方案 A】· `docs/design-ember-killany-debt-cleanup.md`（`082dc0e`）§2/§4  
**Verdict：** ✅ · `dp reload` ✅ 10:01:52 CST · **未 push**

---

## 一句话

深渊 F2/F7、团本 wave2、公会 Boss wave1：单条 `$kill-any` → 同波双 `$kill` AND；不链式、不拆 wave；双树 identical（同 inode）。

---

## 改动（仅 condition）

| 组 | 改后 |
|----|------|
| EmberAbyss floor2 / floor7 | `$kill{余烬深渊·混潮;2}` + `$kill{余烬深渊·骨潮;2}` |
| EmberRaid wave2 | `$kill{团本·通道卫兵;3}` + `$kill{团本·通道射手;4}` |
| EmberGuildBoss wave1 | `$kill{余烬深渊·潮尸;5}` + `$kill{余烬深渊·骨潮;3}` |

路径：`plugins/DungeonPlus/dungeon/{EmberAbyss,EmberRaid,EmberGuildBoss}/monster.yml`  
（`server-runtime/plugins/DungeonPlus` 与 `plugins/DungeonPlus` **同 inode**，改一次即双树同步。）

---

## §4 施工勾选

- [x] `plugins/.../EmberAbyss/monster.yml` — floor2 + floor7
- [x] `plugins/.../EmberRaid/monster.yml` — wave2
- [x] `plugins/.../EmberGuildBoss/monster.yml` — wave1
- [x] 同步 server-runtime 三份（同 inode · cmp identical）
- [x] 抽检：有效行 `$kill-any{` 仅剩小写模板 `AAA,BBB,CCC`；注释「禁 kill-any」保留不计

---

## 硬禁遵守

不链式 / 不拆 wave / 零碰 option·HP·刷点·人数·end / 日常七线 / 周本 / 精英 —— 未动。模板 AAA 与注释未误改。

## 热更

FIFO `dp reload` · **10:01:52** CST · 插件重载完毕 · EmberAbyss / EmberRaid / EmberGuildBoss 初始化完毕。

## 验收（对接设计 §3）

1. 三文件实杀 `$kill-any{` 归零  
2. 同波双 `$kill` AND；amount 2+2 / 3+4 / 5+3；无新组名、无链式  
3. 他线 YAML 零 diff  
4. plugins ↔ server-runtime identical  

## Git

未 push（交总控代推）· `ops.json=[]`
