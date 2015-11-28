package com.newsdistill.articleextractor;

import org.jsoup.nodes.Element;

public class NodeInfo {

	Element elements;
	int level;
	boolean hasChileds;
	public Element getElements() {
		return elements;
	}
	public void setElements(Element elements) {
		this.elements = elements;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public boolean isHasChileds() {
		return hasChileds;
	}
	public void setHasChileds(boolean hasChileds) {
		this.hasChileds = hasChileds;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((elements == null) ? 0 : elements.hashCode());
		result = prime * result + (hasChileds ? 1231 : 1237);
		result = prime * result + level;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeInfo other = (NodeInfo) obj;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		if (hasChileds != other.hasChileds)
			return false;
		if (level != other.level)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "NodeInfo [elements=" + elements + ", level=" + level
				+ ", hasChileds=" + hasChileds + "]";
	}
	
	
	
	
}
