/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tools.caffeine.queue;

import java.lang.reflect.Field;
import java.util.AbstractQueue;
import java.util.Iterator;

import static com.google.common.math.IntMath.ceilingPowerOfTwo;
import static tools.caffeine.queue.UnsafeAccess.UNSAFE;
import static tools.caffeine.queue.UnsafeRefArrayAccess.*;

/**
 * An MPSC array queue which starts at <i>initialCapacity</i> and grows to <i>maxCapacity</i> in
 * linked chunks of the initial size. The queue grows only when the current buffer is full and
 * elements are not copied on resize, instead a link to the new buffer is stored in the old buffer
 * for the consumer to follow.<br>
 * <p>
 * This is a shaded copy of <tt>MpscGrowableArrayQueue</tt> provided by
 * <a href="https://github.com/JCTools/JCTools">JCTools</a> from version 2.0.
 *
 * @author nitsanw@yahoo.com (Nitsan Wakart)
 */
public final class MpscGrowableArrayQueue<E> extends MpscChunkedArrayQueue<E> {

    /**
     * @param initialCapacity the queue initial capacity. If chunk size is fixed this will be the
     *                        chunk size. Must be 2 or more.
     * @param maxCapacity     the maximum capacity will be rounded up to the closest power of 2 and will
     *                        be the upper limit of number of elements in this queue. Must be 4 or more and round up
     *                        to a larger power of 2 than initialCapacity.
     */
    public MpscGrowableArrayQueue(int initialCapacity, int maxCapacity) {
        super(initialCapacity, maxCapacity);
    }

    @Override
    protected int getNextBufferSize(E[] buffer) {
        long maxSize = maxQueueCapacity / 2;
        if (buffer.length > maxSize) {
            throw new IllegalStateException();
        }
        final int newSize = 2 * (buffer.length - 1);
        return newSize + 1;
    }

    @Override
    protected long getCurrentBufferCapacity(long mask) {
        return (mask + 2 == maxQueueCapacity) ? maxQueueCapacity : mask;
    }
}

@SuppressWarnings("OvershadowingSubclassFields")
abstract class MpscChunkedArrayQueue<E> extends MpscChunkedArrayQueueColdProducerFields<E> {
    byte p000, p001, p002, p003, p004, p005, p006, p007;
    byte p008, p009, p010, p011, p012, p013, p014, p015;
    byte p016, p017, p018, p019, p020, p021, p022, p023;
    byte p024, p025, p026, p027, p028, p029, p030, p031;
    byte p032, p033, p034, p035, p036, p037, p038, p039;
    byte p040, p041, p042, p043, p044, p045, p046, p047;
    byte p048, p049, p050, p051, p052, p053, p054, p055;
    byte p056, p057, p058, p059, p060, p061, p062, p063;
    byte p064, p065, p066, p067, p068, p069, p070, p071;
    byte p072, p073, p074, p075, p076, p077, p078, p079;
    byte p080, p081, p082, p083, p084, p085, p086, p087;
    byte p088, p089, p090, p091, p092, p093, p094, p095;
    byte p096, p097, p098, p099, p100, p101, p102, p103;
    byte p104, p105, p106, p107, p108, p109, p110, p111;
    byte p112, p113, p114, p115, p116, p117, p118, p119;

    MpscChunkedArrayQueue(int initialCapacity, int maxCapacity) {
        super(initialCapacity, maxCapacity);
    }

    @Override
    protected long availableInQueue(long pIndex, long cIndex) {
        return maxQueueCapacity - (pIndex - cIndex);
    }

    @Override
    public int capacity() {
        return (int) (maxQueueCapacity / 2);
    }

    @Override
    protected int getNextBufferSize(E[] buffer) {
        return buffer.length;
    }

    @Override
    protected long getCurrentBufferCapacity(long mask) {
        return mask;
    }
}

abstract class MpscChunkedArrayQueueColdProducerFields<E> extends BaseMpscLinkedArrayQueue<E> {
    protected final long maxQueueCapacity;

    MpscChunkedArrayQueueColdProducerFields(int initialCapacity, int maxCapacity) {
        super(initialCapacity);
        if (maxCapacity < 4) {
            throw new IllegalArgumentException("Max capacity must be 4 or more");
        }
        if (ceilingPowerOfTwo(initialCapacity) >= ceilingPowerOfTwo(maxCapacity)) {
            throw new IllegalArgumentException(
                    "Initial capacity cannot exceed maximum capacity(both rounded up to a power of 2)");
        }
        maxQueueCapacity = ((long) ceilingPowerOfTwo(maxCapacity)) << 1;
    }
}

abstract class BaseMpscLinkedArrayQueuePad1<E> extends AbstractQueue<E> {
    byte p000, p001, p002, p003, p004, p005, p006, p007;
    byte p008, p009, p010, p011, p012, p013, p014, p015;
    byte p016, p017, p018, p019, p020, p021, p022, p023;
    byte p024, p025, p026, p027, p028, p029, p030, p031;
    byte p032, p033, p034, p035, p036, p037, p038, p039;
    byte p040, p041, p042, p043, p044, p045, p046, p047;
    byte p048, p049, p050, p051, p052, p053, p054, p055;
    byte p056, p057, p058, p059, p060, p061, p062, p063;
    byte p064, p065, p066, p067, p068, p069, p070, p071;
    byte p072, p073, p074, p075, p076, p077, p078, p079;
    byte p080, p081, p082, p083, p084, p085, p086, p087;
    byte p088, p089, p090, p091, p092, p093, p094, p095;
    byte p096, p097, p098, p099, p100, p101, p102, p103;
    byte p104, p105, p106, p107, p108, p109, p110, p111;
    byte p112, p113, p114, p115, p116, p117, p118, p119;
}

abstract class BaseMpscLinkedArrayQueueProducerFields<E> extends BaseMpscLinkedArrayQueuePad1<E> {
    protected long producerIndex;
}

@SuppressWarnings("OvershadowingSubclassFields")
abstract class BaseMpscLinkedArrayQueuePad2<E> extends BaseMpscLinkedArrayQueueProducerFields<E> {
    byte p000, p001, p002, p003, p004, p005, p006, p007;
    byte p008, p009, p010, p011, p012, p013, p014, p015;
    byte p016, p017, p018, p019, p020, p021, p022, p023;
    byte p024, p025, p026, p027, p028, p029, p030, p031;
    byte p032, p033, p034, p035, p036, p037, p038, p039;
    byte p040, p041, p042, p043, p044, p045, p046, p047;
    byte p048, p049, p050, p051, p052, p053, p054, p055;
    byte p056, p057, p058, p059, p060, p061, p062, p063;
    byte p064, p065, p066, p067, p068, p069, p070, p071;
    byte p072, p073, p074, p075, p076, p077, p078, p079;
    byte p080, p081, p082, p083, p084, p085, p086, p087;
    byte p088, p089, p090, p091, p092, p093, p094, p095;
    byte p096, p097, p098, p099, p100, p101, p102, p103;
    byte p104, p105, p106, p107, p108, p109, p110, p111;
    byte p112, p113, p114, p115, p116, p117, p118, p119;
}

@SuppressWarnings("NullAway")
abstract class BaseMpscLinkedArrayQueueConsumerFields<E> extends BaseMpscLinkedArrayQueuePad2<E> {
    protected long consumerMask;
    protected E[] consumerBuffer;
    protected long consumerIndex;
}

@SuppressWarnings("OvershadowingSubclassFields")
abstract class BaseMpscLinkedArrayQueuePad3<E> extends BaseMpscLinkedArrayQueueConsumerFields<E> {
    byte p000, p001, p002, p003, p004, p005, p006, p007;
    byte p008, p009, p010, p011, p012, p013, p014, p015;
    byte p016, p017, p018, p019, p020, p021, p022, p023;
    byte p024, p025, p026, p027, p028, p029, p030, p031;
    byte p032, p033, p034, p035, p036, p037, p038, p039;
    byte p040, p041, p042, p043, p044, p045, p046, p047;
    byte p048, p049, p050, p051, p052, p053, p054, p055;
    byte p056, p057, p058, p059, p060, p061, p062, p063;
    byte p064, p065, p066, p067, p068, p069, p070, p071;
    byte p072, p073, p074, p075, p076, p077, p078, p079;
    byte p080, p081, p082, p083, p084, p085, p086, p087;
    byte p088, p089, p090, p091, p092, p093, p094, p095;
    byte p096, p097, p098, p099, p100, p101, p102, p103;
    byte p104, p105, p106, p107, p108, p109, p110, p111;
    byte p112, p113, p114, p115, p116, p117, p118, p119;
}

@SuppressWarnings("NullAway")
abstract class BaseMpscLinkedArrayQueueColdProducerFields<E>
        extends BaseMpscLinkedArrayQueuePad3<E> {
    protected volatile long producerLimit;
    protected long producerMask;
    protected E[] producerBuffer;
}

@SuppressWarnings({"PMD", "NullAway", "restriction"})
abstract class BaseMpscLinkedArrayQueue<E> extends BaseMpscLinkedArrayQueueColdProducerFields<E> {
    // No post padding here, subclasses must add

    private final static long P_INDEX_OFFSET;
    private final static long C_INDEX_OFFSET;
    private final static long P_LIMIT_OFFSET;
    private final static Object JUMP = new Object();

    static {
        try {
            Field iField = BaseMpscLinkedArrayQueueProducerFields.class.getDeclaredField("producerIndex");
            P_INDEX_OFFSET = UNSAFE.objectFieldOffset(iField);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        try {
            Field iField = BaseMpscLinkedArrayQueueConsumerFields.class.getDeclaredField("consumerIndex");
            C_INDEX_OFFSET = UNSAFE.objectFieldOffset(iField);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        try {
            Field iField =
                    BaseMpscLinkedArrayQueueColdProducerFields.class.getDeclaredField("producerLimit");
            P_LIMIT_OFFSET = UNSAFE.objectFieldOffset(iField);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param initialCapacity the queue initial capacity. If chunk size is fixed this will be the
     *                        chunk size. Must be 2 or more.
     */
    BaseMpscLinkedArrayQueue(final int initialCapacity) {
        if (initialCapacity < 2) {
            throw new IllegalArgumentException("Initial capacity must be 2 or more");
        }

        int p2capacity = ceilingPowerOfTwo(initialCapacity);
        // leave lower bit of mask clear
        long mask = (p2capacity - 1L) << 1;
        // need extra element to point at next array
        E[] buffer = allocate(p2capacity + 1);
        producerBuffer = buffer;
        producerMask = mask;
        consumerBuffer = buffer;
        consumerMask = mask;
        soProducerLimit(mask); // we know it's all empty to start with
    }

    /**
     * This method assumes index is actually (index << 1) because lower bit is used for resize. This
     * is compensated for by reducing the element shift. The computation is constant folded, so
     * there's no cost.
     */
    private static long modifiedCalcElementOffset(long index, long mask) {
        return REF_ARRAY_BASE + ((index & mask) << (REF_ELEMENT_SHIFT - 1));
    }

    private static long nextArrayOffset(final long mask) {
        return modifiedCalcElementOffset(mask + 2, Long.MAX_VALUE);
    }

    @SuppressWarnings("unchecked")
    public static <E> E[] allocate(int capacity) {
        return (E[]) new Object[capacity];
    }

    @Override
    public final Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }

    @Override
    @SuppressWarnings("MissingDefault")
    public boolean offer(final E e) {
        if (null == e) {
            throw new NullPointerException();
        }

        long mask;
        E[] buffer;
        long pIndex;

        while (true) {
            long producerLimit = lvProducerLimit();
            pIndex = lvProducerIndex();
            // lower bit is indicative of resize, if we see it we spin until it's cleared
            if ((pIndex & 1) == 1) {
                continue;
            }
            // pIndex is even (lower bit is 0) -> actual index is (pIndex >> 1)

            // mask/buffer may get changed by resizing -> only use for array access after successful CAS.
            mask = this.producerMask;
            buffer = this.producerBuffer;
            // a successful CAS ties the ordering, lv(pIndex)-[mask/buffer]->cas(pIndex)

            // assumption behind this optimization is that queue is almost always empty or near empty
            if (producerLimit <= pIndex) {
                int result = offerSlowPath(mask, pIndex, producerLimit);
                switch (result) {
                    case 0:
                        break;
                    case 1:
                        continue;
                    case 2:
                        return false;
                    case 3:
                        resize(mask, buffer, pIndex, e);
                        return true;
                }
            }

            if (casProducerIndex(pIndex, pIndex + 2)) {
                break;
            }
        }
        // INDEX visible before ELEMENT, consistent with consumer expectation
        final long offset = modifiedCalcElementOffset(pIndex, mask);
        soElement(buffer, offset, e);
        return true;
    }

    /**
     * We do not inline resize into this method because we do not resize on fill.
     */
    private int offerSlowPath(long mask, long pIndex, long producerLimit) {
        int result;
        final long cIndex = lvConsumerIndex();
        long bufferCapacity = getCurrentBufferCapacity(mask);
        result = 0;// 0 - goto pIndex CAS
        if (cIndex + bufferCapacity > pIndex) {
            if (!casProducerLimit(producerLimit, cIndex + bufferCapacity)) {
                result = 1;// retry from top
            }
        }
        // full and cannot grow
        else if (availableInQueue(pIndex, cIndex) <= 0) {
            result = 2;// -> return false;
        }
        // grab index for resize -> set lower bit
        else if (casProducerIndex(pIndex, pIndex + 1)) {
            result = 3;// -> resize
        } else {
            result = 1;// failed resize attempt, retry from top
        }
        return result;
    }

    /**
     * @return available elements in queue * 2
     */
    protected abstract long availableInQueue(long pIndex, final long cIndex);

    /**
     * {@inheritDoc}
     * <p>
     * This implementation is correct for single consumer thread use only.
     */
    @Override
    @SuppressWarnings("unchecked")
    public E poll() {
        final E[] buffer = consumerBuffer;
        final long index = consumerIndex;
        final long mask = consumerMask;

        final long offset = modifiedCalcElementOffset(index, mask);
        Object e = lvElement(buffer, offset);// LoadLoad
        if (e == null) {
            if (index != lvProducerIndex()) {
                // poll() == null iff queue is empty, null element is not strong enough indicator, so we
                // must
                // check the producer index. If the queue is indeed not empty we spin until element is
                // visible.
                do {
                    e = lvElement(buffer, offset);
                } while (e == null);
            } else {
                return null;
            }
        }
        if (e == JUMP) {
            final E[] nextBuffer = getNextBuffer(buffer, mask);
            return newBufferPoll(nextBuffer, index);
        }
        soElement(buffer, offset, null);
        soConsumerIndex(index + 2);
        return (E) e;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation is correct for single consumer thread use only.
     */
    @SuppressWarnings("unchecked")
    @Override
    public E peek() {
        final E[] buffer = consumerBuffer;
        final long index = consumerIndex;
        final long mask = consumerMask;

        final long offset = modifiedCalcElementOffset(index, mask);
        Object e = lvElement(buffer, offset);// LoadLoad
        if (e == null && index != lvProducerIndex()) {
            // peek() == null iff queue is empty, null element is not strong enough indicator, so we must
            // check the producer index. If the queue is indeed not empty we spin until element is
            // visible.
            while ((e = lvElement(buffer, offset)) == null) {
                ;
            }
        }
        if (e == JUMP) {
            return newBufferPeek(getNextBuffer(buffer, mask), index);
        }
        return (E) e;
    }

    @SuppressWarnings("unchecked")
    private E[] getNextBuffer(final E[] buffer, final long mask) {
        final long nextArrayOffset = nextArrayOffset(mask);
        final E[] nextBuffer = (E[]) lvElement(buffer, nextArrayOffset);
        soElement(buffer, nextArrayOffset, null);
        return nextBuffer;
    }

    private E newBufferPoll(E[] nextBuffer, final long index) {
        final long offsetInNew = newBufferAndOffset(nextBuffer, index);
        final E n = lvElement(nextBuffer, offsetInNew);// LoadLoad
        if (n == null) {
            throw new IllegalStateException("new buffer must have at least one element");
        }
        soElement(nextBuffer, offsetInNew, null);// StoreStore
        soConsumerIndex(index + 2);
        return n;
    }

    private E newBufferPeek(E[] nextBuffer, final long index) {
        final long offsetInNew = newBufferAndOffset(nextBuffer, index);
        final E n = lvElement(nextBuffer, offsetInNew);// LoadLoad
        if (null == n) {
            throw new IllegalStateException("new buffer must have at least one element");
        }
        return n;
    }

    private long newBufferAndOffset(E[] nextBuffer, final long index) {
        consumerBuffer = nextBuffer;
        consumerMask = (nextBuffer.length - 2L) << 1;
        final long offsetInNew = modifiedCalcElementOffset(index, consumerMask);
        return offsetInNew;
    }

    @Override
    public final int size() {
        // NOTE: because indices are on even numbers we cannot use the size util.

        /*
         * It is possible for a thread to be interrupted or reschedule between the read of the producer
         * and consumer indices, therefore protection is required to ensure size is within valid range.
         * In the event of concurrent polls/offers to this method the size is OVER estimated as we read
         * consumer index BEFORE the producer index.
         */
        long after = lvConsumerIndex();
        long size;
        while (true) {
            final long before = after;
            final long currentProducerIndex = lvProducerIndex();
            after = lvConsumerIndex();
            if (before == after) {
                size = ((currentProducerIndex - after) >> 1);
                break;
            }
        }
        // Long overflow is impossible, so size is always positive. Integer overflow is possible for the
        // unbounded
        // indexed queues.
        if (size > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        } else {
            return (int) size;
        }
    }

    @Override
    public final boolean isEmpty() {
        // Order matters!
        // Loading consumer before producer allows for producer increments after consumer index is read.
        // This ensures this method is conservative in it's estimate. Note that as this is an MPMC there
        // is
        // nothing we can do to make this an exact method.
        return (this.lvConsumerIndex() == this.lvProducerIndex());
    }

    private long lvProducerIndex() {
        return UNSAFE.getLongVolatile(this, P_INDEX_OFFSET);
    }

    private long lvConsumerIndex() {
        return UNSAFE.getLongVolatile(this, C_INDEX_OFFSET);
    }

    private void soProducerIndex(long v) {
        UNSAFE.putOrderedLong(this, P_INDEX_OFFSET, v);
    }

    private boolean casProducerIndex(long expect, long newValue) {
        return UNSAFE.compareAndSwapLong(this, P_INDEX_OFFSET, expect, newValue);
    }

    private void soConsumerIndex(long v) {
        UNSAFE.putOrderedLong(this, C_INDEX_OFFSET, v);
    }

    private long lvProducerLimit() {
        return producerLimit;
    }

    private boolean casProducerLimit(long expect, long newValue) {
        return UNSAFE.compareAndSwapLong(this, P_LIMIT_OFFSET, expect, newValue);
    }

    private void soProducerLimit(long v) {
        UNSAFE.putOrderedLong(this, P_LIMIT_OFFSET, v);
    }

    public long currentProducerIndex() {
        return lvProducerIndex() / 2;
    }

    public long currentConsumerIndex() {
        return lvConsumerIndex() / 2;
    }

    public abstract int capacity();

    public boolean relaxedOffer(E e) {
        return offer(e);
    }

    @SuppressWarnings("unchecked")
    public E relaxedPoll() {
        final E[] buffer = consumerBuffer;
        final long index = consumerIndex;
        final long mask = consumerMask;

        final long offset = modifiedCalcElementOffset(index, mask);
        Object e = lvElement(buffer, offset);// LoadLoad
        if (e == null) {
            return null;
        }
        if (e == JUMP) {
            final E[] nextBuffer = getNextBuffer(buffer, mask);
            return newBufferPoll(nextBuffer, index);
        }
        soElement(buffer, offset, null);
        soConsumerIndex(index + 2);
        return (E) e;
    }

    @SuppressWarnings("unchecked")
    public E relaxedPeek() {
        final E[] buffer = consumerBuffer;
        final long index = consumerIndex;
        final long mask = consumerMask;

        final long offset = modifiedCalcElementOffset(index, mask);
        Object e = lvElement(buffer, offset);// LoadLoad
        if (e == JUMP) {
            return newBufferPeek(getNextBuffer(buffer, mask), index);
        }
        return (E) e;
    }

    private void resize(long oldMask, E[] oldBuffer, long pIndex, final E e) {
        int newBufferLength = getNextBufferSize(oldBuffer);
        final E[] newBuffer = allocate(newBufferLength);

        producerBuffer = newBuffer;
        final int newMask = (newBufferLength - 2) << 1;
        producerMask = newMask;

        final long offsetInOld = modifiedCalcElementOffset(pIndex, oldMask);
        final long offsetInNew = modifiedCalcElementOffset(pIndex, newMask);


        soElement(newBuffer, offsetInNew, e);// element in new array
        soElement(oldBuffer, nextArrayOffset(oldMask), newBuffer);// buffer linked

        // ASSERT code
        final long cIndex = lvConsumerIndex();
        final long availableInQueue = availableInQueue(pIndex, cIndex);
        if (availableInQueue <= 0) {
            throw new IllegalStateException();
        }

        // Invalidate racing CASs
        // We never set the limit beyond the bounds of a buffer
        soProducerLimit(pIndex + Math.min(newMask, availableInQueue));

        // make resize visible to the other producers
        soProducerIndex(pIndex + 2);

        // INDEX visible before ELEMENT, consistent with consumer expectation

        // make resize visible to consumer
        soElement(oldBuffer, offsetInOld, JUMP);
    }

    /**
     * @return next buffer size(inclusive of next array pointer)
     */
    protected abstract int getNextBufferSize(E[] buffer);

    /**
     * @return current buffer capacity for elements (excluding next pointer and jump entry) * 2
     */
    protected abstract long getCurrentBufferCapacity(long mask);
}


/**
 * A concurrent access enabling class used by circular array based queues this class exposes an
 * offset computation method along with differently memory fenced load/store methods into the
 * underlying array. The class is pre-padded and the array is padded on either side to help with
 * False sharing prvention. It is expected theat subclasses handle post padding.
 * <p>
 * Offset calculation is separate from access to enable the reuse of a give compute offset.
 * <p>
 * Load/Store methods using a <i>buffer</i> parameter are provided to allow the prevention of final
 * field reload after a LoadLoad barrier.
 * <p>
 */
@SuppressWarnings("restriction")
final class UnsafeRefArrayAccess {
    public static final long REF_ARRAY_BASE;
    public static final int REF_ELEMENT_SHIFT;

    static {
        final int scale = UNSAFE.arrayIndexScale(Object[].class);
        if (4 == scale) {
            REF_ELEMENT_SHIFT = 2;
        } else if (8 == scale) {
            REF_ELEMENT_SHIFT = 3;
        } else {
            throw new IllegalStateException("Unknown pointer size");
        }
        REF_ARRAY_BASE = UNSAFE.arrayBaseOffset(Object[].class);
    }

    private UnsafeRefArrayAccess() {
    }

    /**
     * A plain store (no ordering/fences) of an element to a given offset
     *
     * @param buffer this.buffer
     * @param offset computed via {@link UnsafeRefArrayAccess#calcElementOffset(long)}
     * @param e      an orderly kitty
     */
    public static <E> void spElement(E[] buffer, long offset, E e) {
        UNSAFE.putObject(buffer, offset, e);
    }

    /**
     * An ordered store(store + StoreStore barrier) of an element to a given offset
     *
     * @param buffer this.buffer
     * @param offset computed via {@link UnsafeRefArrayAccess#calcElementOffset}
     * @param e      an orderly kitty
     */
    public static <E> void soElement(E[] buffer, long offset, E e) {
        UNSAFE.putOrderedObject(buffer, offset, e);
    }

    /**
     * A plain load (no ordering/fences) of an element from a given offset.
     *
     * @param buffer this.buffer
     * @param offset computed via {@link UnsafeRefArrayAccess#calcElementOffset(long)}
     * @return the element at the offset
     */
    @SuppressWarnings("unchecked")
    public static <E> E lpElement(E[] buffer, long offset) {
        return (E) UNSAFE.getObject(buffer, offset);
    }

    /**
     * A volatile load (load + LoadLoad barrier) of an element from a given offset.
     *
     * @param buffer this.buffer
     * @param offset computed via {@link UnsafeRefArrayAccess#calcElementOffset(long)}
     * @return the element at the offset
     */
    @SuppressWarnings("unchecked")
    public static <E> E lvElement(E[] buffer, long offset) {
        return (E) UNSAFE.getObjectVolatile(buffer, offset);
    }

    /**
     * @param index desirable element index
     * @return the offset in bytes within the array for a given index.
     */
    public static long calcElementOffset(long index) {
        return REF_ARRAY_BASE + (index << REF_ELEMENT_SHIFT);
    }
}
