package com.sourcefight.attacks;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.sourcefight.general.Character;
import com.sourcefight.general.Character.BlockLevel;


public class DinoClawsAttack extends Attack {
	private static int numSlashesTotal = 5;
	private static int slashDuration = 300;
	private static int timeBetweenSlashes = 80;
	private static Vector2f slashForce = new Vector2f(1,0);
	
	private int currentSlash = 0;
	
	public DinoClawsAttack(){
//		super(damage, durationFull, durationCancel, startTimeEffective, endTimeEffective, hitBox, clawForce, clawForce);
		super(null, BlockLevel.Middle, 8, numSlashesTotal*slashDuration+(numSlashesTotal-1)*timeBetweenSlashes, 99999, 250, numSlashesTotal*slashDuration+(numSlashesTotal-1)*timeBetweenSlashes,
			  new Rectangle(50,0,80,80), slashForce, slashForce);
		special = true;
	}
	
	public void update(int delta, Character owner, Character other){
		if((currentSlash == 0 && progress > 200) ||
			progress > 200 + slashDuration*currentSlash + timeBetweenSlashes*(currentSlash-1)){ //200 = startTime
			if(currentSlash <= numSlashesTotal){
				currentSlash++;
				hasHit = false;
				Vector2f momentum = this.momentum.copy();
				if(!facingRight){
					momentum.x *= -1;
				}
				owner.setMomentum(momentum);
			}
		}
		
		super.update(delta, owner, other);
	}
	
	public boolean canDoDamage(Character other){
		return progress > 200 + (slashDuration * (currentSlash-1)) + (timeBetweenSlashes * (currentSlash-1)) &&
			   progress < 200 + (slashDuration * (currentSlash)) + (timeBetweenSlashes * (currentSlash-1)) &&
			   !hasHit && other.getHitbox().intersects(hitBox);
	}
	
	public void reset(int time){
		super.reset(time);
		currentSlash = 0;
	}
}
