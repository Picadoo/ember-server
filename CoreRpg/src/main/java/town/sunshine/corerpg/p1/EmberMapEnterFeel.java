package town.sunshine.corerpg.p1;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * D477: map-enter ActionBar cue when a run commits (map quality feel).
 * Zero stamina / fee / drop / AFK change.
 */
public final class EmberMapEnterFeel {

    private EmberMapEnterFeel() {}

    /** Mode tag for ActionBar (Bukkit-free flags). */
    public static String modeTag(boolean shortExpedition, boolean rush, String rushMode,
                                 boolean raid, boolean challenge, int abyss) {
        if (shortExpedition) return "短征";
        if (rush) {
            if ("echo".equals(rushMode)) return "残响";
            if ("outpost".equals(rushMode)) return "前哨";
            return "连战";
        }
        if (raid) return "团本";
        if (abyss > 0) return "深渊" + abyss;
        if (challenge) return "挑战";
        return "主线";
    }

    public static String modeTag(EmberRunMaps.MapDef m, boolean challenge, int abyss) {
        if (m == null) return modeTag(false, false, null, false, challenge, abyss);
        return modeTag(m.shortExpedition, m.rush, m.rushMode, m.raid, challenge, abyss);
    }

    public static String line(String mapName, String mode) {
        String name = mapName == null || mapName.isEmpty() ? "副本" : mapName;
        String tag = mode == null || mode.isEmpty() ? "副本" : mode;
        return "§a入场 · §f" + name + " §7· " + tag;
    }

    public static String line(EmberRunMaps.MapDef m, boolean challenge, int abyss) {
        String name = "副本";
        if (m != null) {
            if (m.name != null && !m.name.isEmpty()) name = m.name;
            else if (m.key != null && !m.key.isEmpty()) name = m.key;
        }
        return line(name, modeTag(m, challenge, abyss));
    }

    public static void flash(Player p, EmberRunMaps.MapDef m, EmberRunSession s) {
        flash(p, m, s, null);
    }

    /** D1397: optional PlayerData → append map-identity nextMissing（不锁通关）. */
    public static void flash(Player p, EmberRunMaps.MapDef m, EmberRunSession s, town.sunshine.corerpg.PlayerData pd) {
        if (p == null) return;
        // D2279/D2291: Q0x first-enter kit unlocks (non-clear-gate)
        if (pd != null && m != null && m.key != null) {
            String k = m.key.toLowerCase(java.util.Locale.ROOT);
            try {
                if ("q01".equals(k) && EmberSkillKit.markStrideEnter(pd)) {
                    p.sendMessage(EmberRunService.P + "§a步态路径已开放 · §f/corerpg p1 stridepath §7（首进 Q01 · 仍需 Q05 身法）");
                    p.sendActionBar("§a步态开放 · §fstridepath");
                } else if ("q02".equals(k) && EmberSkillKit.markDashEnter(pd)) {
                    p.sendMessage(EmberRunService.P + "§a烬突已解锁 · §f潜行+F §7（首进 Q02 · 不锁通关）");
                    p.sendActionBar("§a烬突解锁 · §f潜行+F");
                } else if ("q03".equals(k) && EmberSkillKit.markParryEnter(pd)) {
                    p.sendMessage(EmberRunService.P + "§a余烬招架已解锁 · §fQ §7（首进 Q03 · 不锁通关）");
                    p.sendActionBar("§a招架解锁 · §fQ");
                } else if ("q04".equals(k) && EmberSkillKit.markShapeEnter(pd)) {
                    p.sendMessage(EmberRunService.P + "§a烬斩符文已解锁 · §f扇/直/环 §7（首进 Q04 · 不锁通关 · /corerpg skill shape）");
                    p.sendActionBar("§a符文解锁 · §f/corerpg skill shape");
                    EmberSkillUnlock.scheduleShapePick(p); // D2354 enter pick · no ConfirmTokens twin
                    EmberModePath.scheduleOffer(p, "q04"); // D2420 enter mode variety
                } else if ("q05".equals(k) && EmberSkillKit.markStepEnter(pd)) {
                    p.sendMessage(EmberRunService.P + "§a身法变体已解锁 · §f套装踏步 §7（首进 Q05 · 不锁通关）");
                    p.sendActionBar("§a身法解锁 · §f两件套自动变");
                    EmberSkillUnlock.scheduleStepPick(p); // D2354
                    EmberModePath.scheduleOffer(p, "q05"); // D2420
                    EmberPosturePath.scheduleOfferAfterEnter(p); // D2420 posture variety
                } else if ("q06".equals(k) && EmberSkillKit.markPledgeEnter(pd)) {
                    p.sendMessage(EmberRunService.P + "§a誓约/试炼路径已开放 · §f/corerpg p1 pledgepath §7（首进 Q06 · 不锁通关）");
                    p.sendActionBar("§a誓约开放 · §fpledgepath");
                    EmberModePath.scheduleOffer(p, "q06"); // D2420
                } else if ("q07".equals(k) && EmberSkillKit.markAttuneEnter(pd)) {
                    p.sendMessage(EmberRunService.P + "§a签名调律+挑战版已开放 · §f/corerpg p1 attunepath · challengepath §7（首进 Q07）");
                    p.sendActionBar("§a调律/挑战 · §fattunepath");
                    EmberChallengePath.scheduleOfferAfterEnter(p); // D2420
                }
            } catch (Throwable ignored) { }
            // D2516: Q01 enter unlocks press even if stride latch already set
            if ("q01".equalsIgnoreCase(m.key) && EmberSkillKit.markPressEnter(pd)) {
                try {
                    p.sendMessage(EmberRunService.P + "§a压伤路径已开放 · §f/corerpg p1 presspath §7（首进 Q01）");
                } catch (Throwable ignored) { }
                EmberPressPath.scheduleOfferAfterEnter(p);
            }
            if ("q01".equalsIgnoreCase(m.key) && EmberSkillKit.markCutEnter(pd)) {
                try {
                    p.sendMessage(EmberRunService.P + "§a打断路径已开放 · §f/corerpg p1 cutpath §7（首进 Q01）");
                } catch (Throwable ignored) { }
                EmberCutPath.scheduleOfferAfterEnter(p);
            }
            if ("q01".equalsIgnoreCase(m.key) && EmberSkillKit.markRallyEnter(pd)) {
                try {
                    p.sendMessage(EmberRunService.P + "§a突进路径已开放 · §f/corerpg p1 rallypath §7（首进 Q01）");
                } catch (Throwable ignored) { }
                EmberRallyPath.scheduleOfferAfterEnter(p);
            }
            if ("q01".equalsIgnoreCase(m.key) && EmberSkillKit.markGateEnter(pd)) {
                try {
                    p.sendMessage(EmberRunService.P + "§a开门路径已开放 · §f/corerpg p1 gatepath §7（首进 Q01）");
                } catch (Throwable ignored) { }
                EmberGatePath.scheduleOfferAfterEnter(p);
            }
            if ("q01".equalsIgnoreCase(m.key) && EmberSkillKit.markCallEnter(pd)) {
                try {
                    p.sendMessage(EmberRunService.P + "§a召战路径已开放 · §f/corerpg p1 callpath §7（首进 Q01）");
                } catch (Throwable ignored) { }
                EmberCallPath.scheduleOfferAfterEnter(p);
            }
            if ("q01".equalsIgnoreCase(m.key) && EmberSkillKit.markPackEnter(pd)) {
                try {
                    p.sendMessage(EmberRunService.P + "§a腾包路径已开放 · §f/corerpg p1 packpath §7（首进 Q01）");
                } catch (Throwable ignored) { }
                EmberPackPath.scheduleOfferAfterEnter(p);
            }
            if ("q01".equalsIgnoreCase(m.key) && EmberSkillKit.markLinkEnter(pd)) {
                try {
                    p.sendMessage(EmberRunService.P + "§a跃迁路径已开放 · §f/corerpg p1 linkpath §7（首进 Q01）");
                } catch (Throwable ignored) { }
                EmberLinkPath.scheduleOfferAfterEnter(p);
            }
            if ("q01".equalsIgnoreCase(m.key) && EmberSkillKit.markFleeEnter(pd)) {
                try {
                    p.sendMessage(EmberRunService.P + "§a撤离路径已开放 · §f/corerpg p1 flepath §7（首进 Q01）");
                } catch (Throwable ignored) { }
                EmberFleePath.scheduleOfferAfterEnter(p);
            }
            if ("q01".equalsIgnoreCase(m.key) && EmberSkillKit.markChestEnter(pd)) {
                try {
                    p.sendMessage(EmberRunService.P + "§a开箱路径已开放 · §f/corerpg p1 chestpath §7（首进 Q01）");
                } catch (Throwable ignored) { }
                EmberChestPath.scheduleOfferAfterEnter(p);
            }
            if ("q01".equalsIgnoreCase(m.key) && EmberSkillKit.markHuntEnter(pd)) {
                try {
                    p.sendMessage(EmberRunService.P + "§a猎侧路径已开放 · §f/corerpg p1 huntpath §7（首进 Q01）");
                } catch (Throwable ignored) { }
                EmberHuntPath.scheduleOfferAfterEnter(p);
            }
            if ("q01".equalsIgnoreCase(m.key) && EmberSkillKit.markStashEnter(pd)) {
                try {
                    p.sendMessage(EmberRunService.P + "§a存仓路径已开放 · §f/corerpg p1 stashpath §7（首进 Q01）");
                } catch (Throwable ignored) { }
                EmberStashPath.scheduleOfferAfterEnter(p);
            }
            if ("q01".equalsIgnoreCase(m.key) && EmberSkillKit.markExtraEnter(pd)) {
                try {
                    p.sendMessage(EmberRunService.P + "§a加料路径已开放 · §f/corerpg p1 extrapath §7（首进 Q01）");
                } catch (Throwable ignored) { }
                EmberExtraPath.scheduleOfferAfterEnter(p);
            }
            if ("q01".equalsIgnoreCase(m.key) && EmberSkillKit.markPrepEnter(pd)) {
                try {
                    p.sendMessage(EmberRunService.P + "§a备药路径已开放 · §f/corerpg p1 preppath §7（首进 Q01）");
                } catch (Throwable ignored) { }
                EmberPrepPath.scheduleOfferAfterEnter(p);
            }
            if ("q01".equalsIgnoreCase(m.key) && EmberSkillKit.markBarEnter(pd)) {
                try {
                    p.sendMessage(EmberRunService.P + "§a药栏路径已开放 · §f/corerpg p1 barpath §7（首进 Q01）");
                } catch (Throwable ignored) { }
                EmberBarPath.scheduleOfferAfterEnter(p);
            }
            if ("q01".equalsIgnoreCase(m.key) && EmberMapStashPin.markEnter(pd)) {
                try {
                    p.sendMessage(EmberRunService.P + "§a装备库出处筛选已开放 · §7进主线图自动按本图钉选（非路径）");
                } catch (Throwable ignored) { }
            }
            // D2502: Q02 enter can unlock room even if dash latch already set
            if ("q02".equalsIgnoreCase(m.key) && EmberSkillKit.markRoomEnter(pd)) {
                try {
                    p.sendMessage(EmberRunService.P + "§a房序路径已开放 · §f/corerpg p1 roompath §7（首进 Q02）");
                } catch (Throwable ignored) { }
            }
            if ("q02".equalsIgnoreCase(m.key) && EmberSkillKit.markSpiceEnter(pd)) {
                try {
                    p.sendMessage(EmberRunService.P + "§a花样路径已开放 · §f/corerpg p1 spicepath §7（首进 Q02）");
                } catch (Throwable ignored) { }
                EmberSpicePath.scheduleOfferAfterEnter(p);
            }
            if ("q02".equalsIgnoreCase(m.key) && EmberSkillKit.markCounterEnter(pd)) {
                try {
                    p.sendMessage(EmberRunService.P + "§a破绽路径已开放 · §f/corerpg p1 counterpath §7（首进 Q02）");
                } catch (Throwable ignored) { }
                EmberCounterPath.scheduleOfferAfterEnter(p);
            }
            EmberRoomPath.enterCue(p, pd);
            // D3801: pin stash origin filter to this map (non-Path real behavior)
            EmberRunService runsPin = EmberRunService.get();
            if (EmberMapStashPin.pinOnEnter(p, pd, runsPin, m.key)) {
                try {
                    String pin = EmberMapStashPin.pinKey(m.key);
                    p.sendMessage(EmberRunService.P + "§e装备库已按出处钉选 · §f"
                            + (pin == null ? m.key : pin.toUpperCase(java.util.Locale.ROOT))
                            + " §7· /corerpg p1 stash filter map");
                } catch (Throwable ignored) { }
            }
            EmberSigChase.maybeAutoChaseOnEnter(p, pd, runsPin, m.key); // D3881
        }
        boolean ch = s != null && s.challenge;
        int ab = s == null ? 0 : s.abyss;
        String msg = line(m, ch, ab);
        if (m != null && m.lootFamily != null && !m.lootFamily.isEmpty() && ab == 0 && !ch
                && (s == null || (!s.challenge && s.abyss == 0))) {
            // D1816 EM1-A: short miss/齐 on AB; full methodLine once in chat (display only)
            String meth = EmberMapIdentity.methodLine(m, pd);
            String miss = EmberMapIdentity.nextMissing(m, pd);
            if (miss != null && !miss.isEmpty() && !miss.contains("已齐"))
                msg = msg + " §8· §f" + miss;
            else if (meth != null && (meth.contains("身份齐") || meth.contains("齐·")))
                msg = msg + " §a· 身份齐";
            if (meth != null && !meth.isEmpty()) {
                try { p.sendMessage(EmberRunService.P + "§7" + meth); } catch (Throwable ignored) { }
            }
            // D1999 SC3: chase map match → remain half-line (non-hope)
            if (pd != null && m.key != null && EmberSigChase.chaseMap(pd, m.key)) {
                String cg = EmberSigChase.statusGlance(pd);
                if (cg != null && !cg.isEmpty()) {
                    try { p.sendMessage(EmberRunService.P + "§e" + cg); } catch (Throwable ignored) { }
                    msg = msg + " §8· §e追烙";
                }
            }
        }
        try { p.sendActionBar(msg); } catch (Throwable ignored) { }
        try {
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.4f, 1.2f);
        } catch (Throwable ignored) { }
    }

    /** Admin / unit path: flash a prebuilt line. */
    public static void flashLine(Player p, String msg) {
        if (p == null || msg == null || msg.isEmpty()) return;
        try { p.sendActionBar(msg); } catch (Throwable ignored) { }
        try {
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.4f, 1.2f);
        } catch (Throwable ignored) { }
    }
}
