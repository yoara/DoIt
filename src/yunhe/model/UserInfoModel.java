package yunhe.model;

import android.os.Parcel;
import android.os.Parcelable;

/** 用户信息表 **/
public class UserInfoModel implements Parcelable {
	public static final String TABLENAME = "DI_UserInfo";

	/**
	 * 数据库字段定义 id、标题、内容
	 * **/
	public static final String FIELD_ID = "id";
	public static final String FIELD_NAME = "name";
	public static final String FIELD_DATE = "date";
	public static final String FIELD_MALE = "male";
	public static final String FIELD_WEIBO = "weibo";
	public static final String FIELD_IMGPATH = "imgPath";
	/** 数据库字段定义END **/

	private int id;
	private String name;
	private String date;
	private String male;
	private String weibo;
	private String imgPath;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMale() {
		return male;
	}

	public void setMale(String male) {
		this.male = male;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getWeibo() {
		return weibo;
	}

	public void setWeibo(String weibo) {
		this.weibo = weibo;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public UserInfoModel(int id, String name, String date, String male,
			String weibo) {
		this.id = id;
		this.name = name;
		this.date = date;
		this.male = male;
		this.weibo = weibo;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.id);
		dest.writeString(this.name);
		dest.writeString(this.date);
		dest.writeString(this.male);
		dest.writeString(this.weibo);
	}

	public UserInfoModel() {
	}

	public static final Parcelable.Creator<UserInfoModel> CREATOR = new Parcelable.Creator<UserInfoModel>() {
		@Override
		public UserInfoModel createFromParcel(Parcel source) {
			// 从Parcel中读取数据，返回ContentModel对象
			return new UserInfoModel(source.readInt(), source.readString(),
					source.readString(), source.readString(),
					source.readString());
		}

		@Override
		public UserInfoModel[] newArray(int size) {
			return new UserInfoModel[size];
		}
	};
}
