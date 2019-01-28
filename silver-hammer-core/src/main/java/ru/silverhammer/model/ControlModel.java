/*
 * Copyright (c) 2019, Dmitriy Shchekotin
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
package ru.silverhammer.model;

import ru.silverhammer.HorizontalAlignment;
import ru.silverhammer.Location;
import ru.silverhammer.VerticalAlignment;
import ru.silverhammer.control.IControl;
import ru.silverhammer.reflection.IFieldReflection;

public class ControlModel {

	private final IControl<?, ?> control;
	private final Object data;
	private final IFieldReflection fieldReflection;
	
	private String caption;
	private Location captionLocation;
	private String description;
	private HorizontalAlignment horizontalAlignment;
	private VerticalAlignment verticalAlignment;

	public ControlModel(IControl<?, ?> control, Object data, IFieldReflection fieldReflection) {
		this.control = control;
		this.data = data;
		this.fieldReflection = fieldReflection;
	}

	public IControl<?, ?> getControl() {
		return control;
	}
	
	public Object getData() {
		return data;
	}

	public IFieldReflection getFieldReflection() {
		return fieldReflection;
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
