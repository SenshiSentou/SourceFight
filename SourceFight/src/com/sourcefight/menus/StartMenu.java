package com.sourcefight.menus;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import com.sourcefight.general.Main;

public class StartMenu extends Menu{
	private Image imgBtnPlayGame;
	private Image imgBtnSettings;
	private Image imgBtnQuit;
	
	
	public StartMenu(Main main, int resX, int resY, Menu parentMenu) throws SlickException{
		super(main, resX, resY, parentMenu, true);
		imgBtnPlayGame = new Image("Resources/GUI/btn_playgame.png");
		imgBtnSettings = new Image("Resources/GUI/btn_settings.png");
		imgBtnQuit = new Image("Resources/GUI/btn_quit.png");
		imgCursor[0] = new Image("Resources/GUI/cursor_triangle.png");
		
		cursorPosCur[0] = new Vector2f(0, -1);
		cursorPosMin = new Vector2f(0, -1);
		cursorPosMax = new Vector2f(0, 1);
		
		setBtnVars(resX, resY, imgBtnPlayGame);
	}
	
	protected void selectMenuItem(GameContainer gc, int cursorNum) throws SlickException{
		super.selectMenuItem(gc, cursorNum);
		if(cursorNum == 0){
			switch((int)cursorPosCur[0].y){
				case -1:	Main.setActiveMenu(Main.charSelectMenu);	break;
				case 0:		Main.setActiveMenu(Main.settingsMenu);		break;
				case 1:		gc.exit();									break;
			}
		}
	}
	
	public void render(GameContainer gc, Graphics gfx) throws SlickException {
		gfx.drawImage(imgBtnPlayGame, placementCenter.x, placementCenter.y-btnJump.y);
		gfx.drawImage(imgBtnSettings, placementCenter.x, placementCenter.y);
		gfx.drawImage(imgBtnQuit, placementCenter.x, placementCenter.y+btnJump.y);
		gfx.drawImage(imgCursor[0], placementCenter.x-imgCursor[0].getWidth()/2-btnSpacing, placementCenter.y+(cursorPosCur[0].y*btnJump.y));
	}
}
