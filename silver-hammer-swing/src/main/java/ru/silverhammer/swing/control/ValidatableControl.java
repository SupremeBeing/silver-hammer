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
package ru.silverhammer.swing.control;

import java.awt.Color;

import javax.swing.JComponent;

import ru.silverhammer.core.control.IValidatableControl;

public abstract class ValidatableControl<Value, C extends JComponent> extends Control<Value, C> implements IValidatableControl<Value> {

	private static final long serialVersionUID = 7813353790197108681L;

	private Color normalBackground;
	private Color invalidBackground = Color.RED;

	protected ValidatableControl(boolean scrollable) {
		super(scrollable);
		normalBackground = getComponent().getBackground();
	}

	protected Color getInvalidBackground() {
		return invalidBackground;
	}

	protected void setNormalBackground(Color normalBackground) {
		this.normalBackground = normalBackground;
	}

	protected Color getNormalBackground() {
		return normalBackground;
	}

	@Override
	public void setValidationMessage(String message) {
		getComponent().setToolTipText(message);
		getComponent().setBackground(message == null ? normalBackground : invalidBackground);
	}

	@Override
	public String getValidationMessage() {
		return getComponent().getToolTipText();
	}

	@Override
	public boolean isControlValid() {
		return getValidationMessage() == null;
	}
}
