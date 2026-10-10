# D605 技能希望 · 套装分叉 p1sim 回归（Stage B1）

**日期：** 2026-10-11
**钉：** `P1SIM_RULES=out-guard-parry-t0b-rules-export.json` · n=1500 · 同 pkl 对照
**纪律：** 禁抬 skill_mult · live 定稿值 D434/D436/D438 · 相对 `D_ctrl_*`（烬突）或 base（烬斩/身法）判 ±2
**批：** 总控 A · 方案 M on DESIGN-ember-skill-hope-set-fork

## 1. 烬突身份 vs D_ctrl（DS15 无身份）

| 候选 | 套装 | 可达 | ±2内 | maxΔpp | minΔpp | mean|Δ| | 判定 |
|---|---|---|---|---|---|---|---|
| **D_ig012** 点燃×0.12 | scorch | 36 | 36/36 | +0.0 | +0.0 | 0.00 | ✅ |
| **D_sl15** 缓速1.5s | burst | 36 | 36/36 | +0.4 | +0.0 | 0.01 | ✅ |
| **D_h005** 疗0.005H | sustain | 36 | 35/36 | +2.9 | -0.1 | 0.56 | 🟡 |

## 2. 叠乘 live（烬突身份 + 身法/烬斩落点）vs D_ctrl

| 候选 | 套装 | 可达 | ±2内 | maxΔpp | minΔpp | mean|Δ| | 判定 |
|---|---|---|---|---|---|---|---|
| **H_live_scorch** 点燃+火痕 | scorch | 27 | 27/27 | +1.5 | -0.0 | 0.18 | ✅ |
| **H_live_burst** 缓突+爆闪+烬斩缓 | burst | 27 | 26/27 | +2.6 | -0.1 | 0.47 | 🟡 |
| **H_live_sustain** 突疗+斩疗 | sustain | 27 | 22/27 | +3.3 | +0.0 | 1.14 | ⚠ vs 裸 D_ctrl |

公平对照（叠乘增量）：

| 叠乘 | vs | ±2内 | mean|Δ| | 判定 |
|---|---|---|---|---|
| H_live_scorch | D_ig012 | 27/27 | 0.18 | ✅ |
| H_live_burst | D_sl15 | 26/27 | 0.47 | 🟡 |
| H_live_sustain | D_h005 | 26/27 | 0.64 | 🟡 |
| H_live_sustain | S_h002 | 27/27 | 0.40 | ✅ |

## 3. 烬斩 / 身法 vs base

| 候选 | 套装 | 可达 | ±2内 | maxΔpp | minΔpp | mean|Δ| | 判定 |
|---|---|---|---|---|---|---|---|
| **S_sl10** 烬斩缓1.0s | burst | 39 | 39/39 | +1.4 | -0.1 | 0.14 | ✅ |
| **S_h002** 烬斩疗0.002H | sustain | 39 | 35/39 | +2.5 | +0.0 | 0.67 | 🟡 |
| **F14c0n1_d434** 火痕步对照 | scorch | 27 | 27/27 | +1.5 | -0.0 | 0.18 | ✅ |
| **B14c0** 爆闪缓1.5s | burst | 27 | 26/27 | +2.8 | -0.1 | 0.33 | 🟡 |

## 4. 结论 · **D605 PASS**

- **单件回归：** D_ig012/D_sl15 ✅ · D_h005 🟡 · S_sl10 ✅ · S_h002/B14c0 🟡 · F14c0n1 ✅ — 与 D434/D436/D438 定稿同向。
- **叠乘：** 相对同身份单件（D_ig012/D_sl15/D_h005）全 ✅/🟡；相对裸 D_ctrl 的承烬叠疗出格为预期（双疗），**不改系数**。
- **禁改：** skill_mult / STAMP / sock / K3 / AFK / sx20。
- 下一步：D606 里程碑文案船。

复现：
```bash
P1SIM_RULES=out-guard-parry-t0b-rules-export.json NPROC=6 \
  python3 -u skillkit.py run 1500 /tmp/sk/d605c.pkl \
  D_ctrl_scorch,D_ctrl_burst,D_ctrl_sustain,H_live_scorch,H_live_burst,H_live_sustain,\
  D_ig012,D_sl15,D_h005,S_sl10,S_h002,F14c0n1_d434,B14c0
```

*D605 B1 · 2026-10-11*
