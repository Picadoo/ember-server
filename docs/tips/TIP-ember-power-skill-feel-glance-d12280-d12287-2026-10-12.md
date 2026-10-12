# tip · TIP-F 技能手感一眼（枢纽/技能页 · 三套烬斩分型可辨 · 点进技能组）· D12280–D12287

> **状态：** CLOSED · live 1.65.763-d12287（2026-10-12 16:00 Asia/Shanghai）  
> **上游：** [`DESIGN-ember-growth-variety-thin-2026-10-12.md`](../design/DESIGN-ember-growth-variety-thin-2026-10-12.md) · [`DESIGN-ember-power-paths-variety-2026-10-12.md`](../design/DESIGN-ember-power-paths-variety-2026-10-12.md) · 既有 skill-kit / SF1–SF3 / D617  
> **前置：** TIP-A～E CLOSED live（至 1.65.762-d12279）  
> **施工岗：** 插件岗（TrMenu + 纯展示短串；**零改 skill_mult / CD / 几何数值**）  
> **硬禁：** **零伤乘 · 零抬 skill_mult · 零 Affix Pack** · 不发明新主循环 / 新技能键 · lore 非空 ≤3 · 不改 Economy/pom 脏树

---

## 0. 一句话

枢纽「技能组」悬停就能分清 **焚烬 / 烬爆 / 炽愈** 各自烬斩（形状+落点）差在哪；点进技能页对照。只表面化既有分型，不新造出招。

---

## 1. 既有分型（研究结论 · 勿另发明）

| 族 | 荐烬斩形状（`preferredShape`） | 落点半行（`setFalloutLabel`） | 玩法 cue（`EmberSetFeel.familyCue`） | 身法变体（2pc+Q05，已有） |
|----|-------------------------------|-------------------------------|--------------------------------------|---------------------------|
| 焚烬 scorch | 扇形 FAN | 点燃/火痕 | 点燃打首领 | 火痕步 |
| 烬爆 burst | 直线 LINE | 缓速/闪 | 炸圈清群 | 爆闪步 |
| 炽愈 sustain | 环斩 RING | 微疗/护 | 回血站桩 | 承护步 |

已有展示串：

- `%corerpg_kit_set_hope%` → `EmberSkillUnlock.setHope`：`套装手感 · {族} · 形状 {荐} · 落点 {fall}`  
- `%corerpg_kit_shape%` / `kit_step_dir` / `kit_next_hope`  
- `EmberSkillFeelSf.setShapeHalf` / `shapeShort`  

**缺口：** 枢纽 `S` 只写「形状+身法」，**看不见三套差**；技能页 Open 有 `kit_set_hope` tell，但状态格把 next+set 挤一行，对照弱。

---

## 2. 方案 M（定稿）

### 2.1 短串（展示 only · 复用标签）

| ID | 项 |
|----|----|
| D12280 | 本 tip |
| D12281 | `EmberSkillFeelSf.menuLine(family)`（或 `setHope` 薄封装）：目标形态 `§e焚烬烬斩 §f扇形 §8· 点燃/火痕` / 未成套时 `§8未成套 · 成套后烬斩形状与落点随族变`。内部只拼 `familyName` + `preferredShapeLabel` + `setFalloutLabel`（可附 `familyCue` 四字）——**禁止**新 period、新 skill 系数、新几何 |
| D12282 | PAPI：**优先**让 `%corerpg_kit_set_hope%` 改显示走 `menuLine`（技能页 Open/状态格同步变短）；若改显示会弄乱聊天长句，则 **仅加** `%corerpg_kit_set_line%` 一处挂菜单。禁止再拆 set_feel_scorch/burst/… 丛林 |

### 2.2 枢纽 `S`（已有格 · 不新占 Layout 字母）

| ID | 项 |
|----|----|
| D12283 | `ember_hub.yml` Icon `S` lore **恰好 3 非空行**：`§f%corerpg_kit_set_hope%`（或 `kit_set_line`） / `§7形状 §f%corerpg_kit_shape% §7· 身法 §b%corerpg_kit_step_dir%` / `§e➥ §f打开技能组`；actions 保持 `menu: ember_skill_kit`；**零**伤乘 |

### 2.3 技能页对照（一眼分清三套）

| ID | 项 |
|----|----|
| D12284 | `ember_skill_kit.yml`：状态格（或新增一格「三套差」）lore ≤3，**静态**三族半行对照（文案来自上表，不挂战斗公式），例：`§6焚烬§7扇·点燃` / `§c烬爆§7直·缓速` / `§a炽愈§7环·微疗`；若占格紧张，则状态格 L1=`kit_set_hope` 短串、L2=`kit_next_hope`、L3=点击刷新——**禁止** Open 再堆形状教学墙 |
| D12285 | Open tell：保留 **一行** `§e套装手感：§f%…%`；删与之重复的长说明；烬斩/烬突/符文格 lore 各 ≤3（形状格可写「荐：焚扇/爆直/愈环」半行指针） |
| D12286 | 成套推荐形状：现网 `EmberLoadoutService` 成套 tell / soft prefer **保持**；本 tip 不改 `preferredShape` 映射，只保证菜单与之一致 |

### 2.4 验收

| ID | 项 |
|----|----|
| D12287 | 验收：枢纽悬停能读出 **当前族（或未成套）+ 形状/落点差**；技能页能 **对照三套** 或至少看清当前套；点击进技能组；`skill_mult`/CD/几何/伤乘单测 **不动**；无 Affix / Economy / pom |

### 2.5 玩家口径（固定）

- 「技能手感差」= **烬斩荐形状 + 套装落点半行**（及可选四字 cue）；身法变体名可作次行，**不**新开第四技能。  
- 签名改形状（直线穿刺/环斩/蓄力等）仍属签名页；本格 **不**把签名战力写进 lore。

---

## 3. 明确不做

- 抬 skill_mult、改 1.5B、改 CD、改扇/直/环几何数值  
- 新主循环、第四主动键、Affix Pack、伤乘  
- 新 pity / 新套装公式；把 familyBlurb 长句整段贴回枢纽  
- 同构 forge twin / gem flat densify  

---

## 4. 验收清单

- [ ] 枢纽 `S`：三行内能区分「有套时形状+落点」vs「未成套」  
- [ ] 技能页：三套差可对照（静态三行或当前短串+指针）  
- [ ] hub/skill_kit lore overs>3 = 0  
- [ ] 点击 `S` → `ember_skill_kit`  
- [ ] `git show` 无 Affix / skill 数值表无关脏文件；密码检查 0  
- [ ] reload 记「待恢复服后实测」

---

## 5. 批注

- [x] 总控批 **A · 方案 M** · 2026-10-12 15:56 CST
- tip 已推 · 交插件岗
- 变强途径续：**TIP-F**；研究既有分型，不发明新主循环

## STATUS: CLOSED · live 1.65.763-d12287 · 2026-10-12 16:00 CST
