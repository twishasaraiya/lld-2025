Implement a thread pool executor where:

Multiple producer threads submit tasks to a shared queue
Fixed number of consumer threads pick up and execute tasks
Support task priorities and graceful shutdown
Handle thread pool resizing dynamically


----
Implement a TaskScheduler class in Java.
 It should have a constructor TaskScheduler(int numThreads)
 that initializes a fixed-size thread pool. The class should expose a
 method void submit(Runnable task) which adds a task to a queue.


 The worker threads should pull tasks from this queue and execute them.
 The system should be able to handle a high volume of concurrent submissions
 and shut down gracefully without losing any tasks