package com.karel.game.weapons.shotgun;
import java.util.List;

import com.karel.game.GridEntity;
import com.karel.game.GridObject;
import com.karel.game.Reelin;
import com.karel.game.StunEffect;
import com.karel.game.weapons.EffectID;
import com.raylib.Raylib;
import com.raylib.Vector2;
import com.raylib.Color;

import java.util.ArrayList;

/**
 * A bullet that can hit asteroids.
 * 
 * @author Poul Henriksen
 */
public class Lasso extends Reelin
{
    private List<GridEntity> targets;
    private EffectID pullStun, afterStun;
    private boolean immobiletarget;//if a target is immobile, pull source to center of all immovable targets instead
    private int latchx, latchy;//where the source is pulled too, the center point between all immovable objects

    private int animationFrame = 0;
    public Lasso(double rotation, double targetdistance, GridObject source, boolean fast)
    {
        super(rotation, targetdistance, targetdistance/2/(fast?2:1), source);
        setImage("Weapons/shotgun/projUltUpgrade.png");
        scaleTexture(100);
        setRange(100);
        targets = new ArrayList<GridEntity>();
        pullStun = new EffectID(this, "pull");
        afterStun = new EffectID(this, "pullfinish");
        setDamage(0);
        setSpeed(17);
    }
    public void doReturn(){
        if(!updateCenter()){
            for(GridEntity target: targets){
                target.stun(pullStun);
                target.pullTowards(this, 15);
            }
            super.doReturn();
        }else{
            pullTowards(latchx, latchy, 20);
            getSource().stun(pullStun);
            getSource().pullTowards(this, 10);
            if(distanceTo(getSource())<20){
                die();
            }
        }
    }
    public void render(){
        super.render();
        if(isReturning()){
            animationFrame = (int)face(getSource(), false)-90;
        }
        //draw line between source and me
        double x = getX()+Math.cos(animationFrame*Math.PI/180)*50;
        double y = getY()-getHeight()+Math.sin(animationFrame*Math.PI/180)*getImage().getHeight()/2;
        animationFrame+=20;
        if(animationFrame>=360) animationFrame = 0;
        Raylib.drawLineEx(
            new Vector2(renderTransformX((int)x), renderTransformY((int)y)),
            new Vector2(renderTransformX((int)getSource().getX()), renderTransformY((int)(getSource().getY()-getSource().getHeight()))),
            7,
            new Color(
                (byte)170, (byte)122, (byte)23, (byte)255
            )
        );
    }
    public void die(){
        for(GridEntity target:targets){
            if(!target.isDead()){
                target.unstun(pullStun);
                target.applyEffect(new StunEffect(20, getSource(), afterStun));
            }
            if(immobiletarget){
                if(getSource()!=null&&!getSource().isDead()){
                    getSource().unstun(pullStun);
                }
            }
        }
        super.die();
    }
    //clean up list, removing dead entities, set the center in case of immovability, and return if it should pull or not
    public boolean updateCenter(){//returns true if there are immovable targets
        latchx = 0;
        latchy = 0;
        int immobs = 0;
        for(int i = targets.size()-1; i >= 0; i--){
            GridEntity target = targets.get(i);
            if(target.isDead()){
                targets.remove(i);
            }else if(!target.canBePulled()){
                immobs++;
                latchx+=target.getX();
                latchy+=target.getY();
            }
        }
        if(immobs>0){latchx/=immobs;
        latchy/=immobs;}
        immobiletarget = immobs>0;
        return immobs>0;
    }
    public void doHit(GridEntity targ){
        super.doHit(targ);
        targets.add(targ);
        targ.stun(pullStun);
    }
    public GridEntity getSource(){
        return (GridEntity)(super.getSource());
    }
    public double getGravity(){
        return 3;
    }
}
