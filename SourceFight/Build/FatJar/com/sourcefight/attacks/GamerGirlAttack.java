package com.sourcefight.attacks;
import com.sourcefight.general.Character;
import com.sourcefight.general.Character.BlockLevel;

import java.util.ArrayList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class GamerGirlAttack extends Attack {
	private static int numSymbols = 4;
	private int jumpTime = 250;
	private int stopGravityTime = 850;
	private int attackBeginTime = 0;
	private int attackFinishedTime = 0;
	private int restoreDuration = 300;
	private int timeBetweenSymbols = 500;
	private float symbolSpeed = 1f;
	private static Vector2f symbolMomentum = new Vector2f(0,0);
	private static Vector2f symbolForce = new Vector2f(1,0);
	private ArrayList<GamerGirlAttackSymbol> activeSymbols = new ArrayList<GamerGirlAttackSymbol>();
	private ArrayList<GamerGirlAttackSymbol> finishedSymbols = new ArrayList<GamerGirlAttackSymbol>();
	private Image[] symbolTypes;
	
	private int currentSymbol = 0;
	private int attackPhase = 0;
	private int symbolsFinished = 0;
	
	public GamerGirlAttack() throws SlickException{ //make try-catch?
//		super(damage, durationFull, durationCancel, startTimeEffective, endTimeEffective, hitBox, clawForce, clawForce);
		super(null, BlockLevel.Middle, 10, 99999, 99999, 99999, 99999, new Rectangle(80,0,80,80), new Vector2f(0,0), symbolForce);
		//TODO: set blockLevel on execution
		special = true;
		
		symbolTypes = new Image[]{new Image("Resources/Characters/Trisha/Attacks/gamergirl_symbol_triangle.png"),
								  new Image("Resources/Characters/Trisha/Attacks/gamergirl_symbol_circle.png"),
								  new Image("Resources/Characters/Trisha/Attacks/gamergirl_symbol_cross.png"),
								  new Image("Resources/Characters/Trisha/Attacks/gamergirl_symbol_square.png"),
								  }; 
	}
	
	public void removeActiveSymbol(GamerGirlAttackSymbol symbol){
		finishedSymbols.add(symbol);
		symbolsFinished++;
		if(symbolsFinished == numSymbols){
			attackPhase++; //=3
			attackFinishedTime = progress;
		}
	}
	
	public void update(int delta, Character owner, Character other){
		super.update(delta, owner, other);
		
		if(attackPhase == 0 && progress > jumpTime){
			owner.jump(true);
			attackPhase++;
		}
		else if(attackPhase == 1 && progress > stopGravityTime){
			owner.setMomentum(new Vector2f(0f, 0f));
			owner.setGravityMultiplier(0f);
			attackBeginTime = progress;
			attackPhase++;
		}
		else if(attackPhase == 2){
			if(progress > attackBeginTime + timeBetweenSymbols*(currentSymbol+1)){
				if(currentSymbol < numSymbols){
					symbolMomentum = new Vector2f(Math.atan2(other.getY() - owner.getY(), other.getX() - owner.getX()) * 180/Math.PI).normalise();
					symbolMomentum.x *= symbolSpeed;
					symbolMomentum.y *= symbolSpeed;
					
					if((symbolMomentum.x > 0f && symbolMomentum.getTheta() < 45) || (symbolMomentum.x < 0f && symbolMomentum.getTheta() > 135)){
						blockLevel = BlockLevel.Middle;
					}
					else{
						blockLevel = BlockLevel.High;
					}
					
					activeSymbols.add(new GamerGirlAttackSymbol(owner.getX()+50, owner.getY()+50, symbolTypes[currentSymbol],
																this, damage, hitBox, symbolMomentum, symbolForce, blockLevel));
					currentSymbol++;
					
				}
			}
			for(GamerGirlAttackSymbol symbol:activeSymbols){
				symbol.update(delta, other);
			}
			if(!finishedSymbols.isEmpty()){
				activeSymbols.removeAll(finishedSymbols);
				finishedSymbols.clear();
			}
		}
		else if(attackPhase == 3){
			owner.setGravityMultiplier(1f);
			attackPhase++;
		}
		else if(attackPhase == 4){
			if(progress > attackFinishedTime + restoreDuration){
				finishAttack(owner);
			}
		}
	}
	
	public void render(Graphics gfx){
		for(GamerGirlAttackSymbol symbol:activeSymbols){
			symbol.render(gfx);
		}
	}
	
	public boolean canDoDamage(Character other){
		return super.canDoDamage(other);
	}
	
	public void start(Character owner, boolean facingRight){
		super.start(owner, facingRight);
		owner.setGravityMultiplier(.2f);
	}
	
	public void finishAttack(Character owner){
		super.finishAttack(owner);
		owner.setGravityMultiplier(1f);
	}
	
	public void reset(int time){
		super.reset(time);
		attackPhase = 0;
		currentSymbol = 0;
		symbolsFinished = 0;
	}
}
