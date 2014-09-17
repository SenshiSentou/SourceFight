package com.sourcefight.general;

import java.util.ArrayList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import com.sourcefight.attacks.*;
import com.sourcefight.stages.Stage;

public class Character {
	public enum BlockLevel {None, Low, Middle, High}
	enum PlayerState {Idle, Walk, Run, Crouch, Taunt}
	enum AnimationState {Idle, WalkForward, WalkBackward, RunForward, RunBackward, Crouch, Punch1, Punch2, Punch3}
	
	protected float x, y;
	protected float size;
	protected float walkSpeed = 0.5f;
	protected float runSpeed = 1f;
	protected Vector2f jumpForce = new Vector2f(0.4f, 1.5f);
	protected float specialBarFillSpeed = 0.025f;
	protected float specialBarTransformSpeed = 0.1f;
	private int comboTime = 500; //ms
	private Rectangle hitbox;
	
	private int[] tw = new int[]{160, 160, 160, 245, 245, 160, 160, 160, 160}; //test = 50
	protected int th = 280; //test = 100
	private int frameDuration = 42; //1000 ms / 24fps
	private int framesPerRow = 8;
	private int[] numFrames = new int[]{19, 22, 22, 19, 19, 22, 22, 22, 22};
	private Animation[] anims;
	
	protected int hp = 100;
	protected int lookingRight = 1; //1=True, -1=False
	protected float rotation = 0f;

	protected PlayerState playerState = PlayerState.Idle;
	protected AnimationState animationState = AnimationState.Idle;
	protected BlockLevel blockLevel = BlockLevel.None;
	protected boolean transformed = false;
	protected boolean attacking = false;
	protected boolean charging = false;
	protected boolean doubleJumping = false;
//	protected boolean blocking = false;
	protected Vector2f momentum = new Vector2f(0f, 0f);
	protected float gravityMultiplier = 1f;
	protected float groundHeight;
	protected float specialFill; //amount available for use
	protected float specialCharge; //amount actively charged
	protected int numSegmentsCharged;
	
	protected Attack currentAttack;
	//new Attack(damage, durationFull, durationCancel, startTimeEffective, endTimeEffective, hitBox, momentum, force);
	protected Attack punch = new Attack(null, BlockLevel.Middle, 3, 1200, 1000, 250, 1000, new Rectangle(50, 0, 80, 80), new Vector2f(0f, 0f), new Vector2f(0f, 0f));
	protected Attack comboPunch = new Attack("AAA", BlockLevel.Middle, 6, 1200, 1000, 250, 1000, new Rectangle(50, 0, 80, 80), new Vector2f(0f, 0f), new Vector2f(0.3f, 0f));
	protected Attack runningPunch = new Attack(null, BlockLevel.Middle, 8, 800, 700, 150, 700, new Rectangle(50, 0, 80, 80), new Vector2f(runSpeed*1.5f, 0f), new Vector2f(1.3f, -0.8f));
	protected Attack upperCutPunch = new Attack(null, BlockLevel.Low, 8, 800, 700, 150, 700, new Rectangle(50, 0, 80, 80), new Vector2f(0f, 0f), new Vector2f(0.1f, -1.5f));
	
	protected Attack kick = new Attack(null, BlockLevel.Middle, 3, 1200, 1000, 250, 1000, new Rectangle(50, 0, 80, 80), new Vector2f(0f, 0f), new Vector2f(0f, 0f));
	protected Attack comboKick = new Attack("BBB", BlockLevel.Middle, 8, 1200, 1000, 250, 1000, new Rectangle(50, 0, 80, 80), new Vector2f(0f, 0f), new Vector2f(0.3f, 0f));
	protected Attack lowKick = new Attack(null, BlockLevel.Low, 5, 800, 700, 150, 700, new Rectangle(50, 0, 80, 80), new Vector2f(0f, 0f), new Vector2f(0.3f, 0f));
	protected Attack runningKick = new Attack(null, BlockLevel.Middle, 10, 800, 700, 150, 700, new Rectangle(50, 0, 80, 80), new Vector2f(runSpeed*1.8f, -1.3f), new Vector2f(2.0f, -0.8f));
	protected Attack turnKick = new Attack("LDRB", BlockLevel.Middle, 9, 800, 700, 150, 700, new Rectangle(50, 0, 80, 80), new Vector2f(0f, 0f), new Vector2f(1.0f, -1.5f));
	
	private Attack specialAttackOne;
	private Attack specialAttackTwo;
	private Attack specialAttackThree;
	
	protected int attackDuration = 200; //ms
	ArrayList<ComboItem> combo = new ArrayList<ComboItem>();
	
	protected Game game;
	protected Stage stage;
	protected Character other;
	
	public Character(Game game, Stage stage, String playerName, float x, float size) throws SlickException {
		this.x = x + tw[0]*size*0.5f;
		this.y = stage.getGroundLevel(getCenterX()) - th*size;
		this.size = size;
		this.game = game;
		this.stage = stage;
		this.hitbox = new Rectangle(x, y, tw[0]*size, th*size);
		
		specialAttackOne = new DinoClawsAttack();
		specialAttackTwo = new YukiSlamAttack();
		specialAttackThree = new IAmYukiAttack();
//		specialAttackThree = new GamerGirlAttack();
		
		if(playerName == "Lee"){ //No switch()... *sobs*
			specialAttackOne = new DinoClawsAttack();
			specialAttackTwo = new YukiSlamAttack();
			specialAttackThree = new IAmYukiAttack();
		}
		else if(playerName == "Trisha"){
			specialAttackOne = new DinoClawsAttack();
			specialAttackTwo = new YukiSlamAttack();
			specialAttackThree = new GamerGirlAttack();
		}
		else{
			specialAttackOne = new DinoClawsAttack();
			specialAttackTwo = new YukiSlamAttack();
			specialAttackThree = new IAmYukiAttack();
		}
		
		String spriteSheetPath = "Resources/Characters/"+playerName+"/";
		SpriteSheet ssIdle = new SpriteSheet(new Image(spriteSheetPath+"Idle.png"), tw[0], th);
		SpriteSheet ssWalkFwd = new SpriteSheet(new Image(spriteSheetPath+"Walk.png"), tw[1], th);
		SpriteSheet ssWalkBwd = new SpriteSheet(new Image(spriteSheetPath+"Walk.png"), tw[2], th);
		SpriteSheet ssRunFwd = new SpriteSheet(new Image(spriteSheetPath+"RunFwd.png"), tw[3], th);
		SpriteSheet ssRunBwd = new SpriteSheet(new Image(spriteSheetPath+"RunFwd.png"), tw[4], th);
		SpriteSheet ssPunch1 = new SpriteSheet(new Image(spriteSheetPath+"Punch.png"), tw[6], th);
		
		Animation aIdle = new Animation(true);
		Animation aWalkFwd = new Animation(true);
		Animation aWalkBwd = new Animation(true);
		Animation aRunFwd = new Animation(true);
		Animation aRunBwd = new Animation(true);
		Animation aPunch1 = new Animation(true);
		
		for(int i=0; i<numFrames[0]; i++){
			aIdle.addFrame(ssIdle.getSprite(i%framesPerRow, (int)Math.floor(i/framesPerRow)), frameDuration);
		}
		for(int i=0; i<numFrames[1]; i++){
			aWalkFwd.addFrame(ssWalkFwd.getSprite(i%framesPerRow, (int)Math.floor(i/framesPerRow)), frameDuration);
		}
		for(int i=0; i<numFrames[2]; i++){
			aWalkBwd.addFrame(ssWalkBwd.getSprite(i%framesPerRow, (int)Math.floor(i/framesPerRow)), frameDuration);
		}
		for(int i=0; i<numFrames[3]; i++){
			aRunFwd.addFrame(ssRunFwd.getSprite(i%framesPerRow, (int)Math.floor(i/framesPerRow)), frameDuration);
		}
		for(int i=0; i<numFrames[4]; i++){
			aRunBwd.addFrame(ssRunBwd.getSprite(i%framesPerRow, (int)Math.floor(i/framesPerRow)), frameDuration);
		}
		for(int i=0; i<numFrames[6]; i++){
			aPunch1.addFrame(ssPunch1.getSprite(i%framesPerRow, (int)Math.floor(i/framesPerRow)), frameDuration);
		}
		
		anims = new Animation[]{aIdle, aWalkFwd, aWalkBwd, aRunFwd, aRunBwd, aPunch1};
		for(Animation anim:anims){
			anim.start();
		}
	}
	
	public void update(GameContainer gc, int delta) throws SlickException {
		groundHeight = stage.getGroundLevel(x) - th*size;
		hitbox.setX(x-tw[0]*size*0.5f);
		hitbox.setY(y);
		hitbox.setWidth(tw[0]*size);
		
		if(currentAttack != null){
			currentAttack.update(delta, this, other);
		}
		
		if(charging){
			chargeSpecial(delta);
		}
		else if(specialCharge > 0){
			specialCharge = Math.max(0f, specialCharge - 0.5f * delta);
		}
	}
	
	protected void lateUpdate(int delta){
		if(x > other.x && lookingRight == 1){
			lookingRight = -1;
		}
		else if(x < other.x && lookingRight == -1){
			lookingRight = 1;
		}
		
		if(y != groundHeight || momentum.y != 0f){
			momentum.y += game.gravity * gravityMultiplier * delta * 0.001;
			y += momentum.y;
			if(y >= groundHeight){
				y = groundHeight;
				momentum.y = 0;
				doubleJumping = false;
			}
		}
		
		if(momentum.x != 0f){
			x += momentum.x;
			if(momentum.x > 0){
				momentum.x = Math.max(0f, momentum.x - (y == groundHeight ? game.frictionGround : game.frictionAir) * delta * 0.001f);
			}
			else{
				momentum.x = Math.min(0f, momentum.x + (y == groundHeight ? game.frictionGround : game.frictionAir) * delta * 0.001f);
			}
		}
		
		if(x > stage.getBoundaryX() + Main.resX*0.5f){
			x = stage.getBoundaryX() + Main.resX*0.5f;
		}
		else if(x < -stage.getBoundaryX()){
			x = -stage.getBoundaryX();
		}
		
	}
	
	public void jump(boolean jumpForward){ //can be called from attacks also
		if(y != groundHeight){ //double jump
			momentum.x = jumpForce.x * (((jumpForward && lookingRight == 1) || (!jumpForward && lookingRight == -1)) ? 1 : -1);
			x += momentum.x;
		}
		momentum.y = -jumpForce.y;
		y += momentum.y;
	}
	
	protected void attack(char btn){
		attacking = true;
		
		if(playerState == PlayerState.Run){
			switch(btn){
				case 'A':	setAttack(runningPunch);	break;
				case 'B':	setAttack(runningKick);		break;
			}
		}
		else if(playerState == PlayerState.Crouch){
			switch(btn){
				case 'A':	setAttack(upperCutPunch);	break;
				case 'B':	setAttack(lowKick);			break;
			}
		}
		else{
			String chain = getCombo();
			
			if(chain.endsWith(comboPunch.getCombo())){
				System.out.println("AAA combo!!!");
				setAttack(comboPunch);
				combo.clear();
			}
			else if(chain.endsWith(comboKick.getCombo())){
				System.out.println("BBB combo!!!");
				setAttack(comboKick);
				combo.clear();
			}
			else if(chain.endsWith(turnKick.getCombo())){
				setAttack(turnKick);
				combo.clear();
			}
			else{
				switch(btn){
					case 'A':	setAttack(punch);	break;
					case 'B':	setAttack(kick);	break;
				}
			}
		}
	}
	
	public String getCombo(){
		String chain = "";
		
		for(ComboItem ci:combo){
			if(lookingRight == -1 && ci.key == 'L'){ //flip L to R
				chain += 'R';
			}
			else if(lookingRight == -1 && ci.key == 'R'){ //flip R to L
				chain += 'L';
			}
			else{
				chain += ci.key;
			}
		}
		
		return chain;
	}
	
	public void setAttack(Attack attack){
		attack.reset(game.timeSinceStartup);
		attack.start(this, lookingRight == 1);
		currentAttack = attack;
	}
	
	public void finishAttack(){
		currentAttack = null;
		attacking = false;
		//reset anim
	}
	
	public void takeDamage(int damage, Vector2f force, BlockLevel attackLevel){
		momentum = force.copy();
		if(blockLevel == attackLevel){
			damage = Math.round(damage/3f);
		}
		hp = Math.max(0, hp - damage);
		if(hp == 0){
			//DIIIIIIEEEEEEEE!!! >=D
		}
		
		charging = false;
	}
	
	protected void addToCombo(char btn){
		if(combo.size() == 0 || game.timeSinceStartup < combo.get(combo.size()-1).time + comboTime){
			combo.add(new ComboItem(btn, game.timeSinceStartup));
			
		}
		else{
			combo.clear();
			combo.add(new ComboItem(btn, game.timeSinceStartup));
		}
	}
	
	protected void chargeSpecial(int delta){
		numSegmentsCharged = (int)(specialCharge/(100/3f)); //(int) doubles as floor()
		specialCharge = Math.min(specialCharge + delta*(specialCharge < 100f ? specialBarFillSpeed : specialBarTransformSpeed), (specialFill == 100f ? 200 : specialFill));
		if((int)(specialCharge/(100/3f)) > numSegmentsCharged){
			numSegmentsCharged++;
		}
	}
	
	protected void stopCharging(){
		charging = false;
		//do special attack
		if(specialCharge == 200){
			System.out.println("Transform!");
			transformed = true;
			specialFill = 0;
		}
		else if(specialCharge >= 100){
			specialFill = 0;
			setAttack(specialAttackThree);
		}
		else if(specialCharge >= (100/3f)*2){
			specialFill -= (100/3f)*2;
			setAttack(specialAttackTwo);
		}
		else if(specialCharge >= (100/3f)){
			specialFill -= (100/3f);
			setAttack(specialAttackOne);
		}
	}
	
	public void render(GameContainer gc, Graphics gfx) throws SlickException {
//		anims[0].draw(x-tw*size*0.5f*lookingRight, y, tw*size*lookingRight, th*size);
		gfx.rotate(x, y, rotation);
		anims[animationState.ordinal()].draw(x-tw[animationState.ordinal()]*size*0.5f*lookingRight, y+(playerState == PlayerState.Crouch ? th*size*0.5f : 0f), tw[animationState.ordinal()]*size*lookingRight, th*size*(playerState == PlayerState.Crouch ? 0.5f : 1f));
		gfx.rotate(x, y, -rotation);
		
		if(currentAttack != null && currentAttack.isSpecial()){
			currentAttack.render(gfx);
		}
		
//		gfx.draw(hitbox);
		if(blockLevel != BlockLevel.None){
			gfx.drawRect(x-tw[animationState.ordinal()]*size*0.5f*lookingRight, y+(playerState == PlayerState.Crouch ? th*size*0.5f : 0f), tw[animationState.ordinal()]*size*lookingRight, th*size*(playerState == PlayerState.Crouch ? 0.5f : 1f));
		}
	}
	
	public void setOtherPlayer(Character other){
		this.other = other;
		if(other.x < x){
			lookingRight = -1;
		}
		else{
			x -= tw[0]*size;
		}
	}
	
	public void move(Vector2f amount){
		x += amount.x;
		y = Math.min(groundHeight, y + amount.y);
	}
	
	public void setMomentum(Vector2f momentum){
		this.momentum = momentum.copy();
	}
	
	public void setGravityMultiplier(float amount){
		gravityMultiplier = amount;
	}
	
	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}
	
	public Rectangle getHitbox(){
		return hitbox;
	}
	
	public int getHP(){
		return Math.max(0, hp);
	}
	
	public float getSpecialCharge(){
		return specialCharge;
	}
	
	public float getSpecialFill(){
		return specialFill;
	}
	
	public int getNumSegmentsCharged(){
		return numSegmentsCharged;
	}
	
	public boolean isGrounded(){
		return y == groundHeight;
	}
	
	public boolean isCharging(){
		return charging;
	}
	
	public boolean isTransformed(){
		return transformed;
	}
	
	public float getCenterX(){
		return x+tw[animationState.ordinal()]*size*0.5f*lookingRight;
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	public float getOutsideX(){
		return x-tw[animationState.ordinal()]*size*0.5f*lookingRight;
	}
	
	public float getWidth(){
		return tw[animationState.ordinal()]*size;
	}
	
	public PlayerState getPlayerState(){
		return playerState;
	}
}