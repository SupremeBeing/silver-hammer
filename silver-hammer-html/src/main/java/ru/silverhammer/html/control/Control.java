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
package ru.silverhammer.html.control;

import ru.silverhammer.core.control.IControl;
import ru.silverhammer.core.control.IValueListener;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;

public abstract class Control<Value, A extends Annotation> implements IControl<Value, A> {

	private final boolean scrollable;
	private final Collection<IValueListener> listeners = new ArrayList<>();
	private Value value;

	private boolean enabled;

	protected Control(boolean scrollable) {
		this.scrollable = scrollable;
	}

	@Override
	public final boolean isEnabled() {
		return enabled;
	}
	
	@Override
	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public final void addValueListener(IValueListener listener) {
		if (listener != null) {
			listeners.add(listener);
		}
	}

	@Override
	public final void removeValueListener(IValueListener listener) {
		listeners.remove(listener);
	}
	
	protected void fireValueChanged() {
		for (IValueListener l : listeners) {
			l.changed(this);
		}
	}

	@Override
	public final Value getValue() {
		return value;
	}

	@Override
	public final void setValue(Value value) {
		this.value = value;
		fireValueChanged();
	}
}
