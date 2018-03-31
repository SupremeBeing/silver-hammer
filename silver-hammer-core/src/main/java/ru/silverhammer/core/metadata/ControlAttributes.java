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

import java.lang.reflect.Field;

import ru.silverhammer.common.HorizontalAlignment;
import ru.silverhammer.common.Location;
import ru.silverhammer.common.VerticalAlignment;
import ru.silverhammer.core.control.IControl;

public class ControlAttributes {

	private final IControl<?> control;
	private final Object data;
	private final Field field;
	
	private String caption;
	private Location captionLocation;
	private String description;
	private HorizontalAlignment horizontalAlignment;
	private VerticalAlignment verticalAlignment;

	public ControlAttributes(IControl<?> control, Object data, Field field) {
		this.control = control;
		this.data = data;
		this.field = field;
	}

	public IControl<?> getControl() {
		return control;
	}
	
	public Object getData() {
		return data;
	}

	public Field getField() {
		return field;
	}

	public String getCaption() {
		return caption;
	}

	public Location getCaptionLocation() {
		return captionLocation;
	}

	public String getDescription() {
		return description;
	}

	public HorizontalAlignment getHorizontalAlignment() {
		return horizontalAlignment;
	}

	public VerticalAlignment getVerticalAlignment() {
		return verticalAlignment;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public void setCaptionLocation(Location captionLocation) {
		this.captionLocation = captionLocation;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
	}

	public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
		this.verticalAlignment = verticalAlignment;
	}
}
