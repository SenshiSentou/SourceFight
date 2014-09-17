package com.sourcefight.general;
import org.newdawn.slick.Color;
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
	
	public void render(Graphics gfx) throws SlickException {
		gfx.drawImage(image, x, y);
	}
	
	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
	
	public int getWidth() {
		return image.getWidth();
	}
	
	public int getHeight() {
		return image.getHeight();
	}

	public void setX(float f) {
		x = f;
	}
}