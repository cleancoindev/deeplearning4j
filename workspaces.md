As of 0.9.0 ND4j offers additional memory management model: Workspaces. Basically it allows you to reuse memory in cyclic workloads, without JVM Garbage Collector  use for off-heap memory tracking. In other words: at the end of Workspace loop all INDArrays memory content is invalidated.

Here are some examples how to use it with ND4j: LINK

##Neural networks:
For DL4j users Workspaces give better performance just out of box. All you need to do is to choose affordable modes for training & inference of a given model

 .trainingWorkspaceMode(WorkspaceMode.SEPARATE) and/or .inferenceWorkspaceMode(WorkspaceMode.SINGLE) in your neural network configuration. 
For ParallelWrapper there’s also separate configuration option added for training workspace mode.

Difference between **SEPARATE** and **SINGLE** workspaces is tradeoff between performance & memory footprint:
* **SEPARATE** is slower, but uses less memory.
* **SINGLE** is faster, but uses more memory.

However, it’s totally fine to use different modes for training inference: i.e. use SEPARATE for training, and use SINGLE for inference, since inference only involves feed-forward loop, without backpropagation or updaters involved.

So, with workspaces enabled all memory used during training will be reusable, and tracked without JVM GC interference.
The only exclusion is output() method, which uses workspaces (if enabled) internally for feed-forward loop, and then detaches resulting INDArray from workspaces, thus providing you with independent INDArray, which will be handled by JVM GC.

***Please note***: by default training workspace mode is set to **NONE** for now.

##Iterators:
We provide asynchronous prefetch iterators, AsyncDataSetIterator and AsyncMultiDataSetIterator, which are usually used internally. These iterators are optionally using special cyclic workspace mode for smaller memory footprint. Size of workspace in this case will be determined by memory requirements of first DataSet coming out of underlying iterator and buffer size defined by user. However workspace will be adjusted if memory requirements will change over time (i.e. if you’re using variable length time series)

***Caution***: if you’re using custom iterator or RecordReader, please make sure you’re not initializing something huge within first next() call, do that in your constructor, to avoid undesired workspace growth.

If, for some reason, you don’t want your iterator to be wrapped into asynchronous prefetch (i.e. for debugging purposes), there’s special wrappers provided: AsyncShieldDataSetIterator and AsyncShieldMultiDataSetIterator. Basically that’s just thin wrappers that prevent prefetch.

##Garbage Collector:
If your training process uses workspaces, it’s recommended to disable (or reduce frequency of) periodic gc calls. That can be done using this:

```
// this will limit frequency of gc calls to 5000 milliseconds
Nd4j.getMemoryManager().setAutoGcWindow(5000)

// OR you could totally disable it
Nd4j.getMemoryManager().togglePeriodicGc(false);
```

So, you can put that somewhere before your `model.fit(...)` call.





