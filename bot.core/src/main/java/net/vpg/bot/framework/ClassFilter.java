package net.vpg.bot.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class ClassFilter implements Predicate<Class<?>> {
    private final List<String> disabled = new ArrayList<>();
    private final List<String> enabled = new ArrayList<>();

    public ClassFilter disable(Class<?>... classes) {
        return setEnabled(false, classes);
    }

    public ClassFilter disable(String... regex) {
        return setEnabled(false, regex);
    }

    public ClassFilter enable(Class<?>... classes) {
        return setEnabled(true, classes);
    }

    public ClassFilter enable(String... regex) {
        return setEnabled(true, regex);
    }

    public ClassFilter setEnabled(boolean enabled, Class<?>... classes) {
        return setEnabled(enabled, Arrays.stream(classes).map(Class::getName).toArray(String[]::new));
    }

    public ClassFilter setEnabled(boolean enable, String... regex) {
        List<String> list = enable ? enabled : disabled;
        List<String> other = enable ? disabled : enabled;
        for (String clazz : regex) {
            String pattern = clazz.replaceAll("\\.", "\\.");
            if (other.contains(pattern)) {
                other.remove(pattern);
            } else {
                list.add(pattern);
            }
        }
        return this;
    }

    public Predicate<Class<?>> getPredicate() {
        Pattern enablePattern = Pattern.compile(String.join("|", enabled));
        Pattern disablePattern = Pattern.compile(String.join("|", disabled));
        Predicate<String> predicate = enablePattern.asMatchPredicate().and(disablePattern.asMatchPredicate().negate());
        return clazz -> predicate.test(clazz.getName());
    }

    @Override
    public boolean test(Class<?> clazz) {
        return getPredicate().test(clazz);
    }
}
