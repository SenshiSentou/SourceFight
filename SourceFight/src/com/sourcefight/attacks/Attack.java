package com.sourcefight.attacks;

import java.util.ArrayList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.sourcefight.general.Character;
import com.sourcefight.general.Character.AnimationState;
import com.sourcefight.general.Character.BlockLevel;

public class Attack {
	private static ArrayList<Attack> attacks = new ArrayList<Attack>(); 
	
	protected Character owner;
	protected Character other;
	protected int damage;
	protected int durationRecoverHit;
	protected int durationCancelHit;
	private int durationFull;
	private int durationCancel; //can cancel/ move on after this time
	protected int startTimeEffective; //do damage from t = this ...
	private int endTimeEffective; //... to t = this.
	protected Vector2f momentum;
	protected Vector2f force;
	private Rectangle hitBoxTemplate;
	protected Rectangle hitBox;
	protected boolean facingRight;
	protected boolean special = false; //special moves only
	private String combo; //used to trigger this attack; assumed lookingRight == 1 (L = back; R = forward)
	private int comboTime; //time given to link combo to this attack
	protected BlockLevel blockLevel;
	private AnimationState animationState;

	protected int progress;
	protected boolean hasHit;
	
	public Attack(Character owner, String combo, int comboTime, AnimationState animationState, BlockLevel blockLevel, int damage, int durationRecoverHit, int durationCancelHit, int durationCancel, int startTimeEffective, int endTimeEffective, Rectangle hitBox, Vector2f momentum, Vector2f force) {
		this.owner = owner;
		this.combo = combo;
		this.comboTime = comboTime;
		this.animationState = animationState;
		this.blockLevel = blockLevel;
		this.damage = damage;
		this.durationRecoverHit = durationRecoverHit;
		this.durationCancelHit = durationCancelHit;
		this.durationCancel = durationCancel;
		this.startTimeEffective = startTimeEffective;
		this.endTimeEffective = endTimeEffective;
		this.momentum = momentum;
		this.force = force; 
		this.hitBoxTemplate = hitBox;
		
		attacks.add(this);
	}
	
	public void update(int delta){
		progress += delta;
		
		if(progress > durationFull){
			finishAttack();
		}
		else{
			if(facingRight){
				hitBox = new Rectangle(owner.getX() + hitBoxTemplate.getX(), owner.getY() + hitBoxTemplate.getY(),
							 		   hitBoxTemplate.getWidth(), hitBoxTemplate.getHeight());
			}
			else{
				hitBox = new Rectangle(owner.getX() - hitBoxTemplate.getX() - hitBoxTemplate.getWidth(),
									   owner.getY() + hitBoxTemplate.getY(),
									   hitBoxTemplate.getWidth(), hitBoxTemplate.getHeight());
			}
			
			if(canDoDamage()){
				Vector2f force = this.force.copy();
				if(!facingRight){
					force.x *= -1;
				}
//				System.out.println("boom");
				other.takeDamage(owner.isTransformed() ? damage*2 : damage, force, blockLevel, durationCancelHit, durationRecoverHit);
				if(!isSpecial()){
					owner.addSpecialFill(8);
				}
				hasHit = true;
			}
		}
	}
	
	public void earlyRender(Graphics gfx){}
	
	public void lateRender(Graphics gfx){
		if(hitBox != null){
			gfx.draw(hitBox);
		}
	} //override for special attacks
	
	public void finishAttack(){
		owner.finishAttack();
	}
	
	public boolean canDoDamage(){
		return progress > startTimeEffective && progress < endTimeEffective && !hasHit && other.getHitbox().intersects(hitBox);
	}
	
	public boolean canCancel(){
		return progress > durationCancel;
	}
	
	public boolean isSpecial(){
		return special;
	}
	
	public void reset(int time){
		progress = 0;
		hasHit = false;
	}
	
	public void start(boolean facingRight){
		this.facingRight = facingRight;
		Vector2f momentum = this.momentum.copy();
		if(!facingRight){
			momentum.x *= -1;
		}
		if(!special){
			owner.setMomentum(momentum);
		}
	}
	
	public static ArrayList<Attack> getAttacks(){
		return attacks;
	}
	
	public void setOther(Character other){
		this.other = other;
	}
	
	public void setDurationFull(int duration){
		this.durationFull = duration;
	}
	
	public void setHitboxOffset(Vector2f offset){
		hitBoxTemplate.setX(hitBoxTemplate.getX() + offset.x);
		hitBoxTemplate.setY(hitBoxTemplate.getY() + offset.y);
	}
	
	public Character getOwner(){
		return owner;
	}
	
	public String getCombo(){
		return combo;
	}
	
	public int getComboTime(){
		return comboTime;
	}
	
	public AnimationState getAnimationState() {
		return animationState;
	}
	
//	public BlockLevel getBlockLevel(){
//		return blockLevel;
//	}
//	
	public Rectangle getHitbox(){
		return hitBox;
	}
}