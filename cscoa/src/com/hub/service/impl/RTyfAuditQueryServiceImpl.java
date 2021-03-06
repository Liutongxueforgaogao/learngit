package com.hub.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hub.dao.AuditLevelQueryDao;
import com.hub.dao.RTyanshouAuditLevelQueryDao;
import com.hub.dao.RTyanshouAuditPageDao;
import com.hub.dao.RTyfAuditLevelQueryDao;
import com.hub.entity.AuditLevelQuery;
import com.hub.entity.Suggestion;
import com.hub.entity.UserAndSuggest;
import com.hub.service.AuditLevelQueryService;
import com.hub.tools.SplitStr;

@Service("RTyfAuditQueryServiceImpl")
public class RTyfAuditQueryServiceImpl implements AuditLevelQueryService {

	@Autowired(required = false) 
	private RTyfAuditLevelQueryDao rTyfAuditLevelQueryDao;
	
	/**
	 * 需求一个任务：显示流程信息，就是要把流程中所有已经审核过的审批人的建议都取出来
	 * itemType=2,对应的是表taskdsh的itemtype字段
	 */
	public ArrayList<UserAndSuggest> getAllPeopleSuggest(String systemcode){
		ArrayList<UserAndSuggest> allPeopleSuggest = rTyfAuditLevelQueryDao.getAllPeopleSuggest(systemcode);
		ArrayList<UserAndSuggest> output = new ArrayList<UserAndSuggest>();
		if(allPeopleSuggest!=null){
			for (UserAndSuggest item : allPeopleSuggest) {
				if(item.getSuggest()!=""&&item.getSuggest()!=null){
					//System.out.println("b:"+item.getSuggest()+"==="+item.getUser());
					String[] hfgroup = item.getSuggest().split(";");
					//修改从数据库取出来的回复信息
					for (int i = 0; i < hfgroup.length; i++) {
						//System.out.println("a:"+hfgroup[i]);
						hfgroup[i]=SplitStr.splitSuggest(hfgroup[i]);	
						//System.out.println("6:"+hfgroup[i]);
						UserAndSuggest userSuggest = new UserAndSuggest();
						userSuggest.setUser(item.getUser());
						userSuggest.setSuggest(hfgroup[i]);
						output.add(userSuggest);
					}					
				}else{
//					尚未回复消息，不用显示内容
//					UserAndSuggest userSuggest = new UserAndSuggest();
//					userSuggest.setUser(item.getUser());
//					userSuggest.setSuggest("<font color='grey'>暂无回复</font>");
//					output.add(userSuggest);
				}
			}			
		}
		
		//System.out.println("7:"+output);
		Collections.reverse(output);
		return output;
	}
	
	
	public void insertSuggest(String checkid,String shr,String message,String message1) {
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String date = df.format(new Date());
		rTyfAuditLevelQueryDao.insertSuggest(checkid,date,shr,message, message1);
	}
	
	public AuditLevelQuery getAuditLevelQuery(String stepremarks,
			String systemcode) {
		AuditLevelQuery auditLevelQuery = rTyfAuditLevelQueryDao.getAuditLevelQuery(stepremarks, systemcode);
		return auditLevelQuery;
	}
	/**
	 * 处理从taskdsh表中得到的建议message，并将其拆开
	 */

	public Suggestion getSuggest(String checkid, String systemcode) {
		ArrayList<String> str = new ArrayList<String>();
		AuditLevelQuery auditLevelQuery = rTyfAuditLevelQueryDao.getAuditLevelQuery(checkid, systemcode);
		Suggestion suggestion = new Suggestion();
		//System.out.println("4:"+auditLevelQuery.getStepremarks());
		//设置审核人的名字
		suggestion.setUserLevelname(auditLevelQuery.getStepremarks());
		//设置审核人的回复信息
		String suggestionAndTime = auditLevelQuery.getSuggestionAndTime();
		if(suggestionAndTime!=null){
			String[] hfgroup = suggestionAndTime.split(";");
			//修改从数据库取出来的回复信息
			for (int i = 0; i < hfgroup.length; i++) {
				hfgroup[i]=SplitStr.splitSuggest(hfgroup[i]);	
				//System.out.println(hfgroup[i]);
			}
			ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(hfgroup));
			suggestion.setSuggest(arrayList);
		}
		//System.out.println(suggestion);
		return suggestion;
	}

	/**
	 * 打回和同意操作
	 */
	
	public void disagree(String userLevel, String systemcode) {
		rTyfAuditLevelQueryDao.disagree(userLevel, systemcode);
	}

	
	public void agree(String userLevel, String systemcode) {
		rTyfAuditLevelQueryDao.agree(userLevel, systemcode);
	}


	
	public String getStatus(String username, String systemcode) {
		String status = rTyfAuditLevelQueryDao.getStatus(username, systemcode);
		return status;
	}

}
