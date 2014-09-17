package com.sourcefight.attacks;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.sourcefight.general.Character;
import com.sourcefight.general.Character.BlockLevel;

public class YukiSlamAttack extends Attack {
	private int beginRunTime = 350;
	private int whackRecoverTime = 650;
	private int whackHurtTime = 350;
	private int whackDuration = 350;
	private int whackBeginTime = 0;
	private float runSpeed = 0.75f;
	private int whackDistance = 70;
	private int attackPhase = 0; //0 = wait and run; 1 = begin whack prep.; 2 = whacking can hurt; 3 = finish
	
	public YukiSlamAttack(){
//		super(damage, durationFull, durationCancel, startTimeEffective, endTimeEffective, hitBox, clawForce, clawForce);
		super(null, BlockLevel.Middle, 25, 99999, 99999, 99999, 99999, new Rectangle(50,0,80,80), new Vector2f(0f, 0f), new Vector2f(1.6f, -1.75f));
		special = true;
	}
	
	public void update(int delta, Character owner, Character other){
		if(attackPhase == 0 && progress > beginRunTime){
			owner.move(new Vector2f(runSpeed * delta * (facingRight ? 1 : -1), 0f));
			if(Math.abs(other.getX() - owner.getX()) < whackDistance){
				whackBeginTime = progress;
				attackPhase++;
			}
		}
		else if(attackPhase == 1 && progress > whackBeginTime + whackHurtTime){
			attackPhase++;
		}
		else if(attackPhase == 2 && (hasHit || progress > whackBeginTime + whackHurtTime + whackDuration)){
			attackPhase++;
		}
		else if(attackPhase == 3 && progress > whackBeginTime + whackHurtTime + whackDuration + whackRecoverTime){
			finishAttack(owner);
		}
		
		super.update(delta, owner, other);
	}
	
	public boolean canDoDamage(Character other){
		return attackPhase == 2 && progress > whackBeginTime + whackHurtTime && !hasHit && other.getHitbox().intersects(hitBox);
	}
	
	public void start(Character owner, boolean facingRight){
		super.start(owner, facingRight);
		owner.setGravityMultiplier(.4f);
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
