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
	
	public Player(Game game, Stage stage, int playerNum, String spriteSheetPath, float x, float size) throws SlickException {
		super(game, stage, spriteSheetPath, x, size);
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
		
		Input input = gc.getInput();
		
//		if(attacking && timeLastButtonPressed > game.timeSinceStartup - attackDuration){
//			attacking = false;
//		}
		
		if(currentAttack == null || currentAttack.canCancel()){
			if((playerState == PlayerState.Walk || playerState == PlayerState.Run) && //State check: walk/run
				(!input.isKeyDown(keyLeft) && !input.isKeyDown(keyRight))){
				playerState = PlayerState.Idle;
				animationState = AnimationState.Idle;
			}
			else if(blockLevel != BlockLevel.None && !input.isKeyDown(keyC)){ //State check: block
				blockLevel = BlockLevel.None;
			}
			
			///////////DEBUG///////////
			if(input.isKeyPressed(Input.KEY_Q)){
				specialFill = 100f;
				other.specialFill = 100f;
			}
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
				lastButtonPressed = keyC;
				timeLastButtonPressed = game.timeSinceStartup;
			}
			
			else if(input.isKeyPressed(keyLeft)){
				if(lastButtonPressed == keyLeft && game.timeSinceStartup < timeLastButtonPressed + runPressTime){
					playerState = PlayerState.Run;
					if(lookingRight == 1){
						animationState = AnimationState.RunBackward;
					}
					else{
						animationState = AnimationState.RunForward;
					}
				}
				else{
					playerState = PlayerState.Walk;
					if(lookingRight == 1){
						animationState = AnimationState.WalkBackward;
					}
					else{
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
					if(lookingRight == -1){
						animationState = AnimationState.RunBackward;
					}
					else{
						animationState = AnimationState.RunForward;
					}
				}
				else{
					playerState = PlayerState.Walk;
					if(lookingRight == -1){
						animationState = AnimationState.WalkForward;
					}
					else{
						animationState = AnimationState.WalkBackward;
					}
					lastButtonPressed = keyRight;
				}
				addToCombo('R');
				timeLastButtonPressed = game.timeSinceStartup;
			}
			
			else if(input.isKeyPressed(keyUp)){
				boolean jumpForward = true;
				if((input.isKeyDown(keyLeft) && lookingRight == 1) ||
				   (input.isKeyDown(keyRight) && lookingRight == -1)){
					jumpForward = false;
				}
					
				if(y == groundHeight){ //Jump!
					jump(jumpForward);
				}
				else if(!doubleJumping){
					jump(jumpForward);
					doubleJumping = true;
				}
				addToCombo('U');
				lastButtonPressed = keyUp;
				timeLastButtonPressed = game.timeSinceStartup;
			}
			
			else if(input.isKeyPressed(keyDown)){
				addToCombo('D');
				lastButtonPressed = keyDown;
				timeLastButtonPressed = game.timeSinceStartup;
			}
			
			else if(input.isKeyPressed(keySpecial)){
				charging = true;
				lastButtonPressed = keySpecial;
				timeLastButtonPressed = game.timeSinceStartup;
			}
			
			if(input.isKeyDown(keyUp) && blockLevel == BlockLevel.None && y == groundHeight){ //Jump again!
				jump(true);
			}
			
			else if(input.isKeyDown(keyDown) && y == groundHeight){
				playerState = PlayerState.Crouch;
			}
			
			else if(playerState == PlayerState.Crouch && !input.isKeyDown(keyDown)){ //State check: crouch
				playerState = PlayerState.Idle;
			}
			
			if(!input.isKeyDown(keySpecial) && charging){ //released special key; use special!
				stopCharging();
			}
			
			if(input.isKeyDown(keyC)){
				if(input.isKeyDown(keyUp)){
					blockLevel = BlockLevel.High;
				}
				else if(input.isKeyDown(keyDown)){
					blockLevel = BlockLevel.Low;
				}
				else{
					blockLevel = BlockLevel.Middle;
				}
			}
			
			if(input.isKeyDown(keyLeft) && !input.isKeyDown(keyRight) && !input.isKeyDown(keyDown) && !input.isKeyDown(keyC) && ((lookingRight == 1 && !game.maxZoomReached()) || lookingRight == -1 )){
				switch(playerState){
					case Walk:		x = Math.max(-stage.getBoundaryX(), x - walkSpeed*delta);	break;
					case Run:		x = Math.max(-stage.getBoundaryX(), x - runSpeed*delta);	break;
					default:		break;
				}
				
				lastButtonPressed = keyLeft;
			}
			else if(input.isKeyDown(keyRight) && !input.isKeyDown(keyLeft) && !input.isKeyDown(keyDown) && !input.isKeyDown(keyC) && ((lookingRight == -1 && !game.maxZoomReached()) || lookingRight == 1 )){
				switch(playerState){
					case Walk:		x = Math.min(stage.getBoundaryX() + Main.resX*0.5f, x + walkSpeed*delta);	break;
					case Run:		x = Math.min(stage.getBoundaryX() + Main.resX*0.5f, x + runSpeed*delta);		break;
					default:		break;
				}
				
				lastButtonPressed = keyRight;
			}
		}

		lateUpdate(delta);
	}
	
	protected void attack(char btn){
		super.attack(btn);
		specialFill = Math.min(specialFill + 10f, 100f); //TODO: Does not belong here; only on connecting hits
		timeLastButtonPressed = game.timeSinceStartup;
	}
	
}