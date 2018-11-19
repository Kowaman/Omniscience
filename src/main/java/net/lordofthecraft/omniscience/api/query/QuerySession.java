package net.lordofthecraft.omniscience.api.query;

public class QuerySession {

    //private final List<DataEntry> dataEntries = Lists.newArrayList();
    private Query query;
    private int pageSize = 10;

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
