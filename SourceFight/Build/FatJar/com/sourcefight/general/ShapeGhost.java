package com.sourcefight.general;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;

public class ShapeGhost {
	private Shape shapeOld, shapeCurrent;
	private int progress, duration;
	private float scaleEnd, scaleCurrent; //start is always 1
	private Color colorStart, colorEnd, colorCurrent;
	//These do not actually belong here, but saves recreating them every frame.
	private float fac;
	private Color colorOld;
	
	public ShapeGhost(Shape shape, int duration, float scaleEnd, Color colorStart, Color colorEnd){
		this.shapeOld = shape;
		this.duration = duration;
		this.scaleEnd = scaleEnd;
		this.colorStart = new Color(colorStart);
		this.colorCurrent = new Color(colorStart);
		this.colorEnd = colorEnd;
	}
	
	public boolean isFinished(){
		return progress >= duration;
	}
	
	public void update(GameContainer gc, int delta) throws SlickException {
		progress = Math.min(progress + delta, duration);
		
		fac = (progress/(float)duration);
		
		colorCurrent.r = colorStart.r + fac*(colorEnd.r - colorStart.r);
		colorCurrent.g = colorStart.g + fac*(colorEnd.g - colorStart.g);
		colorCurrent.b = colorStart.b + fac*(colorEnd.b - colorStart.b);
		colorCurrent.a = colorStart.a + fac*(colorEnd.a - colorStart.a);
		scaleCurrent = 1 + fac*(scaleEnd - 1);
	}
	
	public void render(GameContainer gc, Graphics gfx) throws SlickException {
		colorOld = gfx.getColor();
		shapeCurrent = shapeOld.transform(Transform.createScaleTransform(scaleCurrent, scaleCurrent));
		shapeCurrent = shapeCurrent.transform(Transform.createTranslateTransform(shapeOld.getCenterX() - shapeCurrent.getCenterX(),
																				 shapeOld.getCenterY() - shapeCurrent.getCenterY()));
		
		gfx.setColor(colorCurrent);
		gfx.fill(shapeCurrent);
		gfx.setColor(colorOld);
	}
}
