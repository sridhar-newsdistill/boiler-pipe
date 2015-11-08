package com.kohlschutter.boilerpipe.demo;

import java.util.ArrayList;
import java.util.List;

public class ContentObject {
String title;
String description;
List<String> images=new ArrayList<>();
public String getTitle() {
	return title;
}
public void setTitle(String title) {
	this.title = title;
}
public String getDescription() {
	return description;
}
public void setDescription(String description) {
	this.description = description;
}

	
}
