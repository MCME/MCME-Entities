package com.mcmiddleearth.entities.entities;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.goal.Goal;
import com.mcmiddleearth.entities.ai.goal.GoalVirtualEntity;
import com.mcmiddleearth.entities.ai.movement.EntityBoundingBox;
import com.mcmiddleearth.entities.ai.movement.MovementEngine;
import com.mcmiddleearth.entities.ai.movement.MovementType;
import com.mcmiddleearth.entities.entities.attributes.VirtualAttributeFactory;
import com.mcmiddleearth.entities.entities.composite.SpeechBalloon;
import com.mcmiddleearth.entities.entities.composite.SpeechBalloonLayout;
import com.mcmiddleearth.entities.events.events.McmeEntityDamagedEvent;
import com.mcmiddleearth.entities.events.events.McmeEntityDeathEvent;
import com.mcmiddleearth.entities.events.events.goal.GoalChangedEvent;
import com.mcmiddleearth.entities.events.events.virtual.VirtualEntityAttackEvent;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import com.mcmiddleearth.entities.protocol.packets.AbstractPacket;
import com.mcmiddleearth.entities.protocol.packets.DisplayNamePacket;
import com.mcmiddleearth.entities.util.UuidGenerator;
import org.bukkit.Location;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public abstract class VirtualEntity implements McmeEntity, Attributable {

    private UUID uniqueId;

    private String name;
    private String displayName;

    private final Set<Player> viewers = new HashSet<>();

    private final Set<Player> whiteList = new HashSet<>();

    private boolean invertWhiteList = false;

    protected int tickCounter = 0;

    protected AbstractPacket spawnPacket;
    protected AbstractPacket removePacket;
    protected AbstractPacket teleportPacket;
    protected AbstractPacket movePacket;
    protected AbstractPacket statusPacket;
    protected AbstractPacket namePacket;

    private Location location;

    //private float rotation; //remove and replace with location.yaw

    private Vector velocity;

    private boolean lookUpdate, rotationUpdate;

    private boolean teleported;

    private MovementType movementType;

    private GoalVirtualEntity goal;

    private final McmeEntityType type;

    private final Map<Attribute, AttributeInstance> attributes = new HashMap<>();

    private final EntityBoundingBox boundingBox;

    private final MovementEngine movementEngine;

    private final int updateInterval = 10;

    private final int updateRandom = new Random().nextInt(10);

    private final int jumpHeight = 1;
    private final int fallDepth = 1; //if both values differ from each other pathfinding can easily get stuck.
    private final float knockBackPerDamage = 0.01f;

    private int health;
    private boolean dead = false;
    private int deathCounter = 0;

    private boolean isTerminated = false;

    private int attackCoolDown = 40;

    private Set<McmeEntity> attackers = new HashSet<>();

    private Map<Player,SpeechBalloon> speechBallons = new HashMap<>();
    private String[] speech;
    private int speechCounter;

    private SpeechBalloonLayout defaultSpeechBalloonLayout, currentSpeechBalloonLayout;

    private Vector mouth;

    public VirtualEntity(VirtualEntityFactory factory) throws InvalidLocationException {
        this.type = factory.getType();
        this.location = factory.getLocation();
        this.velocity = new Vector(0, 0, 0);
        this.uniqueId = factory.getUniqueId();
        this.name = factory.getName();
        this.displayName = factory.getDisplayName();
        this.invertWhiteList = factory.isInvertWhitelist();
        this.movementType = factory.getMovementType();
        this.boundingBox = factory.getBoundingBox();
        this.boundingBox.setLocation(location);
        this.movementEngine = new MovementEngine(this);
        this.goal = factory.getGoalFactory().build(this);
        this.health = 20;
        this.namePacket = new DisplayNamePacket(this.getEntityId());
        this.defaultSpeechBalloonLayout = factory.getSpeechBalloonLayout();
        this.mouth = factory.getMouth();
    }

    protected VirtualEntity(McmeEntityType type, Location location) {
        this.type = type;
        this.location = location;
        this.velocity = new Vector(0, 0, 0);
        this.uniqueId = UuidGenerator.getRandomV2();
        this.boundingBox = new EntityBoundingBox(0,0,0,0);
        this.movementEngine = null;

    }

    @Override
    public void doTick() {
//Logger.getGlobal().info("VirtualEntity: tick ");
        if(teleported) {
            teleport();
            if(goal!=null) {
                goal.update();
            }
        } else {
            if(goal != null) {
                goal.doTick(); //tick before update to enable update do special stuff that doesn't get overridden by doTick
                if(tickCounter%goal.getUpdateInterval()==goal.getUpdateRandom()) {
                    goal.update();
//Logger.getGlobal().info("Goal update: "+ tickCounter +" "+ goal.getUpdateInterval() + " "+goal.getUpdateRandom());
//Logger.getGlobal().info("Goal update: rotation: "+ goal.hasRotation());
                }
                /*switch(movementType) {
                    case FLYING:
                    case WALKING:
                        goal.doTick();
                }*/
                movementEngine.calculateMovement(goal.getDirection());
                if(goal.hasHeadRotation()) {
//Logger.getGlobal().info("head rotation: "+ goal.getHeadYaw()+" "+goal.getHeadPitch());
                    setHeadRotation(goal.getHeadYaw(), goal.getHeadPitch());
                }
                if(goal.hasRotation()) {
//Logger.getGlobal().info("rotation: "+ goal.getRotation());
                    setRotation(goal.getRotation());
                }
            } else {
                movementEngine.calculateMovement(new Vector(0,0,0));
            }
            move();
            attackCoolDown = Math.max(0, --attackCoolDown);
        }
        tickCounter++;
//Logger.getGlobal().info("+");
        if(isDead() && !isTerminated) {
            deathCounter++;
            if(deathCounter>20) {
                terminate();
            }
//Logger.getGlobal().info("Death counter: "+deathCounter);
        }
        speechCounter = Math.max(-1, --speechCounter);
        if(speechCounter == 0) {
            removeSpeechBalloons();
        }
    }

    public void teleport() {
        teleportPacket.update();
        teleportPacket.send(viewers);
        teleported = false;
        lookUpdate = false;
        rotationUpdate = false;

        spawnPacket.update();
    }

    public void move() {
//Logger.getGlobal().info("move");
//Logger.getGlobal().info("location old: "+ getLocation());
//Logger.getGlobal().info("velocity: "+ velocity+" yaw: "+getRotation()+" head: "+location.getYaw()+" "+location.getPitch());
        location = location.add(velocity);
//Logger.getGlobal().info("location new: "+ getLocation().getX()+" "+getLocation().getY()+" "+getLocation().getZ());
        boundingBox.setLocation(location);

        if(tickCounter % updateInterval == updateRandom) {
            teleportPacket.update();
            teleportPacket.send(viewers);
        } else {
            movePacket.update();
            movePacket.send(viewers);
        }
        lookUpdate = false;
        rotationUpdate = false;

        spawnPacket.update();
    }

    @Override
    public void setLocation(Location location) {
//Logger.getGlobal().info("Teleport!");
        this.location = location.clone();
        this.boundingBox.setLocation(location);
        teleported = true;
    }

    @Override
    public void setVelocity(Vector velocity) {
/*if (!checkFinite(velocity)) {
    Logger.getGlobal().info("set Velocity: "+velocity.getX()+" "+velocity.getY()+" "+velocity.getZ());
    throw new IllegalArgumentException("Set Velocity");
}*/
        this.velocity = velocity;
    }

    public void setMovementType(MovementType movementType) {
        if(!this.movementType.equals(MovementType.FALLING)
              && movementType.equals(MovementType.FALLING)) {
            movementEngine.setFallStart(boundingBox.getMin().getY());
        }
        this.movementType = movementType;
    }

    public void setHeadRotation(float yaw, float pitch) {
        getLocation().setYaw(yaw);
        getLocation().setPitch(pitch);
        lookUpdate = true;
    }

    @Override
    public void setRotation(float yaw) {
        location.setYaw(yaw);
        //rotation = yaw;
        rotationUpdate = true;
    }

    @Override
    public float getRotation() {
        return location.getYaw();//rotation;
    }

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public Goal getGoal() {
        return goal;
    }

    @Override
    public void setGoal(Goal goal) {
        if(goal instanceof GoalVirtualEntity && this.goal!=goal) {
            GoalChangedEvent event = new GoalChangedEvent(this, this.goal, goal);
            EntitiesPlugin.getEntityServer().handleEvent(event);
            if(!event.isCancelled()) {
                this.goal = (GoalVirtualEntity) goal;
            }
        }
    }

    @Override
    public Vector getVelocity() {
        return velocity;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    @Override
    public boolean hasLookUpdate() {
        return lookUpdate;
    }

    @Override
    public boolean hasRotationUpdate() {
        return rotationUpdate;
    }

    @Override
    public Location getTarget() {
        return null;
    }

    @Override
    public boolean onGround() {
        return true;
    }

    public EntityBoundingBox getBoundingBox() {
        return boundingBox;
    }

    @Override
    public McmeEntityType getType() {
        return type;
    }

    public boolean isViewer(Player player) {
        return viewers.contains(player);
    }

    public Set<Player> getViewers() {
        return viewers;
    }

    public synchronized void addViewer(Player player) {
        if(!invertWhiteList && !(whiteList.isEmpty() || whiteList.contains(player))
                || invertWhiteList && whiteList.contains(player)) {
            return;
        }
        spawnPacket.send(player);
        if(displayName!=null) {
            namePacket.send(player);
        }
        viewers.add(player);
        if(speech != null) {
            createSpeechBalloon(player);
        }
    }

    public synchronized void removeViewer(Player player) {
        removePacket.send(player);
        viewers.remove(player);
        SpeechBalloon balloon = speechBallons.get(player);
        if(balloon != null) {
            speechBallons.remove(player);
            EntitiesPlugin.getEntityServer().removeEntity(balloon);
        }
    }

    public void removeAllViewers() {
        List<Player> removal = new ArrayList<>(viewers);
        removal.forEach(this::removeViewer);
    }

    @Override
    public AttributeInstance getAttribute(@NotNull Attribute attribute) {
        return attributes.get(attribute);
    }

    @Override
    public void registerAttribute(@NotNull Attribute attribute) {
        attributes.put(attribute, VirtualAttributeFactory.getAttributeInstance(attribute, null));
    }

    public int getJumpHeight() {
        return jumpHeight;
    }

    public int getFallDepth() {
        return fallDepth;
    }

    public int getHealth() { return health;}

    public void damage(int damage) {
        McmeEntityDamagedEvent event = new McmeEntityDamagedEvent(this, damage);
        EntitiesPlugin.getEntityServer().handleEvent(event);
        if(!event.isCancelled()) {
            health -= event.getDamage();
            if (health <= 0) {
                EntitiesPlugin.getEntityServer().handleEvent(new McmeEntityDeathEvent(this));
                dead = true;
                playAnimation(AnimationType.DEATH);
                //Logger.getGlobal().info("Dead!");
            } else {
                playAnimation(AnimationType.HURT);
            }
        }
    }

    public void heal(int damage) {
        health = Math.min(health + damage, 20);
    }

    public boolean isDead() {
        return dead;
    }

    @Override
    public void receiveAttack(McmeEntity damager, int damage, float knockBackFactor) {
        damage(damage);
        double length = 0.2+damage*knockBackFactor*knockBackPerDamage;
        Vector normal = damager.getLocation().clone().subtract(location.toVector()).toVector().normalize();
        Vector knockBack = normal.multiply(-length).add(new Vector(0,length*2,0));
        setMovementType(MovementType.FALLING);
//Logger.getGlobal().info("Set Velocity: "+ knockBack.getX()+" "+knockBack.getY()+" "+knockBack.getZ());
        setVelocity(knockBack);
        attackers.add(damager);
    }

    @Override
    public void attack(McmeEntity target) {
        VirtualEntityAttackEvent event = new VirtualEntityAttackEvent(this,target);
        EntitiesPlugin.getEntityServer().handleEvent(event);
        if(!event.isCancelled()) {
            playAnimation(AnimationType.ATTACK);
            target.receiveAttack(this, 2, 1);
            attackCoolDown = 40;
        }
    }

    public int getAttackCoolDown() {
        return attackCoolDown;
    }

    @Override
    public Set<McmeEntity> getAttackers() {
        return attackers;
    }

    @Override
    public boolean isTerminated() {
        return isTerminated;
    }

    public void terminate() {
        isTerminated = true;
    }

    @Override
    public  void finalise() {
        removeSpeechBalloons();
    }

    @Override
    public void playAnimation(AnimationType type) { }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        ((DisplayNamePacket)namePacket).setName(displayName);
        namePacket.send(viewers);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void say(String[] lines, int duration) {
        say(lines, defaultSpeechBalloonLayout, duration);
    }

    public void say(String[] lines, SpeechBalloonLayout layout, int duration) {
        this.speech = lines;
        speechCounter = duration;
        currentSpeechBalloonLayout = layout;
        removeSpeechBalloons();
        viewers.forEach(this::createSpeechBalloon);
    }

    private void createSpeechBalloon(Player viewer) {
        try {
            SpeechBalloon balloon = EntitiesPlugin.getEntityServer().spawnSpeechBalloon(this, viewer, currentSpeechBalloonLayout, speech);
            speechBallons.put(viewer,balloon);
        } catch (InvalidLocationException e) {
            e.printStackTrace();
        }
    }

    private void removeSpeechBalloons() {
        speechBallons.forEach((player, balloon) -> balloon.terminate());
        speechBallons.clear();
    }

    public void stopTalking() {
        speechCounter = 1;
    }

    @Override
    public Vector getMouth() {
        return mouth;
    }

    public Set<Player> getWhiteList() {
        return whiteList;
    }

    public boolean isInvertWhiteList() {
        return invertWhiteList;
    }

    /*Location loc;
    public void _test_spawn_(Player player) {
        PacketContainer spawn = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        loc = player.getLocation().add(new Vector(1,0,1));
        spawn.getIntegers().write(0, 100005)
                .write(1,73);
        spawn.getUUIDs().write(0, UUID.randomUUID());
        spawn.getDoubles()
                .write(0, loc.getX())
                .write(1, loc.getY())
                .write(2, loc.getZ());
        spawn.getBytes()
                .write(0, (byte) 0) //yaw
                .write(1, (byte) 0)//pitch
                .write(2, (byte) 10); //head pitch
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket((Player) player, spawn);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    int _test_turn_ = 0;
    public void _test_move_() {
        PacketContainer move = new PacketContainer(PacketType.Play.Server.ENTITY_LOOK);
        move.getIntegers().write(0,100005);
        move.getBytes()
                .write(0, (byte)(_test_turn_*256/360))
                .write(1, (byte) (_test_turn_/4*256/360));
        move.getBooleans().write(0,true);
        _test_turn_++;
        if(_test_turn_==360) {
            _test_turn_ = 0;
        }
        try {
            for (Player viewer : viewers) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(viewer, move);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void _test_move_2() {
        PacketContainer move = new PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE_LOOK);
        move.getIntegers().write(0,100005);
        Player player = Bukkit.getPlayer("Eriol_Eandur");
        Vector dir = player.getLocation().subtract(loc.toVector()).toVector();
        dir.normalize().multiply(3);
        loc.setDirection(dir);
        dir.multiply(_test_turn_%2==0?1:-1);
        move.getShorts()
                .write(0, (short) dir.getBlockX())
                .write(1, (short) dir.getBlockY())
                .write(2, (short) dir.getBlockZ());
        move.getBytes()
                .write(0, (byte)(loc.getYaw()*256/360))
                .write(1, (byte) (loc.getPitch()*256/360));
        _test_turn_++;
        if(_test_turn_==360) {
            _test_turn_ = 0;
        }

        //move.getBytes()
          //      .write(0, (byte)(loc.getYaw()*256/360))
            //    .write(1, (byte) (loc.getPitch()*256/360));
        move.getBooleans().write(0,true);

        PacketContainer look = new PacketContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        look.getIntegers().write(0,100005);
        look.getBytes().write(0,(byte)(loc.getYaw()*256/360));

        dir.multiply(1.0/(32*128));
        loc.add(dir);
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player,look);
            ProtocolLibrary.getProtocolManager().sendServerPacket(player,move);
            //Logger.getGlobal().info("send movelook to : "+player.getName()+" "+move.getBytes().read(0)
             //       +" "+move.getBytes().read(1));
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }*/
}
