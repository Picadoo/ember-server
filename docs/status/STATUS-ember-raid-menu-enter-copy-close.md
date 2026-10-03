# STATUS — B2.110 TrMenu ember_raid.yml L76-77 进本 click tell · 结案

Date: 2026-10-01 13:25 Asia/Shanghai
Owner: 余烬-总控（Grok Bot）

## Verdict
**PASS · 勾销**（reload 实测：待恢复服后实测，SKIP 不挡）

## Tips
- 设计 `4aff2ee` · 批准 `a63f4ac` · 插件 `22b1084` · 测 `e405013`
- 报告：`docs/tests/TEST-B2.110-raid-menu-enter-tell.md`

## Acceptance (测岗)
- 仅 `plugins/TrMenu/menus/ember_raid.yml` L76/L77 2+/2-：「团本已点燃。分路推进……」→「§9[团本] §7尝试进入……」；「消耗 §e50 §8体力 · 人数 3～5」→「§8人数 3～5」；逐字一致，按字节无 tab/CR/行尾空格；L22/L78-80/L109/L118 未动
- `/tmp/chk-b2110.js 22b1084` → `only-L76-L77 /Icons/S/actions/all/1 /Icons/S/actions/all/2`；旧对旧/新对新 exit 1；变异 exit 4/3/1；独立 deep diff 仅两处
- rg 0 命中
- 口径：等级不足、体力不足、周首免、付费、OP、DP 启动失败退还六种情况插件均有私聊；灰显图标 ember_raid.yml:42-55 priority 2 接管点击（依据实测 docs/status/STATUS-ember-enter-stamina-gray-test.md:69）；新句与 DP option.yml L21/L22 不重复，人数与 option.yml:16 一致
- 无脚本点开 ember_raid 菜单；HANDOFF §8 查密码 0；`ops.json` = `[]`

## Corrections (不影响结论)
- 校验脚本 exit 5 分支单边改动走不到（会先在 L85 exit 1），仍会拦住。
- 「refundEnter 退还」过于笼统，例外另开 B2.119。
- blocked_raid 不区分 OP（CoreRpgExpansion:115-119），只记。
- ember_abyss.yml:77 写死「体力 30」即下一窗 B2.111。

## Later candidates
- **B2.119**：TicketEntryService 退还提示例外（需 build，排 B2.108 后）
- B2.109 追加：abyss-followup.js:58；团本菜单点击用例

## Next
- **B2.111**：TrMenu `ember_abyss.yml` L77 → 策划出稿
