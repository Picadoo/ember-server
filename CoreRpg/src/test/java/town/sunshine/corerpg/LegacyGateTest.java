package town.sunshine.corerpg;

import org.junit.Test;

import static org.junit.Assert.*;

/** D198 / ARCH S0-1 + S0-2: legacy dungeon gates while the P1 mode is active. */
public class LegacyGateTest {

    @Test public void s01LegacyGateIdsClosedOnlyWhenP1Active() {
        for (String id : new String[] {"daily", "weekly", "abyss", "raid", "elite", "DAILY", "Elite"}) {
            assertTrue(id, LegacyGate.gateClosed(true, id));
            assertFalse(id, LegacyGate.gateClosed(false, id)); // P1 off: legacy level gate decides as before
        }
    }

    @Test public void s01NonLegacyOrUnknownGateIdsUntouched() {
        for (String id : new String[] {"q01", "q07", "calamity", "", "p1_pass_q01"}) {
            assertFalse(id, LegacyGate.gateClosed(true, id));
            assertFalse(id, LegacyGate.gateClosed(false, id));
        }
        assertFalse(LegacyGate.gateClosed(true, null));
    }

    @Test public void s01GuildBossPassClosedOnlyWhenP1Active() {
        assertTrue(LegacyGate.guildBossPassClosed(true));
        assertFalse(LegacyGate.guildBossPassClosed(false));
    }

    @Test public void s02RefuseLegacyEnterOnlyForPlayersWhileP1Active() {
        assertTrue(LegacyGate.refuseLegacyEnter(true, false, false));   // P1 on, legacy kind, player → refused
        assertFalse(LegacyGate.refuseLegacyEnter(true, false, true));   // admin / OP passes
        assertFalse(LegacyGate.refuseLegacyEnter(true, true, false));   // P1 kind (q0x) never refused here
        assertFalse(LegacyGate.refuseLegacyEnter(true, true, true));
        assertFalse(LegacyGate.refuseLegacyEnter(false, false, false)); // P1 off: unchanged
        assertFalse(LegacyGate.refuseLegacyEnter(false, false, true));
        assertFalse(LegacyGate.refuseLegacyEnter(false, true, false));
    }

    @Test public void everyLegacyKindIsCoveredAndNoP1KindIs() {
        int legacy = 0;
        for (TicketEntryService.Kind k : TicketEntryService.Kind.values()) {
            if (k.p1()) {
                assertFalse(k.name() + " P1 kind must not use a legacy gate id", LegacyGate.gateClosed(true, k.gateId));
                assertFalse(k.name(), LegacyGate.refuseLegacyEnter(true, k.p1(), false));
            } else {
                legacy++;
                assertTrue(k.name() + " legacy kind's DP gate must close", LegacyGate.gateClosed(true, k.gateId));
                assertTrue(k.name(), LegacyGate.refuseLegacyEnter(true, k.p1(), false));
                assertFalse(k.name(), LegacyGate.refuseLegacyEnter(true, k.p1(), true));
                assertFalse(k.name(), LegacyGate.refuseLegacyEnter(false, k.p1(), false));
            }
        }
        assertEquals(11, legacy); // 7 daily + weekly + abyss + raid + elite
        assertEquals("ELITE routes through tryEnter", "elite", TicketEntryService.Kind.ELITE.gateId);
    }

    @Test public void refusalMessageIsTheShortChineseLine() {
        assertEquals("P1 模式下旧副本已关闭", LegacyGate.CLOSED_MSG);
    }

    // ---- D199 / ARCH S0-3 route whitelist ----

    private static boolean refused(String... args) {
        return LegacyGate.refuseRoute(true, true, false, null, args);
    }

    @Test public void s03LegacyLeakRoutesRefusedForPlayers() {
        String[][] leaks = {
                {"arena"}, {"arena", "queue", "1v1"}, {"arena", "claim"}, {"pvp", "claim"}, {"竞技"},
                {"pass", "free"}, {"pass", "claim"}, {"战令", "claim"}, {"vip"}, {"vip", "claim"},
                {"calamity", "join"}, {"calamity", "go"}, {"guild", "create", "x"}, {"alliance", "boss"}, {"盟约"},
                {"scrap"}, {"scrap", "confirm"}, {"reforge"}, {"socket", "list"}, {"enhance"}, {"forge"}, {"锻造"},
                {"part", "craft"}, {"covenant", "set", "x"}, {"talent", "unlock"}, {"shop", "buy", "daily_ticket"},
                {"monthly", "buy"}, {"stamina", "convert"}, {"体力", "兑换"}, {"elite"}, {"elite", "start"},
                {"精英试炼"}, {"abyss", "settle"}, {"abyss", "evacuate"}, {"abyss", "上浮"},
                {"progress", "x", "daily_clear"}, {"loot", "x", "weekly_t1"}, {"mmgive"}, {"mmxp"}, {"xpreward"},
                {"reload"}, {"invsnap"}, {"spawn"}, {"hubbuild"}, {"dailycourtyard"}, {"nosuchcommand"},
        };
        for (String[] a : leaks) assertTrue(String.join(" ", a), refused(a));
    }

    @Test public void s03P1RoutesStayOpen() {
        String[][] ok = {
                {"help"}, {"status"}, {"coin"}, {"sign"}, {"activity"}, {"bounty"}, {"stats"}, {"属性"},
                {"p1", "status"}, {"ember", "sig"}, {"P1", "reroll"}, {"quest", "talk"}, {"主线"}, {"afk", "fight"},
                {"挂机", "2"}, {"life", "buy", "bread"}, {"lv"}, {"enter", "q01"}, {"进本", "q07", "challenge"},
                {"tickets"}, {"cash"}, {"storage"}, {"mail", "claim", "all"}, {"friends"}, {"setting", "sound"},
                {"ladder", "power"}, {"pets", "list"}, {"技能"}, {"flex", "cast"}, {"套装"}, {"ec"}, {"wh", "list"},
                {"raid"}, {"团本", "status"}, {"pass"}, {"pass", "rewards"}, {"pass", "season"}, {"stamina"},
                {"stamina", "show"}, {"calamity"}, {"calamity", "status"}, {"abyss"}, {"abyss", "info"},
                {"elite", "status"}, {"eliteweekly", "STATUS"}, {"ah"},
        };
        for (String[] a : ok) assertFalse(String.join(" ", a), refused(a));
        assertFalse("bare /corerpg", LegacyGate.refuseRoute(true, true, false, null, new String[0]));
    }

    @Test public void s03ConsoleAdminAndLegacyModePass() {
        String[] a = {"pass", "free"};
        assertFalse("console", LegacyGate.refuseRoute(true, false, false, null, a));
        assertFalse("admin", LegacyGate.refuseRoute(true, true, true, null, a));
        assertFalse("P1 off", LegacyGate.refuseRoute(false, true, false, null, a));
        assertFalse("console mmgive", LegacyGate.refuseRoute(true, false, false, null, new String[] {"mmgive", "x"}));
    }

    @Test public void s03ConfigAllowMapOverridesDefault() {
        java.util.Map<String, java.util.Set<String>> allow = new java.util.HashMap<String, java.util.Set<String>>();
        allow.put("arena", java.util.Collections.singleton(LegacyGate.ANY));
        allow.put("pass", new java.util.HashSet<String>(java.util.Arrays.asList("", "free")));
        assertFalse(LegacyGate.refuseRoute(true, true, false, allow, new String[] {"pvp", "queue"}));
        assertFalse(LegacyGate.refuseRoute(true, true, false, allow, new String[] {"pass", "FREE"}));
        assertTrue(LegacyGate.refuseRoute(true, true, false, allow, new String[] {"pass", "claim"}));
        assertTrue("not in custom map", LegacyGate.refuseRoute(true, true, false, allow, new String[] {"status"}));
    }

    @Test public void s03ShippedConfigMatchesBuiltInDefault() throws Exception {
        java.io.File f = new java.io.File("../plugins/CoreRpg/ember-v1.yml");
        org.junit.Assume.assumeTrue(f.isFile());
        org.bukkit.configuration.file.YamlConfiguration y = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(f);
        org.bukkit.configuration.ConfigurationSection sec = y.getConfigurationSection("legacy_gate.allow");
        assertNotNull(sec);
        assertTrue(y.getBoolean("legacy_gate.route_whitelist", false));
        java.util.Map<String, java.util.Set<String>> m = new java.util.HashMap<String, java.util.Set<String>>();
        for (String k : sec.getKeys(false)) {
            java.util.Set<String> acts = new java.util.HashSet<String>();
            if (sec.isList(k)) for (Object o : sec.getList(k)) acts.add(String.valueOf(o).toLowerCase());
            else acts.add(String.valueOf(sec.get(k)).trim());
            m.put(LegacyGate.canonical(k), acts);
        }
        assertEquals(LegacyGate.DEFAULT_ALLOW, m);
    }
    // D200 / ARCH S0-4
    @Test public void s04LegacyXpBlockedOnlyWhileP1AndGuardOn() {
        java.util.Set<String> none = java.util.Collections.<String>emptySet();
        for (String src : new String[] {"daily_clear", "weekly_clear", "abyss_clear", "raid_clear", "guild_boss_clear",
                "elite_weekly", "elite", "boss", "kill", "bounty", "sign", "KILL", "", null}) {
            assertTrue(String.valueOf(src), LegacyGate.blocksLegacyXp(true, true, src, none));
            assertTrue(String.valueOf(src), LegacyGate.blocksLegacyXp(true, true, src, null));
            assertFalse(String.valueOf(src), LegacyGate.blocksLegacyXp(false, true, src, none)); // P1 off: unchanged
            assertFalse(String.valueOf(src), LegacyGate.blocksLegacyXp(true, false, src, none)); // guard off
        }
        java.util.Set<String> allow = new java.util.HashSet<String>(java.util.Arrays.asList("sign"));
        assertFalse(LegacyGate.blocksLegacyXp(true, true, "SIGN", allow));
        assertTrue(LegacyGate.blocksLegacyXp(true, true, "daily_clear", allow));
    }

    @Test public void s04LegacyKillPayoutBlockedOnlyWhileP1AndGuardOn() {
        java.util.Set<String> none = java.util.Collections.<String>emptySet();
        for (String w : new String[] {"ember_hub", "ember_event", "dungeon_EmberDaily_1A2B", "world", "", null}) {
            assertTrue(String.valueOf(w), LegacyGate.blocksLegacyKillPayout(true, true, w, none));
            assertFalse(String.valueOf(w), LegacyGate.blocksLegacyKillPayout(false, true, w, none));
            assertFalse(String.valueOf(w), LegacyGate.blocksLegacyKillPayout(true, false, w, none));
        }
        java.util.Set<String> allow = new java.util.HashSet<String>(java.util.Arrays.asList("ember_hub"));
        assertFalse(LegacyGate.blocksLegacyKillPayout(true, true, "Ember_Hub", allow));
        assertTrue(LegacyGate.blocksLegacyKillPayout(true, true, "ember_event", allow));
    }

    @Test public void s04CalamitySettleBlockedOnlyWhileP1AndGuardOn() {
        assertTrue(LegacyGate.blocksCalamitySettle(true, true));
        assertFalse(LegacyGate.blocksCalamitySettle(true, false));
        assertFalse(LegacyGate.blocksCalamitySettle(false, true));
        assertFalse(LegacyGate.blocksCalamitySettle(false, false));
    }
}
