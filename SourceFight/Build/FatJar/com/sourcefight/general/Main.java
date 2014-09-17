package com.sourcefight.general;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SavedState;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

import com.sourcefight.menus.*;


public class Main extends BasicGame{
	private static Menu activeMenu;
	
	public static int resX = 800;
	public static int resY = 600;
	public static UnicodeFont fontImpact;
	public static SavedState options; //would be better left to Menu.java, but this ensures only one instance.
	
	public static StartMenu startMenu;
	public static CharSelectMenu charSelectMenu;
	public static StageSelectMenu stageSelectMenu;
	public static SettingsMenu settingsMenu;
	public static Game game;
	
	private String playerOne;
	private String playerTwo;
	private String stageName;
	
	public Main(String title) {
		super(title);
	}
	
	public static void main(String args[]) throws SlickException {
		AppGameContainer app = new AppGameContainer(new Main("Sourcefight"));
		app.setDisplayMode(resX, resY, false);
		app.start();
	}

	@Override
	public void render(GameContainer gc, Graphics gfx) throws SlickException {
		if(getActiveMenu() != null){
			getActiveMenu().render(gc, gfx);
		}
		else if(game != null){
			game.render(gc, gfx);
		}
	}

	@SuppressWarnings("unchecked") //font.getEffects()
	@Override
	public void init(GameContainer gc) throws SlickException {
		fontImpact = new UnicodeFont("Resources/Fonts/Impact.ttf", 24, true, false);
		fontImpact.addAsciiGlyphs();
		fontImpact.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
		fontImpact.loadGlyphs();
		
		options = new SavedState("options");
		
		startMenu = new StartMenu(this, resX, resY, null);
		charSelectMenu = new CharSelectMenu(this, resX, resY, startMenu);
		stageSelectMenu = new StageSelectMenu(this, resX, resY, charSelectMenu);
		settingsMenu = new SettingsMenu(this, resX, resY, gc, startMenu);
		
		setActiveMenu(startMenu);
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		if(activeMenu != null){
			activeMenu.update(gc, delta);
		}
		else if(game != null){
			game.update(gc, delta);
		}
	}
	
	public void setCharacter(int player, String characterName){
		if(player == 0){
			playerOne = characterName;
		}
		else{
			playerTwo = characterName;
		}
	}
	
	public void setStage(String stageName){
		this.stageName = stageName; 
	}
	
	public void startGame() throws SlickException{
		setActiveMenu(null);
		game = new Game(playerOne, playerTwo, stageName);
	}
	
	public static Menu getActiveMenu() {
		return activeMenu;
	}
	
	public static void setActiveMenu(Menu menu) {
		if(activeMenu != null){
			activeMenu.deactivate();
		}
		activeMenu = menu;
		if(activeMenu != null){
			activeMenu.activate();
		}
	}
	
	public static Game getGame(){
		return game;
	}
	
}