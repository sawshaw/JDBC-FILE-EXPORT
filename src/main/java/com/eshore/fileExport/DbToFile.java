package com.eshore.fileExport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mercy
 *操作数据库，将数据库数据生成json文件
 */
public class DbToFile {
	 public static Logger logger = LoggerFactory.getLogger(DbToFile.class);
	 //文件上传目录
	 private  String ftpDir="";
	 //文件备份目录
	 private String localBakDir="";
	 //表名称
	 private String tableName="";
	 //文件名
	 private String fileName="";
	 private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
	 private final String sqlQuery="select business_number,pay_type,area_code,create_time,product_id,product_spec_code,product_name,price,book_type,acc_type,dele_code from ";
	 private final String sqlCount="select count(*) from ";
	 private int dataNum=0;
	 Dbutils util=new Dbutils();
	 //开始
	 public void work(){
		 ftpDir=MapRegister.getMap().get("ftpDir");
		 localBakDir=MapRegister.getMap().get("localBakDir");
		 tableName=MapRegister.getMap().get("tableName");
		 fileName=MapRegister.getMap().get("fileName");
		 dataNum=Integer.parseInt(MapRegister.getMap().get("dataNum"));
		 logger.info("run...."+sdf1.format(new Date()));
		 this.createFolder(ftpDir,localBakDir);
		 //清除ftp目录数据
		 this.clearFtpFile(ftpDir);
		 localBakDir=this.getFileBakPath(localBakDir);
		 String ymdFormat=this.getPreMonthDay(1);
		 List<BillData> billList=new ArrayList<BillData>();
		 fileName=fileName.replace("yyyymmdd", ymdFormat);
		 int count=this.count();
		 logger.info("当天数据总量:{}",count);
		 if(count<dataNum){
			 billList=this.queryBillList(0,0);
			 fileName=fileName.replace("xxx", "001");
			 logger.info("fileName:"+fileName);
			 this.writeFile(fileName, billList,ftpDir);
			 this.removeFiles(fileName,ftpDir,localBakDir,"");
		 }else{
			 //分页传
			 int page=count/dataNum;
			 int size=dataNum;
			 int fileSplit=0;
			 String splitResult="";
			 for(int i=0;i<=page;i++){
				 String tmpFileName=fileName;
				 int start=i*size;
				 fileSplit++;
				 billList=this.queryBillList(start,size);
				 splitResult=String.format("%0"+3+"d",fileSplit);
				 System.out.println("===="+splitResult);
				 tmpFileName=tmpFileName.replace("xxx", splitResult.toString());
				 logger.info("tmpFileName:"+tmpFileName);
				 this.writeFile(tmpFileName, billList,ftpDir);
				 this.removeFiles(tmpFileName,ftpDir,localBakDir,"");
			 }
		 }
	 }
	 //删除指定目录下的文件
	 public void clearFtpFile(String ftpDir){
		File file=new File(ftpDir);
	    if(!file.exists()){
	    	file.mkdirs(); 
	    }else{
	    	/*Path path=Paths.get(ftpDir);
	    	try {
				Files.delete(path);
			} catch (IOException e) {
				e.printStackTrace();
			}*/
	    	File[] filePaths = file.listFiles();
	    	for(File f : filePaths) {
	    		if(f.isFile()) {
	    			f.delete();
	    		}
		    }
	    }
		 
	 }
	 //移动源文件到目标目录 ，不移动改成复制
	 public boolean removeFiles(String oldFileName, String sPath, String dPath, String newFileName){
		 File sfile=new File(sPath);
		 File dfile=new File(dPath);
		 Path spaths=Paths.get(sPath + oldFileName);
		 Path dpaths=Paths.get(dPath + oldFileName);
		 //创建目标文件目录
		 if(!dfile.exists()){
			 dfile.mkdirs(); 
		 }
		 File sf = new File(sPath + oldFileName);
		 if ((newFileName != null) && (newFileName.trim().length() > 0)){
			 oldFileName = newFileName;
		 }
		 File df = new File(dPath + oldFileName);
		/* if (df.exists()) {
			 logger.info("目标数据已经存在,替换掉");
			 try {
				 //Files.delete(dpaths);
				 //Files.move(spaths,dpaths,StandardCopyOption.REPLACE_EXISTING);
				 Files.copy(spaths, dpaths, StandardCopyOption.REPLACE_EXISTING);
			 } catch (IOException e) {
				 e.printStackTrace();
			 }
			 //sf.renameTo(df);
			 return false;
		 }*/
		 try {
			Files.copy(spaths, dpaths, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		 //sf.renameTo(df);
		 return true;
	  	}
	public String getFileBakPath(String localBak){
		String yearMonthStr=this.getPreMonthDay(0);
		localBak=localBak+ yearMonthStr+"/";
		return localBak;
	}
	//创建文件夹
	public void createFolder(String ftpDir,String localBak){
		File file1=new File(ftpDir);
	    if(!file1.exists()){
	    	file1.mkdirs(); 
	    }
		File file2=new File(localBak);
		if(!file2.exists()){
			file2.mkdirs(); 
	    }
	}
	
	//写文件
	public void writeFile(String fileName, List<BillData> billList,String ftpPath){
		logger.info("write files....");
		StringBuffer file = new StringBuffer(ftpPath);
	    file.append(fileName);
	    try {
	      FileOutputStream fos = new FileOutputStream(file.toString());
	      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
	      for (BillData bill : billList) {
	        String billString = this.getBillString(bill);
	        writer.write(billString);
	      }
	      writer.flush();
	      writer.close();
	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	    } catch (UnsupportedEncodingException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	  }
	//拼接话单
	public String getBillString(BillData bill ){
		String billTime=sdf2.format(bill.getCreateTime());
		String productID="ISMP2000";
		productID=productID+bill.getProductId();
		String areaCode=bill.getAreaCode().substring(1, bill.getAreaCode().length());
		String billString = "";
		billString="{\"business_number\":\""+bill.getBusinessNumber()+"\",\"pay_type\":\""+bill.getPayType()
		+"\",\"area_code\":\""+areaCode+"\",\"create_time\":\""+billTime
		+"\",\"product_id\":\""+productID+"\",\"product_name\":\""+bill.getProductName()
		+"\",\"price\":\""+bill.getPrice()+"\",\"book_type\":\""+bill.getBookType()
		+"\",\"acctype\":\""+bill.getAccType()+"\",\"Dele_code\":\""+bill.getDeleCode()
		+"\"}\r\n";
		return billString;
	}
	//获取数据总量,每5W条生成一个文件，如果小于5W条自动略过
	public int count(){
		int count = 0;
		String sqlStr=sqlCount+tableName;
		ResultSet rs = util.Query(sqlStr,null);
		try {
			while (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("count:"+count);
		return count;
	}
	//获取前一天的所有计费数据
	public List<BillData> queryBillList(int start,int size){
		logger.info("query data...");
		String sqlStr=sqlQuery+tableName;
		List<BillData> billList=new ArrayList<BillData>();
		ResultSet rs=null;
		if(start!=0||size!=0){
			sqlStr=sqlStr+" limit ?,?";
			rs=util.Query(sqlStr,start,size);
		}else{
			rs=util.Query(sqlStr,null);
		}
		try {
			return rs != null ? Conveter.convert2BeanList(rs, BillData.class) : null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		//logger.info("sql：{}",sqlStr);
		/*try {
			while(rs.next()){
				BillData bill=new BillData();
				int i=1;
				bill.setBusinessNumber(String.valueOf(rs.getObject(i++)));
				bill.setPayType(Integer.parseInt(String.valueOf(rs.getObject(i++))));
				bill.setAreaCode(String.valueOf(rs.getObject(i++)));
				bill.setCreateTime(Timestamp.valueOf(String.valueOf(rs.getObject(i++))));
				bill.setProductId(String.valueOf(rs.getObject(i++)));
				bill.setProductSpecCode(String.valueOf(rs.getObject(i++)));
				bill.setProductName(String.valueOf(rs.getObject(i++)));
				bill.setPrice(Integer.parseInt(String.valueOf(rs.getObject(i++))));
				bill.setBookType(Integer.parseInt(String.valueOf(rs.getObject(i++))));
				bill.setAccType(String.valueOf(rs.getObject(i++)));
				bill.setDeleCode(String.valueOf(rs.getObject(i++)));
				billList.add(bill);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return billList;*/
	}
	
	//获取前一天的月份
	public String getPreMonthDay(int type){
		String dayBefore="";
        Date date = null; 
		String specifiedDay=new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis());
		Calendar c = Calendar.getInstance(); 
        try {  
            date = new SimpleDateFormat("yyyy-MM-dd").parse(specifiedDay);  
        } catch (ParseException e) {  
            e.printStackTrace();  
        }  
        c.setTime(date);  
        int day = c.get(Calendar.DATE);  
        c.set(Calendar.DATE, day - 1);  
        if(type==0){
	        dayBefore = new SimpleDateFormat("yyyy-MM").format(c  
	                .getTime());  
	        dayBefore=dayBefore.replace("-", "");
        }else if(type==1){
        	dayBefore = new SimpleDateFormat("yyyyMMdd").format(c  
                    .getTime());  
        }else{
        	dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c  
                .getTime());  
        }
        return dayBefore;
	}
}
