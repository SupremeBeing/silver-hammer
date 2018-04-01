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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import ru.silverhammer.common.Location;
import ru.silverhammer.core.Caption;
import ru.silverhammer.core.GroupId;
import ru.silverhammer.core.control.annotation.CheckBoxGroup;
import ru.silverhammer.core.control.annotation.ComboBox;
import ru.silverhammer.core.control.annotation.Password;
import ru.silverhammer.core.control.annotation.Text;
import ru.silverhammer.core.control.annotation.TextArea;
import ru.silverhammer.core.initializer.annotation.ControlProperties;
import ru.silverhammer.core.initializer.annotation.EnumerationItems;
import ru.silverhammer.core.initializer.annotation.StringItems;
import ru.silverhammer.core.processor.annotation.Categories.Category;
import ru.silverhammer.core.processor.annotation.Groups.Group;
import ru.silverhammer.core.validator.annotation.DateFormat;
import ru.silverhammer.core.validator.annotation.MaxDate;
import ru.silverhammer.core.validator.annotation.MinDate;
import ru.silverhammer.core.validator.annotation.MinSize;
import ru.silverhammer.core.validator.annotation.NotNullable;
import ru.silverhammer.core.validator.annotation.StringFormat;

@Category(caption = "User", description = "User personal information", icon = "/user.png", mnemonic = 'u', groups = {@Group("user")})
public class User {

	private static final String EMAIL = "\\b[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}\\b";
		
	@Text
	@GroupId("user")
	@Caption("Name")
	@MinSize(value = 1, message = "Name must be specified")
	private String name;

	@Password
	@GroupId("user")
	@Caption("Password")
	private char[] password;

	@Text
	@GroupId("user")
	@Caption("E-mail")
	@StringFormat(format = EMAIL, message = "Invalid e-mail")
	@MinSize(value = 1, message = "E-mail must be specified")
	private String email;

	@Text
	@GroupId("user")
	@Caption(value = "Date of birth")
	@DateFormat(format = "dd/MM/yyyy", message = "Date of birth should be in %s format")
	@MinDate(format = "dd/MM/yyyy", value = "01/01/1800", message = "Are you a human?")
	@MaxDate(format = "dd/MM/yyyy", value = "01/01/2019", message = "Are you from the future?")
	@NotNullable(message = "Date of birth must be specified")
	private Date date;

	@ComboBox
	@GroupId("user")
	@Caption("Sex")
	@EnumerationItems
	private Sex sex = Sex.Male;

	@TextArea
	@GroupId("user")
	@Caption(value = "Description", location = Location.Top)
	@ControlProperties(visibleRows = 5)
	private String description;

	@CheckBoxGroup
	@GroupId("user")
	@Caption(value = "Spoken languages", location = Location.Top)
	@StringItems({"English", "Russian", "Spanish", "German", "Italian"})
	private Collection<String> languages = new ArrayList<String>() {
		private static final long serialVersionUID = 1988350958145919237L;
		{
			add("English");
			add("Russian");
		}
	};

}
