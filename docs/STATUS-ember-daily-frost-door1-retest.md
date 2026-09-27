# STATUS · 霜晶 door1 定向复测

**日期：** 2026-09-28 06:13:00 Asia/Shanghai  
**岗：** 余烬-测试岗执行器  
**范围：** 仅霜晶房1（`daily_frost` / `EmberDailyFrost`）· 不必通关全本  
**依据：** 前报 FAIL `docs/STATUS-ember-daily-wave-variance-test.md` · 修复 `5f29534` `EmberDailyFrost/monster.yml` 链式返工  
**测前：** play FIFO 控制台 `dp reload` → `[06:08:34] [DungeonPlus] 插件重载完毕`（**RCON 关** `enable-rcon=false`）  
**Verdict：** **✅ PASS**（三条硬条全过 · ops=`[]`）

---

## 一句话

链式 `$kill` 返工后复测：左刷后 Δ≈**3.9s** 右刷可辨；**仅清左**时 door1 仍 **iron_bars×9**（右×2 仍在）；清右后 **AIR×9** +「冻台已清 · 冰闸开了」。真击杀、禁 killall、禁改配置、未 commit/push；测后 ops play+login=`[]`。

---

## 测前 YAML 确认

`plugins/DungeonPlus/dungeon/EmberDailyFrost/monster.yml`（与 `server-runtime` 同 hash）：

| 组 | 刷怪 | `$kill` | 开门 |
|----|------|---------|------|
| `wave1` | 左×3（x≈-3） | `霜晶尸×3` | 否；`start` 链式 `$monstergroup{wave1b;delay=3}` |
| `wave1b` | 右×2（x≈3） | `霜晶尸×2` | **是** → z=16×9 AIR +「冻台已清 · 冰闸开了」 |

注释已写明：勿 `$kill×5` 合并（跨组不计）；开门挂在 `wave1b`。

---

## 三条硬条

| # | 硬条 | 结果 | 证据 |
|---|------|------|------|
| 1 | 左刷后 ≈3s 右刷（交错仍可辨） | **✅ PASS** | `tLeft`→`tRight` Δ=**3947ms**（左新 ID → 右新 ID）；文案「左冻台…右侧将交错刷出」→「右冻台·交错侧袭」 |
| 2 | 左右全清后 door1 z=16 **AIR** +「冻台已清 · 冰闸开了」 | **✅ PASS** | 清右后 door1 **air×9**；聊天实锤该句；并见「左侧冻台已清」 |
| 3 | **未清右时**门仍铁栅（右怪计入开门） | **✅ PASS** | 右刷前速清左 → 采样时右 ID×2 仍存活 · door1 **iron_bars×9** · 无提前「冰闸开了」→ 再清右才 AIR |

---

## 证据摘要

- **玩家：** `WvFd513`（短唯一 bot）· OP 临时 `WvWv70`  
- **交错：** Δ=**3947ms**（约 3.9s，可辨；落在 ≈3s±2s 窗）  
- **仅清左采样：** `iron_bars` @ `(-1..1,70..72,16)` 共 9；`rightStill` id=7745/7746；`leftClearedMsg=true`；`doorOpenAtSample=false`  
- **全清后：** `air×9`；文案序列：  
  `【裂隙·左冻台】…` → `左侧冻台已清` → `【裂隙·右冻台】…` → **`冻台已清 · 冰闸开了`**  
- **击杀：** 脚本仅 `attack`/`swingArm`；**禁 killall**  
- **JSON：** `/tmp/frost-door1-retest.json`

### 方法说明（硬条3）

DP `$kill` 按组独立、计同 Display 名。若右组已启动后再杀左残留，击杀会计入 `wave1b` 导致「未清右却开门」。本复测在右刷前速清左（`left_done_before_right_spawn=true`），再在右×2 存活时采样门材质，避免误判。

---

## 环境

| 项 | 值 |
|----|----|
| commit（修复） | `5f29534` fix(dp): frost door1 chain kill like rail |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 端口 | proxy 25565 / login 25566 / play 25567 |
| 热更 | FIFO `console.in` → `dp reload`（06:08:34 CST） |
| 工作区 | `/workspace/minecraft` |
| 禁改配置 / 未 commit/push | ✅ |

---

## 成功标准核对

| 标准 | 状态 |
|------|------|
| 报告落盘 | ✅ 本文件 |
| 结论明确 | ✅ **PASS** |
| 三条硬条 | ✅ / ✅ / ✅ |
| ops 清空 | ✅ play=`[]` · login=`[]`（De-opped 06:12:56） |
| 未 commit/push | ✅ |
| 禁改配置 | ✅（仅读 YAML + `dp reload`） |

---

## 阻塞点

无。前报霜晶门计数 FAIL 已由链式 `$kill` 修复并经本定向复测关闭。

**最终结论：** 霜晶 door1 复测 **PASS**。
