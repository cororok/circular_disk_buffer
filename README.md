# circular disk buffer

## What is it?
**disk/storage** based circular buffer supporting **queue, stack and deque**.


## When does it need?
It will be useful when the memory based stack/queue does not fit to **hold large data** and needs some **persistent storage** to save them. Even though Database or MessageQueue can handle it but this project tries to provide a **lightweight** solution.

## features
It supports **unfixed and fixed** length of any binary data and there is no limit on the file size until java supports.

## How to use
see code below.
```java
import static java.lang.System.out;

import java.io.IOException;
import java.util.Arrays;

public class Demo {
	final long diskSpaceByte = 1000_000_000; // 1g
	final byte[] input0 = "abcdefghijklmnopqrstuvwxyz".getBytes();
	final byte[] input1 = new byte[] { 1, 2, 3 };
	final String dataFileName = "myFileName";

	void usage() {
		try (Queue q = new CircularDiskQueueAndStack(diskSpaceByte, dataFileName)) {
			q.addLast(input0); // <> stack uses addFirst()
			q.addLast(input1);
			out.println(q.size() == 2);

			byte[] out0 = q.removeFirst(); // deque has additional removeLast()
			byte[] out1 = q.removeFirst();
			out.println(q.size() == 0);

			out.println(Arrays.equals(input0, out0));
			out.println(Arrays.equals(input1, out1));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// if it needs delete data + index file
		}
	}

	void mainClasses() throws IOException {
		// unfixed length of the input data.
		Queue queue = new CircularDiskQueueAndStack(diskSpaceByte, dataFileName);
		Stack stack = new CircularDiskQueueAndStack(diskSpaceByte, dataFileName);

		// unfixed length, can read/write on both first/last. 
		// needs additional duplicated header compared to stack/queue.
		Deque deque = new CircularDiskDeque(diskSpaceByte, dataFileName);

		// efficient when the length of the input data is fixed because 
		// no need to keep size header
		long fixedSizeByte = 500;
		Deque fixedDeque = new CircularDiskDequeFixed(diskSpaceByte, dataFileName, fixedSizeByte);

		// needs to close above
	}
}

```

