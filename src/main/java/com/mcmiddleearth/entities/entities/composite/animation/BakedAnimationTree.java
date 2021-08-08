package com.mcmiddleearth.entities.entities.composite.animation;

import com.mcmiddleearth.entities.entities.composite.BakedAnimationEntity;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class BakedAnimationTree {

    private BakedAnimation animation;

    private final Map<String,BakedAnimationTree> children = new HashMap<>();

    public BakedAnimationTree(BakedAnimation animation) {
        this.animation = animation;
    }

    public void addAnimation(String path, BakedAnimation animation) {
        addAnimation(path.split("\\."),animation);
    }

    public void addAnimation(String[] path, BakedAnimation animation) {
        if(path.length==0) {
            return;
        }
        BakedAnimationTree child = children.get(path[0]);
        if(child == null) {
            child = new BakedAnimationTree(null);
            children.put(path[0], child);
        }
        if(path.length>1) {
            child.addAnimation(subPath(path)/*Arrays.copyOfRange(path,1,path.length-1)*/,animation);
        } else {
            child.animation = animation;
        }
    }

    public BakedAnimation getAnimation(String path) {
        String[] pathKeys = path.split("\\.");
        return getAnimation(pathKeys);
    }

    public BakedAnimation getAnimation(String[] path) {
//Logger.getGlobal().info("path: "+Joiner.on('.').join(path));
        if(path.length==0) {
            return null;
        } else {
            BakedAnimationTree child = children.get(path[0]);
//Logger.getGlobal().info("child: "+child);
            if(child == null) {
                return null;
            } else {
                if(path.length==1) {
                    return child.animation;
                } else {
//Logger.getGlobal().info("rekurse");
                    return child.getAnimation(subPath(path));
                }
            }
        }
    }

    public BakedAnimation getAnimation(BakedAnimationEntity entity) {
        String[] path = new String[] {
                entity.getMovementType().name().toLowerCase(),
                entity.getMovementSpeedAnimation().name().toLowerCase(),
                entity.getActionType().name().toLowerCase()
        };
        /*switch(entity.getMovementType()) {
            case FLYING:
                path = path + "flying";
                break;
        } else if(entity.isFlying()) {
            path = path + "air";
        } else {
            path = path + "water";
        }
        double speed = entity.getVelocity().lengthSquared();
        if(speed < 0.1) {
            path = path + ".idle";
        } else if(speed < 2) {
            path = path + ".walk";
        } else {
            path = path + ".sprint";
        }
        if(entity.isDead()) {
            path = path + ".death";
        } else if(entity.isAttacking()) {
            path = path + ".attack";
        } else if(entity.isInteracting()) {
            path = path + ".interact";
        }*/
        SearchResult searchResult = searchAnimation(path/*.split("\\.")*/);
        BakedAnimation result = searchResult.getBestMatch();
/*if(result == null ){// || entity.getActionType().equals(ActionType.ATTACK)) {
    Logger.getGlobal().info("Path: "+Joiner.on('.').join(path));
}
/*if(entity.getActionType().equals(ActionType.ATTACK)) {
    Logger.getGlobal().info("Attack result: "+(result == null?"null":result.getName()));
}*/
        return result;
    }

    private SearchResult searchAnimation(String[] path) {
        BakedAnimationTree next = children.get(path[0]);
        if(next != null) {
            if(path.length == 1) {
                return new SearchResult(next.animation,null,true);
            } else {
                path = subPath(path);//Arrays.copyOfRange(path, 1, path.length - 1);
                SearchResult searchResult = next.searchAnimation(path);
                if(searchResult.getAnimation()!=null) {
                    return searchResult;
                }
                SearchResult alternative = searchAlternative(path);
                if(alternative!=null) {
                    return alternative;
                } else {
                    return searchResult;
                }
            }
        } else {
            if(path.length>1) {
                SearchResult alternative = searchAlternative(subPath(path));//Arrays.copyOfRange(path, 1, path.length - 1));
                if(alternative!=null) {
                    return alternative;
                }
            }
            return new SearchResult(null,animation,false);
        }
    }

    private SearchResult searchAlternative(String[] path) {
        while(path.length>0) {
            BakedAnimationTree next = children.get(path[0]);
            if(next != null) {
                if(path.length == 1) {
                    return new SearchResult(next.animation,null,false);
                } else {
                    path = subPath(path);
                    SearchResult alternative = next.searchAlternative(path);
                    if (alternative!=null && alternative.getAnimation() != null) {
                        alternative.setExactMatch(false);
                        return alternative;
                    }
                }
            } else {
                path = subPath(path);
            }
            /*for (Map.Entry<String, BakedAnimationTree> entry : children.entrySet()) {
                //if (entry.getValue() != next) {
                    SearchResult alternative = entry.getValue().searchAnimation(path);
                    if (alternative.getAnimation() != null) {
                        alternative.setExactMatch(false);
                        return alternative;
                    }
                //}
            }
            if(path.length>1) {
                path = Arrays.copyOfRange(path, 1, path.length - 1);
            } else {
                path = new String[0];
            }*/
        }
        return null;
    }

    public static class SearchResult {

        private final BakedAnimation animation;
        private final BakedAnimation baseAnimation;
        private boolean exactMatch;

        public SearchResult(BakedAnimation animation, BakedAnimation replacement, boolean exactMatch) {
            this.animation = animation;
            this.baseAnimation = replacement;
            this.exactMatch = exactMatch;
        }

        public BakedAnimation getBestMatch() {
            return animation != null ? animation : baseAnimation;
        }

        public boolean isExactMatch() {
            return exactMatch;
        }

        public void setExactMatch(boolean exactMatch) {
            this.exactMatch = exactMatch;
        }

        public BakedAnimation getAnimation() {
            return animation;
        }

        public BakedAnimation getBaseAnimation() {
            return baseAnimation;
        }
    }

    private String[] subPath(String[] path) {
        if(path.length <= 1) {
            return new String[0];
        } else {
            return Arrays.copyOfRange(path, 1, path.length);
        }
    }

    public void debug() {
        Logger.getGlobal().info("Node: "+this.toString()+" "+(animation==null?"null":animation.getName()));
        children.forEach((key,value)->{
            Logger.getGlobal().info("child:"+ this.toString()+" -> "+key);
            value.debug();
        });
    }

    public List<String> getAnimationKeys() {
        List<String> result = new ArrayList<>();
        if(animation != null) {
            result.add(animation.getName());
        }
        children.forEach((key, child) -> result.addAll(child.getAnimationKeys()));
        return result.stream().sorted().collect(Collectors.toList());
    }


}
