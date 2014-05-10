package com.wcs.demo.goc.bridge.workflow;

public class DocRoute12 extends Route {
	
	public DocRoute12() {
		setName("路由：1 → 2");
	}
	
	public DocRoute12(String start, String status) {
		setName("路由：1 → 2");
		setStart(start);
		setStatus(status);
	}
	
	@Override
	public void excute() {
		
		System.out.println(getName() + " excute result " + getResult("DOC"));

	}

}
