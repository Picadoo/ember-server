# STATUS · B2.7 主线 quest 日票→体力文案 B · 测试验收

- **时间：** 2026-09-28 23:04 CST（Asia/Shanghai）
- **执行：** 余烬-测试岗执行器
- **依据：** `docs/design-ember-quest-stamina-copy.md`（`b7ccbb8`）· 批准 B `a6c48bc` · 施工 tip `4eb9af8`
- **Verdict：** **✅ PASS**（验收点 1–3 全绿 · 可选 4 静态+热更抽检 · ops=`[]` · **未**宣称 B0.1 票扣显示名已清）
- **禁项：** 未改 YAML；未扩扫非本窗

---

## 一句话

双路径 `quest.yml` 玩家可见「日票/周票」已清零；设计表 6 处均为体力/体力药口径；相对 tip 父提交仅 6 字符串行变更（live/src 各 6±6），步骤 type/event/count/items、体力 cost、进本、TrMenu、cash、DP/MM/loot **零 diff**；热更 `23:02:51` Quest 10 chapters loaded。

---

## 验收点

| # | 点 | 结果 | 证据要点 |
|---|----|------|----------|
| 1 | `rg '日票\|周票'` 双路径 EMPTY | ✅ **PASS** | `plugins/CoreRpg/quest.yml` + `CoreRpg/src/main/resources/quest.yml` → **0 hit**（exit 1） |
| 2 | 抽检 6 处口径 | ✅ **PASS** | 见下表；双路径行号与文案一致 |
| 3 | 相对 tip 仅字符串行；禁项零 diff | ✅ **PASS** | tip `4eb9af8` 仅改两份 quest.yml + 施工 STATUS；unified 仅 6 对 ± 字符串；无 type/event/count/items/mobs/complete_on/xp/cost 键变更；cash/TrMenu 等禁项路径 **0 byte** |
| 4 | 可选：卷1 done / 残窟 intro+hint / 周烬 talk / 两处 daily_clear | ✅ **PASS（静态+热更）** | live 抽检 6 句已换；`[23:02:51]` Quest: 10 chapters loaded；**未**完整跑本（不强制） |

**总评：** **PASS**

### 点 2 · 六处口径（live = src，`cmp` IDENTICAL）

| # | 行 | 键位 | 现网（节选） |
|---|---:|------|--------------|
| 1 | 68 | 卷1 签到复命 `done` | `体力药拿去` |
| 2 | 73 | 残窟 `intro` | `日回体力约可刷 3 次日常，主线再送 1 瓶体力药` |
| 3 | 81 | 残窟 kill `hint` | `耗体力` |
| 4 | 140 | 周烬 talk `done` | `留够体力` |
| 5 | 243 | ch5 `daily_clear` hint | `日回体力约可刷 3 次日常` |
| 6 | 355 | ch8 `daily_clear` hint | `日回体力` |

### 点 3 · diff 摘要（相对 `4eb9af8^`）

```
仅字符串行（双路径同 diff）：
- 日票拿去 → 体力药拿去
- 每日 3 张免费日票，主线再送 1 张 → 日回体力约可刷 3 次日常，主线再送 1 瓶体力药
- 需日票 → 耗体力
- 带上周票 → 留够体力
- 每日 3 张免费日票 → 日回体力约可刷 3 次日常
- 每日免费日票 → 日回体力
结构旁证（未改）：items:{consumable_ember_stamina_30:1,...}；type/kill·event/daily_clear·count 2/6/15 等仍在。
```

### 明示不宣称

- **B0.1** 票扣显示名债：**本窗不宣称已清**
- 非「日票/周票」其它旧口吻：未扩扫

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567（均 LISTEN） |
| 施工 tip | `4eb9af8` fix(quest): B2.7 stamina copy (B dual-path) |
| 热更 | `23:02:51` CoreRpg reload · Quest 10 chapters |
| 临时 OP | **未用**；`ops.json` play/login 均为 `[]` |
| YAML | **未改** |

---

## 回报总控

| 项 | 值 |
|----|-----|
| **总评** | **PASS** |
| 点1 rg EMPTY | PASS |
| 点2 六处口径 | PASS |
| 点3 结构/禁项零 diff | PASS（仅字符串行） |
| 点4 可选抽检 | PASS（静态+热更；未跑本） |
| 报告 | `docs/STATUS-ember-quest-stamina-copy-test.md` |
| ops | `[]` |
| 阻塞点 | 无 |
| tip（测报 commit 后） | 见落盘后 git tip；**未 push**，总控代推 |
