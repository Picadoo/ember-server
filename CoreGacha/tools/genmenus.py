#!/usr/bin/env python3
"""Generate the CoreGacha TrMenu pages from gacha.yml (item icons / ids of the shard shop come from the config).
Usage: python3 CoreGacha/tools/genmenus.py [gacha.yml] [menus dir]
Re-run after adding items or banners, then `trmenu reload`. The main page lists the banners named in MAIN_BANNERS."""
import sys, os, yaml

here = os.path.dirname(os.path.abspath(__file__))
cfg_path = sys.argv[1] if len(sys.argv) > 1 else os.path.join(here, '..', 'src', 'main', 'resources', 'gacha.yml')
out_dir = sys.argv[2] if len(sys.argv) > 2 else os.path.join(here, '..', '..', 'plugins', 'TrMenu', 'menus')
cfg = yaml.safe_load(open(cfg_path, encoding='utf-8'))
items = cfg['items']
TIERS = ['legend', 'epic', 'rare', 'common']
TNAME = {'legend': '§6传说', 'epic': '§d史诗', 'rare': '§b稀有', 'common': '§7普通'}
STANDARD, LIMITED = 'standard', 'gq26'   # banners on the main page (next season: change LIMITED, re-run)
lim = cfg['banners'][LIMITED]

HEAD = "# 由 CoreGacha/tools/genmenus.py 从 gacha.yml 生成 —— 不要手改，改生成器后重跑（设计 docs/design/DESIGN-ember-gacha.md §8）\n"
PANE = "  '#':\n    display:\n      material: gray stained glass pane\n      name: '§8'\n"

def q(s):
    return "'" + str(s).replace("'", "''") + "'"

def icon(key, material, name, lore, actions=None, update=20, extra=''):
    o = f"  {q(key)}:\n    update: {update}\n    display:\n      material: {q(material)}\n      name: {q(name)}\n"
    if extra:
        o += extra
    o += "      lore:\n" + ''.join(f"        - {q(l)}\n" for l in lore)
    if actions:
        o += "    actions:\n"
        for k, v in actions.items():
            o += f"      {k}:\n" + ''.join(f"        - {q(x)}\n" for x in v)
    return o

def cmd(c, close=True, snd='BLOCK_NOTE_PLING-1-2'):
    return ([f'sound: {snd}'] + (['close'] if close else []) + [f'command: {c}'])

def menu(name, title, layout, icons, binding=None):
    s = HEAD + f"Title: {q(title)}\n"
    if binding:
        s += f"Bindings:\n  Commands:\n    - {q(binding)}\n"
    s += "Options:\n  Arguments: false\n  Default-Layout: 0\n  Hide-Player-Inventory: false\n  Min-Click-Delay: 400\n"
    s += "Layout:\n" + ''.join(f"  - {q(r)}\n" for r in layout)
    s += "Events:\n  Open:\n    - 'sound: BLOCK_CHEST_OPEN-1-0'\nIcons:\n" + PANE + ''.join(icons)
    open(os.path.join(out_dir, name + '.yml'), 'w', encoding='utf-8').write(s)
    print('wrote', name)

back = lambda target='ember_gacha', label='§7返回扭蛋页': icon('B', 'arrow', label, ['', '§e➥ §f返回'], {'all': ['sound: BLOCK_NOTE_PLING-1-0', f'menu: {target}']})

# ---------------------------------------------------------------- main page
wallet = icon('I', 'nether star', '§d外观扭蛋 §8（只出外观，零属性）', [
    '', '§7扭蛋券：§f%coregacha_tickets% §8张  §7光屑：§b%coregacha_shards%',
    '§7今日已抽：§f%coregacha_today% §8（每日上限）',
    '§7收藏：§f%coregacha_owned_count%',
    '', '§7券从哪来：每天活跃在线 30 / 90 分钟、主线结算 1 / 3 局各 1 张，',
    '§7或用余烬币 / 余烬徽换（每天最多 5 张）。§c没有任何充值入口。',
    '§7重复的外观变光屑，光屑能直接换池里任意一件。'])
def banner_icons(b, key_card, k1, k10, kr, ks):
    bid = b
    card_lore = ['', '%coregacha_b_desc_' + bid + '%', '%coregacha_b_left_' + bid + '%', '',
                 '§6传说：§f%coregacha_b_legend_' + bid + '%', '%coregacha_b_pity_' + bid + '%',
                 '§7火花：§f%coregacha_b_spark_' + bid + '% §8（满 ' + str(cfg.get('spark', 200)) + ' 任选一件）',
                 '§7本池累计：§f%coregacha_b_pulls_' + bid + '% §7抽', '', '§e➥ §f左键：概率公示 · 右键：火花兑换']
    mat = cfg['banners'][bid].get('icon', 'CHEST')
    acts = {'left': cmd(f'gacha rates {bid}'), 'right': cmd(f'gacha spark {bid}')}
    out = []
    pull_lore = lambda n: ['', f'§7消耗 §f{n}§7 张扭蛋券 · 现有 §f%coregacha_tickets%', '§7今日 §f%coregacha_today%', '', '§e➥ §f点击抽取']
    if cfg['banners'][bid].get('type') == 'limited':
        closed = ("    icons:\n      - condition: 'check papi %coregacha_b_open_" + bid + "% < 1'\n        priority: 1\n        display:\n"
                  "          material: gray dye\n          name: '§8" + cfg['banners'][bid]['name'] + "（已结束）'\n          lore:\n"
                  "            - ''\n            - '§7限定外观已转入常驻池，可在常驻池抽到或用光屑兑换'\n")
        def lim_icon(key, mat2, name, lore, actions):
            s = icon(key, mat2, name, lore, actions)
            return s.replace("    update: 20\n", "    update: 20\n" + closed, 1)
        out.append(lim_icon(key_card, mat, '§c' + cfg['banners'][bid]['name'] + ' §6（限定）', card_lore, acts))
        out.append(lim_icon(k1, 'firework charge', '§f单抽 §8· ' + cfg['banners'][bid]['name'], pull_lore(1), {'all': cmd(f'gacha pull {bid} 1')}))
        out.append(lim_icon(k10, 'firework', '§6十连 §8· ' + cfg['banners'][bid]['name'], pull_lore(10) [:3] + ['§7十连内至少一件史诗或以上', '', '§e➥ §f点击抽取'], {'all': cmd(f'gacha pull {bid} 10')}))
        out.append(lim_icon(kr, 'paper', '§f概率公示 §8· ' + cfg['banners'][bid]['name'], ['', '§7四档基础 / 综合概率、保底规则、逐件概率', '', '§e➥ §f打开'], {'all': cmd(f'gacha rates {bid}')}))
        out.append(lim_icon(ks, 'blaze powder', '§f火花兑换 §8· %coregacha_b_spark_' + bid + '%', ['', '§7每抽 +1，满 ' + str(cfg.get('spark', 200)) + ' 在本池任选一件没有的', '§8限定池结束后剩下的火花 1:1 折成光屑', '', '§e➥ §f查看'], {'all': cmd(f'gacha spark {bid}')}))
    else:
        out.append(icon(key_card, mat, '§b' + cfg['banners'][bid]['name'], card_lore, acts))
        out.append(icon(k1, 'ender pearl', '§f单抽 §8· ' + cfg['banners'][bid]['name'], pull_lore(1), {'all': cmd(f'gacha pull {bid} 1')}))
        out.append(icon(k10, 'eye of ender', '§6十连 §8· ' + cfg['banners'][bid]['name'], pull_lore(10)[:3] + ['§7十连内至少一件史诗或以上', '', '§e➥ §f点击抽取'], {'all': cmd(f'gacha pull {bid} 10')}))
        out.append(icon(kr, 'paper', '§f概率公示 §8· ' + cfg['banners'][bid]['name'], ['', '§7四档基础 / 综合概率、保底规则、逐件概率', '', '§e➥ §f打开'], {'all': cmd(f'gacha rates {bid}')}))
        out.append(icon(ks, 'blaze powder', '§f火花兑换 §8· %coregacha_b_spark_' + bid + '%', ['', '§7每抽 +1，满 ' + str(cfg.get('spark', 200)) + ' 在本池任选一件没有的', '', '§e➥ §f查看'], {'all': cmd(f'gacha spark {bid}')}))
    return out

main_icons = [wallet] + banner_icons(STANDARD, 'S', '1', 'T', 'R', 'K') + banner_icons(LIMITED, 'L', '2', 'M', 'r', 'k') + [
    icon('H', 'book', '§f最近 100 抽', ['', '§7时间、池、品质、外观、新 / 重复、保底', '', '§e➥ §f打开记录页'], {'all': cmd('gacha history')}),
    icon('C', 'prismarine crystals', '§b光屑兑换 §8· 现有 %coregacha_shards%', ['', '§7重复的外观会变成光屑：传说 300 · 史诗 80 · 稀有 20 · 普通 5',
         '§7兑换价：传说 1800 · 史诗 480 · 稀有 120 · 普通 40', '§7能换所有开放池 + 常驻池里的任意一件', '', '§e➥ §f打开兑换页'], {'all': cmd('gacha shop')}),
    icon('E', 'gold ingot', '§e换扭蛋券 §8· 今天还能换 %coregacha_exch_left% 张', ['', '§7余烬币：§f%coregacha_coin% §8（1200 币 = 1 张）',
         '§7余烬徽：§f%coregacha_badges% §8（20 徽 = 1 张）', '§8两种合计每天最多 5 张 · 只用游戏里挣的', '',
         '§e➥ §f左键：用 1200 余烬币换 1 张 · 右键：用 20 余烬徽换 1 张'], {'left': cmd('gacha exchange coin 1'), 'right': cmd('gacha exchange badge 1')}),
    icon('W', 'armor stand', '§d我的外观', ['', '§7徽记 %coregacha_wear_badge% §7· 称号 %coregacha_wear_tag%', '§7光环 %coregacha_wear_aura% §7· 宠物 %coregacha_wear_pet%',
         '§7烟火 %coregacha_wear_show% §8（/gacha fx 放）', '§8CoreRpg 的件在外观商店换上', '', '§e➥ §f左键：装上 / 取下（聊天栏）· 右键：外观商店'],
         {'left': cmd('gacha wear'), 'right': cmd('corerpg p1 cosmetic')}),
    icon('B', 'arrow', '§7返回主菜单', ['', '§e➥ §f返回'], {'all': ['sound: BLOCK_NOTE_PLING-1-0', 'menu: ember_hub']}),
]
menu('ember_gacha', '§d余烬 · 外观扭蛋', ['####I####', '#S1T#L2M#', '#R#K#r#k#', '#########', '#H#C#E#W#', '####B####'], main_icons, '(?i)ember_gacha')

# ---------------------------------------------------------------- rates page
mat = {'legend': 'gold block', 'epic': 'purpur block', 'rare': 'lapis block', 'common': 'stone'}
rate_icons = [icon('I', 'paper', '%coregacha_view_name% §7· 概率公示', ['%coregacha_view_rules%'])]
for k, t in zip('LERC', TIERS):
    rate_icons.append(icon(k, mat[t], TNAME[t] + ' §7%coregacha_view_tier_' + t + '%', ['', '%coregacha_view_items_' + t + '%', '',
                           '§8单件概率 = 品质综合概率 × 品质内份额（传说份额按「不连出同一件」算）']))
rate_icons += [
    icon('s', 'nether star', '§b看常驻池', ['', '§e➥ §f切换'], {'all': cmd('gacha rates standard')}),
    icon('l', 'firework', '§c看' + lim['name'], ['', '§e➥ §f切换'], {'all': cmd(f'gacha rates {LIMITED}')}),
    icon('P', 'clock', '§f保底说明', ['', f"§7第 1–{cfg['rates']['soft_pity']['start']} 抽传说 {cfg['rates']['base']['legend']*100:.1f}%，之后每抽 +{cfg['rates']['soft_pity']['step']*100:.0f}%",
         f"§7第 {cfg['rates']['hard_pity']} 抽必出传说（永远不会到第 {cfg['rates']['hard_pity']+1} 抽）",
         f"§7每 {cfg['rates']['tier2_every']} 抽至少一件史诗或以上", '§7同一个池不会连续出同一件传说',
         '§8传说宠物只在主城显示 · 光环副本里不显示',
         f"§7火花：每抽 +1，满 {cfg.get('spark', 200)} 任选一件", '§7限定池共用一组保底（换季延续），常驻池单独一组',
         '§7「综合概率」= 算上保底后长期的实际概率（精确计算，离线百万抽验证）']),
    back(),
]
menu('ember_gacha_rates', '§d扭蛋 · 概率公示', ['####I####', '#L#E#R#C#', '#########', '##s#P#l##', '####B####'], rate_icons)

# ---------------------------------------------------------------- history page
hist = [icon(str(i), 'book', f'§f记录 {i * 25 - 24}–{i * 25} §8（新的在前）', ['%coregacha_hist_page_' + str(i) + '%']) for i in range(1, 5)]
hist += [icon('I', 'writable book', '§f最近 100 抽 §8· 共 %coregacha_hist_count% 条', ['', '§7每一抽都记在数据库（审计）：时间、池、品质、外观、保底前后',
         '§7聊天版：/gacha history chat'], {'all': cmd('gacha history chat')}), back()]
menu('ember_gacha_history', '§d扭蛋 · 最近 100 抽', ['####I####', '#1#2#3#4#', '####B####'], hist)

# ---------------------------------------------------------------- shard shop (every item; the server checks it is in an open pool)
keys = list("abcdefghijklmnopqrstuvwxyzACDEFGHJKLMNOPQRTUVWXYZ0123456789")
order = sorted(items.items(), key=lambda kv: TIERS.index(kv[1]['tier']))
if len(order) > 45 or len(order) > len(keys):
    sys.exit('too many items for one shop page')
shop_icons, cells = [], []
for (iid, it), k in zip(order, keys):
    cells.append(k)
    shop_icons.append(icon(k, it.get('icon', 'PAPER'), '%coregacha_shop_name_' + iid + '%', [
        '%coregacha_shop_kind_' + iid + '%', '%coregacha_shop_desc_' + iid + '%', '', '%coregacha_shop_price_' + iid + '%', '%coregacha_shop_state_' + iid + '%', '',
        '§e➥ §f左键：兑换（聊天确认）· Shift+左键：直接兑换'], {'left': cmd(f'gacha craft {iid}'), 'shift_left': cmd(f'gacha craft {iid} confirm')}))
cells += [' '] * (45 - len(cells))
rows = [''.join(cells[i:i + 9]) for i in range(0, 45, 9)]
shop_icons += [icon('I', 'prismarine crystals', '§b光屑 %coregacha_shards%', ['', '§7重复返还：传说 300 · 史诗 80 · 稀有 20 · 普通 5', '§7兑换价：传说 1800 · 史诗 480 · 稀有 120 · 普通 40',
              '§8不在开放池里的件（还没开的限定）不能换']), back()]
menu('ember_gacha_shop', '§d扭蛋 · 光屑兑换', rows + ['###I#B###'], shop_icons)

# ---------------------------------------------------------------- reveal page (tier-coloured)
GL = {4: ('orange stained glass', True), 3: ('magenta stained glass', True), 2: ('light blue stained glass', False), 1: ('light gray stained glass', False)}
rv = []
for i in range(1, 11):
    k = str(i - 1)
    s = f"  {q(k)}:\n    update: 10\n    icons:\n"
    for code in (4, 3, 2, 1):
        m, shiny = GL[code]
        s += (f"      - condition: 'check papi %coregacha_rv_{i}_is{code}% > 0'\n        priority: {code}\n        display:\n"
              f"          material: {q(m)}\n" + ("          shiny: true\n" if shiny else "") +
              f"          name: '%coregacha_rv_{i}_name%'\n          lore:\n            - '%coregacha_rv_{i}_kind%'\n            - '%coregacha_rv_{i}_desc%'\n"
              f"            - ''\n            - '%coregacha_rv_{i}_note%'\n")
    s += "    display:\n      material: black stained glass pane\n      name: '§8'\n"
    rv.append(s)
rv += [
    icon('I', 'nether star', '§d%coregacha_rv_banner% §7· 结果', ['', '§6金 = 传说 §d紫 = 史诗 §b蓝 = 稀有 §7灰 = 普通', '§7券 §f%coregacha_tickets% §7· 光屑 §b%coregacha_shards%',
         '%coregacha_rv_pity%']),
    icon('A', 'ender pearl', '§a再抽一次', ['', '§7同一个池，1 张券', '', '§e➥ §f点击'], {'all': cmd('gacha pull %coregacha_rv_bid% 1')}),
    icon('Z', 'eye of ender', '§6十连', ['', '§7同一个池，10 张券', '', '§e➥ §f点击'], {'all': cmd('gacha pull %coregacha_rv_bid% 10')}),
    icon('W', 'armor stand', '§d我的外观', ['', '§e➥ §f装上 / 取下'], {'all': cmd('gacha wear')}),
    back(),
]
menu('ember_gacha_reveal', '§d扭蛋 · 开箱', ['####I####', '#########', '##01234##', '##56789##', '#########', '#A#W#B#Z#'], rv)
