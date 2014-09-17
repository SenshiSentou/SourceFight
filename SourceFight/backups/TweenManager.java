package general;
//UNUSED!!

import java.util.ArrayList;

public class TweenManager {
	static ArrayList<Tween> tweens = new ArrayList<Tween>();
	
	public void update(int delta){
		for(Tween tween:tweens){
			tween.update(delta);
		}
	}
}