package com.mcmiddleearth.entities.entities.attributes;

import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class VirtualEntityAttributeInstance implements AttributeInstance {

    private final Attribute attribute;

    private final double defaultValue;
    private double baseValue, value;

    private final List<AttributeModifier> modifiers = new ArrayList<>();

    public VirtualEntityAttributeInstance(Attribute attribute, double defaultValue) {
        this(attribute, defaultValue, defaultValue);
    }

    public VirtualEntityAttributeInstance(Attribute attribute, double defaultValue, double baseValue) {
        this.attribute = attribute;
        this.defaultValue = defaultValue;
        this.baseValue = baseValue;
        calculateValue();
    }

    @Override
    public @NotNull Attribute getAttribute() {
        return attribute;
    }

    @Override
    public double getBaseValue() {
        return baseValue;
    }

    @Override
    public void setBaseValue(double value) {
        this.baseValue = value;
    }

    @Override
    public @NotNull Collection<AttributeModifier> getModifiers() {
        return modifiers;
    }

    @Override
    public void addModifier(@NotNull AttributeModifier modifier) {
        modifiers.add(modifier);
        calculateValue();
    }

    @Override
    public void removeModifier(@NotNull AttributeModifier modifier) {
        modifiers.remove(modifier);
        calculateValue();
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public double getDefaultValue() {
        return defaultValue;
    }

    private void calculateValue() {
Logger.getGlobal().info("base: "+baseValue);
        AtomicDouble add = new AtomicDouble();
        AtomicDouble multiplyBase = new AtomicDouble(1);
        AtomicReference<Double> multiply = new AtomicReference<>((double) 1);
        modifiers.forEach(modifier -> {
Logger.getGlobal().info("modifier: "+modifier.getOperation()+" "+modifier.getAmount());
            switch(modifier.getOperation()) {
                case ADD_NUMBER:
                    add.addAndGet(modifier.getAmount());
                    break;
                case ADD_SCALAR:
                    multiplyBase.addAndGet(modifier.getAmount());
                    break;
                case MULTIPLY_SCALAR_1:
                    multiply.updateAndGet(v -> (double) (v * (1 + modifier.getAmount())));
            }
        });
Logger.getGlobal().info("add: "+add.get()+" multiplybase: "+multiplyBase.get()+" mutiply: "+multiply.get());
        value = (baseValue+add.get()) * multiplyBase.get() * multiply.get();
Logger.getGlobal().info("value: "+value);
    }
}
