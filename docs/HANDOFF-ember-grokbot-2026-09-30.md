# 余烬服 · Grok Bot 交接（额度切换）

Date: 2026-09-30 22:44 Asia/Shanghai  
From: 余烬-总控（本号额度尽，全面停工）  
To: 下一任 Grok Bot / 总控  
Repo: https://github.com/Picadoo/ember-server · branch `main`  
Workspace: `/workspace/minecraft`

## 0. 一句话

拉最新 `main`，从 **B2.90 PASS · 勾销** 之后继续；**不要**再开本号未交的 B2.91。文案薄窗流水线可接着做，或按用户新指示转向内容/平衡。

## 1. HEAD（交接提交完成后）

- 最后玩法/文案施工 tip：`57cd7de` — `docs(corerpg): B2.90 cash weekly free_tickets 注释`
- 测报 tip：`fb06ed1`
- 本交接 + B2.90 close：见本文件同批 commit（push 后以 `git log -1` 为准）

## 2. 刚结窗（近期薄窗 continuum）

| 窗 | 对象 | 结果 | 关键 tip（约） |
|----|------|------|----------------|
| B2.85 | cash raid free_tickets 注释 | PASS · 勾销 | close `c2fa2bf` 一带 |
| B2.86 | cash weekly shop hard_cap 注释 | PASS · 勾销 | close `0cb448a` 一带 |
| B2.87 | progress gate「扣票」旁注 | PASS · 勾销 | close `1330cad` |
| B2.88 | ember_shop 周体力包 lore | PASS · 勾销 | close `cd4e07c` · 施工 `45308ff` |
| B2.89 | ember_shop 文件头 SKU | PASS · 勾销 | close `f1977ce` · 施工 `245fe40` |
| **B2.90** | cash weekly.free_tickets 旁注 | **PASS · 勾销** | 设计 `838cb7d` · 批准 `74509b6` · 插件 `57cd7de` · 测 `fb06ed1` |

权威 backlog：`docs/design-ember-content-backlog.md`

## 3. 流水线（保持）

1. 策划 tip（ahead，勿 push）→ 总控核对 → **代推**设计 tip → 批 **A**（tip STATUS + backlog）→ commit+push 批准  
2. FYI 策划 `priority:false`；交专岗施工 `priority:true`  
3. 专岗本地 commit → 总控核对 **代推** → FYI 专岗；交测岗静态 `priority:true`  
4. 测 PASS（本地测报）→ 总控代推测报 → tip **PASS · 勾销** + close tip + backlog → push  
5. FYI 测试 `priority:false`；交策划下一窗 `priority:true`（除非用户要求停）  
6. 对用户短里程碑  

设计先于施工：策划交稿 → 总控批 → 施工。薄窗优先；禁无必要长测。挑刺玩家仅重大里程碑。

## 4. 专岗（agent id）

| 岗 | id | 职责摘要 |
|----|-----|----------|
| 余烬-总控 | （本会话） | 调度、批 A、代推、close、交接 |
| 余烬-策划 | `c6e68449-f367-47c1-ae09-8858722b2730` | tip 设计稿 |
| 余烬-插件 | `7be5df13-4392-4727-b146-bd2877280d13` | Core* / TrMenu YAML |
| 余烬-物品 | `3d5e5d27-2acc-40e1-bf6e-df93dd03e64b` | NeigeItems |
| 余烬-怪物 | `9e72a040-7213-4256-9dc0-317338b0a200` | MythicMobs |
| 余烬-测试 | `2c4a9fd5-b44c-4e44-aa67-369cfef70c78` | 静态/冒烟验收 |
| 余烬-Paper | `a2958879-69d1-41e8-8a3c-374554adc5d3` | Paper 1.12.2 / NMS |
| 余烬-挑刺玩家 | `a1d7c59b-21f1-4286-baeb-59d836c09825` | 仅重大里程碑 |

### 4.1 新号专岗 id（2026-10-01 重建，替代上表）

| 岗 | id |
|----|-----|
| 余烬-总控 | Grok Bot（用户主 bot） |
| 余烬-策划 | `140d4710-e7d4-45ca-ad4a-dba75abe0af3` |
| 余烬-插件 | `78749aa1-2fd7-4843-9173-642238b4f65f` |
| 余烬-物品 | `3c51611a-c47b-43d4-911b-b123b055f60b` |
| 余烬-怪物 | `8957a58f-aaea-430d-8314-334ec6ee544d` |
| 余烬-测试 | `a46f07ef-0c32-4594-82f3-1ed3fcd46b3b` |
| 余烬-Paper | `8724bc4b-4240-4e72-a307-cdb04a7a9c7b` |
| 余烬-挑刺玩家 | `2fa6b735-502d-4d68-afa2-aa7d350dce80` |

## 5. 锁定偏好（勿破）

- TrMenu 玩家 UX；玩家不打 slash；自定义物走 **NeigeItems ID**（禁显示名匹配）
- 同服 Multiverse；体力门（非票观感）；calc-first 平衡
- 定期 push `Picadoo/ember-server`；secrets / 大 jar / 大地图 **勿进 git**
- **勿宣称**：B0.1 已清、票已废、战令/勋阶/活动/深渊·灾厄完整落地、地图已正式、PAPI·日限已接入、LuckPerms·勋阶完整

## 6. 建议下一薄窗（候选，未立项）

按优先级自拣一窗即可；**勿捆厚窗**：

1. `plugins/CoreRpg/cash.yml` ~L89 `abyss.free_tickets: 0`（无旁注）→ 对齐 weekly/raid 维护备忘  
2. 同文件 ~L94 `elite.free_tickets: 0`（段首已有维护备忘，可只补行内或跳过）  
3. DP `dungeon/*/option.yml` 历史「扣票 / B0.1 / 余烬周票」注释 → 体力口径维护备忘（**一文件一窗**；勿宣称 B0.1 已清）  
4. soft：NI `ember-dungeon-tickets.yml` 显示名仍「余烬日票」等（迁移期可兑换；改名需设计）  
5. soft 玩法债：断塔近阶偶发掉底厅；霜/锈无 Boss 前压；AFK 二档通胀  

明确不做（除非用户新令）：四件甲 / 誓约主动大改 / 锻炉重做 / 骨饰双上 / 位移+保命双上 / 无证据重开 B0.1 / 精英预览壳硬开。

## 7. 脏文件（永不 commit）

工作区常见脏项，**一律勿 add**：

- `plugins/CoreRpg/calamity-state.yml`、`ladder.yml`
- `plugins/CoreRpg/players/**`
- ProtocolLib `lastupdate`、Adyeshach 试验 NPC、mineflayer 试验脚本
- `secrets/*.env`、大二进制、世界存档

## 8. Live 事实抽检（交接时）

- shop L151：`§8限购 1/周 · 周硬顶 免费1+购买≤2`
- shop 文件头 L2–3：展示名 + SKU 别名备忘（无「原日票/周票」）
- cash L83：`free_tickets: 0          # 维护备忘：遗留票物/体力口径；周本 free_tickets 发放口径留档（现行 0）`
- progress 等级门槛旁注：遗留票物/体力口径维护备忘（B2.87）

## 9. 新号启动清单

1. `git clone` / `git pull` `Picadoo/ember-server` → `main`  
2. 读本文件 + `docs/design-ember-content-backlog.md` 顶部 Progress snapshot  
3. 确认脏文件未入 index  
4. 向用户确认：继续文案薄窗（从 §6 候选）还是切内容/平衡  
5. 用专岗 id 发任务；代推 ahead；勿让专岗自行 push  

## 10. 停工声明

本号（交接触发账号）在 B2.90 close + 本文件 push 后 **全面停止**：不再批 A、不再交策划/插件/测试新窗、不再升 B2.91。
