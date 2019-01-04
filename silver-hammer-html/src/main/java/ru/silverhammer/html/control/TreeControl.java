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

import ru.silverhammer.core.control.IHierarchyControl;
import ru.silverhammer.core.control.SelectionType;
import ru.silverhammer.core.control.annotation.Tree;

public class TreeControl extends ValidatableControl<Object, Tree> implements IHierarchyControl<Object, Object, Tree> {

	private int visibleRows;
	private SelectionType selectionType = SelectionType.Single;

	public TreeControl() {
		super(true);
	}

	public int getVisibleRowCount() {
		return visibleRows;
	}

	public void setVisibleRowCount(int count) {
		visibleRows = count;
	}

	public SelectionType getSelectionType() {
		return selectionType;
	}

	public void setSelectionType(SelectionType mode) {
		this.selectionType = mode;
	}

	@Override
	public void init(Tree annotation) {
		setEnabled(!annotation.readOnly());
		if (annotation.visibleRows() > 0) {
			setVisibleRowCount(annotation.visibleRows());
		}
		setSelectionType(annotation.selection());
	}

	@Override
	public void addItem(Object parent, Object o) {

	}

	@Override
	public void addItem(Object parent, int i, Object o) {

	}

	@Override
	public void setItem(Object parent, int i, Object o) {

	}

	@Override
	public void removeItem(Object o) {

	}

	@Override
	public void removeItem(Object parent, int i) {

	}

	@Override
	public Object getItem(Object parent, int i) {
		return null;
	}

	@Override
	public int getItemCount(Object parent) {
		return 0;
	}

	@Override
	public void clearItems() {

	}

	@Override
	public void expand(Object o) {

	}

	@Override
	public void collapse(Object o) {

	}

	@Override
	public Object getParent(Object o) {
		return null;
	}
}
