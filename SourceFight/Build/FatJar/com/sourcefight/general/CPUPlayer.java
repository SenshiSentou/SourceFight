package com.sourcefight.general;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

import com.sourcefight.attacks.Attack;
import com.sourcefight.stages.Stage;


public class CPUPlayer extends Character {
	private enum AIState {Wait, Attack, Defend, Charge, Flee, Dead}
//	private enum MoveDirection {None, Forward, Back}
	private enum Action {None, Attack, Move, Charge, ExecuteCharge}
	
//	private int difficulty;
	private int aiUpdateInterval = 100;
	
//	public int aggressiveness; //0-100
	
	
	private AIState aiState = AIState.Wait;
	private ArrayList<Attack> attackWishlist = new ArrayList<Attack>();
//	private MoveDirection moveDirection = MoveDirection.None;
	private ArrayList<Action> actionQueue = new ArrayList<Action>();
	private int timeBetweenActions; //i.e. crouch->tba->punch for uppercut
	private boolean preparingAttack;
	private int targetX; //used for moving/ walking/ running/ fleeing
	
	//Charge AI
	private float executeChargeTreshold; //execute charge when actionExecutionDesire exceeds this number 
	private float actionExecutionDesire; //0-1
	private int holdOutForChargedSegment; //-1 = none/default
	private boolean otherApproachedSinceLastAIUpdate;
	
	//Flee AI
	private float startRunningThreshold;
	
	private float previousOtherX;
	
	private int timeAILastUpdated;
	private int timeAttackPreparationBegan;
	private int timeLastAttackFinished;
	
	public CPUPlayer(Game game, Stage stage, String spriteSheetPath, float x, float size, int difficulty) throws SlickException {
		super(game, stage, spriteSheetPath, x, size);
//		this.difficulty = difficulty;
	}
	
	public void update(GameContainer gc, int delta) throws SlickException {
		super.update(gc, delta);
		
		//General update
//		if((aiState == AIState.Attack || aiState == AIState.Flee) && (currentAttack == null || currentAttack.canCancel())){
//			if(playerState == PlayerState.Walk){
//				x += walkSpeed * lookingRight * delta;
//			}
//			else if(playerState == PlayerState.Run){
//				x += runSpeed * lookingRight * delta;
//			}
//		}
		
		//AI
		switch(aiState){
			case Wait:							break;
			case Attack:	aiAttack(delta);	break;
			case Defend:	aiDefend(delta);	break;
			case Charge:	aiCharge(delta);	break;
			case Flee:		aiFlee(delta);		break;
			case Dead:							break;
		}
		
		if(canMakeAIUpdate()){ 
			if(currentAttack == null || currentAttack.canCancel()){
				considerAIState();
			}
			
			timeAILastUpdated = game.timeSinceStartup;
		}
		
		previousOtherX = other.getX();
		
		//And back to general
		lateUpdate(delta);
	}
	
	public void considerAIState(){
		if(other.isCharging()){ //RESPONDER -- other is charging				R
			if(Math.abs(x - other.getX()) < 150 || (Math.abs(x - other.getX()) < 700 &&
													Math.random()*100 < other.getSpecialCharge()*1)){ //attempt to stop charge -- //TODO: *1 = difficulty multiplier
				if(aiState == AIState.Charge && charging){ //both charging
					if(100-specialCharge < 20f){
						holdOutForChargedSegment = 3;
						actionQueue.clear();
						actionQueue.add(Action.ExecuteCharge);
					}
					else if((100/3f)*2-specialCharge < 20f){
						holdOutForChargedSegment = 2;
					}
					else if((100/3f)-specialCharge < 20f){
						holdOutForChargedSegment = 1;
					}
					else{
						enterAIState(AIState.Attack);
						return;
					}
					//holdOutSegment != 0 || -1
					if(actionQueue.size() > 1 && actionQueue.get(1) != Action.ExecuteCharge){
						actionQueue.set(1, Action.ExecuteCharge);
					}
					else{
						actionQueue.add(Action.ExecuteCharge);
					}
				} //end both charging
				else if(other.getNumSegmentsCharged() == 3){
					if((other.getSpecialCharge() < 130 && Math.random()*100 < other.getSpecialCharge()*1) //TODO: *1 = difficulty multiplier
						|| other.getSpecialCharge() > 165){ //Get the F out!
						enterAIState(AIState.Flee);
					}
					else{
						enterAIState(Math.random() < 0.5f ? AIState.Attack : AIState.Flee);
					}
				}
				else{
					enterAIState(Math.random() < 0.5f ? AIState.Attack : AIState.Flee);
				}
			} //end distance < 150
		} //End other is charging
		
//		else{
//			if(aiState != AIState.Charge){ 
//				enterAIState(AIState.Charge);
//			}
//		}
	}
	
	public void aiAttack(int delta){
		if(canMakeAIUpdate() && (currentAttack == null || currentAttack.canCancel())){
			if((playerState == PlayerState.Walk && Math.abs(x - other.getX()) < 50) ||
				(playerState == PlayerState.Run && Math.abs(x - other.getX()) < 150)){ //attack!
				attack(Math.random() < .5f ? 'A' : 'B');
			}
			else if(playerState == PlayerState.Walk){ //consider stepping it up a notch
				if(Math.random() < 0.3 * 1){ //TODO: *1 = diff. mult.
					playerState = PlayerState.Run;
				}
			}
		}
	}
	
	public void aiDefend(int delta){
		
	}
	
	public void aiFlee(int delta){
		switch(actionQueue.get(0)){
			case Move:		if(canMakeAIUpdate() && playerState == PlayerState.Walk){
								if(Math.abs(x - previousOtherX) > Math.abs(x - other.getX())){ //other approached
									otherApproachedSinceLastAIUpdate = true;
								}
											
								if(otherApproachedSinceLastAIUpdate){
									actionExecutionDesire = (float)((Math.random() * 0.5f) + Math.max(0, 1 - (Math.abs(x - other.x) * 0.002))); // *0.002 == /500
									//TODO: difficulty..
									if(Math.abs(other.x - x) < 300f){
										if(other.getPlayerState() == PlayerState.Walk){
											actionExecutionDesire += 0.2f;
										}
										else if(other.getPlayerState() == PlayerState.Run){
											actionExecutionDesire += 0.4f;
										}
									}
								} //end otherApproachSinceLastAIUpdate
								otherApproachedSinceLastAIUpdate = false;
							} //end canMakeAIUpdate() && walking
							break;
			default:	break;
		}
	}
	
	public void aiCharge(int delta){
		if((actionQueue.size() == 0 || (actionQueue.size() > 0 && actionQueue.get(0) == Action.None)) && canMakeAIUpdate()){
			if(Math.abs(other.x - x) < 150 && Math.random() < 0.35){ //punch away player
				System.out.println("Punch away player before charge");
				actionQueue.clear();
				attackWishlist.clear();
				timeBetweenActions = (int)(Math.random()*400*1); //TODO: *1 = diff. mult. -- e.g. crouch->punch  
				
				if(Math.abs(other.x - x) < 80){
					if(Math.random() < 0.5){ //upperCutPunch
						prepareAttack();
						System.out.println("prepping atk for uppercut");
						playerState = PlayerState.Crouch;
						attackWishlist.add(upperCutPunch);
					}
					else{ //turnKick
						addToCombo('L');
						addToCombo('D');
						addToCombo('R');
						attack('B');
					}
					
					actionQueue.add(Action.Attack);
					actionQueue.add(Action.Charge);
				}
				else{
					if(Math.random() < 0.5){
						attackWishlist.add(runningPunch);
					}
					else{
						attackWishlist.add(runningKick);
					}
					System.out.println("Going for running attack");
					actionQueue.add(Action.Move);
					actionQueue.add(Action.Attack);
					actionQueue.add(Action.Charge);
				}
				
			}
			else if(Math.random() < 0.75){ //create some distance
				System.out.println("Get away before charge");
//				moveDirection = MoveDirection.Back;
				targetX = (int)Math.min(stage.getBoundaryX(), Math.max(-stage.getBoundaryX(), (x + Math.random() * 500) * -lookingRight));
				System.out.println("Target X = "+targetX);
				playerState = Math.random() < 0.3 * 1 ? PlayerState.Walk : PlayerState.Run; //TODO: *1 ...
				actionQueue.clear();
				actionQueue.add(Action.Move);
				actionQueue.add(Action.Charge);
			}
			else{ //just begin
				System.out.println("Begin charge");
				charging = true;
				actionQueue.clear();
				actionQueue.add(Action.Charge);
			}
		} //end (actionQueue.size() == 0 || (actionQueue.size() > 0 && actionQueue.get(0) == Action.None)) && canMakeAIUpdate()
		
		
		if(!actionQueue.isEmpty()){
			switch(actionQueue.get(0)){
				case Attack:	if(preparingAttack && game.timeSinceStartup > timeAttackPreparationBegan + timeBetweenActions){
									if(attackWishlist.get(0) == upperCutPunch){
										attack('A');
										preparingAttack = false;
										timeBetweenActions = (int)(Math.random()*400*1); //TODO: *1 = diff. mult. -- atk->charge
									}
								}
								else if(!preparingAttack && (currentAttack == null || currentAttack.canCancel()) && game.timeSinceStartup > timeLastAttackFinished + timeBetweenActions){
									actionQueue.remove(0);
								}
				System.out.println("ActionQueue[0] == Attack");
								break;
				
				case Move:		if(actionQueue.size() > 1 && actionQueue.get(1) == Action.Attack){
									if(playerState == PlayerState.Run && Math.abs(x - other.x) < 100){
										if(attackWishlist.get(0) == runningPunch){
											attack('A');
										}
										else{
											attack('B');
										}
									}
									else if(playerState == PlayerState.Walk && Math.abs(x - other.x) < 60){
										//no other attacks can be in wishlist at this point; no need to check
										attack(Math.random() < 0.5f ? 'A' : 'B');
									}
								} //end next action == attack
								else{
									if(playerState == PlayerState.Walk){
										if(targetX > x){
											x = Math.min(targetX, x + walkSpeed * delta);
										}
										else{
											x = Math.max(targetX, x - walkSpeed * delta);
										}
									}
									else if(playerState == PlayerState.Run){
										if(targetX > x){
											x = Math.min(targetX, x + runSpeed * delta);
										}
										else{
											x = Math.max(targetX, x - runSpeed * delta);
										}
									}
									
									if(Math.abs(targetX - x) < 1){ //Arrived! (place in previous if-else?)
										actionQueue.remove(0);
									}
								}
				System.out.println("ActionQueue[0] == Move");
								break;
				
				case None:
				case Charge:	if(!charging){
									charging = true;
								}
								if(actionQueue.size() > 1 && actionQueue.get(1) == Action.ExecuteCharge){ //execute ASAP
									if(canMakeAIUpdate()){ //remove this conditional?
										System.out.println("CM_AIUpdate");
										if(actionExecutionDesire > executeChargeTreshold){
											System.out.println("Treshold ("+executeChargeTreshold+") < AED ("+actionExecutionDesire+")");
											actionQueue.remove(0); //Go to chargeExecute
										}
										else if(holdOutForChargedSegment == -1 && actionExecutionDesire > 0.65f){
											if(100-specialCharge < 7.5f){
												holdOutForChargedSegment = 3;
											}
											else if((100/3f)*2-specialCharge < 7.5f){
												holdOutForChargedSegment = 2;
											}
											else if((100/3f)-specialCharge < 7.5f){
												holdOutForChargedSegment = 1;
											}
											else{
												holdOutForChargedSegment = 0;
											}
											timeBetweenActions = (int)(Math.random()*750*1); //don't stop charging immediately  --  TODO: diff. mult.
											System.out.println("Set new holdOut to "+holdOutForChargedSegment);
										}
									} //end canMakeAIUpdate()
									else if(holdOutForChargedSegment != -1 && numSegmentsCharged >= holdOutForChargedSegment){
										System.out.println("holdout was "+holdOutForChargedSegment+" and we're gonna execute!");
										timeBetweenActions -= delta;
										if(timeBetweenActions < 1){
											actionQueue.remove(0); //Go to chargeExecute
										}
									}
								} //end next action == ExecuteCharge
								else{
									if(Math.abs(x - previousOtherX) > Math.abs(x - other.getX())){ //other approached
										otherApproachedSinceLastAIUpdate = true;
									}
									if(otherApproachedSinceLastAIUpdate){
										if(canMakeAIUpdate()){
											if(actionQueue.size() > 1 && actionQueue.get(1) != Action.ExecuteCharge){
												actionQueue.set(1, Action.ExecuteCharge);
												executeChargeTreshold = (float)Math.random();
											}
											else{
												actionQueue.add(Action.ExecuteCharge);
												executeChargeTreshold = (float)Math.random();
											}
										}
										
										actionExecutionDesire = (float)(Math.min(1, Math.max(0, (500 - Math.abs(other.x - x))/500)) * 0.5f); //0=far, 0.5=closest
										//TODO: difficulty..
										if(Math.abs(other.x - x) < 300f){
											if(other.getPlayerState() == PlayerState.Walk){
												actionExecutionDesire += 0.1f;
											}
											else if(other.getPlayerState() == PlayerState.Run){
												actionExecutionDesire += 0.2f;
											}
										}
										
										otherApproachedSinceLastAIUpdate = false;
										System.out.println("AED: "+actionExecutionDesire);
									} //end other approached since last update
								}
//				System.out.println("ActionQueue[0] == Charge");
								break;
								
				case ExecuteCharge:	if(charging){
										if(numSegmentsCharged == 0){ //charge failed, reconsider all options!
											considerAIState();
										}
										stopCharging();
									}
									break;
			}
		}
	}
	
	private void prepareAttack() {
		System.out.println("prepping atk general");
		preparingAttack = true;
		timeAttackPreparationBegan = game.timeSinceStartup;
	}
	
	public void finishAttack(){
		super.finishAttack();
		timeLastAttackFinished = game.timeSinceStartup;
	}

	public void enterAIState(AIState state){
		if(aiState != state){
			exitCurrentAIState();
			
			switch(state){
				case Attack:	playerState = Math.abs(x - other.getX()) < Math.random()*350*1 ? PlayerState.Walk : PlayerState.Run; //TODO *1 = diff. mult.
								break;
				case Defend:	break;
				case Charge:	holdOutForChargedSegment = -1;
								executeChargeTreshold = 0;
								actionExecutionDesire = 0;
								break;
				case Flee:		actionQueue.clear();
								actionQueue.add(Action.Move);
								targetX = (int)Math.min(stage.getBoundaryX(), Math.max(-stage.getBoundaryX(), (x + (200 + Math.random() * 350)) * -lookingRight));
								playerState = Math.random() < 0.3f ? PlayerState.Walk : PlayerState.Run;
								startRunningThreshold = (float)Math.random();
								break;
				default:	break;
			}
			
			aiState = state;
		}
	}
	
	public void exitCurrentAIState(){
		switch(aiState){
			case Attack:	break;
			case Defend:	break;
			case Charge:	if(charging){
								stopCharging();
							}
							break;
			default:	break;
		}
	}
	
	public boolean canMakeAIUpdate(){
		return game.timeSinceStartup > timeAILastUpdated + aiUpdateInterval;
	}
	
	public float deletemeplease(){
		return startRunningThreshold;
	}
	
}