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
package ru.silverhammer.reflection;

import org.junit.Assert;
import org.junit.Test;

import ru.silverhammer.reflection.data.Child;
import ru.silverhammer.reflection.data.GrandChild;
import ru.silverhammer.reflection.data.Parent;

import java.util.List;

public class FieldReflectionTest {

	@Test
	public void testGetValue() {
		GrandChild grandChild = new GrandChild(10);
		ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
		IFieldReflection field = cr.findField("code");
		Object value = field.getValue(grandChild);
		Assert.assertNotNull(value);
		Assert.assertTrue(value instanceof Integer);
		Assert.assertEquals(10, value);
	}
	
	@Test(expected = RuntimeException.class)
	public void testGetInvalidField() {
		ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
		IFieldReflection field = cr.findField("code");
		field.getStaticValue();
	}

	@Test
	public void testGetStaticField() {
		ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
		IFieldReflection field = cr.findField("CONSTANT");
		Object value = field.getStaticValue();
		Assert.assertNotNull(value);
		Assert.assertTrue(value instanceof String);
		Assert.assertEquals("the name", value);
	}
	
	@Test
	public void testFindShadowField() {
		ClassReflection<Child> cr = new ClassReflection<>(Child.class);
		GrandChild grandChild = new GrandChild("visible");
		grandChild.setMessage("shadowed");
		IFieldReflection field = cr.findField("message");
		Assert.assertNotNull(field);
		Object value = field.getValue(grandChild);
		Assert.assertEquals("shadowed", value);
	}
	
	@Test
	public void testSetValue() {
		GrandChild grandChild = new GrandChild(10);
		ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
		IFieldReflection field = cr.findField("code");
		field.setValue(grandChild, 20);
		Assert.assertEquals(20, grandChild.getCode());
	}
	
	@Test(expected = RuntimeException.class)
	public void testSetInvalidField() {
		ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
		IFieldReflection field = cr.findField("code");
		field.setValue(null, 0);
	}

	@Test(expected = RuntimeException.class)
	public void testSetInvalidTypeField() {
		ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
		GrandChild grandChild = new GrandChild(10);
		IFieldReflection field = cr.findField("code");
		field.setValue(grandChild, "string");
	}

	@Test(expected = RuntimeException.class)
	public void testSetStaticFinalField() {
		ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
		IFieldReflection field = cr.findField("CONSTANT");
		field.setStaticValue("ALTERED");
	}

	@Test
	public void testSetFinalField() {
		ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
		GrandChild grandChild = new GrandChild(10);
		IFieldReflection field = cr.findField("message");
		field.setValue(grandChild, "Altered");
		Assert.assertEquals("Altered", grandChild.getMessage());
	}

	@Test
	public void testSetStaticField() {
		ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
		IFieldReflection field = cr.findField("STATIC");
		field.setStaticValue(200);
		Assert.assertEquals(200, GrandChild.STATIC);
	}

	@Test
	public void testGenericType() {
		ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
		IFieldReflection field = cr.findField("map");
		List<ClassReflection<?>> generics = field.getGenericClasses();
		Assert.assertEquals(2, generics.size());
		Assert.assertEquals(String.class, generics.get(0).getType());
		Assert.assertEquals(Parent.class, generics.get(1).getType());
	}
}
