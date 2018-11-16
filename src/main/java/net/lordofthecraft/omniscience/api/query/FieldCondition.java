package net.lordofthecraft.omniscience.api.query;

import com.google.common.collect.Range;

import static com.google.common.base.Preconditions.checkNotNull;

public class FieldCondition implements SearchCondition {
    private final String field;
    private final MatchRule rule;
    private final Object value;

    public FieldCondition(String field, MatchRule rule, Object value) {
        checkNotNull(field);
        checkNotNull(rule);
        checkNotNull(value);
        this.field = field;
        this.rule = rule;
        this.value = value;
    }

    public static FieldCondition of(String field, MatchRule rule, Object value) {
        return new FieldCondition(field, rule, value);
    }

    public static FieldCondition of(String field, Range<?> value) {
        return new FieldCondition(field, MatchRule.BETWEEN, value);
    }

    public String getField() {
        return field;
    }

    public MatchRule getRule() {
        return rule;
    }

    public Object getValue() {
        return value;
    }
}
