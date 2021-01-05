package com.hub.web;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hub.dao.DetailsInformationDao;
import com.hub.entity.AuditPage;
import com.hub.entity.DetailsInformation;
import com.hub.entity.InformationExm00101;
import com.hub.entity.InfromationOfExm001Addition;
import com.hub.entity.Rtm001;
import com.hub.entity.Rtm00101;
import com.hub.entity.Rtm002;
import com.hub.entity.Rtm00201;
import com.hub.entity.UserAndSuggest;
import com.hub.service.impl.AuditLevelQueryServiceImpl;
import com.hub.service.impl.AuditPageServiceImpl;
import com.hub.service.impl.DetailsPageServiceImpl;
import com.hub.service.impl.RTyfAuditPageServiceImpl;
import com.hub.service.impl.RTyfAuditQueryServiceImpl;
import com.hub.service.impl.RTysAuditPageServiceImpl;
import com.hub.service.impl.RTysAuditQueryServiceImpl;
import com.hub.service.impl.RTzfAuditPageServiceImpl;
import com.hub.service.impl.RTzfAuditQueryServiceImpl;
import com.hub.service.impl.UserServiceImpl;
import com.hub.service.impl.ZjzfAuditPageServiceImpl;
import com.hub.service.impl.ZjzfAuditQueryServiceImpl;
import com.hub.tools.ChangeTime;
import com.hub.tools.GetEncoding;

import net.sf.json.JSONObject;
@Controller
public class ZjzfController {
	@Autowired(required = false) 
	@Qualifier("UserServiceImpl")
	private UserServiceImpl userService;
	
	@Autowired(required = false) 
	@Qualifier("ZjzfAuditPageServiceImpl")
	private ZjzfAuditPageServiceImpl zjzfAuditPageServiceImpl;
	
	@Autowired(required = false) 
	@Qualifier("DetailsPageServiceImpl")
	private DetailsPageServiceImpl detailsPageService;
	
	@Autowired(required = false)
	@Qualifier("ZjzfAuditQueryServiceImpl")
	private ZjzfAuditQueryServiceImpl zjzfAuditQueryServiceImpl;
	
	//��DAOֱ�ӵ���ȡ���ݣ�û�о���Service�㣬�ӽṹ����˵����ѧ�����Ǻ�ֱ��
	@Autowired(required = false) 
	private DetailsInformationDao detailsInformationDao;
	@RequestMapping("/ZjzfAudit")
	private String auditPage(Map<String, Object> map,HttpServletRequest request){
		String username="";
		try {
			String pagestr = request.getParameter("pagenum");
			int pagenum = Integer.parseInt(pagestr);
			Cookie[] userCookie = request.getCookies();
			for(Cookie cookie : userCookie){
			    if(cookie.getName().equals("userLevel")){
			        username = cookie.getValue();
			        map.put("user", username);
			        map.put("username", detailsInformationDao.getName(username));//��ȡ������
			    }
			}
			List<AuditPage> allAudit = zjzfAuditPageServiceImpl.getAllAudit(username, pagenum);
			map.put("audits", allAudit);
			map.put("pagenum", pagestr);
			//����δ��˵ĸ���
			int auditSize = zjzfAuditPageServiceImpl.getAuditSize(username);
			int maxpage = auditSize/10;
			String maxpagestr = maxpage+"";
			map.put("maxpage", maxpage);
		} catch (Exception e) {
			map.put("error", "�б����ݻ�ȡ�쳣");
			return "zjzf_audit";
		}
		return "zjzf_audit";
	}
	
	
	@RequestMapping("/detailsZjzf")
	private String detailsPage(HttpServletRequest request,Map<String, Object> map){
		String	checkid = request.getParameter("checkid");	
		if(checkid!=""){
			//ȡ������ϸ��Ϣ
			Rtm002 detailsInformation = detailsPageService.getRtm002DetailsInformation(checkid);
			map.put("sqrname", detailsInformationDao.getName(detailsInformation.getStaff()));//��ȡ������������
			
			if(detailsInformation!=null){
				detailsInformation.setInputdate(ChangeTime.formatTime(detailsInformation.getInputdate()));
				detailsInformation.setImage(UserController.imgbase+detailsInformation.getImage());
				detailsInformation.setImagetwo(UserController.imgbase+detailsInformation.getImagetwo());
				detailsInformation.setImagethree(UserController.imgbase+detailsInformation.getImagethree());
				detailsInformation.setImagefour(UserController.imgbase+detailsInformation.getImagefour());
				detailsInformation.setImagefive(UserController.imgbase+detailsInformation.getImagefive());
				//���ݹ�˾����revcompany���ҹ�˾����
				String revcompanyname = detailsInformationDao.getRevcompanyname(detailsInformation.getRevcompany(),detailsInformation.getFeecompany());
				detailsInformation.setRevcompanyname(revcompanyname);
				//���ݴ����֧����ʽ
				if(detailsInformation.getPaytype()!=null) {
					String paytype = detailsInformationDao.getPaytype(detailsInformation.getPaytype());
					detailsInformation.setPaytype(paytype);
				}
				//
				if(detailsInformation.getFeekink()!=null) {
					String expensename = detailsInformationDao.getExpensename(detailsInformation.getFeekink());
					detailsInformation.setFeekink(expensename);
				}
				
				map.put("details", detailsInformation);
			}
			//ȡrtm00101��ϸ��Ϣ
			List<Rtm00201> allInformationByRtm00201 = detailsInformationDao.getAllInformationByRtm00201(checkid);
			for (Rtm00201 rtm00101item : allInformationByRtm00201) {
				String subjectname = detailsInformationDao.getExpensename(rtm00101item.getSubjectcode());
				rtm00101item.setSubjectname(subjectname);
				//�������кͿ���
				String bankAndBankno = detailsInformationDao.getBankAndBankno(detailsInformation.getFeecompany(),rtm00101item.getFeecompany());
				if(bankAndBankno!=null&&!bankAndBankno.equals("")) {
					String[] split = bankAndBankno.split(";");
					if(split.length>1) {
						//invoiceno
						rtm00101item.setBank(split[0]);
						//����remark�ֶ���Ϊ����
						//rtm00101item.setRemark(split[0]);
						//����Invoiceno�ֶ���Ϊ�����˺�
						rtm00101item.setInvoiceno((split[1]));
					}
				}
			}
		
			if(allInformationByRtm00201!=null){
				map.put("rtm00201", allInformationByRtm00201);
			}
			
		}
		
		//ȡ�����������˵Ľ���
		ArrayList<UserAndSuggest> allPeopleSuggest = zjzfAuditQueryServiceImpl.getAllPeopleSuggest(checkid);
		map.put("allPeopleSuggest", allPeopleSuggest);
		
		//��ȡ���״̬
		String username ="";
		Cookie[] userCookie = request.getCookies();
		for(Cookie cookie : userCookie){
	        if(cookie.getName().equals("userLevel")){
	            username = cookie.getValue();
	            map.put("user", username);
	            map.put("username", detailsInformationDao.getName(username));//��ȡ������
	        }
	    }
		String status = zjzfAuditQueryServiceImpl.getStatus(username, checkid);
		//�����״̬����ǰ���ж�
		if(status==null){
			map.put("atag", "0");//��ʾδ���			
		}else if(status.equals("1")){
			map.put("atag", "1");//��ʾ��ͨ��						
		}else if(status.equals("0")){
			map.put("atag", "-1");//��ʾδͨ��
		}
		return "zjzf_details";
	}
	
	
	
	@RequestMapping("/searchtoZjzf")
	public String search(HttpServletResponse response){
/*		try {
			response.sendRedirect("search.jsp");//�ض���ᵼ�´��ݵĲ�����ʧ
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		return "zjzf_search";
	}
	
	@RequestMapping("/searchByConditionZjzf")
	public String searchByCondition (Map<String, Object> map,HttpServletRequest request){
		String pddh = null;//��˵���
		String pdrqstart = null;//������ڿ�ʼ
		String pdrqend = null;//������ڿ�ʼ
		String pdzt = null;//���״̬
		String username = "";//�û���
		try {
			String  stuname = request.getParameter("condition");
			System.out.println("��Ҫ��ѯ��״̬��"+stuname);
			String str = new String(stuname.getBytes("ISO-8859-1"),"utf-8");
			JSONObject jb = new JSONObject();
			pddh = (String) jb.fromObject(str).get("pddh").toString();
			pdrqstart = (String) jb.fromObject(str).get("pdrqstart").toString();
			pdrqend = (String) jb.fromObject(str).get("pdrqend").toString();
			
			pdrqstart=pdrqstart+" 00:00:00";//����������ڿ�ʼ
			pdrqend=pdrqend+" 23:59:59";//����������ڽ���
//			pdzt = (String) jb.fromObject(str).get("pdkwv");
			pdzt = (String) jb.fromObject(stuname).get("pdkwv");//����������Ҫ��Ϊ�������
		/*	if(pdzt==null){//��֪��Ϊʲô�����ݿ�鲻����
				pdzt="δͨ��";
			}*/
			pdzt=pdzt+"";
			Cookie[] userCookie = request.getCookies();
			for(Cookie cookie : userCookie){
			    if(cookie.getName().equals("userLevel")){
			        username = cookie.getValue();
			        map.put("user", username);
			        map.put("username", detailsInformationDao.getName(username));//��ȡ������
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<AuditPage> allAudit=zjzfAuditPageServiceImpl.getAuditBycondition(username, pddh, pdrqstart,pdrqend, pdzt);
		int size = allAudit.size();
		map.put("zjzf_size",size);
//		System.out.println(allAudit.size());
//		Iterator<AuditPage> it = allAudit.iterator();
//		while(it.hasNext()){
//			System.out.println(it.next().toString());
//		}
		Iterator<AuditPage> it = allAudit.iterator();
		while(it.hasNext()){
			AuditPage auditPage = it.next();
			String datestr = auditPage.getDate();
			if(datestr!=null) {
				String[] datasplit = datestr.split(" ");
				auditPage.setDate(datasplit[0]);
			}
		}
		map.put("audits", allAudit);
		
		return "zjzf_search";
	}
	
	
	
	/**
	 * ��ذ�ť����������������
	 * @return
	 */
	@RequestMapping("/disagreeZjzf")
	public String disagree(Map<String, Object> map,HttpServletRequest request){
		//System.out.println("�����ؿ�����");
		String username="";
		String replyMessage = request.getParameter("replyMessage");
		String replyMessage1 = request.getParameter("replyMessage");

		String checkid = request.getParameter("checkid");
		Cookie[] userCookie = request.getCookies();
		for(Cookie cookie : userCookie){
	        if(cookie.getName().equals("userLevel")){
	        	username = cookie.getValue();
	        	map.put("user",username);
	        	map.put("username", detailsInformationDao.getName(username));//��ȡ������
	        }
	    } 
		//��ȡ���״̬
		String status = zjzfAuditQueryServiceImpl.getStatus(username, checkid);
		
		//��غ���Ҫ���ϴ����Ϣ
		if(replyMessage!=null||!replyMessage.equals("")){
			replyMessage = detailsInformationDao.getName(username)+"(�Ѵ��):"+replyMessage;
		}else{
			replyMessage = detailsInformationDao.getName(username)+"(�Ѵ��)";
		}
		
		if(username!=null&&!username.equals("")&&status==null){
			try {
				zjzfAuditQueryServiceImpl.insertSuggest(checkid, username, replyMessage,replyMessage1);
				zjzfAuditQueryServiceImpl.disagree(username, checkid);
			} catch (Exception e) {
				map.put("error", "���ʧ��");
			}	
			//status="0";
		}
		//��ѯ��ϸ��Ϣ
		if(checkid!=""){
			//ȡ������ϸ��Ϣ
			Rtm002 detailsInformation = detailsPageService.getRtm002DetailsInformation(checkid);
			map.put("sqrname", detailsInformationDao.getName(detailsInformation.getStaff()));//��ȡ������������
			
			if(detailsInformation!=null){
				detailsInformation.setInputdate(ChangeTime.formatTime(detailsInformation.getInputdate()));
				detailsInformation.setImage(UserController.imgbase+detailsInformation.getImage());
				detailsInformation.setImagetwo(UserController.imgbase+detailsInformation.getImagetwo());
				detailsInformation.setImagethree(UserController.imgbase+detailsInformation.getImagethree());
				detailsInformation.setImagefour(UserController.imgbase+detailsInformation.getImagefour());
				detailsInformation.setImagefive(UserController.imgbase+detailsInformation.getImagefive());
				//���ݹ�˾����revcompany���ҹ�˾����
				String revcompanyname = detailsInformationDao.getRevcompanyname(detailsInformation.getRevcompany(),detailsInformation.getFeecompany());
				detailsInformation.setRevcompanyname(revcompanyname);
				//���ݴ����֧����ʽ
				if(detailsInformation.getPaytype()!=null) {
					String paytype = detailsInformationDao.getPaytype(detailsInformation.getPaytype());
					detailsInformation.setPaytype(paytype);
				}
				//
				if(detailsInformation.getFeekink()!=null) {
					String expensename = detailsInformationDao.getExpensename(detailsInformation.getFeekink());
					detailsInformation.setFeekink(expensename);
				}
				
				map.put("details", detailsInformation);
			}
			//ȡrtm00101��ϸ��Ϣ
			List<Rtm00201> allInformationByRtm00201 = detailsInformationDao.getAllInformationByRtm00201(checkid);
			for (Rtm00201 rtm00101item : allInformationByRtm00201) {
				String subjectname = detailsInformationDao.getExpensename(rtm00101item.getSubjectcode());
				rtm00101item.setSubjectname(subjectname);
				//�������кͿ���
				String bankAndBankno = detailsInformationDao.getBankAndBankno(detailsInformation.getFeecompany(),rtm00101item.getFeecompany());
				if(bankAndBankno!=null&&!bankAndBankno.equals("")) {
					String[] split = bankAndBankno.split(";");
					if(split.length>1) {
						//����remark�ֶ���Ϊ����
						rtm00101item.setRemark(split[0]);
						//����Invoiceno�ֶ���Ϊ�����˺�
						rtm00101item.setInvoiceno((split[1]));
					}
				}
			}
		
			if(allInformationByRtm00201!=null){
				map.put("rtm00201", allInformationByRtm00201);
			}
			
		}
		//ȡ�����������˵Ľ���
		ArrayList<UserAndSuggest> allPeopleSuggest = zjzfAuditQueryServiceImpl.getAllPeopleSuggest(checkid);
		map.put("allPeopleSuggest", allPeopleSuggest);
		//��ȡ���״̬
		status = zjzfAuditQueryServiceImpl.getStatus(username, checkid);
		//�����״̬����ǰ���ж�
		if(status==null){
			map.put("atag", "0");//��ʾδ���			
		}else if(status.equals("1")){
			map.put("atag", "1");//��ʾ��ͨ��						
		}else if(status.equals("0")){
			map.put("atag", "-1");//��ʾδͨ��
		}
		
		return "zjzf_details";
	}
	
	/**
	 * ͬ�ⰴť����������������
	 * @return
	 */
	@RequestMapping("/agreeZjzf")
	public String agree(Map<String, Object> map,HttpServletRequest request){
		String username="";
		String replyMessage = request.getParameter("replyMessage");
		String replyMessage1 = request.getParameter("replyMessage");
		String checkid = request.getParameter("checkid");
		Cookie[] userCookie = request.getCookies();
		for(Cookie cookie : userCookie){
			if(cookie.getName().equals("userLevel")){
				username = cookie.getValue();
				map.put("user",username);
				map.put("username", detailsInformationDao.getName(username));//��ȡ������
			}
		} 

		//ͬ�����Ҫ���ϴ����Ϣ
		if(replyMessage!=null||!replyMessage.equals("")){
			replyMessage = detailsInformationDao.getName(username)+"(��ͬ��):"+replyMessage;
		}else{
			replyMessage = detailsInformationDao.getName(username)+"(��ͬ��)";
		}
				
		//��ȡ���״̬
		String status = zjzfAuditQueryServiceImpl.getStatus(username, checkid);
		//System.out.println("״̬1��"+status);
		//ͬ�����Ҫ��ʾͬ�⡾20170517��ͬ��ɹ��ſ��Բ���"��ͬ��"����
		
		if(username!=null&&status==null){
			try {
				zjzfAuditQueryServiceImpl.insertSuggest(checkid, username, replyMessage,replyMessage1);	
				zjzfAuditQueryServiceImpl.agree(username, checkid);
			} catch (Exception e) {
				map.put("error", "ͬ��ʧ��");
			}
			//status="1";
		}
		//��ѯ��ϸ��Ϣ
		if(checkid!=""){
			//ȡ������ϸ��Ϣ
			Rtm002 detailsInformation = detailsPageService.getRtm002DetailsInformation(checkid);
			map.put("sqrname", detailsInformationDao.getName(detailsInformation.getStaff()));//��ȡ������������
			
			if(detailsInformation!=null){
				detailsInformation.setInputdate(ChangeTime.formatTime(detailsInformation.getInputdate()));
				detailsInformation.setImage(UserController.imgbase+detailsInformation.getImage());
				detailsInformation.setImagetwo(UserController.imgbase+detailsInformation.getImagetwo());
				detailsInformation.setImagethree(UserController.imgbase+detailsInformation.getImagethree());
				detailsInformation.setImagefour(UserController.imgbase+detailsInformation.getImagefour());
				detailsInformation.setImagefive(UserController.imgbase+detailsInformation.getImagefive());
				//���ݹ�˾����revcompany���ҹ�˾����
				String revcompanyname = detailsInformationDao.getRevcompanyname(detailsInformation.getRevcompany(),detailsInformation.getFeecompany());
				detailsInformation.setRevcompanyname(revcompanyname);
				//���ݴ����֧����ʽ
				if(detailsInformation.getPaytype()!=null) {
					String paytype = detailsInformationDao.getPaytype(detailsInformation.getPaytype());
					detailsInformation.setPaytype(paytype);
				}
				//
				if(detailsInformation.getFeekink()!=null) {
					String expensename = detailsInformationDao.getExpensename(detailsInformation.getFeekink());
					detailsInformation.setFeekink(expensename);
				}
				
				map.put("details", detailsInformation);
			}
			//ȡrtm00101��ϸ��Ϣ
			List<Rtm00201> allInformationByRtm00201 = detailsInformationDao.getAllInformationByRtm00201(checkid);
			for (Rtm00201 rtm00101item : allInformationByRtm00201) {
				String subjectname = detailsInformationDao.getExpensename(rtm00101item.getSubjectcode());
				rtm00101item.setSubjectname(subjectname);
				//�������кͿ���
				String bankAndBankno = detailsInformationDao.getBankAndBankno(detailsInformation.getFeecompany(),rtm00101item.getFeecompany());
				if(bankAndBankno!=null&&!bankAndBankno.equals("")) {
					String[] split = bankAndBankno.split(";");
					if(split.length>1) {
						//����remark�ֶ���Ϊ����
						rtm00101item.setRemark(split[0]);
						//����Invoiceno�ֶ���Ϊ�����˺�
						rtm00101item.setInvoiceno((split[1]));
					}
				}
			}
		
			if(allInformationByRtm00201!=null){
				map.put("rtm00201", allInformationByRtm00201);
			}
			
		}
		//ȡ�����������˵Ľ���
		ArrayList<UserAndSuggest> allPeopleSuggest = zjzfAuditQueryServiceImpl.getAllPeopleSuggest(checkid);
		map.put("allPeopleSuggest", allPeopleSuggest);
		
		//��ȡ���״̬
		status = zjzfAuditQueryServiceImpl.getStatus(username, checkid);
		//�����״̬����ǰ���ж�
		if(status==null){
			map.put("atag", "0");//��ʾδ���	
		}else if(status.equals("1")){
			map.put("atag", "1");//��ʾ��ͨ��						
		}else if(status.equals("0")){
			map.put("atag", "-1");//��ʾδͨ��
		}
		return "zjzf_details";
	}
}


