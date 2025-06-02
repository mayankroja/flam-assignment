### Q1. LRU Cache

An implementation of an **LRU (Least Recently Used) Cache**, which makes memory management efficient by removing the least recently used item when the cache is full.
It utilizes a combination of a **hash map** (for O(1) access) and a **doubly linked list** (to monitor the usage order).
Whenever a key is accessed or changed, it is brought to the front (most recently used). If the cache is full, the least recently used (at the end) is evicted.

---

### Q2. Custom HashMap

This is a minimal implementation of a **HashMap** written from scratch using Python without Python's built-in `dict`.
It provides standard operations such as `put`, `get`, and `remove` in worst-case **O(1)** time with the help of **open addressing with linear probing** to resolve collisions.
The internal array adapts automatically using **rehashing** when the load factor exceeds some value so that efficiency can be kept up.

---
