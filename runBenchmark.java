/*
 * Name: D.F.R.Michele
 * Student ID: W2119981/20231647
 */

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class runBenchmark {

    static class BenchmarkStats {
        int fileCount;
        long totalRuntime;
        long minRuntime = Long.MAX_VALUE;
        long maxRuntime = Long.MIN_VALUE;
    }

    static int countEdges(Main.Graph graph) {
        int edgeCount = 0;

        for (int vertex : graph.getVertices()) {
            edgeCount += graph.getOutgoingNeighbors(vertex).size();
        }

        return edgeCount;
    }

    static void processFolder(String folderPath, String expectedType, BenchmarkStats stats) {
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Invalid folder: " + folderPath);
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

        if (files == null || files.length == 0) {
            System.out.println("No .txt files found in: " + folderPath);
            return;
        }

        Arrays.sort(files, Comparator.comparing(File::getName));

        System.out.println("\n===== " + expectedType.toUpperCase() + " FILES (40 VERTICES ONLY) =====");

        for (File file : files) {
            try {
                Main.Graph graph = Main.readGraphFromFile(file.getAbsolutePath());

                int vertexCount = graph.vertexCount();

                if (vertexCount != 40) {
                    continue;
                }

                int edgeCount = countEdges(graph);

                long startTime = System.nanoTime();
                Main.AnalysisResult result = Main.analyseGraph(graph);
                long endTime = System.nanoTime();

                long runtime = endTime - startTime;

                boolean expectedAcyclic = expectedType.equalsIgnoreCase("acyclic");
                boolean correct = (result.acyclic == expectedAcyclic);

                stats.fileCount++;
                stats.totalRuntime += runtime;
                stats.minRuntime = Math.min(stats.minRuntime, runtime);
                stats.maxRuntime = Math.max(stats.maxRuntime, runtime);

                System.out.println(
                    file.getName()
                    + " | V=" + vertexCount
                    + " | E=" + edgeCount
                    + " | Time(ns)=" + runtime
                    + " | Output=" + (result.acyclic ? "Acyclic" : "Cyclic")
                    + " | Correct=" + (correct ? "Yes" : "No")
                );

            } catch (IOException e) {
                System.out.println("Error reading file: " + file.getName());
                System.out.println("Reason: " + e.getMessage());
            }
        }
    }

    static void printSummary(String graphType, BenchmarkStats stats) {
        if (stats.fileCount == 0) {
            System.out.println("\nNo 40-vertex files processed for " + graphType + ".");
            return;
        }

        double averageRuntime = (double) stats.totalRuntime / stats.fileCount;

        System.out.println("\n----- " + graphType.toUpperCase() + " SUMMARY -----");
        System.out.println("Files tested: " + stats.fileCount);
        System.out.println("Average runtime (ns): " + averageRuntime);
        System.out.println("Minimum runtime (ns): " + stats.minRuntime);
        System.out.println("Maximum runtime (ns): " + stats.maxRuntime);
    }

    static void RunBenchmark() {
        String acyclicFolder = "benchmarks/acyclic";
        String cyclicFolder = "benchmarks/cyclic";

        BenchmarkStats acyclicStats = new BenchmarkStats();
        BenchmarkStats cyclicStats = new BenchmarkStats();

        processFolder(acyclicFolder, "acyclic", acyclicStats);
        processFolder(cyclicFolder, "cyclic", cyclicStats);

        printSummary("acyclic", acyclicStats);
        printSummary("cyclic", cyclicStats);
    }

    public static void main(String[] args) {
        RunBenchmark();
    }
}