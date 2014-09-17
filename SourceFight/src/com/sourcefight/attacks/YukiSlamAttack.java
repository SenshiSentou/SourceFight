package com.sourcefight.attacks;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.sourcefight.general.Character;
import com.sourcefight.general.Character.AnimationState;
import com.sourcefight.general.Character.BlockLevel;

public class YukiSlamAttack extends Attack {
	private int beginRunTime = 14*Character.getFrameDuration();
	private int whackRecoverTime = 10*Character.getFrameDuration();
	private int whackHurtTime = 5*Character.getFrameDuration();
	private int whackDuration = 9*Character.getFrameDuration(); //2 frames + (8*"hold"-1)
	private int whackBeginTime = 0;
	private float runSpeed = 0.75f;
	private int whackDistance = 70;
	private int attackPhase = 0; //0 = wait and run; 1 = begin whack prep.; 2 = whacking can hurt; 3 = finish
	Animation[] anims = new Animation[2]; //Yuki stick
	int transformedState = 0;
	int[] animOffsetsY = new int[]{-27, 0};
	int[][] animOffsetsX = new int[][]{{-20, -78, 58},
									   {-38, -96, -18}};
	int animOffsetX = animOffsetsX[0][0];
	
	public YukiSlamAttack(Character owner) throws SlickException{
		super(owner, null, 0, AnimationState.SpecialAttack2, BlockLevel.Middle, 25, 450, 600, 99999, 99999, 99999, new Rectangle(50,0,80,80), new Vector2f(0f, 0f), new Vector2f(0.64f, -0.7f));
		
		anims[0] = new Animation(false);
		anims[1] = new Animation(false);
		SpriteSheet[] sheets = new SpriteSheet[]{new SpriteSheet(new Image("Resources/Characters/Lee/Attacks/YukiSlam.png"), 310, 310),
												 new SpriteSheet(new Image("Resources/Characters/Lee/Attacks/TF_YukiSlam.png"), 310, 310)};
		int fpr = Character.getFramesPerRow();
		
		for(int i=0; i<2; i++){
			for(int j=0; j<49; j++){
				anims[i].addFrame(sheets[i].getSprite(j%fpr, (int)Math.floor(j/fpr)), Character.getFrameDuration());
			}
		}
		
		anims[0].setDuration(39, Character.getFrameDuration()*8);
		anims[1].setDuration(39, Character.getFrameDuration()*8);
		
		setDurationFull(99999);
		special = true;
	}
	
	public void update(int delta){
		if(attackPhase == 0){
			if(progress > beginRunTime){
				owner.move(new Vector2f(runSpeed * delta * (facingRight ? 1 : -1), 0f));
				if(Math.abs(other.getX() - owner.getX()) < whackDistance){
					owner.setAnimationFrame(owner.getAnimation(26, true), 33);
					anims[transformedState].setCurrentFrame(33);
					whackBeginTime = progress;
					attackPhase++;
				}
				else if(anims[transformedState].getFrame() > 32){
					owner.setAnimationFrame(owner.getAnimation(26, true), 14);
					anims[transformedState].setCurrentFrame(14);
				}
				animOffsetX = animOffsetsX[transformedState][1];
			}
			else if(anims[transformedState].getFrame() > 11){
				animOffsetX = animOffsetsX[transformedState][1];
			}
		}
		else if(attackPhase == 1 && progress > whackBeginTime + whackHurtTime){
			attackPhase++;
		}
		else if(attackPhase == 2 && (hasHit || progress > whackBeginTime + whackHurtTime + whackDuration)){
			attackPhase++;
		}
		else if(attackPhase == 3 && progress > whackBeginTime + whackHurtTime + whackDuration + whackRecoverTime){
			finishAttack();
		}
		
		if(anims[transformedState].getFrame() > 37){
			animOffsetX = animOffsetsX[transformedState][2];
		}
		
		anims[transformedState].update(delta);
		super.update(delta);
	}
	
	public void lateRender(Graphics gfx){
		super.lateRender(gfx);
		anims[transformedState].draw(owner.getOutsideX(true)+animOffsetX*(facingRight?1:-1), owner.getY()+animOffsetsY[transformedState], anims[0].getWidth()*(facingRight?1:-1), anims[0].getHeight());
		
	}
	
	public boolean canDoDamage(){
		return attackPhase == 2 && progress > whackBeginTime + whackHurtTime && !hasHit && other.getHitbox().intersects(hitBox);
	}
	
	public void start(boolean facingRight){
		super.start(facingRight);
		owner.setGravityMultiplier(.4f);
		transformedState = owner.isTransformed()?1:0;
		animOffsetX = animOffsetsX[transformedState][0];
		anims[transformedState].restart();
	}
	
	public void finishAttack(){
		super.finishAttack();
		owner.setGravityMultiplier(1f);
	}
	
	public void reset(int time){
		super.reset(time);
		attackPhase = 0;
	}
}
