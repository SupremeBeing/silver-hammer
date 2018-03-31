/*
 * Copyright (c) 2017, Dmitriy Shchekotin
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package ru.silverhammer.core.metadata;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CategoryAttributes implements Iterable<GroupAttributes> {

	private final int id;
	private final List<GroupAttributes> groups = new ArrayList<>();

	private String caption;
	private String description;
	private String iconPath;
	private char mnemonic = 0;

	public CategoryAttributes(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public String getCaption() {
		return caption;
	}
	
	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String getIconPath() {
		return iconPath;
	}
	
	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public char getMnemonic() {
		return mnemonic;
	}
	
	public void setMnemonic(char mnemonic) {
		this.mnemonic = mnemonic;
	}
	
	public void addGroupAttributes(GroupAttributes attributes) {
		if (attributes != null && !groups.contains(attributes)) {
			groups.add(attributes);
		}
	}
	
	public void removeGroupAttributes(GroupAttributes attributes) {
		groups.remove(attributes);
	}

	@Override
	public Iterator<GroupAttributes> iterator() {
		return groups.iterator();
	}
	
	public boolean isEmpty() {
		return groups.size() == 0;
	}
}
