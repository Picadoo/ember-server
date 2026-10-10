# STATUS · P1 孔石 · 2026-10-11

STATUS=**批 A·M PASS · Stage1 PASS** · CST 2026-10-11  
TRIGGER=玩家「宝石要想办法弄了」  
RESEARCH=`docs/design/RESEARCH-ember-gem-reference-2026-10-11.md`  
DESIGN=`docs/design/DESIGN-ember-p1-socket-gems-2026-10-11.md`  
COORD=`/workspace/COORD-p1-socket-gems-2026-10-11.txt`

## 摘要

- 批 **A·M**：方案 M（DATA_VERSION **3** + Stage2 初值待 p1sim）已授权施工。
- **Stage1 PASS（数据 only，无战斗）：** `EmberItemData` 孔字段 sock0/sock1；canonical `|sk:a,b`；NBT sk0/sk1；`cr_p1_item.sock0/sock1`；旧 v1/v2 HMAC 不变、读入视为无孔；锻造 `copy` / 转化 / 六槽 reissue 保留宝石；lore 占位「孔：〔…〕」；解锁由 enhance 推导（刃 ≥0/≥5，符 ≥3，甲 0）。
- **不做（仍禁）：** EmberFormula 加法、掉落、菜单、复开旧 `/corerpg socket`、enhance.yml 白名单、K3/AFK/sx20。

## jar

- CoreRpg **`1.65.292-gem-s1.local`**

## 下一步

1. Stage2：flat A/D + 吸血/移速受控 + **p1sim 绿** 再定初值  
2. Stage3 获取 / Stage4 UI（新命令面，不复用 legacy socket）

## 禁

无 K3 / 无 AFK 表抬 / 无 sx20 / 无新永久伤乘区
