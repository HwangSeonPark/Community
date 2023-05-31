package com.co.kr.controller;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.co.kr.code.Code;
import com.co.kr.domain.CommunityFileDomain;
import com.co.kr.domain.CommunityListDomain;
import com.co.kr.exception.RequestException;
import com.co.kr.service.CommunityService;
import com.co.kr.util.CommonUtils;
import com.co.kr.vo.FileListVO;

import groovyjarjarpicocli.CommandLine.Model;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class CommunityController {
	
	@Autowired
	private CommunityService communityService;

	
	@PostMapping(value = "coupload")
	public ModelAndView coUpload(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq) throws IOException, ParseException {
		
		ModelAndView mav = new ModelAndView();
		int coSeq = communityService.fileProcess(fileListVO, request, httpReq);
		fileListVO.setContent("");
		fileListVO.setTitle("");
		fileListVO.setClassification("");

		mav = coSelectOneCall(fileListVO, String.valueOf(coSeq),request);
		mav.setViewName("community/list.html");
		return mav;
		
	}

	public ModelAndView coSelectOneCall(@ModelAttribute("fileListVO") FileListVO fileListVO, String coSeq, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		
		map.put("coSeq", Integer.parseInt(coSeq));
		CommunityListDomain communityListDomain =communityService.communitySelectOne(map);
		System.out.println("communityListDomain"+communityListDomain);
		List<CommunityFileDomain> fileList =  communityService.communitySelectOneFile(map);
		
		for (CommunityFileDomain list : fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}
		mav.addObject("codetail", communityListDomain);
		mav.addObject("cocodetail", communityListDomain);
		mav.addObject("files", fileList);

		
		session.setAttribute("files", fileList);

		return mav;
	
	}
	
	
	@GetMapping("codetail")
	public ModelAndView coDetail(@ModelAttribute("fileListVO") FileListVO fileListVO, @RequestParam("coSeq") String coSeq, HttpServletRequest request) throws IOException {
	    ModelAndView mav = new ModelAndView();
 
	    communityService.coincreaseViewCount(Integer.parseInt(coSeq));
  
	    mav = coSelectOneCall(fileListVO, coSeq, request);
	    mav.setViewName("community/communityindex.html");
	    return mav;
	}

	
	@GetMapping("cocodetail")
    public ModelAndView cocoDetail(@ModelAttribute("fileListVO") FileListVO fileListVO, @RequestParam("coSeq") String coSeq, HttpServletRequest request) throws IOException {
		ModelAndView mav = new ModelAndView();

	    communityService.coincreaseViewCount(Integer.parseInt(coSeq));

		mav = coSelectOneCall(fileListVO, coSeq,request);
		mav.setViewName("community/list.html");
		return mav;
	}
	
	@GetMapping("coedit")
	public ModelAndView coedit(FileListVO fileListVO, @RequestParam("coSeq") String coSeq, HttpServletRequest request) throws IOException {
		ModelAndView mav = new ModelAndView();

		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		
		map.put("coSeq", Integer.parseInt(coSeq));
		CommunityListDomain communityListDomain =communityService.communitySelectOne(map);
		List<CommunityFileDomain> fileList =  communityService.communitySelectOneFile(map);
		
		for (CommunityFileDomain list : fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}

		fileListVO.setSeq(communityListDomain.getCoSeq());
		fileListVO.setContent(communityListDomain.getCoContent());
		fileListVO.setTitle(communityListDomain.getCoTitle());
		fileListVO.setClassification(communityListDomain.getCoClassification());
		fileListVO.setIsEdit("edit");  
		
	
		mav.addObject("detail", communityListDomain);
		mav.addObject("files", fileList);
		mav.addObject("fileLen",fileList.size());
		
		mav.setViewName("community/communityEditList.html");
		return mav;
	}
	
	@PostMapping("coeditSave")
	public ModelAndView coeditSave(@ModelAttribute("fileListVO") FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq) throws IOException {
		ModelAndView mav = new ModelAndView();
		
		communityService.fileProcess(fileListVO, request, httpReq);
		
		mav = coSelectOneCall(fileListVO, fileListVO.getSeq(),request);
		fileListVO.setContent(""); 
		fileListVO.setTitle(""); 
		fileListVO.setClassification(""); 
		mav.setViewName("community/list.html");
		return mav;
	}
	
	@GetMapping("coremove")
	public ModelAndView coRemove(@RequestParam("coSeq") String coSeq, HttpServletRequest request) throws IOException {
		ModelAndView mav = new ModelAndView();
		
		HttpSession session = request.getSession();
		HashMap<String, Object> map = new HashMap<String, Object>();
		List<CommunityFileDomain> fileList = null;
		if(session.getAttribute("files") != null) {						
			fileList = (List<CommunityFileDomain>) session.getAttribute("files");
		}

		map.put("coSeq", Integer.parseInt(coSeq));
		
		communityService.coContentRemove(map);

		for (CommunityFileDomain list : fileList) {
			list.getUpFilePath();
			Path filePath = Paths.get(list.getUpFilePath());
	 
	        try {
	        	
	            Files.deleteIfExists(filePath); 
	
							communityService.coFileRemove(list);
				
	        } catch (DirectoryNotEmptyException e) {
							throw RequestException.fire(Code.E404, "디렉토리가 존재하지 않습니다", HttpStatus.NOT_FOUND);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}

		session.removeAttribute("files"); 
		mav =coListCall();
		mav.setViewName("community/list.html");
		
		return mav;
	}

	public ModelAndView coListCall() {
		ModelAndView mav = new ModelAndView();
		List<CommunityListDomain> items = communityService.communityList();
		mav.addObject("items", items);
		return mav;
	}
	
	@RequestMapping(value="cosearchContent1")
	   public ModelAndView cosearchContent1(HttpServletRequest request) {
	      ModelAndView mav = new ModelAndView();
	      HashMap<String, String> map = new HashMap<String, String>();
	      map.put("coTitle",request.getParameter("cosearchtitle"));
	      System.out.println("cosearchtitle : "+request.getParameter("cosearchtitle"));
	      List<CommunityListDomain> items = communityService.cosearchTitle(map);
	      System.out.println("items ==> " + items);
	      mav.addObject("items", items);
	      mav.setViewName("community/communityindex.html");
	      return mav;
	   }
	
	@RequestMapping(value="cosearchContent2")
	   public ModelAndView cosearchContent2(HttpServletRequest request) {
	      ModelAndView mav = new ModelAndView();
	      HashMap<String, String> map = new HashMap<String, String>();
	      map.put("coTitle",request.getParameter("cosearchtitle"));
	      System.out.println("cosearchtitle : "+request.getParameter("cosearchtitle"));
	      List<CommunityListDomain> items = communityService.cosearchTitle(map);
	      System.out.println("items ==> " + items);
	      mav.addObject("items", items);
	      mav.setViewName("community/list.html");
	      return mav;
	   }
	

}