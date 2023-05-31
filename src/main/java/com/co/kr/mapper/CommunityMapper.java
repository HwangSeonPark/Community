package com.co.kr.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.co.kr.domain.CommunityContentDomain;
import com.co.kr.domain.CommunityFileDomain;
import com.co.kr.domain.CommunityListDomain;
@Mapper
public interface CommunityMapper {

	
	public List<CommunityListDomain> communityList();
	
	public void communitycontentUpload(CommunityContentDomain communityContentDomain);
	
	public void communityfileUpload(CommunityFileDomain communityFileDomain);

	public void coContentUpdate(CommunityContentDomain communityContentDomain);
	
	public void coFileUpdate(CommunityFileDomain communityFileDomain);

	public void coContentRemove(HashMap<String, Object> map);
	
	public void coFileRemove(CommunityFileDomain communityFileDomain);
	
	public CommunityListDomain communitySelectOne(HashMap<String, Object> map);

	public List<CommunityFileDomain> communitySelectOneFile(HashMap<String, Object> map);
	
	public void coincreaseViewCount(int coSeq);
	
	public List<CommunityListDomain> cosearchTitle(HashMap<String, String> map);
    
}