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

import javax.swing.JSlider;

import ru.silverhammer.core.control.ISliderControl;

public class SliderControl extends ValidatableControl<Integer, JSlider> implements ISliderControl {

	private static final long serialVersionUID = 5915614741825633432L;

	public SliderControl() {
		super(false);
		getComponent().addChangeListener(l -> fireValueChanged());
	}
	
	@Override
	protected JSlider createComponent() {
		return new JSlider();
	}

	@Override
	public void setMinimum(int min) {
		getComponent().setMinimum(min);
	}

	@Override
	public void setMaximum(int max) {
		getComponent().setMaximum(max);
	}

	@Override
	public void setMinorTicks(int minor) {
		getComponent().setMinorTickSpacing(minor);
	}

	@Override
	public void setMajorTicks(int major) {
		getComponent().setMajorTickSpacing(major);
	}

	@Override
	public void setLabels(boolean labels) {
		getComponent().setPaintLabels(labels);
	}

	@Override
	public void setTicks(boolean ticks) {
		getComponent().setPaintTicks(ticks);
	}

	@Override
	public int getMinimum() {
		return getComponent().getMinimum();
	}

	@Override
	public int getMaximum() {
		return getComponent().getMaximum();
	}

	@Override
	public int getMinorTicks() {
		return getComponent().getMinorTickSpacing();
	}

	@Override
	public int getMajorTicks() {
		return getComponent().getMajorTickSpacing();
	}

	@Override
	public boolean hasLabels() {
		return getComponent().getPaintLabels();
	}

	@Override
	public boolean hasTicks() {
		return getComponent().getPaintTicks();
	}

	@Override
	public Integer getValue() {
		return getComponent().getValue();
	}

	@Override
	public void setValue(Integer value) {
		if (value != null) {
			getComponent().setValue(value);
		} else {
			getComponent().setValue(0);
		}
	}
}
