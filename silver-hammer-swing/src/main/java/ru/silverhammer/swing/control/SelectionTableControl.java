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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ru.silverhammer.model.UiModel;
import ru.silverhammer.processor.Caption;
import ru.silverhammer.control.SelectionTable;
import ru.silverhammer.conversion.IStringConverter;
import ru.silverhammer.reflection.ClassReflection;
import ru.silverhammer.reflection.IFieldReflection;

public class SelectionTableControl extends TableControl<SelectionTable> {

	private static final long serialVersionUID = -3692427066762483919L;

	private final IStringConverter converter;
	private final UiModel model;

	public SelectionTableControl(IStringConverter converter, UiModel model) {
		super();
		this.converter = converter;
		this.model = model;
	}
	
	@Override
	public Object getValue() {
		if (!isMultiSelection()) {
			int i = getComponent().getSelectedRow();
			return i == -1 ? null : data.get(i);
		} else {
			List<Object[]> result = new ArrayList<>();
			for (int i : getComponent().getSelectedRows()) {
				result.add(data.get(i));
			}
			return result;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object value) {
		getComponent().clearSelection();
		if (!isMultiSelection() && value instanceof Object[]) {
			int i = findRow((Object[]) value);
			if (i != -1) {
				getComponent().setRowSelectionInterval(i, i);
			}
		} else if (isMultiSelection() && value instanceof Collection) {
			for (Object[] o : (Collection<Object[]>) value) {
				int i = findRow(o);
				if (i != -1) {
					getComponent().setRowSelectionInterval(i, i);
				}
			}
		}
		fireValueChanged();
	}
	
	@Override
	public void init(SelectionTable annotation) {
		if (annotation.visibleRows() > 0) {
			setVisibleRowCount(annotation.visibleRows());
		}
		setSelectionType(annotation.multiSelection());
		if (annotation.annotationCaptions() != Void.class) {
			for (IFieldReflection fr : new ClassReflection<>(annotation.annotationCaptions()).getFields()) {
				if (model.hasControlAnnotation(fr)) {
					Caption c = fr.getAnnotation(Caption.class);
					getCaptions().add(c == null ? fr.getName() : converter.getString(c.value()));
				}
			}
		} else if (annotation.captions().length > 0) {
			for (String caption : annotation.captions()) {
				getCaptions().add(converter.getString(caption));
			}
		}
	}
}
