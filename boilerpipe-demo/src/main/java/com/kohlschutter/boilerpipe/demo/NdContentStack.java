package com.kohlschutter.boilerpipe.demo;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NdContentStack {
	private List<String> tagInfo = new LinkedList<String>();
	private Map<String, Integer> contentSkipCount = new LinkedHashMap<String, Integer>();

	public int getTop() {
		int val=tagInfo.size()-1;
		return val;
	}

	public boolean isEmpty() {
	if(	tagInfo.size()==0)
	{
		return true;
	}
	else{
		return false;
	}
	}

	public boolean isFull() {
		return false;
	}

	public void push(String tagPlusPos) {
		if (isFull()) {
			throw new RuntimeException();
		}
	this.tagInfo.add(tagPlusPos);
		//System.out.println(this.tagInfo.toString());
	}

	public String pop() {
		if (this.isEmpty()) {
			throw new RuntimeException();
		}
		int topPos = this.getTop();
		
		return tagInfo.remove(topPos);
	}

	public List<String> getTagInfo() {
		return tagInfo;
	}

	public void setTagInfo(List<String> tagInfo) {
		this.tagInfo = tagInfo;
	}

	public Map<String, Integer> getContentSkipCount() {
		return contentSkipCount;
	}

	public void setContentSkipCount(Map<String, Integer> contentSkipCount) {
		this.contentSkipCount = contentSkipCount;
	}
	
	

}
