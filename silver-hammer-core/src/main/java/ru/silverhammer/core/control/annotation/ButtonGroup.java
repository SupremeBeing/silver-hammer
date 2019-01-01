package ru.silverhammer.core.control.annotation;

import ru.silverhammer.core.ProcessorReference;
import ru.silverhammer.core.processor.ControlFieldProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@ProcessorReference(ControlFieldProcessor.class)
public @interface ButtonGroup {}
