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

import java.util.Collection;

import ru.silverhammer.control.SelectionList;

public class SelectionListControl extends ListControl<SelectionList> {

	private static final long serialVersionUID = 396462498473332445L;

	@Override
	public Object getValue() {
		if (!isMultiSelection()) {
			return getComponent().getSelectedValue();
		} else {
			return getComponent().getSelectedValuesList();
		}
	}

	@Override
	public void setValue(Object value) {
		if (!isMultiSelection()) {
			getComponent().setSelectedValue(value, true);
		} else {
			getComponent().clearSelection();
			if (value instanceof Collection) {
				int count = getModel().getSize();
				for (int i = 0; i < count; i++) {
					Object val = getModel().get(i);
					if (((Collection<?>) value).contains(val)) {
						getComponent().addSelectionInterval(i, i);
					}
				}
			}
		}
	}

	@Override
	public void init(SelectionList annotation) {
		if (annotation.visibleRows() > 0) {
			setVisibleRowCount(annotation.visibleRows());
		}
		setSelectionType(annotation.multiSelection());
	}
}
