package net.lordofthecraft.omniscience.api.entry;

import net.lordofthecraft.omniscience.OmniConfig;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DataAggregateEntry extends DataEntry {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(OmniConfig.INSTANCE.getSimpleDateFormat());

    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(Calendar calender) {
        this.date = simpleDateFormat.format(calender.getTime());
    }
}
