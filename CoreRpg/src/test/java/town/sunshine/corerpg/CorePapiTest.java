package town.sunshine.corerpg;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static town.sunshine.corerpg.CorePapi.Section.*;

/** D240 / ARCH S3-11: %corerpg_*% section routing (CoreRpgExpansion split) keeps every pre-split key. */
public final class CorePapiTest {

    /** Exact key list of the pre-D240 CoreRpgExpansion if-chain (1.65.65). */
    private static final List<String> PRE_SPLIT = Arrays.asList(
            "coin", "quest", "quest_objective", "quest_chapter", "signed", "activity", "abyss_used", "calamity_next",
            "covenant", "flex_skill", "flex_skill_id", "flex_skill_name", "flex_display", "kit_shape", "slash_shape",
            "kit_shape_key", "kit_dash", "kit_dash_unlock", "kit_shape_unlock", "kit_step_unlock", "kit_huohen_unlock",
            "kit_step", "kit_step_name", "kit_huohen", "kit_step_dir", "step_dir", "kit_step_dir_key", "kit_charge", "kit_parry", "kit_parry_unlock", "kit_parry_name", "kit_parry_cd",
            "skill_charge_ready", "talent_points", "talent_available", "ember_xp", "ember_level", "level",
            "ember_xp_need", "vip_title", "talent_spent", "talent_earned", "crystal_cash", "cash", "monthly",
            "daily_tickets", "daily_cap", "guildboss_pass", "mail_unread",
            "stamina", "stamina_max", "stamina_bank", "stamina_cost_daily", "stamina_cost_weekly", "stamina_cost_abyss",
            "stamina_cost_elite", "stamina_cost_raid", "stamina_reset", "stamina_credit_weekly", "stamina_credit_elite",
            "stamina_credit_raid", "stamina_blocked_weekly", "stamina_blocked_raid", "stamina_blocked_elite");

    @Test public void everyPreSplitKeyHasASection() {
        for (String k : PRE_SPLIT) assertTrue(k, CorePapi.route(k) != NONE);
        Set<String> all = new HashSet<String>();
        all.addAll(CorePapi.ACCOUNT); all.addAll(CorePapi.CASH); all.addAll(CorePapi.KIT);
        all.addAll(CorePapi.PROGRESS); all.addAll(CorePapi.STAMINA);
        assertEquals(new TreeSet<String>(PRE_SPLIT), new TreeSet<String>(all)); // no key added or lost
        int sum = CorePapi.ACCOUNT.size() + CorePapi.CASH.size() + CorePapi.KIT.size() + CorePapi.PROGRESS.size() + CorePapi.STAMINA.size();
        assertEquals("sections are disjoint", all.size(), sum);
    }

    @Test public void sectionsAndPrefixes() {
        assertEquals(ACCOUNT, CorePapi.route("coin"));
        assertEquals(ACCOUNT, CorePapi.route("mail_unread"));
        assertEquals(ACCOUNT, CorePapi.route("guildboss_pass"));
        assertEquals(CASH, CorePapi.route("daily_cap"));
        assertEquals(KIT, CorePapi.route("kit_charge"));
        assertEquals(PROGRESS, CorePapi.route("level"));
        assertEquals(STAMINA, CorePapi.route("stamina_blocked_raid"));
        assertEquals(PET, CorePapi.route("pet_level"));
        assertEquals(PET, CorePapi.route("pet_feed_hint"));
        assertEquals(PET, CorePapi.route("pet_unlocked_count"));
        assertEquals(P1, CorePapi.route("p1_q01_state"));
        assertEquals(P1, CorePapi.route("p1_"));
        assertEquals(GATE, CorePapi.route("gate_elite"));
        assertEquals(GATE, CorePapi.route("gate_nosuch"));
        assertEquals(NONE, CorePapi.route("nosuch"));
        assertEquals(NONE, CorePapi.route("stamina_nosuch"));
        assertEquals(NONE, CorePapi.route("p1"));
        assertEquals(NONE, CorePapi.route(null));
    }

    /** D384: pet_* keys route to PET; do not steal p1_/gate_/ember_level. */
    @Test public void petKeysRoute() {
        for (String k : CorePapi.PET) assertEquals(k, PET, CorePapi.route(k));
        assertEquals(9, CorePapi.PET.size());
        assertEquals(PROGRESS, CorePapi.route("ember_level")); // character level ≠ pet
        assertEquals(NONE, CorePapi.route("pet"));
        assertEquals(NONE, CorePapi.route("pet_nosuch"));
        assertEquals(P1, CorePapi.route("p1_pet_level")); // p1_ prefix wins; not a pet key
    }

    /** Non-p1 %corerpg_*% keys used by live configs all resolve to a section. */
    @Test public void configKeysRoute() throws IOException {
        Pattern p = Pattern.compile("%corerpg_([a-z0-9_]+)%");
        TreeSet<String> stray = new TreeSet<String>();
        for (String dir : Arrays.asList("src/main/resources", "../server-runtime/plugins/TrMenu", "../server-runtime/plugins/DungeonPlus")) {
            Path root = Paths.get(dir);
            if (!Files.isDirectory(root)) continue;
            try (Stream<Path> s = Files.walk(root)) {
                for (Path f : (Iterable<Path>) s.filter(x -> x.toString().endsWith(".yml"))::iterator) {
                    Matcher m = p.matcher(new String(Files.readAllBytes(f), StandardCharsets.UTF_8));
                    while (m.find()) if (CorePapi.route(m.group(1)) == NONE) stray.add(m.group(1));
                }
            }
        }
        assertEquals(Collections.<String>emptySet(), stray);
    }
}
