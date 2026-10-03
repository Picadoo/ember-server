# STATUS · 挂机二档菜单 UX 对齐 A

**日期：** 2026-09-28 21:53 CST（Asia/Shanghai）  
**岗：** 余烬-插件  
**依据：** 总控【批 A · 代推】· tip `27ad2f9` · 设计 `b6d7fad` · `docs/design/design-ember-afk-ux-tier2-menu.md`  
**Verdict：** ✅ TrMenu 文案已热更 · **未 push**

---

## 一句话

规则 I lore / 文件头改为**双软顶人话**（达顶→约两成五；再挂再降）；日顶数字不变；顺手 hub 挂机入口补「达顶后双软顶」。零动 `over_chance*` / Java / chat / MM / 体力。

---

## 改动

| 路径 | 内容 |
|------|------|
| `plugins/TrMenu/menus/ember_afk.yml` | 头注释 + 规则 I lore：去掉「超过后掉率 25%」单档句；写达顶后再降一档；保留日顶 150/80/10/2/150/100；灰字建议转日常/周本 |
| `plugins/TrMenu/menus/ember_hub.yml` | 挂机庭 lore 半句：`· 达顶后双软顶`（可选，已做） |

**未改：** `afk_caps` / `over_chance` / `over_chance_2` / `afkCapped` / 一·二档 chat / 选层 1～4 lore / 体力 / MM。

现网数值仍：`over_chance: 0.25` · `over_chance_2: 0.08`（config 本 commit 零 diff）。

---

## 禁项自检

| 禁项 | 结果 |
|------|------|
| 裸 `0.08` / `over_chance*` 写入菜单 | ✅ 无 |
| 宣称封死通胀 | ✅ 无（「长挂显著低于满速」） |
| 玩家面斜杠教学新增 | ✅ 无（`command: corerpg afk` 底层保留，与 B0.3 同） |
| 改 cap / Java / chat | ✅ 无 |

---

## 热更

- TrMenu 自动重载 `ember_afk.yml` / `ember_hub.yml`（21:53:10）+ `trmenu reload` → **32 个菜单已加载**（21:53:16 CST）

---

## 验收命令（测岗）

1. 枢纽 → 挂机庭：入口 lore 可见「达顶后双软顶」
2. 打开规则与今日上限：读得出达顶后再降一档；日顶数字与现网一致；**无**「超过后掉率 25%」作唯一句
3. 点规则仍可看今日进度；进任一层冒烟
4. `plugins/CoreRpg/config.yml`：`over_chance` / `over_chance_2` **未动**

---

## Git

- **未 push**；请总控代推后派测冒烟。
