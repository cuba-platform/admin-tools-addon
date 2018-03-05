package com.haulmont.addon.admintools.global.auto_import.dto;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ImportFileObjects implements Map<String, ImportDataObject> {

    protected Map<String, ImportDataObject> delegate;

    public ImportFileObjects() {
        this.delegate = new HashMap<>();
    }

    public ImportFileObjects(Map<String, ImportDataObject> delegate) {
        this.delegate = delegate;
    }

    // code generation
    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public ImportDataObject get(Object key) {
        return delegate.get(key);
    }

    @Override
    public ImportDataObject put(String key, ImportDataObject value) {
        return delegate.put(key, value);
    }

    @Override
    public ImportDataObject remove(Object key) {
        return delegate.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends ImportDataObject> m) {
        delegate.putAll(m);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Set<String> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<ImportDataObject> values() {
        return delegate.values();
    }

    @Override
    public Set<Entry<String, ImportDataObject>> entrySet() {
        return delegate.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public ImportDataObject getOrDefault(Object key, ImportDataObject defaultValue) {
        return delegate.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super ImportDataObject> action) {
        delegate.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super ImportDataObject, ? extends ImportDataObject> function) {
        delegate.replaceAll(function);
    }

    @Override
    public ImportDataObject putIfAbsent(String key, ImportDataObject value) {
        return delegate.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return delegate.remove(key, value);
    }

    @Override
    public boolean replace(String key, ImportDataObject oldValue, ImportDataObject newValue) {
        return delegate.replace(key, oldValue, newValue);
    }

    @Override
    public ImportDataObject replace(String key, ImportDataObject value) {
        return delegate.replace(key, value);
    }

    @Override
    public ImportDataObject computeIfAbsent(String key, Function<? super String, ? extends ImportDataObject> mappingFunction) {
        return delegate.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public ImportDataObject computeIfPresent(String key, BiFunction<? super String, ? super ImportDataObject, ? extends ImportDataObject> remappingFunction) {
        return delegate.computeIfPresent(key, remappingFunction);
    }

    @Override
    public ImportDataObject compute(String key, BiFunction<? super String, ? super ImportDataObject, ? extends ImportDataObject> remappingFunction) {
        return delegate.compute(key, remappingFunction);
    }

    @Override
    public ImportDataObject merge(String key, ImportDataObject value, BiFunction<? super ImportDataObject, ? super ImportDataObject, ? extends ImportDataObject> remappingFunction) {
        return delegate.merge(key, value, remappingFunction);
    }
}
