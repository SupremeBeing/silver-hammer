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
package ru.silverhammer.core.initializer;

import java.lang.reflect.Field;

import ru.silverhammer.common.injection.Inject;
import ru.silverhammer.core.control.ICaptionControl;
import ru.silverhammer.core.control.IControl;
import ru.silverhammer.core.control.IEditableControl;
import ru.silverhammer.core.control.IMultiCaptionControl;
import ru.silverhammer.core.control.IRowsControl;
import ru.silverhammer.core.control.ISelectionTypeControl;
import ru.silverhammer.core.control.IValueTypeControl;
import ru.silverhammer.core.initializer.annotation.ControlProperties;
import ru.silverhammer.core.string.IStringProcessor;

public class ControlPropertiesInitializer implements IInitializer<IControl<?>, ControlProperties> {

	private final IStringProcessor processor;

	public ControlPropertiesInitializer(@Inject IStringProcessor processor) {
		this.processor = processor;
	}

	@Override
	public void init(IControl<?> control, ControlProperties annotation, Object data, Field field) {
		control.setEnabled(!annotation.readOnly());
		if (control instanceof IEditableControl) {
			((IEditableControl<?>) control).setEditable(annotation.editable());
		}
		if (control instanceof ICaptionControl && annotation.captions().length > 0) {
			String caption = annotation.captions()[0];
			((ICaptionControl<?>) control).setCaption(processor == null ? caption : processor.getString(caption));
		} else if (control instanceof IMultiCaptionControl && annotation.captions().length > 0) {
			for (String caption : annotation.captions()) {
				((IMultiCaptionControl<?>) control).addCaption(processor == null ? caption : processor.getString(caption));
			}
		}
		if (control instanceof IRowsControl && annotation.visibleRows() > 0) {
			((IRowsControl<?>) control).setVisibleRowCount(annotation.visibleRows());
		}
		if (control instanceof ISelectionTypeControl) {
			((ISelectionTypeControl<?>) control).setSelectionType(annotation.selection());
		}
		if (control instanceof IValueTypeControl) {
			((IValueTypeControl<?>) control).setValueType(annotation.value());
		}
	}

}
