# STATUS · B1.4 软抛光（等级 hint S1～S8）

**日期：** 2026-09-28 08:48 CST（Asia/Shanghai）  
**岗：** 余烬-插件  
**依据：** 总控【批准 · B1.4 硬关单 + 软抛光 8 条】· `docs/design/design-ember-b14-quest-join-copy.md` §4.2 / §10  
**Verdict：** ✅ **软抛光+短抽 PASS**（`ee3cfbc` · 测报另 commit）· 硬关单 PASS

---

## 一句话

仅替换 `quest.yml` 八条等级 `hint`：并列斜杠 → 间隔号「·」；S1～S4 补「打开枢纽菜单 →」。未动 join_message、步骤结构、数值、掉落、TrMenu、config 内部 command。

---

## 落地勾选（§4.2）

| # | 章 | 结果 |
|---|----|------|
| S1 | ch2 Lv20 | ✅ `打开枢纽菜单 → 签到 · 日常本 · 悬赏 · 挂机庭①灰坡，都能补经验` |
| S2 | ch3 Lv25 | ✅ `…日常本 · 悬赏 · 签到 · 挂机庭②荒原` |
| S3 | ch4 Lv30 | ✅ `…深渊 · 日常本 · 悬赏 · 挂机庭②荒原` |
| S4 | ch5 Lv35 | ✅ `…深渊 · 日常本 · 悬赏 · 灾厄 · 挂机庭③焦土` |
| S5 | ch7 Lv40 | ✅ `…日常本 · 深渊 · 挂机庭 · 悬赏` |
| S6 | ch8 Lv45 | ✅ `…深渊 · 日常本 · 灾厄 · 挂机庭` |
| S7 | ch9 Lv50 | ✅ `…精英试炼 · 深渊 · 日常本` |
| S8 | ch10 Lv60 | ✅ `…日常本 · 深渊 · 精英试炼 · 团本 · 灾厄` |

---

## 改动文件

| 路径 | 说明 |
|------|------|
| `plugins/CoreRpg/quest.yml` | 派工目标 · 仅上述 8 条 hint |
| `CoreRpg/src/main/resources/quest.yml` | 与 live 同步（防下次 package 回退） |
| `docs/status/STATUS-ember-b14-quest-join-soft.md` | 本 STATUS |

**未改：** join_message、步骤 type/数值/items、TrMenu、config 内部 command、DP/MM。

---

## 热重载

- **时间：** 2026-09-28 **08:48:06** CST  
- **命令：** FIFO → `corerpg reload`  
- **结果：** `[CoreRpg] Quest: 10 chapters loaded` · `配置已重载`

---

## 验收（测岗短抽）

抽 ch2 / ch3 / ch7 等级 hint 文案：有「打开枢纽菜单」；并列用「·」无裸 `/`。

---

## Git

- **未 push**（交总控推）  
- `ops.json` = `[]`

## Blocker

无。

## Checklist

- [x] §4.2 S1～S8 hint 落地
- [x] resources/quest.yml 同步
- [x] corerpg reload
- [x] 短抽 ch2/ch3/ch7 **PASS**（`docs/status/STATUS-ember-b14-quest-join-soft-test.md`）
