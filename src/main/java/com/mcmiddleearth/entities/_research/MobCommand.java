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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Logger;

public class MobCommand implements CommandExecutor {

    private ProtocolManager manager = ProtocolLibrary.getProtocolManager();


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        Location loc = player.getLocation().add(new Vector(3,0,3));
        String type = (args.length<1?"skeleton":args[0]);

        PacketContainer spawn = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        spawn.getIntegers().write(0, 100002)
                           .write(1,getEntityType(type));
        spawn.getUUIDs().write(0, UUID.randomUUID());
        spawn.getDoubles()
                .write(0, loc.getX())
                .write(1, loc.getY())
                .write(2, loc.getZ());
        spawn.getBytes()
                .write(0, (byte) 0) //yaw
                .write(1, (byte) 0)//pitch
                .write(2, (byte) 10); //head pitch
        /*spawn.getShorts()
                .write(0, (short) -300)
                .write(1, (short) 0)
                .write(2, (short) -300);*/

        PacketContainer equip = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
        equip.getIntegers().write(0, 100002);
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipment = new ArrayList<>();
        equipment.add(new Pair<>(EnumWrappers.ItemSlot.HEAD,new ItemStack(Material.DIAMOND_HELMET)));
        equipment.add(new Pair<>(EnumWrappers.ItemSlot.CHEST,new ItemStack(Material.DIAMOND_CHESTPLATE)));
        equipment.add(new Pair<>(EnumWrappers.ItemSlot.LEGS,new ItemStack(Material.DIAMOND_LEGGINGS)));
        equipment.add(new Pair<>(EnumWrappers.ItemSlot.FEET,new ItemStack(Material.DIAMOND_BOOTS)));
        equipment.add(new Pair<>(EnumWrappers.ItemSlot.OFFHAND,new ItemStack(Material.DIAMOND_AXE)));
        equipment.add(new Pair<>(EnumWrappers.ItemSlot.MAINHAND,new ItemStack(Material.BOW)));
        equip.getSlotStackPairLists().write(0, equipment);

        PacketContainer meta = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        meta.getIntegers().write(0,100002);
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
            manager.sendServerPacket((Player) sender, spawn);
            manager.sendServerPacket((Player) sender, equip);
            manager.sendServerPacket((Player) sender, meta);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        sender.sendMessage("mob spawned");
        new BukkitRunnable() {
            private int counter = 0;
            @Override
            public void run() {
                if(counter<400) {
                    PacketContainer move = new PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE_LOOK);
                    move.getIntegers().write(0,100002);
                    Vector dir = player.getLocation().subtract(loc.toVector()).toVector();
                    dir.normalize().multiply(300);
                    loc.setDirection(dir);
                    move.getShorts()
                            .write(0, (short) 1)//dir.getBlockX())
                            .write(1, (short) 0)//dir.getBlockY())
                            .write(2, (short) 0);//dir.getBlockZ());
                    move.getBytes()
                            .write(0, (byte)(loc.getYaw()*256/360))
                            .write(1, (byte) (loc.getPitch()*256/360));
                    move.getBooleans().write(0,true);

                    PacketContainer look = new PacketContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
                    look.getIntegers().write(0,100002);
                    look.getBytes().write(0,(byte)(loc.getYaw()*256/360));

                    dir.multiply(1.0/(32*128));
                    loc.add(dir);
                    try {
                        //manager.sendServerPacket(player,look);
                        manager.sendServerPacket(player,move);
                        Logger.getGlobal().info("send movelook to : "+player.getName()+" "+move.getBytes().read(0)
                                +" "+move.getBytes().read(1));
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    counter++;
                } else {
                    cancel();
                    player.sendMessage("Removing mob");

                    PacketContainer destroy = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
                    destroy.getIntegerArrays().write(0, new int[]{100002});
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

    private int getEntityType(String type) {
        EntityType entityType = EntityType.SKELETON;
        try {
            entityType = EntityType.valueOf(type.toUpperCase());
        } catch(Exception ignored) {  }
        switch(entityType) {
            case BAT:
                return 3;
            case BEE:
                return 4;
            case BLAZE:
                return 5;
            case CAT:
                return 7;
            case CAVE_SPIDER:
                return 8;
            case CHICKEN:
                return 9;
            case COD:
                return 10;
            case COW:
                return 11;
            case CREEPER:
                return 12;
            case DOLPHIN:
                return 13;
            case DONKEY:
                return 14;
            case DROWNED:
                return 16;
            case ELDER_GUARDIAN:
                return 17;
            case ENDER_DRAGON:
                return 19;
            case ENDERMAN:
                return 20;
            default: //skeleton
                return 73;
        }
    }


}