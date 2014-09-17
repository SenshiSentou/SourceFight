package com.sourcefight.stages;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import com.sourcefight.general.Main;
import com.sourcefight.general.Sprite;

public class StageOffice extends Stage {
	private Color bgColor;
	private Color floorColor = new Color(.8f, .7f, .6f);
//	private Image skirting;
	private Image wall;
	
	public StageOffice() throws SlickException {
		super();
		Image ccPanel = new Image("Resources/Stages/Office/Office.png");
//		skirting = new Image("Resources/Stages/Office/Skirting.png");
//		skirting = ccPanel.getSubImage(5, 356, 5, 22);
		wall = ccPanel.getSubImage(2, 0, 20, ccPanel.getHeight());
//		skirting.setFilter(Image.FILTER_NEAREST);
		
		background = new Sprite[]{new Sprite(ccPanel, -318, Main.resY - ccPanel.getHeight() - 50),
		};//new Sprite(skirting, 0, 0)};
		foreground = new Sprite[]{};
		groundLevel = new int[][]{{-820, 545}};
		
		bgColor = ccPanel.getColor(0, 0);
	}
	
	public void update(GameContainer gc, int delta) throws SlickException {
//		if(gc.getInput().isKeyDown(Input.KEY_4)){
//			background[0].setX(background[0].getX() - delta*0.1f);
//			System.out.println(background[0].getX());
//		}
//		else if(gc.getInput().isKeyDown(Input.KEY_5)){
//			background[0].setX(background[0].getX() + delta*0.1f);
//			System.out.println(background[0].getX());
//		}
	}
	
	public void earlyRender(Graphics gfx) throws SlickException {
//		gfx.setColor(bgColor);
//		gfx.fillRect(-1525, 0, 3050, 451);
		gfx.setColor(floorColor);
		gfx.fillRect(-1525, 400, 3500, 700);
//		skirting.draw(-1525, 451, 3050, skirting.getHeight()+1);
		wall.draw(background[0].getX() - 700, background[0].getY(), 700, wall.getHeight());
		wall.draw(background[0].getX() + background[0].getWidth(), background[0].getY(), 700, wall.getHeight());
		super.earlyRender(gfx);
	}
	
	public void lateRender(Graphics gfx) throws SlickException {
		super.lateRender(gfx);
	}
}