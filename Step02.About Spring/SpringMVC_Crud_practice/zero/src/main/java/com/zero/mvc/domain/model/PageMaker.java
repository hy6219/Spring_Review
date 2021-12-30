package com.zero.mvc.domain.model;

import java.net.URI;

import org.springframework.web.util.UriComponentsBuilder;

public class PageMaker {
	private Criteria cri;
	private int totalCount;
	private int startPage;
	private int endPage;
	private boolean prev;
	private boolean next;
	private int displayPageNum=10;
	
	
	public PageMaker() {
		super();
		// TODO Auto-generated constructor stub
	}


	public PageMaker(Criteria cri, int totalCount, int startPage, int endPage, boolean prev, boolean next,
			int displayPageNum) {
		super();
		this.cri = cri;
		this.totalCount = totalCount;
		this.startPage = startPage;
		this.endPage = endPage;
		this.prev = prev;
		this.next = next;
		this.displayPageNum = displayPageNum;
	}


	public Criteria getCri() {
		return cri;
	}


	public void setCri(Criteria cri) {
		this.cri = cri;
	}


	public int getTotalCount() {
		return totalCount;
	}


	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
		calcData();//endPage 갱신
	}


	public int getStartPage() {
		return startPage;
	}


	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}


	public int getEndPage() {
		return endPage;
	}


	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}


	public boolean isPrev() {
		return prev;
	}


	public void setPrev(boolean prev) {
		this.prev=prev;
	}


	public boolean isNext() {
		return next;
	}


	public void setNext(boolean next) {
		this.next=next;
	}


	public int getDisplayPageNum() {
		return displayPageNum;
	}


	public void setDisplayPageNum(int displayPageNum) {
		this.displayPageNum = displayPageNum;
	}
	
	//페이징 처리작업을 위한 prev,next,totalCount,endPage,startPage 등 계산
	private void calcData() {
		//페이지번호 끝 계산
		this.endPage=(int)(Math.ceil(cri.getPage()/(double)this.displayPageNum)*this.displayPageNum);
		//페이지번호 시작 계산
		this.startPage=(this.endPage-this.displayPageNum)+1;
		//전체 데이터 갯수 갱신될때, endPage도 갱신될 수 있도록 setTotalCount 내부에 calcData 메서드 호출해주기!!
		//다시 계산!!(endPage)
		int tempEndPage=(int)(Math.ceil(this.totalCount/(double)cri.getPageNum()));
		
		if(this.endPage>tempEndPage) this.endPage=tempEndPage;
		
		//이전 페이지링크
		this.prev = this.startPage==1?false:true;
		//이후페이지링크: 누적 컨텐츠수 < 총 컨텐츠 수인지 파악
		this.next = cri.getPageNum() * this.endPage >= this.totalCount? false:true;
	}


	@Override
	public String toString() {
		return "PageMaker [cri=" + cri + ", totalCount=" + totalCount + ", startPage=" + startPage + ", endPage="
				+ endPage + ", prev=" + prev + ", next=" + next + ", displayPageNum=" + displayPageNum + "]";
	}
	
	
	//URI 생성
	public String makeQuery(int page) {
		URI uri=UriComponentsBuilder.newInstance()
				.queryParam("page",page)
				.queryParam("pageNum", cri.getPageNum())
				.build()
				.toUri();
		
		return uri.toString();
	}
	
}
