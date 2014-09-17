package com.sourcefight.stages;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.sourcefight.general.Main;
import com.sourcefight.general.Sprite;


public class Stage {
	protected Sprite[] background;
	protected int[][] groundLevel;
	
	public Stage() throws SlickException{
		Image mainStage = new Image("Resources/Stages/VidconStage_MainStage.png");
		
		background = new Sprite[]{new Sprite(mainStage, Main.resX/2 - mainStage.getWidth()/2, Main.resY - mainStage.getHeight() + 60), //60 = temp!!
								  };
	}
	
	public void update(GameContainer gc, int delta) throws SlickException {
		
	}
	
	public void render(GameContainer gc, Graphics gfx) throws SlickException {
		for(Sprite bg:background){
			bg.render(gc, gfx);
		}
	}

	public int getGroundLevel(float x) {
		if(x < 400){
			for(int i = 0; i<groundLevel.length; i++){
				if(x < groundLevel[i][0]){
					return groundLevel[i-1][1];
				}
			}
			return groundLevel[groundLevel.length-1][1];
		}
		else{ //iterate backwards and mirror values around map center (X = 400)
			for(int i = groundLevel.length-1; i>0; i--){
				if(x < 400 + (400 - groundLevel[i][0])){
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
