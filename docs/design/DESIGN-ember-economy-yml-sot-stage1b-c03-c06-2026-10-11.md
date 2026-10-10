# DESIGN · yml-SoT Stage1b · C03–C06 / C11 叶子草案（2026-10-11 · D966）

> **状态：C03 已落地 D969–D973；C04–C06/C11 仍文档 only。数值不变。**  
> **上游：** Stage0 §2 / §4；Stage1 已切 C07/C12/C13/C20/C22–C24。  
> **为何单独开：** C03–C06 / C11 登记行**无** `golden` / economy 叶子；价在 `EmberUpgradeRules` 数组与洗练路径。搬家需先定表形，否则易漂。

---

## 1. 现行 Java 真源（只读盘点）

### C03 强化 `enhanceCost(currentEnh)` → 目标档 `t = current+1`（1..10）

| t | shard | core | coin | rate | max_tries |
|---|------:|-----:|-----:|-----:|----------:|
| 1 | 4 | 0 | 40 | 1.00 | 1 |
| 2 | 6 | 0 | 60 | 1.00 | 1 |
| 3 | 8 | 0 | 80 | 1.00 | 1 |
| 4 | 12 | 0 | 120 | 0.85 | 3 |
| 5 | 16 | 1 | 180 | 0.70 | 4 |
| 6 | 22 | 1 | 260 | 0.55 | 5 |
| 7 | 30 | 2 | 380 | 0.40 | 6 |
| 8 | 40 | 3 | 540 | 0.30 | 8 |
| 9 | 54 | 4 | 760 | 0.22 | 10 |
| 10 | 72 | 6 | 1080 | 0.15 | 12 |

（`RATE` / `MAX_TRIES` 属强化行为，**可**同迁或留 Java；Stage1b 默认**只迁材料+币**，rate/tries 仍 Java。）

### C04 升阶 / C05 精工 / C06 成色

见 `EmberUpgradeRules` `ascend*` / `craft*` / `qualityCost`（本 Stage1b 开窗前再贴全表；形状同「按档叶子」）。

### C11 洗练

登记无 golden；价在 Growth/Pay 路径。Stage1b 开窗时先 `rg` 实扣再定叶键。

---

## 2. 提案 yml 形（C03 示例）

```yaml
sinks:
  C03:
    # key = target enhance level
    "1.shard": 4
    "1.coin": 40
    "5.shard": 16
    "5.core": 1
    "5.coin": 180
    # …
```

或嵌套：

```yaml
  C03:
    levels:
      1: { shard: 4, coin: 40 }
      5: { shard: 16, core: 1, coin: 180 }
```

**裁决倾向（待开窗确认）：** 扁平 `"N.mat"` 与现有 `S23.daily.coin` / `C23.blank_t1` 一致，少改 `amount()` 解析。

---

## 3. 施工切片（以后）

1. 导出 Java 数组 → economy yml 叶子 + `EmberEconomy` `.g()` 金样。  
2. `enhanceCost` 改读 `amount("C03", t+".shard")` 等；缺键 fail-closed。  
3. 单测：逐档与旧数组逐位相等；p1sim 不变。  
4. 再 C04→C05→C06→C11。

**禁区：** 不改数字；不碰 gem flat / skill_mult / K3 / AFK / sticky；不假 FULL modelGaps。

---

## 4. 本窗附带

- D967：`EmberCountersTest` 再验 PASS（D942–D965 无新计数器键）。  
- D968：ARCH/STATUS 指向本文。

