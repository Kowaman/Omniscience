package net.lordofthecraft.omniscience;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OmniCoreTest {

    @Mock
    Omniscience omniscience;

    @Mock
    PluginCommand command;

    @Test
    public void onEnable() {
        OmniCore core = new OmniCore();
        doNothing().when(omniscience).saveDefaultConfig();
        when(omniscience.getConfig()).thenReturn(getDummyConfiguration());
        when(omniscience.getCommand("omniscience")).thenReturn(command);
        core.onEnable(omniscience);
        verify(omniscience, times(1)).saveDefaultConfig();
    }

    @Test
    public void onLoad() {
        OmniCore core = new OmniCore();
        core.onLoad(omniscience);
    }

    @Test
    public void onDisable() {
        OmniCore core = new OmniCore();
        core.onDisable(omniscience);
    }

    private FileConfiguration getDummyConfiguration() {
        YamlConfiguration configuration = new YamlConfiguration();
        List<Map<String, Map<String, Object>>> values = Lists.newArrayList();
        Map<String, Object> deepVals = Maps.newHashMap();
        deepVals.put("address", "90.0.12.3");
        deepVals.put("port", 27017);
        deepVals.put("usesauth", false);
        deepVals.put("user", "username");
        deepVals.put("pass", "password");
        Map<String, Map<String, Object>> vals = Maps.newHashMap();
        vals.put("ServerA", deepVals);
        values.add(vals);
        configuration.set("mongodb.servers", values);
        return configuration;
    }
}