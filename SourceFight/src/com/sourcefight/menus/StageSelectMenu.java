package com.sourcefight.menus;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import com.sourcefight.general.Main;

public class StageSelectMenu extends Menu{
	private Image imgBtnVidcon, imgBtnOffice;
	private Image characterInfoImage;
	private String characterInfoText;
	private int characterInfoTextBoxX = 100;
	private int characterInfoTextBoxY = 100;
	private int characterInfoTextBoxWidth = 300;
	private int loadGameState;
	private int cursorCorrection;
	private Vector2f[] buttonPosStatic, buttonPosCurrent;
	private float[] buttonScaleStatic, buttonScaleCurrent;
	private int btnTweenDirection = 0;
	private int currentTime, btnTweenTimeStart;
	private int btnTweenDuration = 500;
	private Vector2f cursorPosPrev = new Vector2f(0f, 0f);
	private Image[] imgBtnStatic = new Image[4];
	
	//////indices/////
	//		2		//
	//	 3	   1	//
	//		0		//
	////////////////////////////////
	//Rendering order = 2, 3, 1, 0//
	//Rendering Right = 2, 3, 1, 0//
	////////////////////////////////
	
	public StageSelectMenu(Main main, int resX, int resY, Menu parentMenu) throws SlickException{
		super(main, resX, resY, parentMenu, true);
		
		imgBtnVidcon = new Image("Resources/Stages/th_vidcon.png");
		imgBtnOffice = new Image("Resources/Stages/th_office.png");
		imgCursor[0] = new Image("Resources/GUI/ss_cursor.png");
		imgCursor[0].setImageColor(255, 255, 255);
		
		cursorPosCur[0] = new Vector2f(0, 0);
		cursorPosMin = new Vector2f(0, 0);
		cursorPosMax = new Vector2f(3, 0);
		
		cursorCorrection = (imgCursor[0].getWidth() - imgBtnVidcon.getWidth())/2;
		btnSpacing = 10;
		inverted = true;
		seamless = true;
		setBtnVars(resX, resY, imgBtnVidcon);
		
		buttonScaleStatic = new float[]{1f, 0.65f, 0.5f, 0.65f};
		buttonScaleCurrent = new float[]{1f, 0.65f, 0.5f, 0.65f};
//		buttonScaleStatic = new float[]{1f, 1f, 1f, 1f};
//		buttonScaleCurrent = new float[]{1f, 1f, 1f, 1f};
		
		
		buttonPosStatic = new Vector2f[]{new Vector2f(placementCenter.x, placementCenter.y),
										 new Vector2f(placementCenter.x + imgBtnVidcon.getWidth()*0.5f - imgBtnVidcon.getWidth()*0.5f*buttonScaleStatic[1] + imgBtnVidcon.getWidth()*.75f, placementCenter.y - imgBtnVidcon.getHeight()*.325f),
										 new Vector2f(placementCenter.x + imgBtnVidcon.getWidth()*0.5f - imgBtnVidcon.getWidth()*0.5f*buttonScaleStatic[2], placementCenter.y - imgBtnVidcon.getHeight()*.8f),
										 new Vector2f(placementCenter.x + imgBtnVidcon.getWidth()*0.5f - imgBtnVidcon.getWidth()*0.5f*buttonScaleStatic[3] - imgBtnVidcon.getWidth()*.75f, placementCenter.y - imgBtnVidcon.getHeight()*.325f)
										 };
		
		buttonPosCurrent = new Vector2f[buttonPosStatic.length];
		for(int i=0; i<buttonPosStatic.length; i++){
			buttonPosCurrent[i] = buttonPosStatic[i].copy();
		}
		
		imgBtnStatic[0] = imgBtnOffice;
		imgBtnStatic[1] = imgBtnVidcon;
		imgBtnStatic[2] = imgBtnOffice.copy();//null;
		imgBtnStatic[3] = imgBtnVidcon.copy();//null;
	}
	
	public void update(GameContainer gc, int delta, boolean acceptingInput) throws SlickException {
		currentTime += delta;
		
		super.update(gc, delta, btnTweenDirection == 0);
		
		if(btnTweenDirection != 0){
			float fac = Math.min(((currentTime - btnTweenTimeStart)/(float)btnTweenDuration), 1f);
			
			for(int i=0; i<4; i++){
				int index = (int)(cursorPosCur[0].x + i);
				int startX = (int)buttonPosStatic[(index + btnTweenDirection + 4) % 4].x; //(int)buttonPosStatic[(index + btnTweenDirection + 4) % 4].x;
				int endX = (int)buttonPosStatic[index % 4].x;
				int startY = (int)buttonPosStatic[(index + btnTweenDirection + 4) % 4].y; //(int)buttonPosStatic[(index + btnTweenDirection + 4) % 4].y;
				int endY = (int)buttonPosStatic[index % 4].y;
				
				float startScale = buttonScaleStatic[(index + btnTweenDirection + 4) % 4];
				float endScale = buttonScaleStatic[index % 4];
//				System.out.println(startScale+" - "+endScale);
				
				buttonPosCurrent[i].x = startX + fac*(endX - startX);
				buttonPosCurrent[i].y = startY + fac*(endY - startY);
				buttonScaleCurrent[i] = startScale + fac*(endScale - startScale);
				
				if(fac == 1f){
					btnTweenDirection = 0;
				}
			}
		}
		
		else if(loadGameState == 2){
			switch((int)cursorPosCur[0].x){
			case 0:		main.setStage("Office");
						break;
			case 1:		main.setStage("Vidcon");
						break;
			case 2:		main.setStage("Office");
						break;
			case 3:		main.setStage("Vidcon");
						break;
			}
			main.startGame();
		}
	}
	
	protected void selectMenuItem(GameContainer gc, int numCursor) throws SlickException{
		super.selectMenuItem(gc, numCursor);
		if(numCursor == 0){
			loadGameState = 1;
			switch((int)(Math.random()*6)){
//				default:	characterInfoText = "This is Lee's text. It shall describe the bla bla bla of the yada yada yada. On Lopem Ipsum something something.";
				default:	characterInfoText = "This is the loading screen. It needs work.";
			}
			
			int currentLineWidth = 0;
			int currentWordWidth = 0;
			int spaceWidth = Main.fontImpact.getWidth(" ");
			String[] words = characterInfoText.split(" ");
			characterInfoText = "";
			
			for(String word:words){
				currentWordWidth = Main.fontImpact.getWidth(word);
				if(currentLineWidth + currentWordWidth <= characterInfoTextBoxWidth){
					characterInfoText += word + " ";
					currentLineWidth += currentWordWidth + spaceWidth;
				}
				else{
					characterInfoText += "\n" + word + " ";
					currentLineWidth = currentWordWidth + spaceWidth;
				}
			}
		}
	}
	
	public void cursorPosUpdated(int cursorNum) {
//		System.out.println(cursorPosCur[0].x);
		if((cursorPosCur[0].x < cursorPosPrev.x && !(cursorPosPrev.x == cursorPosMax.x && cursorPosCur[0].x == cursorPosMin.x)) || (cursorPosPrev.x == cursorPosMin.x && cursorPosCur[0].x == cursorPosMax.x)){ //left
//			System.out.println("left! "+cursorPosPrev.x+" --> "+cursorPosCur[0].x);
			
			btnTweenDirection = 1;
			btnTweenTimeStart = currentTime;
			cursorPosPrev.x = cursorPosCur[0].x;
		}
		else if(cursorPosCur[0].x > cursorPosPrev.x || (cursorPosPrev.x == cursorPosMax.x && cursorPosCur[0].x == cursorPosMin.x)){ //right
//			System.out.println("right! "+cursorPosPrev.x+" --> "+cursorPosCur[0].x);
			btnTweenDirection = -1;
			btnTweenTimeStart = currentTime;
			cursorPosPrev.x = cursorPosCur[0].x;
		}
		
	}
	
	public void render(GameContainer gc, Graphics gfx) throws SlickException {
		if(loadGameState == 1){
			gfx.drawString(characterInfoText, characterInfoTextBoxX, characterInfoTextBoxY);
			loadGameState = 2;
		}
		else{
			int cur = (int)cursorPosCur[0].x;
			int a = (2-cur+4) % 4;
			int b = (3-cur+4) % 4;
			int c = (1-cur+4) % 4;
			int d = (0-cur+4) % 4;
			imgBtnStatic[a].draw(buttonPosCurrent[a].x, buttonPosCurrent[a].y, buttonScaleCurrent[a], new Color(1f, 1f, 1f, buttonScaleCurrent[a]));
			imgBtnStatic[b].draw(buttonPosCurrent[b].x, buttonPosCurrent[b].y, buttonScaleCurrent[b], new Color(1f, 1f, 1f, buttonScaleCurrent[b]));
			imgBtnStatic[c].draw(buttonPosCurrent[c].x, buttonPosCurrent[c].y, buttonScaleCurrent[c], new Color(1f, 1f, 1f, buttonScaleCurrent[c]));
			imgBtnStatic[d].draw(buttonPosCurrent[d].x, buttonPosCurrent[d].y, buttonScaleCurrent[d], new Color(1f, 1f, 1f, buttonScaleCurrent[d]));
			
			gfx.drawImage(imgCursor[0], placementCenter.x+cursorCorrection, placementCenter.y-cursorCorrection);
//			gfx.drawImage(imgCursor[0], placementCenter.x-cursorCorrection+(btnJump.x*cursorPosCur[0].x), placementCenter.y-cursorCorrection+(btnJump.y*cursorPosCur[0].y));
		}
	}
}