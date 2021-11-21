package com.mcmiddleearth.entities.protocol.packets.simple;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.mcmiddleearth.entities.entities.simple.SimplePlayer;
import com.mcmiddleearth.entities.protocol.packets.AbstractPacket;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collections;

public class SimplePlayerSpawnPacket extends AbstractPacket {

    private final PacketContainer info;
    private final PacketContainer spawn;

    private final SimplePlayer player;

    public SimplePlayerSpawnPacket(SimplePlayer player) {
        this.player = player;
        info = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        info.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        WrappedGameProfile profile = new WrappedGameProfile(player.getUniqueId(), (player.getName()!=null?player.getName():" "));
        WrappedChatComponent displayName = WrappedChatComponent.fromText("*"+player.getDisplayName()+"*");
        PlayerInfoData data = new PlayerInfoData(profile, 0, EnumWrappers.NativeGameMode.SURVIVAL, displayName);
        info.getPlayerInfoDataLists().write(0, Collections.singletonList(data));
        send(info, player.getViewers());

        spawn = new PacketContainer(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
        spawn.getIntegers().write(0, player.getEntityId());
        spawn.getUUIDs().write(0, profile.getUUID());
        update();
    }

    @Override
    public void update() {
        Location loc = player.getLocation();
        spawn.getDoubles()
                .write(0, loc.getX())
                .write(1, loc.getY())
                .write(2, loc.getZ());
        spawn.getBytes()
                .write(0, (byte)(loc.getYaw()*256/360))
                .write(1, (byte) (loc.getPitch()*256/360));
    }

    @Override
    public void send(Player recipient) {
//Logger.getGlobal().info("send player spawn to : "+recipient.getName());
        send(info, recipient);
        send(spawn, recipient);
    }

}
