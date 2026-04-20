# Directed Graph Analyzer (Java)

A Java-based application that analyzes directed graphs to determine whether they are **acyclic** or **cyclic**. The project uses a **sink removal algorithm** for acyclicity checking and **Depth-First Search (DFS)** for cycle detection. It also includes a benchmarking module to evaluate performance across multiple graph inputs.

---

## 🚀 Features

- Read directed graphs from `.txt` files
- Determine if a graph is **acyclic (DAG)** or **cyclic**
- Step-by-step **sink removal process**
- Detect and display a cycle using DFS (if cyclic)
- Benchmark multiple graph files
- Measure runtime using high-precision timing (`System.nanoTime()`)
- Display summary statistics (min, max, average runtime)

---

## 🛠️ Technologies Used

- Java
- Java Collections Framework (`Map`, `Set`, `List`)
- File handling with `BufferedReader`
- GUI file selection using `JFileChooser`
- High-resolution timing with `System.nanoTime()`

---

## 📁 Project Structure
├── Main.java
├── runBenchmark.java
├── benchmarks/
│ ├── acyclic/
│ └── cyclic/
└── README.md

---

### File Descriptions

- **Main.java**  
  Handles file selection, graph loading, analysis, and result display.

- **runBenchmark.java**  
  Processes multiple graph files, measures runtime, and prints performance summaries.

---

## 📄 Input File Format

Graph input files must follow this structure:

```txt
<number_of_vertices>
from to
from to
from to
...
Example
5
0 1
1 2
2 3
3 4

```

⚙️ How It Works
🔹 Acyclicity Check (Sink Removal Algorithm)

A sink is a vertex with out-degree = 0.

Algorithm:

Find a sink
Remove it from the graph
Repeat until:
Graph becomes empty → ✅ Acyclic
No sink found → ❌ Cyclic

🔹 Cycle Detection (DFS)

If the graph is cyclic:

DFS is used to detect a back edge
A recursion stack (onStack) is used
When a node is revisited in the same path → a cycle is found
The cycle is then reconstructed and printed

---

▶️ How to Run
```1. Compile
javac Main.java runBenchmark.java
```
```2. Run Main Program
java Main
```
- Opens a file chooser
- Select a graph file
- Displays:
    Removed sinks
    Whether graph is acyclic/cyclic
    A cycle (if found)

```3. Run Benchmark
java runBenchmark
```
- Processes graph files in:
    benchmarks/acyclic
    benchmarks/cyclic
- Measures runtime
- Validates correctness
- Displays summary statistics

---

📊 Benchmarking

The benchmark module:

- Processes multiple graph files automatically
- Measures execution time using System.nanoTime()
- Compares results with expected graph type
- Outputs:
    Runtime per file
    Minimum runtime
    Maximum runtime
    Average runtime

--- 

📚 Key Concepts Demonstrated
- Directed graph representation using adjacency maps
- Sink removal algorithm for DAG detection
- DFS-based cycle detection
- Efficient vertex and edge management
- File parsing and validation
- Performance benchmarking in Java
