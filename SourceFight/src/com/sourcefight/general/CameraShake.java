package com.sourcefight.general;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

public class CameraShake {
	private Vector2f amplitudeX, amplitudeY;
	private int interval;
	private int lastShakeTime;
	private int shakeX, shakeY;
	private int fadeDuration;
	private int fadeTimeEnd;
	private int startTime;
	
	public CameraShake(Vector2f amplitudeX, Vector2f amplitudeY, int interval, int time, int fadeDuration, int fadeTimeEnd) {
		this.amplitudeX = amplitudeX;
		this.amplitudeY = amplitudeY;
		this.interval = interval;
		this.startTime = time;
		this.fadeDuration = fadeDuration;
		if(fadeTimeEnd > 0){
			this.fadeTimeEnd = time + fadeTimeEnd - fadeDuration;
		}
	}
	
	public void shake(Graphics gfx, int currentTime){
		if(currentTime > lastShakeTime + interval){
			float fade = 1;
			if(currentTime - startTime <= fadeDuration){
				fade = (currentTime - startTime) / (float)fadeDuration;
			}
			else if(currentTime >= fadeTimeEnd && fadeTimeEnd + fadeDuration - currentTime > 0){
				fade = (fadeTimeEnd + fadeDuration - currentTime) / (float)fadeDuration;
			}
			shakeX = (int)((amplitudeX.x + Math.random() * (amplitudeX.y - amplitudeX.x)) * fade);
			shakeY = (int)((amplitudeY.x + Math.random() * (amplitudeY.y - amplitudeY.x)) * fade);
			gfx.translate(shakeX, shakeY);
			lastShakeTime = currentTime;
		}
	}
}
