package net.lordofthecraft.omniscience.api.query;

import com.google.common.collect.Lists;

import java.util.List;

public class Query {

    private List<SearchCondition> searchCriteria;

    public Query() {
        this.searchCriteria = Lists.newArrayList();
    }

    public List<SearchCondition> getSearchCriteria() {
        return searchCriteria;
    }
}
