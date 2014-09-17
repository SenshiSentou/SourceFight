package com.sourcefight.attacks;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.sourcefight.general.Character;
import com.sourcefight.general.Character.BlockLevel;

public class Attack {
	protected int damage;
	private int durationFull;
	private int durationCancel; //can cancel/ move on after this time
	private int startTimeEffective; //do damage from t = this ...
	private int endTimeEffective; //... to t = this.
	protected Vector2f momentum;
	protected Vector2f force;
	private Rectangle hitBoxTemplate;
	protected Rectangle hitBox;
	protected boolean facingRight;
	protected boolean special = false; //special moves only
	private String combo; //used to trigger this attack; assumed lookingRight == 1 (L = back; R = forward)
	protected BlockLevel blockLevel;
	
	protected int progress;
	protected boolean hasHit;
	
	public Attack(String combo, BlockLevel blockLevel, int damage, int durationFull, int durationCancel, int startTimeEffective, int endTimeEffective, Rectangle hitBox, Vector2f momentum, Vector2f force) {
		this.combo = combo;
		this.blockLevel = blockLevel;
		this.damage = damage;
		this.durationFull = durationFull;
		this.durationCancel = durationCancel;
		this.startTimeEffective = startTimeEffective;
		this.endTimeEffective = endTimeEffective;
		this.momentum = momentum;
		this.force = force; 
		this.hitBoxTemplate = hitBox;
	}
	
	public void update(int delta, Character owner, Character other){
		progress += delta;
		
		if(progress > durationFull){
			finishAttack(owner);
		}
		else{
			if(facingRight){
				hitBox = new Rectangle(owner.getX() + hitBoxTemplate.getX(), owner.getY() + hitBoxTemplate.getY(),
							 		   hitBoxTemplate.getWidth(), hitBoxTemplate.getHeight());
			}
			else{
				hitBox = new Rectangle(owner.getX() - hitBoxTemplate.getX() - hitBoxTemplate.getWidth(),
									   owner.getY() - hitBoxTemplate.getY() - hitBoxTemplate.getHeight(),
									   hitBoxTemplate.getWidth(), hitBoxTemplate.getHeight());
			}
			
			if(canDoDamage(other)){
				Vector2f force = this.force.copy();
				if(!facingRight){
					force.x *= -1;
				}
				System.out.println("boom");
				other.takeDamage(owner.isTransformed() ? damage*2 : damage, force, blockLevel);
				hasHit = true;
			}
		}
	}
	
	public void render(Graphics gfx){
		if(hitBox != null){
			gfx.draw(hitBox);
			}
	} //override for special attacks
	
	public void finishAttack(Character owner){
		owner.finishAttack();
	}
	
	public boolean canDoDamage(Character other){
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
	
	public void start(Character owner, boolean facingRight){
		this.facingRight = facingRight;
		Vector2f momentum = this.momentum.copy();
		if(!facingRight){
			momentum.x *= -1;
		}
		if(!special){
			owner.setMomentum(momentum);
		}
	}
	
	public String getCombo(){
		return combo;
	}
	
//	public BlockLevel getBlockLevel(){
//		return blockLevel;
//	}
//	
//	public Rectangle getHitbox(){
//		return hitBox;
//	}
}