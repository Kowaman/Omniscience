package net.lordofthecraft.omniscience.api.query;

import com.google.common.collect.Range;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

import static net.lordofthecraft.omniscience.api.query.DataKeys.*;

public class SearchConditionGroup {
    private final List<SearchCondition> conditions = new ArrayList<>();
    private final Operator operator;

    public SearchConditionGroup(Operator operator) {
        this.operator = operator;
    }

    public static SearchConditionGroup from(Location location) {
        SearchConditionGroup group = new SearchConditionGroup(Operator.AND);

        group.add(FieldCondition.of(X, MatchRule.EQUALS, location.getBlockX()));
        group.add(FieldCondition.of(Y, MatchRule.EQUALS, location.getBlockY()));
        group.add(FieldCondition.of(Z, MatchRule.EQUALS, location.getBlockZ()));
        group.add(FieldCondition.of(WORLD, MatchRule.EQUALS, location.getWorld().getUID()));

        return group;
    }

    public static SearchConditionGroup from(Location location, int radius) {
        SearchConditionGroup group = new SearchConditionGroup(Operator.AND);

        group.add(FieldCondition.of(WORLD, MatchRule.EQUALS, location.getWorld().getUID()));

        Range<Integer> xRange = Range.open(location.getBlockX() - radius, location.getBlockX() + radius);
        group.add(FieldCondition.of(X, xRange));

        Range<Integer> yRange = Range.open(location.getBlockY() - radius, location.getBlockY() + radius);
        group.add(FieldCondition.of(Y, yRange));

        Range<Integer> zRange = Range.open(location.getBlockZ() - radius, location.getBlockZ() + radius);
        group.add(FieldCondition.of(Z, zRange));

        return group;
    }

    public void add(SearchCondition condition) {
        conditions.add(condition);
    }

    public void add(List<SearchCondition> conditions) {
        this.conditions.addAll(conditions);
    }

    public List<SearchCondition> getConditions() {
        return conditions;
    }

    public Operator getOperator() {
        return operator;
    }

    enum Operator {
        AND,
        OR
    }
}
