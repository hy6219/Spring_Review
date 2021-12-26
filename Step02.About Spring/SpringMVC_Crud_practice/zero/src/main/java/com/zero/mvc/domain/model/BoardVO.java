package com.zero.mvc.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardVO {
	private Integer bno;
	private String title;
	private String content;
	private String writer;
	private LocalDateTime regDate;
	private int viewCnt;
}
