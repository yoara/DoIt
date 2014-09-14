package yunhe.database;

import yunhe.model.ContentModel;
import yunhe.model.UserInfoModel;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/** DBHelper封装了数据库新建或更新的方法 **/
public class DBHelper extends SQLiteOpenHelper{

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//更新数据库，一般用于数据库删除和新建
		db.execSQL("DROP TABLE IF EXISTS "+ContentModel.TABLENAME);  
		db.execSQL("DROP TABLE IF EXISTS "+UserInfoModel.TABLENAME);
		createTable(db);
	}
	
	private void createTable(SQLiteDatabase db){
		String sql_content = "CREATE TABLE " + ContentModel.TABLENAME +
				" ('" +ContentModel.FIELD_ID+"' INTEGER PRIMARY KEY AUTOINCREMENT," +
				"'" +ContentModel.FIELD_TITLE+"' VARCHAR ," +
				"'" +ContentModel.FIELD_CONTENT+"' VARCHAR ," +
				"'" +ContentModel.FIELD_DATE+"' VARCHAR ," +
				"'" +ContentModel.FIELD_TIME+"' VARCHAR ," +
				"'" +ContentModel.FIELD_ISDONE+"' VARCHAR ," +
				"'" +ContentModel.FIELD_ISALARM+"' VARCHAR " +
				");";
		db.execSQL(sql_content);
		
		String sql_userInfo = "CREATE TABLE " + UserInfoModel.TABLENAME +
				" ('" +UserInfoModel.FIELD_ID+"' INTEGER PRIMARY KEY," +
				"'" +UserInfoModel.FIELD_NAME+"' VARCHAR ," +
				"'" +UserInfoModel.FIELD_DATE+"' VARCHAR ," +
				"'" +UserInfoModel.FIELD_MALE+"' VARCHAR ," +
				"'" +UserInfoModel.FIELD_WEIBO+"' VARCHAR ," +
				"'" +UserInfoModel.FIELD_IMGPATH+"' VARCHAR ," +
				"'" +UserInfoModel.FIELD_INITACTIVITY+"' VARCHAR " +
				");";
		db.execSQL(sql_userInfo);
	}
}
