package com.sourcefight.attacks;
import com.sourcefight.general.Character;
import com.sourcefight.general.Main;
import com.sourcefight.general.Character.BlockLevel;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class GamerGirlAttackSymbol {
	private float x, y;
	Image image;
	private Vector2f momentum; //of the symbol; not owner
	private Vector2f force;
	private Rectangle hitBoxTemplate;
	private Rectangle hitBox;
	private int damage;
	private int durationCancelHit;
	private int durationRecoverHit;
	private int impactTime;
	private int explosionDuration = 300;
	private boolean hasHit;
	private int progress;
	private GamerGirlAttack attack;
	private BlockLevel blockLevel;
	
	public GamerGirlAttackSymbol(float x, float y, Image image, GamerGirlAttack attack, int damage, int durationCancelHit,
								 int durationRecoverHit, Rectangle hitBox, Vector2f momentum, Vector2f force, BlockLevel blockLevel) {
		this.x = x;
		this.y = y;
		this.image = image;
		this.attack = attack;
		this.damage = damage;
		this.durationCancelHit = durationCancelHit;
		this.durationRecoverHit = durationRecoverHit;
		this.hitBoxTemplate = hitBox;
		this.momentum = momentum;
		this.force = force;
		this.blockLevel = blockLevel;
	}
	
	public void update(int delta, Character other){
		progress += delta;
		x += momentum.x * delta;
		y += momentum.y * delta;
		
		if(!hasHit){
			hitBox = new Rectangle(x-hitBoxTemplate.getWidth()*0.5f, y-hitBoxTemplate.getHeight()*0.5f,
								   hitBoxTemplate.getWidth(), hitBoxTemplate.getHeight());
			if(other.getHitbox().intersects(hitBox)){
				impactTime = progress;
				hasHit = true;
				other.takeDamage(damage, force, blockLevel, durationCancelHit, durationRecoverHit);
				momentum.x = momentum.y = 0f;
				//boom!
			}
			else if(y + hitBoxTemplate.getHeight() >= Main.getGame().getStage().getGroundLevel(x)){
				impactTime = progress;
				hasHit = true;
				//also boom
			}
		}
		else if(progress > impactTime + explosionDuration){
			attack.removeActiveSymbol(this);
		}
	}
	
	public void render(Graphics gfx){
		gfx.drawImage(image, x, y);
		image.draw(x, y);
	}
}
