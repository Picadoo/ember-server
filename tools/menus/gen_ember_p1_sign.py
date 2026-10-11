#!/usr/bin/env python3
"""D180/D12201: generates plugins/TrMenu/menus/ember_p1_sign.yml (28 calendar cells + 4 online milestones).
Rewards are NOT hard-coded here: every label comes from PAPI (%corerpg_p1_sign_r<n>% / %corerpg_p1_online_r<i>%),
i.e. from ember-v1.yml signin / online, so the page never drifts from the config. Run from the repo root."""
import os
CELLS = list('1234567890') + list('ACEFGJKLNPQRSTUVWX')  # 28 keys, none of H I M O B # a b c d
BIG = {7, 14, 21, 28}
out = []
w = out.append
w("# P1 每日签到 + 在线时长（D180，docs/design/DESIGN-ember-signin-online-2026-10-04.md）— CoreRpg EmberSignService")
w("# 生成：python3 tools/menus/gen_ember_p1_sign.py（奖励文字全部来自 PAPI = ember-v1.yml signin / online，不手写数字）")
w("# 月历按「本月第 n 次签到」排（HoYoLAB / 黑色沙漠同款累计制）：漏签不清零；第 7 / 14 / 21 / 28 次是大格")
w("# 补签：每月 3 次、每天 1 次，先签今天 + 今天有效在线满 60 分钟；自动补本月最早漏的那天")
w("# 在线：15 / 30 / 60 / 120 分钟有效在线（5 分钟没操作停表；挂机庭里也计时，自动战斗算有操作）")
w("Title: '§6签到 · 在线 §8(%corerpg_p1_sign_month%)'")
w("Options:\n  Arguments: false\n  Default-Layout: 0\n  Hide-Player-Inventory: false\n  Min-Click-Delay: 300")
w("Layout:")
w("  - '#H# I #O#'")
for r in range(4):
    w("  - '#" + ''.join(CELLS[r * 7:(r + 1) * 7]) + "#'")
w("  - ' M abcd B'")
w("Events:\n  Open:\n    - 'sound: BLOCK_CHEST_OPEN-1-0'\n    - 'command: corerpg p1 onlinepath'\n    - 'command: corerpg p1 signpath'")
w("Icons:")
w("  '#':\n    display:\n      material: gray stained glass pane\n      name: '§8 '")
w("""  B:
    display:
      material: arrow
      name: '§7返回主菜单'
    actions:
      all:
        - 'sound: BLOCK_NOTE_PLING-1-0'
        - 'menu: ember_hub'""")
w("""  H:
    update: 20
    display:
      material: book
      name: '§6本月签到'
      lore:
        - '%corerpg_p1_sign_head%'
        - '%corerpg_p1_sign_next%'
        - '%corerpg_p1_sign_hope%'""")
w("""  I:
    display:
      material: paper
      name: '§f规则'
      lore:
        - '§7每天点「今天」格 · 按本月第 n 次发奖（漏签不清零）'
        - '§7第7/14/21/28大格 · 补签月3次（先签今天+在线60分）'
        - '§7在线15/30/60/120可领 · 挂机庭也计时'""")
w("""  O:
    update: 20
    display:
      material: clock
      name: '§a今日在线'
      lore:
        - '§7有效在线：§f%corerpg_p1_online_min% §7分 · %corerpg_p1_online_state%'
        - '%corerpg_p1_online_next%'
        - '§e➥ §f一键领完 · 可领 §f%corerpg_p1_online_can% §7档'
    actions:
      all:
        - 'sound: BLOCK_NOTE_PLING-1-2'
        - 'command: corerpg p1 online claim'""")
w("""  M:
    update: 20
    icons:
      - condition: 'check papi %corerpg_p1_sign_mkcan% > 0'
        priority: 1
        display:
          material: name tag
          name: '§a补签'
          shiny: true
          lore:
            - '%corerpg_p1_sign_mk%'
            - '§7本月剩余 §f%corerpg_p1_sign_mkleft% §7次'
            - '§e➥ §f点击补签'
        actions:
          all:
            - 'sound: ENTITY_PLAYER_LEVELUP-1-1'
            - 'command: corerpg p1 sign makeup'
    display:
      material: name tag
      name: '§7补签'
      lore:
        - '%corerpg_p1_sign_mk%'
        - '§7本月剩余 §f%corerpg_p1_sign_mkleft% §7次'
        - '§8规则：先签今天 · 今天有效在线满 60 分钟 · 每天 1 次'
    actions:
      all:
        - 'sound: BLOCK_NOTE_BASS-1-0'""")
for i, n in enumerate(range(1, 29)):
    k = CELLS[i]
    big = n in BIG
    fut_mat = 'gold ingot' if big else 'paper'
    tag = '§6大格 · ' if big else ''
    w(f"""  '{k}':
    update: 20
    icons:
      - condition: 'check papi %corerpg_p1_sign_d{n}% > 0'
        priority: 2
        display:
          material: lime stained glass pane
          amount: {n}
          name: '§a第 {n} 次 · 已领'
          lore:
            - '§7%corerpg_p1_sign_r{n}%'
      - condition: 'check papi %corerpg_p1_sign_k{n}% > 0'
        priority: 1
        display:
          material: chest
          amount: {n}
          shiny: true
          name: '§e{tag}第 {n} 次 · 今天可签'
          lore:
            - '§f%corerpg_p1_sign_r{n}%'
            - '§e➥ §f点击签到'
        actions:
          all:
            - 'sound: ENTITY_PLAYER_LEVELUP-1-1'
            - 'command: corerpg p1 sign claim'
    display:
      material: {fut_mat}
      amount: {n}
      name: '§7{tag}第 {n} 次'
      lore:
        - '§7%corerpg_p1_sign_r{n}%'
    actions:
      all:
        - 'sound: BLOCK_NOTE_BASS-1-0'""")
for i, k in enumerate('abcd', 1):
    w(f"""  {k}:
    update: 20
    icons:
      - condition: 'check papi %corerpg_p1_online_d{i}% > 0'
        priority: 2
        display:
          material: lime stained glass pane
          name: '§a在线 %corerpg_p1_online_m{i}% 分钟 · 已领'
          lore:
            - '§7%corerpg_p1_online_r{i}%'
      - condition: 'check papi %corerpg_p1_online_k{i}% > 0'
        priority: 1
        display:
          material: clock
          shiny: true
          name: '§e在线 %corerpg_p1_online_m{i}% 分钟 · 可领取'
          lore:
            - '§f%corerpg_p1_online_r{i}%'
            - '§e➥ §f点击领取'
        actions:
          all:
            - 'sound: ENTITY_PLAYER_LEVELUP-1-1'
            - 'command: corerpg p1 online claim {i}'
    display:
      material: coal
      name: '§7在线 %corerpg_p1_online_m{i}% 分钟'
      lore:
        - '§7%corerpg_p1_online_r{i}%'
        - '§7今日有效在线 §f%corerpg_p1_online_min% §7分钟'
    actions:
      all:
        - 'sound: BLOCK_NOTE_BASS-1-0'""")
path = os.path.join('plugins', 'TrMenu', 'menus', 'ember_p1_sign.yml')
open(path, 'w', encoding='utf-8').write('\n'.join(out) + '\n')
print('wrote', path)
