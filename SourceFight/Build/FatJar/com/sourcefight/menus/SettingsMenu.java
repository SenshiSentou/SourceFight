package com.sourcefight.menus;

import java.io.IOException;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import com.sourcefight.general.Main;

public class SettingsMenu extends Menu implements KeyListener{
	private Image imgBtnReturn;
	private GameContainer gameContainer;
	
	private int keymapDescWidth = 120;
	private int keymapKeyWidth = 50;
	private int keymapHeight = 30;
	private int btnCenterOffset = 50;
	private int placementLeft, placementRight; //x only
	private int btnTop = 70;
	private int cursorOffset = -10; //- inset; + offset
	private float cursorScale;
	private boolean inputtingKey = false;
	private boolean keyCaught = false; //KeyListener's keyPressed() does not clear input buffer; use 1-frame delay.
	private Vector2f[] keymapDescPos;
	private Vector2f[] keymapKeyPos = new Vector2f[16];
	private int oldKey; //caching for mid-keymap cancellation
	private String[] keyNames = {"up",
								"down",
								"left",
								"right",
								"a",
								"b",
								"block",
								"special"};
	
	public SettingsMenu(Main main, int resX, int resY, GameContainer gc, Menu parentMenu) throws SlickException{
		super(main, resX, resY, parentMenu, true);
		gameContainer = gc;
		
		if(Main.options.getNumber("isInitialized", 0) != 1){ //Set up defaults
			Main.options.setNumber("p1_up", Input.KEY_UP);
			Main.options.setNumber("p1_down", Input.KEY_DOWN);
			Main.options.setNumber("p1_left", Input.KEY_LEFT);
			Main.options.setNumber("p1_right", Input.KEY_RIGHT);
			Main.options.setNumber("p1_a", Input.KEY_PERIOD);
			Main.options.setNumber("p1_b", Input.KEY_SLASH);
			Main.options.setNumber("p1_block", Input.KEY_RALT);
			Main.options.setNumber("p1_special", Input.KEY_RSHIFT);
			
			Main.options.setNumber("p2_up", Input.KEY_W);
			Main.options.setNumber("p2_down", Input.KEY_S);
			Main.options.setNumber("p2_left", Input.KEY_A);
			Main.options.setNumber("p2_right", Input.KEY_D);
			Main.options.setNumber("p2_a", Input.KEY_E);
			Main.options.setNumber("p2_b", Input.KEY_R);
			Main.options.setNumber("p2_block", Input.KEY_F);
			Main.options.setNumber("p1_special", Input.KEY_SPACE);
			
			Main.options.setNumber("isInitialized", 1);
			try {
				Main.options.save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		imgBtnReturn = new Image("Resources/GUI/btn_playgame.png");
		imgCursor[0] = new Image("Resources/GUI/cursor_triangle.png");
		
		cursorPosCur[0] = new Vector2f(0, 0);
		cursorPosMin = new Vector2f(0, 0);
		cursorPosMax = new Vector2f(1, 7);
		
		//Settings btn vars manually here due to rects being used instead of images
		btnSpacing = 20;
		btnWidth = keymapDescWidth + keymapKeyWidth;
		btnHeight = keymapHeight;
		btnJump = new Vector2f(btnWidth+btnCenterOffset, btnHeight+btnSpacing);
		placementCenter = new Vector2f(resX/2 - btnWidth/2, resY/2 - btnHeight/2);
		placementLeft = (int)(placementCenter.x - btnCenterOffset - btnWidth/2);
		placementRight = (int)(placementCenter.x + btnCenterOffset + btnWidth/2);
		
		cursorScale = (btnHeight/(float)imgCursor[0].getHeight());
		
		keymapDescPos = new Vector2f[]{
				new Vector2f(placementLeft+(btnWidth - Main.fontImpact.getWidth("Player 1"))/2, btnTop+(keymapHeight - Main.fontImpact.getHeight("Player 1"))/2-btnJump.y),
				new Vector2f(placementLeft+(keymapDescWidth - Main.fontImpact.getWidth("Up"))/2, btnTop+(keymapHeight - Main.fontImpact.getHeight("Up"))/2),
				new Vector2f(placementLeft+(keymapDescWidth - Main.fontImpact.getWidth("Down"))/2, btnTop+(keymapHeight - Main.fontImpact.getHeight("Down"))/2+btnJump.y),
				new Vector2f(placementLeft+(keymapDescWidth - Main.fontImpact.getWidth("Left"))/2, btnTop+(keymapHeight - Main.fontImpact.getHeight("Left"))/2+btnJump.y*2),
				new Vector2f(placementLeft+(keymapDescWidth - Main.fontImpact.getWidth("Right"))/2, btnTop+(keymapHeight - Main.fontImpact.getHeight("Right"))/2+btnJump.y*3),
				new Vector2f(placementLeft+(keymapDescWidth - Main.fontImpact.getWidth("Button A"))/2, btnTop+(keymapHeight - Main.fontImpact.getHeight("Button A"))/2+btnJump.y*4),
				new Vector2f(placementLeft+(keymapDescWidth - Main.fontImpact.getWidth("Button B"))/2, btnTop+(keymapHeight - Main.fontImpact.getHeight("Button B"))/2+btnJump.y*5),
				new Vector2f(placementLeft+(keymapDescWidth - Main.fontImpact.getWidth("Block"))/2, btnTop+(keymapHeight - Main.fontImpact.getHeight("Block"))/2+btnJump.y*6),
				new Vector2f(placementLeft+(keymapDescWidth - Main.fontImpact.getWidth("Special"))/2, btnTop+(keymapHeight - Main.fontImpact.getHeight("Special"))/2+btnJump.y*7)
				};
		
		setKeymapKeyPos(0, "p1_up", true);
		setKeymapKeyPos(1, "p1_down", true);
		setKeymapKeyPos(2, "p1_left", true);
		setKeymapKeyPos(3, "p1_right", true);
		setKeymapKeyPos(4, "p1_a", true);
		setKeymapKeyPos(5, "p1_b", true);
		setKeymapKeyPos(6, "p1_block", true);
		setKeymapKeyPos(7, "p1_special", true);
		
		setKeymapKeyPos(8, "p2_up", true);
		setKeymapKeyPos(9, "p2_down", true);
		setKeymapKeyPos(10, "p2_left", true);
		setKeymapKeyPos(11, "p2_right", true);
		setKeymapKeyPos(12, "p2_a", true);
		setKeymapKeyPos(13, "p2_b", true);
		setKeymapKeyPos(14, "p2_block", true);
		setKeymapKeyPos(15, "p2_special", true);
	}
	
	protected void selectMenuItem(GameContainer gc, int cursorNum){
		if(cursorNum == 0){
			String keyName = (cursorPosCur[0].x == 0 ? "p1_" : "p2_") + keyNames[(int)cursorPosCur[0].y];
			oldKey = (int)Main.options.getNumber(keyName);
			
			inputtingKey = true;
			setKeymapKeyPos((int)cursorPosCur[0].y+(cursorPosCur[0].x == 0 ? 0 : 7), "_", false);
			Main.options.setNumber(keyName, -2);
		}
	}
	
	public void update(GameContainer gc, int delta) throws SlickException {
		if(!inputtingKey){
			if(keyCaught){
				gc.getInput().clearKeyPressedRecord();
				keyCaught = false;
			}
			else{
				super.update(gc, delta);
			}
		}
	}
	
	public void render(GameContainer gc, Graphics gfx) throws SlickException {
		for(int i=0; i<16; i++){
			gfx.setColor(new Color(241, 0, 25));
			gfx.fillRect(i<8?placementLeft:placementRight, btnTop+btnJump.y*(i%8), keymapDescWidth, keymapHeight);
			gfx.setColor(new Color(20, 20, 20));
			gfx.fillRect((i<8?placementLeft:placementRight)+keymapDescWidth, btnTop+btnJump.y*(i%8), keymapKeyWidth, keymapHeight);
		}
		
		
		
		Main.fontImpact.drawString(keymapDescPos[0].x, keymapDescPos[0].y, "Player 1", Color.white);
		Main.fontImpact.drawString(keymapDescPos[1].x, keymapDescPos[1].y, "Up", Color.white);
		Main.fontImpact.drawString(keymapDescPos[2].x, keymapDescPos[2].y, "Down", Color.white);
		Main.fontImpact.drawString(keymapDescPos[3].x, keymapDescPos[3].y, "Left", Color.white);
		Main.fontImpact.drawString(keymapDescPos[4].x, keymapDescPos[4].y, "Right", Color.white);
		Main.fontImpact.drawString(keymapDescPos[5].x, keymapDescPos[5].y, "Button A", Color.white);
		Main.fontImpact.drawString(keymapDescPos[6].x, keymapDescPos[6].y, "Button B", Color.white);
		Main.fontImpact.drawString(keymapDescPos[7].x, keymapDescPos[7].y, "Block", Color.white);
		Main.fontImpact.drawString(keymapDescPos[8].x, keymapDescPos[8].y, "Special", Color.white);
		
		Main.fontImpact.drawString(btnJump.x+btnCenterOffset+keymapDescPos[0].x, keymapDescPos[0].y, "Player 2", Color.white);
		Main.fontImpact.drawString(btnJump.x+btnCenterOffset+keymapDescPos[1].x, keymapDescPos[1].y, "Up", Color.white);
		Main.fontImpact.drawString(btnJump.x+btnCenterOffset+keymapDescPos[2].x, keymapDescPos[2].y, "Down", Color.white);
		Main.fontImpact.drawString(btnJump.x+btnCenterOffset+keymapDescPos[3].x, keymapDescPos[3].y, "Left", Color.white);
		Main.fontImpact.drawString(btnJump.x+btnCenterOffset+keymapDescPos[4].x, keymapDescPos[4].y, "Right", Color.white);
		Main.fontImpact.drawString(btnJump.x+btnCenterOffset+keymapDescPos[5].x, keymapDescPos[5].y, "Button A", Color.white);
		Main.fontImpact.drawString(btnJump.x+btnCenterOffset+keymapDescPos[6].x, keymapDescPos[6].y, "Button B", Color.white);
		Main.fontImpact.drawString(btnJump.x+btnCenterOffset+keymapDescPos[7].x, keymapDescPos[7].y, "Block", Color.white);
		Main.fontImpact.drawString(btnJump.x+btnCenterOffset+keymapDescPos[8].x, keymapDescPos[8].y, "Special", Color.white);

		Main.fontImpact.drawString(keymapKeyPos[0].x, keymapKeyPos[0].y, getKeyName((int)Main.options.getNumber("p1_up")), Color.white);
		Main.fontImpact.drawString(keymapKeyPos[1].x, keymapKeyPos[1].y, getKeyName((int)Main.options.getNumber("p1_down")), Color.white);
		Main.fontImpact.drawString(keymapKeyPos[2].x, keymapKeyPos[2].y, getKeyName((int)Main.options.getNumber("p1_left")), Color.white);
		Main.fontImpact.drawString(keymapKeyPos[3].x, keymapKeyPos[3].y, getKeyName((int)Main.options.getNumber("p1_right")), Color.white);
		Main.fontImpact.drawString(keymapKeyPos[4].x, keymapKeyPos[4].y, getKeyName((int)Main.options.getNumber("p1_a")), Color.white);
		Main.fontImpact.drawString(keymapKeyPos[5].x, keymapKeyPos[5].y, getKeyName((int)Main.options.getNumber("p1_b")), Color.white);
		Main.fontImpact.drawString(keymapKeyPos[6].x, keymapKeyPos[6].y, getKeyName((int)Main.options.getNumber("p1_block")), Color.white);
		Main.fontImpact.drawString(keymapKeyPos[7].x, keymapKeyPos[7].y, getKeyName((int)Main.options.getNumber("p1_special")), Color.white);
		
		Main.fontImpact.drawString(keymapKeyPos[8].x, keymapKeyPos[8].y, getKeyName((int)Main.options.getNumber("p2_up")), Color.white);
		Main.fontImpact.drawString(keymapKeyPos[9].x, keymapKeyPos[9].y, getKeyName((int)Main.options.getNumber("p2_down")), Color.white);
		Main.fontImpact.drawString(keymapKeyPos[10].x, keymapKeyPos[10].y, getKeyName((int)Main.options.getNumber("p2_left")), Color.white);
		Main.fontImpact.drawString(keymapKeyPos[11].x, keymapKeyPos[11].y, getKeyName((int)Main.options.getNumber("p2_right")), Color.white);
		Main.fontImpact.drawString(keymapKeyPos[12].x, keymapKeyPos[12].y, getKeyName((int)Main.options.getNumber("p2_a")), Color.white);
		Main.fontImpact.drawString(keymapKeyPos[13].x, keymapKeyPos[13].y, getKeyName((int)Main.options.getNumber("p2_b")), Color.white);
		Main.fontImpact.drawString(keymapKeyPos[14].x, keymapKeyPos[14].y, getKeyName((int)Main.options.getNumber("p2_block")), Color.white);
		Main.fontImpact.drawString(keymapKeyPos[15].x, keymapKeyPos[15].y, getKeyName((int)Main.options.getNumber("p2_special")), Color.white);
		
		gfx.drawImage(imgBtnReturn, placementCenter.x, gc.getScreenHeight()-imgBtnReturn.getHeight()-30);
		
		imgCursor[0].draw(placementLeft - imgCursor[0].getWidth()*cursorScale - cursorOffset + (btnJump.x+btnCenterOffset)*cursorPosCur[0].x,
									btnTop+(cursorPosCur[0].y*btnJump.y),
									cursorScale);
	}
	
	private void setKeymapKeyPos(int index, String keyName, boolean lookupKey){ //keyName = "p1_up" for lookupKey, "_" otherwise
		String key;
		if(lookupKey){
			int keyNumber = (int)Main.options.getNumber(keyName);
			key = getKeyName(keyNumber);
		}
		else{
			key = keyName;
		}
		
		keymapKeyPos[index] = new Vector2f(placementLeft+keymapDescWidth+((index>7?1:0)*(btnJump.x+btnCenterOffset))+(keymapKeyWidth-Main.fontImpact.getWidth(key))/2,
										   btnTop+(keymapHeight-Main.fontImpact.getHeight(key))/2+btnJump.y*(index%8));
	}
	
	public String getKeyName(int keyCode){
		switch(keyCode){
			case -1:	return "";
			case -2:	return "_";
			default:	String key = Input.getKeyName(keyCode);
						if(key.equals("PERIOD")){
							key = ".";
						}
						else if(key.equals("COMMA")){
							key = ",";
						}
						else if(key.equals("SLASH")){
							key = "/";
						}
						else if(key.equals("BACKSLASH")){
							key = "\\";
						}
						else if(key.equals("MINUS")){
							key = "-";
						}
						else if(key.equals("EQUALS")){
							key = "=";
						}
						else if(key.equals("APOSTROPHE")){
							key = "'";
						}
						else if(key.equals("GRAVE")){
							key = "`";
						}
						else if(key.equals("LWIN")){
							key = "`";
						}
						else if(key.equals("RWIN")){
							key = "`";
						}
						else if(key.equals("LMENU")){
							key = "L.ALT";
						}
						else if(key.equals("RMENU")){
							key = "R.ALT";
						}
						else if(key.equals("LSHIFT")){
							key = "L.SHIFT";
						}
						else if(key.equals("RSHIFT")){
							key = "R.SHIFT";
						}
						else if(key.equals("LBRACKET")){
							key = "[";
						}
						else if(key.equals("RBRACKET")){
							key = "]";
						}
						else if(key.equals("CONTROL")){
							key = "CTRL";
						}
						else if(key.equals("CAPITAL")){
							key = "CAPS";
						}
						
						return key;
		}
	}
	
	public void activate(){
		super.activate();
		gameContainer.getInput().addKeyListener(this);
	}
	
	public void deactivate(){
		super.deactivate();
		gameContainer.getInput().removeKeyListener(this);
	}
	
	@Override
	public void keyPressed(int key, char c){
		if(inputtingKey){
			String keyName = (cursorPosCur[0].x == 0 ? "p1_" : "p2_") + keyNames[(int)cursorPosCur[0].y];
			
			if(Input.getKeyName(key).equals("ESCAPE")){
				Main.options.setNumber(keyName, oldKey);
				setKeymapKeyPos((int)cursorPosCur[0].y+(cursorPosCur[0].x == 0 ? 0 : 8), getKeyName(oldKey), false);
			}
			else{
				Main.options.setNumber(keyName, key);
				setKeymapKeyPos((int)cursorPosCur[0].y+(cursorPosCur[0].x == 0 ? 0 : 8), keyName, true);
				
				for(String name:keyNames){
					if(!keyName.endsWith(name)){
						if(Main.options.getNumber("p1_"+name) == key){ //Uh-oh, double assignment!
							Main.options.setNumber("p1_"+name, -1);
						}
						else if(Main.options.getNumber("p2_"+name) == key){ //Uh-oh, double assignment!
							Main.options.setNumber("p2_"+name, -1);
						}
					}
				}
				
				try{
					Main.options.save();
				} catch (IOException e){
					e.printStackTrace();
				}
			}
			
			inputtingKey = false;
			keyCaught = true;
			cursorLocked[0] = false;
		}
//		else if(Input.getKeyName(key).equals("ESCAPE")){
//			System.out.println("ESC");
//			goToParentMenu();
//		}
	}
	
	@Override
	public void inputEnded() {}

	@Override
	public void inputStarted() {}

	@Override
	public boolean isAcceptingInput() { return true; }

	@Override
	public void setInput(Input arg0) {}

	@Override
	public void keyReleased(int arg0, char arg1) {}
}
