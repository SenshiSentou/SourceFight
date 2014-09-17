package com.sourcefight.attacks;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.sourcefight.general.Character;
import com.sourcefight.general.Character.BlockLevel;


public class IAmYukiAttack extends Attack {
	private int jumpTime = 250;
	private int stopGravityTime = 850;
	private int crashDownTime = 2500;
	private float hitRange = 2.5f; //AoE only
	private Vector2f crashForce;// = new Vector2f(0f, 1.5f);
	private int attackPhase = 0;
	
	public IAmYukiAttack(){
//		super(damage, durationFull, durationCancel, startTimeEffective, endTimeEffective, hitBox, clawForce, clawForce);
		super(null, BlockLevel.Middle, 40, 4000, 99999, 3000, 4000, new Rectangle(50,100,80,80), new Vector2f(0f, 0f), new Vector2f(3f, -2.5f));
		special = true;
	}
	
	public void update(int delta, Character owner, Character other){
		super.update(delta, owner, other);
		if(attackPhase == 0 && progress > jumpTime){
			owner.jump(true);
			attackPhase++;
		}
		else if(attackPhase == 1 && progress > stopGravityTime){
			owner.setMomentum(new Vector2f(0f, 0f));
			owner.setGravityMultiplier(0f);
			attackPhase++;
		}
		else if(attackPhase == 2 && progress > crashDownTime){
//			crashForce.x = (other.x - owner.x)/1000f;
			crashForce = new Vector2f(Math.atan2(other.getY() - owner.getY(), other.getX() - owner.getX()) * 180/Math.PI).normalise();
			if((crashForce.x > 0f && crashForce.getTheta() < 45) || (crashForce.x < 0f && crashForce.getTheta() > 135)){
				blockLevel = BlockLevel.Middle;
			}
			else{
				blockLevel = BlockLevel.High;
			}
			attackPhase++;
		}
		else if(attackPhase == 3){
			if(!owner.isGrounded()){
				Vector2f moveAmount = crashForce.copy();
				moveAmount.x *= delta;
				moveAmount.y *= delta;
				owner.move(moveAmount);
			}
			else{
				if(!hasHit && Math.abs(other.getX() - owner.getX()) < hitRange){ //AoE
					Vector2f force = this.force.copy();
					if(facingRight){
						force.x *= -1;
					}
					force.x *= 0.5f;
					force.y *= 0.5f;
					other.takeDamage((int)(damage*0.5f), force, blockLevel);
				}
				attackPhase++;
			}
		}
	}
	
	public boolean canDoDamage(Character other){
		return super.canDoDamage(other);
	}
	
	public void start(Character owner, boolean facingRight){
		super.start(owner, facingRight);
		owner.setGravityMultiplier(.2f);
	}
	
	public void finishAttack(Character owner){
		super.finishAttack(owner);
		owner.setGravityMultiplier(1f);
	}
	
	public void reset(int time){
		super.reset(time);
		attackPhase = 0;
	}
}
