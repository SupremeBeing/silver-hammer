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
package ru.silverhammer.swing.dialog;

import java.awt.Container;
import java.awt.Window;

import ru.silverhammer.core.control.IControl;
import ru.silverhammer.core.control.IControlListener;
import ru.silverhammer.core.metadata.MetadataCollector;
import ru.silverhammer.core.metadata.UiMetadata;
import ru.silverhammer.core.string.IStringProcessor;
import ru.silverhammer.core.string.SimpleStringProcessor;
import ru.silverhammer.swing.SwingControlResolver;
import ru.silverhammer.swing.SwingUiBuilder;
import ru.silverhammer.swing.dialog.StandardDialog;

public class GenerationDialog extends StandardDialog implements IControlListener {

	private static final long serialVersionUID = 414732643695055693L;

	private final UiMetadata metadata;

	public GenerationDialog(Window owner, Object... data) {
		this(owner, new SimpleStringProcessor(), data);
	}

	public GenerationDialog(Window owner, IStringProcessor stringProcessor, Object... data) {
		super(owner);
		MetadataCollector collector = new MetadataCollector(new SwingControlResolver(), stringProcessor);
		metadata = collector.collect(data);
		SwingUiBuilder builder = new SwingUiBuilder();
		Container container = builder.buildUi(metadata);
		setCanAccept(metadata.isValid());
		metadata.visitControlAttributes((ca) -> ca.getControl().addControlListener(this));
		setContent(container);
		setLocationRelativeTo(owner);
	}
	
	@Override
	public void valueChanged(IControl<?> control) {
		setCanAccept(metadata.isValid());
	}
	
	@Override
	protected void accepted() {
		metadata.commit();
	}
}
