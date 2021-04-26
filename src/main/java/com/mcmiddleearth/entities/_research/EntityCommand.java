package com.mcmiddleearth.entities._research;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.PrettyPrinter;
import com.comphenix.protocol.wrappers.*;
import com.mcmiddleearth.entities.EntitiesPlugin;
import io.papermc.paper.chat.ChatComposer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class EntityCommand implements CommandExecutor {

    private ProtocolManager manager = ProtocolLibrary.getProtocolManager();


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        Location loc = player.getLocation().add(new Vector(3,0,3));
        String type = (args.length<1?"armor":args[0]);

        PacketContainer spawn = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        spawn.getIntegers().write(0, 100003)
                           .write(1, -300) //velocity
                           .write(2, 0)
                           .write(3, -300)
                           .write(4, 2*256/360) //yaw
                           .write(5, 10*256/360) //pitch
                           .write(6,0); // object data
        spawn.getEntityTypeModifier().write(0,getEntityType(type));
        spawn.getUUIDs().write(0, UUID.randomUUID());
        spawn.getDoubles()
                .write(0, loc.getX())
                .write(1, loc.getY())
                .write(2, loc.getZ());
        /*spawn.getBytes()
                .write(0, (byte) 0) //yaw
                .write(1, (byte) 0) //pitch
                .write(2,(byte) -300)
                .write(3,(byte) 00)
                .write(4,(byte) -300);*/

        /*spawn.getVectors()
                .write(0, new Vector(-300,0,-300));*/
        /*spawn.getShorts()
                .write(0, (short) -300)
                .write(1, (short) 0)
                .write(2, (short) -300);*/
        try {
            Logger.getGlobal().info(PrettyPrinter.printObject(spawn.getHandle()));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        PacketContainer meta = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        meta.getIntegers().write(0,100003);
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        WrappedDataWatcher.WrappedDataWatcherObject nameValue = new WrappedDataWatcher
                .WrappedDataWatcherObject(2,
                                          WrappedDataWatcher.Registry.getChatComponentSerializer(true));
        WrappedDataWatcher.WrappedDataWatcherObject nameVisible = new WrappedDataWatcher
                .WrappedDataWatcherObject(3,
                                          WrappedDataWatcher.Registry.get(Boolean.class));
        watcher.setObject(nameValue, Optional.of(WrappedChatComponent
                        .fromChatMessage("2He&cl§cl#co!\\ntesta\\\nornow\ntest")[0].getHandle()));
        watcher.setObject(nameVisible, true);
        WrappedDataWatcher.WrappedDataWatcherObject invisibility = new WrappedDataWatcher
                .WrappedDataWatcherObject(0,WrappedDataWatcher.Registry.get(Byte.class));
        watcher.setObject(invisibility, Byte.decode("0x20"),false);
        WrappedDataWatcher.WrappedDataWatcherObject silent = new WrappedDataWatcher
                .WrappedDataWatcherObject(4,WrappedDataWatcher.Registry.get(Boolean.class));
        watcher.setObject(silent, true,false);
        WrappedDataWatcher.WrappedDataWatcherObject gravity = new WrappedDataWatcher
                .WrappedDataWatcherObject(5,WrappedDataWatcher.Registry.get(Boolean.class));
        watcher.setObject(gravity, false,false);
        WrappedDataWatcher.WrappedDataWatcherObject base = new WrappedDataWatcher
                .WrappedDataWatcherObject(14,WrappedDataWatcher.Registry.get(Byte.class));
        watcher.setObject(base, Byte.decode("0x08"),false);
        /*List<WrappedWatchableObject> metadata = new ArrayList<>();
        metadata.add(new WrappedWatchableObject(2,type));
        metadata.add(new WrappedWatchableObject(4,true));
        metadata.add(new WrappedWatchableObject(0xff,null));
        meta.getWatchableCollectionModifier().write(0,metadata);*/
        meta.getWatchableCollectionModifier().write(0,watcher.getWatchableObjects());

        PacketContainer equip = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
        equip.getIntegers().write(0, 100003);
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipment = new ArrayList<>();
        equipment.add(new Pair<>(EnumWrappers.ItemSlot.HEAD,new ItemStack(Material.DIAMOND_HELMET)));
        equipment.add(new Pair<>(EnumWrappers.ItemSlot.CHEST,new ItemStack(Material.DIAMOND_CHESTPLATE)));
        equipment.add(new Pair<>(EnumWrappers.ItemSlot.LEGS,new ItemStack(Material.DIAMOND_LEGGINGS)));
        equipment.add(new Pair<>(EnumWrappers.ItemSlot.FEET,new ItemStack(Material.DIAMOND_BOOTS)));
        equipment.add(new Pair<>(EnumWrappers.ItemSlot.OFFHAND,new ItemStack(Material.DIAMOND_AXE)));
        equipment.add(new Pair<>(EnumWrappers.ItemSlot.MAINHAND,new ItemStack(Material.BOW)));
        equip.getSlotStackPairLists().write(0, equipment);

        try {
            manager.sendServerPacket((Player) sender, spawn);
            manager.sendServerPacket((Player) sender, equip);
            manager.sendServerPacket((Player) sender, meta);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        sender.sendMessage("entity spawned");
        new BukkitRunnable() {
            private int counter = 0;
            @Override
            public void run() {
                if(counter<400) {
                    PacketContainer move = new PacketContainer(PacketType.Play.Server.ENTITY_LOOK);
                    move.getIntegers().write(0,100003);
                    Vector dir = player.getLocation().subtract(loc.toVector()).toVector();
                    dir.normalize().multiply(300);
                    loc.setDirection(dir);
                    /*move.getShorts()
                            .write(0, (short) dir.getBlockX())
                            .write(1, (short) dir.getBlockY())
                            .write(2, (short) dir.getBlockZ());*/
                    move.getBytes()
                            .write(0, (byte)(loc.getYaw()*256/360))
                            .write(1, (byte) (loc.getPitch()*256/360));
                    move.getBooleans().write(0,true);

                    PacketContainer look = new PacketContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
                    look.getIntegers().write(0,100003);
                    look.getBytes().write(0,(byte)(loc.getYaw()*256/360));

                    dir.multiply(1.0/(32*128));
                    //loc.add(dir);
                    try {
                        //manager.sendServerPacket(player,look);
                        manager.sendServerPacket(player,move);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    counter++;
                } else {
                    cancel();
                    player.sendMessage("Removing entity");
                    /*PacketContainer info = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
                    info.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                    PlayerInfoData data = new PlayerInfoData(profile, 0, EnumWrappers.NativeGameMode.SURVIVAL, displayName);
                    info.getPlayerInfoDataLists().write(0, Collections.singletonList(data));*/

                    PacketContainer destroy = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
                    destroy.getIntegerArrays().write(0, new int[]{100003});
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

    private EntityType getEntityType(String type) {
        EntityType entityType = EntityType.ARMOR_STAND;
        try {
            entityType = EntityType.valueOf(type.toUpperCase());
        } catch(Exception ignored) {  }
        return entityType;
        /*switch(entityType) {
            case AREA_EFFECT_CLOUD:
                return 0;
            case ARROW:
                return 2;
            case BOAT:
                return 6;
            case DRAGON_FIREBALL:
                return 15;
            case ENDER_CRYSTAL:
                return 18;
            case EVOKER:
                return 22;
            case ENDER_PEARL:
                return 24;
            case FALLING_BLOCK:
                return 26;
            case FIREWORK:
                return 27;
            case IRON_GOLEM:
                return 36;
            case FIREBALL:
                return 39;
            case SMALL_FIREBALL:
                return 76;
            case LIGHTNING:
                return 41;
            case MINECART:
                return 45;
            case WITHER_SKULL:
                return 99;
            default: //armor stand
                return 1;
        }*/
    }


}