# STATUS · 源码模板 join + 灾厄广播去指令同步

**日期：** 2026-09-28 08:50 CST（Asia/Shanghai）  
**岗：** 余烬-插件  
**依据：** 总控【派工 · 源码模板/灾厄广播去指令同步】· B1.4 测报轻债  
**Verdict：** ✅ **施工+短抽 PASS**（`30886e2` · 测报另 commit）

---

## 一句话

`src/config.yml` 的 `join_message` 四行已对齐 live（菜单/右键灰烛，无 `/corerpg` `/ember`）；灾厄 `message_pre` / `message_open` 双端改为「打开枢纽菜单 → 灾厄 → 奔赴」。B1.4 已结 quest hint **未动**。

---

## 改动

| 路径 | 动作 |
|------|------|
| `CoreRpg/src/main/resources/config.yml` | `join_message` ×4 对齐 `plugins/CoreRpg/config.yml` |
| `plugins/CoreRpg/calamity.yml` | `message_pre` / `message_open`：`/ember →` → `打开枢纽菜单 →` |
| `CoreRpg/src/main/resources/calamity.yml` | 同上 |
| `docs/status/STATUS-ember-src-join-calamity-sync.md` | 本 STATUS |

**未改：** live `plugins/CoreRpg/config.yml` join（已合规）；`plugin.yml` usage；YAML `#` 运维注释；灾厄数值/窗期/刷怪；`quest.yml`。

### join 对齐后（src）

1. 灰烛就在出生点北边——右键他看当前目标  
2. 挂机庭…打开枢纽菜单可返回（原样）  
3. 打开枢纽菜单：主页 · 强化 · 镶嵌 · 签到 · 生活补给…  
4. 进本门槛…——打开枢纽菜单进入  

### 灾厄广播

- `message_pre`: `§7打开枢纽菜单 → 灾厄 → 奔赴`  
- `message_open`: 同上后缀  

---

## 热重载

- **时间：** 2026-09-28 **08:50:40** CST  
- **命令：** FIFO → `corerpg reload`  
- **结果：** `[CoreRpg] 配置已重载`

---

## 验收

1. src join 四行与 live 文案一致（无斜杠命令教学）  
2. 灾厄广播无 `/ember` 教学  
3. `quest.yml` git clean  

## Git

- **未 push**（交总控推） · `ops.json=[]`

## Blocker

无。

## Checklist

- [x] src join_message 对齐 live
- [x] calamity 广播双端去 `/ember`
- [x] quest.yml 未回退
- [x] 静态短抽 **PASS**（`docs/status/STATUS-ember-src-join-calamity-sync-test.md`）
