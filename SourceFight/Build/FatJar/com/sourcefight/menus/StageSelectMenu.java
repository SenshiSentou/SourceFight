package com.sourcefight.menus;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import com.sourcefight.general.Main;

public class StageSelectMenu extends Menu{
	private Image imgBtnVidcon;
	private int cursorCorrection;
	
	public StageSelectMenu(Main main, int resX, int resY, Menu parentMenu) throws SlickException{
		super(main, resX, resY, parentMenu, true);
		
		imgBtnVidcon = new Image("Resources/Stages/th_vidcon.png");
		imgCursor[0] = new Image("Resources/GUI/ss_cursor.png");
		imgCursor[0].setImageColor(255, 255, 255);
		
		cursorPosCur[0] = new Vector2f(0, 0);
		cursorPosMin = new Vector2f(0, 0);
		cursorPosMax = new Vector2f(0, 0);
		
		cursorCorrection = (imgCursor[0].getWidth() - imgBtnVidcon.getWidth())/2;
		btnSpacing = 10;
		setBtnVars(resX, resY, imgBtnVidcon);
	}
	
	protected void selectMenuItem(GameContainer gc, int numCursor) throws SlickException{
		super.selectMenuItem(gc, numCursor);
		if(numCursor == 0){
			switch((int)cursorPosCur[0].x){
				case 0:		main.setStage("Vidcon");
							main.startGame();
							break;
			}
		}
	}
	
	public void render(GameContainer gc, Graphics gfx) throws SlickException {
		gfx.drawImage(imgBtnVidcon, placementCenter.x, placementCenter.y);		
		gfx.drawImage(imgCursor[0], placementCenter.x-cursorCorrection+(btnJump.x*cursorPosCur[0].x), placementCenter.y-cursorCorrection+(btnJump.y*cursorPosCur[0].y));
	}
}