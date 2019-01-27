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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class ArrayConstructorReflection<T> extends VirtualReflection implements IConstructorReflection<T> {

    private final Class<?> arrayType;

    ArrayConstructorReflection(Class<?> arrayType) {
        if (arrayType == null || !arrayType.isArray()) {
            throw new IllegalArgumentException();
        }
        this.arrayType = arrayType;
    }

    @Override
    public AccessType getAccessType() {
        return AccessType.Public;
    }

    @Override
    public List<IParameterReflection> getParameters() {
        List<IParameterReflection> result = new ArrayList<>();
        result.add(new VirtualParameterReflection(int.class, "size"));
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T invoke(Object... args) {
        if (args.length != 1 || !(args[0] instanceof Integer)) {
            throw new RuntimeException();
        }
        return (T) Array.newInstance(arrayType.getComponentType(), (Integer) args[0]);
    }

    @Override
    public String getName() {
        return arrayType.getName();
    }

    @Override
    public Class<?> getType() {
        return arrayType;
    }

    @Override
    public int hashCode() {
        return arrayType.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ArrayConstructorReflection) {
            return Objects.equals(arrayType, ((ArrayConstructorReflection) obj).getType());
        }
        return false;
    }
}
