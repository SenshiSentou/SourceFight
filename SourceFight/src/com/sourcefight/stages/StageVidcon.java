package com.sourcefight.stages;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.sourcefight.general.Main;
import com.sourcefight.general.Sprite;

public class StageVidcon extends Stage {
	Image mainScreen;
	
	public StageVidcon() throws SlickException{
		super();
		Image mainStage = new Image("Resources/Stages/Vidcon/MainStage.png");
		Image zenMonkey = new Image("Resources/Stages/Vidcon/ZenMonkey_220x220.png");
		Image logoPDS = new Image("Resources/Stages/Vidcon/PDS_Logo_Small.png");
		
		zenMonkey.setAlpha(0.8f);
		logoPDS.setAlpha(0.8f);
		
		mainScreen = new Image(Main.resX, Main.resY); //to be rendered at 700x420 on (stage.x+400, stage.y+102)
		
		background = new Sprite[]{new Sprite(mainStage, Main.resX/2 - mainStage.getWidth()*0.5f, Main.resY - mainStage.getHeight() + 60), //60 = temp!!
								  new Sprite(zenMonkey, Main.resX/2 - zenMonkey.getWidth()*0.5f, 140),
								  new Sprite(logoPDS, Main.resX/2 + 270, 70)
								  };
		foreground = new Sprite[]{};//new Sprite(zenMonkey, Main.resX/2 - zenMonkey.getWidth()*0.5f, 140),
//				  				  new Sprite(logoPDS, Main.resX/2 + 270, 70)
//				  				  };
		groundLevel = new int[][]{{-1525, 615}, //was x = -9999
								  {-307, 595},
								  {-275, 578},
								  {-230, 563},
								  {-196, 540},
								  {-124, 525}};
	}
	
	public void update(GameContainer gc, int delta) throws SlickException {
		
	}
	
	public void earlyRender(Graphics gfx) throws SlickException {
//		gfx.drawImage(mainScreen, background[0].getX()+400, background[0].getY()+102);
//		gfx.setColor(new Color(.85f, .92f, 1f, .1f));
//		gfx.fillRect(background[0].getX()+400, background[0].getY()+102, 700, 420);
		
		gfx.setColor(new Color(.5f, .5f, .5f, 1f));
		gfx.fillRect(background[0].getX()+400, background[0].getY()+102, 700, 420);
		super.earlyRender(gfx);
	}
	
	public void lateRender(Graphics gfx) throws SlickException {
		super.lateRender(gfx);
		gfx.pushTransform();
		gfx.resetTransform();
		gfx.scale(.1f, .1f);
		gfx.translate(0f, -800f);
		gfx.copyArea(mainScreen, 0, 0);
		gfx.popTransform();
	}
}


/* For main screen bg rendering:
 * Render all sprites on 2 Graphics objects: gfx and gfxScreen.
 * gfxScreen.scale(SizeDest/main.resX);
 * all.render(gc, gfxScreen);
 * all.render(gc, gfx);
 */