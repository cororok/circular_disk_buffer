package cororok.circular_buffer;

/**
 * It only keeps index information so user can know where are start/end point now and from where to where it should
 * read/write data from/into storage. If read/write fails it should rollback.
 * 
 * @author songduk.park cororok@gmail.com
 */
public class CircularBufferInfo {

	/**
	 * maximum space it can hold data
	 * 
	 */
	final long capacity;

	/**
	 * start point
	 */
	private long start;

	/**
	 * end point
	 */
	private long end;

	/**
	 * currently total space it is using to keep data
	 */
	private long length;

	/**
	 * # of elements it has
	 */
	private long size;

	/**
	 * used to rollback
	 */
	CircularBufferInfo previous;

	public CircularBufferInfo(long capacity) {
		this.capacity = capacity;
		if (this.capacity > 0) // previous doesn't need another previous
			this.previous = new CircularBufferInfo(0);
	}

	/**
	 * is used to restore from previous index
	 * 
	 * @param capacity
	 * @param start
	 * @param end
	 * @param size
	 */
	public CircularBufferInfo(long capacity, long start, long end, long size) {
		this(capacity);

		this.start = start;
		this.end = end;
		this.size = size;
		if (size == 0) {
			if (start == 0 && end == capacity)
				throw new RuntimeException("wrong size, it shoud be full");
			length = 0;
		} else if (start > end) { // means reverse state
			if (start == capacity) {
				if (end == 0)
					throw new RuntimeException("wrong size, it shoud be zero");
				else
					length = end;
			} else
				length = (capacity - start) + end;
		} else if (start == end) {
			length = capacity;
		} else {
			length = end - start;
		}
	}

	/**
	 * It adds given space at the right side of end point and move the end point to the right side. Basically there are
	 * three cases. 1st not enough space to put new space then it will return null without a change. 2nd there is enough
	 * space between end point to the end (of the storage) or end point to the start point then it will move end point
	 * to the right side and return an array[2]. 3rd there is not enough space between end point to the end but
	 * it has additional space for the rest between zero to the start point then it will move end point to the left side
	 * of start point and returns an array[4].
	 * 
	 * @param space
	 *            the amount which needs to allocate the data inside.
	 * @return null if available space is not enough or an array which contains two or four index in the array to write
	 *         data.
	 * 
	 *         For example capacity is 5 then it will have 5 cells(space) inside like [0][1][2][3][4](Note: it is a
	 *         single dimension, no is index of array) and start point/end point both are (at) 0. If parameter space is
	 *         3 it will return a 2 size of array [0][3] which tells it needs space from 0 to (before)3 to allocate the
	 *         given 3 space. And start point is still at 0 but end point was moved to 3. Let's say later it removes this
	 *         3 space from first then the start point and end point will be 3. And now add another 3 space then it will
	 *         return 4 size of array [3][5][0][1] because it has to split 3 into to two space from 3 to (before)5 and
	 *         the rest 1 space from 0 to (before)1
	 */
	public long[] addLast(final long space) {
		long[] range;
		if (size == 0) {
			if (end == 0 || end == capacity)
				range = moveRightOnly(0, capacity, space);
			else
				range = moveRightAndOver(end, start, space);
		} else if (start < end) {
			range = moveRightAndOver(end, start, space);
		} else if (start == end) {
			return null; // full
		} else {
			range = moveRightOnly(end, start, space);
		}

		// after
		if (range == null)
			return null;
		if (range.length == 2)
			end = range[1];
		else
			end = range[3];

		increaseLengthSize(space);
		return range;
	}

	/**
	 * It adds given space at the left side of start point and move the start point to the left side
	 * 
	 * @param space
	 *            the amount which needs to allocate the data inside.
	 * 
	 * @return null if available space is not enough or an array which contains two or four index in the array to write
	 *         data.
	 */
	public long[] addFirst(final long space) {
		long[] range;
		if (size == 0) {
			if (end == 0 || end == capacity)
				range = moveLeftOnly(capacity, 0, space);
			else
				range = moveLeftAndOver(start, end, space);
		} else if (start < end) {
			range = moveLeftAndOver(start, end, space);
		} else if (start == end) {
			return null; // full
		} else {
			range = moveLeftOnly(start, end, space);
		}

		// after
		if (range == null)
			return null;
		start = range[0];

		increaseLengthSize(space);
		return range;
	}

	/**
	 * remove space inside as much as the given space from the end point to the right side and move end point to right
	 * side.
	 * 
	 * @param space
	 *            the size of data it will remove the data inside.
	 * @return like addLast/First it returns null if there is no data to remove or either two size of array which tells
	 *         [from][to] or four size of array which tells [from1][to1][from2][to2] that means the original data was
	 *         divided into two peaces from[0] to [1] + from[2] to [3]
	 */
	public long[] removeLast(final long space) {
		if (canRemove(space) == false)
			return null;

		long[] range;
		if (start < end) {
			range = moveLeftOnly(end, start, space);
		} else if (start == end) { // full
			if (end == 0 || end == capacity)
				range = moveLeftOnly(capacity, 0, space);
			else
				range = moveLeftAndOver(end, start, space);
		} else {
			range = moveLeftAndOver(end, start, space);
		}

		// after
		if (range == null)
			return null;
		end = range[0];

		decreaseLengthSize(space);
		return range;
	}

	/**
	 * remove space inside as much as the given space from the start point to the left side and move start point to left
	 * side.
	 * 
	 * @param space
	 *            the size of data it will remove the data inside.
	 * @return like addLast/First it returns null if there is no data to remove or either two size of array which tells
	 *         [from][to] or four size of array which tells [from1][to1][from2][to2] that means the original data was
	 *         divided into two peaces from[0] to [1] + from[2] to [3]
	 */
	public long[] removeFirst(final long space) {
		if (canRemove(space) == false)
			return null;

		long[] range;
		if (start < end) {
			range = moveRightOnly(start, end, space);
		} else if (start == end) { // full
			if (end == 0 || end == capacity)
				range = moveRightOnly(0, capacity, space);
			else
				range = moveRightAndOver(start, end, space);
		} else {
			range = moveRightAndOver(start, end, space);
		}

		// after
		if (range == null)
			return null;
		if (range.length == 2)
			start = range[1];
		else
			start = range[3];

		decreaseLengthSize(space);
		return range;
	}

	private void increaseLengthSize(long space) {
		length += space;
		++size;
	}

	private void decreaseLengthSize(long space) {
		length -= space;
		--size;
	}

	private long[] moveRightOnly(final long start, final long end, final long space) {
		long newStart = start + space;
		if (newStart > end)
			return null;
		return new long[] { start, newStart };
	}

	private long[] moveLeftOnly(final long start, final long end, final long space) {
		long newStart = start - space;
		if (newStart < end)
			return null;
		return new long[] { newStart, start };
	}

	private long[] moveRightAndOver(final long start, final long end, final long space) {
		if (start == capacity) {
			if (space > end)
				return null; // not enough
			return new long[] { 0, space };
		}

		long newStart = start + space;
		if (newStart <= capacity)
			return new long[] { start, newStart };

		newStart = newStart - capacity; // there is a 2nd part
		if (newStart > end)
			return null;
		return new long[] { start, capacity, 0, newStart };
	}

	private long[] moveLeftAndOver(final long start, final long end, final long space) {
		if (start == 0) {
			long newStart = capacity - space;
			if (newStart < end)
				return null; // not enough
			return new long[] { newStart, capacity };
		}

		long newStart = start - space;
		if (newStart >= 0)
			return new long[] { newStart, start };

		// newStart is negative
		newStart = newStart + capacity; // there is a 2nd part
		if (newStart < end)
			return null;
		return new long[] { newStart, capacity, 0, start };
	}

	public void backupStatus() {
		previous.start = start;
		previous.end = end;
		previous.length = length;
		previous.size = size;
	}

	/**
	 * go back to previous status because of some error
	 */
	public void rollback() {
		start = previous.start;
		end = previous.end;
		length = previous.length;
		size = previous.size;
	}

	// get methods start
	public long getStart() {
		return start;
	}

	public long getEnd() {
		return end;
	}

	public long getCapacity() {
		return capacity;
	}

	public long size() {
		return size;
	}

	public long length() {
		return length;
	}
	// get methods end

	public boolean isEmpty() {
		return 0 == length;
	}

	public boolean isFull() {
		return capacity == length;
	}

	private boolean canRemove(long space) {
		if (size == 0)
			return false;

		return this.length >= space;
	}

	public boolean canAdd(long add) {
		return getAvailableSpace() >= add;
	}

	@Override
	public String toString() {
		return "CircularBuffer [capacity=" + capacity + ", start=" + start + ", end=" + end + ", length=" + length
				+ ", size=" + size + ", previous=" + previous + "]";
	}

	public long getAvailableSpace() {
		return capacity - length;
	}

}
