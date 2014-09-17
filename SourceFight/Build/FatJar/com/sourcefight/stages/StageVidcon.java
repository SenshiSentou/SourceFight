package com.sourcefight.stages;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.sourcefight.general.Main;
import com.sourcefight.general.Sprite;

public class StageVidcon extends Stage {
//	Image mainScreen;
	
	public StageVidcon() throws SlickException{
		super();
		Image mainStage = new Image("Resources/Stages/VidconStage_MainStage.png");
//		mainScreen = new Image(Main.resX, Main.resY); //to be rendered at 700x420 on (stage.x+400, stage.y+102)
		
		background = new Sprite[]{new Sprite(mainStage, Main.resX/2 - mainStage.getWidth()/2, Main.resY - mainStage.getHeight() + 60), //60 = temp!!
								  };
		groundLevel = new int[][]{{-1525, 615}, //was x = -9999
								  {-307, 595},
								  {-275, 578},
								  {-230, 563},
								  {-196, 540},
								  {-124, 525}};
	}
	
	public void update(GameContainer gc, int delta) throws SlickException {
		
	}
	
	public void render(GameContainer gc, Graphics gfx) throws SlickException {
		super.render(gc, gfx);
		
	}
}


/* For main screen bg rendering:
 * Render all sprites on 2 Graphics objects: gfx and gfxScreen.
 * gfxScreen.scale(SizeDest/main.resX);
 * all.render(gc, gfxScreen);
 * all.render(gc, gfx);
 */