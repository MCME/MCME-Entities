package com.mcmiddleearth.entities._research;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import com.mcmiddleearth.entities.EntitiesPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class NPCCommand implements CommandExecutor {

    private ProtocolManager manager = ProtocolLibrary.getProtocolManager();


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        Location loc = player.getLocation().add(new Vector(3,0,3));
        String name = (args.length<1?"NPC":args[0]);
        PacketContainer info = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        info.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        WrappedGameProfile profile = new WrappedGameProfile(UUID.randomUUID(), name);
        WrappedChatComponent displayName = WrappedChatComponent.fromText(name+"*");
        PlayerInfoData data = new PlayerInfoData(profile, 0, EnumWrappers.NativeGameMode.SURVIVAL, displayName);
        info.getPlayerInfoDataLists().write(0, Collections.singletonList(data));

        PacketContainer spawn = new PacketContainer(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
        spawn.getIntegers().write(0, 100001);
        spawn.getUUIDs().write(0, profile.getUUID());
        spawn.getDoubles()
                .write(0, loc.getX())
                .write(1, loc.getY())
                .write(2, loc.getZ());
        spawn.getBytes()
                .write(0, (byte) 0) //yaw
                .write(1, (byte) 0);//pitch

        PacketContainer equip = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
        equip.getIntegers().write(0, 100001);
        List<Pair<EnumWrappers.ItemSlot,org.bukkit.inventory.ItemStack>> equipment = new ArrayList<>();
        equipment.add(new Pair<>(EnumWrappers.ItemSlot.HEAD,new ItemStack(Material.DIAMOND_HELMET)));
        equipment.add(new Pair<>(EnumWrappers.ItemSlot.CHEST,new ItemStack(Material.DIAMOND_CHESTPLATE)));
        equipment.add(new Pair<>(EnumWrappers.ItemSlot.LEGS,new ItemStack(Material.DIAMOND_LEGGINGS)));
        equipment.add(new Pair<>(EnumWrappers.ItemSlot.FEET,new ItemStack(Material.DIAMOND_BOOTS)));
        equipment.add(new Pair<>(EnumWrappers.ItemSlot.OFFHAND,new ItemStack(Material.DIAMOND_AXE)));
        equipment.add(new Pair<>(EnumWrappers.ItemSlot.MAINHAND,new ItemStack(Material.BOW)));
        equip.getSlotStackPairLists().write(0, equipment);

        PacketContainer meta = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        meta.getIntegers().write(0,100001);
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        WrappedDataWatcher.WrappedDataWatcherObject nameValue = new WrappedDataWatcher
                .WrappedDataWatcherObject(2,
                WrappedDataWatcher.Registry.getChatComponentSerializer(true));
        WrappedDataWatcher.WrappedDataWatcherObject nameVisible = new WrappedDataWatcher
                .WrappedDataWatcherObject(3,
                WrappedDataWatcher.Registry.get(Boolean.class));
        watcher.setObject(nameValue, Optional.of(WrappedChatComponent
                .fromChatMessage("Hello!")[0].getHandle()));
        watcher.setObject(nameVisible, true);
        /*List<WrappedWatchableObject> metadata = new ArrayList<>();
        metadata.add(new WrappedWatchableObject(2,type));
        metadata.add(new WrappedWatchableObject(4,true));
        metadata.add(new WrappedWatchableObject(0xff,null));
        meta.getWatchableCollectionModifier().write(0,metadata);*/
        meta.getWatchableCollectionModifier().write(0,watcher.getWatchableObjects());

        try {
            manager.sendServerPacket((Player) sender, info);
            manager.sendServerPacket((Player) sender, spawn);
            manager.sendServerPacket((Player) sender, equip);
            manager.sendServerPacket((Player) sender, meta);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        sender.sendMessage("NPC spawned");
        new BukkitRunnable() {
            private int counter = 0;
            @Override
            public void run() {
                if(counter<400) {
                    PacketContainer move = new PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE_LOOK);
                    move.getIntegers().write(0,100001);
                    Vector dir = player.getLocation().subtract(loc.toVector()).toVector();
                    dir.normalize().multiply(300);
                    loc.setDirection(dir);
                    move.getShorts()
                            .write(0, (short) dir.getBlockX())
                            .write(1, (short) dir.getBlockY())
                            .write(2, (short) dir.getBlockZ());
                    move.getBytes()
                            .write(0, (byte)(loc.getYaw()*256/360))
                            .write(1, (byte) (loc.getPitch()*256/360));
                    move.getBooleans().write(0,true);

                    PacketContainer look = new PacketContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
                    look.getIntegers().write(0,100001);
                    look.getBytes().write(0,(byte)(loc.getYaw()*256/360));

                    dir.multiply(1.0/(32*128));
                    loc.add(dir);
                    try {
                        manager.sendServerPacket(player,look);
                        manager.sendServerPacket(player,move);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    counter++;
                } else {
                    cancel();
                    player.sendMessage("Removing npc");
                    /*PacketContainer info = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
                    info.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                    PlayerInfoData data = new PlayerInfoData(profile, 0, EnumWrappers.NativeGameMode.SURVIVAL, displayName);
                    info.getPlayerInfoDataLists().write(0, Collections.singletonList(data));*/

                    PacketContainer destroy = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
                    destroy.getIntegerArrays().write(0, new int[]{100001});
                    try {
                        manager.sendServerPacket(player,destroy);
                        //manager.sendServerPacket(player,info);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.runTaskTimer(EntitiesPlugin.getInstance(),1,1);
        return true;
    }


}