package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName="builder")
public class CommunityFileDomain {

	
	private Integer coSeq;
	private String mbId;
	private String upOriginalFileName;
	private String upNewFileName; 
	private String upFilePath;
	private Integer upFileSize;
	
}