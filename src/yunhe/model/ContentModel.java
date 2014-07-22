package yunhe.model;

import android.os.Parcel;
import android.os.Parcelable;

/** 信息记录表 **/
public class ContentModel implements Parcelable {
	public static final String TABLENAME = "Content";

	/**
	 * 数据库字段定义 id、标题、内容
	 * **/
	public static final String FIELD_ID = "id";
	public static final String FIELD_TITLE = "title";
	public static final String FIELD_CONTENT = "content";
	public static final String FIELD_DATE = "date";
	public static final String FIELD_TIME = "time";
	/** 数据库字段定义END **/

	private int id;
	private String title;
	private String content;
	private String date;
	private String time;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public ContentModel(int id, String title, String content, String date,
			String time) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.date = date;
		this.time = time;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.id);
		dest.writeString(this.title);
		dest.writeString(this.content);
		dest.writeString(this.date);
		dest.writeString(this.time);
	}

	public ContentModel() {
	}

	public static final Parcelable.Creator<ContentModel> CREATOR = new Parcelable.Creator<ContentModel>() {
		@Override
		public ContentModel createFromParcel(Parcel source) {
			// 从Parcel中读取数据，返回ContentModel对象
			return new ContentModel(source.readInt(), source.readString(),
					source.readString(), source.readString(),
					source.readString());
		}

		@Override
		public ContentModel[] newArray(int size) {
			return new ContentModel[size];
		}
	};
}
