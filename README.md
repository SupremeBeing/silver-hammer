# Silver Hammer

## Overview

Silver Hammer is an extensible annotation-based UI generation framework.

Currently only Swing implementation is present. JavaFX and web implementations are planned.

## Example

### Sample data
```
public class Person {
		
	@Caption("Name")
	@Text
	@MinSize(value = 1, message = "Name must be specified")
	private String name;
		
	@Caption("E-mail")
	@Text
	@StringFormat(format = "\\b[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}\\b", message = "Invalid e-mail")
	@MinSize(value = 1, message = "E-mail must be specified")
	private String email;

}
```

### Execution
```
Person person = new Person();
GenerationDialog dialog = new GenerationDialog(null, person);
dialog.setTitle("Person");
dialog.setVisible(true);
```

