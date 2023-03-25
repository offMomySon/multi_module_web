package node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Node {
    private static Set<String> visits = new HashSet<>();

    private final List<Node> subNodes;
    private final String value;

    public Node(String value) {
        this.subNodes = new ArrayList<>();
        this.value = value;
    }

    public void addNode(Node... nodes) {
        Collections.addAll(subNodes, nodes);
    }

    public void dfs() {
        if(visits.contains(value)){
            System.out.println("circular detected. value : " + value);
            return;
        }

        System.out.println("value : " + value);
        visits.add(value);

        for (Node subNode : subNodes) {
            subNode.dfs();
        }
    }

    public static void main(String[] args) {
        Node A = new Node("A");
        Node B = new Node("B");
        Node C = new Node("C");
        Node D = new Node("D");
        Node E = new Node("E");
        Node F = new Node("F");
        Node G = new Node("G");
        Node H = new Node("H");
        Node I = new Node("I");
        Node J = new Node("J");
        Node AA = new Node("A");

        A.addNode(B, C);
        B.addNode(D,E,F);
        E.addNode(AA);
        C.addNode(G);
        G.addNode(H);
        H.addNode(I,J);

        A.dfs();

    }
}
