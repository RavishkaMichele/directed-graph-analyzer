/*
 * Name: D.F.R.Michele
 * Student ID: W2119981/20231647
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main {

    static class AnalysisResult {
        boolean acyclic;
        List<Integer> removedSinks;
        List<Integer> cycle;

        AnalysisResult(boolean acyclic, List<Integer> removedSinks, List<Integer> cycle) {
            this.acyclic = acyclic;
            this.removedSinks = removedSinks;
            this.cycle = cycle;
        }
    }

    static class Graph {
        private final Map<Integer, Set<Integer>> outgoing;
        private final Map<Integer, Set<Integer>> incoming;

        public Graph() {
            outgoing = new LinkedHashMap<>();
            incoming = new LinkedHashMap<>();
        }

        public void addVertex(int v) {
            outgoing.putIfAbsent(v, new LinkedHashSet<>());
            incoming.putIfAbsent(v, new LinkedHashSet<>());
        }

        public void addEdge(int from, int to) {
            addVertex(from);
            addVertex(to);

            if (!outgoing.get(from).contains(to)) {
                outgoing.get(from).add(to);
                incoming.get(to).add(from);
            }
        }

        public boolean containsVertex(int v) {
            return outgoing.containsKey(v);
        }

        public Set<Integer> getVertices() {
            return outgoing.keySet();
        }

        public Set<Integer> getOutgoingNeighbors(int v) {
            return outgoing.getOrDefault(v, Collections.emptySet());
        }

        public int outDegree(int v) {
            return outgoing.getOrDefault(v, Collections.emptySet()).size();
        }

        public boolean isEmpty() {
            return outgoing.isEmpty();
        }

        public int vertexCount() {
            return outgoing.size();
        }

        public void removeVertex(int v) {
            if (!containsVertex(v)) {
                return;
            }

            for (int predecessor : new ArrayList<>(incoming.get(v))) {
                outgoing.get(predecessor).remove(v);
            }

            for (int successor : new ArrayList<>(outgoing.get(v))) {
                incoming.get(successor).remove(v);
            }

            outgoing.remove(v);
            incoming.remove(v);
        }

        public Integer findAnySink() {
            for (int v : outgoing.keySet()) {
                if (outDegree(v) == 0) {
                    return v;
                }
            }
            return null;
        }

        public Graph copy() {
            Graph copy = new Graph();
            for (int v : outgoing.keySet()) {
                copy.addVertex(v);
            }
            for (int from : outgoing.keySet()) {
                for (int to : outgoing.get(from)) {
                    copy.addEdge(from, to);
                }
            }
            return copy;
        }
    }

    static Graph readGraphFromFile(String filename) throws IOException {
        Graph g = new Graph();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String firstLine = br.readLine();

            if (firstLine == null) {
                throw new IOException("Input file is empty.");
            }

            firstLine = firstLine.trim();
            if (firstLine.isEmpty()) {
                throw new IOException("First line is empty. Expected number of vertices.");
            }

            int numberOfVertices;
            try {
                numberOfVertices = Integer.parseInt(firstLine);
            } catch (NumberFormatException e) {
                throw new IOException("First line must be an integer giving the number of vertices.");
            }

            if (numberOfVertices < 0) {
                throw new IOException("Number of vertices cannot be negative.");
            }

            for (int i = 0; i < numberOfVertices; i++) {
                g.addVertex(i);
            }

            String line;
            int lineNumber = 1;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\s+");
                if (parts.length != 2) {
                    throw new IOException("Invalid edge format at line " + lineNumber + ": " + line);
                }

                int from;
                int to;

                try {
                    from = Integer.parseInt(parts[0]);
                    to = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    throw new IOException("Non-integer edge at line " + lineNumber + ": " + line);
                }

                if (from < 0 || from >= numberOfVertices || to < 0 || to >= numberOfVertices) {
                    throw new IOException(
                        "Vertex out of range at line " + lineNumber + ": " + line +
                        ". Expected vertices between 0 and " + (numberOfVertices - 1)
                    );
                }

                g.addEdge(from, to);
            }
        }

        return g;
    }

    static AnalysisResult analyseGraph(Graph original) {
        Graph working = original.copy();
        List<Integer> removedSinks = new ArrayList<>();

        while (!working.isEmpty()) {
            Integer sink = working.findAnySink();

            if (sink == null) {
                List<Integer> cycle = findCycle(working);
                return new AnalysisResult(false, removedSinks, cycle);
            }

            removedSinks.add(sink);
            working.removeVertex(sink);
        }

        return new AnalysisResult(true, removedSinks, null);
    }

    static List<Integer> findCycle(Graph g) {
        Set<Integer> visited = new HashSet<>();
        Set<Integer> onStack = new HashSet<>();
        List<Integer> path = new ArrayList<>();

        for (int vertex : g.getVertices()) {
            if (!visited.contains(vertex)) {
                List<Integer> cycle = dfsCycle(vertex, g, visited, onStack, path);
                if (cycle != null) {
                    return cycle;
                }
            }
        }

        return null;
    }

    static List<Integer> dfsCycle(int current, Graph g, Set<Integer> visited,
                                  Set<Integer> onStack, List<Integer> path) {
        visited.add(current);
        onStack.add(current);
        path.add(current);

        for (int neighbor : g.getOutgoingNeighbors(current)) {
            if (!visited.contains(neighbor)) {
                List<Integer> cycle = dfsCycle(neighbor, g, visited, onStack, path);
                if (cycle != null) {
                    return cycle;
                }
            } else if (onStack.contains(neighbor)) {
                int startIndex = path.indexOf(neighbor);
                List<Integer> cycle = new ArrayList<>();

                for (int i = startIndex; i < path.size(); i++) {
                    cycle.add(path.get(i));
                }
                cycle.add(neighbor);

                return cycle;
            }
        }

        onStack.remove(current);
        path.remove(path.size() - 1);
        return null;
    }

    static void printResult(AnalysisResult result) {
        System.out.println();
        System.out.println("===== RESULT =====");

        if (result.removedSinks.isEmpty()) {
            System.out.println("No sinks were removed.");
        } else {
            System.out.println("Sinks removed in order:");
            for (int i = 0; i < result.removedSinks.size(); i++) {
                System.out.println("Step " + (i + 1) + ": removed sink " + result.removedSinks.get(i));
            }
        }

        if (result.acyclic) {
            System.out.println("Final answer: YES - the graph is acyclic.");
        } else {
            System.out.println("Final answer: NO - the graph is cyclic.");
            if (result.cycle != null && !result.cycle.isEmpty()) {
                System.out.print("One cycle found: ");
                for (int i = 0; i < result.cycle.size(); i++) {
                    System.out.print(result.cycle.get(i));
                    if (i < result.cycle.size() - 1) {
                        System.out.print(" -> ");
                    }
                }
                System.out.println();
            } else {
                System.out.println("A cycle exists, but no cycle could be reconstructed.");
            }
        }
    }

    static String chooseFile() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // if look and feel fails, continue normally
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a graph input file");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));
        fileChooser.setCurrentDirectory(new File("."));

        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath();
        }

        return null;
    }

    public static void main(String[] args) {
        System.out.println("Directed Graph Acyclicity Checker");
        System.out.println("---------------------------------");

        String filename = chooseFile();

        if (filename == null) {
            System.out.println("No file was selected. Program terminated.");
            return;
        }

        try {
            Graph graph = readGraphFromFile(filename);

            System.out.println();
            System.out.println("Selected file: " + filename);
            System.out.println("Graph loaded successfully.");
            System.out.println("Number of vertices: " + graph.vertexCount());

            AnalysisResult result = analyseGraph(graph);
            printResult(result);

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}