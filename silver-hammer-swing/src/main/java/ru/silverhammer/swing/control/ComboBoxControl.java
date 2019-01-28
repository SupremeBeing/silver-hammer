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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

import ru.silverhammer.control.ICollectionControl;
import ru.silverhammer.control.ComboBox;

// TODO: disable internal first key navigation
public class ComboBoxControl extends Control<Object, ComboBox, JComboBox<Object>> implements ICollectionControl<Object, ComboBox, Object> {

	private static final long serialVersionUID = 1465641213860936391L;

	public ComboBoxControl() {
		super(false);
		getComponent().setEditable(false);
		getComponent().addItemListener(l -> fireValueChanged());
		getComponent().addKeyListener(new SearchAdapter() {
			@Override
			protected void search(String search) {
				for (int i = 0; i < getComponent().getItemCount(); i++) {
					Object item = getComponent().getItemAt(i);
					if (item != null && item.toString().contains(search)) {
						getComponent().setSelectedIndex(i);
						break;
					}
				}
			}
		});
		getComponent().getEditor().getEditorComponent().addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				fireValueChanged();
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				fireValueChanged();
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				fireValueChanged();
			}
		});
	}

	@Override
	protected JComboBox<Object> createComponent() {
		return new JComboBox<>();
	}

	@Override
	public void setValidationMessage(String message) {
		if (isEditable()) {
			getComponent().getEditor().getEditorComponent().setBackground(message == null ? getNormalBackground() : getInvalidBackground());
			getComponent().setToolTipText(message);
		} else {
			super.setValidationMessage(message);
		}
	}

	@Override
	public Object getValue() {
		if (isEditable()) {
			return getComponent().getEditor().getItem();
		} else {
			return getComponent().getSelectedItem();
		}
	}

	@Override
	public void setValue(Object value) {
		if (isEditable()) {
			getComponent().getEditor().setItem(value);
		} else {
			getComponent().setSelectedIndex(-1);
			if (value != null) {
				for (int i = 0; i < getComponent().getItemCount(); i++) {
					if (value.equals(getComponent().getItemAt(i))) {
						getComponent().setSelectedIndex(i);
						break;
					}
				}
			}
		}
	}

	public boolean isEditable() {
		return getComponent().isEditable();
	}

	public void setEditable(boolean editable) {
		setNormalBackground(editable ? getComponent().getEditor().getEditorComponent().getBackground() : getComponent().getBackground());
		getComponent().setEditable(editable);
	}

	@Override
	public List<Object> getCollection() {
		return new ArrayList<Object>() {
			@Override
			public boolean add(Object item) {
				if (item != null) {
					getComponent().addItem(item);
					return true;
				}
				return false;
			}

			@Override
			public Object remove(int i) {
				getComponent().removeItemAt(i);
				return null;
			}

			@Override
			public int size() {
				return getComponent().getItemCount();
			}

			@Override
			public Object get(int i) {
				return getComponent().getItemAt(i);
			}

			@Override
			public void clear() {
				getComponent().removeAllItems();
			}
		};
	}

	@Override
	public void init(ComboBox annotation) {
		setEditable(annotation.editable());
	}
}
