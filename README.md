# Silver Hammer

## Overview

Silver Hammer is an extensible annotation-based UI generation framework.

Currently only Swing implementation is present. JavaFX and web implementations are planned.

## Example

### Sample data
```java
public class Person {

	private static final String EMAIL = "\\b[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}\\b";
		
	@Text
	@Caption("Name")
	@MinSize(value = 1, message = "Name must be specified")
	private String name;
		
	@Text
	@Caption("E-mail")
	@StringFormat(format = EMAIL, message = "Invalid e-mail")
	@MinSize(value = 1, message = "E-mail must be specified")
	private String email;

}
```

### Execution
```java
Person person = new Person();
GenerationDialog dialog = new GenerationDialog(null, person);
dialog.setTitle("Person");
dialog.setVisible(true);
```

