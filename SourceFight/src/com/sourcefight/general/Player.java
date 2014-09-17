package com.sourcefight.general;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import com.sourcefight.stages.Stage;


public class Player extends Character{
	private int runPressTime = 300; //ms
	
	private String btnPrefix;
	private int timeLastButtonPressed; //ms
	private int lastButtonPressed;
	
	private int keyLeft;
	private int keyRight;
	private int keyUp;
	private int keyDown;
	private int keyA; //atk1
	private int keyB; //atk2
	private int keyC; //block
	private int keySpecial;
	
	int debug = 0;
	
	public Player(Game game, Stage stage, int playerNum, String spriteSheetPath, float x, float size, Character other, boolean copyAnims) throws SlickException {
		super(game, stage, spriteSheetPath, x, size, other, copyAnims);
		btnPrefix = "p"+playerNum+"_";
		
		keyLeft = (int)Main.options.getNumber(btnPrefix+"left");
		keyRight = (int)Main.options.getNumber(btnPrefix+"right");
		keyUp = (int)Main.options.getNumber(btnPrefix+"up");
		keyDown = (int)Main.options.getNumber(btnPrefix+"down");
		keyA = (int)Main.options.getNumber(btnPrefix+"a");
		keyB = (int)Main.options.getNumber(btnPrefix+"b");
		keyC = (int)Main.options.getNumber(btnPrefix+"block");
		keySpecial = (int)Main.options.getNumber(btnPrefix+"special");
	}
	
	public void update(GameContainer gc, int delta) throws SlickException {
		super.update(gc, delta);
		
		if(roundOver){
			return;
		}
		
		Input input = gc.getInput();
		
//		if(attacking && timeLastButtonPressed > game.timeSinceStartup - attackDuration){
//			attacking = false;
//		}
		
		if((currentAttack == null || currentAttack.canCancel()) && !isStunned()){
			if((playerState == PlayerState.Walk || playerState == PlayerState.Run) && //State check: walk/run
				(!input.isKeyDown(keyLeft) && !input.isKeyDown(keyRight))){
				playerState = PlayerState.Idle;
				if(y == groundHeight){
					animationState = AnimationState.Idle;
				}
			}
			else if(blockLevel != BlockLevel.None && !input.isKeyDown(keyC)){ //State check: blocking && released block
				blockLevel = BlockLevel.None;
				if(input.isKeyDown(keyDown)){ //up means jump; handled later on
					animationState = AnimationState.Crouch;
				}
				else{
					animationState = AnimationState.Idle;
				}
			}
			else if(blockLevel == BlockLevel.High && !input.isKeyDown(keyUp)){ //State check: block high && released up
				if(input.isKeyDown(keyDown)){
					if(input.isKeyDown(keyC)){
						blockLevel = BlockLevel.Low;
						animationState = AnimationState.BlockLow;
					}
					else{
						blockLevel = BlockLevel.None;
						animationState = AnimationState.Crouch;
					}
				}
				else{
					if(input.isKeyDown(keyC)){
						blockLevel = BlockLevel.Middle;
						animationState = AnimationState.BlockMid;
					}
					else{
						blockLevel = BlockLevel.None;
						animationState = AnimationState.Idle;
					}
				}
				
				
			}
			
			///////////DEBUG/////////// TODO
//			if(input.isKeyPressed(Input.KEY_Q)){
//				specialFill = 100f;
//				other.specialFill = 100f;
//			}
//			if(input.isKeyPressed(Input.KEY_2)){
//				transformed = !transformed;
//				rTh = transformed?1:0;
//			}
//			if(input.isKeyPressed(Input.KEY_T)){
//				for(int s=0; s<spriteOffsetX.length-30; s++){
//					spriteOffsetX[spriteOffsetX.length-1-s] -= 1;
//				}
//				debug--;
//				System.out.println(debug);
//			}
//			if(input.isKeyPressed(Input.KEY_Y)){
//				for(int s=0; s<spriteOffsetX.length-10; s++){
//					spriteOffsetX[spriteOffsetX.length-1-s] += 1;
//				}
//				debug++;
//				System.out.println(debug);
//			}
//			
//			if(input.isKeyDown(Input.KEY_U)){
//				hitboxes[animationState.ordinal()].setY(hitboxes[animationState.ordinal()].getY() - delta*.015f);
//			}
//			if(input.isKeyDown(Input.KEY_J)){
//				hitboxes[animationState.ordinal()].setY(hitboxes[animationState.ordinal()].getY() + delta*.015f);
//			}
//			if(input.isKeyDown(Input.KEY_H)){
//				hitboxes[animationState.ordinal()].setX(hitboxes[animationState.ordinal()].getX() - delta*.015f);
//			}
//			if(input.isKeyDown(Input.KEY_K)){
//				hitboxes[animationState.ordinal()].setX(hitboxes[animationState.ordinal()].getX() + delta*.015f);
//			}
//			if(input.isKeyDown(Input.KEY_Y)){
//				hitboxes[animationState.ordinal()].setWidth(hitboxes[animationState.ordinal()].getWidth() - delta*.015f);
//			}
//			if(input.isKeyDown(Input.KEY_I)){
//				hitboxes[animationState.ordinal()].setWidth(hitboxes[animationState.ordinal()].getWidth() + delta*.015f);
//			}
//			if(input.isKeyDown(Input.KEY_O)){
//				hitboxes[animationState.ordinal()].setHeight(hitboxes[animationState.ordinal()].getHeight() - delta*.015f);
//			}
//			if(input.isKeyDown(Input.KEY_L)){
//				hitboxes[animationState.ordinal()].setHeight(hitboxes[animationState.ordinal()].getHeight() + delta*.015f);
//			}
//			
//			if(input.isMousePressed(0)){
//				System.out.println("Mouse X: "+input.getMouseX()+" - Mouse Y "+input.getMouseY());
//			}
			///////////////////////////
			
			if(input.isKeyPressed(keyA)){
				addToCombo('A');
				attack('A');
				lastButtonPressed = keyA;
				timeLastButtonPressed = game.timeSinceStartup;
			}
			else if(input.isKeyPressed(keyB)){
				addToCombo('B');
				attack('B');
				lastButtonPressed = keyB;
				timeLastButtonPressed = game.timeSinceStartup;
			}
			else if(input.isKeyPressed(keyC)){
				addToCombo('C');
				if(input.isKeyDown(keyUp)){
					blockLevel = BlockLevel.High;
					animationState = AnimationState.BlockHigh;
				}
				else if(input.isKeyDown(keyDown)){
					blockLevel = BlockLevel.Low;
					animationState = AnimationState.BlockLow;
				}
				else{
					blockLevel = BlockLevel.Middle;
					animationState = AnimationState.BlockMid;
				}
				lastButtonPressed = keyC;
				timeLastButtonPressed = game.timeSinceStartup;
			}
			
			else if(input.isKeyPressed(keyLeft)){
				if(lastButtonPressed == keyLeft && game.timeSinceStartup < timeLastButtonPressed + runPressTime){
					playerState = PlayerState.Run;
					if(lookingRight == 1 && y == groundHeight){
						animationState = AnimationState.RunBackward;
					}
					else if(y == groundHeight){
						animationState = AnimationState.RunForward;
					}
				}
				else{
					playerState = PlayerState.Walk;
					if(lookingRight == 1 && y == groundHeight){
						animationState = AnimationState.WalkBackward;
					}
					else if(y == groundHeight){
						animationState = AnimationState.WalkForward;
					}
					lastButtonPressed = keyLeft;
				}
				addToCombo('L');
				timeLastButtonPressed = game.timeSinceStartup;
			}
			
			else if(input.isKeyPressed(keyRight)){
				if(lastButtonPressed == keyRight && game.timeSinceStartup < timeLastButtonPressed + runPressTime){
					playerState = PlayerState.Run;
					if(lookingRight == -1 && y == groundHeight){
						animationState = AnimationState.RunBackward;
					}
					else if(y == groundHeight){
						animationState = AnimationState.RunForward;
					}
				}
				else{
					playerState = PlayerState.Walk;
					if(lookingRight == -1 && y == groundHeight){
						animationState = AnimationState.WalkForward;
					}
					else if(y == groundHeight){
						animationState = AnimationState.WalkBackward;
					}
					lastButtonPressed = keyRight;
				}
				addToCombo('R');
				timeLastButtonPressed = game.timeSinceStartup;
			}
			
			else if(input.isKeyPressed(keyUp)){
				if(blockLevel != BlockLevel.None){
					blockLevel = BlockLevel.High;
					animationState = AnimationState.BlockHigh;
				}
				else{
					boolean jumpForward = true;
					if((input.isKeyDown(keyLeft) && lookingRight == 1) ||
					   (input.isKeyDown(keyRight) && lookingRight == -1)){
						jumpForward = false;
					}
						
					if(y == groundHeight){ //Jump!
						jump(jumpForward, true, delta);
					}
					else if(!doubleJumping){
						jump(jumpForward, true, delta);
						doubleJumping = true;
					}
				}
				addToCombo('U');
				lastButtonPressed = keyUp;
				timeLastButtonPressed = game.timeSinceStartup;
			}
			
			else if(input.isKeyPressed(keyDown)){
				addToCombo('D');
				if(blockLevel != BlockLevel.None){
					blockLevel = BlockLevel.Low;
					animationState = AnimationState.BlockLow;
				}
				else{
					animationState = AnimationState.Crouch;
				}
				lastButtonPressed = keyDown;
				timeLastButtonPressed = game.timeSinceStartup;
			}
			
			else if(input.isKeyPressed(keySpecial)){
				charging = true;
				lastButtonPressed = keySpecial;
				timeLastButtonPressed = game.timeSinceStartup;
			}
			
			if(input.isKeyDown(keyUp) && blockLevel == BlockLevel.None && y == groundHeight){ //Jump again!
				jump(true, true, delta);
			}
			
			else if(input.isKeyDown(keyDown) && y == groundHeight){
				playerState = PlayerState.Crouch;
				if(blockLevel != BlockLevel.None){
					animationState = AnimationState.BlockLow;
				}
				else if(currentAttack != upperCutPunch && currentAttack != lowKick){
					animationState = AnimationState.Crouch;
				}
			}
			
//			else if(playerState == PlayerState.Crouch && !input.isKeyDown(keyDown)){ //State check: crouch
			else if(animationState == AnimationState.Crouch && !input.isKeyDown(keyDown)){ //State check: crouch
				playerState = PlayerState.Idle;
				if(blockLevel != BlockLevel.None){
					animationState = AnimationState.BlockMid;
				}
				else{
					animationState = AnimationState.Idle;
				}
			}
			
			if(!input.isKeyDown(keySpecial) && charging){ //released special key; use special!
				stopCharging();
			}
			
//			if(input.isKeyDown(keyC)){
//				if(input.isKeyDown(keyUp)){
//					blockLevel = BlockLevel.High;
//				}
//				else if(input.isKeyDown(keyDown)){
//					blockLevel = BlockLevel.Low;
//				}
//				else{
//					blockLevel = BlockLevel.Middle;
//				}
//			}
			
			if(input.isKeyDown(keyLeft) && !input.isKeyDown(keyRight) && !input.isKeyDown(keyDown) && !input.isKeyDown(keyC) && ((lookingRight == 1 && !game.maxZoomReached()) || lookingRight == -1 )){
				switch(playerState){
					case Walk:		x = Math.max(-stage.getBoundaryX(), x - walkSpeed*delta);
									if(justLanded){
										animationState = lookingRight == -1 ? AnimationState.WalkForward : AnimationState.WalkBackward;
										justLanded = false;
									}
									break;
					case Run:		x = Math.max(-stage.getBoundaryX(), x - runSpeed*delta);
									if(justLanded){
										animationState = lookingRight == -1 ? AnimationState.RunForward : AnimationState.RunBackward;
										justLanded = false;
									}
									break;
					default:		break;
				}
				
				lastButtonPressed = keyLeft;
			}
			else if(input.isKeyDown(keyRight) && !input.isKeyDown(keyLeft) && !input.isKeyDown(keyDown) && !input.isKeyDown(keyC) && ((lookingRight == -1 && !game.maxZoomReached()) || lookingRight == 1 )){
				switch(playerState){
					case Walk:		x = Math.min(stage.getBoundaryX() + Main.resX*0.5f, x + walkSpeed*delta);
									if(justLanded){
										animationState = lookingRight == 1 ? AnimationState.WalkForward : AnimationState.WalkBackward;
										justLanded = false;
									}
									break;
					case Run:		x = Math.min(stage.getBoundaryX() + Main.resX*0.5f, x + runSpeed*delta);
									if(justLanded){
										animationState = lookingRight == 1 ? AnimationState.RunForward : AnimationState.RunBackward;
										justLanded = false;
									}
									break;
					default:		break;
				}
				
				lastButtonPressed = keyRight;
			}
		}

		lateUpdate(delta);
	}
	
	protected void attack(char btn){
		super.attack(btn);
//		specialFill = Math.min(specialFill + 10f, 100f); //TODO: Does not belong here; only on connecting hits
		timeLastButtonPressed = game.timeSinceStartup;
	}
	
}