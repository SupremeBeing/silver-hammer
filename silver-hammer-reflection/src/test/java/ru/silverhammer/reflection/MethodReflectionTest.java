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
import ru.silverhammer.reflection.data.GrandChild;

public class MethodReflectionTest {

    @Test
    public void testInvocation() {
        ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
        IMethodReflection m = cr.findMethod("getCode");
        GrandChild grandChild = new GrandChild(100);
        Object value = m.invoke(grandChild);
        Assert.assertTrue(value instanceof Integer);
        Assert.assertEquals(100, value);
    }

    @Test
    public void testStaticInvocation() {
        ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
        IMethodReflection m = cr.findMethod("setStatic");
        Object value = m.invoke(null, 20);
        Assert.assertNull(value);
        Assert.assertEquals(20, GrandChild.STATIC);
    }

    @Test(expected = NullPointerException.class)
    public void testInvalidStaticInvocation() {
        ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
        IMethodReflection m = cr.findMethod("getCode");
        m.invoke(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidInvocation() {
        ClassReflection<GrandChild> cr = new ClassReflection<>(GrandChild.class);
        IMethodReflection m = cr.findMethod("getCode");
        GrandChild grandChild = new GrandChild(100);
        m.invoke(grandChild, 200);
    }

}
