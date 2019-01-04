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

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import ru.silverhammer.core.control.annotation.Text;

public class TextControl extends ValidatableControl<String, Text, JTextField> {

	private static final long serialVersionUID = -2398089634039989572L;

	public TextControl() {
		super(false);
		getComponent().getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				fireValueChanged();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				fireValueChanged();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				fireValueChanged();
			}
		});
	}

	@Override
	protected JTextField createComponent() {
		return new JTextField();
	}

	public boolean isEditable() {
		return getComponent().isEditable();
	}

	public void setEditable(boolean editable) {
		getComponent().setEditable(editable);
	}

	@Override
	public String getValue() {
		return getComponent().getText();
	}

	@Override
	public void setValue(String value) {
		getComponent().setText(value);
	}

	@Override
	public void init(Text annotation) {
		setEnabled(!annotation.readOnly());
		setEditable(annotation.editable());
	}
}
