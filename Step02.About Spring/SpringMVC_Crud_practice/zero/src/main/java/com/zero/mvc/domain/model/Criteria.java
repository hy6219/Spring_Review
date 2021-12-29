package com.zero.mvc.domain.model;

public class Criteria {
	/**
	 * @author gs813
	 * page: 페이지값
	 * pageNum:한 페이지에 보여질 아이템수
	 * 나중에 조건이 붙는다면 이 클래스에 추가하면 됨!
	 * */
	private int page;
	private int pageNum;
	private int pageStart;
	
	public Criteria() {
		super();
		// TODO Auto-generated constructor stub
		this.page=1;//초기값 1로 고정~
		this.pageNum=10;//초기값 10으로 고정~
	}
	



	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		if(page<=0) {
			this.page=1;
			return;
		}
		
		this.page = page;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		if(pageNum <=0 || pageNum>100) {
			this.pageNum=10;//한 페이지당 아이템은 [0,100]을 벗어나지 못하도록
			return;
		}
		
		this.pageNum = pageNum;
	}
	
	//시작 인덱스를 반환해주기
	public int getPageStart() {
		return (this.page-1)*this.pageNum;
	}
	
	

	public void setPageStart(int pageStart) {
		this.pageStart = pageStart;
	}




	@Override
	public String toString() {
		return "Criteria [page=" + page + ", pageNum=" + pageNum + "]";
	}
	
	
	
}
