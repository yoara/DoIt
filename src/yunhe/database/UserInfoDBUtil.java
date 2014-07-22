package yunhe.database;

import yunhe.model.UserInfoModel;
import yunhe.util.Constants;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/** 用户信息表操作类 **/
public class UserInfoDBUtil {
	private final static int USER_INFO_ID = 1;
	/***
	 * 保存数据到数据库
	 * @param model 用户信息对象{@link UserInfoModel}
	 * @param context 调用该操作的context
	 */
	public void saveToUserInfoDb(UserInfoModel model,Context context) {
		ContentValues cv = packageContentValues(model);
		DBHelper sqler = new DBHelper
				(context,Constants.DATANAME,null,Constants.DB_VERSION);
		SQLiteDatabase db = sqler.getWritableDatabase();
		db.insert(UserInfoModel.TABLENAME, null, cv);
		db.close();
		sqler.close();
	}
	/***
	 * 更新数据到数据库
	 * @param model 用户信息对象{@link UserInfoModel}
	 * @param context 调用该操作的context
	 */
	public void updateUserInfoDb(UserInfoModel model,Context context) {
		ContentValues cv = packageContentValues(model);
		DBHelper sqler = new DBHelper
				(context,Constants.DATANAME,null,Constants.DB_VERSION);
		SQLiteDatabase db = sqler.getWritableDatabase();
		db.update(UserInfoModel.TABLENAME, cv, 
				UserInfoModel.FIELD_ID +"=? ", 
				new String[]{""+USER_INFO_ID});
		db.close();
		sqler.close();
	}
	/**组装存储数据**/
	private ContentValues packageContentValues(UserInfoModel model){
		ContentValues cv = new ContentValues();
		/**存储字段**/
		cv.put(UserInfoModel.FIELD_ID, USER_INFO_ID);
		cv.put(UserInfoModel.FIELD_NAME, model.getName());
		cv.put(UserInfoModel.FIELD_DATE, model.getDate());
		cv.put(UserInfoModel.FIELD_MALE, model.getMale());
		cv.put(UserInfoModel.FIELD_WEIBO, model.getWeibo());
		/**存储字段END**/
		return cv;
	}
	
	/***
	 * 查询单个详细数据
	 * @param context 调用该操作的context
	 */
	public UserInfoModel queryUserInfoForDetail(Context context){
		DBHelper sqler = new DBHelper
				(context,Constants.DATANAME,null,Constants.DB_VERSION);
		SQLiteDatabase db = sqler.getReadableDatabase();
		//id、标题、内容、日期、时间
		Cursor cursor = db.query(UserInfoModel.TABLENAME,
				new String[]{UserInfoModel.FIELD_ID,UserInfoModel.FIELD_NAME,UserInfoModel.FIELD_DATE,
				UserInfoModel.FIELD_MALE,UserInfoModel.FIELD_WEIBO,
				}, 										//返回的列
				UserInfoModel.FIELD_ID +"=? ", 			//where 字句：name=?
				new String[]{""+USER_INFO_ID}, 			//字句参数
				null, 									//groupBy
				null, 									//have
				null									//orderBy
				);
		UserInfoModel model = packageDetail(cursor);
		cursor.close();
		db.close();
		sqler.close();
		return model;
	}
	/**封装显示的详细数据**/
	private UserInfoModel packageDetail(Cursor cursor) {
		UserInfoModel model = new UserInfoModel();
		//页面:id、标题、内容、日期、时间
		boolean hasResult = false;
		while(cursor.moveToNext()){
			hasResult = true;
			model.setId(cursor.getInt(cursor.getColumnIndex(UserInfoModel.FIELD_ID)));
            model.setName(cursor.getString(cursor.getColumnIndex(UserInfoModel.FIELD_NAME)));
            model.setDate(cursor.getString(cursor.getColumnIndex(UserInfoModel.FIELD_DATE)));
            model.setMale(cursor.getString(cursor.getColumnIndex(UserInfoModel.FIELD_MALE)));
            model.setWeibo(cursor.getString(cursor.getColumnIndex(UserInfoModel.FIELD_WEIBO)));
    		break;
        }
		if(hasResult){
			return model;
		}else{
			return null;
		}
	}
}
