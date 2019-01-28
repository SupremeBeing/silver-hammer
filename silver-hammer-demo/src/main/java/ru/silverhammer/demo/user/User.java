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
package ru.silverhammer.demo.user;

import ru.silverhammer.control.*;
import ru.silverhammer.processor.Caption;
import ru.silverhammer.processor.Description;
import ru.silverhammer.processor.GroupId;
import ru.silverhammer.Location;
import ru.silverhammer.converter.ArrayToCollection;
import ru.silverhammer.converter.FileToString;
import ru.silverhammer.converter.ValueToItems;
import ru.silverhammer.decorator.FileChooser;
import ru.silverhammer.initializer.EnumerationItems;
import ru.silverhammer.initializer.ReadOnly;
import ru.silverhammer.initializer.StringItems;
import ru.silverhammer.processor.Structure.Category;
import ru.silverhammer.processor.Generatable;
import ru.silverhammer.processor.Structure.Group;
import ru.silverhammer.demo.user.UserGroup.Type;
import ru.silverhammer.validator.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Category(caption = "user.tab", description = "User personal information", icon = "/user.png", mnemonic = 'u', groups = {
		@Group("user"),
		@Group(value = "langs", caption = "user.langs"),
		@Group(value = "groups", caption = "user.groups")
})
@Category(caption = "Achievements", mnemonic = 'a', groups = {
		@Group("list")
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

	@ComboBox(editable = false)
	@GroupId("user")
	@Caption("user.sex")
	@EnumerationItems
	@NotNullable(message = "Please select sex")
	private Sex sex = null;

	@ComboBox
	@GroupId("user")
	@Caption("user.city")
	@StringItems({"Paris", "New York", "Saint Petersburg", "Prague"})
	@MinSize(value = 1, message = "Please specify city")
	private String city = "Saint Petersburg";	

	@Text
	@ReadOnly(true)
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

	@CheckBoxGroup
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

	@ContentTable(annotationCaptions = UserGroup.class, visibleRows = 3)
	@GroupId("groups")
	@ValueToItems(UserGroup.class)
	@ArrayToCollection(UserGroup.class)
	private UserGroup[] groups = {
			new UserGroup("Administrator", "Group of system administrators", Type.Admin),
			new UserGroup("Remote user", "Remote access is granted for this group", Type.RemoteAccess)
	};
	
	@CheckBox(caption = "user.visibility")
	private boolean isPublic = true;
	
	@Generatable
	private Achievement[] achievements = new Achievement[] {
			new Achievement("Registered and filled in the profile with deceitful information."),
			new Achievement("Failed to close the application ten times in a row."),
			new Achievement("Made a mistake in a word \"pneumonoultramicroscopicsilicovolcanoconiosis\".")
	};
}
