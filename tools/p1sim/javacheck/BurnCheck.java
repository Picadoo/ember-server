package town.sunshine.corerpg.p1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * M03 cross-check driver (tools/p1sim/javacheck/burncheck.py): runs the REAL EmberBurnBook on an event script from stdin
 * and prints every observable result, so the Python mirror (tools/p1sim/burnbook.py) can be diffed line by line.
 * Script lines: BOOK ignite TARGET PERTICK NOW | BOOK transfer FROM TO NOW MAXMS | BOOK due NOW | BOOK remove TARGET
 *               | BOOK ticks N | BOOK clear. Compiled against copies of the live sources; never part of the plugin build.
 */
public final class BurnCheck {
    public static void main(String[] a) throws Exception {
        Map<String, EmberBurnBook> books = new HashMap<String, EmberBurnBook>();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
        String line;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] f = line.split(" ");
            EmberBurnBook b = books.get(f[0]);
            if (b == null) { b = new EmberBurnBook(); books.put(f[0], b); }
            String op = f[1];
            if (op.equals("ignite")) {
                String ev = b.ignite(f[2], Double.parseDouble(f[3]), Long.parseLong(f[4]));
                System.out.println(f[0] + " ignite " + f[2] + " evicted=" + ev + " size=" + b.size());
            } else if (op.equals("transfer")) {
                boolean ok = b.transfer(f[2], f[3], Long.parseLong(f[4]), Long.parseLong(f[5]));
                System.out.println(f[0] + " transfer " + f[2] + ">" + f[3] + " " + ok + " size=" + b.size());
            } else if (op.equals("due")) {
                StringBuilder sb = new StringBuilder(f[0] + " due " + f[2] + ":");
                for (EmberBurnBook.Tick t : b.due(Long.parseLong(f[2])))
                    sb.append(String.format(Locale.ROOT, " %s=%.6f", t.target, t.amount));
                System.out.println(sb.toString() + " size=" + b.size());
            } else if (op.equals("remove")) {
                b.remove(f[2]);
            } else if (op.equals("ticks")) {
                b.setTicks(Integer.parseInt(f[2]));
            } else if (op.equals("clear")) {
                b.clear();
            }
        }
    }
}
