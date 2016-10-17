package KMean;

import java.util.HashSet;
import java.util.Set;

public class Cluster {

    public Cluster(int id, Point centroid) {
        this.id = id;
        this.points = new HashSet<>();
        this.centroid = centroid;
    }

    public Set<Point> getPoints() {
        return points;
    }

    public Point getCentroid() {
        return centroid;
    }

    void addPoint(Point point) {
        points.add(point);
    }

    void deletePoint(Point point) {
        points.remove(point);
    }

    public int getId() {
        return id;
    }

    public void plotCluster() {
        System.out.println("[Cluster: " + id+"]");
        System.out.println("[Centroid: " + centroid + "]");
        System.out.println("[Points: \n");

        for(Point p : points) {
            System.out.println(p);
        }

        System.out.println("]");
    }

    /******************************/

    Set<Point> points;
    Point centroid;
    int id;
}