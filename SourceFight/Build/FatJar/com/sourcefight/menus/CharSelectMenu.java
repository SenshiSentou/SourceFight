package com.sourcefight.menus;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import com.sourcefight.general.Main;

public class CharSelectMenu extends Menu{
	private Image imgBtnSteve;
	private Image imgBtnJoe;
	private Image imgBtnElliott;
	private Image imgBtnTrisha;
	private Image imgBtnMeg;
	private Image imgBtnLee;
	
	private int btnTop = 20;
	private int cursorCorrection;
	
	public CharSelectMenu(Main main, int resX, int resY, Menu parentMenu) throws SlickException{
		super(main, resX, resY, parentMenu, false);
		//enterConfirm = false;
		
		imgBtnSteve = new Image("Resources/Portraits/cs_steve.png");
		imgBtnJoe = new Image("Resources/Portraits/cs_joe.png");
		imgBtnElliott = new Image("Resources/Portraits/cs_elliott.png");
		imgBtnTrisha = new Image("Resources/Portraits/cs_trisha.png");
		imgBtnMeg = new Image("Resources/Portraits/cs_meg.png");
		imgBtnLee = new Image("Resources/Portraits/cs_lee.png");
		
		imgCursor[0] = new Image("Resources/GUI/cs_cursor.png");
		imgCursor[1] = imgCursor[0].copy();
		imgCursor[0].setImageColor(100, 0, 0);
		imgCursor[1].setImageColor(0, 0, 100);
		
		cursorPosCur[0] = new Vector2f(-1, 0);
		cursorPosCur[1] = new Vector2f(-1, 0);
		cursorPosMin = new Vector2f(-1, 0);
		cursorPosMax = new Vector2f(1, 1);
		
		cursorCorrection = (imgCursor[0].getWidth() - imgBtnSteve.getWidth())/2;
		btnSpacing = 10;
		setBtnVars(resX, resY, imgBtnSteve);
	}
	
	protected void selectMenuItem(GameContainer gc, int numCursor) throws SlickException{
		super.selectMenuItem(gc, numCursor);
		
		if(cursorPosCur[numCursor].y == 0){
			switch((int)cursorPosCur[numCursor].x){
				case -1:	main.setCharacter(numCursor, "Steve");	break;
				case 0:		main.setCharacter(numCursor, "Joe");	break;
				case 1:		main.setCharacter(numCursor, "Elliott");	break;
			}
		}
		else{
			switch((int)cursorPosCur[numCursor].x){
			case -1:	main.setCharacter(numCursor, "Lee");	break;
			case 0:		main.setCharacter(numCursor, "Meg");	break;
			case 1:		main.setCharacter(numCursor, "Trisha");	break;
			}
		}
		
		cursorLocked[numCursor] = true;
		if((numCursor == 0 && cursorLocked[1]) || (numCursor == 1 && cursorLocked[0])){
			Main.setActiveMenu(Main.stageSelectMenu);
		}
	}
	
	public void render(GameContainer gc, Graphics gfx) throws SlickException {
		gfx.drawImage(imgBtnJoe, placementCenter.x-btnJump.x, btnTop);
		gfx.drawImage(imgBtnSteve, placementCenter.x, btnTop);
		gfx.drawImage(imgBtnElliott, placementCenter.x+btnJump.x, btnTop);
		gfx.drawImage(imgBtnLee, placementCenter.x-btnJump.x, btnTop+btnJump.y);
		gfx.drawImage(imgBtnMeg, placementCenter.x, btnTop+btnJump.y);
		gfx.drawImage(imgBtnTrisha, placementCenter.x+btnJump.x, btnTop+btnJump.y);

		gfx.drawImage(imgCursor[1], placementCenter.x-cursorCorrection+(btnJump.x*cursorPosCur[1].x), btnTop-cursorCorrection+(btnJump.y*cursorPosCur[1].y));
		gfx.drawImage(imgCursor[0], placementCenter.x-cursorCorrection+(btnJump.x*cursorPosCur[0].x), btnTop-cursorCorrection+(btnJump.y*cursorPosCur[0].y));
	}
	
	public void activate(){
		super.activate();
		cursorLocked[0] = cursorLocked[1] = false; 
	}
}
