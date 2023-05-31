package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName="builder")
public class CommunityListDomain {

	private String coSeq;
	private String mbId;
	private String coClassification;
	private String coTitle;
	private String coContent;
	private String coCreateAt;
	private String coUpdateAt;
	private Integer coCount;

}