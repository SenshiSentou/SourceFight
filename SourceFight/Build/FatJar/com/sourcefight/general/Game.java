package com.sourcefight.general;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Transform;

import com.sourcefight.stages.Stage;
import com.sourcefight.stages.StageVidcon;


public class Game {
	public int timeSinceStartup;
	public float gravity = 5f;
	public float frictionAir = 1.0f; //X only
	public float frictionGround = 2.2f; //X only
	
	private Stage stage;
	private Player playerOne;
	private Character playerTwo;
	private int playerOffset = 50;
	private int zoomMargin = 30; //space between edge of screen and characters
	private int zoomHoldMargin = 35; //must move back in by this amount before zooming in
	private float maxZoom = 0.3f;
	private float minZoom = 1f;
	private float zoom = 1;
	private boolean paused = false;
	private int roundTime = 60000; //ms
	private int[] playerScore = new int[2];
	
	//GUI elements. XY = top-left
	private int hpBarX = 20;
	private int hpBarY = 20;
	private int hpBarW = 300;
	private int hpBarH = 30;
	private Color hpBarColor = new Color(.8f, .1f, .1f, 1f);
	
	private int specialBarX = 20;
	private int specialBarY = 55;
	private int specialBarW = 225;
	private int specialBarH = 15;
	private int specialBarSlopeX = 20; //difference in top and bottom X; creating a '/' shape
	private int specialBarSegmentGap = 5;
	private Color specialBarEmptyColor = new Color(.1f, .1f, .8f, 1f);
	private Color specialBarFillColor = new Color(.3f, .3f, .8f, 1f);
	private Color specialBarChargeColor = new Color(.5f, .5f, .8f, 1f);
	private Color specialBarTransformColor = new Color(.5f, .8f, .3f, 1f);
	
	private int victoryBoxY = 50;
	private int victoryBoxW = 15;
	private int victoryBoxH = 15;
	private int victoryBoxSpacing = 10;
	private Color victoryBoxColor = new Color(.8f, .1f, .1f, 1f);
		
	private Polygon[] specialBarSegments = new Polygon[6];
	private Polygon[] specialBarFills = new Polygon[4];
	private Rectangle[] victoryBoxes = new Rectangle[3];
	private ArrayList<ShapeGhost> shapeGhosts = new ArrayList<ShapeGhost>(); 
	private ArrayList<ShapeGhost> finishedShapeGhosts = new ArrayList<ShapeGhost>(); 
	
	private int[] playerSpecialChargeSegmentsFilled = new int[2];
	
	
	public Game(String playerOneName, String playerTwoName, String stageName) throws SlickException{
		if(stageName.equals("Vidcon")){
			stage = new StageVidcon();
		}
		
		playerOne = new Player(this, stage, 1, playerOneName, Main.resX/2 - playerOffset, 1f); //size was 3f in tests
		playerTwo = new Player(this, stage, 2, playerTwoName, Main.resX/2 + playerOffset, 1f);
//		playerTwo = new CPUPlayer(this, stage, playerTwoName, Main.resX/2 + playerOffset, 3f, 1);
		
		playerOne.setOtherPlayer(playerTwo);
		playerTwo.setOtherPlayer(playerOne);
		
		float specialBarSegmentW = specialBarW / 3 - (specialBarSegmentGap * 2) / 3; //longest part in case of side-segments. 3 = numSegments
		 //Poly order = TL, BL, BR, TR
		specialBarSegments[0] = new Polygon(new float[]{specialBarX, specialBarY,
														specialBarX, specialBarY+specialBarH,
														specialBarX + specialBarSegmentW - specialBarSlopeX, specialBarY + specialBarH,
														specialBarX + specialBarSegmentW, specialBarY});
		
		specialBarSegments[1] = new Polygon(new float[]{specialBarX + specialBarSegmentW + specialBarSegmentGap, specialBarY,
														specialBarX + specialBarSegmentW + specialBarSegmentGap - specialBarSlopeX, specialBarY + specialBarH,
														specialBarX + specialBarSegmentW*2 + specialBarSegmentGap - specialBarSlopeX, specialBarY + specialBarH,
														specialBarX + specialBarSegmentW*2 + specialBarSegmentGap, specialBarY});
		
		specialBarSegments[2] = new Polygon(new float[]{specialBarX + specialBarSegmentW*2 + specialBarSegmentGap*2, specialBarY,
														specialBarX + specialBarSegmentW*2 + specialBarSegmentGap*2 - specialBarSlopeX, specialBarY + specialBarH,
														specialBarX + specialBarSegmentW*3 + specialBarSegmentGap*2 - specialBarSlopeX, specialBarY + specialBarH,
														specialBarX + specialBarSegmentW*3 + specialBarSegmentGap*2, specialBarY});
		
		specialBarFills[0] = new Polygon(new float[]{specialBarX - specialBarW, specialBarY,
										 			 specialBarX - specialBarW, specialBarY + specialBarH,
										 			 specialBarX - specialBarSlopeX, specialBarY + specialBarH,
										 			 specialBarX, specialBarY});
		
		Transform mirrorX = new Transform(Transform.createRotateTransform((float)Math.PI, Main.resX*0.5f, specialBarY+specialBarH*0.5f),
										  new Transform(Transform.createScaleTransform(1, -1),
												  		Transform.createTranslateTransform(0f, -specialBarY*2 - specialBarH)));
		
		specialBarSegments[3] = (Polygon)specialBarSegments[0].transform(mirrorX);
		specialBarSegments[4] = (Polygon)specialBarSegments[1].transform(mirrorX);
		specialBarSegments[5] = (Polygon)specialBarSegments[2].transform(mirrorX);
		
		specialBarFills[1] = (Polygon)specialBarFills[0].transform(mirrorX);
		specialBarFills[2] = specialBarFills[0].copy(); //transform bar
		specialBarFills[3] = specialBarFills[1].copy(); //transform bar
		
		victoryBoxes[0] = new Rectangle(Main.resX/2 - victoryBoxW*1.5f - victoryBoxSpacing, victoryBoxY, victoryBoxW, victoryBoxH);
		victoryBoxes[1] = new Rectangle(Main.resX/2 - victoryBoxW*0.5f, victoryBoxY, victoryBoxW, victoryBoxH);
		victoryBoxes[2] = new Rectangle(Main.resX/2 + victoryBoxW*0.5f + victoryBoxSpacing, victoryBoxY, victoryBoxW, victoryBoxH);
		
	}
	
	public void update(GameContainer gc, int delta) throws SlickException {		
		if(!paused){
			timeSinceStartup += delta;
			playerSpecialChargeSegmentsFilled[0] = playerOne.getNumSegmentsCharged();
			playerSpecialChargeSegmentsFilled[1] = playerTwo.getNumSegmentsCharged();
			
			stage.update(gc, delta);
			playerOne.update(gc, delta);
			playerTwo.update(gc, delta);
			
			if(playerOne.getNumSegmentsCharged() > playerSpecialChargeSegmentsFilled[0] && playerOne.getNumSegmentsCharged() < 4){ //+1 this frame
				shapeGhosts.add(new ShapeGhost(specialBarSegments[playerSpecialChargeSegmentsFilled[0]],
											   400, 1.3f, new Color(1f, 1f, 1f, 0.8f),
											   new Color(specialBarChargeColor.r, specialBarChargeColor.g, specialBarChargeColor.b, 0f)));
				playerSpecialChargeSegmentsFilled[0]++;
			}
			
			if(playerTwo.getNumSegmentsCharged() > playerSpecialChargeSegmentsFilled[1] && playerTwo.getNumSegmentsCharged() < 4){ //+1 this frame
				shapeGhosts.add(new ShapeGhost(specialBarSegments[playerSpecialChargeSegmentsFilled[1]+3],
											   400, 1.3f, new Color(.75f, .75f, 1f, 1f),
											   new Color(specialBarChargeColor.r, specialBarChargeColor.g, specialBarChargeColor.b, 0f)));
				playerSpecialChargeSegmentsFilled[1]++;
			}
			
			for(ShapeGhost shapeGhost:shapeGhosts){
				if(shapeGhost.isFinished()){
					finishedShapeGhosts.add(shapeGhost);
				}
				else{
					shapeGhost.update(gc, delta);
				}
			}
			shapeGhosts.removeAll(finishedShapeGhosts);
			finishedShapeGhosts.clear();
		
		}
		
		if(gc.getInput().isKeyPressed(Input.KEY_ESCAPE) && timeSinceStartup < roundTime){
			paused = !paused;
		}
	}
	
	public void render(GameContainer gc, Graphics gfx) throws SlickException {
		gfx.translate(Main.resX/2, Main.resY/2);
		gfx.scale(zoom, zoom);
		gfx.translate(-Main.resX/2, -Main.resY/2);
		
		int cx = (int)((playerOne.getCenterX() + playerTwo.getCenterX())/2);
		float cameraTranslationX = Main.resX/2-cx;
		float playerOneScreenX = cameraTranslationX*zoom + playerOne.getOutsideX()*zoom + (Main.resX-(Main.resX*zoom))/2; //Player1 (player.x + (1-zoom)*player.x);
		
		gfx.translate(cameraTranslationX, 0);
		
		if(playerOneScreenX < zoomMargin || playerOneScreenX > Main.resX - zoomMargin){ //zoom out
			zoom = Math.max(zoom - .001f, maxZoom);
		}
		else if((playerOne.getCenterX() < playerTwo.getCenterX() && playerOneScreenX > zoomMargin + zoomHoldMargin) ||
				(playerOne.getCenterX() > playerTwo.getCenterX() && playerOneScreenX < Main.resX - zoomMargin - zoomHoldMargin)){ //zoom in
			zoom = Math.min(zoom + .001f, minZoom);
		}
		
		stage.render(gc, gfx);
		playerOne.render(gc, gfx);
		playerTwo.render(gc, gfx);
		
		gfx.resetTransform();
		
		String timeLeft = String.valueOf((int)(Math.max(0, (roundTime - timeSinceStartup + 1000))/1000));
		Main.fontImpact.drawString(Main.resX*0.5f - Main.fontImpact.getWidth(timeLeft)*0.5f, 10, timeLeft);
		
		gfx.setColor(hpBarColor);
		
//		gfx.fillRect(hpBarX, hpBarY, hpBarW, hpBarH);
		gfx.fillRect(hpBarX, hpBarY, playerOne.getHP() * ((float)hpBarW/100), hpBarH);
		
//		gfx.fillRect(Main.resX - hpBarX, hpBarY, -hpBarW, hpBarH);
		gfx.fillRect(Main.resX - hpBarX, hpBarY, playerTwo.getHP() * -((float)hpBarW/100), hpBarH);
		
		//first layer; empty
		gfx.setColor(specialBarEmptyColor);
		
		for(Polygon specialBarSegment:specialBarSegments){
			gfx.fill(specialBarSegment);
		}
		
		//mask for second layer; fill
		gfx.setDrawMode(Graphics.MODE_ALPHA_MAP);
		gfx.clearAlphaMap();
		
		gfx.setColor(new Color(1f,1f,1f,1f));
		
		for(Polygon specialBarSegment:specialBarSegments){ 
			gfx.fill(specialBarSegment);
		}
		
		gfx.setDrawMode(Graphics.MODE_ALPHA_BLEND);
		gfx.setColor(specialBarFillColor);
		
		gfx.fill(specialBarFills[0].transform(Transform.createTranslateTransform(playerOne.getSpecialFill()/100*specialBarW, 0)));
		gfx.fill(specialBarFills[1].transform(Transform.createTranslateTransform(-playerTwo.getSpecialFill()/100*specialBarW, 0)));
		
		//mask for third layer; charge
		gfx.setDrawMode(Graphics.MODE_ALPHA_MAP);
		gfx.clearAlphaMap();
		
		gfx.setColor(new Color(1f,1f,1f,1f));
		
		for(Polygon specialBarSegment:specialBarSegments){ 
			gfx.fill(specialBarSegment);
		}
		
		gfx.setDrawMode(Graphics.MODE_ALPHA_BLEND);
		gfx.setColor(specialBarChargeColor);
		
		gfx.fill(specialBarFills[0].transform(Transform.createTranslateTransform(Math.min(100, playerOne.getSpecialCharge())/100*specialBarW, 0)));
		gfx.fill(specialBarFills[1].transform(Transform.createTranslateTransform(Math.min(100, -playerTwo.getSpecialCharge())/100*specialBarW, 0)));
		
		gfx.setColor(specialBarTransformColor);
		
		gfx.fill(specialBarFills[2].transform(Transform.createTranslateTransform(Math.max(0, playerOne.getSpecialCharge()-100)/100*specialBarW, 0)));
		gfx.fill(specialBarFills[3].transform(Transform.createTranslateTransform(Math.max(0, -playerTwo.getSpecialCharge()-100)/100*specialBarW, 0)));
		
		
		gfx.setDrawMode(Graphics.MODE_NORMAL);
		
		gfx.setColor(victoryBoxColor);
		
		conditionalRectFill(gfx, victoryBoxes[0], playerScore[0] > 0);
		conditionalRectFill(gfx, victoryBoxes[1], playerScore[0] > 1 || playerScore[1] > 1);
		conditionalRectFill(gfx, victoryBoxes[2], playerScore[1] > 0);
		
		for(ShapeGhost shapeGhost:shapeGhosts){
			shapeGhost.render(gc, gfx);
		}
		
		if(paused){
			Main.fontImpact.drawString(Main.resX*0.5f - Main.fontImpact.getWidth("PAUSED")*0.5f, 300, "PAUSED");
		}
		
	}
	
	private void conditionalRectFill(Graphics gfx, Rectangle rect, boolean fill) {
		if(fill){
			gfx.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
		}
		else{
			gfx.drawRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
		}
	}

	public boolean maxZoomReached(){
		return zoom == maxZoom;
	}
	
	public Stage getStage(){
		return stage;
	}
}