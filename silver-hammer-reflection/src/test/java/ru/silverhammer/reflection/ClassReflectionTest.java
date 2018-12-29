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

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import ru.silverhammer.reflection.data.Child;
import ru.silverhammer.reflection.data.GrandChild;
import ru.silverhammer.reflection.data.Parent;

public class ClassReflectionTest {

	@Test
	public void testClassHierarchy() {
		ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
		List<ClassReflection<?>> hierarchy = cr.getHierarchy();
		Assert.assertEquals(4, hierarchy.size());
		Assert.assertEquals(Object.class, hierarchy.get(0).getElement());
		Assert.assertEquals(Parent.class, hierarchy.get(1).getElement());
		Assert.assertEquals(Child.class, hierarchy.get(2).getElement());
		Assert.assertEquals(GrandChild.class, hierarchy.get(3).getElement());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullClass() {
		new ClassReflection<>(null);
	}

	@Test
	public void testVoidHierarchy() {
		ClassReflection<Void> cr = new ClassReflection<>(void.class);
		List<ClassReflection<?>> hierarchy = cr.getHierarchy();
		Assert.assertEquals(1, hierarchy.size());
		Assert.assertEquals(void.class, hierarchy.get(0).getElement());
	}

	@Test
	public void testArrayHierarchy() {
		ClassReflection<int[]> cr = new ClassReflection<>(int[].class);
		List<ClassReflection<?>> hierarchy = cr.getHierarchy();
		Assert.assertEquals(2, hierarchy.size());
		Assert.assertEquals(Object.class, hierarchy.get(0).getElement());
		Assert.assertEquals(int[].class, hierarchy.get(1).getElement());
	}

	@Test
	public void testGetConstructors() {
		ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
		List<IConstructorReflection<GrandChild>> ctors = cr.getConstructors();
		Assert.assertEquals(3, ctors.size());
		Assert.assertEquals(1, ctors.get(0).getParameters().size());
		Assert.assertEquals(1, ctors.get(1).getParameters().size());
		Assert.assertEquals(1, ctors.get(2).getParameters().size());
	}

	@Test
	public void testGetArrayConstructors() {
		ClassReflection<int[]> cr = new ClassReflection<>(int[].class);
		List<IConstructorReflection<int[]>> ctors = cr.getConstructors();
		Assert.assertEquals(1, ctors.size());
	}

	@Test
	public void testGetEnumConstructors() {
		ClassReflection<ElementType> cr = new ClassReflection<>(ElementType.class);
		List<IConstructorReflection<ElementType>> ctors = cr.getConstructors();
		Assert.assertEquals(1, ctors.size());
	}

	@Test
	public void testGetPrimitiveConstructors() {
		ClassReflection<Integer> cr = new ClassReflection<>(int.class);
		List<IConstructorReflection<Integer>> ctors = cr.getConstructors();
		Assert.assertEquals(2, ctors.size());
	}

	@Test(expected = RuntimeException.class)
	public void testAbstractInstantiate() {
		ClassReflection<Parent> cr = new ClassReflection<>(Parent.class);
		cr.instantiate();
	}

	@Test
	public void testNullInstantiate() {
		ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
		GrandChild grandChild = cr.instantiate((String) null);
		Assert.assertNotNull(grandChild);
		Assert.assertNull(grandChild.getMessage()); // string constructor was called
	}

	@Test
	public void testInstantiate() {
		ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
		GrandChild grandChild = cr.instantiate("Message");
		Assert.assertEquals(GrandChild.class, grandChild.getClass());
		Assert.assertEquals("Message", grandChild.getMessage());
	}

	@Test
	public void testArrayInstantiate() {
		ClassReflection<int[]> cr = new ClassReflection<>(int[].class);
		int[] intArray = cr.instantiate(2);
		Assert.assertNotNull(intArray);
		Assert.assertEquals(2, intArray.length);
	}

	@Test
	public void testInnerInstantiate() {
		ClassReflection<Child> cr = new ClassReflection<>(Child.class);
		Child child = cr.instantiate("Nothing");
		ClassReflection<Child.Inner> ir = new ClassReflection<>(Child.Inner.class);
		Child.Inner inner = ir.instantiate(child);
		Assert.assertNotNull(inner);
	}

	@Test
	public void testInterfaceInstantiate() {
		ClassReflection<List> cr = new ClassReflection<>(List.class);
		List list = cr.instantiate();
		Assert.assertNull(list);
	}

	@Test
	public void testAnnotationInstantiate() {
		ClassReflection<SuppressWarnings> cr = new ClassReflection<>(SuppressWarnings.class);
		SuppressWarnings a = cr.instantiate();
		Assert.assertNull(a);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEnumInstantiate() {
		ClassReflection<ElementType> cr = new ClassReflection<>(ElementType.class);
		cr.instantiate("PARAMETER", 3);
	}

	@Test
	public void testPrimitiveInstantiate() {
		ClassReflection<Integer> cr = new ClassReflection<>(int.class);
		Integer obj = cr.instantiate(10);
		Assert.assertNotNull(obj);
		Assert.assertEquals(Integer.valueOf(10), obj);
	}

	@Test
	public void testSupertypeArgsInstantiate() {
		Collection<String> list = new ArrayList<>();
		ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
		GrandChild grandChild = cr.instantiate(list);
		Assert.assertNotNull(grandChild);
		Assert.assertEquals(list, grandChild.getCollection());
	}
	
	@Test
	public void testPrimitiveArgsInstantiate() {
		ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
		int value = 10;
		GrandChild grandChild = cr.instantiate(value);
		Assert.assertNotNull(grandChild);
		Assert.assertEquals(value, grandChild.getCode());
	}

	@Test
	public void testPrimitiveCastInstantiate() {
		ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
		short value = 10;
		GrandChild grandChild = cr.instantiate(value);
		Assert.assertNull(grandChild);
	}

	@Test
	public void testFindField() {
		ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
		IFieldReflection field = cr.findField("code");
		Assert.assertNotNull(field);
		Assert.assertEquals("code", field.getName());
	}
	
	@Test
	public void testFindMissingField() {
		ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
		IFieldReflection field = cr.findField("code2");
		Assert.assertNull(field);
	}
	
	@Test
	public void testFindStaticField() {
		ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
		IFieldReflection field = cr.findField("CONSTANT");
		Assert.assertNotNull(field);
		Assert.assertEquals("CONSTANT", field.getName());
		Assert.assertTrue(field.isStatic());
	}
	
	@Test
	public void testFindInnerField() {
		ClassReflection<Child.Inner> cr = new ClassReflection<>(Child.Inner.class);
		IFieldReflection field = cr.findField("string");
		Assert.assertNotNull(field);
		Assert.assertEquals("string", field.getName());
	}
	
	@Test
	public void testFindMethod() {
		ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
		IMethodReflection method = cr.findMethod("getCode");
		Assert.assertNotNull(method);
		Assert.assertEquals("getCode", method.getName());
	}

	@Test
	public void testFindOverrideMethod() {
		GrandChild grandChild = new GrandChild("visible");
		grandChild.setMessage("shadowed");
		ClassReflection<Child> cr = new ClassReflection<>(Child.class);
		IMethodReflection method = cr.findMethod("getMessage");
		Assert.assertNotNull(method);
		Object value = method.invoke(grandChild);
		Assert.assertEquals("visible", value);
	}

	@Test
	public void testFindMissingMethod() {
		ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
		IMethodReflection method = cr.findMethod("getCode2");
		Assert.assertNull(method);
	}
	
	@Test
	public void testFindStaticMethod() {
		ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
		IMethodReflection method = cr.findMethod("setStatic");
		Assert.assertNotNull(method);
		Assert.assertEquals("setStatic", method.getName());
		Assert.assertTrue(method.isStatic());
	}
	
	@Test
	public void testFindInnerMethod() {
		ClassReflection<Child.Inner> cr = new ClassReflection<>(Child.Inner.class);
		IMethodReflection method = cr.findMethod("setString");
		Assert.assertNotNull(method);
		Assert.assertEquals("setString", method.getName());
	}

	@Test
	public void testGetMethods() {
		ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
		List<IMethodReflection> methods = cr.getMethods();
		Assert.assertTrue(methods.size() > 6);
		Assert.assertTrue(methods.stream().anyMatch(m -> "getCode".equals(m.getName())));
		Assert.assertTrue(methods.stream().anyMatch(m -> "setStatic".equals(m.getName())));
	}

	@Test
	public void testGetFields() {
		ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
		List<IFieldReflection> fields = cr.getFields();
		Assert.assertEquals(7, fields.size());
		Assert.assertEquals("CONSTANT", fields.get(1).getName());
		Assert.assertEquals("STATIC", fields.get(2).getName());
		Assert.assertEquals("code", fields.get(3).getName());
	}

}
