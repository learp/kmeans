package KMean;

import java.util.*;

public class KMeans {

    public KMeans(Collection<Point> points, int clusterCount, Random random) {
        prepareAndCheck(points);

        for (int i = 0; i < clusterCount; i++) {
            List<Double> initCoordinates = new ArrayList<>();

            for (int j = 0; j < dimension; j++) {
                initCoordinates.add(Math.abs(random.nextDouble()));
            }

            clusters.add(new Cluster(i, new Point(initCoordinates)));
        }
    }

    public KMeans(Collection<Point> points, int clusterCount) {
        prepareAndCheck(points);

        clusters.add(new Cluster(0, points.iterator().next()));
        for (int i = 1; i < clusterCount; i++) {
            List<Double> maxDistance = new ArrayList<>(Collections.nCopies(clusterCount, Double.MIN_VALUE));

            Point maxPoint = null;
            for (Point point : points) {
                if (max(point, maxDistance)) {
                    maxPoint = point;
                }
            }

            clusters.add(new Cluster(i, maxPoint));
        }
    }

    private boolean max(Point point, List<Double> distance) {
        for (Cluster cluster : clusters) {
            if (point.cosineDistanceTo(cluster.centroid) < distance.get(cluster.id)) {
                return false;
            }
        }

        for (Cluster cluster : clusters) {
            distance.set(cluster.id, point.cosineDistanceTo(cluster.centroid));
        }

        return true;
    }

    private void prepareAndCheck(Collection<Point> points) {
        if (points.isEmpty()) {
            throw new IllegalArgumentException("list of points is empty");
        }

        this.points = new HashSet<>(points);
        this.clusters = new ArrayList<>();

        Iterator<Point> it = points.iterator();
        dimension = it.next().vector.size();
        while (it.hasNext()) {
            if (dimension != it.next().vector.size()) {
                throw new IllegalArgumentException("different size");
            }
        }
    }

    public List<Cluster> getClusters() {
        return clusters;
    }

    public void run() {
        boolean needUpdate = true;

        while (needUpdate) {
            updatePoints();
            needUpdate = updateCentroids();
        }
    }

    void updatePoints() {
        int clusterId = 0;

        for(Point point : points) {
            double min = Double.MAX_VALUE;

            for(Cluster cluster : clusters) {
                double distance = point.cosineDistanceTo(cluster.centroid);

                if (distance < min) {
                    min = distance;
                    clusterId = cluster.id;
                }
            }

            clusters.get(point.clusterId).deletePoint(point);
            point.setClusterId(clusterId);
            clusters.get(clusterId).addPoint(point);
        }
    }

    boolean updateCentroids() {
        boolean isSomeCentroidUpdate = false;

        for(Cluster cluster : clusters) {
            Point res = new Point(dimension);

            for(Point point : cluster.points) {
                res.sum(point);
            }

            if (cluster.points.size() == 0) {
                break;
            }

            res.divide(cluster.points.size());

            isSomeCentroidUpdate |= !cluster.centroid.equals(res);
            cluster.centroid = res;
        }

        return isSomeCentroidUpdate;
    }

    /******************************/

    List<Cluster> clusters;
    Set<Point> points;
    int dimension;
}
