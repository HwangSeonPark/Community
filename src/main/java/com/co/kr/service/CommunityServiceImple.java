package com.co.kr.service;


import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.co.kr.code.Code;
import com.co.kr.domain.CommunityContentDomain;
import com.co.kr.domain.CommunityFileDomain;
import com.co.kr.domain.CommunityListDomain;
import com.co.kr.exception.RequestException;
import com.co.kr.mapper.CommunityMapper;
import com.co.kr.util.CommonUtils;
import com.co.kr.vo.FileListVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class CommunityServiceImple implements CommunityService {

	@Autowired
	private CommunityMapper communityMapper;
	
	@Override
	public List<CommunityListDomain> communityList() {
		// TODO Auto-generated method stub
		return communityMapper.communityList();
	}

	@Override
	public int fileProcess(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq) {
		
		HttpSession session = httpReq.getSession();
		
		CommunityContentDomain communityContentDomain = CommunityContentDomain.builder()
				.mbId(session.getAttribute("id").toString())
				.coClassification(fileListVO.getClassification())
				.coTitle(fileListVO.getTitle())
				.coContent(fileListVO.getContent())
				.coCount(fileListVO.getCount())
				.build();
		
				if(fileListVO.getIsEdit() != null) {
					communityContentDomain.setCoSeq(Integer.parseInt(fileListVO.getSeq()));
					
					communityMapper.coContentUpdate(communityContentDomain);
				}else {	
					
					communityMapper.communitycontentUpload(communityContentDomain);
				}
				
				int coSeq = communityContentDomain.getCoSeq();
				String mbId = communityContentDomain.getMbId();
				
				List<MultipartFile> multipartFiles = request.getFiles("files");
				
				if(fileListVO.getIsEdit() != null) { 

					List<CommunityFileDomain> fileList = null;
					
					for (MultipartFile multipartFile : multipartFiles) {
						
						if(!multipartFile.isEmpty()) {   
							
							if(session.getAttribute("files") != null) {	

								fileList = (List<CommunityFileDomain>) session.getAttribute("files");
								
								for (CommunityFileDomain list : fileList) {
									list.getUpFilePath();
									Path filePath = Paths.get(list.getUpFilePath());
							 
							        try {
							        	
							            Files.deleteIfExists(filePath); 
							        
										coFileRemove(list);
										
							        } catch (DirectoryNotEmptyException e) {
										throw RequestException.fire(Code.E404, "디렉토리가 존재하지 않습니다", HttpStatus.NOT_FOUND);
							        } catch (IOException e) {
							            e.printStackTrace();
							        }
								}
								

							}
							
							
						}

					}
					
					
				}
				
				Path rootPath = Paths.get(new File("/Users/hwangseon").toString(),"community", File.separator).toAbsolutePath().normalize();			
				File pathCheck = new File(rootPath.toString());
				
				if(!pathCheck.exists()) pathCheck.mkdirs();
				
				for (MultipartFile multipartFile : multipartFiles) {
					
					if(!multipartFile.isEmpty()) { 
						
						String originalFileExtension;
						String contentType = multipartFile.getContentType();
						String origFilename = multipartFile.getOriginalFilename();
						
						if(ObjectUtils.isEmpty(contentType)){
							break;
						}else { 
							if(contentType.contains("image/jpeg")) {
								originalFileExtension = ".jpg";
							}else if(contentType.contains("image/png")) {
								originalFileExtension = ".png";
							}else {
								break;
							}
						}
						
						String uuid = UUID.randomUUID().toString();
						String current = CommonUtils.currentTime();
						String newFileName = uuid + current + originalFileExtension;
						
						Path targetPath = rootPath.resolve(newFileName);
						
						File file = new File(targetPath.toString());
						
						try {
						
							multipartFile.transferTo(file);
					
							file.setWritable(true);
							file.setReadable(true);
							
							
							CommunityFileDomain communityFileDomain = CommunityFileDomain.builder()
									.coSeq(coSeq)
									.mbId(mbId)
									.upOriginalFileName(origFilename)
									.upNewFileName("resources/communityfile/"+newFileName) 
									.upFilePath(targetPath.toString())
									.upFileSize((int)multipartFile.getSize())
									.build();
							
								communityMapper.communityfileUpload(communityFileDomain);
							
						} catch (IOException e) {
							throw RequestException.fire(Code.E404, "잘못된 업로드 파일", HttpStatus.NOT_FOUND);
						}
					}

				}
				
		
				return coSeq; 
	}

	@Override
	public void coContentRemove(HashMap<String, Object> map) {
		communityMapper.coContentRemove(map);
	}

	@Override
	public void coFileRemove(CommunityFileDomain communityFileDomain) {
		communityMapper.coFileRemove(communityFileDomain);
	}

	@Override
	public CommunityListDomain communitySelectOne(HashMap<String, Object> map) {
		return communityMapper.communitySelectOne(map);
	}

	@Override
	public List<CommunityFileDomain> communitySelectOneFile(HashMap<String, Object> map) {
		return communityMapper.communitySelectOneFile(map);
	}
	
	@Override
    public void coincreaseViewCount(int coSeq) {
        communityMapper.coincreaseViewCount(coSeq);
    }
	
	@Override
	public List<CommunityListDomain> cosearchTitle(HashMap<String, String> map) {
		return communityMapper.cosearchTitle(map);
	}

}