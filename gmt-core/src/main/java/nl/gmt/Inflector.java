package nl.gmt;

import org.apache.commons.lang.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Inflector {
    private Inflector() {
    }

    private static final List<Tuple<Pattern, String>> PLURAL_RULES = new ArrayList<>();
    private static final List<Tuple<Pattern, String>> SINGULAR_RULES = new ArrayList<>();
    private static final List<String> UNCOUNTABLES = new ArrayList<>();
    private static final CallbackMatcher CAMELIZE_PATTERN1 = new CallbackMatcher(Pattern.compile("/(.?)"));
    private static final CallbackMatcher CAMELIZE_PATTERN2 = new CallbackMatcher(Pattern.compile("(?:^|_)(.)"));
    private static final CallbackMatcher UNDERSCORE_PATTERN1 = new CallbackMatcher(Pattern.compile("([A-Z]+)([A-Z][a-z])"));
    private static final CallbackMatcher UNDERSCORE_PATTERN2 = new CallbackMatcher(Pattern.compile("([a-z\\d])([A-Z])"));

    static {
        UNCOUNTABLES.add("equipment");
        UNCOUNTABLES.add("information");
        UNCOUNTABLES.add("rice");
        UNCOUNTABLES.add("money");
        UNCOUNTABLES.add("species");
        UNCOUNTABLES.add("series");
        UNCOUNTABLES.add("fish");
        UNCOUNTABLES.add("sheep");

        addPlural("$", "s", true);
        addPlural("s$", "s");
        addPlural("(ax|test)is$", "$1es");
        addPlural("(octop|vir)us$", "$1i");
        addPlural("(alias|status)$", "$1es");
        addPlural("(bu)s$", "$1ses");
        addPlural("(buffal|tomat)o$", "$1oes");
        addPlural("([ti])um$", "$1a");
        addPlural("sis$", "ses");
        addPlural("(?:([^f])fe|([lr])f)$", "$1$2ves");
        addPlural("(hive)$", "$1s");
        addPlural("([^aeiouy]|qu)y$", "$1ies");
        addPlural("(x|ch|ss|sh)$", "$1es");
        addPlural("(matr|vert|ind)(?:ix|ex)$", "$1ices");
        addPlural("([m|l])ouse$", "$1ice");
        addPlural("^(ox)$", "$1en");
        addPlural("(quiz)$", "$1zes");

        addSingular("s$", "");
        addSingular("(n)ews$", "$1ews");
        addSingular("([ti])a$", "$1um");
        addSingular("((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)ses$", "$1$2sis");
        addSingular("(^analy)ses$", "$1sis");
        addSingular("([^f])ves$", "$1fe");
        addSingular("(hive)s$", "$1");
        addSingular("(tive)s$", "$1");
        addSingular("([lr])ves$", "$1f");
        addSingular("([^aeiouy]|qu)ies$", "$1y");
        addSingular("(s)eries$", "$1eries");
        addSingular("(m)ovies$", "$1ovie");
        addSingular("(x|ch|ss|sh)es$", "$1");
        addSingular("([m|l])ice$", "$1ouse");
        addSingular("(bus)es$", "$1");
        addSingular("(o)es$", "$1");
        addSingular("(shoe)s$", "$1");
        addSingular("(cris|ax|test)es$", "$1is");
        addSingular("(octop|vir)i$", "$1us");
        addSingular("(alias|status)es$", "$1");
        addSingular("^(ox)en", "$1");
        addSingular("(vert|ind)ices$", "$1ex");
        addSingular("(matr)ices$", "$1ix");
        addSingular("(quiz)zes$", "$1");

        addIrregular("person", "people");
        addIrregular("man", "men");
        addIrregular("child", "children");
        addIrregular("sex", "sexes");
        addIrregular("move", "moves");
        addIrregular("cow", "kine");
    }

    private static void addIrregular(String singular, String plural) {
        addPlural(singular.substring(0, 1).toLowerCase() + singular.substring(1) + "$", plural.substring(0, 1).toLowerCase() + plural.substring(1));
        addPlural(singular.substring(0, 1).toUpperCase() + singular.substring(1) + "$", plural.substring(0, 1).toUpperCase() + plural.substring(1));
        addSingular(plural.substring(0, 1).toLowerCase() + plural.substring(1) + "$", singular.substring(0, 1).toLowerCase() + singular.substring(1));
        addSingular(plural.substring(0, 1).toUpperCase() + plural.substring(1) + "$", singular.substring(0, 1).toUpperCase() + singular.substring(1));
    }

    private static void addPlural(String expression, String replacement) {
        addPlural(expression, replacement, false);
    }

    private static void addPlural(String expression, String replacement, boolean caseSensitive) {
        Pattern re = Pattern.compile(expression, caseSensitive ? 0 : Pattern.CASE_INSENSITIVE);

        PLURAL_RULES.add(0, new Tuple<>(re, replacement));
    }

    private static void addSingular(String expression, String replacement) {
        addSingular(expression, replacement, false);
    }

    private static void addSingular(String expression, String replacement, boolean caseSensitive) {
        Pattern re = Pattern.compile(expression, caseSensitive ? 0 : Pattern.CASE_INSENSITIVE);

        SINGULAR_RULES.add(0, new Tuple<>(re, replacement));
    }

    public static String pluralize(String value) {
        Validate.notNull(value, "value");

        if (UNCOUNTABLES.contains(value)) {
            return value;
        }

        for (Tuple<Pattern, String> rule : PLURAL_RULES) {
            Matcher matcher = rule.getKey().matcher(value);

            if (matcher.find()) {
                return matcher.replaceAll(rule.getValue());
            }
        }

        return value;
    }

    public static String singularize(String value) {
        if (UNCOUNTABLES.contains(value)) {
            return value;
        }

        for (Tuple<Pattern, String> rule : SINGULAR_RULES) {
            Matcher matcher = rule.getKey().matcher(value);

            if (matcher.find()) {
                return matcher.replaceAll(rule.getValue());
            }
        }

        return value;
    }

    public static String camelize(String value) {
        return camelize(value, true);
    }

    public static String camelize(String value, boolean firstLetterUppercase) {
        if (!firstLetterUppercase) {
            return value.substring(0, 1).toLowerCase() + camelize(value.substring(1));
        }

        return CAMELIZE_PATTERN2.replaceMatches(
            CAMELIZE_PATTERN1.replaceMatches(value, new CallbackMatcher.Callback() {
                @Override
                public String replace(MatchResult matchResult) {
                    return "::" + matchResult.group(1).toUpperCase();
                }
            }),
            new CallbackMatcher.Callback() {
                @Override
                public String replace(MatchResult matchResult) {
                    return matchResult.group(1).toUpperCase();
                }
            }
        );
    }

    public static String underscore(String value) {
        value = value.replace("::", "/");

        value = UNDERSCORE_PATTERN1.replaceMatches(
            value,
            new CallbackMatcher.Callback() {
                @Override
                public String replace(MatchResult matchResult) {
                    return matchResult.group(1) + "_" + matchResult.group(2);
                }
            }
        );

        value = UNDERSCORE_PATTERN2.replaceMatches(
            value,
            new CallbackMatcher.Callback() {
                @Override
                public String replace(MatchResult matchResult) {
                    return matchResult.group(1) + "_" + matchResult.group(2);
                }
            }
        );

        value = value.replace("-", "_");

        return value.toLowerCase();
    }

    private static class Tuple<TKey, TValue> {
        private TKey key;
        private TValue value;

        public Tuple(TKey key, TValue value) {
            this.key = key;
            this.value = value;
        }

        public TKey getKey() {
            return key;
        }

        public TValue getValue() {
            return value;
        }
    }

    private static class CallbackMatcher {
        private final Pattern pattern;

        public CallbackMatcher(Pattern pattern) {
            this.pattern = pattern;
        }

        public String replaceMatches(String string, Callback callback) {
            StringBuffer sb = new StringBuffer();

            Matcher matcher = pattern.matcher(string);

            while (matcher.find()) {
                matcher.appendReplacement(sb, callback.replace(matcher.toMatchResult()));
            }

            matcher.appendTail(sb);

            return sb.toString();
        }

        public static interface Callback {
            public String replace(MatchResult matchResult);
        }
    }
}
