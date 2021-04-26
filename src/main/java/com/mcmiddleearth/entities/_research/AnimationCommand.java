package com.mcmiddleearth.entities._research;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class AnimationCommand implements CommandExecutor {

    private ProtocolManager manager = ProtocolLibrary.getProtocolManager();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        int entityId = Integer.parseInt(args[0]);
        PacketContainer packet;

        if(args.length<3) {
            int anim = Integer.parseInt(args[1]);
            packet = new PacketContainer(PacketType.Play.Server.ANIMATION);
            packet.getIntegers().write(0, entityId)
                    .write(1, anim);

        } else {
            packet = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
            packet.getIntegers().write(0,entityId);
            WrappedDataWatcher watcher = new WrappedDataWatcher();
            WrappedDataWatcher.WrappedDataWatcherObject state = new WrappedDataWatcher
                    .WrappedDataWatcherObject(0,WrappedDataWatcher.Registry.get(Byte.class));
            watcher.setObject(state, Byte.decode(args[2]));
            WrappedDataWatcher.WrappedDataWatcherObject pose = new WrappedDataWatcher
                    .WrappedDataWatcherObject(6,WrappedDataWatcher.Registry.get(EnumWrappers.EntityPose.valueOf(args[1].toUpperCase()).toNms().getClass()));
            watcher.setObject(pose, EnumWrappers.EntityPose.valueOf(args[1].toUpperCase()).toNms());


            WrappedDataWatcher.WrappedDataWatcherObject nameValue = new WrappedDataWatcher
                    .WrappedDataWatcherObject(2,
                    WrappedDataWatcher.Registry.getChatComponentSerializer(true));
            WrappedDataWatcher.WrappedDataWatcherObject nameVisible = new WrappedDataWatcher
                    .WrappedDataWatcherObject(3,
                    WrappedDataWatcher.Registry.get(Boolean.class));
            watcher.setObject(nameValue, Optional.of(WrappedChatComponent
                    .fromChatMessage("Animation! That's the command used to display this text which hopefully contains a line feed.")[0].getHandle()));
            watcher.setObject(nameVisible, true);

            packet.getWatchableCollectionModifier().write(0,watcher.getWatchableObjects());
        }
        try {
            manager.sendServerPacket((Player) sender, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return true;
    }
}
