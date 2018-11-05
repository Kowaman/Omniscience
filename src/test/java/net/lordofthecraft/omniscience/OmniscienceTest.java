package net.lordofthecraft.omniscience;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OmniscienceTest {

    @Mock
    Omniscience omniscience;

    @Test
    public void onEnable() {
        omniscience.onEnable();
        doNothing().when(omniscience).saveDefaultConfig();
        verify(omniscience, times(1)).saveDefaultConfig();
    }

    @Test
    public void onLoad() {
        omniscience.onLoad();
    }

    @Test
    public void onDisable() {
        omniscience.onDisable();
    }
}