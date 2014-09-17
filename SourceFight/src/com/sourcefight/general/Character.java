package com.sourcefight.general;

import java.util.ArrayList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
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
	public enum BlockLevel {None, Low, Middle, High, Kick2}
	public enum AnimationState {Idle, WalkForward, WalkBackward, RunForward, RunBackward, Crouch, Jump, DoubleJump,
								BlockLow, BlockMid, BlockHigh, HitSmall, HitBig,
								Punch1, Punch2, Punch3, RunningPunch, UpperCutPunch,
								Kick1, Kick2, Kick3, RunningKick, LowKick, TurnKick,
								Transform, SpecialAttack1, SpecialAttack2, SpecialAttack3}
	enum HitStatus {None, Small, Big}
	enum PlayerState {Idle, Walk, Run, Crouch, Taunt}
	
	static private int frameDuration = 42; //1000 ms / 24fps
	static private int framesPerRow = 8;
	
	protected float x, y;
	protected float size;
	protected float walkSpeed = 0.5f;
	protected float runSpeed = 1f;
	private int cancelTimeHit;
	private int recoverTimeHit;
	protected Vector2f jumpForce = new Vector2f(0.16f, 0.8f);
	private int doubleJumpRollTime = 300; //ms
	private int bigHitRotationSpeed = 150; //ms
	private int bigHitMaxRotation = 35; //ms
	protected float specialBarFillSpeed = 0.025f;
	protected float specialBarTransformSpeed = 0.1f;
	private int comboTime = 500; //ms
	private Rectangle hitbox = new Rectangle(0, 0, 0, 0);
	protected Rectangle[] hitboxes;
	
	private int[] tw = new int[]{160, 160, 160, 245, 245, 110, 140, 220,
								 110, 110, 160, 225, 255,
								 160, 160, 160, 230, 160,
								 160, 160, 210, 230, 160, 190,
								 190, 165, 230, 235,
								 
								 200, 195, 195, 260, 260, 215, 220, 215,
								 215, 200, 185, 260, 275,
								 270, 270, 270, 270, 215,
								 235, 235, 230, 270, 240, 270,
								 215, 240, 270, 310
								 }; //test = 50
	protected int[] th = new int[]{280, 310}; //test = 100
	private int[] numFrames = new int[]{20, 23, 23, 19, 19, 1, 5, 9,
										1, 1, 1, 5, 5,
										9, 9, 9, 5, 5, //punch1 is actually 10; no biggie
										6, 6, 8, 9, 5, 11,
										7, 23, 49, 21};
	protected int[] spriteOffsetX = new int[]{4, -10, -10, 10, 10, -11, 00, 50, //4,10,10,10,10,10 //TODO: MAKE PRIVATE
											-11, -14, -31, -69, 0, //10, 8, -10
											4, 4, 4, 0, -11, //25,25,25,00,00
											5, 5, -22, 0, 10, -50,
											-45, 00, 40, 00,
											//27 = idle
											-31, -26, -26, 0, 0, -39, -16, -16,
											-39, -43, -50, -72, 0,
											-34, -34, -13, 0, -39,
											-34, -34, -34, 0, -27, -54,
											-54, 00, 00, 00};
	private int[] spriteOffsetY = new int[]{0, 3};
	private Animation[] anims;
	
	protected int hp = 100;
	protected int lookingRight = 1; //1=True, -1=False
	protected float rotation = 0f;

	protected PlayerState playerState = PlayerState.Idle;
	protected AnimationState animationState = AnimationState.Idle;
	protected BlockLevel blockLevel = BlockLevel.None;
	protected HitStatus hitStatus = HitStatus.None;
	protected boolean transformed = false;
	protected boolean transforming = false;
	protected boolean attacking = false;
	protected boolean charging = false;
	protected boolean doubleJumping = false;
	protected boolean roundOver = false;
	private int transformBeginTime = 0;
	private int transformDuration = 5000;
	private int doubleJumpStartTime = 0;
	private int doubleJumpStartDirection = 1;
	private int timeLastHit = 0;
	private int timeLastLanded = 0;
	protected boolean justLanded = false;
//	protected boolean blocking = false;
	protected Vector2f momentum = new Vector2f(0f, 0f);
	protected float gravityMultiplier = 1f;
	protected float groundHeight;
	protected float specialFill; //amount available for use
	protected float specialCharge; //amount actively charged
	protected int numSegmentsCharged;
	private Color transformTint = Color.white;
	
	protected Attack currentAttack;
	//new Attack(combo, comboTime, animationState, blockLevel, damage, durationCancelHit, durationRecoverHit, durationCancel, startTimeEffective, endTimeEffective, hitBox, momentum, force)
	protected Attack punch = new Attack(this, null, 1300, AnimationState.Punch1, BlockLevel.Middle, 3, 300, 400, 220, 120, 1000, new Rectangle(10, 125, 70, 20), new Vector2f(0f, 0f), new Vector2f(0.12f, 0f));
	protected Attack comboPunch = new Attack(this, "AAA", 0, AnimationState.Punch3, BlockLevel.Middle, 6, 450, 600, 600, 200, 1000, new Rectangle(10, 105, 65, 45), new Vector2f(0f, 0f), new Vector2f(0.28f, -0.16f));
	protected Attack runningPunch = new Attack(this, null, 0, AnimationState.RunningPunch, BlockLevel.Middle, 8, 600, 500, 9999, 150, 700, new Rectangle(50, 140, 65, 20), new Vector2f(runSpeed*0.6f, 0f), new Vector2f(0.52f, -0.32f));
	protected Attack upperCutPunch = new Attack(this, null, 0, AnimationState.UpperCutPunch, BlockLevel.Low, 8, 500, 650, 240, 120, 700, new Rectangle(10, 120, 40, 60), new Vector2f(0f, 0f), new Vector2f(0.01f, -0.6f));
	
	protected Attack kick = new Attack(this, null, 800, AnimationState.Kick1, BlockLevel.Low, 3, 200, 300, 240, 170, 1000, new Rectangle(10, 140, 73, 40), new Vector2f(0f, 0f), new Vector2f(0.12f, 0f));
	protected Attack comboKick = new Attack(this, "BBB", 0, AnimationState.Kick3, BlockLevel.High, 8, 350, 450, 1000, 250, 1000, new Rectangle(10, 80, 75, 50), new Vector2f(0f, 0f), new Vector2f(0.4f, 0f));
	protected Attack lowKick = new Attack(this, null, 0, AnimationState.LowKick, BlockLevel.Low, 5, 300, 400, 700, 150, 700, new Rectangle(10, 240, 68, 32), new Vector2f(0f, 0f), new Vector2f(0.12f, 0f));
	protected Attack runningKick = new Attack(this, null, 0, AnimationState.RunningKick, BlockLevel.Middle, 10, 450, 600, 700, 150, 700, new Rectangle(30, 195, 68, 37), new Vector2f(runSpeed*0.72f, -0.52f), new Vector2f(0.8f, -0.32f));
	protected Attack turnKick = new Attack(this, "LDRB", 0, AnimationState.TurnKick, BlockLevel.Middle, 9, 400, 550, 700, 150, 700, new Rectangle(-95, 170, 170, 60), new Vector2f(0f, 0f), new Vector2f(0.4f, -0.6f));
	
	private Attack specialAttackOne;
	private Attack specialAttackTwo;
	private Attack specialAttackThree;
	
	protected int attackDuration = 200; //ms
	ArrayList<ComboItem> combo = new ArrayList<ComboItem>();
	
	private int rAnimNum; //used in rendering; prevents creation every single frame
	protected int rTh; //TODO: MAKE PRIVATE
	
	protected Game game;
	protected Stage stage;
	protected Character other;
	
	public Character(Game game, Stage stage, String playerName, float x, float size, Character other, boolean copyAnims) throws SlickException {
		this.x = x + tw[0]*size*0.5f;
		this.y = stage.getGroundLevel(getCenterX(true)) - th[transformed?1:0]*size;
		this.size = size;
		this.game = game;
		this.stage = stage;
		this.other = other;
//		this.hitbox = new Rectangle(x, y, tw[0]*size, th*size);
		
		hitboxes = new Rectangle[]{new Rectangle(-74, 14, 124, 263), //idle
								   new Rectangle(-45, 0, 83, 266), //walkfwd
								   new Rectangle(-45, 0, 83, 266), //walkbwd
								   new Rectangle(-23, 3, 109, 265), //runfwd
								   new Rectangle(-23, 3, 109, 265), //runbwd
								   new Rectangle(-50, 53, 82, 221), //crouch
								   new Rectangle(-15, 0, 70, 280), //jump
								   new Rectangle(-82, 0, 148, 280), //doublejump
								   new Rectangle(-50, 53, 82, 221), //crouchblock
								   new Rectangle(-74, 14, 109, 263), //midblock
								   new Rectangle(-74, 14, 109, 263), //highblock
								   new Rectangle(-74, 14, 109, 263), //hitsmall
								   new Rectangle(-74, 14, 109, 263), //hitbig
								   new Rectangle(-74, 14, 124, 263), //punch1
								   new Rectangle(-74, 14, 124, 263), //punch2
								   new Rectangle(-74, 14, 124, 263), //punch3
								   new Rectangle(-23, 3, 109, 265), //runningpunch
								   new Rectangle(-50, 53, 82, 221), //uppercutpunch
								   new Rectangle(-74, 14, 124, 263), //kick1
								   new Rectangle(-74, 14, 124, 263), //kick2
								   new Rectangle(-74, 14, 124, 263), //kick3
								   new Rectangle(-23, 3, 109, 265), //runningkick
								   new Rectangle(-50, 53, 82, 221), //lowkick
								   new Rectangle(-74, 14, 124, 263), //turnkick
								   new Rectangle(-74, 14, 124, 263), //transform
								   new Rectangle(-74, 14, 124, 263), //special1
								   new Rectangle(-74, 14, 124, 263), //special2
								   new Rectangle(-74, 14, 124, 263) //special3
								   };
		if(other != null && copyAnims){
			anims = other.getAnimationsCopy();
		}
		else{
			int n = 0;
			String spriteSheetPathRegular = "Resources/Characters/"+playerName+"/Regular/";	
			String spriteSheetPathTransformed = "Resources/Characters/"+playerName+"/Transformed/";	
			SpriteSheet[] spriteSheets = new SpriteSheet[]{new SpriteSheet(new Image(spriteSheetPathRegular+"Idle.png"), tw[n++], th[0]),
														   new SpriteSheet(new Image(spriteSheetPathRegular+"Walk.png"), tw[n++], th[0]),
														   new SpriteSheet(new Image(spriteSheetPathRegular+"Walk.png"), tw[n++], th[0]),
														   new SpriteSheet(new Image(spriteSheetPathRegular+"RunFwd.png"), tw[n++], th[0]),
														   new SpriteSheet(new Image(spriteSheetPathRegular+"RunFwd.png"), tw[n++], th[0]),
													 	   new SpriteSheet(new Image(spriteSheetPathRegular+"Crouch.png"), tw[n++], th[0]),
													 	   new SpriteSheet(new Image(spriteSheetPathRegular+"Jump.png"), tw[n++], th[0]),
													 	   new SpriteSheet(new Image(spriteSheetPathRegular+"DoubleJump.png"), tw[n++], th[0]),
														   new SpriteSheet(new Image(spriteSheetPathRegular+"CrouchBlock.png"), tw[n++], th[0]),
														   new SpriteSheet(new Image(spriteSheetPathRegular+"BlockMid.png"), tw[n++], th[0]),
														   new SpriteSheet(new Image(spriteSheetPathRegular+"BlockHigh.png"), tw[n++], th[0]),
														   new SpriteSheet(new Image(spriteSheetPathRegular+"HitSmall.png"), tw[n++], th[0]),
														   new SpriteSheet(new Image(spriteSheetPathRegular+"HitBig.png"), tw[n++], th[0]),
														   new SpriteSheet(new Image(spriteSheetPathRegular+"Punch1.png"), tw[n++], th[0]),
														   new SpriteSheet(new Image(spriteSheetPathRegular+"Punch2.png"), tw[n++], th[0]),
														   new SpriteSheet(new Image(spriteSheetPathRegular+"Punch3.png"), tw[n++], th[0]),
														   new SpriteSheet(new Image(spriteSheetPathRegular+"RunningPunch.png"), tw[n++], th[0]),
														   new SpriteSheet(new Image(spriteSheetPathRegular+"UpperCutPunch.png"), tw[n++], th[0]),
														   new SpriteSheet(new Image(spriteSheetPathRegular+"Kick1.png"), tw[n++], th[0]),
														   new SpriteSheet(new Image(spriteSheetPathRegular+"Kick2.png"), tw[n++], th[0]),
														   new SpriteSheet(new Image(spriteSheetPathRegular+"Kick3.png"), tw[n++], th[0]),
														   new SpriteSheet(new Image(spriteSheetPathRegular+"RunningKick.png"), tw[n++], th[0]),
														   new SpriteSheet(new Image(spriteSheetPathRegular+"LowKick.png"), tw[n++], th[0]),
														   new SpriteSheet(new Image(spriteSheetPathRegular+"TurnKick.png"), tw[n++], th[0]),
														   new SpriteSheet(new Image(spriteSheetPathRegular+"Transform.png"), tw[n++], th[0]),
														   new SpriteSheet(new Image(spriteSheetPathRegular+"Special1.png"), tw[n++], th[0]),
														   new SpriteSheet(new Image(spriteSheetPathRegular+"Special2.png"), tw[n++], th[0]),
														   new SpriteSheet(new Image(spriteSheetPathRegular+"Special3.png"), tw[n++], th[0]),
														   new SpriteSheet(new Image(spriteSheetPathTransformed+"Idle.png"), tw[n++], th[1]),
														   new SpriteSheet(new Image(spriteSheetPathTransformed+"Walk.png"), tw[n++], th[1]),
														   new SpriteSheet(new Image(spriteSheetPathTransformed+"Walk.png"), tw[n++], th[1]),
														   new SpriteSheet(new Image(spriteSheetPathTransformed+"RunFwd.png"), tw[n++], th[1]),
														   new SpriteSheet(new Image(spriteSheetPathTransformed+"RunFwd.png"), tw[n++], th[1]),
													 	   new SpriteSheet(new Image(spriteSheetPathTransformed+"Crouch.png"), tw[n++], th[1]),
													 	   new SpriteSheet(new Image(spriteSheetPathTransformed+"Jump.png"), tw[n++], th[1]),
													 	   new SpriteSheet(new Image(spriteSheetPathTransformed+"DoubleJump.png"), tw[n++], th[1]),
														   new SpriteSheet(new Image(spriteSheetPathTransformed+"CrouchBlock.png"), tw[n++], th[1]),
														   new SpriteSheet(new Image(spriteSheetPathTransformed+"BlockMid.png"), tw[n++], th[1]),
														   new SpriteSheet(new Image(spriteSheetPathTransformed+"BlockHigh.png"), tw[n++], th[1]),
														   new SpriteSheet(new Image(spriteSheetPathTransformed+"HitSmall.png"), tw[n++], th[1]),
														   new SpriteSheet(new Image(spriteSheetPathTransformed+"HitBig.png"), tw[n++], th[1]),
														   new SpriteSheet(new Image(spriteSheetPathTransformed+"Punch1.png"), tw[n++], th[1]),
														   new SpriteSheet(new Image(spriteSheetPathTransformed+"Punch2.png"), tw[n++], th[1]),
														   new SpriteSheet(new Image(spriteSheetPathTransformed+"Punch3.png"), tw[n++], th[1]),
														   new SpriteSheet(new Image(spriteSheetPathTransformed+"RunningPunch.png"), tw[n++], th[1]),
														   new SpriteSheet(new Image(spriteSheetPathTransformed+"UpperCutPunch.png"), tw[n++], th[1]),
														   new SpriteSheet(new Image(spriteSheetPathTransformed+"Kick1.png"), tw[n++], th[1]),
														   new SpriteSheet(new Image(spriteSheetPathTransformed+"Kick2.png"), tw[n++], th[1]),
														   new SpriteSheet(new Image(spriteSheetPathTransformed+"Kick3.png"), tw[n++], th[1]),
														   new SpriteSheet(new Image(spriteSheetPathTransformed+"RunningKick.png"), tw[n++], th[1]),
														   new SpriteSheet(new Image(spriteSheetPathTransformed+"LowKick.png"), tw[n++], th[1]),
														   new SpriteSheet(new Image(spriteSheetPathTransformed+"TurnKick.png"), tw[n++], th[1]),
														   new SpriteSheet(new Image(spriteSheetPathTransformed+"Transform.png"), tw[n++], th[1]),
														   new SpriteSheet(new Image(spriteSheetPathTransformed+"Special1.png"), tw[n++], th[1]),
														   new SpriteSheet(new Image(spriteSheetPathTransformed+"Special2.png"), tw[n++], th[1]),
														   new SpriteSheet(new Image(spriteSheetPathTransformed+"Special3.png"), tw[n++], th[1]),
														   };
			
			anims = new Animation[spriteSheets.length];
			
			for(int i=0; i<anims.length; i++){
				anims[i] = new Animation(true);
				System.out.println(i+" - "+i%numFrames.length+" :: "+tw[i]+" - "+spriteSheets[i].getWidth()+" - "+numFrames[i%numFrames.length]);
				for(int j=0; j<numFrames[i%numFrames.length]; j++){
					anims[i].addFrame(spriteSheets[i].getSprite(j%framesPerRow, (int)Math.floor(j/framesPerRow)), frameDuration);
				}
			}
		}
		
		anims[7].setDuration(5, doubleJumpRollTime); //double jump
		anims[13].setDuration(2, frameDuration*15); //punch1
		anims[14].setDuration(3, frameDuration*15); //punch2
		anims[15].setDuration(6, frameDuration*15); //punch3
		anims[16].setDuration(4, frameDuration*10); //runningpunch
		anims[17].setDuration(3, frameDuration*10); //uppercutpunch
		anims[18].setDuration(3, frameDuration*8); //kick1
		anims[19].setDuration(3, frameDuration*8); //kick2
		anims[20].setDuration(3, frameDuration*3); //kick3
		anims[21].setDuration(6, frameDuration*6); //runningkick
		anims[22].setDuration(2, frameDuration*6); //lowkick
		anims[24].setDuration(6, transformDuration/2 - frameDuration*6); //transform
		
		anims[7+numFrames.length].setDuration(5, doubleJumpRollTime); //double jump
		anims[13+numFrames.length].setDuration(2, frameDuration*15); //punch1
		anims[14+numFrames.length].setDuration(3, frameDuration*15); //punch2
		anims[15+numFrames.length].setDuration(6, frameDuration*15); //punch3
		anims[16+numFrames.length].setDuration(4, frameDuration*10); //runningpunch
		anims[17+numFrames.length].setDuration(3, frameDuration*10); //uppercutpunch
		anims[18+numFrames.length].setDuration(3, frameDuration*8); //kick1
		anims[19+numFrames.length].setDuration(3, frameDuration*8); //kick2
		anims[20+numFrames.length].setDuration(3, frameDuration*3); //kick3
		anims[21+numFrames.length].setDuration(6, frameDuration*6); //runningkick
		anims[22+numFrames.length].setDuration(2, frameDuration*6); //lowkick
		anims[24+numFrames.length].setDuration(0, transformDuration/2 - frameDuration*6); //transform
		
		punch.setDurationFull(anims[13].getDuration()); //assume same length as anims[14]
		comboPunch.setDurationFull(anims[15].getDuration());
		runningPunch.setDurationFull(anims[16].getDuration());
		upperCutPunch.setDurationFull(anims[17].getDuration());
		
		kick.setDurationFull(anims[18].getDuration()); //assume same length as anims[19]
		comboKick.setDurationFull(anims[20].getDuration());
		runningKick.setDurationFull(anims[21].getDuration());
		lowKick.setDurationFull(anims[22].getDuration());
		turnKick.setDurationFull(anims[23].getDuration());
		
		anims[6].setLooping(false); //jump
		anims[7].setLooping(false); //doublejump
		
		anims[6+numFrames.length].setLooping(false); //jump
		anims[7+numFrames.length].setLooping(false); //doublejump
		
		if(playerName == "Lee"){ //No switch()... *sobs*
			specialAttackOne = new DinoClawsAttack(this);
			specialAttackTwo = new YukiSlamAttack(this);
			
			((DinoClawsAttack)specialAttackOne).setAnimationLoopable();
			anims[25].setDuration(39, frameDuration*8); //Special1
			anims[25+numFrames.length].setDuration(39, frameDuration*8); //Special1
			
			anims[26].setDuration(39, frameDuration*8);
			anims[26+numFrames.length].setDuration(39, frameDuration*8);
			
			anims[27].setDuration(10, frameDuration*24); //special3 ready mid-air
			anims[27+numFrames.length].setDuration(10, frameDuration*24); //special3 ready mid-air
			anims[27].setDuration(16, frameDuration*50); //special3 fist down
			anims[27+numFrames.length].setDuration(16, frameDuration*50); //special3 fist down
			
			specialAttackThree = new IAmYukiAttack(this);
		}
		else if(playerName == "Trisha"){
			specialAttackOne = new DinoClawsAttack(this);
			specialAttackTwo = new YukiSlamAttack(this);
			specialAttackThree = new GamerGirlAttack(this);
		}
		else{
			specialAttackOne = new DinoClawsAttack(this);
			specialAttackTwo = new YukiSlamAttack(this);
			specialAttackThree = new IAmYukiAttack(this);
		}

		anims[0].start();
	}
	
	public void postInit(Character other){
		this.other = other;
		if(other.x < x){
			lookingRight = -1;
		}
		else{
			x -= tw[0]*size;
		}
		
		for(Attack attack:Attack.getAttacks()){
			if(attack.getOwner() == this){
				attack.setOther(other);
			}
		}
	}
	
	public void update(GameContainer gc, int delta) throws SlickException {
		if(hitStatus == HitStatus.Big){
			groundHeight = stage.getGroundLevel(x) - th[transformed?1:0]*size*0.7f;
		}
		else{
			groundHeight = stage.getGroundLevel(x) - th[transformed?1:0]*size;
		}
		
//		hitbox.setX(x+hitboxes[animationState.ordinal()].getX()*lookingRight + (lookingRight * hitboxes[animationState.ordinal()].getWidth()*1.5f));
		hitbox.setWidth(hitboxes[animationState.ordinal()].getWidth()); //TODO: only on animationState change?
		hitbox.setHeight(hitboxes[animationState.ordinal()].getHeight()); //TODO: only on animationState change?
		hitbox.setX(x-(lookingRight==-1?hitbox.getWidth():0) + hitboxes[animationState.ordinal()].getX()*lookingRight);//+hitboxes[animationState.ordinal()].getX());
		hitbox.setY(y+hitboxes[animationState.ordinal()].getY());
		
		if(currentAttack != null){
			currentAttack.update(delta);
		}
		
		if(charging){
			chargeSpecial(delta);
		}
		else if(specialCharge > 0){
			specialCharge = Math.max(0f, specialCharge - 0.5f * delta);
		}
		if(transforming){
			float fac = (game.timeSinceStartup-transformBeginTime)/(float)transformDuration;
			if(fac >= 1f){
				transforming = false;
				game.setCameraShake(null);
				animationState = AnimationState.Idle;
			}
			else if(fac >= 0.5f){
				transformTint = Color.lerp(new Color(1f, 1f, 1f, 0f), Color.white, (1-fac)*2);
				if(!transformed){
					transformed = true;
					anims[AnimationState.Transform.ordinal()+numFrames.length].restart();
					rTh = 1;
					
					for(Attack attack:Attack.getAttacks()){
						if(attack.getOwner() == this){
							attack.setHitboxOffset(new Vector2f(0, th[1]-th[0]));
						}
					}
				}
			}
			else{
				transformTint = Color.lerp(new Color(1f, 1f, 1f, 0f), Color.white, fac*2);
			}
		}
	}
	
	protected void lateUpdate(int delta){
		if(x > stage.getBoundaryX() + Main.resX*0.5f){
			x = stage.getBoundaryX() + Main.resX*0.5f;
		}
		else if(x < -stage.getBoundaryX() + Main.resX*0.5f){
			x = -stage.getBoundaryX() + Main.resX*0.5f;
		}
		
		if(currentAttack == null && x > other.x && lookingRight == 1){
			lookingRight = -1;
			rotation = -rotation;
		}
		else if(currentAttack == null && x < other.x && lookingRight == -1){
			lookingRight = 1;
			rotation = -rotation;
		}
		
		if(justLanded){
			if(hitStatus == HitStatus.None && !transforming && currentAttack == null && animationState != AnimationState.Crouch){
				animationState = AnimationState.Idle;
			}
			justLanded = false;
		}
		
		if(y != groundHeight || momentum.y != 0f){
			momentum.y += game.gravity * gravityMultiplier * delta * 0.001f;
			y += momentum.y * delta;
			if(doubleJumping){
				if(doubleJumpStartDirection == 1){
					rotation = Math.min(360, (360/doubleJumpRollTime) * (game.timeSinceStartup - doubleJumpStartTime));
				}
				else{
					rotation = Math.max(0, 360 - (360/doubleJumpRollTime) * (game.timeSinceStartup - doubleJumpStartTime));
				}
			}
			
			if(y >= groundHeight){
				y = groundHeight;
				momentum.y = 0;
				doubleJumping = false;
				if(hitStatus != HitStatus.Big){
					rotation = 0;
				}
				justLanded = true;
				timeLastLanded = game.timeSinceStartup;
			}
		}
		
		if(momentum.x != 0f){
			x += momentum.x * delta;
			if(momentum.x > 0){
				momentum.x = Math.max(0f, momentum.x - (y == groundHeight ? game.frictionGround : game.frictionAir) * delta * 0.001f);
//				System.out.println(momentum.x+" - "+game.frictionGround+" - "+game.frictionAir);
			}
			else{
				momentum.x = Math.min(0f, momentum.x + (y == groundHeight ? game.frictionGround : game.frictionAir) * delta * 0.001f);
			}
		}
		
		if(hitStatus == HitStatus.Small){
			int curAnim = transformed?11+numFrames.length:11;
			if(game.timeSinceStartup > timeLastHit + recoverTimeHit){
				if(anims[curAnim].getFrame() == anims[curAnim].getFrameCount()-1){
					hitStatus = HitStatus.None;
					animationState = AnimationState.Idle;
				}
				else if(anims[curAnim].isStopped()){
					anims[curAnim].start();
				}
			}
			else if(!anims[curAnim].isStopped() && anims[curAnim].getFrame() >= 2){
				anims[curAnim].stop();
			}
		}
		else if(hitStatus == HitStatus.Big){
			int curAnim = transformed?12+numFrames.length:12;
			if(lookingRight == 1){
				rotation = Math.max(-bigHitMaxRotation, rotation - bigHitRotationSpeed*delta*0.001f);
			}
			else{
				rotation = Math.min(bigHitMaxRotation, rotation + bigHitRotationSpeed*delta*0.001f);
			}
			if(game.timeSinceStartup > timeLastLanded + recoverTimeHit && y == groundHeight){
				if(anims[curAnim].getFrame() == anims[curAnim].getFrameCount()-1){
					hitStatus = HitStatus.None;
					animationState = AnimationState.Idle;
					rotation = 0;
				}
				else if(anims[curAnim].isStopped()){
					anims[curAnim].start();
				}
			}
			else if(!anims[curAnim].isStopped() && anims[curAnim].getFrame() >= 2){
				anims[curAnim].stop();
			}
		}		
	}
	
	public void jump(boolean jumpForward, boolean showAnim, int delta){ //can be called from attacks also
		if(currentAttack != null && !currentAttack.isSpecial()){
			currentAttack = null;
		}
		
		if(y != groundHeight){ //double jump
			momentum.x = jumpForce.x * (((jumpForward && lookingRight == 1) || (!jumpForward && lookingRight == -1)) ? 1 : -1);
			x += momentum.x * delta;
			if(showAnim){
				anims[transformed?7+numFrames.length:7].restart();
				animationState = AnimationState.DoubleJump;
			}
			doubleJumpStartTime = game.timeSinceStartup;
			doubleJumpStartDirection = (((jumpForward && lookingRight == 1) || (!jumpForward && lookingRight == -1)) ? 1 : -1);
		}
		else if(showAnim){
			anims[transformed?6+numFrames.length:6].restart();
			animationState = AnimationState.Jump;
		}
		momentum.y = -jumpForce.y;
		y += momentum.y * delta;
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
				setAttack(comboPunch);
				combo.clear();
			}
			else if(chain.endsWith(comboKick.getCombo())){
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
		game.setPlayerRenderOrder(other, this);
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
		attack.start(lookingRight == 1);
		if(currentAttack == punch && attack.getAnimationState() == AnimationState.Punch1){ //exception; becomes Punch2
			animationState = AnimationState.Punch2;
		}
		else if(currentAttack == kick && attack.getAnimationState() == AnimationState.Kick1){ //exception; becomes Kick2
			animationState = AnimationState.Kick2;
		}
		else{
			animationState = attack.getAnimationState();
		}
		currentAttack = attack;
		if(transformed){
			anims[animationState.ordinal()+numFrames.length].restart();
		}
		else{
			anims[animationState.ordinal()].restart();
		}
	}
	
	public void finishAttack(){
		currentAttack = null;
		attacking = false;
		if(transformed){
			anims[numFrames.length].restart();
		}
		else{
			anims[0].restart();
		}
		animationState = playerState == PlayerState.Run ? AnimationState.RunForward : AnimationState.Idle;
	}
	
	public void takeDamage(int damage, Vector2f force, BlockLevel attackLevel, int cancelTimeHit, int recoverTimeHit){
		momentum = force.copy();
		if(blockLevel == attackLevel){
			damage = Math.round(damage/3f);
		}
		else{
			if(momentum.length() < 0.6f){
				anims[transformed?11+numFrames.length:11].restart();
				animationState = AnimationState.HitSmall;
				hitStatus = HitStatus.Small;
			}
			else{
				anims[transformed?12+numFrames.length:12].restart();
				animationState = AnimationState.HitBig;
				hitStatus = HitStatus.Big;
			}
			timeLastHit = game.timeSinceStartup;
			this.cancelTimeHit = cancelTimeHit;
			this.recoverTimeHit = recoverTimeHit;
		}
		hp = Math.max(0, hp - damage);
		if(hp == 0){
			//DIIIIIIEEEEEEEE!!! >=D
		}
		
		charging = false;
	}
	
	protected void addToCombo(char btn){
		if(combo.size() == 0 || game.timeSinceStartup < combo.get(combo.size()-1).time + getComboTime()){
			combo.add(new ComboItem(btn, game.timeSinceStartup));
			
		}
		else{
			combo.clear();
			combo.add(new ComboItem(btn, game.timeSinceStartup));
		}
	}
	
	private int getComboTime(){
		return (currentAttack != null) ? currentAttack.getComboTime() : comboTime;
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
			transforming = true;
			animationState = AnimationState.Transform;
			transformBeginTime = game.timeSinceStartup;
			game.setCameraShake(new CameraShake(new Vector2f(-50f, 50f), new Vector2f(-50f, 50f), 100,
												game.timeSinceStartup, 800, transformDuration));
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
	
	public void endRound(Character winner){
//		roundOver = true;
//		currentAttack = null;
		if(winner == this){
			//anim1
		}
		else if(winner == other){
			//anim2
		}
		else{ //tie
			//anim3
		}
	}
	
	public void render(GameContainer gc, Graphics gfx) throws SlickException {
		if(currentAttack != null && currentAttack.isSpecial()){
			currentAttack.earlyRender(gfx);
		}
		
		if(transformed){
			rAnimNum = animationState.ordinal()+numFrames.length;
		}
		else{
			rAnimNum = animationState.ordinal();
		}
		
		gfx.rotate(x, y+th[transformed?1:0]*0.5f, rotation);
		anims[rAnimNum].draw(x-(tw[rAnimNum]*size*0.5f-spriteOffsetX[rAnimNum])*lookingRight, y+spriteOffsetY[rTh],
					 		 tw[rAnimNum]*size*lookingRight, th[rTh]*size);
		if(transforming){
			anims[rAnimNum].drawFlash(x-(tw[rAnimNum]*size*0.5f-spriteOffsetX[rAnimNum])*lookingRight, y+spriteOffsetY[rTh],
						    		  tw[rAnimNum]*size*lookingRight, th[rTh]*size, transformTint);
		}
		gfx.rotate(x, y+th[transformed?1:0]*0.5f, -rotation);
		
		if(currentAttack != null && currentAttack.isSpecial()){
			currentAttack.lateRender(gfx);
		}
		
		/*Debug*/
//		gfx.draw(hitbox);
//		if(currentAttack != null && currentAttack.getHitbox() != null){
//			gfx.draw(currentAttack.getHitbox());
//		}
		
//		if(x<other.getX()){
//			gfx.setColor(Color.white);
//			gfx.drawString(animationState.toString(), 20, 30);
//			gfx.drawString("X: "+hitboxes[animationState.ordinal()].getX(), 20, 50);
//			gfx.drawString("Y: "+hitboxes[animationState.ordinal()].getY(), 20, 70);
//			gfx.drawString("W: "+hitboxes[animationState.ordinal()].getWidth(), 20, 90);
//			gfx.drawString("H: "+hitboxes[animationState.ordinal()].getHeight(), 20, 110);
//			
//		}
	}
	
	public void move(Vector2f amount){
		x += amount.x;
		y = Math.min(groundHeight, y + amount.y);
	}
	
	static public int getFrameDuration(){
		return frameDuration;
	}
	
	static public int getFramesPerRow(){
		return framesPerRow;
	}
	
	public void addSpecialFill(int amount){
		specialFill = Math.min(specialFill + amount, 100f);
	}
	
	public void setAnimationFrame(Animation animation, int index){
		animation.setCurrentFrame(index);
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
	
	public boolean isStunned(){
		if(hitStatus == HitStatus.None){
			return false;
		}
		return game.timeSinceStartup < timeLastHit + cancelTimeHit;
	}
	
	public boolean isTransformed(){
		return transformed;
	}
	
	public float getCenterX(boolean useIdle){
		return x+tw[useIdle ? 0 : animationState.ordinal()]*size*0.5f*lookingRight;
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	public float getOutsideX(boolean useIdle){
		return x-tw[useIdle ? 0 : animationState.ordinal()]*size*0.5f*lookingRight;
	}
	
	public float getWidth(){
		return tw[animationState.ordinal()]*size;
	}
	
	public float getHeight(boolean transformed){
		return th[transformed?1:0];
	}
	
	public PlayerState getPlayerState(){
		return playerState;
	}
	
	public Animation getAnimation(int index, boolean considerTransfomed){
		if(considerTransfomed && transformed){
			return anims[index+numFrames.length];
		}
		return anims[index];
	}
	
	public Animation[] getAnimationsCopy(){
		Animation[] copy = anims.clone();
		for(Animation anim:copy){
			anim = anim.copy();
		}
		return copy;
	}
}