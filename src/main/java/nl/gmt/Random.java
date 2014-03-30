package nl.gmt;

import org.apache.commons.lang.Validate;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Random {
    private Random() {
    }

    public static final SecureRandom RANDOM = new SecureRandom();
    private static final Map<TextType, String> RANGES = buildRanges();

    private static Map<TextType, String> buildRanges() {
        Map<TextType, String> result = new HashMap<>();

        StringBuilder sb = new StringBuilder();

        addRange(sb, 'a', 'z');
        addRange(sb, 'A', 'Z');
        addRange(sb, '0', '9');

        result.put(TextType.ALPHA_NUMERIC, sb.toString());

        sb.setLength(0);

        addRange(sb, '0', '9');
        addRange(sb, 'A', 'F');

        result.put(TextType.HEX, sb.toString());

        sb.setLength(0);

        addRange(sb, 33, 126);

        result.put(TextType.PRINTABLE, sb.toString());

        return Collections.unmodifiableMap(result);
    }

    private static void addRange(StringBuilder sb, int start, int end) {
        for (int i = start; i <= end; i++) {
            sb.append((char)i);
        }
    }

    public static String createRandomText(int length, TextType type) {
        Validate.notNull(type, "type");

        String range = RANGES.get(type);

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(range.charAt(RANDOM.nextInt(range.length())));
        }

        return sb.toString();
    }

    public static enum TextType {
        HEX,
        ALPHA_NUMERIC,
        PRINTABLE
    }
}
