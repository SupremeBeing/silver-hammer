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
package ru.silverhammer.swing.demo.user;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import ru.silverhammer.core.Caption;
import ru.silverhammer.core.Description;
import ru.silverhammer.core.GroupId;
import ru.silverhammer.core.Location;
import ru.silverhammer.core.control.ValueType;
import ru.silverhammer.core.control.annotation.*;
import ru.silverhammer.core.converter.annotation.ArrayToCollection;
import ru.silverhammer.core.converter.annotation.FileToString;
import ru.silverhammer.core.converter.annotation.ValueToItems;
import ru.silverhammer.core.decorator.annotation.FileChooser;
import ru.silverhammer.core.initializer.annotation.ControlEvent;
import ru.silverhammer.core.initializer.annotation.EnumerationItems;
import ru.silverhammer.core.initializer.annotation.StringItems;
import ru.silverhammer.core.metadata.UiMetadata;
import ru.silverhammer.core.processor.annotation.GeneratableField;
import ru.silverhammer.core.processor.annotation.Categories.Category;
import ru.silverhammer.core.processor.annotation.Groups.Group;
import ru.silverhammer.core.validator.annotation.DateFormat;
import ru.silverhammer.core.validator.annotation.FileExists;
import ru.silverhammer.core.validator.annotation.MaxDate;
import ru.silverhammer.core.validator.annotation.MinDate;
import ru.silverhammer.core.validator.annotation.MinSize;
import ru.silverhammer.core.validator.annotation.NotNullable;
import ru.silverhammer.core.validator.annotation.StringFormat;
import ru.silverhammer.swing.control.TextControl;
import ru.silverhammer.swing.demo.user.UserGroup.Type;

@Category(caption = "user.tab", description = "User personal information", icon = "/user.png", mnemonic = 'u', groups = {
		@Group("user"),
		@Group(value = "langs", caption = "user.langs"),
		@Group(value = "groups", caption = "user.groups")
})
public class User {

	private static final String EMAIL = "\\b[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}\\b";
		
	@Text
	@GroupId("user")
	@Caption("user.name")
	@MinSize(value = 1, message = "Name must be specified")
	private String name;

	@Password
	@GroupId("user")
	@Caption("user.password")
	private char[] password;

	@Text
	@ControlEvent("setEmail")
	@GroupId("user")
	@Caption("user.email")
	@StringFormat(format = EMAIL, message = "Invalid e-mail")
	@MinSize(value = 1, message = "E-mail must be specified")
	private String email;

	@Text
	@GroupId("user")
	@Caption(value = "user.birth")
	@DateFormat(format = "dd/MM/yyyy", message = "Date of birth should be in %s format")
	@MinDate(format = "dd/MM/yyyy", value = "01/01/1800", message = "Are you a human?")
	@MaxDate(format = "dd/MM/yyyy", value = "01/01/2019", message = "Are you from the future?")
	@NotNullable(message = "Date of birth must be specified")
	private Date birthDate;

	@ComboBox
	@GroupId("user")
	@Caption("user.sex")
	@EnumerationItems
	private Sex sex = Sex.Male;

	@ComboBox
	@GroupId("user")
	@Caption("user.city")
	@StringItems({"Paris", "New York", "Saint Petersburg", "Prague"})
	private String city = "Saint Petersburg";	

	@Text(readOnly = true)
	@GroupId("user")
	@Caption("user.avatar")
	@FileChooser(approveCaption = "Select image", filters = {"JPG | jpg, jpeg", "PNG | png"})
	@FileExists(message = "File doesn't exist")
	@FileToString
	private File file;

	@Label
	@GroupId("user")
	@Caption("user.creation")
	@DateFormat(format = "dd/MM/yyyy", message = "Creation date should be in %s format")
	private Date date = new Date(0);

	@TextArea(visibleRows = 5)
	@GroupId("user")
	@Caption(value = "user.description", location = Location.Top)
	@Description("<html>A free-form description of the user.<br/>Can contain multiple lines.</html>")
	private String description;

	@ButtonGroup
	@GroupId("langs")
	@StringItems({"English", "Russian", "Spanish", "German", "Italian"})
	@MinSize(value = 1, message = "Select at least one language")
	private Collection<String> languages = new ArrayList<String>() {
		private static final long serialVersionUID = 1988350958145919237L;
		{
			add("English");
			add("Russian");
		}
	};

	@Table(annotationCaptions = UserGroup.class, visibleRows = 3, value = ValueType.Content)
	@GroupId("groups")
	@ValueToItems(UserGroup.class)
	@ArrayToCollection(UserGroup.class)
	private UserGroup[] groups = {
			new UserGroup("Administrator", "Group of system administrators", Type.Admin),
			new UserGroup("Remote user", "Remote access is granted for this group", Type.RemoteAccess)
	};
	
	@CheckBox(caption = "user.visibility")
	private boolean isPublic = true;
	
	@GeneratableField
	private Achievement[] achievements = new Achievement[] {
			new Achievement("Registered and filled in the profile with deceitful information."),
			new Achievement("Failed to close the application ten times in a row."),
			new Achievement("Made a mistake in a word \"pneumonoultramicroscopicsilicovolcanoconiosis\".")
	};

	@SuppressWarnings("unused")
	private void setEmail(UiMetadata metadata) {
		TextControl emailControl = metadata.findControl(this, "email");
		TextControl nameControl = metadata.findControl(this, "name");
		String email = emailControl.getValue();
		String name = nameControl.getValue();
		if (email != null && (name == null || name.length() == 0 || email.startsWith(name))) {
			nameControl.setValue(email);
		}
	}
}
