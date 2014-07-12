package yunhe.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import yunhe.model.ActivityShowContentModel;
import yunhe.model.ContentModel;
import yunhe.util.Constants;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ContentDBUtil {
	
	/***
	 * 刪除数据By ID
	 * @return
	 */
	public void deleteContentById(String id,Context context) {
		DBHelper sqler = new DBHelper
				(context,Constants.DATANAME,null,Constants.DB_VERSION);
		SQLiteDatabase db = sqler.getWritableDatabase();
		db.delete(ContentModel.TABLENAME, " id=? ", new String[]{id});
		db.close();
		sqler.close();
	}
	/***
	 * 保存数据到数据库
	 * @return
	 */
	public void saveToContentDb(ContentModel model,Context context) {
		ContentValues cv = packageContentValues(model);
		DBHelper sqler = new DBHelper
				(context,Constants.DATANAME,null,Constants.DB_VERSION);
		SQLiteDatabase db = sqler.getWritableDatabase();
		db.insert(ContentModel.TABLENAME, null, cv);
		db.close();
		sqler.close();
	}
	/***
	 * 更新数据到数据库
	 * @return
	 */
	public void updateContentDb(ContentModel model,Context context) {
		ContentValues cv = packageContentValues(model);
		DBHelper sqler = new DBHelper
				(context,Constants.DATANAME,null,Constants.DB_VERSION);
		SQLiteDatabase db = sqler.getWritableDatabase();
		db.update(ContentModel.TABLENAME, cv, 
				ContentModel.FIELD_ID +"=? ", 
				new String[]{""+model.getId()});
		db.close();
		sqler.close();
	}
	/**组装存储数据**/
	private ContentValues packageContentValues(ContentModel model){
		ContentValues cv = new ContentValues();
		/**存储字段**/
		cv.put(ContentModel.FIELD_TITLE, model.getTitle());
		cv.put(ContentModel.FIELD_CONTENT, model.getContent());
		cv.put(ContentModel.FIELD_DATE, model.getDate());
		cv.put(ContentModel.FIELD_TIME, model.getTime());
		/**存储字段END**/
		return cv;
	}
	
	/***
	 * 查询记录数据
	 * @return
	 */
	public List<Map<String, Object>> queryContentForList(String date, Context context){
		DBHelper sqler = new DBHelper
				(context,Constants.DATANAME,null,Constants.DB_VERSION);
		SQLiteDatabase db = sqler.getReadableDatabase();
		
		Cursor cursor = db.query(ContentModel.TABLENAME,
				new String[]{ContentModel.FIELD_ID,ContentModel.FIELD_TITLE}, 		//返回的列
				getPackageSqlWhereStr(date), 			//where 字句：name=?
				getPackageSqlWhereParams(date), 		//字句参数
				null, 									//groupBy
				null, 									//have
				null									//orderBy
				);
		List<Map<String, Object>> listItem = packageList(cursor);
		cursor.close();
		db.close();
		sqler.close();
		return listItem;
	}
	private String[] getPackageSqlWhereParams(String date) {
		if(date!=null&&date!=""){
			return new String[]{date};
		}
		return null;
	}
	private String getPackageSqlWhereStr(String date) {
		StringBuffer strSb = new StringBuffer(" 1=1 ");
		if(date!=null&&date!=""){
			strSb.append(" and ");
			strSb.append(ContentModel.FIELD_DATE).append("=? ");
		}
		return strSb.toString();
	}
	/**封装显示的数据List**/
	private List<Map<String, Object>> packageList(Cursor cursor) {
		List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();
		//页面显示 （id） ，名称
		//遍历结果
		while(cursor.moveToNext()){
			int id = cursor.getInt(cursor.getColumnIndex(ContentModel.FIELD_ID));
            String title = cursor.getString(cursor.getColumnIndex(ContentModel.FIELD_TITLE));  
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(ActivityShowContentModel.ITEM_ID, id);
            map.put(ActivityShowContentModel.ITEM_TITLE, title);
            listItem.add(map);
        }
		return listItem;
	}
	
	/***
	 * 查询单个详细数据
	 * @return
	 */
	public ContentModel queryContentForDetail(String id, Context context){
		DBHelper sqler = new DBHelper
				(context,Constants.DATANAME,null,Constants.DB_VERSION);
		SQLiteDatabase db = sqler.getReadableDatabase();
		//id、标题、内容、日期、时间
		Cursor cursor = db.query(ContentModel.TABLENAME,
				new String[]{ContentModel.FIELD_ID,ContentModel.FIELD_TITLE,ContentModel.FIELD_CONTENT,
				ContentModel.FIELD_DATE,ContentModel.FIELD_TIME,
				}, 										//返回的列
				ContentModel.FIELD_ID +"=? ", 			//where 字句：name=?
				new String[]{id}, 						//字句参数
				null, 									//groupBy
				null, 									//have
				null									//orderBy
				);
		ContentModel model = packageDetail(cursor);
		cursor.close();
		db.close();
		sqler.close();
		return model;
	}
	/**封装显示的详细数据**/
	private ContentModel packageDetail(Cursor cursor) {
		ContentModel model = new ContentModel();
		//页面:id、标题、内容、日期、时间
		boolean hasResult = false;
		while(cursor.moveToNext()){
			hasResult = true;
			model.setId(cursor.getInt(cursor.getColumnIndex(ContentModel.FIELD_ID)));
            model.setTitle(cursor.getString(cursor.getColumnIndex(ContentModel.FIELD_TITLE)));
            model.setContent(cursor.getString(cursor.getColumnIndex(ContentModel.FIELD_CONTENT)));
            model.setDate(cursor.getString(cursor.getColumnIndex(ContentModel.FIELD_DATE)));
            model.setTime(cursor.getString(cursor.getColumnIndex(ContentModel.FIELD_TIME)));
    		break;
        }
		if(hasResult){
			return model;
		}else{
			return null;
		}
	}
}
