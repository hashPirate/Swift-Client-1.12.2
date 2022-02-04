package me.pignol.swift.api.util.objects;

import it.unimi.dsi.fastutil.objects.*;
import me.pignol.swift.client.modules.Module;

import java.util.*;

public class ModuleList extends AbstractObjectList<Module> {
    public final static int DEFAULT_INITIAL_CAPACITY = 16;

    protected final boolean wrapped;
    public transient Module[] array;

    public int size;

    public ModuleList(final int capacity) {
        if (capacity < 0) throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
        array = new Module[capacity];
        wrapped = false;
    }


    public ModuleList() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public ModuleList(final Collection<? extends Module> c) {
        this(c.size());
        size = ObjectIterators.unwrap(c.iterator(), array);
    }


    public ModuleList(final ObjectCollection<? extends Module> c) {
        this(c.size());
        size = ObjectIterators.unwrap(c.iterator(), array);
    }

    public ModuleList(final ObjectList<? extends Module> l) {
        this(l.size());
        l.getElements(0, array, 0, size = l.size());
    }

    public ModuleList(final Module[] a) {
        this(a, 0, a.length);
    }

    public ModuleList(final Module[] a, final int offset, final int length) {
        this(length);
        System.arraycopy(a, offset, this.array, 0, length);
        size = length;
    }

    public Module[] elements() {
        return array;
    }

    public void ensureCapacity(final int capacity) {
        if (wrapped) array = ObjectArrays.ensureCapacity(array, capacity, size);
        else {
            if (capacity > array.length) {
                final Module[] t = new Module[capacity];
                System.arraycopy(array, 0, t, 0, size);
                array = t;
            }
        }
    }

    private void grow(final int capacity) {
        if (wrapped) array = ObjectArrays.grow(array, capacity, size);
        else {
            if (capacity > array.length) {
                final int newLength = (int) Math.max(Math.min(2L * array.length, it.unimi.dsi.fastutil.Arrays.MAX_ARRAY_SIZE), capacity);
                final Module[] t = new Module[newLength];
                System.arraycopy(array, 0, t, 0, size);
                array = t;
            }
        }
    }

    public void add(final int index, final Module k) {
        ensureIndex(index);
        grow(size + 1);
        if (index != size) System.arraycopy(array, index, array, index + 1, size - index);
        array[index] = k;
        size++;
    }

    public boolean add(final Module k) {
        grow(size + 1);
        array[size++] = k;
        return true;
    }

    public Module get(final int index) {
        if (index >= size) throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + size + ")");
        return array[index];
    }

    public int indexOf(final Object k) {
        for (int i = 0; i < size; i++)
            if (((k) == null ? (array[i]) == null : (k).equals(array[i]))) return i;
        return -1;
    }

    public int lastIndexOf(final Object k) {
        for (int i = size; i-- != 0;)
            if (((k) == null ? (array[i]) == null : (k).equals(array[i]))) return i;
        return -1;
    }

    public Module remove(final int index) {
        if (index >= size) throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + size + ")");
        final Module old = array[index];
        size--;
        if (index != size) System.arraycopy(array, index + 1, array, index, size - index);
        array[size] = null;
        return old;
    }

    public boolean rem(final Object k) {
        int index = indexOf(k);
        if (index == -1) return false;
        remove(index);
        return true;
    }

    public boolean remove(final Object o) {
        return rem(o);
    }

    public Module set(final int index, final Module k) {
        if (index >= size) throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + size + ")");
        Module old = array[index];
        array[index] = k;
        return old;
    }

    public void clear() {
        Arrays.fill(array, 0, size, null);
        size = 0;
    }

    public int size() {
        return size;
    }

    public void size(final int size) {
        if (size > array.length) ensureCapacity(size);
        if (size > this.size) Arrays.fill(array, this.size, size, (null));
        else Arrays.fill(array, size, this.size, (null));
        this.size = size;
    }

    public boolean isEmpty() {
        return size == 0;
    }


    public void trim(final int n) {
        if (n >= array.length || size == array.length) return;
        final Module[] t = new Module[Math.max(n, size)];
        System.arraycopy(array, 0, t, 0, size);
        array = t;
    }

    public void getElements(final int from, final Object[] a, final int offset, final int length) {
        ObjectArrays.ensureOffsetLength(a, offset, length);
        System.arraycopy(this.array, from, a, offset, length);
    }

    public void removeElements(final int from, final int to) {
        it.unimi.dsi.fastutil.Arrays.ensureFromTo(size, from, to);
        System.arraycopy(array, to, array, from, size - to);
        size -= (to - from);
        int i = to - from;
        while (i-- != 0)
            array[size + i] = null;
    }

    public void addElements(final int index, final Module a[], final int offset, final int length) {
        ensureIndex(index);
        ObjectArrays.ensureOffsetLength(a, offset, length);
        grow(size + length);
        System.arraycopy(this.array, index, this.array, index + length, size - index);
        System.arraycopy(a, offset, this.array, index, length);
        size += length;
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        final Object[] a = this.array;
        int j = 0;
        for (int i = 0; i < size; i++)
            if (!c.contains(a[i])) a[j++] = a[i];
        Arrays.fill(a, j, size, null);
        final boolean modified = size != j;
        size = j;
        return modified;
    }

    public ObjectListIterator<Module> listIterator(final int index) {
        ensureIndex(index);
        return new AbstractObjectListIterator<Module>() {
            int pos = index, last = -1;

            public boolean hasNext() {
                return pos < size;
            }

            public boolean hasPrevious() {
                return pos > 0;
            }

            public Module next() {
                if (!hasNext()) throw new NoSuchElementException();
                return array[last = pos++];
            }

            public Module previous() {
                if (!hasPrevious()) throw new NoSuchElementException();
                return array[last = --pos];
            }

            public int nextIndex() {
                return pos;
            }

            public int previousIndex() {
                return pos - 1;
            }

            public void add(Module k) {
                ModuleList.this.add(pos++, k);
                last = -1;
            }

            public void set(Module k) {
                if (last == -1) throw new IllegalStateException();
                ModuleList.this.set(last, k);
            }

            public void remove() {
                if (last == -1) throw new IllegalStateException();
                ModuleList.this.remove(last);
                /*
                 * If the last operation was a next(), we are removing an
                 * element *before* us, and we must decrease pos
                 * correspondingly.
                 */
                if (last < pos) pos--;
                last = -1;
            }
        };
    }

    public ModuleList clone() {
        ModuleList c = new ModuleList(size);
        System.arraycopy(array, 0, c.array, 0, size);
        c.size = size;
        return c;
    }

    private boolean valEquals(final Module a, final Module b) {
        return a == null ? b == null : a.equals(b);
    }

    public boolean equals(final ModuleList l) {
        if (l == this) return true;
        int s = size();
        if (s != l.size()) return false;
        final Module[] a1 = array;
        final Module[] a2 = l.array;
        while (s-- != 0)
            if (!valEquals(a1[s], a2[s])) return false;
        return true;
    }

}
