package com.hub.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hub.dao.AuditPageDao;
import com.hub.dao.RTyanshouAuditPageDao;
import com.hub.dao.RTzfAuditPageDao;
import com.hub.entity.AuditPage;
import com.hub.service.AuditPageService;

@Service("RTzfAuditPageServiceImpl")
public class RTzfAuditPageServiceImpl implements AuditPageService {

	@Autowired(required = false) 
	private RTzfAuditPageDao rTzfAuditPageDao;
	
	public List<AuditPage> getAllAudit(String username,int pagenum) {
		List<AuditPage> allAudit = rTzfAuditPageDao.getAllAudit(username,pagenum);
		return allAudit;
	}	
	 public int getAuditSize(String username){
		 int auditSize = rTzfAuditPageDao.getAuditSize(username);
		 return auditSize;
	 }
	 //获取多部门未审核个数
	 public int getMultiAuditSize(String username){
		 
		 return 0;
	 }
	 //获交通未审核个数
	 public int getTrafficAuditSize(String username){
		 
		 return 0;
	 }
	 //获取差旅未审核个数
	 public int getTravelAuditSize(String username){
		 
		 return 0;
	 }
	 //获取暂付款未审核个数
	 public int getTempAuditSize(String username){
		 
		 return 0;
	 }
	
	public List<AuditPage> getAuditBycondition(String username, String pddh, String pdrqstart,String pdrqend, String pdzt) {
		// TODO Auto-generated method stub
		List<AuditPage> auditResult = rTzfAuditPageDao.getAuditBycondition(username, pddh, pdrqstart,pdrqend, pdzt);
		return auditResult;
	}
	
}
