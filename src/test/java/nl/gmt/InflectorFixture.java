package nl.gmt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class InflectorFixture {
    @Test
    public void simpleTranslations() {
        verifyPlural("car", "cars");
        verifyPlural("virus", "viri");
        verifyPlural("some car", "some cars");
    }

    @Test
    public void underscore() {
        verifyUnderscore("this_is_a_var", "ThisIsAVar");
        verifyUnderscore("this/is/a_var", "This::Is::AVar");
        verifyUnderscore("this_contains_a_number1235", "ThisContainsANumber1235");
    }

    private void verifyUnderscore(String underscored, String camelized) {
        assertEquals(underscored, Inflector.underscore(camelized));
        assertEquals(camelized, Inflector.camelize(underscored));
    }

    @Test
    public void camelize() {
    }

    private void verifyPlural(String singular, String plural) {
        assertEquals(singular, Inflector.singularize(plural));
        assertEquals(plural, Inflector.pluralize(singular));
    }
}
