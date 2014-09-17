package com.sourcefight.menus;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
//import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import com.sourcefight.general.Main;

/* Defaults: 	    Movement        Action buttons
 * Player 1: 	up down left right		/ .
 * Player 2: 	w s a d     			e r
 * */

public class Menu {
	protected Main main;
	protected Menu parentMenu;
	
	protected Image[] imgCursor = new Image[2];
	protected Vector2f placementCenter;
	
	protected int btnWidth, btnHeight;
	protected int btnSpacing = 40;
	protected Vector2f btnJump; //the total amount of delta between btns
	
	protected Vector2f[] cursorPosCur = new Vector2f[2];
	protected Vector2f cursorPosMin;
	protected Vector2f cursorPosMax;
	
	protected boolean[] cursorLocked = {false, false};
	
	protected boolean enterConfirm;
	protected int[][] playerKeys;
	
	private boolean active;
	
	public Menu(Main main, int resX, int resY, Menu parentMenu, boolean enterConfirm) throws SlickException{
		this.main = main;
		this.enterConfirm = enterConfirm;
		this.parentMenu = parentMenu;
	}
	
	public void setBtnVars(int resX, int resY, Image img_button){
		btnWidth = img_button.getWidth();
		btnHeight = img_button.getHeight();
		btnJump = new Vector2f(btnWidth+btnSpacing, btnHeight+btnSpacing);
		placementCenter = new Vector2f(resX/2 - btnWidth/2, resY/2 - btnHeight/2);
	}
	
	public void update(GameContainer gc, int delta) throws SlickException {
		if(gc.getInput().isKeyPressed(Input.KEY_APOSTROPHE)){
			System.out.println("EQ");
		}
		if(active){
			Input input = gc.getInput();
			if(input.isKeyPressed(Input.KEY_ESCAPE)){
				goToParentMenu();
			}
			for(int i=0; i<2; i++){
				if(cursorPosCur[i] != null && !cursorLocked[i]){
					if(input.isKeyPressed(playerKeys[i][0]) && cursorPosCur[i].y > cursorPosMin.y){
						cursorPosCur[i].y--;
					}
					else if(input.isKeyPressed(playerKeys[i][1]) && cursorPosCur[i].y < cursorPosMax.y){
						cursorPosCur[i].y++;
					}
					else if(input.isKeyPressed(playerKeys[i][2]) && cursorPosCur[i].x > cursorPosMin.x){
						cursorPosCur[i].x--;
					}
					else if(input.isKeyPressed(playerKeys[i][3]) && cursorPosCur[i].x < cursorPosMax.x){
						cursorPosCur[i].x++;
					}
					else if(input.isKeyPressed(playerKeys[i][4])){
						selectMenuItem(gc, i);
					}
				}
				else{
					if(input.isKeyPressed(playerKeys[i][5])){
						cancelMenuItem(gc, i);
					}
				}
			}
		}
	}
	
	protected void goToParentMenu(){
		if(parentMenu != null){
			Main.setActiveMenu(parentMenu);
		}
	}
	
	public void activate(){
		if(!enterConfirm){
			playerKeys = new int[][]{{(int)Main.options.getNumber("p1_up", Input.KEY_UP),
									  (int)Main.options.getNumber("p1_down", Input.KEY_DOWN),
									  (int)Main.options.getNumber("p1_left", Input.KEY_LEFT),
									  (int)Main.options.getNumber("p1_right", Input.KEY_RIGHT),
									  (int)Main.options.getNumber("p1_a", Input.KEY_SLASH),
									  (int)Main.options.getNumber("p1_b", Input.KEY_PERIOD),
									  (int)Main.options.getNumber("p1_block", Input.KEY_RALT),
									  (int)Main.options.getNumber("p1_special", Input.KEY_RSHIFT),
									 },
									 {(int)Main.options.getNumber("p2_up", Input.KEY_W),
									  (int)Main.options.getNumber("p2_down", Input.KEY_S),
									  (int)Main.options.getNumber("p2_left", Input.KEY_A),
									  (int)Main.options.getNumber("p2_right", Input.KEY_D),
									  (int)Main.options.getNumber("p2_a", Input.KEY_E),
									  (int)Main.options.getNumber("p2_b", Input.KEY_R),
									  (int)Main.options.getNumber("p2_block", Input.KEY_F),
									  (int)Main.options.getNumber("p2_special", Input.KEY_SPACE),
									 }};
			}
			else{
				playerKeys = new int[][]{{(int)Input.KEY_UP,
										  (int)Input.KEY_DOWN,
										  (int)Input.KEY_LEFT,
										  (int)Input.KEY_RIGHT,
										  (int)Input.KEY_ENTER,
										  (int)Input.KEY_ESCAPE,
										  (int)Input.KEY_RALT,
										  (int)Input.KEY_RSHIFT,
										 },
										 {(int)Input.KEY_W,
										  (int)Input.KEY_S,
										  (int)Input.KEY_A,
										  (int)Input.KEY_D,
										  (int)Input.KEY_E,
										  (int)Input.KEY_R,
										  (int)Input.KEY_F,
										  (int)Input.KEY_SPACE,
										 }};
			}
		
		active = true;
	}
	
	public void deactivate(){
		active = false;
	}
	
	protected void selectMenuItem(GameContainer gc, int cursorNum) throws SlickException{
		gc.getInput().clearKeyPressedRecord();
	}
	protected void cancelMenuItem(GameContainer gc, int cursorNum){
		cursorLocked[cursorNum] = false;
	}
	
	public void render(GameContainer gc, Graphics gfx) throws SlickException {}
}