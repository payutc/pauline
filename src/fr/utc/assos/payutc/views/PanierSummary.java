package fr.utc.assos.payutc.views;

import fr.utc.assos.payutc.PaulineSession;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

public class PanierSummary extends TextView {
	
	private int mNbArticles;
	private int mTotalCost;
	
	//constructor 1 required for in-code creation
	public PanierSummary(Context context){
		super(context);
		init();
	}

	//constructor 2 required for inflation from resource file
	public PanierSummary(Context context, AttributeSet attr){
		super(context,attr);
		init();
	}

	//constructor 3 required for inflation from resource file
	public PanierSummary(Context context, AttributeSet attr, int defaultStyles){
		super(context, attr, defaultStyles);
		init();
	}
	
	public void init() {
		set(0, 0);
	}
	
	public void set(int nb, int cost) {
		mNbArticles = nb;
		mTotalCost = cost;
		updateText();
	}
	
	public void set(PaulineSession session) {
		set(session.getNbItems(), session.getTotal());
	}
	
	private void updateText() {
		String suffixArticles = " article";
		if (mNbArticles > 1) {
			suffixArticles += "s";
		}
		super.setText(""+(mTotalCost/100.0)+"€ ("+mNbArticles+suffixArticles+")");
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		updateText();
		super.onDraw(canvas);
	}
}
