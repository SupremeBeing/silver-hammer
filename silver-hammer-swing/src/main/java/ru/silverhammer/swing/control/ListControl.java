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
import java.util.Objects;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import ru.silverhammer.core.control.ICollectionControl;
import ru.silverhammer.core.control.SelectionType;
import ru.silverhammer.core.control.ValueType;
import ru.silverhammer.core.control.annotation.List;

// TODO: disable internal first key navigation
public class ListControl extends ValidatableControl<Object, List, JList<Object>>
	implements ICollectionControl<Object, Object, List> {

	private static final long serialVersionUID = 396462498473332445L;

	private ValueType valueType = ValueType.Selection;
	
	public ListControl() {
		super(true);
		getComponent().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getComponent().getSelectionModel().addListSelectionListener(e -> fireValueChanged());
		getComponent().addKeyListener(new SearchAdapter() {
			@Override
			protected void search(String search) {
				for (int i = 0; i < getModel().getSize(); i++) {
					Object item = getModel().getElementAt(i);
					if (item != null && item.toString().contains(search)) {
						getComponent().setSelectedIndex(i);
						break;
					}
				}
			}
		});
	}
	
	private DefaultListModel<Object> getModel() {
		return (DefaultListModel<Object>) getComponent().getModel();
	}

	public ValueType getValueType() {
		return valueType;
	}

	public void setValueType(ValueType mode) {
		this.valueType = mode;
	}

	@Override
	public Object getValue() {
		if (getValueType() == ValueType.Content) {
			Collection<Object> value = new ArrayList<>();
			for (int i = 0; i < getModel().getSize(); i++) {
				value.add(getModel().getElementAt(i));
			}
			return value;
		} else if (getValueType() == ValueType.Selection) {
			if (getSelectionType() == SelectionType.Single) {
				return getComponent().getSelectedValue();
			} else {
				return getComponent().getSelectedValuesList();
			}
		}
		return null;
	}

	@Override
	public void setValue(Object value) {
		if (getValueType() == ValueType.Content) {
			getModel().removeAllElements();
			if (value instanceof Collection) {
				for (Object o : (Collection<?>) value) {
					getModel().addElement(o);
				}
			}
			fireValueChanged();
		} else if (getValueType() == ValueType.Selection) {
			if (getSelectionType() == SelectionType.Single) {
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
	}

	public int getVisibleRowCount() {
		return getComponent().getVisibleRowCount();
	}

	public void setVisibleRowCount(int count) {
		getComponent().setVisibleRowCount(count);
	}

	public SelectionType getSelectionType() {
		int mode = getComponent().getSelectionMode();
		if (mode == ListSelectionModel.SINGLE_SELECTION) {
			return SelectionType.Single;
		} else if (mode == ListSelectionModel.SINGLE_INTERVAL_SELECTION) {
			return SelectionType.Interval;
		} else if (mode == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION) {
			return SelectionType.Multi;
		}
		return null;
	}

	public void setSelectionType(SelectionType mode) {
		if (mode == SelectionType.Single) {
			getComponent().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		} else if (mode == SelectionType.Interval) {
			getComponent().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		} else if (mode == SelectionType.Multi) {
			getComponent().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		}
	}

	public Object getSingleSelection() {
		return getComponent().getSelectedValue();
	}

	public Object[] getSelection() {
		Collection<Object> list = getComponent().getSelectedValuesList();
		return list.toArray(new Object[list.size()]);
	}

	public void select(Object value) {
		int count = getModel().getSize();
		for (int i = 0; i < count; i++) {
			Object val = getModel().get(i);
			if (Objects.equals(value, val)) {
				getComponent().setSelectionInterval(i, i);
				break;
			}
		}
	}

	public void deselect(Object value) {
		int count = getModel().getSize();
		for (int i = 0; i < count; i++) {
			Object val = getModel().get(i);
			if (Objects.equals(value, val)) {
				getComponent().removeSelectionInterval(i, i);
				break;
			}
		}
	}

	@Override
	protected JList<Object> createComponent() {
		return new JList<>(new DefaultListModel<>());
	}

	@Override
	public void addItem(Object item) {
		if (item != null) {
			getModel().addElement(item);
			if (valueType == ValueType.Content) {
				fireValueChanged();
			}
		}
	}

	@Override
	public void removeItem(Object item) {
		if (getModel().removeElement(item) && valueType == ValueType.Content) {
			fireValueChanged();
		}
	}

	@Override
	public void clearItems() {
		getModel().removeAllElements();
		if (valueType == ValueType.Content) {
			fireValueChanged();
		}
	}

	@Override
	public void addItem(int i, Object item) {
		if (item != null) {
			getModel().add(i, item);
			if (valueType == ValueType.Content) {
				fireValueChanged();
			}
		}
	}

	@Override
	public void setItem(int i, Object item) {
		if (item != null) {
			getModel().set(i, item);
			if (valueType == ValueType.Content) {
				fireValueChanged();
			}
		}
	}

	@Override
	public void removeItem(int i) {
		getModel().remove(i);
		if (valueType == ValueType.Content) {
			fireValueChanged();
		}
	}

	@Override
	public int getItemCount() {
		return getModel().getSize();
	}

	@Override
	public Object getItem(int i) {
		return getModel().get(i);
	}

	@Override
	public void init(List annotation) {
		setEnabled(!annotation.readOnly());
		if (annotation.visibleRows() > 0) {
			setVisibleRowCount(annotation.visibleRows());
		}
		setSelectionType(annotation.selection());
		setValueType(annotation.value());
	}
}
