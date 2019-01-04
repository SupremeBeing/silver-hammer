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

import ru.silverhammer.core.Caption;
import ru.silverhammer.core.control.ICollectionControl;
import ru.silverhammer.core.control.SelectionType;
import ru.silverhammer.core.control.ValueType;
import ru.silverhammer.core.control.annotation.Table;
import ru.silverhammer.core.resolver.IControlResolver;
import ru.silverhammer.core.string.IStringProcessor;
import ru.silverhammer.reflection.ClassReflection;
import ru.silverhammer.reflection.IFieldReflection;

import java.util.ArrayList;
import java.util.List;

public class TableControl extends ValidatableControl<Object, Table> implements ICollectionControl<Object[], Object, Table> {

	private final IStringProcessor stringProcessor;
	private final IControlResolver controlResolver;
	private final List<String> captions = new ArrayList<>();
	private final ArrayList<Object[]> data = new ArrayList<>();

	private ValueType valueType = ValueType.Selection;
	private int visibleRows;
    private SelectionType selectionType = SelectionType.Single;

	public TableControl(IStringProcessor stringProcessor, IControlResolver controlResolver) {
		super(true);
		this.stringProcessor = stringProcessor;
		this.controlResolver = controlResolver;
	}
	
	public ValueType getValueType() {
		return valueType;
	}

	public void setValueType(ValueType mode) {
		this.valueType = mode;
	}

	public int getVisibleRowCount() {
		return visibleRows;
	}

	public void setVisibleRowCount(int count) {
	    this.visibleRows = count;
	}

	public SelectionType getSelectionType() {
	    return selectionType;
	}

	public void setSelectionType(SelectionType mode) {
	    this.selectionType = mode;
	}

	public void addCaption(String caption) {
		captions.add(caption);
	}

	public void removeCaption(String caption) {
		captions.remove(caption);
	}

	public void addCaption(int i, String caption) {
		captions.add(i, caption);
	}

	public void setCaption(int i, String caption) {
		captions.set(i, caption);
	}

	public void removeCaption(int i) {
		captions.remove(i);
	}

	public int getCaptionCount() {
		return captions.size();
	}

	public String getCaption(int i) {
		return captions.get(i);
	}

	public void clearCaptions() {
		captions.clear();
	}

	@Override
	public void init(Table annotation) {
		setEnabled(!annotation.readOnly());
		if (annotation.visibleRows() > 0) {
			setVisibleRowCount(annotation.visibleRows());
		}
		setSelectionType(annotation.selection());
		setValueType(annotation.value());
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

    @Override
    public void addItem(Object[] objects) {

    }

    @Override
    public void addItem(int i, Object[] objects) {

    }

    @Override
    public void setItem(int i, Object[] objects) {

    }

    @Override
    public void removeItem(Object[] objects) {

    }

    @Override
    public void removeItem(int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public Object[] getItem(int i) {
        return new Object[0];
    }

    @Override
    public void clearItems() {

    }
}
