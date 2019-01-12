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
package ru.silverhammer.swing.control;

import ru.silverhammer.core.Caption;
import ru.silverhammer.core.control.annotation.ContentTable;
import ru.silverhammer.core.resolver.IControlResolver;
import ru.silverhammer.core.string.IStringProcessor;
import ru.silverhammer.reflection.ClassReflection;
import ru.silverhammer.reflection.IFieldReflection;

import java.util.Collection;

public class ContentTableControl extends TableControl<ContentTable> {

	private static final long serialVersionUID = -3692427066762483919L;

	private final IStringProcessor stringProcessor;
	private final IControlResolver controlResolver;

	public ContentTableControl(IStringProcessor stringProcessor, IControlResolver controlResolver) {
		this.stringProcessor = stringProcessor;
		this.controlResolver = controlResolver;
	}
	
	@Override
	public Object getValue() {
		return data.clone();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object value) {
		data.clear();
		if (value instanceof Collection) {
			data.addAll((Collection<Object[]>) value);
		}
		getModel().fireTableStructureChanged();
		fireValueChanged();
	}
	
	@Override
	public void init(ContentTable annotation) {
		if (annotation.visibleRows() > 0) {
			setVisibleRowCount(annotation.visibleRows());
		}
		setSelectionType(annotation.multiSelection());
		if (annotation.annotationCaptions() != Void.class) {
			for (IFieldReflection fr : new ClassReflection<>(annotation.annotationCaptions()).getFields()) {
				if (controlResolver.hasControlAnnotation(fr)) {
					Caption c = fr.getAnnotation(Caption.class);
					addCaption(c == null ? fr.getName() : (stringProcessor == null ? c.value() : stringProcessor.getString(c.value())));
				}
			}
		} else if (annotation.captions().length > 0) {
			for (String caption : annotation.captions()) {
				addCaption(stringProcessor == null ? caption : stringProcessor.getString(caption));
			}
		}
	}
}
