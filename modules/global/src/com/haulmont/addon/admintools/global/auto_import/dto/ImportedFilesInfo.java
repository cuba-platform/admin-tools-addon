package com.haulmont.addon.admintools.global.auto_import.dto;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * The map of imported files in the format 'path to file' - 'status info'
 */
public class ImportedFilesInfo implements Map<String, ImportStatusInfo> {

    protected Map<String, ImportStatusInfo> delegate;

    public ImportedFilesInfo() {
        this.delegate = new HashMap<>();
    }

    public ImportedFilesInfo(Map<String, ImportStatusInfo> delegate) {
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
    public ImportStatusInfo get(Object key) {
        return delegate.get(key);
    }

    @Override
    public ImportStatusInfo put(String key, ImportStatusInfo value) {
        return delegate.put(key, value);
    }

    @Override
    public ImportStatusInfo remove(Object key) {
        return delegate.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends ImportStatusInfo> m) {
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
    public Collection<ImportStatusInfo> values() {
        return delegate.values();
    }

    @Override
    public Set<Entry<String, ImportStatusInfo>> entrySet() {
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
    public ImportStatusInfo getOrDefault(Object key, ImportStatusInfo defaultValue) {
        return delegate.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super ImportStatusInfo> action) {
        delegate.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super ImportStatusInfo, ? extends ImportStatusInfo> function) {
        delegate.replaceAll(function);
    }

    @Override
    public ImportStatusInfo putIfAbsent(String key, ImportStatusInfo value) {
        return delegate.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return delegate.remove(key, value);
    }

    @Override
    public boolean replace(String key, ImportStatusInfo oldValue, ImportStatusInfo newValue) {
        return delegate.replace(key, oldValue, newValue);
    }

    @Override
    public ImportStatusInfo replace(String key, ImportStatusInfo value) {
        return delegate.replace(key, value);
    }

    @Override
    public ImportStatusInfo computeIfAbsent(String key, Function<? super String, ? extends ImportStatusInfo> mappingFunction) {
        return delegate.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public ImportStatusInfo computeIfPresent(String key, BiFunction<? super String, ? super ImportStatusInfo, ? extends ImportStatusInfo> remappingFunction) {
        return delegate.computeIfPresent(key, remappingFunction);
    }

    @Override
    public ImportStatusInfo compute(String key, BiFunction<? super String, ? super ImportStatusInfo, ? extends ImportStatusInfo> remappingFunction) {
        return delegate.compute(key, remappingFunction);
    }

    @Override
    public ImportStatusInfo merge(String key, ImportStatusInfo value, BiFunction<? super ImportStatusInfo, ? super ImportStatusInfo, ? extends ImportStatusInfo> remappingFunction) {
        return delegate.merge(key, value, remappingFunction);
    }
}
