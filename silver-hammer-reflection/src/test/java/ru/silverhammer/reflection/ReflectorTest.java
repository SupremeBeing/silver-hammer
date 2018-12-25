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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import ru.silverhammer.reflection.Reflector;
import ru.silverhammer.reflection.data.Child;
import ru.silverhammer.reflection.data.GrandChild;
import ru.silverhammer.reflection.data.Parent;

public class ReflectorTest {
	
	@Test
	public void testInstantiate() {
		Parent parent = Reflector.instantiate(Parent.class);
		Assert.assertEquals(Parent.class, parent.getClass());
	}

	@Test(expected = NullPointerException.class)
	public void testNullInstantiate() {
		GrandChild grandChild = Reflector.instantiate(null);
		Assert.assertEquals(GrandChild.class, grandChild.getClass());
	}

	@Test
	public void testArgsInstantiate() {
		GrandChild grandChild = Reflector.instantiate(GrandChild.class, "Message");
		Assert.assertEquals(GrandChild.class, grandChild.getClass());
		Assert.assertEquals("Message", grandChild.getMessage());
	}

	// TODO: consider adding super type support to Reflector
	@Test(expected = RuntimeException.class)
	public void testSupertypeArgsInstantiate() {
		Collection<String> list = new ArrayList<>();
		Reflector.instantiate(GrandChild.class, list);
	}
	
	// TODO: consider adding primitives support to Reflector
	@Test(expected = RuntimeException.class)
	public void testPrimitiveArgsInstantiate() {
		Reflector.instantiate(GrandChild.class, 10);
	}
	
	@Test
	public void testFindField() {
		Field field = Reflector.findField(GrandChild.class, "code");
		Assert.assertNotNull(field);
		Assert.assertEquals("code", field.getName());
	}
	
	@Test
	public void testFindMissingField() {
		Field field = Reflector.findField(GrandChild.class, "code2");
		Assert.assertNull(field);
	}
	
	@Test
	public void testFindNullField() {
		Field field = Reflector.findField(GrandChild.class, null);
		Assert.assertNull(field);
	}

	@Test
	public void testNullClassFindField() {
		Field field = Reflector.findField(null, "code");
		Assert.assertNull(field);
	}

	@Test
	public void testFindStaticField() {
		Field field = Reflector.findField(GrandChild.class, "CONSTANT");
		Assert.assertNotNull(field);
		Assert.assertEquals("CONSTANT", field.getName());
		Assert.assertTrue(Modifier.isStatic(field.getModifiers()));
	}
	
	@Test
	public void testFindInnerField() {
		Field field = Reflector.findField(Child.Inner.class, "string");
		Assert.assertNotNull(field);
		Assert.assertEquals("string", field.getName());
	}
	
	@Test
	public void testGetValue() {
		GrandChild grandChild = new GrandChild(10);
		Field field = Reflector.findField(GrandChild.class, "code");
		Object value = Reflector.getValue(grandChild, field);
		Assert.assertNotNull(value);
		Assert.assertTrue(value instanceof Integer);
		Assert.assertEquals(10, value);
	}
	
	@Test(expected = RuntimeException.class)
	public void testGetNullField() {
		GrandChild grandChild = new GrandChild(10);
		Reflector.getValue(grandChild, null);
	}
	
	@Test(expected = RuntimeException.class)
	public void testGetInvalidField() {
		Field field = Reflector.findField(GrandChild.class, "code");
		Reflector.getValue(null, field);
	}

	@Test
	public void testGetStaticField() {
		Field field = Reflector.findField(GrandChild.class, "CONSTANT");
		Object value = Reflector.getValue(null, field);
		Assert.assertNotNull(value);
		Assert.assertTrue(value instanceof String);
		Assert.assertEquals("the name", value);
	}
	
	@Test
	public void testFindShadowField() {
		GrandChild grandChild = new GrandChild("visible");
		grandChild.setMessage("shadowed");
		Field field = Reflector.findField(Child.class, "message");
		Assert.assertNotNull(field);
		Object value = Reflector.getValue(grandChild, field);
		Assert.assertEquals("shadowed", value);
	}
	
	@Test
	public void testSetValue() {
		GrandChild grandChild = new GrandChild(10);
		Field field = Reflector.findField(GrandChild.class, "code");
		Reflector.setValue(grandChild, field, 20);
		Assert.assertEquals(20, grandChild.getCode());
	}
	
	@Test(expected = RuntimeException.class)
	public void testSetNullField() {
		GrandChild grandChild = new GrandChild(10);
		Reflector.setValue(grandChild, null, 20);
	}
	
	@Test(expected = RuntimeException.class)
	public void testSetInvalidField() {
		Field field = Reflector.findField(GrandChild.class, "code");
		Reflector.setValue(null, field, 0);
	}

	@Test(expected = RuntimeException.class)
	public void testSetInvalidTypeField() {
		GrandChild grandChild = new GrandChild(10);
		Field field = Reflector.findField(GrandChild.class, "code");
		Reflector.setValue(grandChild, field, "string");
	}

	// TODO: consider altering modifiers
	@Test(expected = RuntimeException.class)
	public void testSetStaticFinalField() {
		Field field = Reflector.findField(GrandChild.class, "CONSTANT");
		Reflector.setValue(null, field, "ALTERED");
		Assert.assertEquals("ALTERED", GrandChild.CONSTANT);
	}

	@Test
	public void testSetFinalField() {
		GrandChild grandChild = new GrandChild(10);
		Field field = Reflector.findField(GrandChild.class, "message");
		Assert.assertTrue(Modifier.isFinal(field.getModifiers()));
		Reflector.setValue(grandChild, field, "Altered");
		Assert.assertEquals("Altered", grandChild.getMessage());
	}

	@Test
	public void testSetStaticField() {
		Field field = Reflector.findField(GrandChild.class, "STATIC");
		Reflector.setValue(null, field, 200);
		Assert.assertEquals(200, GrandChild.STATIC);
	}
	
	@Test
	public void testFindMethod() {
		Method method = Reflector.findMethod(GrandChild.class, "getCode");
		Assert.assertNotNull(method);
		Assert.assertEquals("getCode", method.getName());
	}
	
	@Test
	public void testFindMissingMethod() {
		Method method = Reflector.findMethod(GrandChild.class, "getCode2");
		Assert.assertNull(method);
	}
	
	@Test
	public void testFindNullMethod() {
		Method method = Reflector.findMethod(GrandChild.class, null);
		Assert.assertNull(method);
	}

	@Test
	public void testNullClassFindMethod() {
		Method method = Reflector.findMethod(null, "getCode");
		Assert.assertNull(method);
	}

	@Test
	public void testFindStaticMethod() {
		Method method = Reflector.findMethod(GrandChild.class, "setStatic");
		Assert.assertNotNull(method);
		Assert.assertEquals("setStatic", method.getName());
		Assert.assertTrue(Modifier.isStatic(method.getModifiers()));
	}
	
	@Test
	public void testFindInnerMethod() {
		Method method = Reflector.findMethod(Child.Inner.class, "setString");
		Assert.assertNotNull(method);
		Assert.assertEquals("setString", method.getName());
	}
	
	// TODO: consider try using MethodHandle to access overridden method
	@Test
	public void testFindOverrideMethod() throws Throwable {
		GrandChild grandChild = new GrandChild("visible");
		grandChild.setMessage("shadowed");
		Method method = Reflector.findMethod(Child.class, "getMessage");
		Assert.assertNotNull(method);
		Object value = Reflector.invoke(grandChild, method);
		Assert.assertEquals("visible", value);
	}
	
	// getFields, getMethods, invoke, getGenericTypeArguments
}
