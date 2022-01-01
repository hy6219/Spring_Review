package com.zero.mvc.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria extends Criteria{
	//검색종류
	private String searchType;
	//검색 키워드
	private String keyword;	
}
