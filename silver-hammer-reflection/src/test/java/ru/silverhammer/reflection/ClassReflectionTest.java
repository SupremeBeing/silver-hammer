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

public class ClassReflectionTest {

	@Test
	public void testClassHierarchy() {
		ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
		ClassReflection<?>[] hierarchy = cr.getHierarchy();
		Assert.assertEquals(4, hierarchy.length);
		Assert.assertEquals(Object.class, hierarchy[0].getElement());
		Assert.assertEquals(Parent.class, hierarchy[1].getElement());
		Assert.assertEquals(Child.class, hierarchy[2].getElement());
		Assert.assertEquals(GrandChild.class, hierarchy[3].getElement());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullHierarchy() {
		new ClassReflection<>(null);
	}

	@Test
	public void testVoidHierarchy() {
		ClassReflection<Void> cr = new ClassReflection<>(void.class);
		ClassReflection<?>[] hierarchy = cr.getHierarchy();
		Assert.assertEquals(1, hierarchy.length);
		Assert.assertEquals(void.class, hierarchy[0].getElement());
	}

	@Test
	public void testArrayHierarchy() {
		ClassReflection<int[]> cr = new ClassReflection<>(int[].class);
		ClassReflection<?>[] hierarchy = cr.getHierarchy();
		Assert.assertEquals(2, hierarchy.length);
		Assert.assertEquals(Object.class, hierarchy[0].getElement());
		Assert.assertEquals(int[].class, hierarchy[1].getElement());
	}

	@Test
	public void testGetConstructors() {
		ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
		ConstructorReflection<GrandChild>[] ctors = cr.getConstructors();
		Assert.assertEquals(3, ctors.length);
	}
}
