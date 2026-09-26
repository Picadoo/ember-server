package town.sunshine.corerpg;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;

public final class DailyService {
    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final WeekFields WEEK = WeekFields.ISO;

    private DailyService() {}

    public static String today() { return LocalDate.now(ZONE).format(FMT); }
    public static String yesterday() { return LocalDate.now(ZONE).minusDays(1).format(FMT); }
    public static boolean isToday(String date) { return date != null && date.equals(today()); }
    public static ZoneId zone() { return ZONE; }
    public static String nowHm() {
        return java.time.LocalTime.now(ZONE).format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    /** ISO week key in Asia/Shanghai, e.g. 2026-W37 (Monday-based). */
    public static String weekId() {
        LocalDate d = LocalDate.now(ZONE);
        int y = d.get(WEEK.weekBasedYear());
        int w = d.get(WEEK.weekOfWeekBasedYear());
        return y + "-W" + (w < 10 ? "0" : "") + w;
    }
}
