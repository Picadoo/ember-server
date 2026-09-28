# STATUS · 日常第二房 / Boss 前门压试点

**日期：** 2026-09-28（Asia/Shanghai）  
**岗：** 余烬-总控  
**依据：** `docs/design-ember-daily-room2-variance.md`（**已批准**）

## Verdict

**🚧 施工中**（仅三线：庭院 / 潮蚀 / 断塔）

| 线 | 杠杆 | 要点 |
|----|------|------|
| EmberDaily | Boss 前门压 | door2→门槛尸×2→Boss；wave1=4；房2不动 |
| EmberDailyTide | Boss 前门压 | door2→门槛浪矢×2→Boss；wave1=4；房2不动 |
| EmberDailySpire | 房2链式重叠 | wave2a start→delay2 wave2b；开门在 wave2b |

- 霜晶 / 锈轨 / 非试点：**零 diff**
- 不加血；不动体力/箱/掉落/Boss HP

## Checklist

- [x] 设计批准
- [ ] DP `monster.yml` 三线落地
- [ ] §8 短抽验收
