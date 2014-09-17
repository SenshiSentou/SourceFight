package com.sourcefight.general;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Sprite {
	Image image;
	float x;
	float y;
	
	public Sprite(Image image, float x, float y){
			this.image = image;
			this.x = x;
			this.y = y;
			//Main.sprites.add(this);
	}
	
	public void render(GameContainer gc, Graphics gfx) throws SlickException {
		gfx.drawImage(image, x, y);
	}
}