package net.lordofthecraft.omniscience.core.listener.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.lordofthecraft.omniscience.api.data.DataWrapper;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class BungeeListener implements PluginMessageListener {
    private static final String SUBCHANNEL_NAME = "Omniscience";

    @Override
    public void onPluginMessageReceived(@NotNull String bungeeChannel, @NotNull Player player, @NotNull byte[] message) {
        if (!bungeeChannel.equals("BungeeCord")) {
            return;
        }

        ByteArrayDataInput input = ByteStreams.newDataInput(message);
        String subChannel = input.readUTF();

        if (subChannel.equalsIgnoreCase(SUBCHANNEL_NAME)) {
            short payloadSize = input.readShort();
            byte[] payload = new byte[Short.toUnsignedInt(payloadSize)];
            input.readFully(payload);
            DataInputStream msg = new DataInputStream(new ByteArrayInputStream(payload));

            try {
                String event = msg.readUTF();
                String server = msg.readUTF();

                if (!server.equalsIgnoreCase(Bukkit.getServer().getName())) return;
                UUID uuid = UUID.fromString(msg.readUTF());
                DataWrapper wrapper = DataWrapper.ofJson(msg.readUTF());

                if (uuid.getLeastSignificantBits() == 0L && uuid.getMostSignificantBits() == 0L) {

                } else {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                    if (offlinePlayer.isOnline()) {

                    } else {

                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
