# STATUS · 源码 join/灾厄广播同步静态短抽

**测试时间：** 2026-09-28 08:52:49 +0800 (Asia/Shanghai)  
**依据：** `docs/status/STATUS-ember-src-join-calamity-sync.md` · commit `30886e2`  
**测试方式：** 静态读取/YAML 解析/git diff；未执行 reload；未改配置；未 commit/push。  
**总评：** **PASS**

## 三条硬条

### 1. `join_message` 四行语义对齐：**PASS**

解析后的 src/live 四行完全一致（四行），且两端均无 `/corerpg` 或 `/ember` 教学。

- src：`CoreRpg/src/main/resources/config.yml`
- live：`plugins/CoreRpg/config.yml`

原文摘录（src）：
```
  4: join_message:
  5:   - "§6§l[余烬服] §e跟着主线走：引路人·灰烛就在出生点北边——右键他看当前目标。"
  6:   - "§7挂机庭打僵尸拿碎片 · 骷髅拿骨尘 · 日本蛮兵拿核心碎片 · 死亡不掉落 · 打开枢纽菜单可返回"
  7:   - "§7打开枢纽菜单：主页 · 强化 · 镶嵌 · 签到 · 生活补给。工坊 NPC 也可右键进入。"
  8:   - "§7进本门槛：周本 Lv.20 · 深渊 Lv.25 · 灾厄 Lv.30 · 团本 Lv.35——打开枢纽菜单进入。"
```
原文摘录（live）：
```
  4: join_message:
  5: - §6§l[余烬服] §e跟着主线走：引路人·灰烛就在出生点北边——右键他看当前目标。
  6: - §7挂机庭打僵尸拿碎片 · 骷髅拿骨尘 · 日本蛮兵拿核心碎片 · 死亡不掉落 · 打开枢纽菜单可返回
  7: - §7打开枢纽菜单：主页 · 强化 · 镶嵌 · 签到 · 生活补给。工坊 NPC 也可右键进入。
  8: - §7进本门槛：周本 Lv.20 · 深渊 Lv.25 · 灾厄 Lv.30 · 团本 Lv.35——打开枢纽菜单进入。
```

### 2. `calamity.yml` 灾厄广播：**PASS**

`message_pre` / `message_open` 在 live + src 四处均含「打开枢纽菜单」，且均无 `/ember`。

原文摘录（src：`CoreRpg/src/main/resources/calamity.yml`）：
```
  23:   message_pre: "§4[余烬灾厄] §e{minutes} 分钟后降临！§7打开枢纽菜单 → 灾厄 → 奔赴"
  24:   message_open: "§4§l[余烬灾厄] §e灾厄使降临祭坛！§7打开枢纽菜单 → 灾厄 → 奔赴"
```
原文摘录（live：`plugins/CoreRpg/calamity.yml`）：
```
  23:   message_pre: "§4[余烬灾厄] §e{minutes} 分钟后降临！§7打开枢纽菜单 → 灾厄 → 奔赴"
  24:   message_open: "§4§l[余烬灾厄] §e灾厄使降临祭坛！§7打开枢纽菜单 → 灾厄 → 奔赴"
```

### 3. `quest.yml` 相对 `ee3cfbc`：**PASS**

对以下两路径执行 `git diff --quiet ee3cfbc -- ...`，无差异；当前 Git blob 与 `ee3cfbc` 对应对象一致，确认无非预期 diff，B1.4 未回退：

- `CoreRpg/src/main/resources/quest.yml`
- `plugins/CoreRpg/quest.yml`

## 回执

- **总评：** **PASS**
- **报告：** `docs/status/STATUS-ember-src-join-calamity-sync-test.md`
- **可选 JSON：** `/tmp/src-join-calamity-sync-test.json`
- **阻塞点：** 无。
- **范围说明：** 工作区原有的运行态/服务器文件变更未触碰；本次仅落盘本报告及可选 JSON。
