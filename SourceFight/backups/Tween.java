package general;
public class Tween{
	static enum Interpolation {Linear, QuadraticEaseIn, QuadraticEaseOut};
	
	float from, to, duration, time;
	
	public Tween(float from, float to, int duration){
		this.from = from;
		this.to = to;
		this.duration = duration;
		TweenManager.tweens.add(this);
	}
	
	public void update(int delta){
		if(time+delta <= duration){
			time += delta;
		}
		else{
			time = duration;
		}
	}
	
	public float getValue(Interpolation interp){
		float t = time/duration;
		switch(interp){
			case Linear:				return from + t*100*(to-from);
			case QuadraticEaseIn:		return (to-from)*t*t+from;
			case QuadraticEaseOut:		return -(to-from)*t*(t-2)+from;
			
		}
		return 0f;
	}
}
