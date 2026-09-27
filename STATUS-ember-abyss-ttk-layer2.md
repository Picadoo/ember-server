# STATUS · 深渊看守·深 TTK 复测（天赋二层后 · 阶段 4.3）

**日期：** 2026-09-27 16:41～16:47（Asia/Shanghai / CST）  
**执行岗：** 余烬-测试  
**依据：** `docs/design-stage4-abyss-9-12.md` §2.3；前置 `STATUS-ember-talent-layer2.md` **PASS**  
**账号：** 主测 **`Atk2_4288`**（非 op）；管理 `RpgBot`（uuid `148ec8b6-253f-36f4-bcd7-bf2395b8df80`，仅测时 op）  
**脚本：** `/tmp/abyss-ttk-l2.js` · stdout `/tmp/abyss-ttk-l2-out.txt` · 标记 **`ABYSS_TTK_L2_RESULT`**  
**未改：** 怪物 / DP / CoreRpg YAML / jar（只建议）

---

## 总评

# **FAIL**

参照档（单人烬刃 · T2刃+6～+8 · T2符 · 天赋满二层≤30 · Lv40+）下，第 10 / 12 层「看守·深」被轻易秒杀（**TTK ≪ 30s**），结束剩余生命接近满血，远低于 §2.3 窗口。

---

## 第 10 / 12 判定

| 层 | 目标 TTK | 实测 TTK | 目标剩血 | 实测剩血 | 判定 |
|----|----------|----------|----------|----------|------|
| **10 看守·深** | 50～80s | **≈5.0s** | 25～50% | **≈100%** | **FAIL** |
| **12 看守·深** | 55～90s | **≈2.0s** | 25～55% | **≈98%** | **FAIL** |

硬规则：烬刃轻易 **&lt;30s** 清掉 → **FAIL**（两层均触发）。

### TTK 取证（游玩服 `latest.log`，CST）

| 事件 | 时间 |
|------|------|
| 第9→10（floor10 开打/进度） | 16:45:31 |
| 第10→11（floor10 通过） | 16:45:36 → **TTK_10 ≈ 5.0s** |
| MM 刷 `EmberAbyssWatcherDeep`（floor12）WARN | 16:45:39 |
| 第11→12（floor12 进度 / Boss 击杀） | 16:45:41 → **TTK_12 ≈ 2.0s** |
| 顶层 settle COMPLETE | 16:45:43 |

Bot 内置时钟（0.5s / 1.8s）因「出现检测」滞后略偏短；以上以服务端 progress 间隔为准。  
层间隔另含 DP `delay=3`；上表已用 progress 点差，不把层间等待算进 Boss TTK。

---

## 参照档落实

| 项 | 值 |
|----|-----|
| 等级 | **Lv.60**（earned=30） |
| 誓约 | 烬刃 blaze |
| 天赋 | 一层满 + 二层 `blaze_ember2` / `blaze_heat2` / `blaze_second` · **已花=30 / 已获=30** |
| 武器 | `gear_ember_t2_blade` **+7** · Sharpness III |
| 饰品 | `gear_ember_t2_talisman` **+4** |
| 属性（进本前） | 攻击 **+55.0** · 生命 **103.0** · 减伤 52.4% · 暴击 5.5% |
| 补给 | 面包×64 · `ticket_ember_abyss` |
| 战斗 buff | **无**（不做 resistance/strength；平衡主证据） |
| 进本 | `/ember` → 深渊 → 开始下潜 · **PASS** |
| 清层 | 自然 1→12（真实 DP 刷怪 / 真实 Boss 战）· 死亡 **0** · 整局战斗 ~106s · hits 125 |

---

## 附加发现（影响血量兑现）

刷 `EmberAbyssWatcherDeep` 时 MythicMobs **两次**报错：

> Mob HP is greater than server's maxHealth setting …

| 配置 | 值 |
|------|-----|
| `EmberAbyss.yml` Health | **2200** |
| `server-runtime/spigot.yml` → `settings.attribute.maxHealth.max` | **2048.0** |

→ 配表 2200 **无法完整生效**（被上限卡住），秒杀问题在「面板本就偏脆」之外还有 **服务器属性上限** 缺口。

普通看守 `EmberAbyssWatcher` Health=1500 &lt; 2048，不受此 WARN。

---

## 调参建议（只建议 · 本岗不改）

1. **先抬** `spigot.yml` `attribute.maxHealth.max` 到 **≥ 目标 Deep 血量**（例如 10000+），否则 YAML 写再高也会被封顶并打 WARN。  
2. **大幅抬** `EmberAbyssWatcherDeep` **Health**（现 2200；在满二层烬刃攻≈55 下 TTK 仅数秒，估需数倍～一个数量级才接近 50～80s / 55～90s）。  
3. **酌情抬 Damage**（现 4），把结束剩血压进 **25～50%（10）/ 25～55%（12）**；当前秒杀导致剩血≈满。  
4. 改完后用同参照档（Lv40+ · 满二层 · T2刃+6～+8 · 无战斗 buff）再跑本脚本复测。  
5. **不要**为达标去削玩家天赋/装备；4.3 目标就是二层上线后的压力复测。

---

## ops / 端口

| 步骤 | 结果 |
|------|------|
| 停游玩服 → 写 ops RpgBot op4 | OK（16:41） |
| `JAVA_HOME=.../jdk8u504-b01 ./start.sh custom` | Done **16:41:43** |
| 测中临时 `/op Atk2_4288` 仅用于 `enhance set`，随即 `/deop` | OK |
| 测完停服 → **`ops.json=[]`** → 再起 | Done（pid 见 runtime；**16:47 前后**） |
| 三端口 | **25565** 代理 · **25566** 登录 · **25567** 游玩 · 均 LISTEN |
| 结束后 ops | **`[]`** |

---

## 产物路径

| 路径 | 说明 |
|------|------|
| `/workspace/minecraft/STATUS-ember-abyss-ttk-layer2.md` | 本报告 |
| `/tmp/abyss-ttk-l2.js` | 测法脚本（勿提交） |
| `/tmp/abyss-ttk-l2-out.txt` | `ABYSS_TTK_L2_RESULT` JSON |
| `server-runtime/logs/latest.log` | 16:45:31～16:45:43 progress / MM WARN 证据 |

---

## ABYSS_TTK_L2_RESULT（摘要）

```
verdict=FAIL
account=Atk2_4288
TTK_10=5.0  endHpPct_10=100  judge_10=FAIL
TTK_12=2.0  endHpPct_12=98   judge_12=FAIL
deaths=0  level=60  blade=+7  tal=+4  talent_spent=30
```

---

## 4.3 复测（抬血后）

详见 **`STATUS-ember-abyss-ttk-4.3.md`**（2026-09-27 16:50～17:02 CST）。

| 层 | 抬血前 | 抬血后（Health 20000 / Dmg 7） | 目标 | 判定 |
|----|--------|-------------------------------|------|------|
| 10 | ≈5.0s / ≈100% | **≈204.8s / ≈100%** | 50～80s / 25～50% | **FAIL**（过长） |
| 12 | ≈2.0s / ≈98% | **≈204.7s / ≈100%** | 55～90s / 25～55% | **FAIL**（过长） |

总评：**FAIL**（已非秒杀，但 TTK 过长、结束剩血仍满）。建议下调 Health≈5500～9000、抬 Damage≈14～22。
