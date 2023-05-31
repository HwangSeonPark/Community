package com.co.kr.service;


import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.co.kr.domain.CommunityFileDomain;
import com.co.kr.domain.CommunityListDomain;
import com.co.kr.vo.FileListVO;

public interface CommunityService {
	
	
	public List<CommunityListDomain> communityList();
	
	public int fileProcess(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq);
	
	public void coContentRemove(HashMap<String, Object> map);
	
	public void coFileRemove(CommunityFileDomain CommunityFileDomain);

	public CommunityListDomain communitySelectOne(HashMap<String, Object> map);
	
	public List<CommunityFileDomain> communitySelectOneFile(HashMap<String, Object> map);
	
	public void coincreaseViewCount(int bdSeq);

	public List<CommunityListDomain> cosearchTitle(HashMap<String, String> map);

}