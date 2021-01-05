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
import com.hub.entity.UserAndSuggest;
import com.hub.service.impl.AuditLevelQueryServiceImpl;
import com.hub.service.impl.AuditPageServiceImpl;
import com.hub.service.impl.DetailsPageServiceImpl;
import com.hub.service.impl.RTysAuditPageServiceImpl;
import com.hub.service.impl.RTysAuditQueryServiceImpl;
import com.hub.service.impl.RTzfAuditPageServiceImpl;
import com.hub.service.impl.RTzfAuditQueryServiceImpl;
import com.hub.service.impl.UserServiceImpl;
import com.hub.tools.ChangeTime;
import com.hub.tools.GetEncoding;

import net.sf.json.JSONObject;
@Controller
public class RTzfController {
	@Autowired(required = false) 
	@Qualifier("UserServiceImpl")
	private UserServiceImpl userService;
	
	@Autowired(required = false) 
	@Qualifier("RTzfAuditPageServiceImpl")
	private RTzfAuditPageServiceImpl rTzfAuditPageServiceImpl;
	
	@Autowired(required = false) 
	@Qualifier("DetailsPageServiceImpl")
	private DetailsPageServiceImpl detailsPageService;
	
	@Autowired(required = false)
	@Qualifier("RTzfAuditQueryServiceImpl")
	private RTzfAuditQueryServiceImpl rTzfAuditQueryServiceImpl;
	
	//��DAOֱ�ӵ���ȡ���ݣ�û�о���Service�㣬�ӽṹ����˵����ѧ�����Ǻ�ֱ��
	@Autowired(required = false) 
	private DetailsInformationDao detailsInformationDao;
	@RequestMapping("/RTzfAudit")
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
			List<AuditPage> allAudit = rTzfAuditPageServiceImpl.getAllAudit(username, pagenum);
			map.put("audits", allAudit);
			map.put("pagenum", pagestr);
			//����δ��˵ĸ���
			int auditSize = rTzfAuditPageServiceImpl.getAuditSize(username);
			int maxpage = auditSize/10;
			String maxpagestr = maxpage+"";
			map.put("maxpage", maxpage);
		} catch (Exception e) {
			map.put("error", "�б����ݻ�ȡ�쳣");
			return "rtzf_audit";
		}
		return "rtzf_audit";
	}
	@RequestMapping("/RTzfAuditMulti")
	private String rTzfAuditMulti(Map<String, Object> map,HttpServletRequest request){
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
			List<AuditPage> allAudit = rTzfAuditPageServiceImpl.getAllAudit(username, pagenum);
			map.put("audits", allAudit);
			map.put("pagenum", pagestr);
			//����δ��˵ĸ���
			int auditSize = rTzfAuditPageServiceImpl.getAuditSize(username);
			int maxpage = auditSize/10;
			String maxpagestr = maxpage+"";
			map.put("maxpage", maxpage);
		} catch (Exception e) {
			map.put("error", "�б����ݻ�ȡ�쳣");
			return "rtzf_audit";
		}
		return "rtzf_audit_multi";
	}
	
	
	@RequestMapping("/detailsRTZF")
	private String detailsPage(HttpServletRequest request,Map<String, Object> map){
		String	checkid = request.getParameter("checkid");	
		if(checkid!=""){
			//ȡ������ϸ��Ϣ
			Rtm001 detailsInformation = detailsPageService.getRtm001DetailsInformation(checkid);
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
			//ȡexm00101��ϸ��Ϣ
			List<Rtm00101> allInformationByRtm00101 = detailsInformationDao.getAllInformationByRtm00101(checkid);
			for (Rtm00101 rtm00101item : allInformationByRtm00101) {
				String expensename = detailsInformationDao.getExpensename(rtm00101item.getDeptno());
				rtm00101item.setDeptname(expensename);
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
			if(allInformationByRtm00101!=null){
				map.put("rtm00101", allInformationByRtm00101);
			}
			
		}
		
		//ȡ�����������˵Ľ���
		ArrayList<UserAndSuggest> allPeopleSuggest = rTzfAuditQueryServiceImpl.getAllPeopleSuggest(checkid);
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
		String status = rTzfAuditQueryServiceImpl.getStatus(username, checkid);
		//�����״̬����ǰ���ж�
		if(status==null){
			map.put("atag", "0");//��ʾδ���			
		}else if(status.equals("1")){
			map.put("atag", "1");//��ʾ��ͨ��						
		}else if(status.equals("0")){
			map.put("atag", "-1");//��ʾδͨ��
		}
		return "rtzf_details";
	}
	
	
	
	@RequestMapping("/searchtoRTZF")
	public String search(HttpServletResponse response){
/*		try {
			response.sendRedirect("search.jsp");//�ض���ᵼ�´��ݵĲ�����ʧ
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		return "rtzf_search";
	}
	
	@RequestMapping("/searchByConditionRTZF")
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
		System.out.println("״̬��"+pdzt);

		List<AuditPage> allAudit=rTzfAuditPageServiceImpl.getAuditBycondition(username, pddh, pdrqstart,pdrqend, pdzt);
		int size = allAudit.size();
		map.put("rtzf_size",size);
//		System.out.println(allAudit.size());
//		Iterator<AuditPage> it = allAudit.iterator();
//		while(it.hasNext()){
//			System.out.println(it.next().toString());
//		}
		Iterator<AuditPage> it = allAudit.iterator();
		while(it.hasNext()){
			AuditPage auditPage = it.next();
			System.out.println("AAA:"+auditPage.getTotalamount());
			String datestr = auditPage.getDate();
			if(datestr!=null) {
				String[] datasplit = datestr.split(" ");
				auditPage.setDate(datasplit[0]);
			}
		}
		map.put("audits", allAudit);
		
		return "rtzf_search";
	}
	
	
	
	/**
	 * ��ذ�ť����������������
	 * @return
	 */
	@RequestMapping("/disagreeRTZF")
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
		String status = rTzfAuditQueryServiceImpl.getStatus(username, checkid);
		
		//��غ���Ҫ���ϴ����Ϣ
		if(replyMessage!=null||!replyMessage.equals("")){
			replyMessage = detailsInformationDao.getName(username)+"(�Ѵ��):"+replyMessage;
		}else{
			replyMessage = detailsInformationDao.getName(username)+"(�Ѵ��)";
		}
		
		if(username!=null&&!username.equals("")&&status==null){
			try {
				rTzfAuditQueryServiceImpl.insertSuggest(checkid, username, replyMessage,replyMessage1);
				rTzfAuditQueryServiceImpl.disagree(username, checkid);
			} catch (Exception e) {
				map.put("error", "���ʧ��");
			}	
			//status="0";
		}
		//��ѯ��ϸ��Ϣ
		if(checkid!=""){
			//ȡ������ϸ��Ϣ
			Rtm001 detailsInformation = detailsPageService.getRtm001DetailsInformation(checkid);
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
			//ȡexm00101��ϸ��Ϣ
			List<Rtm00101> allInformationByRtm00101 = detailsInformationDao.getAllInformationByRtm00101(checkid);
			for (Rtm00101 rtm00101item : allInformationByRtm00101) {
				String expensename = detailsInformationDao.getExpensename(rtm00101item.getDeptno());
				rtm00101item.setDeptname(expensename);
				//�������кͿ���
				String bankAndBankno = detailsInformationDao.getBankAndBankno(detailsInformation.getFeecompany(),rtm00101item.getFeecompany());
				if(bankAndBankno!=null&&!bankAndBankno.equals("")) {
					String[] split = bankAndBankno.split(";");
					if(split.length>1) {
						//����remark�ֶ���Ϊ����
						rtm00101item.setBank(split[0]);
						//����Invoiceno�ֶ���Ϊ�����˺�
						rtm00101item.setBankcode((split[1]));
					}
				}
			}
			if(allInformationByRtm00101!=null){
				map.put("rtm00101", allInformationByRtm00101);
			}
		}
		//ȡ�����������˵Ľ���
		ArrayList<UserAndSuggest> allPeopleSuggest = rTzfAuditQueryServiceImpl.getAllPeopleSuggest(checkid);
		map.put("allPeopleSuggest", allPeopleSuggest);
		//��ȡ���״̬
		status = rTzfAuditQueryServiceImpl.getStatus(username, checkid);
		//�����״̬����ǰ���ж�
		if(status==null){
			map.put("atag", "0");//��ʾδ���			
		}else if(status.equals("1")){
			map.put("atag", "1");//��ʾ��ͨ��						
		}else if(status.equals("0")){
			map.put("atag", "-1");//��ʾδͨ��
		}
		
		return "rtzf_details";
	}
	
	/**
	 * ͬ�ⰴť����������������
	 * @return
	 */
	@RequestMapping("/agreeRTZF")
	public String agree(Map<String, Object> map,HttpServletRequest request){
		System.out.println("����ͬ�������");
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
		String status = rTzfAuditQueryServiceImpl.getStatus(username, checkid);
		//System.out.println("״̬1��"+status);
		//ͬ�����Ҫ��ʾͬ�⡾20170517��ͬ��ɹ��ſ��Բ���"��ͬ��"����
		
		if(username!=null&&status==null){
			try {
				rTzfAuditQueryServiceImpl.insertSuggest(checkid, username, replyMessage,replyMessage1);	
				rTzfAuditQueryServiceImpl.agree(username, checkid);
			} catch (Exception e) {
				map.put("error", "ͬ��ʧ��");
			}
			//status="1";
		}
		//��ѯ��ϸ��Ϣ
		if(checkid!=""){
			//ȡ������ϸ��Ϣ
			Rtm001 detailsInformation = detailsPageService.getRtm001DetailsInformation(checkid);
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
			//ȡexm00101��ϸ��Ϣ
			List<Rtm00101> allInformationByRtm00101 = detailsInformationDao.getAllInformationByRtm00101(checkid);
			for (Rtm00101 rtm00101item : allInformationByRtm00101) {
				String expensename = detailsInformationDao.getExpensename(rtm00101item.getDeptno());
				rtm00101item.setDeptname(expensename);
				//�������кͿ���
				String bankAndBankno = detailsInformationDao.getBankAndBankno(detailsInformation.getFeecompany(),rtm00101item.getFeecompany());
				if(bankAndBankno!=null&&!bankAndBankno.equals("")) {
					String[] split = bankAndBankno.split(";");
					if(split.length>1) {
						//����remark�ֶ���Ϊ����
						rtm00101item.setBank(split[0]);
						//����Invoiceno�ֶ���Ϊ�����˺�
						rtm00101item.setBankcode((split[1]));
					}
				}
			}
			if(allInformationByRtm00101!=null){
				map.put("rtm00101", allInformationByRtm00101);
			}
			//ȡexm001�ĸ�����Ϣ��20170512���ӡ�
			
		}
		//ȡ�����������˵Ľ���
		ArrayList<UserAndSuggest> allPeopleSuggest = rTzfAuditQueryServiceImpl.getAllPeopleSuggest(checkid);
		map.put("allPeopleSuggest", allPeopleSuggest);
		
		//��ȡ���״̬
		status = rTzfAuditQueryServiceImpl.getStatus(username, checkid);
		System.out.println("״̬2��"+status);
		//�����״̬����ǰ���ж�
		if(status==null){
			map.put("atag", "0");//��ʾδ���	
		}else if(status.equals("1")){
			map.put("atag", "1");//��ʾ��ͨ��						
		}else if(status.equals("0")){
			map.put("atag", "-1");//��ʾδͨ��
		}
		return "rtzf_details";
	}
	//
	@RequestMapping("/agrees")
	public String agrees(Map<String, Object> map,HttpServletRequest request){
		System.out.println("��������ͬ�������");
		String username="";
		String replyMessage = request.getParameter("replyMessage");
		String replyMessage1 = request.getParameter("replyMessage");
		String checkids = request.getParameter("checkids");
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
				
		if(username!=null){
			try {
				rTzfAuditQueryServiceImpl.insertSuggestBatch(checkids, username, replyMessage,replyMessage1);	
				rTzfAuditQueryServiceImpl.agree(username, checkids);
			} catch (Exception e) {
				map.put("error", "ͬ��ʧ��");
			}
		}
		return "";
	}
	@RequestMapping("/disagrees")
	public String disagrees(Map<String, Object> map,HttpServletRequest request){
		String username="";
		String replyMessage = request.getParameter("replyMessage");
		String replyMessage1 = request.getParameter("replyMessage");
		String checkids = request.getParameter("checkids");
		Cookie[] userCookie = request.getCookies();
		for(Cookie cookie : userCookie){
			if(cookie.getName().equals("userLevel")){
				username = cookie.getValue();
				map.put("user",username);
				map.put("username", detailsInformationDao.getName(username));//��ȡ������
			}
		} 

		//��غ���Ҫ���ϴ����Ϣ
		if(replyMessage!=null||!replyMessage.equals("")){
			replyMessage = detailsInformationDao.getName(username)+"(�Ѵ��):"+replyMessage;
		}else{
			replyMessage = detailsInformationDao.getName(username)+"(�Ѵ��)";
		}
		
		if(username!=null&&!username.equals("")){
			try {
				rTzfAuditQueryServiceImpl.insertSuggest(checkids, username, replyMessage,replyMessage1);
				rTzfAuditQueryServiceImpl.disagree(username, checkids);
			} catch (Exception e) {
				map.put("error", "���ʧ��");
			}	
			//status="0";
		}
		return "";
	}
}


