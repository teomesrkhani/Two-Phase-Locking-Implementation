package Lock;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;

public class WaitForGraph {
    Map<Integer, List<Integer>> adjacencyMap;
    Map<Integer, Integer> indegreeMap;
    Set<Integer> transactionIds;

    public WaitForGraph() {
        adjacencyMap = new HashMap<Integer, List<Integer>>();
        indegreeMap = new HashMap<Integer, Integer>();
        transactionIds = new HashSet<Integer>();
    }

    // If T-X must Wait for a lock held by T-Y, Draw a directed edge from T-X to T-Y
    public void addEdge(int transactionId1, int transactionId2) {
        List<Integer> adjacencyList = adjacencyMap.getOrDefault(transactionId1, new ArrayList<Integer>());
        adjacencyList.add(transactionId2);
        adjacencyMap.put(transactionId1, adjacencyList);

        indegreeMap.put(transactionId2, indegreeMap.getOrDefault(transactionId2, 0) + 1);

        transactionIds.add(transactionId1);
        transactionIds.add(transactionId2);
    }

    // Remove all directed edges coming out of T-X
    public void removeTransactionEdges(int transactionId) {
        // Remove all outgoing edges
        List<Integer> adjacencyList = adjacencyMap.get(transactionId);
        if (adjacencyList != null) {
            for (int adjacentTransactionId : adjacencyList) {
                indegreeMap.put(adjacentTransactionId, indegreeMap.get(adjacentTransactionId) - 1);
            }
            adjacencyMap.put(transactionId, new ArrayList<Integer>());
        }

        // Remove all incoming edges
        for (List<Integer> otherAdjacencyList : adjacencyMap.values()){
            while(otherAdjacencyList.contains(transactionId)) {
                otherAdjacencyList.remove((Integer)transactionId);
            }
        }
        indegreeMap.put(transactionId, 0);
    }

    // Function to determine if graph has a cycle
    // Algorithm found here: https://www.geeksforgeeks.org/detect-cycle-in-a-graph/
    public Set<Integer> findCycle() {
        Map<Integer, Integer> indegreeMap = new HashMap<Integer, Integer>(this.indegreeMap);
        Queue<Integer> queue = new LinkedList<Integer>();
        Map<Integer, Boolean> visitedMap = new HashMap<Integer, Boolean>();

        // Enqueue vertices with 0 in-degree
        for(int transactionId : transactionIds) {
            if (indegreeMap.getOrDefault(transactionId, 0) == 0) {
                queue.add(transactionId);
            }
        }

        // BFS traversal
        while (queue.size() > 0) {
            int transactionId = queue.remove();
            visitedMap.put(transactionId, true);

            // Reduce indegree of adjacent vertices
            List<Integer> adjacencyList = adjacencyMap.get(transactionId);
            if (adjacencyList != null) {
                for (int adjacentTransactionId : adjacencyList) {
                    indegreeMap.put(adjacentTransactionId, indegreeMap.get(adjacentTransactionId) - 1);
                    // If in-degree becomes 0, enqueue the vertex
                    if (indegreeMap.get(adjacentTransactionId) == 0) {
                        queue.add(adjacentTransactionId);
                    }
                }
            }
        }

        // Any vertices that are not visited is part of a cycle
        Set<Integer> cycle = new HashSet<Integer>();
        for (int transactionId : transactionIds) {
            if (!visitedMap.getOrDefault(transactionId, false)) {
                cycle.add(transactionId);
            }
        }

        return cycle;
    }
}
