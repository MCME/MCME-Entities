package com.mcmiddleearth.entities.entities.composite.animation;

import com.google.common.base.Joiner;
import com.mcmiddleearth.entities.api.ActionType;
import com.mcmiddleearth.entities.api.MovementSpeed;
import com.mcmiddleearth.entities.entities.composite.BakedAnimationEntity;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class BakedAnimationTree {

    private final List<BakedAnimation> animations;

    private final Map<String,BakedAnimationTree> children = new HashMap<>();

    private final Random random = new Random();

    public BakedAnimationTree(BakedAnimation animation) {
        this.animations = new ArrayList<>();
        if(animation != null) this.animations.add(animation);
    }

    public void addAnimation(String path, BakedAnimation animation) {
        String[] pathArray = path.split("\\.");
        addAnimation(pathArray,animation);
        for(int i = 0; i < pathArray.length; i++) {
            try{
                String[] temp = pathArray.clone();
                MovementSpeed speed = MovementSpeed.valueOf(temp[i].toUpperCase());
                switch (speed) {
                    case SLOW:
                        temp[i] = MovementSpeed.BACKWARD_SLOW.name().toLowerCase();
                        addBackwardFallbackAnimation(temp, animation);
                        break;
                    case WALK:
                        temp[i] = MovementSpeed.BACKWARD_WALK.name().toLowerCase();
                        addBackwardFallbackAnimation(temp, animation);
                        break;
                    case SPRINT:
                        temp[i] = MovementSpeed.BACKWARD_SPRINT.name().toLowerCase();
                        addBackwardFallbackAnimation(temp, animation);
                        break;
                }
            } catch (IllegalArgumentException ignore) {}
        }
    }

    private void addBackwardFallbackAnimation(String[] path, BakedAnimation animation) {
        if(getAnimation(path) == null) {
            addAnimation(path, animation.getReverse(Joiner.on('.').join(path)));
//Logger.getGlobal().info("adding fallback: "+Joiner.on('.').join(path));
        }
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
            child.animations.add(animation);
        }
    }

    public BakedAnimation getAnimation(String path) {
        String[] pathKeys = path.split("\\.");
        //return getAnimation(pathKeys);
        SearchResult searchResult = searchAnimation(pathKeys/*.split("\\.")*/);
        return searchResult.getBestMatch();
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
                    return child.getAnimation();
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
                ActionType.IDLE.name().toLowerCase()
                //entity.getActionType().name().toLowerCase()
        };
        SearchResult searchResult = searchAnimation(path/*.split("\\.")*/);
        return searchResult.getBestMatch();
    }

    private BakedAnimation getAnimation() {
        if(animations.size()>0) {
            return animations.get(random.nextInt(animations.size()));
        } else {
            return null;
        }
    }

    private SearchResult searchAnimation(String[] path) {
        BakedAnimationTree next = children.get(path[0]);
        if(next != null) {
            if(path.length == 1) {
                return new SearchResult(next.getAnimation(),null,true);
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
            return new SearchResult(null,getAnimation(),false);
        }
    }

    private SearchResult searchAlternative(String[] path) {
        while(path.length>0) {
            BakedAnimationTree next = children.get(path[0]);
            if(next != null) {
                if(path.length == 1) {
                    return new SearchResult(next.getAnimation(),null,false);
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
        Logger.getGlobal().info("Node: "+ this +" Animations: "+ animations.size());
        children.forEach((key,value)->{
            Logger.getGlobal().info("child:"+ this +" -> "+key);
            value.debug();
        });
    }

    public List<String> getAnimationKeys() {
        List<String> result = new ArrayList<>();
        animations.forEach(anim -> result.add(anim.getName()));
        children.forEach((key, child) -> result.addAll(child.getAnimationKeys()));
        return result.stream().sorted().collect(Collectors.toList());
    }


}
