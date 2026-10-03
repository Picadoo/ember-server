# STATUS · 团本使徒 TTK 校准 A · 临时降门

**日期：** 2026-09-28 11:31 CST（Asia/Shanghai）  
**岗：** 余烬-插件  
**依据：** 总控【派工 · 团本使徒 TTK 校准 A · 临时降门】· tip `9a06265` · 设计 `884dcdd` · `docs/design/design-ember-raid-apostle-ttk-calib.md`  
**Verdict：** ✅ 降门已热更 · **未 push** · **待测岗采数后还原 min=3**

---

## 一句话

`EmberRaid` 人数门临时 `min=3`→`min=1`（`max=5` 不动），单 bot 可进本采使徒 ΔTTK；测毕须还原。

---

## 改前 / 改后（仅 1 行 + 注释）

**文件：** `plugins/DungeonPlus/dungeon/EmberRaid/option.yml`  
（与 `server-runtime/plugins/DungeonPlus/dungeon/EmberRaid/option.yml` **同 inode**）

| | 内容 |
|--|------|
| **改前** | `$team-condition{team=true;min=3;max=5;message=§c团本人数 3～5，当前 (<size>)} @system` |
| **改后** | `$team-condition{team=true;min=1;max=5;message=§c团本人数 1～5（校准窗），当前 (<size>)} @system` |
| 注释 | `# 2026-09-28 使徒 TTK 校准窗：min 临时 1（测毕还原 3）` |

**零改：** `max=5`；等级 `$js-condition`；票/体力/掉落/同袍；使徒/通道怪 HP·伤·技能；日常/周本/精英。

---

## 热重载

FIFO：`printf 'dp reload\n' > server-runtime/console.in`  
**11:31:28** CST · `插件重载完毕` · `[EmberRaid] 地牢内容初始化完毕`。

---

## 还原约定

本轮 **只降门**。测毕还原 `min=1`→`min=3`（message 可改回「3～5」）由测岗执行或再派本岗复核；**不得**永久留 min=1。

## Git

本地 commit；**未 push**（交总控代推后立刻派测岗采数）。
