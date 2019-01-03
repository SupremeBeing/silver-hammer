/*
 * Copyright (c) 2018, Dmitriy Shchekotin
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
package ru.silverhammer.swing.demo;

import ru.silverhammer.core.string.IStringProcessor;
import ru.silverhammer.core.string.MultilingualStringProcessor;
import ru.silverhammer.swing.demo.settings.Environment;
import ru.silverhammer.swing.demo.settings.Settings;
import ru.silverhammer.swing.demo.user.User;
import ru.silverhammer.swing.dialog.GenerationDialog;

public class Program {

	public static void main(String... args) {
		Environment env = new Environment();
		Settings settings = new Settings();
		User user = new User();
		IStringProcessor stringProcessor = new MultilingualStringProcessor("messages");
		GenerationDialog dialog = new GenerationDialog(null, stringProcessor, user, env, settings);
		dialog.setTitle("Silver Hammer Demo");
		dialog.setVisible(true);
	}

}
