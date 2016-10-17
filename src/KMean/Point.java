package KMean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Point {

    Point(int size) {
        vector = new ArrayList<>(Collections.nCopies(size, 0d));
    }

    public Point(List<Double> vector) {
        this.vector = new ArrayList<>(vector);
    }

    public void setClusterId(int id) {
        clusterId = id;
    }

    public int getClusterId() {
        return clusterId;
    }

    double distanceTo(Point p) {
        double distance = 0;

        for (int i = 0; i < vector.size(); i++) {
            distance += Math.pow((vector.get(i) - p.vector.get(i)), 2);
        }

        return Math.sqrt(distance);
    }

    void sum(Point right) {
        for (int i = 0; i < vector.size(); i++) {
            vector.set(i, vector.get(i) + right.vector.get(i));
        }
    }

    void divide(int div) {
        for (int i = 0; i < vector.size(); i++) {
            vector.set(i, vector.get(i)/div);
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("(");

        for (Double d : vector) {
            result.append(d).append(", ");
        }

        return result.append(clusterId).append(")").toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        Point that = (Point) obj;

        if (vector.size() != that.vector.size()) {
            return false;
        }

        for (int i = 0; i < vector.size(); i++) {
            if (Math.abs(vector.get(i) - that.vector.get(i)) >= 1e-6) {
                return false;
            }
        }

        return clusterId == that.clusterId;
    }

    List<Double> vector;
    int clusterId;
}