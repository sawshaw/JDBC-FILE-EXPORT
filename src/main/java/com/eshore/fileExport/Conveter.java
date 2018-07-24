/**
 * @Date 2016年7月19日
 *
 * @author Administrator
 */
package com.eshore.fileExport;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

/**
 * @author mercy
 *
 */
public class Conveter<T> {

	public static <T> T convert2Bean(ResultSet rs, Class<T> bean) throws Exception {
		Field[] fields = bean.getDeclaredFields();
		T obj = bean.newInstance();
		for (Field field : fields) {
			String pname = field.getName();
			BeanUtils.setProperty(obj, pname, rs.getObject(pname));
		}

		return obj != null ? obj : null;
	}

	public static <T> List<T> convert2BeanList(ResultSet rs, Class<T> bean) throws Exception {
		Field[] fields = bean.getDeclaredFields();
		List<T> lists = new ArrayList<T>();
		String pName="";
		String sqlName="";
		while (rs.next()) {
			T obj = bean.newInstance();
			for (Field field : fields) {
				String newName=getUnderlineString(field.getName());
				if(newName.contains("_")){
					String f1=newName.substring(0,newName.indexOf("_"));
					String f2=newName.substring(newName.indexOf("_"),newName.indexOf("_")+1);
					String f3=newName.substring(newName.indexOf("_")+1,newName.length());
					System.out.println("f1:"+f1+",f2:"+f2+",f3:"+f3);
					sqlName=f1+f2+f3;
				}
				pName = field.getName();
				BeanUtils.setProperty(obj, pName, rs.getObject(sqlName));
			}
			lists.add(obj);
		}
		return lists != null ? lists : null;
	}
	public static char upperOrLower(char c){
		if(c <= 90 && c >= 65){
            c += 32;
        } else if(c <= 122 && c >= 97){
            c -= 32;
        }
		return c;
	}
	public static boolean isUpper(char c){
		if(c <= 90 && c >= 65){
            return true;
        } else if(c <= 122 && c >= 97){
            return false;
        }
		return false;
	}
	//由payType转为pay_type
	public static String getUnderlineString(String str){
		StringBuilder strs=new StringBuilder();
		for(int i=0;i<str.length();i++){
			if(isUpper(str.charAt(i))){
				strs.append("_").append(upperOrLower(str.charAt(i)));
			}else{
				strs.append(str.charAt(i));
			}
		}
		return strs.toString();
	}


}
