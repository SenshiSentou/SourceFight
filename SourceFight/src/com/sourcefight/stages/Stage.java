package com.sourcefight.stages;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import com.sourcefight.general.Character;
import com.sourcefight.general.Main;
import com.sourcefight.general.Sprite;


public class Stage {
	protected Sprite[] background;
	protected Sprite[] foreground;
	protected int[][] groundLevel;
	
	public Stage() throws SlickException{}
	
	public void update(GameContainer gc, int delta) throws SlickException {
		
	}
	
	public void earlyRender(Graphics gfx) throws SlickException {
		for(Sprite bg:background){
			bg.render(gfx);
		}
	}
	
	public void lateRender(Graphics gfx) throws SlickException {
		for(Sprite fg:foreground){
			fg.render(gfx);
		}
	}
	
	public void endRound(Character winner){
		
	}

	public int getGroundLevel(float x) {
		if(x < Main.resX*0.5f){
			for(int i = 0; i<groundLevel.length; i++){
				if(x < groundLevel[i][0]){
					return groundLevel[i==0 ? 0 : i-1][1];
				}
			}
			return groundLevel[groundLevel.length-1][1];
		}
		else{ //iterate backwards and mirror values around stage center (X = 400)
			for(int i = groundLevel.length-1; i>0; i--){
				if(x < Main.resX*0.5f + (Main.resX*0.5f - groundLevel[i][0])){
					return groundLevel[i][1];
				}
			}
			return groundLevel[0][1];
		}
	}
	
	public int getBoundaryX(){
		return Math.abs(groundLevel[0][0]);
	}
}
