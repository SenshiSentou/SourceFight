package com.sourcefight.attacks;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.sourcefight.general.Character;
import com.sourcefight.general.Character.AnimationState;
import com.sourcefight.general.Character.BlockLevel;


public class DinoClawsAttack extends Attack {
	private static int numSlashesTotal = 5;
	private static int slashDuration = 336; //300
	private static int timeBetweenSlashes = 0; //80
	private static Vector2f slashForce = new Vector2f(0.4f,0);
	
	private int currentSlash = 0;
//	private Animation anim;
	//private boolean finishingFinalSlash = false;
	
	public DinoClawsAttack(Character owner){
//		super(damage, durationFull, durationCancel, startTimeEffective, endTimeEffective, hitBox, clawForce, clawForce);
		super(owner, null, 0, AnimationState.SpecialAttack1, BlockLevel.Middle, 8, 600, 800,
			  99999, 250, numSlashesTotal*slashDuration+(numSlashesTotal-1)*timeBetweenSlashes, new Rectangle(10,90,80,95), slashForce, slashForce);
		setDurationFull(numSlashesTotal*slashDuration+(numSlashesTotal-1)*timeBetweenSlashes);
		special = true;
	}
	
	public void update(int delta){
		if((currentSlash == 0 && progress > 168) || //progress > 200
			progress > 168 + slashDuration*currentSlash + timeBetweenSlashes*(currentSlash-1)){ //168 = startTime
			if(currentSlash <= numSlashesTotal){
				hasHit = false;
				Vector2f momentum = this.momentum.copy();
				if(!facingRight){
					momentum.x *= -1;
				}
				owner.setMomentum(momentum);
//				if(currentSlash == 2 || currentSlash == 4){ //reset animation to loop previous two slashes
//					anim.setCurrentFrame(5);
//				}
				currentSlash++;
				
			}
		}
		
//		if(progress > 1722+168 && !finishingFinalSlash){
//			anim.setCurrentFrame(21);
//			finishingFinalSlash = true;
//		}
		
		super.update(delta);
	}
	
	public boolean canDoDamage(){
		return progress > 200 + (slashDuration * (currentSlash-1)) + (timeBetweenSlashes * (currentSlash-1)) &&
			   progress < 200 + (slashDuration * (currentSlash)) + (timeBetweenSlashes * (currentSlash-1)) &&
			   !hasHit && other.getHitbox().intersects(hitBox);
	}
	
	public void reset(int time){
		super.reset(time);
		currentSlash = 0;
//		anim = owner.getAnimation(24, true);
		//finishingFinalSlash = false;
	}
	
	public void setAnimationLoopable(){
		Image[] endFrames = new Image[4];
		Animation[] anims = new Animation[]{owner.getAnimation(25, false), owner.getAnimation(53, false)};
		
		for(int i=0; i<2; i++){
			for(int j=0; j<endFrames.length; j++){
				endFrames[j] = anims[i].getImage(19+j);
			}
			
			for(int j=3; j<7; j++){ //[19-22] --> [3-6]
				anims[i].setFrame(16+j, anims[i].getImage(j), Character.getFrameDuration());
			}
			
			for(int j=6; j<19; j++){ //Add second loop (slash 3+4): [6-18]
				anims[i].addFrame(anims[i].getImage(j), Character.getFrameDuration());
			}
			
			for(int j=3; j<7; j++){ //Add final loop (slash 5): [3-6]
				anims[i].addFrame(anims[i].getImage(j), Character.getFrameDuration());
			}
			
			for(int j=0; j<endFrames.length; j++){ //Add return to idle: [19-22]
				anims[i].addFrame(endFrames[j], Character.getFrameDuration());
			}
		}
	}
}