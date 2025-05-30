package com.karel.game;
import java.util.List;

import com.karel.game.particles.Explosion;

/**
 * A bullet that can hit asteroids.
 * 
 * @author Poul Henriksen
 */
public class Grenade extends FlyingRock
{
    private int life = 45;
    public Grenade(double rotation, double targetdistance, double height, GridObject source)
    {
        super(rotation, targetdistance, height, source);
        setRange(100);
        setDamage(250);
        setCheckHitMode(2);
        setDieOnHit(false);
    }
    public double getGravity(){
        return 2;
    }
    public void update(){
        super.update();
        life--;
        if(life<=0){
            die();
        }
    }
    public void die(){
        for(int i = 0; i <= 360; i+=30){
            GrenadeFragment wb = new GrenadeFragment(i+getDirection(), this);
            addObjectHere(wb);
        }
        addObjectHere(new Explosion(getRange()/60));
        super.die();
    }
}
