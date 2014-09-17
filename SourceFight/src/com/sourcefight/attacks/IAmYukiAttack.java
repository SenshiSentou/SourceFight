package com.sourcefight.attacks;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.sourcefight.general.Character;
import com.sourcefight.general.Character.AnimationState;
import com.sourcefight.general.Character.BlockLevel;
import com.sourcefight.general.Main;


public class IAmYukiAttack extends Attack {
	private int jumpTime = 6*Character.getFrameDuration();
	private int stopGravityTime;// = 800;
	private int crashDownTime;
	private int landedDownTime;
	private int fistDownDuration;
	private int resumePunchDistance = 350;
//	private float hitRange = 2.5f; //AoE only replace!!!!!
	private Polygon rocksHitPolygonTemplate = new Polygon(new float[]{438f, 521f, 434f, 479f, 419f, 402f, 291f, 377f, 276f, 388f,
															  270f, 416f, 236f, 442f, 221f, 430f, 219f, 469f, 180f, 434f,
															  164f, 500f, 176f, 521f});
	private Polygon rocksHitPolygon;
	private Vector2f crashForce;// = new Vector2f(0f, 1.5f);
	private int attackPhase = 0;
	private boolean drawRocks;
	private Animation yukiAnim = new Animation(true);
	private Animation[] rockAnims = new Animation[7];
	private Color yukiColor = new Color(1f, 1f, 1f, 0f);
	
	public IAmYukiAttack(Character owner) throws SlickException{
//		super(damage, durationFull, durationCancel, startTimeEffective, endTimeEffective, hitBox, clawForce, clawForce);
		super(owner, null, 0, AnimationState.SpecialAttack3, BlockLevel.Middle, 40, 800, 800, 99999, 99999, 99999, new Rectangle(10, 235, 25, 45), new Vector2f(0f, 0f), new Vector2f(1.2f, -1f));
		setDurationFull(99999);
		stopGravityTime = 9*Character.getFrameDuration() + 400;
		crashDownTime = stopGravityTime + owner.getAnimation(27, false).getDuration(10) - 400;
		fistDownDuration = owner.getAnimation(27, false).getDuration(16) - 10 * Character.getFrameDuration();
		startTimeEffective = crashDownTime;
		special = true;
		
		SpriteSheet yukiSheet = new SpriteSheet(new Image("Resources/Characters/Lee/Attacks/YukiSpirit.png"), 400, 356);
		for(int i=0; i<16; i++){
			yukiAnim.addFrame(yukiSheet.getSprite(i%8, i/8), Character.getFrameDuration());
		}
		yukiAnim.setDuration(7, owner.getAnimation(27, false).getDuration(10) - 2*Character.getFrameDuration());
		yukiAnim.setDuration(15, owner.getAnimation(27, false).getDuration(16));
		
		SpriteSheet rockSheet = new SpriteSheet(new Image("Resources/Characters/Lee/Attacks/Rocks.png"), 90, 105);
		for(int i=0; i<rockAnims.length; i++){
			rockAnims[i] = new Animation(true);
			rockAnims[i].setPingPong(true);
			rockAnims[i].setLooping(false);
			for(int j=0; j<8; j++){
				rockAnims[i].addFrame(rockSheet.getSprite(j%8, i), Character.getFrameDuration());
			}
		}
	}
	
	public void update(int delta){
		facingRight = other.getX() > owner.getX();
		super.update(delta);
		
		if(attackPhase == 0 && progress > jumpTime){
			owner.jump(true, false, delta);
			yukiAnim.restart();
			attackPhase++;
		}
		else if(attackPhase == 1){
			yukiColor = Color.lerp(new Color(1f, 1f, 1f, 0f), Color.white, Math.min((progress-jumpTime)/(stopGravityTime*0.7f-jumpTime), 1f));
			
			if(progress > stopGravityTime){
				owner.setMomentum(new Vector2f(0f, 0f));
				owner.setGravityMultiplier(0f);
				attackPhase++;
			}
		}
		else if(attackPhase == 2 && progress > crashDownTime){
//			crashForce.x = (other.x - owner.x)/1000f;
			crashForce = new Vector2f(Math.atan2(other.getY() - owner.getY(), other.getX()-10 - owner.getX()) * 180/Math.PI).normalise();
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
				Animation ownerAnim = owner.getAnimation(27, true);
				float dist = Math.min(new Vector2f(owner.getX(), owner.getY()).distance(new Vector2f(other.getX(), other.getY())),
									  Main.getGame().getStage().getGroundLevel(owner.getX()) - owner.getY());
//				System.out.println(dist);
				
				if(ownerAnim.getFrame() == 11){
					if(!ownerAnim.isStopped() && dist > resumePunchDistance){
						ownerAnim.stop();
						yukiAnim.stop();
					}
					else if(ownerAnim.isStopped() && (dist < resumePunchDistance)){
						ownerAnim.start();
						yukiAnim.start();
						yukiAnim.setCurrentFrame(yukiAnim.getFrame()+1);
					}
				}
				
				Vector2f moveAmount = crashForce.copy();
				moveAmount.x *= delta;
				moveAmount.y *= delta;
				owner.move(moveAmount);
			}
			else{
//				if(!hasHit && Math.abs(other.getX() - owner.getX()) < hitRange){ //AoE 
				if(facingRight){
					rocksHitPolygon = rocksHitPolygonTemplate;
					rocksHitPolygon.setX(owner.getX() - 123);
				}
				else{
					rocksHitPolygon = rocksHitPolygonTemplate.getFlippedCopy(true, false);
					rocksHitPolygon.setX(owner.getX() - 102);
				}
				
				rocksHitPolygon.setY(owner.getY() + 127);
				checkRocksHit();
				
				for(Animation rockAnim:rockAnims){
					rockAnim.restart();
				}
				
				landedDownTime = progress;
				drawRocks = true;
				attackPhase++;
			}
		}
		else if(attackPhase == 4){
			yukiColor = Color.lerp(Color.white, new Color(1f, 1f, 1f, 0f), Math.max((progress-landedDownTime)/280f, 0f));
			
			if(!hasHit){
				checkRocksHit();
			}
			
			if(rockAnims[0].getFrame() == 7){
				for(Animation rockAnim:rockAnims){
					rockAnim.stop();
				}
			}
			if(progress > landedDownTime + fistDownDuration){
				for(Animation rockAnim:rockAnims){
					rockAnim.start();
				}
				attackPhase++;
			}
		}
		else if(attackPhase == 5){
			if(rockAnims[0].getFrame() == 0){
				drawRocks = false;
			}
			if(owner.getAnimation(27, true).getFrame() == owner.getAnimation(27, true).getFrameCount()-1){
				finishAttack();
			}
		}
	}
	
	public void earlyRender(Graphics gfx){
		int r = facingRight ? 1 : -1;
		
		if(attackPhase > 0){
			yukiAnim.draw(owner.getX()-yukiAnim.getWidth()*0.5f*r, owner.getY()-yukiAnim.getHeight()*0.2f, yukiAnim.getWidth()*r, yukiAnim.getHeight(), yukiColor);
		}
		if(drawRocks){
			int rockWidth = rockAnims[0].getWidth();
			int rockHeight = rockAnims[0].getHeight();
			
			rockAnims[2].draw(owner.getX() + 80*r, Main.getGame().getStage().getGroundLevel(owner.getX() + 80*r) - 25 - rockHeight, rockWidth*r, rockHeight);
			rockAnims[4].draw(owner.getX() - 90*r, Main.getGame().getStage().getGroundLevel(owner.getX() - 90*r) - 10 - rockHeight, rockWidth*r, rockHeight);
			rockAnims[5].draw(owner.getX() - 30*r, Main.getGame().getStage().getGroundLevel(owner.getX() - 30*r) - 35 - rockHeight, rockWidth*r, rockHeight);
		}
	}
	
	public void lateRender(Graphics gfx){
		if(drawRocks){
			int r = facingRight ? 1 : -1;
			int rockWidth = rockAnims[0].getWidth();
			int rockHeight = rockAnims[0].getHeight();
			
			rockAnims[1].draw(owner.getX() - 130*r, Main.getGame().getStage().getGroundLevel(owner.getX() - 130*r) - 5 - rockHeight, rockWidth*r, rockHeight);
			rockAnims[0].draw(owner.getX() - 70*r, Main.getGame().getStage().getGroundLevel(owner.getX() - 70*r) + 20 - rockHeight, rockWidth*r, rockHeight);
			rockAnims[6].draw(owner.getX() + 50*r, Main.getGame().getStage().getGroundLevel(owner.getX() + 50*r) + 0 - rockHeight, rockWidth*r, rockHeight);
			rockAnims[3].draw(owner.getX() - 10*r, Main.getGame().getStage().getGroundLevel(owner.getX() - 10*r) + 30 - rockHeight, rockWidth*r, rockHeight);
			
			gfx.draw(rocksHitPolygon);
		}
	}
	
	private void checkRocksHit(){
		if(rocksHitPolygon.intersects(other.getHitbox())){
			Vector2f force = this.force.copy();
			if(!facingRight){
				force.x *= -1;
			}
			force.x *= 0.5f;
			force.y *= 0.5f;
			other.takeDamage((int)(damage*0.5f), force, blockLevel, durationCancelHit, durationRecoverHit);
		}
	}
	
	public boolean canDoDamage(){
		return super.canDoDamage() && !owner.isGrounded();
	}
	
	public void start(boolean facingRight){
		super.start(facingRight);
		owner.setGravityMultiplier(.2f);
		yukiAnim.restart();
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