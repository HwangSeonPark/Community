package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName="builder")
public class CommunityContentDomain {

	private Integer coSeq;
	private String coClassification;
	private String mbId;
	private String coTitle;
	private String coContent;
	private Integer coCount;
	
}