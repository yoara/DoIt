package yunhe.util;

import android.graphics.Color;

public enum ListTitleGradientColorEnum {
	BLUE_0("#004FFF","#005AFF","#006FFF"),
	BLUE_1("#005FFF","#006AFF","#007FFF"),
	BLUE_2("#006FFF","#007AFF","#008FFF"),
	BLUE_3("#007FFF","#008AFF","#009FFF"),
	BLUE_4("#008FFF","#009AFF","#00AFFF"),
	BLUE_5("#009FFF","#00AAFF","#00BFFF"),
	BLUE_6("#00AFFF","#00BAFF","#00CFFF"),
	BLUE_7("#00BFFF","#00CAFF","#00DFFF"),
	BLUE_8("#00CFFF","#00DAFF","#00EFFF"),
	BLUE_9("#00DFFF","#00EAFF","#00FFFF"),
	BLUE_10("#00EFFF","#0FFFFF","#DFF3FF");
	ListTitleGradientColorEnum(String startColor,
			String centerColor,String endColor){
		this.startColor = startColor;
		this.centerColor = centerColor;
		this.endColor = endColor;
	}
	private String startColor;
	private String centerColor;
	private String endColor;
	public int[] getGradientColors(){
		return new int[]{Color.parseColor(startColor),
			Color.parseColor(centerColor),
			Color.parseColor(endColor)};
	}
}
